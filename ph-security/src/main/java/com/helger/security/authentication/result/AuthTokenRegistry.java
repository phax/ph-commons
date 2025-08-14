/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.security.authentication.result;

import java.util.Map;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.base.enforcer.ValueEnforcer;
import com.helger.base.state.ESuccess;
import com.helger.base.string.Strings;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.security.authentication.subject.IAuthSubject;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * This class manages all the currently available authentications tokens.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class AuthTokenRegistry
{
  private static final SimpleReadWriteLock RW_LOCK = new SimpleReadWriteLock ();
  private static final ICommonsMap <String, AuthToken> MAP = new CommonsHashMap <> ();

  private AuthTokenRegistry ()
  {}

  @Nonnull
  public static IAuthToken createToken (@Nonnull final IAuthIdentification aIdentification,
                                        @Nonnegative final int nExpirationSeconds)
  {
    final AuthToken aToken = new AuthToken (aIdentification, nExpirationSeconds);
    final String sTokenID = aToken.getID ();

    RW_LOCK.writeLocked ( () -> {
      if (MAP.containsKey (sTokenID))
        throw new IllegalArgumentException ("Token '" + sTokenID + "' already contained");
      MAP.put (sTokenID, aToken);
    });

    return aToken;
  }

  @Nonnull
  public static ESuccess removeToken (@Nonnull final String sTokenID)
  {
    return RW_LOCK.writeLockedGet ( () -> {
      final AuthToken aToken = MAP.remove (sTokenID);
      if (aToken == null)
        return ESuccess.FAILURE;

      // manually set token as expired to avoid further usage in case somebody
      // has a reference to the token
      aToken.setExpired ();
      return ESuccess.SUCCESS;
    });
  }

  @Nullable
  private static AuthToken _getValidNotExpiredToken (@Nullable final String sTokenID)
  {
    if (Strings.isEmpty (sTokenID))
      return null;

    final AuthToken aToken = RW_LOCK.readLockedGet ( () -> MAP.get (sTokenID));
    return aToken != null && !aToken.isExpired () ? aToken : null;
  }

  @Nullable
  public static IAuthToken getValidToken (@Nullable final String sTokenID)
  {
    return _getValidNotExpiredToken (sTokenID);
  }

  @Nullable
  public static IAuthToken validateTokenAndUpdateLastAccess (@Nullable final String sTokenID)
  {
    final AuthToken aToken = _getValidNotExpiredToken (sTokenID);
    if (aToken == null)
      return null;

    RW_LOCK.writeLocked (aToken::updateLastAccess);
    return aToken;
  }

  /**
   * Get all tokens of the specified auth subject. All tokens are returned, no matter whether they
   * are expired or not.
   *
   * @param aSubject
   *        The subject to query. May not be <code>null</code>.
   * @return The list and never <code>null</code>.
   */
  @Nonnull
  public static ICommonsList <IAuthToken> getAllTokensOfSubject (@Nonnull final IAuthSubject aSubject)
  {
    ValueEnforcer.notNull (aSubject, "Subject");

    return RW_LOCK.readLockedGet ( () -> CommonsArrayList.createFiltered (MAP.values (),
                                                                          aToken -> aToken.getIdentification ()
                                                                                          .hasAuthSubject (aSubject)));
  }

  /**
   * Remove all tokens of the given subject
   *
   * @param aSubject
   *        The subject for which the tokens should be removed.
   * @return The number of removed tokens. Always &ge; 0.
   */
  @Nonnegative
  public static int removeAllTokensOfSubject (@Nonnull final IAuthSubject aSubject)
  {
    ValueEnforcer.notNull (aSubject, "Subject");

    // get all token IDs matching a given subject
    // Note: required IAuthSubject to implement equals!
    final ICommonsList <String> aDelTokenIDs = new CommonsArrayList <> ();
    RW_LOCK.readLocked ( () -> {
      for (final Map.Entry <String, AuthToken> aEntry : MAP.entrySet ())
        if (aEntry.getValue ().getIdentification ().hasAuthSubject (aSubject))
          aDelTokenIDs.add (aEntry.getKey ());
    });

    for (final String sDelTokenID : aDelTokenIDs)
      removeToken (sDelTokenID);

    return aDelTokenIDs.size ();
  }
}

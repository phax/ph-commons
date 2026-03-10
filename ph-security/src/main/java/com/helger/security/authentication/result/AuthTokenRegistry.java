/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.ESuccess;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;
import com.helger.security.authentication.subject.IAuthSubject;

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

  /**
   * Create a new authentication token for the provided identification.
   *
   * @param aIdentification
   *        The identification for which the token should be created. May not be <code>null</code>.
   * @param nExpirationSeconds
   *        The number of seconds after which the token expires. Use
   *        {@link IAuthToken#EXPIRATION_SECONDS_INFINITE} for no expiration.
   * @return The newly created auth token. Never <code>null</code>.
   * @throws IllegalArgumentException
   *         If a token with the same ID already exists.
   */
  @NonNull
  public static IAuthToken createToken (@NonNull final IAuthIdentification aIdentification,
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

  /**
   * Remove the token with the specified ID. If the token is found, it is also marked as expired.
   *
   * @param sTokenID
   *        The ID of the token to remove. May not be <code>null</code>.
   * @return {@link ESuccess#SUCCESS} if the token was found and removed, {@link ESuccess#FAILURE}
   *         otherwise.
   */
  @NonNull
  public static ESuccess removeToken (@NonNull final String sTokenID)
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
    if (StringHelper.isEmpty (sTokenID))
      return null;

    final AuthToken aToken = RW_LOCK.readLockedGet ( () -> MAP.get (sTokenID));
    return aToken != null && !aToken.isExpired () ? aToken : null;
  }

  /**
   * Get the valid (non-expired) token with the specified ID.
   *
   * @param sTokenID
   *        The token ID to look up. May be <code>null</code>.
   * @return The valid token, or <code>null</code> if the token was not found, is expired, or if the
   *         ID is <code>null</code>.
   */
  @Nullable
  public static IAuthToken getValidToken (@Nullable final String sTokenID)
  {
    return _getValidNotExpiredToken (sTokenID);
  }

  /**
   * Validate that the token with the specified ID exists and is not expired, and update its last
   * access timestamp.
   *
   * @param sTokenID
   *        The token ID to validate. May be <code>null</code>.
   * @return The valid token with updated last access time, or <code>null</code> if the token was
   *         not found or is expired.
   */
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
  @NonNull
  public static ICommonsList <IAuthToken> getAllTokensOfSubject (@NonNull final IAuthSubject aSubject)
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
  public static int removeAllTokensOfSubject (@NonNull final IAuthSubject aSubject)
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

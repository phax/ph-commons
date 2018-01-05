/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import java.time.Duration;
import java.time.LocalDateTime;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation of the {@link IAuthToken} interface.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class AuthToken implements IAuthToken
{
  private final String m_sID;
  private final IAuthIdentification m_aIdentification;
  private final LocalDateTime m_aCreationDT;
  private LocalDateTime m_aLastAccessDT;
  private final int m_nExpirationSeconds;
  private boolean m_bExpired;

  public AuthToken (@Nonnull final IAuthIdentification aIdentification, @Nonnegative final int nExpirationSeconds)
  {
    ValueEnforcer.notNull (aIdentification, "Identification");
    ValueEnforcer.isGE0 (nExpirationSeconds, "ExpirationSeconds");

    // create new ID
    m_sID = AuthTokenIDGenerator.generateNewTokenID ();
    if (StringHelper.hasNoText (m_sID))
      throw new IllegalStateException ("Failed to create token ID");

    m_aIdentification = aIdentification;
    m_aCreationDT = PDTFactory.getCurrentLocalDateTime ();
    m_aLastAccessDT = m_aCreationDT;
    m_nExpirationSeconds = nExpirationSeconds;
    m_bExpired = false;
  }

  @Nonnull
  public String getID ()
  {
    return m_sID;
  }

  @Nonnull
  public IAuthIdentification getIdentification ()
  {
    return m_aIdentification;
  }

  @Nonnull
  public LocalDateTime getCreationDate ()
  {
    return m_aCreationDT;
  }

  @Nonnull
  public LocalDateTime getLastAccessDate ()
  {
    return m_aLastAccessDT;
  }

  @Nonnegative
  public int getExpirationSeconds ()
  {
    return m_nExpirationSeconds;
  }

  public boolean isExpirationPossible ()
  {
    return m_nExpirationSeconds > EXPIRATION_SECONDS_INFINITE;
  }

  /**
   * Get the date time when this token will expire. This date time changes every
   * time the last access is updated.
   *
   * @return The expiration date and time or <code>null</code> if this token
   *         cannot expire.
   * @see #isExpirationPossible()
   */
  @Nullable
  public LocalDateTime getExpirationDate ()
  {
    return isExpirationPossible () ? m_aLastAccessDT.plusSeconds (m_nExpirationSeconds) : null;
  }

  public boolean isExpired ()
  {
    if (!m_bExpired && isExpirationPossible ())
    {
      // Only if expiration is defined
      if (Duration.between (m_aLastAccessDT, PDTFactory.getCurrentLocalDateTime ())
                  .getSeconds () > m_nExpirationSeconds)
        m_bExpired = true;
    }
    return m_bExpired;
  }

  /**
   * Mark the token as expired independent of the time since the last access.
   */
  void setExpired ()
  {
    m_bExpired = true;
  }

  /**
   * Update the last access date and time of this token. This means that the
   * expiration time starts again (if any).
   */
  void updateLastAccess ()
  {
    m_aLastAccessDT = PDTFactory.getCurrentLocalDateTime ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final AuthToken rhs = (AuthToken) o;
    return m_sID.equals (rhs.m_sID) &&
           m_aIdentification.equals (rhs.m_aIdentification) &&
           m_aCreationDT.equals (rhs.m_aCreationDT) &&
           m_aLastAccessDT.equals (rhs.m_aLastAccessDT) &&
           m_bExpired == rhs.m_bExpired &&
           m_nExpirationSeconds == rhs.m_nExpirationSeconds;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sID)
                                       .append (m_aIdentification)
                                       .append (m_aCreationDT)
                                       .append (m_aLastAccessDT)
                                       .append (m_bExpired)
                                       .append (m_nExpirationSeconds)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("id", m_sID)
                                       .append ("identification", m_aIdentification)
                                       .append ("creationDT", m_aCreationDT)
                                       .append ("lastAccessDT", m_aLastAccessDT)
                                       .append ("expired", m_bExpired)
                                       .append ("expirationSeconds", m_nExpirationSeconds)
                                       .getToString ();
  }
}

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
package com.helger.security.password.salt;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;
import com.helger.security.bcrypt.BCrypt;

/**
 * Implementation of {@link IPasswordSalt} using {@link BCrypt#gensalt(int)}.
 *
 * @author Philip Helger
 */
@Nonnegative
@Immutable
public final class PasswordSaltBCrypt implements IPasswordSalt
{
  private final byte [] m_aBytes;
  private final String m_sSalt;

  /**
   * Create a new BCrypt password salt with the default rounds of
   * {@value BCrypt#GENSALT_DEFAULT_LOG2_ROUNDS}.
   */
  public PasswordSaltBCrypt ()
  {
    this (BCrypt.GENSALT_DEFAULT_LOG2_ROUNDS);
  }

  /**
   * Constructor to create a new BCrypt password salt with the provided rounds.
   *
   * @param nRounds
   *        the log2 of the number of rounds of hashing to apply - the work
   *        factor therefore increases as 2**log_rounds.
   */
  public PasswordSaltBCrypt (@Nonnegative final int nRounds)
  {
    ValueEnforcer.isGT0 (nRounds, "Rounds");
    m_sSalt = BCrypt.gensalt (nRounds);
    m_aBytes = m_sSalt.getBytes (StandardCharsets.UTF_8);
  }

  @Nonnegative
  public int getSaltByteCount ()
  {
    return m_aBytes.length;
  }

  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  public byte [] getSaltBytes ()
  {
    return ArrayHelper.getCopy (m_aBytes);
  }

  @Nonnull
  @Nonempty
  public String getSaltString ()
  {
    return m_sSalt;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final PasswordSaltBCrypt rhs = (PasswordSaltBCrypt) o;
    return Arrays.equals (m_aBytes, rhs.m_aBytes);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aBytes).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Salt", m_sSalt).getToString ();
  }
}

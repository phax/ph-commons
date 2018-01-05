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

import java.util.Arrays;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.random.RandomHelper;
import com.helger.commons.random.VerySecureRandom;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation of {@link IPasswordSalt} using
 * {@link VerySecureRandom}.
 *
 * @author Philip Helger
 */
@Nonnegative
@Immutable
public final class PasswordSalt implements IPasswordSalt
{
  /** Default salt byte length is nothing else is specified. */
  public static final int DEFAULT_SALT_BYTES = 512;

  private final byte [] m_aBytes;
  private final String m_sSalt;

  /**
   * Create a new password salt with the default length of
   * {@value #DEFAULT_SALT_BYTES} and random bytes.
   */
  public PasswordSalt ()
  {
    this (DEFAULT_SALT_BYTES);
  }

  /**
   * Constructor to create a new password salt with the provided byte count.
   *
   * @param nSaltBytes
   *        The number of salt bytes to use. Must be &gt; 0.
   */
  public PasswordSalt (@Nonnegative final int nSaltBytes)
  {
    ValueEnforcer.isGT0 (nSaltBytes, "SaltBytes");
    m_aBytes = new byte [nSaltBytes];
    RandomHelper.getRandom ().nextBytes (m_aBytes);
    m_sSalt = StringHelper.getHexEncoded (m_aBytes);
  }

  private PasswordSalt (@Nonnull @Nonempty final byte [] aBytes)
  {
    ValueEnforcer.notEmpty (aBytes, "Bytes");
    m_aBytes = aBytes;
    m_sSalt = StringHelper.getHexEncoded (aBytes);
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
    final PasswordSalt rhs = (PasswordSalt) o;
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
    return new ToStringGenerator (this).append ("bytes#", m_aBytes.length).getToString ();
  }

  /**
   * Try to create a {@link PasswordSalt} object from the passed string. First
   * the string is hex decoded into a byte array and this is the password salt.
   *
   * @param sSalt
   *        The string to be used. May be <code>null</code>.
   * @return <code>null</code> if the passed salt string is <code>null</code> or
   *         empty.
   * @throws IllegalArgumentException
   *         if the passed salt string cannot be hex decoded.
   */
  @Nullable
  public static PasswordSalt createFromStringMaybe (@Nullable final String sSalt)
  {
    if (StringHelper.hasNoText (sSalt))
      return null;

    // Decode String to bytes
    // Throws an IllegalArgumentException if an invalid character is encountered
    final byte [] aBytes = StringHelper.getHexDecoded (sSalt);
    return new PasswordSalt (aBytes);
  }
}

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
package com.helger.security.password.salt;

import java.util.Arrays;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.array.ArrayHelper;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.string.StringHelper;
import com.helger.base.string.StringHex;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.commons.random.VerySecureRandom;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Default implementation of {@link IPasswordSalt} using {@link VerySecureRandom}.
 *
 * @author Philip Helger
 */
@Nonnegative
@Immutable
public final class PasswordSalt implements IPasswordSalt
{
  /** Default salt byte length is nothing else is specified. */
  public static final int DEFAULT_SALT_BYTES = 32;

  private final byte [] m_aSaltBytes;
  private final String m_sSaltString;

  /**
   * Constructor with salt bytes.
   *
   * @param aBytes
   *        Salt bytes to use. May not be <code>null</code> or empty.
   */
  public PasswordSalt (@Nonnull @Nonempty final byte [] aBytes)
  {
    ValueEnforcer.notEmpty (aBytes, "Bytes");
    m_aSaltBytes = aBytes;
    m_sSaltString = StringHex.getHexEncoded (aBytes);
  }

  @Nonnegative
  public int getSaltByteCount ()
  {
    return m_aSaltBytes.length;
  }

  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  public byte [] getSaltBytes ()
  {
    return ArrayHelper.getCopy (m_aSaltBytes);
  }

  @Nonnull
  @Nonempty
  public String getSaltString ()
  {
    return m_sSaltString;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final PasswordSalt rhs = (PasswordSalt) o;
    return Arrays.equals (m_aSaltBytes, rhs.m_aSaltBytes);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aSaltBytes).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Bytes#", m_aSaltBytes.length).getToString ();
  }

  /**
   * @return A new password salt with the default length of {@value #DEFAULT_SALT_BYTES} and random
   *         bytes.
   * @since 10.1.4
   */
  @Nonnull
  public static PasswordSalt createRandom ()
  {
    return createRandom (DEFAULT_SALT_BYTES);
  }

  /**
   * Create a new password salt with the provided byte count.
   *
   * @param nSaltBytes
   *        The number of salt bytes to use. Must be &gt; 0.
   * @return the new salt object
   * @since 10.1.4
   */
  @Nonnull
  public static PasswordSalt createRandom (@Nonnegative final int nSaltBytes)
  {
    ValueEnforcer.isGT0 (nSaltBytes, "SaltBytes");
    final byte [] aBytes = new byte [nSaltBytes];
    VerySecureRandom.getInstance ().nextBytes (aBytes);
    return new PasswordSalt (aBytes);
  }

  /**
   * Try to create a {@link PasswordSalt} object from the passed string. First the string is hex
   * decoded into a byte array and this is the password salt.
   *
   * @param sSalt
   *        The string to be used. May be <code>null</code>.
   * @return <code>null</code> if the passed salt string is <code>null</code> or empty.
   * @throws IllegalArgumentException
   *         if the passed salt string cannot be hex decoded.
   */
  @Nullable
  public static PasswordSalt createFromStringMaybe (@Nullable final String sSalt)
  {
    if (StringHelper.isEmpty (sSalt))
      return null;

    // Decode String to bytes
    // Throws an IllegalArgumentException if an invalid character is encountered
    final byte [] aBytes = StringHex.getHexDecoded (sSalt);
    return new PasswordSalt (aBytes);
  }
}

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
package com.helger.security.messagedigest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.WillClose;
import javax.annotation.WillNotClose;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.io.IHasByteArray;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Represents a single message digest value as the combination of the algorithm
 * and the digest bytes.
 *
 * @author Philip Helger
 */
@Immutable
public class MessageDigestValue implements IHasByteArray, Serializable
{
  public static final boolean DEFAULT_COPY_NEEDED = true;

  private final EMessageDigestAlgorithm m_eAlgorithm;
  private final byte [] m_aDigestBytes;

  private final boolean m_bIsCopy;

  public MessageDigestValue (@Nonnull final EMessageDigestAlgorithm eAlgorithm,
                             @Nonnull @Nonempty final byte [] aDigestBytes)
  {
    this (eAlgorithm, aDigestBytes, DEFAULT_COPY_NEEDED);
  }

  public MessageDigestValue (@Nonnull final EMessageDigestAlgorithm eAlgorithm,
                             @Nonnull @Nonempty final byte [] aDigestBytes,
                             final boolean bIsCopyNeeded)
  {
    ValueEnforcer.notNull (eAlgorithm, "Algorithm");
    ValueEnforcer.notEmpty (aDigestBytes, "DigestBytes");
    m_eAlgorithm = eAlgorithm;
    m_aDigestBytes = bIsCopyNeeded ? ArrayHelper.getCopy (aDigestBytes) : aDigestBytes;
    m_bIsCopy = bIsCopyNeeded;
  }

  /**
   * @return The message digest algorithm used. Never <code>null</code>.
   */
  @Nonnull
  public EMessageDigestAlgorithm getAlgorithm ()
  {
    return m_eAlgorithm;
  }

  public boolean isCopy ()
  {
    return m_bIsCopy;
  }

  /**
   * @return A copy of the message digest bytes. The length depends on the used
   *         algorithm. Never <code>null</code>.
   */
  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  @Deprecated
  public byte [] getAllDigestBytes ()
  {
    return getAllBytes ();
  }

  /**
   * @return The message digest bytes. The length depends on the used algorithm.
   *         Never <code>null</code>.
   * @since 9.1.3
   */
  @Nonnull
  @Nonempty
  @ReturnsMutableObject
  public byte [] bytes ()
  {
    return m_aDigestBytes;
  }

  @Nonnegative
  public int size ()
  {
    return m_aDigestBytes.length;
  }

  @Nonnegative
  public int getOffset ()
  {
    return 0;
  }

  /**
   * Write the digest bytes to the specified output stream.
   *
   * @param aOS
   *        The output stream to write to. May not be <code>null</code>.
   * @throws IOException
   *         In case of a write error
   */
  public void writeDigestBytes (@Nonnull @WillNotClose final OutputStream aOS) throws IOException
  {
    ValueEnforcer.notNull (aOS, "OutputStream");
    aOS.write (m_aDigestBytes, 0, m_aDigestBytes.length);
  }

  /**
   * @return The hex-encoded String of the message digest bytes. Never
   *         <code>null</code>.
   */
  @Nonnull
  @Nonempty
  public String getHexEncodedDigestString ()
  {
    return StringHelper.getHexEncoded (m_aDigestBytes);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MessageDigestValue rhs = (MessageDigestValue) o;
    return m_eAlgorithm.equals (rhs.m_eAlgorithm) && EqualsHelper.equals (m_aDigestBytes, rhs.m_aDigestBytes);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_eAlgorithm).append (m_aDigestBytes).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("algorithm", m_eAlgorithm)
                                       .append ("bytes", Arrays.toString (m_aDigestBytes))
                                       .getToString ();
  }

  /**
   * Create a new {@link MessageDigestValue} object based on the passed source
   * byte array
   *
   * @param aBytes
   *        The byte array to create the hash value from. May not be
   *        <code>null</code>.
   * @param eAlgorithm
   *        The algorithm to be used. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static MessageDigestValue create (@Nonnull final byte [] aBytes,
                                           @Nonnull final EMessageDigestAlgorithm eAlgorithm)
  {
    final MessageDigest aMD = eAlgorithm.createMessageDigest ();
    aMD.update (aBytes);
    // aMD goes out of scope anyway, so no need to copy byte[]
    return new MessageDigestValue (eAlgorithm, aMD.digest (), false);
  }

  /**
   * Create a new {@link MessageDigestValue} object based on the passed source
   * {@link InputStream}.
   *
   * @param aIS
   *        The input stream to read from. May not be <code>null</code>.
   * @param eAlgorithm
   *        The algorithm to be used. May not be <code>null</code>.
   * @return Never <code>null</code>.
   * @throws IOException
   *         In case reading throws an IOException
   */
  @Nonnull
  public static MessageDigestValue create (@Nonnull @WillClose final InputStream aIS,
                                           @Nonnull final EMessageDigestAlgorithm eAlgorithm) throws IOException
  {
    final MessageDigest aMD = eAlgorithm.createMessageDigest ();
    StreamHelper.readUntilEOF (aIS, (aBytes, nBytes) -> aMD.update (aBytes, 0, nBytes));
    // aMD goes out of scope anyway, so no need to copy byte[]
    return new MessageDigestValue (eAlgorithm, aMD.digest (), false);
  }
}

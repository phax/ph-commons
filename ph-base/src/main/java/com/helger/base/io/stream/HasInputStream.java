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
package com.helger.base.io.stream;

import java.io.InputStream;
import java.util.function.Supplier;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.iface.IHasInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.tostring.ToStringGenerator;

/**
 * Special implementation of {@link IHasInputStream} with that has an InputStream supplier that can
 * be read more than once!
 *
 * @author Philip Helger
 */
@Immutable
public class HasInputStream implements IHasInputStream
{
  private final Supplier <? extends InputStream> m_aISP;
  private final boolean m_bReadMultiple;

  /**
   * Constructor
   *
   * @param aISP
   *        {@link InputStream} supplier. May not be <code>null</code>.
   * @param bReadMultiple
   *        <code>true</code> if the supplier can be invoked more than once (e.g. from a byte[]) or
   *        <code>false</code> if it can be invoked only once (e.g. from an open socket).
   */
  public HasInputStream (@NonNull final Supplier <? extends InputStream> aISP, final boolean bReadMultiple)
  {
    m_aISP = ValueEnforcer.notNull (aISP, "ISP");
    m_bReadMultiple = bReadMultiple;
  }

  /** {@inheritDoc} */
  public final boolean isReadMultiple ()
  {
    return m_bReadMultiple;
  }

  /** {@inheritDoc} */
  public final InputStream getInputStream ()
  {
    return m_aISP.get ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ISP", m_aISP).append ("ReadMultiple", m_bReadMultiple).getToString ();
  }

  /**
   * Create a new object with a supplier that can read multiple times.
   *
   * @param aISP
   *        {@link InputStream} provider. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static HasInputStream multiple (@NonNull final Supplier <? extends InputStream> aISP)
  {
    return new HasInputStream (aISP, true);
  }

  /**
   * Create a new object with a supplier that can be read only once.
   *
   * @param aISP
   *        {@link InputStream} provider. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static HasInputStream once (@NonNull final Supplier <? extends InputStream> aISP)
  {
    return new HasInputStream (aISP, false);
  }

  private static final class HISNBBAOS implements IHasInputStream
  {
    private final NonBlockingByteArrayOutputStream m_aBAOS;

    /**
     * Constructor.
     *
     * @param aBAOS
     *        The {@link NonBlockingByteArrayOutputStream} to wrap. May not be <code>null</code>.
     */
    public HISNBBAOS (@NonNull final NonBlockingByteArrayOutputStream aBAOS)
    {
      m_aBAOS = aBAOS;
    }

    /** {@inheritDoc} */
    @NonNull
    public InputStream getInputStream ()
    {
      return m_aBAOS.getAsInputStream ();
    }

    /** {@inheritDoc} */
    public boolean isReadMultiple ()
    {
      return true;
    }
  }

  /**
   * Get a special implementation of {@link IHasInputStream} for
   * {@link NonBlockingByteArrayOutputStream}. This input stream can be read multiple times.
   *
   * @param aBAOS
   *        Source stream. May not be <code>null</code>.
   * @return Never <code>null</code>.
   * @since 9.2.1
   */
  @NonNull
  @ReturnsMutableCopy
  public static IHasInputStream create (@NonNull final NonBlockingByteArrayOutputStream aBAOS)
  {
    ValueEnforcer.notNull (aBAOS, "BAOS");
    return new HISNBBAOS (aBAOS);
  }

  private static final class HISByteArray implements IHasInputStream
  {
    private final byte [] m_aBytes;
    private final int m_nOfs;
    private final int m_nLen;

    /**
     * Constructor.
     *
     * @param aBytes
     *        The byte array to wrap. May not be <code>null</code>.
     * @param nOfs
     *        The offset in the byte array of the first byte to read. Must be &ge; 0.
     * @param nLen
     *        The maximum number of bytes to read from the byte array. Must be &ge; 0.
     */
    public HISByteArray (final byte @NonNull [] aBytes, @Nonnegative final int nOfs, @Nonnegative final int nLen)
    {
      ValueEnforcer.isArrayOfsLen (aBytes, nOfs, nLen);
      m_aBytes = aBytes;
      m_nOfs = nOfs;
      m_nLen = nLen;
    }

    /** {@inheritDoc} */
    @NonNull
    public InputStream getInputStream ()
    {
      // No copy needed
      return new NonBlockingByteArrayInputStream (m_aBytes, m_nOfs, m_nLen, false);
    }

    /** {@inheritDoc} */
    public boolean isReadMultiple ()
    {
      return true;
    }
  }

  /**
   * Get a special implementation of {@link IHasInputStream} for byte array. This input stream can
   * be read multiple times. <br>
   * Note: don't alter the byte array after passing it in. It is not copied for performance reasons.
   *
   * @param aBytes
   *        Source byte array. May not be <code>null</code>.
   * @return Never <code>null</code>.
   * @since 11.2.0
   */
  @NonNull
  @ReturnsMutableCopy
  public static IHasInputStream create (final byte @NonNull [] aBytes)
  {
    ValueEnforcer.notNull (aBytes, "Bytes");
    return create (aBytes, 0, aBytes.length);
  }

  /**
   * Get a special implementation of {@link IHasInputStream} for a part of a byte array. This input
   * stream can be read multiple times. <br>
   * Note: don't alter the byte array after passing it in. It is not copied for performance reasons.
   *
   * @param aBytes
   *        Source byte array. May not be <code>null</code>.
   * @param nOfs
   *        The offset in the byte array of the first byte to read. Must be &ge; 0.
   * @param nLen
   *        The maximum number of bytes to read from the byte array. Must be &ge; 0.
   * @return Never <code>null</code>.
   * @since 12.3.5
   */
  @NonNull
  @ReturnsMutableCopy
  public static IHasInputStream create (final byte @NonNull [] aBytes,
                                        @Nonnegative final int nOfs,
                                        @Nonnegative final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (aBytes, nOfs, nLen);
    return new HISByteArray (aBytes, nOfs, nLen);
  }
}

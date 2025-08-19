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
package com.helger.collection.iterator;

import java.util.NoSuchElementException;

import com.helger.annotation.Nonnegative;
import com.helger.base.array.bytes.ByteArrayWrapper;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;

import jakarta.annotation.Nonnull;

/**
 * This is a small helper class for iterating over arrays of byte.
 *
 * @author Philip Helger
 */
public final class ArrayIteratorByte
{
  public static final boolean DEFAULT_COPY_NEEDED = true;

  private final ByteArrayWrapper m_aBytes;
  private int m_nIndex = 0;

  public ArrayIteratorByte (@Nonnull final byte... aArray)
  {
    this (aArray, 0, aArray.length, DEFAULT_COPY_NEEDED);
  }

  public ArrayIteratorByte (@Nonnull final byte [] aArray, final boolean bCopyNeeded)
  {
    this (aArray, 0, aArray.length, bCopyNeeded);
  }

  /**
   * Constructor with offset and length
   *
   * @param aBytes
   *        Source array
   * @param nOfs
   *        Offset. Must be &ge; 0.
   * @param nLength
   *        Length. Must be &ge; 0.
   */
  public ArrayIteratorByte (@Nonnull final byte [] aBytes, @Nonnegative final int nOfs, @Nonnegative final int nLength)
  {
    this (aBytes, nOfs, nLength, DEFAULT_COPY_NEEDED);
  }

  public ArrayIteratorByte (@Nonnull final byte [] aBytes,
                            @Nonnegative final int nOfs,
                            @Nonnegative final int nLength,
                            final boolean bCopyNeeded)
  {
    ValueEnforcer.isArrayOfsLen (aBytes, nOfs, nLength);
    m_aBytes = new ByteArrayWrapper (aBytes, nOfs, nLength, bCopyNeeded);
  }

  public boolean hasNext ()
  {
    return m_nIndex < m_aBytes.size ();
  }

  public byte next ()
  {
    if (!hasNext ())
      throw new NoSuchElementException ();
    return m_aBytes.bytes ()[m_nIndex++];
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ArrayIteratorByte rhs = (ArrayIteratorByte) o;
    return m_aBytes.equals (rhs.m_aBytes) && m_nIndex == rhs.m_nIndex;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aBytes).append (m_nIndex).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Bytes", m_aBytes).append ("Index", m_nIndex).getToString ();
  }
}

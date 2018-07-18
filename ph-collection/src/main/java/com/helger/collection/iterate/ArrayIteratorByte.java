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
package com.helger.collection.iterate;

import java.util.Arrays;
import java.util.NoSuchElementException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.io.IHasByteArray;
import com.helger.commons.string.ToStringGenerator;

/**
 * This is a small helper class for iterating over arrays of byte.
 *
 * @author Philip Helger
 */
public final class ArrayIteratorByte implements IHasByteArray
{
  public static final boolean DEFAULT_COPY_NEEDED = true;

  private final byte [] m_aBytes;
  private final int m_nOffset;
  private final int m_nLength;
  private final boolean m_bIsCopy;
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
    m_aBytes = bCopyNeeded ? ArrayHelper.getCopy (aBytes, nOfs, nLength) : aBytes;
    m_nOffset = bCopyNeeded ? 0 : nOfs;
    m_nLength = nLength;
    m_bIsCopy = bCopyNeeded;
  }

  public boolean hasNext ()
  {
    return m_nIndex < m_aBytes.length;
  }

  public byte next ()
  {
    if (!hasNext ())
      throw new NoSuchElementException ();
    return m_aBytes[m_nIndex++];
  }

  public boolean isCopy ()
  {
    return m_bIsCopy;
  }

  @Nonnull
  @ReturnsMutableObject
  public byte [] bytes ()
  {
    return m_aBytes;
  }

  @Nonnegative
  public int getOffset ()
  {
    return m_nOffset;
  }

  @Nonnegative
  public int size ()
  {
    return m_nLength;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ArrayIteratorByte rhs = (ArrayIteratorByte) o;
    return Arrays.equals (m_aBytes, rhs.m_aBytes) &&
           m_nOffset == rhs.m_nOffset &&
           m_nLength == rhs.m_nLength &&
           m_bIsCopy == rhs.m_bIsCopy &&
           m_nIndex == rhs.m_nIndex;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aBytes)
                                       .append (m_nOffset)
                                       .append (m_nLength)
                                       .append (m_bIsCopy)
                                       .append (m_nIndex)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("byte[]#", m_aBytes.length)
                                       .append ("Offset", m_nOffset)
                                       .append ("Length", m_nLength)
                                       .append ("IsCopy", m_bIsCopy)
                                       .append ("Index", m_nIndex)
                                       .getToString ();
  }
}

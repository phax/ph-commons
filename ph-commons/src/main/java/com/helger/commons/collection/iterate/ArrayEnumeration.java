/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.collection.iterate;

import java.util.Enumeration;
import java.util.NoSuchElementException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * This is a small helper class implementing {@link Enumeration} for array input
 * data.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        Type of object to iterate.
 */
@NotThreadSafe
public final class ArrayEnumeration <ELEMENTTYPE> implements Enumeration <ELEMENTTYPE>
{
  private ELEMENTTYPE [] m_aArray;
  private int m_nIndex;

  /**
   * Constructor iterating over the whole array
   *
   * @param aArray
   *        The array to enumerate. May not be <code>null</code>.
   */
  @SafeVarargs
  public ArrayEnumeration (@Nonnull final ELEMENTTYPE... aArray)
  {
    this (aArray, 0, aArray.length);
  }

  /**
   * Constructor iterating an array partially.
   *
   * @param aArray
   *        The array to be enumerated. May not be <code>null</code>.
   * @param nStartIndex
   *        The index of the first element to be enumerated.
   * @param nLength
   *        The maximum number of elements to be iterated.
   */
  public ArrayEnumeration (@Nonnull final ELEMENTTYPE [] aArray,
                           @Nonnegative final int nStartIndex,
                           @Nonnegative final int nLength)
  {
    ValueEnforcer.notNull (aArray, "Array");
    ValueEnforcer.isGE0 (nStartIndex, "StartIndex");
    ValueEnforcer.isGE0 (nLength, "Length");
    m_nIndex = 0;
    m_aArray = ArrayHelper.getCopy (aArray, nStartIndex, nLength);
  }

  public boolean hasMoreElements ()
  {
    return m_nIndex < m_aArray.length;
  }

  @Nullable
  public ELEMENTTYPE nextElement ()
  {
    if (!hasMoreElements ())
      throw new NoSuchElementException ();
    final ELEMENTTYPE ret = m_aArray[m_nIndex];
    ++m_nIndex;
    return ret;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ArrayEnumeration <?> rhs = (ArrayEnumeration <?>) o;
    return EqualsHelper.equals (m_aArray, rhs.m_aArray) && m_nIndex == rhs.m_nIndex;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aArray).append (m_nIndex).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("array", m_aArray).append ("index", m_nIndex).toString ();
  }
}

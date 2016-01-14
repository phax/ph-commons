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
package com.helger.commons.mutable;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.compare.CompareHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

/**
 * Object wrapper around a char so that it can be passed a final object but is
 * mutable.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class MutableChar extends AbstractMutableInteger <MutableChar>
{
  /** The default value if the default constructor is used. */
  public static final char DEFAULT_VALUE = CGlobal.DEFAULT_CHAR;

  private char m_cValue;

  /**
   * Initialize with default value {@value #DEFAULT_VALUE}.
   */
  public MutableChar ()
  {
    this (DEFAULT_VALUE);
  }

  /**
   * Initialize with a certain int value. If the value does not fit into a char,
   * the value is cut!
   *
   * @param cValue
   *        The value to be used.
   */
  public MutableChar (final int cValue)
  {
    this ((char) cValue);
  }

  /**
   * Initialize with a certain value.
   *
   * @param aValue
   *        The value to be used.
   */
  public MutableChar (@Nonnull final Character aValue)
  {
    this (aValue.charValue ());
  }

  /**
   * Initialize with a certain value.
   *
   * @param cValue
   *        The value to be used.
   */
  public MutableChar (final char cValue)
  {
    m_cValue = cValue;
  }

  public MutableChar (@Nonnull final MutableChar aOther)
  {
    this (aOther.m_cValue);
  }

  public char charValue ()
  {
    return m_cValue;
  }

  @Override
  public float floatValue ()
  {
    return m_cValue;
  }

  @Override
  public double doubleValue ()
  {
    return m_cValue;
  }

  @Override
  public int intValue ()
  {
    return m_cValue;
  }

  @Override
  public long longValue ()
  {
    return m_cValue;
  }

  /**
   * Increment by 1 and return the modified value.
   *
   * @return The by 1 incremented value.
   */
  public int inc ()
  {
    return inc (1);
  }

  public int inc (final int nDelta)
  {
    m_cValue += nDelta;
    onAfterChange ();
    return m_cValue;
  }

  public int inc (@Nonnull final MutableChar aMC)
  {
    ValueEnforcer.notNull (aMC, "MC");
    return inc (aMC.m_cValue);
  }

  public int dec ()
  {
    return inc (-1);
  }

  public int dec (final int nDelta)
  {
    return inc (-nDelta);
  }

  public int dec (@Nonnull final MutableChar aMC)
  {
    ValueEnforcer.notNull (aMC, "MC");
    return inc (-aMC.m_cValue);
  }

  @Nonnull
  public EChange set (final int cValue)
  {
    return set ((char) cValue);
  }

  @Nonnull
  public EChange set (@Nonnull final MutableChar aMC)
  {
    ValueEnforcer.notNull (aMC, "MC");
    return set (aMC.m_cValue);
  }

  @Nonnull
  public EChange set (final char cValue)
  {
    if (m_cValue == cValue)
      return EChange.UNCHANGED;
    m_cValue = cValue;
    onAfterChange ();
    return EChange.CHANGED;
  }

  public boolean is0 ()
  {
    return m_cValue == 0;
  }

  public boolean isSmaller0 ()
  {
    // char is always >= 0
    return false;
  }

  public boolean isSmallerOrEqual0 ()
  {
    return m_cValue <= 0;
  }

  public boolean isGreater0 ()
  {
    return m_cValue > 0;
  }

  public boolean isGreaterOrEqual0 ()
  {
    // char is always >= 0
    return true;
  }

  public boolean isEven ()
  {
    return (m_cValue % 2) == 0;
  }

  public int compareTo (@Nonnull final MutableChar rhs)
  {
    return CompareHelper.compare (m_cValue, rhs.m_cValue);
  }

  @Nonnull
  public MutableChar getClone ()
  {
    return new MutableChar (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MutableChar rhs = (MutableChar) o;
    return m_cValue == rhs.m_cValue;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_cValue).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("value", m_cValue).toString ();
  }
}

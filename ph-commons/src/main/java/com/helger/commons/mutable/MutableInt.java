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
 * Object wrapper around an int so that it can be passed a final object but is
 * mutable.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class MutableInt extends AbstractMutableInteger <MutableInt>
{
  /** The default value if the default constructor is used. */
  public static final int DEFAULT_VALUE = CGlobal.DEFAULT_INT;

  private int m_nValue;

  /**
   * Initialize with default value {@value #DEFAULT_VALUE}.
   */
  public MutableInt ()
  {
    this (DEFAULT_VALUE);
  }

  /**
   * Initialize with a certain value.
   *
   * @param aValue
   *        The value to be used.
   */
  public MutableInt (@Nonnull final Number aValue)
  {
    this (aValue.intValue ());
  }

  /**
   * Initialize with a certain value.
   *
   * @param nValue
   *        The value to be used.
   */
  public MutableInt (final int nValue)
  {
    m_nValue = nValue;
  }

  @Override
  public int intValue ()
  {
    return m_nValue;
  }

  @Override
  public float floatValue ()
  {
    return m_nValue;
  }

  @Override
  public double doubleValue ()
  {
    return m_nValue;
  }

  @Override
  public long longValue ()
  {
    return m_nValue;
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
    m_nValue += nDelta;
    onAfterChange ();
    return m_nValue;
  }

  public int inc (@Nonnull final Number aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (aDelta.intValue ());
  }

  public int dec ()
  {
    return inc (-1);
  }

  public int dec (final int nDelta)
  {
    return inc (-nDelta);
  }

  public int dec (@Nonnull final Number aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (-aDelta.intValue ());
  }

  public int divide (final int nDivisor)
  {
    m_nValue /= nDivisor;
    onAfterChange ();
    return m_nValue;
  }

  public int divide (@Nonnull final Number aDivisor)
  {
    ValueEnforcer.notNull (aDivisor, "Divisor");
    return divide (aDivisor.intValue ());
  }

  public int multiply (final int nMultiplicand)
  {
    m_nValue *= nMultiplicand;
    onAfterChange ();
    return m_nValue;
  }

  public int multiply (@Nonnull final Number aMultiplicand)
  {
    ValueEnforcer.notNull (aMultiplicand, "Multiplicand");
    return multiply (aMultiplicand.intValue ());
  }

  @Nonnull
  public EChange set (final int nValue)
  {
    if (m_nValue == nValue)
      return EChange.UNCHANGED;
    m_nValue = nValue;
    onAfterChange ();
    return EChange.CHANGED;
  }

  @Nonnull
  public EChange set (@Nonnull final Number aValue)
  {
    ValueEnforcer.notNull (aValue, "Value");
    return set (aValue.intValue ());
  }

  public boolean is0 ()
  {
    return m_nValue == 0;
  }

  public boolean isSmaller0 ()
  {
    return m_nValue < 0;
  }

  public boolean isSmallerOrEqual0 ()
  {
    return m_nValue <= 0;
  }

  public boolean isGreater0 ()
  {
    return m_nValue > 0;
  }

  public boolean isGreaterOrEqual0 ()
  {
    return m_nValue >= 0;
  }

  public boolean isEven ()
  {
    return (m_nValue % 2) == 0;
  }

  public int compareTo (@Nonnull final MutableInt rhs)
  {
    return CompareHelper.compare (m_nValue, rhs.m_nValue);
  }

  @Nonnull
  public MutableInt getClone ()
  {
    return new MutableInt (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MutableInt rhs = (MutableInt) o;
    return m_nValue == rhs.m_nValue;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_nValue).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("value", m_nValue).toString ();
  }
}

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
package com.helger.base.numeric.mutable;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.compare.CompareHelper;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.state.EChange;
import com.helger.base.tostring.ToStringGenerator;

/**
 * Object wrapper around a double so that it can be passed a final object but is mutable.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class MutableDouble extends AbstractMutableNumeric <MutableDouble>
{
  private double m_dValue;

  /**
   * Initialize with a certain value.
   *
   * @param aValue
   *        The value to be used.
   */
  public MutableDouble (@NonNull final Number aValue)
  {
    this (aValue.doubleValue ());
  }

  /**
   * Initialize with a certain value.
   *
   * @param dValue
   *        The value to be used.
   */
  public MutableDouble (final double dValue)
  {
    m_dValue = dValue;
  }

  @Override
  public double doubleValue ()
  {
    return m_dValue;
  }

  @Override
  public float floatValue ()
  {
    return (float) m_dValue;
  }

  @Override
  public int intValue ()
  {
    return (int) m_dValue;
  }

  @Override
  public long longValue ()
  {
    return (long) m_dValue;
  }

  /**
   * Increment by 1 and return the modified value.
   *
   * @return The by 1 incremented value.
   */
  public double inc ()
  {
    return inc (1);
  }

  /**
   * Increment by the given delta and return the modified value.
   *
   * @param dDelta
   *        The delta to add.
   * @return The new value after incrementing.
   */
  public double inc (final double dDelta)
  {
    m_dValue += dDelta;
    onAfterChange ();
    return m_dValue;
  }

  /**
   * Increment by the given delta and return the modified value.
   *
   * @param aDelta
   *        The delta to add. May not be <code>null</code>.
   * @return The new value after incrementing.
   */
  public double inc (@NonNull final Number aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (aDelta.doubleValue ());
  }

  /**
   * Decrement by 1 and return the modified value.
   *
   * @return The by 1 decremented value.
   */
  public double dec ()
  {
    return inc (-1);
  }

  /**
   * Decrement by the given delta and return the modified value.
   *
   * @param dDelta
   *        The delta to subtract.
   * @return The new value after decrementing.
   */
  public double dec (final double dDelta)
  {
    return inc (-dDelta);
  }

  /**
   * Decrement by the given delta and return the modified value.
   *
   * @param aDelta
   *        The delta to subtract. May not be <code>null</code>.
   * @return The new value after decrementing.
   */
  public double dec (@NonNull final Number aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (-aDelta.doubleValue ());
  }

  /**
   * Divide the current value by the given divisor.
   *
   * @param dDivisor
   *        The divisor to use.
   * @return The new value after division.
   */
  public double divide (final double dDivisor)
  {
    m_dValue /= dDivisor;
    onAfterChange ();
    return m_dValue;
  }

  /**
   * Divide the current value by the given divisor.
   *
   * @param aDivisor
   *        The divisor to use. May not be <code>null</code>.
   * @return The new value after division.
   */
  public double divide (@NonNull final Number aDivisor)
  {
    ValueEnforcer.notNull (aDivisor, "Divisor");
    return divide (aDivisor.doubleValue ());
  }

  /**
   * Multiply the current value by the given multiplicand.
   *
   * @param dMultiplicand
   *        The multiplicand to use.
   * @return The new value after multiplication.
   */
  public double multiply (final double dMultiplicand)
  {
    m_dValue *= dMultiplicand;
    onAfterChange ();
    return m_dValue;
  }

  /**
   * Multiply the current value by the given multiplicand.
   *
   * @param aMultiplicand
   *        The multiplicand to use. May not be <code>null</code>.
   * @return The new value after multiplication.
   */
  public double multiply (@NonNull final Number aMultiplicand)
  {
    ValueEnforcer.notNull (aMultiplicand, "Multiplicand");
    return multiply (aMultiplicand.doubleValue ());
  }

  /**
   * Set a new value.
   *
   * @param dValue
   *        The new value to set.
   * @return {@link EChange#CHANGED} if the value was changed.
   */
  @NonNull
  public EChange set (final double dValue)
  {
    if (EqualsHelper.equals (dValue, m_dValue))
      return EChange.UNCHANGED;
    m_dValue = dValue;
    onAfterChange ();
    return EChange.CHANGED;
  }

  /**
   * Set a new value.
   *
   * @param aValue
   *        The new value to set. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if the value was changed.
   */
  @NonNull
  public EChange set (@NonNull final Number aValue)
  {
    ValueEnforcer.notNull (aValue, "Value");
    return set (aValue.doubleValue ());
  }

  /** {@inheritDoc} */
  public boolean is0 ()
  {
    return EqualsHelper.equals (m_dValue, 0);
  }

  /** {@inheritDoc} */
  public boolean isLT0 ()
  {
    return CompareHelper.compare (m_dValue, 0) < 0;
  }

  /** {@inheritDoc} */
  public boolean isLE0 ()
  {
    return CompareHelper.compare (m_dValue, 0) <= 0;
  }

  /** {@inheritDoc} */
  public boolean isGT0 ()
  {
    return CompareHelper.compare (m_dValue, 0) > 0;
  }

  /** {@inheritDoc} */
  public boolean isGE0 ()
  {
    return CompareHelper.compare (m_dValue, 0) >= 0;
  }

  /**
   * Get the current value and then increment by 1.
   *
   * @return The value before incrementing.
   */
  public double getAndInc ()
  {
    final double ret = doubleValue ();
    inc ();
    return ret;
  }

  /**
   * Increment by 1 and then get the new value.
   *
   * @return The value after incrementing.
   */
  public double incAndGet ()
  {
    inc ();
    return doubleValue ();
  }

  /** {@inheritDoc} */
  public int compareTo (@NonNull final MutableDouble rhs)
  {
    return CompareHelper.compare (m_dValue, rhs.m_dValue);
  }

  /** {@inheritDoc} */
  @NonNull
  public MutableDouble getClone ()
  {
    return new MutableDouble (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MutableDouble rhs = (MutableDouble) o;
    return EqualsHelper.equals (m_dValue, rhs.m_dValue);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_dValue).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("value", m_dValue).getToString ();
  }
}

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
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.state.EChange;
import com.helger.base.tostring.ToStringGenerator;

/**
 * Object wrapper around a long so that it can be passed a final object but is
 * mutable.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class MutableLong extends AbstractMutableInteger <MutableLong>
{
  private long m_nValue;

  /**
   * Initialize with a certain value.
   *
   * @param aValue
   *        The value to be used.
   */
  public MutableLong (@NonNull final Number aValue)
  {
    this (aValue.longValue ());
  }

  /**
   * Initialize with a certain value.
   *
   * @param nValue
   *        The value to be used.
   */
  public MutableLong (final long nValue)
  {
    m_nValue = nValue;
  }

  @Override
  public long longValue ()
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
  public int intValue ()
  {
    return (int) m_nValue;
  }

  /**
   * Increment by 1 and return the modified value.
   *
   * @return The by 1 incremented value.
   */
  public long inc ()
  {
    return inc (1);
  }

  /**
   * Increment by the given delta and return the modified value.
   *
   * @param nDelta
   *        The delta to add.
   * @return The new value after incrementing.
   */
  public long inc (final long nDelta)
  {
    m_nValue += nDelta;
    onAfterChange ();
    return m_nValue;
  }

  /**
   * Increment by the given delta and return the modified value.
   *
   * @param aDelta
   *        The delta to add. May not be <code>null</code>.
   * @return The new value after incrementing.
   */
  public long inc (@NonNull final Number aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (aDelta.longValue ());
  }

  /**
   * Decrement by 1 and return the modified value.
   *
   * @return The by 1 decremented value.
   */
  public long dec ()
  {
    return inc (-1);
  }

  /**
   * Decrement by the given delta and return the modified value.
   *
   * @param nDelta
   *        The delta to subtract.
   * @return The new value after decrementing.
   */
  public long dec (final long nDelta)
  {
    return inc (-nDelta);
  }

  /**
   * Decrement by the given delta and return the modified value.
   *
   * @param aDelta
   *        The delta to subtract. May not be <code>null</code>.
   * @return The new value after decrementing.
   */
  public long dec (@NonNull final Number aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (-aDelta.longValue ());
  }

  /**
   * Divide the current value by the given divisor.
   *
   * @param nDivisor
   *        The divisor to use.
   * @return The new value after division.
   */
  public long divide (final long nDivisor)
  {
    m_nValue /= nDivisor;
    onAfterChange ();
    return m_nValue;
  }

  /**
   * Divide the current value by the given divisor.
   *
   * @param aDivisor
   *        The divisor to use. May not be <code>null</code>.
   * @return The new value after division.
   */
  public long divide (@NonNull final Number aDivisor)
  {
    ValueEnforcer.notNull (aDivisor, "Divisor");
    return divide (aDivisor.longValue ());
  }

  /**
   * Multiply the current value by the given multiplicand.
   *
   * @param nMultiplicand
   *        The multiplicand to use.
   * @return The new value after multiplication.
   */
  public long multiply (final long nMultiplicand)
  {
    m_nValue *= nMultiplicand;
    onAfterChange ();
    return m_nValue;
  }

  /**
   * Multiply the current value by the given multiplicand.
   *
   * @param aMultiplicand
   *        The multiplicand to use. May not be <code>null</code>.
   * @return The new value after multiplication.
   */
  public long multiply (@NonNull final Number aMultiplicand)
  {
    ValueEnforcer.notNull (aMultiplicand, "Multiplicand");
    return multiply (aMultiplicand.longValue ());
  }

  /**
   * Set a new value.
   *
   * @param nValue
   *        The new value to set.
   * @return {@link EChange#CHANGED} if the value was changed.
   */
  @NonNull
  public EChange set (final long nValue)
  {
    if (nValue == m_nValue)
      return EChange.UNCHANGED;
    m_nValue = nValue;
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
    return set (aValue.longValue ());
  }

  /** {@inheritDoc} */
  public boolean is0 ()
  {
    return m_nValue == 0;
  }

  /** {@inheritDoc} */
  public boolean isLT0 ()
  {
    return m_nValue < 0;
  }

  /** {@inheritDoc} */
  public boolean isLE0 ()
  {
    return m_nValue <= 0;
  }

  /** {@inheritDoc} */
  public boolean isGT0 ()
  {
    return m_nValue > 0;
  }

  /** {@inheritDoc} */
  public boolean isGE0 ()
  {
    return m_nValue >= 0;
  }

  /** {@inheritDoc} */
  public boolean isEven ()
  {
    return (m_nValue % 2) == 0;
  }

  /**
   * Get the current value and then increment by 1.
   *
   * @return The value before incrementing.
   */
  public long getAndInc ()
  {
    final long ret = longValue ();
    inc ();
    return ret;
  }

  /**
   * Increment by 1 and then get the new value.
   *
   * @return The value after incrementing.
   */
  public long incAndGet ()
  {
    inc ();
    return longValue ();
  }

  /** {@inheritDoc} */
  public int compareTo (@NonNull final MutableLong rhs)
  {
    return CompareHelper.compare (m_nValue, rhs.m_nValue);
  }

  /** {@inheritDoc} */
  @NonNull
  public MutableLong getClone ()
  {
    return new MutableLong (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MutableLong rhs = (MutableLong) o;
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
    return new ToStringGenerator (this).append ("value", m_nValue).getToString ();
  }
}

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
 * Object wrapper around a short so that it can be passed a final object but is
 * mutable.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class MutableShort extends AbstractMutableInteger <MutableShort>
{
  private short m_nValue;

  /**
   * Initialize with a certain int value. If the value does not fit into a
   * short, the value is cut!
   *
   * @param nValue
   *        The value to be used.
   */
  public MutableShort (final int nValue)
  {
    this ((short) nValue);
  }

  /**
   * Initialize with a certain value.
   *
   * @param aValue
   *        The value to be used.
   */
  public MutableShort (@NonNull final Number aValue)
  {
    this (aValue.shortValue ());
  }

  /**
   * Initialize with a certain value.
   *
   * @param nValue
   *        The value to be used.
   */
  public MutableShort (final short nValue)
  {
    m_nValue = nValue;
  }

  @Override
  public short shortValue ()
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

  /**
   * Increment by the given delta and return the modified value.
   *
   * @param nDelta
   *        The delta to add.
   * @return The new value after incrementing.
   */
  public int inc (final int nDelta)
  {
    m_nValue = (short) (m_nValue + nDelta);
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
  public int inc (@NonNull final Number aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (aDelta.shortValue ());
  }

  /**
   * Decrement by 1 and return the modified value.
   *
   * @return The by 1 decremented value.
   */
  public int dec ()
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
  public int dec (final int nDelta)
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
  public int dec (@NonNull final Number aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (-aDelta.shortValue ());
  }

  /**
   * Set a new value.
   *
   * @param nValue
   *        The new value to set. If the value does not fit into a short, it is cut.
   * @return {@link EChange#CHANGED} if the value was changed.
   */
  @NonNull
  public EChange set (final int nValue)
  {
    return set ((short) nValue);
  }

  /**
   * Set a new value.
   *
   * @param nValue
   *        The new value to set.
   * @return {@link EChange#CHANGED} if the value was changed.
   */
  @NonNull
  public EChange set (final short nValue)
  {
    if (m_nValue == nValue)
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
    return set (aValue.shortValue ());
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
  public short getAndInc ()
  {
    final short ret = shortValue ();
    inc ();
    return ret;
  }

  /**
   * Increment by 1 and then get the new value.
   *
   * @return The value after incrementing.
   */
  public short incAndGet ()
  {
    inc ();
    return shortValue ();
  }

  /** {@inheritDoc} */
  public int compareTo (@NonNull final MutableShort rhs)
  {
    return CompareHelper.compare (m_nValue, rhs.m_nValue);
  }

  /** {@inheritDoc} */
  @NonNull
  public MutableShort getClone ()
  {
    return new MutableShort (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MutableShort rhs = (MutableShort) o;
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

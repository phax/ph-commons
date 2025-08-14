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
package com.helger.commons.mutable;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.compare.CompareHelper;
import com.helger.base.enforcer.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.state.EChange;
import com.helger.base.string.ToStringGenerator;

import jakarta.annotation.Nonnull;

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
  public MutableLong (@Nonnull final Number aValue)
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

  public long inc (final long nDelta)
  {
    m_nValue += nDelta;
    onAfterChange ();
    return m_nValue;
  }

  public long inc (@Nonnull final Number aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (aDelta.longValue ());
  }

  public long dec ()
  {
    return inc (-1);
  }

  public long dec (final long nDelta)
  {
    return inc (-nDelta);
  }

  public long dec (@Nonnull final Number aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (-aDelta.longValue ());
  }

  public long divide (final long nDivisor)
  {
    m_nValue /= nDivisor;
    onAfterChange ();
    return m_nValue;
  }

  public long divide (@Nonnull final Number aDivisor)
  {
    ValueEnforcer.notNull (aDivisor, "Divisor");
    return divide (aDivisor.longValue ());
  }

  public long multiply (final long nMultiplicand)
  {
    m_nValue *= nMultiplicand;
    onAfterChange ();
    return m_nValue;
  }

  public long multiply (@Nonnull final Number aMultiplicand)
  {
    ValueEnforcer.notNull (aMultiplicand, "Multiplicand");
    return multiply (aMultiplicand.longValue ());
  }

  @Nonnull
  public EChange set (final long nValue)
  {
    if (nValue == m_nValue)
      return EChange.UNCHANGED;
    m_nValue = nValue;
    onAfterChange ();
    return EChange.CHANGED;
  }

  @Nonnull
  public EChange set (@Nonnull final Number aValue)
  {
    ValueEnforcer.notNull (aValue, "Value");
    return set (aValue.longValue ());
  }

  public boolean is0 ()
  {
    return m_nValue == 0;
  }

  public boolean isLT0 ()
  {
    return m_nValue < 0;
  }

  public boolean isLE0 ()
  {
    return m_nValue <= 0;
  }

  public boolean isGT0 ()
  {
    return m_nValue > 0;
  }

  public boolean isGE0 ()
  {
    return m_nValue >= 0;
  }

  public boolean isEven ()
  {
    return (m_nValue % 2) == 0;
  }

  public long getAndInc ()
  {
    final long ret = longValue ();
    inc ();
    return ret;
  }

  public long incAndGet ()
  {
    inc ();
    return longValue ();
  }

  public int compareTo (@Nonnull final MutableLong rhs)
  {
    return CompareHelper.compare (m_nValue, rhs.m_nValue);
  }

  @Nonnull
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

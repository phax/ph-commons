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
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.state.EChange;
import com.helger.base.string.ToStringGenerator;

import jakarta.annotation.Nonnull;

/**
 * Object wrapper around a double so that it can be passed a final object but is mutable.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class MutableDouble extends AbstractMutableNumeric <MutableDouble>
{
  private double m_dValue;

  public MutableDouble (@Nonnull final Number aValue)
  {
    this (aValue.doubleValue ());
  }

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

  public double inc (final double dDelta)
  {
    m_dValue += dDelta;
    onAfterChange ();
    return m_dValue;
  }

  public double inc (@Nonnull final Number aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (aDelta.doubleValue ());
  }

  public double dec ()
  {
    return inc (-1);
  }

  public double dec (final double dDelta)
  {
    return inc (-dDelta);
  }

  public double dec (@Nonnull final Number aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (-aDelta.doubleValue ());
  }

  public double divide (final double dDivisor)
  {
    m_dValue /= dDivisor;
    onAfterChange ();
    return m_dValue;
  }

  public double divide (@Nonnull final Number aDivisor)
  {
    ValueEnforcer.notNull (aDivisor, "Divisor");
    return divide (aDivisor.doubleValue ());
  }

  public double multiply (final double dMultiplicand)
  {
    m_dValue *= dMultiplicand;
    onAfterChange ();
    return m_dValue;
  }

  public double multiply (@Nonnull final Number aMultiplicand)
  {
    ValueEnforcer.notNull (aMultiplicand, "Multiplicand");
    return multiply (aMultiplicand.doubleValue ());
  }

  @Nonnull
  public EChange set (final double dValue)
  {
    if (EqualsHelper.equals (dValue, m_dValue))
      return EChange.UNCHANGED;
    m_dValue = dValue;
    onAfterChange ();
    return EChange.CHANGED;
  }

  @Nonnull
  public EChange set (@Nonnull final Number aValue)
  {
    ValueEnforcer.notNull (aValue, "Value");
    return set (aValue.doubleValue ());
  }

  public boolean is0 ()
  {
    return EqualsHelper.equals (m_dValue, 0);
  }

  public boolean isLT0 ()
  {
    return CompareHelper.compare (m_dValue, 0) < 0;
  }

  public boolean isLE0 ()
  {
    return CompareHelper.compare (m_dValue, 0) <= 0;
  }

  public boolean isGT0 ()
  {
    return CompareHelper.compare (m_dValue, 0) > 0;
  }

  public boolean isGE0 ()
  {
    return CompareHelper.compare (m_dValue, 0) >= 0;
  }

  public double getAndInc ()
  {
    final double ret = doubleValue ();
    inc ();
    return ret;
  }

  public double incAndGet ()
  {
    inc ();
    return doubleValue ();
  }

  public int compareTo (@Nonnull final MutableDouble rhs)
  {
    return CompareHelper.compare (m_dValue, rhs.m_dValue);
  }

  @Nonnull
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

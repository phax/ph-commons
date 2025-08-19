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
package com.helger.base.numeric.mutable;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.compare.CompareHelper;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.state.EChange;
import com.helger.base.tostring.ToStringGenerator;

import jakarta.annotation.Nonnull;

/**
 * Object wrapper around a float so that it can be passed a final object but is mutable.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class MutableFloat extends AbstractMutableNumeric <MutableFloat>
{
  private float m_fValue;

  public MutableFloat (@Nonnull final Number aValue)
  {
    this (aValue.floatValue ());
  }

  public MutableFloat (final float fValue)
  {
    m_fValue = fValue;
  }

  @Override
  public float floatValue ()
  {
    return m_fValue;
  }

  @Override
  public double doubleValue ()
  {
    return m_fValue;
  }

  @Override
  public int intValue ()
  {
    return (int) m_fValue;
  }

  @Override
  public long longValue ()
  {
    return (long) m_fValue;
  }

  /**
   * Increment by 1 and return the modified value.
   *
   * @return The by 1 incremented value.
   */
  public float inc ()
  {
    return inc (1f);
  }

  public float inc (final float fDelta)
  {
    m_fValue += fDelta;
    onAfterChange ();
    return m_fValue;
  }

  public float inc (@Nonnull final Number aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (aDelta.floatValue ());
  }

  public float dec ()
  {
    return inc (-1f);
  }

  public float dec (final float fDelta)
  {
    return inc (-fDelta);
  }

  public float dec (@Nonnull final Number aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (-aDelta.floatValue ());
  }

  public float divide (final float fDivisor)
  {
    m_fValue /= fDivisor;
    onAfterChange ();
    return m_fValue;
  }

  public float divide (@Nonnull final Number aDivisor)
  {
    ValueEnforcer.notNull (aDivisor, "Divisor");
    return divide (aDivisor.floatValue ());
  }

  public float multiply (final float fMultiplicand)
  {
    m_fValue *= fMultiplicand;
    onAfterChange ();
    return m_fValue;
  }

  public float multiply (@Nonnull final Number aMultiplicand)
  {
    ValueEnforcer.notNull (aMultiplicand, "Multiplicand");
    return multiply (aMultiplicand.floatValue ());
  }

  @Nonnull
  public EChange set (final float fValue)
  {
    if (EqualsHelper.equals (fValue, m_fValue))
      return EChange.UNCHANGED;
    m_fValue = fValue;
    onAfterChange ();
    return EChange.CHANGED;
  }

  @Nonnull
  public EChange set (@Nonnull final Number aValue)
  {
    ValueEnforcer.notNull (aValue, "Value");
    return set (aValue.floatValue ());
  }

  public boolean is0 ()
  {
    return EqualsHelper.equals (m_fValue, 0f);
  }

  public boolean isLT0 ()
  {
    return CompareHelper.compare (m_fValue, 0f) < 0;
  }

  public boolean isLE0 ()
  {
    return CompareHelper.compare (m_fValue, 0f) <= 0;
  }

  public boolean isGT0 ()
  {
    return CompareHelper.compare (m_fValue, 0f) > 0;
  }

  public boolean isGE0 ()
  {
    return CompareHelper.compare (m_fValue, 0f) >= 0;
  }

  public float getAndInc ()
  {
    final float ret = floatValue ();
    inc ();
    return ret;
  }

  public float incAndGet ()
  {
    inc ();
    return floatValue ();
  }

  public int compareTo (@Nonnull final MutableFloat rhs)
  {
    return CompareHelper.compare (m_fValue, rhs.m_fValue);
  }

  @Nonnull
  public MutableFloat getClone ()
  {
    return new MutableFloat (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MutableFloat rhs = (MutableFloat) o;
    return EqualsHelper.equals (m_fValue, rhs.m_fValue);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_fValue).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("value", m_fValue).getToString ();
  }
}

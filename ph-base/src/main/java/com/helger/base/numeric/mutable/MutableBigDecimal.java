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

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.CGlobal;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.numeric.BigHelper;
import com.helger.base.state.EChange;
import com.helger.base.tostring.ToStringGenerator;

/**
 * Object wrapper around a {@link BigDecimal} so that it can be passed a final object but is
 * mutable.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class MutableBigDecimal extends AbstractMutableNumeric <MutableBigDecimal>
{
  private BigDecimal m_aValue;

  public MutableBigDecimal (final long nValue)
  {
    this (BigHelper.toBigDecimal (nValue));
  }

  public MutableBigDecimal (final double dValue)
  {
    this (BigHelper.toBigDecimal (dValue));
  }

  public MutableBigDecimal (@NonNull final MutableBigDecimal aOther)
  {
    this (aOther.m_aValue);
  }

  public MutableBigDecimal (@NonNull final BigDecimal aValue)
  {
    m_aValue = ValueEnforcer.notNull (aValue, "Value");
  }

  @Override
  @NonNull
  public BigDecimal getAsBigDecimal ()
  {
    return m_aValue;
  }

  @Override
  public double doubleValue ()
  {
    return m_aValue.doubleValue ();
  }

  @Override
  public float floatValue ()
  {
    return m_aValue.floatValue ();
  }

  @Override
  public int intValue ()
  {
    return m_aValue.intValue ();
  }

  @Override
  public long longValue ()
  {
    return m_aValue.longValue ();
  }

  /**
   * Increment by 1 and return the modified value.
   *
   * @return The by 1 incremented value.
   */
  @NonNull
  public BigDecimal inc ()
  {
    return inc (BigDecimal.ONE);
  }

  @NonNull
  public BigDecimal inc (final long nDelta)
  {
    return inc (BigHelper.toBigDecimal (nDelta));
  }

  @NonNull
  public BigDecimal inc (final double dDelta)
  {
    return inc (BigHelper.toBigDecimal (dDelta));
  }

  @NonNull
  public BigDecimal inc (@NonNull final MutableBigDecimal aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (aDelta.m_aValue);
  }

  @NonNull
  public BigDecimal inc (@NonNull final BigDecimal aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    m_aValue = m_aValue.add (aDelta);
    onAfterChange ();
    return m_aValue;
  }

  @NonNull
  public BigDecimal dec ()
  {
    return inc (CGlobal.BIGDEC_MINUS_ONE);
  }

  @NonNull
  public BigDecimal dec (final long nDelta)
  {
    return inc (BigHelper.toBigDecimal (-nDelta));
  }

  @NonNull
  public BigDecimal dec (final double dDelta)
  {
    return inc (BigHelper.toBigDecimal (-dDelta));
  }

  @NonNull
  public BigDecimal dec (@NonNull final BigDecimal aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (aDelta.negate ());
  }

  @NonNull
  public BigDecimal dec (@NonNull final MutableBigDecimal aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (aDelta.m_aValue.negate ());
  }

  @NonNull
  public BigDecimal divide (final long nDivisor,
                            @Nonnegative final int nScale,
                            @NonNull final RoundingMode eRoundingMode)
  {
    return divide (BigHelper.toBigDecimal (nDivisor), nScale, eRoundingMode);
  }

  @NonNull
  public BigDecimal divide (final double dDivisor,
                            @Nonnegative final int nScale,
                            @NonNull final RoundingMode eRoundingMode)
  {
    return divide (BigHelper.toBigDecimal (dDivisor), nScale, eRoundingMode);
  }

  @NonNull
  public BigDecimal divide (@NonNull final MutableBigDecimal aDivisor,
                            @Nonnegative final int nScale,
                            @NonNull final RoundingMode eRoundingMode)
  {
    ValueEnforcer.notNull (aDivisor, "Divisor");
    return divide (aDivisor.m_aValue, nScale, eRoundingMode);
  }

  @NonNull
  public BigDecimal divide (@NonNull final BigDecimal aDivisor,
                            @Nonnegative final int nScale,
                            @NonNull final RoundingMode eRoundingMode)
  {
    ValueEnforcer.notNull (aDivisor, "Divisor");
    m_aValue = m_aValue.divide (aDivisor, nScale, eRoundingMode);
    onAfterChange ();
    return m_aValue;
  }

  @NonNull
  public BigDecimal multiply (final long nMultiplicand)
  {
    return multiply (BigHelper.toBigDecimal (nMultiplicand));
  }

  @NonNull
  public BigDecimal multiply (final double dMultiplicand)
  {
    return multiply (BigHelper.toBigDecimal (dMultiplicand));
  }

  @NonNull
  public BigDecimal multiply (@NonNull final MutableBigDecimal aMultiplicand)
  {
    ValueEnforcer.notNull (aMultiplicand, "Multiplicand");
    return multiply (aMultiplicand.m_aValue);
  }

  @NonNull
  public BigDecimal multiply (@NonNull final BigDecimal aMultiplicand)
  {
    ValueEnforcer.notNull (aMultiplicand, "Multiplicand");
    m_aValue = m_aValue.multiply (aMultiplicand);
    onAfterChange ();
    return m_aValue;
  }

  @NonNull
  public EChange set (final long nDelta)
  {
    return set (BigHelper.toBigDecimal (nDelta));
  }

  @NonNull
  public EChange set (final double dDelta)
  {
    return set (BigHelper.toBigDecimal (dDelta));
  }

  @NonNull
  public EChange set (@NonNull final MutableBigDecimal aValue)
  {
    ValueEnforcer.notNull (aValue, "Value");
    return set (aValue.m_aValue);
  }

  @NonNull
  public EChange set (@NonNull final BigDecimal aValue)
  {
    ValueEnforcer.notNull (aValue, "Value");
    if (aValue.equals (m_aValue))
      return EChange.UNCHANGED;
    m_aValue = aValue;
    onAfterChange ();
    return EChange.CHANGED;
  }

  public boolean is0 ()
  {
    return BigHelper.isEQ0 (m_aValue);
  }

  public boolean isLT0 ()
  {
    return BigHelper.isLT0 (m_aValue);
  }

  public boolean isLE0 ()
  {
    return BigHelper.isLE0 (m_aValue);
  }

  public boolean isGT0 ()
  {
    return BigHelper.isGT0 (m_aValue);
  }

  public boolean isGE0 ()
  {
    return BigHelper.isGE0 (m_aValue);
  }

  @NonNull
  public BigDecimal getAndInc ()
  {
    final BigDecimal ret = getAsBigDecimal ();
    inc ();
    return ret;
  }

  @NonNull
  public BigDecimal incAndGet ()
  {
    inc ();
    return getAsBigDecimal ();
  }

  public int compareTo (@NonNull final MutableBigDecimal rhs)
  {
    return m_aValue.compareTo (rhs.m_aValue);
  }

  @NonNull
  public MutableBigDecimal getClone ()
  {
    return new MutableBigDecimal (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MutableBigDecimal rhs = (MutableBigDecimal) o;
    final Object aObj1 = m_aValue;
    return EqualsHelper.equals (aObj1, rhs.m_aValue);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aValue).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("value", m_aValue).getToString ();
  }
}

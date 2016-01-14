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

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.math.MathHelper;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

/**
 * Object wrapper around a {@link BigDecimal} so that it can be passed a final
 * object but is mutable.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class MutableBigDecimal extends AbstractMutableNumeric <MutableBigDecimal>
{
  /** The default value if the default constructor is used. */
  public static final BigDecimal DEFAULT_VALUE = BigDecimal.ZERO;

  private BigDecimal m_aValue;

  /**
   * Initialize with default value {@link #DEFAULT_VALUE}
   */
  public MutableBigDecimal ()
  {
    this (DEFAULT_VALUE);
  }

  public MutableBigDecimal (final long nValue)
  {
    this (MathHelper.toBigDecimal (nValue));
  }

  public MutableBigDecimal (final double dValue)
  {
    this (MathHelper.toBigDecimal (dValue));
  }

  public MutableBigDecimal (@Nonnull final MutableBigDecimal aOther)
  {
    this (aOther.m_aValue);
  }

  public MutableBigDecimal (@Nonnull final BigDecimal aValue)
  {
    m_aValue = ValueEnforcer.notNull (aValue, "Value");
  }

  @Override
  @Nonnull
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
  @Nonnull
  public BigDecimal inc ()
  {
    return inc (BigDecimal.ONE);
  }

  @Nonnull
  public BigDecimal inc (final long nDelta)
  {
    return inc (MathHelper.toBigDecimal (nDelta));
  }

  @Nonnull
  public BigDecimal inc (final double dDelta)
  {
    return inc (MathHelper.toBigDecimal (dDelta));
  }

  @Nonnull
  public BigDecimal inc (@Nonnull final MutableBigDecimal aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (aDelta.m_aValue);
  }

  @Nonnull
  public BigDecimal inc (@Nonnull final BigDecimal aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    m_aValue = m_aValue.add (aDelta);
    onAfterChange ();
    return m_aValue;
  }

  @Nonnull
  public BigDecimal dec ()
  {
    return inc (CGlobal.BIGDEC_MINUS_ONE);
  }

  @Nonnull
  public BigDecimal dec (final long nDelta)
  {
    return inc (MathHelper.toBigDecimal (-nDelta));
  }

  @Nonnull
  public BigDecimal dec (final double dDelta)
  {
    return inc (MathHelper.toBigDecimal (-dDelta));
  }

  @Nonnull
  public BigDecimal dec (@Nonnull final BigDecimal aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (aDelta.negate ());
  }

  @Nonnull
  public BigDecimal dec (@Nonnull final MutableBigDecimal aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (aDelta.m_aValue.negate ());
  }

  @Nonnull
  public BigDecimal divide (final long nDivisor,
                            @Nonnegative final int nScale,
                            @Nonnull final RoundingMode eRoundingMode)
  {
    return divide (MathHelper.toBigDecimal (nDivisor), nScale, eRoundingMode);
  }

  @Nonnull
  public BigDecimal divide (final double dDivisor,
                            @Nonnegative final int nScale,
                            @Nonnull final RoundingMode eRoundingMode)
  {
    return divide (MathHelper.toBigDecimal (dDivisor), nScale, eRoundingMode);
  }

  @Nonnull
  public BigDecimal divide (@Nonnull final MutableBigDecimal aDivisor,
                            @Nonnegative final int nScale,
                            @Nonnull final RoundingMode eRoundingMode)
  {
    ValueEnforcer.notNull (aDivisor, "Divisor");
    return divide (aDivisor.m_aValue, nScale, eRoundingMode);
  }

  @Nonnull
  public BigDecimal divide (@Nonnull final BigDecimal aDivisor,
                            @Nonnegative final int nScale,
                            @Nonnull final RoundingMode eRoundingMode)
  {
    ValueEnforcer.notNull (aDivisor, "Divisor");
    m_aValue = m_aValue.divide (aDivisor, nScale, eRoundingMode);
    onAfterChange ();
    return m_aValue;
  }

  @Nonnull
  public BigDecimal multiply (final long nMultiplicand)
  {
    return multiply (MathHelper.toBigDecimal (nMultiplicand));
  }

  @Nonnull
  public BigDecimal multiply (final double dMultiplicand)
  {
    return multiply (MathHelper.toBigDecimal (dMultiplicand));
  }

  @Nonnull
  public BigDecimal multiply (@Nonnull final MutableBigDecimal aMultiplicand)
  {
    ValueEnforcer.notNull (aMultiplicand, "Multiplicand");
    return multiply (aMultiplicand.m_aValue);
  }

  @Nonnull
  public BigDecimal multiply (@Nonnull final BigDecimal aMultiplicand)
  {
    ValueEnforcer.notNull (aMultiplicand, "Multiplicand");
    m_aValue = m_aValue.multiply (aMultiplicand);
    onAfterChange ();
    return m_aValue;
  }

  @Nonnull
  public EChange set (final long nDelta)
  {
    return set (MathHelper.toBigDecimal (nDelta));
  }

  @Nonnull
  public EChange set (final double dDelta)
  {
    return set (MathHelper.toBigDecimal (dDelta));
  }

  @Nonnull
  public EChange set (@Nonnull final MutableBigDecimal aValue)
  {
    ValueEnforcer.notNull (aValue, "Value");
    return set (aValue.m_aValue);
  }

  @Nonnull
  public EChange set (@Nonnull final BigDecimal aValue)
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
    return MathHelper.isEqualToZero (m_aValue);
  }

  public boolean isSmaller0 ()
  {
    return MathHelper.isLowerThanZero (m_aValue);
  }

  public boolean isSmallerOrEqual0 ()
  {
    return MathHelper.isLowerOrEqualThanZero (m_aValue);
  }

  public boolean isGreater0 ()
  {
    return MathHelper.isGreaterThanZero (m_aValue);
  }

  public boolean isGreaterOrEqual0 ()
  {
    return MathHelper.isGreaterOrEqualThanZero (m_aValue);
  }

  public int compareTo (@Nonnull final MutableBigDecimal rhs)
  {
    return m_aValue.compareTo (rhs.m_aValue);
  }

  @Nonnull
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
    return EqualsHelper.equals (m_aValue, rhs.m_aValue);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aValue).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("value", m_aValue).toString ();
  }
}

/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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

import java.math.BigInteger;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.equals.EqualsUtils;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.math.MathHelper;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

/**
 * Object wrapper around a {@link BigInteger} so that it can be passed a final
 * object but is mutable.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class MutableBigInteger extends Number implements IMutableNumeric <MutableBigInteger>
{
  /** The default value if the default constructor is used. */
  public static final BigInteger DEFAULT_VALUE = BigInteger.ZERO;

  private BigInteger m_aValue;

  /**
   * Initialize with default value {@link #DEFAULT_VALUE}
   */
  public MutableBigInteger ()
  {
    this (DEFAULT_VALUE);
  }

  public MutableBigInteger (@Nonnull final BigInteger aValue)
  {
    m_aValue = ValueEnforcer.notNull (aValue, "Value");
  }

  @Nonnull
  public BigInteger getAsBigInteger ()
  {
    return m_aValue;
  }

  @Override
  public double doubleValue ()
  {
    return m_aValue.doubleValue ();
  }

  @Nonnull
  public Double getAsDouble ()
  {
    return Double.valueOf (doubleValue ());
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
  public BigInteger inc ()
  {
    return inc (BigInteger.ONE);
  }

  @Nonnull
  public BigInteger inc (@Nonnull final BigInteger aDelta)
  {
    m_aValue = m_aValue.add (aDelta);
    return m_aValue;
  }

  @Nonnull
  public BigInteger dec ()
  {
    return inc (CGlobal.BIGINT_MINUS_ONE);
  }

  @Nonnull
  public BigInteger dec (@Nonnull final BigInteger aDelta)
  {
    return inc (aDelta.negate ());
  }

  @Nonnull
  public EChange set (@Nonnull final BigInteger aValue)
  {
    if (EqualsUtils.equals (aValue, m_aValue))
      return EChange.UNCHANGED;
    m_aValue = aValue;
    return EChange.CHANGED;
  }

  public boolean is0 ()
  {
    return MathHelper.isEqualToZero (m_aValue);
  }

  public boolean isNot0 ()
  {
    return MathHelper.isNotEqualToZero (m_aValue);
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

  public int compareTo (@Nonnull final MutableBigInteger rhs)
  {
    return m_aValue.compareTo (rhs.m_aValue);
  }

  @Nonnull
  public MutableBigInteger getClone ()
  {
    return new MutableBigInteger (m_aValue);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MutableBigInteger rhs = (MutableBigInteger) o;
    return EqualsUtils.equals (m_aValue, rhs.m_aValue);
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

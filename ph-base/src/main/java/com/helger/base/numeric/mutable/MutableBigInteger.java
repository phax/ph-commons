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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.CGlobal;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.numeric.BigHelper;
import com.helger.base.state.EChange;
import com.helger.base.tostring.ToStringGenerator;

/**
 * Object wrapper around a {@link BigInteger} so that it can be passed a final object but is
 * mutable.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class MutableBigInteger extends AbstractMutableNumeric <MutableBigInteger>
{
  private BigInteger m_aValue;

  public MutableBigInteger (final long nValue)
  {
    this (BigInteger.valueOf (nValue));
  }

  public MutableBigInteger (@NonNull final MutableBigInteger aOther)
  {
    this (aOther.m_aValue);
  }

  public MutableBigInteger (@NonNull final BigInteger aValue)
  {
    m_aValue = ValueEnforcer.notNull (aValue, "Value");
  }

  @Override
  @NonNull
  public BigDecimal getAsBigDecimal ()
  {
    return new BigDecimal (m_aValue);
  }

  @Override
  @NonNull
  public BigInteger getAsBigInteger ()
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
  public BigInteger inc ()
  {
    return inc (BigInteger.ONE);
  }

  @NonNull
  public BigInteger inc (final long nDelta)
  {
    return inc (BigInteger.valueOf (nDelta));
  }

  @NonNull
  public BigInteger inc (@NonNull final MutableBigInteger aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (aDelta.m_aValue);
  }

  @NonNull
  public BigInteger inc (@NonNull final BigInteger aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    m_aValue = m_aValue.add (aDelta);
    onAfterChange ();
    return m_aValue;
  }

  @NonNull
  public BigInteger dec ()
  {
    return inc (CGlobal.BIGINT_MINUS_ONE);
  }

  @NonNull
  public BigInteger dec (final long nDelta)
  {
    return inc (BigInteger.valueOf (-nDelta));
  }

  @NonNull
  public BigInteger dec (@NonNull final MutableBigInteger aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (aDelta.m_aValue.negate ());
  }

  @NonNull
  public BigInteger dec (@NonNull final BigInteger aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (aDelta.negate ());
  }

  @NonNull
  public BigInteger divide (final long nDivisor)
  {
    return divide (BigHelper.toBigInteger (nDivisor));
  }

  @NonNull
  public BigInteger divide (@NonNull final MutableBigInteger aDivisor)
  {
    ValueEnforcer.notNull (aDivisor, "Divisor");
    return divide (aDivisor.m_aValue);
  }

  @NonNull
  public BigInteger divide (@NonNull final BigInteger aDivisor)
  {
    ValueEnforcer.notNull (aDivisor, "Divisor");
    m_aValue = m_aValue.divide (aDivisor);
    onAfterChange ();
    return m_aValue;
  }

  @NonNull
  public BigInteger multiply (final long nMultiplicand)
  {
    return multiply (BigHelper.toBigInteger (nMultiplicand));
  }

  @NonNull
  public BigInteger multiply (@NonNull final MutableBigInteger aMultiplicand)
  {
    ValueEnforcer.notNull (aMultiplicand, "Multiplicand");
    return multiply (aMultiplicand.m_aValue);
  }

  @NonNull
  public BigInteger multiply (@NonNull final BigInteger aMultiplicand)
  {
    ValueEnforcer.notNull (aMultiplicand, "Multiplicand");
    m_aValue = m_aValue.multiply (aMultiplicand);
    onAfterChange ();
    return m_aValue;
  }

  @NonNull
  public EChange set (final long nDelta)
  {
    return set (BigInteger.valueOf (nDelta));
  }

  @NonNull
  public EChange set (@NonNull final MutableBigInteger aValue)
  {
    ValueEnforcer.notNull (aValue, "Value");
    return set (aValue.m_aValue);
  }

  @NonNull
  public EChange set (@NonNull final BigInteger aValue)
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
  public BigInteger getAndInc ()
  {
    final BigInteger ret = getAsBigInteger ();
    inc ();
    return ret;
  }

  @NonNull
  public BigInteger incAndGet ()
  {
    inc ();
    return getAsBigInteger ();
  }

  public int compareTo (@NonNull final MutableBigInteger rhs)
  {
    return m_aValue.compareTo (rhs.m_aValue);
  }

  @NonNull
  public MutableBigInteger getClone ()
  {
    return new MutableBigInteger (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MutableBigInteger rhs = (MutableBigInteger) o;
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
    return new ToStringGenerator (this).append ("value", m_aValue).getToString ();
  }
}

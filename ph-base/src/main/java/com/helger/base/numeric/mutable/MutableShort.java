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

  public int inc (final int nDelta)
  {
    m_nValue = (short) (m_nValue + nDelta);
    onAfterChange ();
    return m_nValue;
  }

  public int inc (@NonNull final Number aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (aDelta.shortValue ());
  }

  public int dec ()
  {
    return inc (-1);
  }

  public int dec (final int nDelta)
  {
    return inc (-nDelta);
  }

  public int dec (@NonNull final Number aDelta)
  {
    ValueEnforcer.notNull (aDelta, "Delta");
    return inc (-aDelta.shortValue ());
  }

  @NonNull
  public EChange set (final int nValue)
  {
    return set ((short) nValue);
  }

  @NonNull
  public EChange set (final short nValue)
  {
    if (m_nValue == nValue)
      return EChange.UNCHANGED;
    m_nValue = nValue;
    onAfterChange ();
    return EChange.CHANGED;
  }

  @NonNull
  public EChange set (@NonNull final Number aValue)
  {
    ValueEnforcer.notNull (aValue, "Value");
    return set (aValue.shortValue ());
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

  public short getAndInc ()
  {
    final short ret = shortValue ();
    inc ();
    return ret;
  }

  public short incAndGet ()
  {
    inc ();
    return shortValue ();
  }

  public int compareTo (@NonNull final MutableShort rhs)
  {
    return CompareHelper.compare (m_nValue, rhs.m_nValue);
  }

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

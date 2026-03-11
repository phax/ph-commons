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
 * Object wrapper around a char so that it can be passed a final object but is
 * mutable.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class MutableChar extends AbstractMutableInteger <MutableChar>
{
  private char m_cValue;

  /**
   * Initialize with a certain int value. If the value does not fit into a char,
   * the value is cut!
   *
   * @param cValue
   *        The value to be used.
   */
  public MutableChar (final int cValue)
  {
    this ((char) cValue);
  }

  /**
   * Initialize with a certain value.
   *
   * @param aValue
   *        The value to be used.
   */
  public MutableChar (@NonNull final Character aValue)
  {
    this (aValue.charValue ());
  }

  /**
   * Initialize with a certain value.
   *
   * @param cValue
   *        The value to be used.
   */
  public MutableChar (final char cValue)
  {
    m_cValue = cValue;
  }

  /**
   * Copy constructor.
   *
   * @param aOther
   *        The object to copy from. May not be <code>null</code>.
   */
  public MutableChar (@NonNull final MutableChar aOther)
  {
    this (aOther.m_cValue);
  }

  /**
   * @return The current char value.
   */
  public char charValue ()
  {
    return m_cValue;
  }

  @Override
  public float floatValue ()
  {
    return m_cValue;
  }

  @Override
  public double doubleValue ()
  {
    return m_cValue;
  }

  @Override
  public int intValue ()
  {
    return m_cValue;
  }

  @Override
  public long longValue ()
  {
    return m_cValue;
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
    m_cValue += nDelta;
    onAfterChange ();
    return m_cValue;
  }

  /**
   * Increment by the given delta and return the modified value.
   *
   * @param aMC
   *        The delta to add. May not be <code>null</code>.
   * @return The new value after incrementing.
   */
  public int inc (@NonNull final MutableChar aMC)
  {
    ValueEnforcer.notNull (aMC, "MC");
    return inc (aMC.m_cValue);
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
   * @param aMC
   *        The delta to subtract. May not be <code>null</code>.
   * @return The new value after decrementing.
   */
  public int dec (@NonNull final MutableChar aMC)
  {
    ValueEnforcer.notNull (aMC, "MC");
    return inc (-aMC.m_cValue);
  }

  /**
   * Set a new value.
   *
   * @param cValue
   *        The new value to set. If the value does not fit into a char, it is cut.
   * @return {@link EChange#CHANGED} if the value was changed.
   */
  @NonNull
  public EChange set (final int cValue)
  {
    return set ((char) cValue);
  }

  /**
   * Set a new value.
   *
   * @param aMC
   *        The new value to set. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if the value was changed.
   */
  @NonNull
  public EChange set (@NonNull final MutableChar aMC)
  {
    ValueEnforcer.notNull (aMC, "MC");
    return set (aMC.m_cValue);
  }

  /**
   * Set a new value.
   *
   * @param cValue
   *        The new value to set.
   * @return {@link EChange#CHANGED} if the value was changed.
   */
  @NonNull
  public EChange set (final char cValue)
  {
    if (m_cValue == cValue)
      return EChange.UNCHANGED;
    m_cValue = cValue;
    onAfterChange ();
    return EChange.CHANGED;
  }

  /** {@inheritDoc} */
  public boolean is0 ()
  {
    return m_cValue == 0;
  }

  /** {@inheritDoc} */
  public boolean isLT0 ()
  {
    // char is always >= 0
    return false;
  }

  /** {@inheritDoc} */
  public boolean isLE0 ()
  {
    return m_cValue <= 0;
  }

  /** {@inheritDoc} */
  public boolean isGT0 ()
  {
    return m_cValue > 0;
  }

  /** {@inheritDoc} */
  public boolean isGE0 ()
  {
    // char is always >= 0
    return true;
  }

  /** {@inheritDoc} */
  public boolean isEven ()
  {
    return (m_cValue % 2) == 0;
  }

  /**
   * Get the current value and then increment by 1.
   *
   * @return The value before incrementing.
   */
  public char getAndInc ()
  {
    final char ret = charValue ();
    inc ();
    return ret;
  }

  /**
   * Increment by 1 and then get the new value.
   *
   * @return The value after incrementing.
   */
  public char incAndGet ()
  {
    inc ();
    return charValue ();
  }

  /** {@inheritDoc} */
  public int compareTo (@NonNull final MutableChar rhs)
  {
    return CompareHelper.compare (m_cValue, rhs.m_cValue);
  }

  /** {@inheritDoc} */
  @NonNull
  public MutableChar getClone ()
  {
    return new MutableChar (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MutableChar rhs = (MutableChar) o;
    return m_cValue == rhs.m_cValue;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_cValue).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("value", m_cValue).getToString ();
  }
}

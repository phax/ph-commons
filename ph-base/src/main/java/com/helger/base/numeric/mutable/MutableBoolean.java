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

import java.util.function.Supplier;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.base.compare.CompareHelper;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.state.EChange;
import com.helger.base.tostring.ToStringGenerator;

/**
 * Object wrapper around a boolean so that it can be passed a final object but
 * is mutable.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class MutableBoolean implements IMutableObject <MutableBoolean>
{
  private boolean m_bValue;

  /**
   * Initialize with a certain value.
   *
   * @param bValue
   *        The value to be used.
   */
  public MutableBoolean (final boolean bValue)
  {
    m_bValue = bValue;
  }

  /**
   * Initialize with a certain value.
   *
   * @param aValue
   *        The value to be used. May not be <code>null</code>.
   */
  public MutableBoolean (@NonNull final Boolean aValue)
  {
    this (aValue.booleanValue ());
  }

  /**
   * Copy constructor.
   *
   * @param aValue
   *        The object to copy from. May not be <code>null</code>.
   */
  public MutableBoolean (@NonNull final MutableBoolean aValue)
  {
    this (aValue.m_bValue);
  }

  /**
   * This method is invoked after a value changed.
   */
  @OverrideOnDemand
  protected void onAfterChange ()
  {}

  /**
   * @return The current boolean value.
   */
  public boolean booleanValue ()
  {
    return m_bValue;
  }

  /**
   * @return The current value as a {@link Boolean} object. Never <code>null</code>.
   */
  @NonNull
  public Boolean getAsBoolean ()
  {
    return Boolean.valueOf (m_bValue);
  }

  /**
   * Set a new value.
   *
   * @param aValue
   *        The new value to set. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if the value was changed.
   */
  @NonNull
  public EChange set (@NonNull final Boolean aValue)
  {
    ValueEnforcer.notNull (aValue, "Value");
    return set (aValue.booleanValue ());
  }

  /**
   * Set a new value.
   *
   * @param aValue
   *        The new value to set. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if the value was changed.
   */
  @NonNull
  public EChange set (@NonNull final MutableBoolean aValue)
  {
    ValueEnforcer.notNull (aValue, "Value");
    return set (aValue.m_bValue);
  }

  /**
   * Set a new value.
   *
   * @param bValue
   *        The new value to set.
   * @return {@link EChange#CHANGED} if the value was changed.
   */
  @NonNull
  public EChange set (final boolean bValue)
  {
    if (m_bValue == bValue)
      return EChange.UNCHANGED;
    m_bValue = bValue;
    onAfterChange ();
    return EChange.CHANGED;
  }

  /**
   * If the current value is <code>true</code>, get a value from the supplier; otherwise return
   * <code>null</code>.
   *
   * @param <T>
   *        The return type.
   * @param aSupplier
   *        The supplier to use. May not be <code>null</code>.
   * @return The supplied value or <code>null</code>.
   */
  @Nullable
  public <T> T getIf (@NonNull final Supplier <? extends T> aSupplier)
  {
    return getIf (aSupplier, null);
  }

  /**
   * If the current value is <code>true</code>, get a value from the supplier; otherwise return the
   * default value.
   *
   * @param <T>
   *        The return type.
   * @param aSupplier
   *        The supplier to use. May not be <code>null</code>.
   * @param aDefault
   *        The default value to return if the current value is <code>false</code>. May be
   *        <code>null</code>.
   * @return The supplied value or the default value.
   */
  @Nullable
  public <T> T getIf (@NonNull final Supplier <? extends T> aSupplier, @Nullable final T aDefault)
  {
    if (m_bValue)
      return aSupplier.get ();
    return aDefault;
  }

  /** {@inheritDoc} */
  public int compareTo (@NonNull final MutableBoolean rhs)
  {
    return CompareHelper.compareFalseBeforeTrue (m_bValue, rhs.m_bValue);
  }

  /** {@inheritDoc} */
  @NonNull
  public MutableBoolean getClone ()
  {
    return new MutableBoolean (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MutableBoolean rhs = (MutableBoolean) o;
    return m_bValue == rhs.m_bValue;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_bValue).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("value", m_bValue).getToString ();
  }
}

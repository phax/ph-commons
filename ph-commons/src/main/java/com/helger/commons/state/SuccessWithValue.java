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
package com.helger.commons.state;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.wrapper.IWrapper;

/**
 * Wraps a success indicator and an arbitrary value.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The data type that is wrapped together with the success indicator
 */
@Immutable
public class SuccessWithValue <DATATYPE> implements ISuccessIndicator, IWrapper <DATATYPE>
{
  private final ESuccess m_eSuccess;
  private final DATATYPE m_aObj;

  /**
   * Constructor
   *
   * @param aSuccessIndicator
   *        The success indicator. May not be <code>null</code>.
   */
  public SuccessWithValue (@Nonnull final ISuccessIndicator aSuccessIndicator)
  {
    this (aSuccessIndicator, null);
  }

  /**
   * Constructor
   *
   * @param aSuccessIndicator
   *        The success indicator. May not be <code>null</code>.
   * @param aObj
   *        The assigned value. May be <code>null</code>.
   */
  public SuccessWithValue (@Nonnull final ISuccessIndicator aSuccessIndicator, @Nullable final DATATYPE aObj)
  {
    ValueEnforcer.notNull (aSuccessIndicator, "SuccessIndicator");

    // Wrap in ESuccess so that equals works for sure
    m_eSuccess = ESuccess.valueOf (aSuccessIndicator);
    m_aObj = aObj;
  }

  public boolean isSuccess ()
  {
    return m_eSuccess.isSuccess ();
  }

  @Nullable
  public DATATYPE get ()
  {
    return m_aObj;
  }

  /**
   * Get the store value if this is a success. Otherwise the passed failure
   * value is returned.
   *
   * @param aFailureValue
   *        The failure value to be used. May be <code>null</code>.
   * @return Either the stored value or the failure value. May be
   *         <code>null</code>.
   */
  @Nullable
  public DATATYPE getIfSuccess (@Nullable final DATATYPE aFailureValue)
  {
    return m_eSuccess.isSuccess () ? m_aObj : aFailureValue;
  }

  /**
   * Get the store value if this is a success. Otherwise <code>null</code> is
   * returned.
   *
   * @return Either the stored value or <code>null</code>.
   */
  @Nullable
  public DATATYPE getIfSuccessOrNull ()
  {
    return getIfSuccess (null);
  }

  /**
   * Get the store value if this is a failure. Otherwise the passed success
   * value is returned.
   *
   * @param aSuccessValue
   *        The failure value to be used. May be <code>null</code>.
   * @return Either the stored value or the failure value. May be
   *         <code>null</code>.
   */
  @Nullable
  public DATATYPE getIfFailure (@Nullable final DATATYPE aSuccessValue)
  {
    return m_eSuccess.isFailure () ? m_aObj : aSuccessValue;
  }

  /**
   * Get the store value if this is a failure. Otherwise <code>null</code> is
   * returned.
   *
   * @return Either the stored value or <code>null</code>.
   */
  @Nullable
  public DATATYPE getIfFailureOrNull ()
  {
    return getIfFailure (null);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final SuccessWithValue <?> rhs = (SuccessWithValue <?>) o;
    return m_eSuccess.equals (rhs.m_eSuccess) && EqualsHelper.equals (m_aObj, rhs.m_aObj);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_eSuccess).append (m_aObj).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("success", m_eSuccess).append ("obj", m_aObj).toString ();
  }

  /**
   * Create a new object with the given value.
   *
   * @param <DATATYPE>
   *        The data type that is wrapped together with the success indicator
   * @param aSuccessIndicator
   *        The success indicator. May not be <code>null</code>.
   * @param aValue
   *        The value to be used. May be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static <DATATYPE> SuccessWithValue <DATATYPE> create (@Nonnull final ISuccessIndicator aSuccessIndicator,
                                                               @Nullable final DATATYPE aValue)
  {
    return new SuccessWithValue <DATATYPE> (aSuccessIndicator, aValue);
  }

  /**
   * Create a new success object with the given value.
   *
   * @param <DATATYPE>
   *        The data type that is wrapped together with the success indicator
   * @param aValue
   *        The value to be used. May be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static <DATATYPE> SuccessWithValue <DATATYPE> createSuccess (@Nullable final DATATYPE aValue)
  {
    return new SuccessWithValue <DATATYPE> (ESuccess.SUCCESS, aValue);
  }

  /**
   * Create a new failure object with the given value.
   *
   * @param <DATATYPE>
   *        The data type that is wrapped together with the success indicator
   * @param aValue
   *        The value to be used. May be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static <DATATYPE> SuccessWithValue <DATATYPE> createFailure (@Nullable final DATATYPE aValue)
  {
    return new SuccessWithValue <DATATYPE> (ESuccess.FAILURE, aValue);
  }
}

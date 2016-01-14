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
 * Wraps a change indicator and an arbitrary value.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The data type that is wrapped together with the change indicator
 */
@Immutable
public class ChangeWithValue <DATATYPE> implements IChangeIndicator, IWrapper <DATATYPE>
{
  private final EChange m_eChange;
  private final DATATYPE m_aObj;

  /**
   * Constructor
   *
   * @param aChangeIndicator
   *        The change indicator. May not be <code>null</code>.
   * @param aObj
   *        The assigned value. May be <code>null</code>.
   */
  public ChangeWithValue (@Nonnull final IChangeIndicator aChangeIndicator, @Nullable final DATATYPE aObj)
  {
    ValueEnforcer.notNull (aChangeIndicator, "ChangeIndicator");

    // Wrap in EChange so that equals works for sure
    m_eChange = EChange.valueOf (aChangeIndicator);
    m_aObj = aObj;
  }

  public boolean isChanged ()
  {
    return m_eChange.isChanged ();
  }

  @Nullable
  public DATATYPE get ()
  {
    return m_aObj;
  }

  /**
   * Get the store value if this is a change. Otherwise the passed unchanged
   * value is returned.
   *
   * @param aUnchangedValue
   *        The unchanged value to be used. May be <code>null</code>.
   * @return Either the stored value or the unchanged value. May be
   *         <code>null</code>.
   */
  @Nullable
  public DATATYPE getIfChanged (@Nullable final DATATYPE aUnchangedValue)
  {
    return m_eChange.isChanged () ? m_aObj : aUnchangedValue;
  }

  /**
   * Get the store value if this is a changed. Otherwise <code>null</code> is
   * returned.
   *
   * @return Either the stored value or <code>null</code>.
   */
  @Nullable
  public DATATYPE getIfChangedOrNull ()
  {
    return getIfChanged (null);
  }

  /**
   * Get the store value if this is unchanged. Otherwise the passed changed
   * value is returned.
   *
   * @param aChangedValue
   *        The changed value to be used. May be <code>null</code>.
   * @return Either the stored value or the changed value. May be
   *         <code>null</code>.
   */
  @Nullable
  public DATATYPE getIfUnchanged (@Nullable final DATATYPE aChangedValue)
  {
    return m_eChange.isUnchanged () ? m_aObj : aChangedValue;
  }

  /**
   * Get the store value if this is unchanged. Otherwise <code>null</code> is
   * returned.
   *
   * @return Either the stored value or <code>null</code>.
   */
  @Nullable
  public DATATYPE getIfUnchangedOrNull ()
  {
    return getIfUnchanged (null);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ChangeWithValue <?> rhs = (ChangeWithValue <?>) o;
    return m_eChange.equals (rhs.m_eChange) && EqualsHelper.equals (m_aObj, rhs.m_aObj);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_eChange).append (m_aObj).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("change", m_eChange).append ("obj", m_aObj).toString ();
  }

  /**
   * Create a new changed object with the given value.
   *
   * @param <DATATYPE>
   *        The data type that is wrapped together with the change indicator
   * @param aValue
   *        The value to be used. May be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static <DATATYPE> ChangeWithValue <DATATYPE> createChanged (@Nullable final DATATYPE aValue)
  {
    return new ChangeWithValue <DATATYPE> (EChange.CHANGED, aValue);
  }

  /**
   * Create a new unchanged object with the given value.
   *
   * @param <DATATYPE>
   *        The data type that is wrapped together with the change indicator
   * @param aValue
   *        The value to be used. May be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static <DATATYPE> ChangeWithValue <DATATYPE> createUnchanged (@Nullable final DATATYPE aValue)
  {
    return new ChangeWithValue <DATATYPE> (EChange.UNCHANGED, aValue);
  }
}

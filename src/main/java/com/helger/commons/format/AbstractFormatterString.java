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
package com.helger.commons.format;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.typeconvert.TypeConverter;

/**
 * Base implementation class of the {@link IFormatter} interface that provides
 * the common functionality.
 *
 * @author Philip Helger
 */
public abstract class AbstractFormatterString implements IFormatter
{
  private final IFormatter m_aPrevFormatter;

  /**
   * Default constructor
   */
  public AbstractFormatterString ()
  {
    this (null);
  }

  /**
   * @param aPrevFormatter
   *        Optional previous formatter to be invoked, before this formatter is
   *        invoked. May be <code>null</code>.
   */
  public AbstractFormatterString (@Nullable final IFormatter aPrevFormatter)
  {
    m_aPrevFormatter = aPrevFormatter;
  }

  /**
   * @return The previous formatter to be used before this formatter. May be
   *         <code>null</code>.
   */
  @Nullable
  public final IFormatter getPreviousFormatter ()
  {
    return m_aPrevFormatter;
  }

  /**
   * Convert the source value to a string by using the {@link TypeConverter}.
   *
   * @param aValue
   *        The value to be converted to a string
   * @return The string representation of the object and never <code>null</code>
   *         .
   */
  @OverrideOnDemand
  @Nonnull
  protected String getValueAsString (@Nullable final Object aValue)
  {
    final String sValue = TypeConverter.convertIfNecessary (aValue, String.class);
    return sValue != null ? sValue : "";
  }

  /**
   * Convert the passed value from Object to formatted String. Use
   * {@link #getValueAsString(Object)} in implementations of this method to do
   * the base conversion from Object to String.
   *
   * @param aValue
   *        The source object. May be <code>null</code>.
   * @return The formatted string value.
   */
  @Nullable
  protected abstract String getFormattedValueAsString (@Nullable Object aValue);

  @Nullable
  public final String getFormattedValue (@Nullable final Object aValue)
  {
    // Invoked any previous formatter before formatting with this object
    final Object aBase = m_aPrevFormatter == null ? aValue : m_aPrevFormatter.getFormattedValue (aValue);

    // Main formatting
    return getFormattedValueAsString (aBase);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).appendIfNotNull ("prevFormatter", m_aPrevFormatter).toString ();
  }
}

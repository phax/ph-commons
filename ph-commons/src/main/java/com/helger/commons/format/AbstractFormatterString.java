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
public abstract class AbstractFormatterString implements IFormatter <Object>
{
  /**
   * Default constructor
   */
  public AbstractFormatterString ()
  {}

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

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).toString ();
  }
}

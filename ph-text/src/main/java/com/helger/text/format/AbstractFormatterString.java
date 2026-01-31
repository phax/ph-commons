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
package com.helger.text.format;

import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.OverrideOnDemand;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.typeconvert.impl.TypeConverter;

/**
 * Base implementation class of the {@link Function} interface that provides the
 * common functionality.
 *
 * @author Philip Helger
 */
public abstract class AbstractFormatterString implements Function <Object, String>
{
  /**
   * Default constructor
   */
  protected AbstractFormatterString ()
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
  @NonNull
  protected String getValueAsString (@Nullable final Object aValue)
  {
    final String sValue = TypeConverter.convert (aValue, String.class);
    return sValue != null ? sValue : "";
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).getToString ();
  }
}

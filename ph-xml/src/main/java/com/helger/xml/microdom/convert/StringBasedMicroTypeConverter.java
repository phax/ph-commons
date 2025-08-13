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
package com.helger.xml.microdom.convert;

import com.helger.annotation.Nonempty;
import com.helger.commons.typeconvert.TypeConverter;
import com.helger.commons.valueenforcer.ValueEnforcer;
import com.helger.xml.microdom.IMicroElement;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * An implementation if {@link IMicroTypeConverter} that uses a regular type
 * converter conversion (see {@link TypeConverter}) from and to string for
 * conversion.
 *
 * @author Philip Helger
 * @param <T>
 *        Effective data type
 */
public final class StringBasedMicroTypeConverter <T> implements IMicroTypeConverter <T>
{
  private final Class <T> m_aNativeClass;

  public StringBasedMicroTypeConverter (@Nonnull final Class <T> aNativeClass)
  {
    ValueEnforcer.notNull (aNativeClass, "NativeClass");

    m_aNativeClass = aNativeClass;
  }

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final T aObject,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull @Nonempty final String sTagName)
  {
    // Convert object to string
    final String sValue = TypeConverter.convert (aObject, String.class);

    // Convert string to micro element
    return StringMicroTypeConverter.getInstance ().convertToMicroElement (sValue, sNamespaceURI, sTagName);
  }

  @Nonnull
  public T convertToNative (@Nonnull final IMicroElement aElement)
  {
    // Convert micro element to string
    final String sValue = StringMicroTypeConverter.getInstance ().convertToNative (aElement);

    // Convert string to native type
    return TypeConverter.convert (sValue, m_aNativeClass);
  }
}

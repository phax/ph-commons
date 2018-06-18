/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.typeconvert.TypeConverterException;
import com.helger.commons.typeconvert.TypeConverterException.EReason;
import com.helger.xml.microdom.IMicroElement;

/**
 * A utility class for converting objects from and to {@link IMicroElement}.<br>
 * The functionality is a special case of the
 * {@link com.helger.commons.typeconvert.TypeConverter} as we need a parameter
 * for conversion in this case.<br>
 * All converters are registered in the {@link MicroTypeConverterRegistry}.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class MicroTypeConverter
{
  @PresentForCodeCoverage
  private static final MicroTypeConverter s_aInstance = new MicroTypeConverter ();

  private MicroTypeConverter ()
  {}

  @Nullable
  public static <T> IMicroElement convertToMicroElement (@Nullable final T aObject,
                                                         @Nonnull @Nonempty final String sTagName)
  {
    // Use a null namespace
    return convertToMicroElement (aObject, null, sTagName);
  }

  @Nullable
  public static <T> IMicroElement convertToMicroElement (@Nullable final T aObject,
                                                         @Nullable final String sNamespaceURI,
                                                         @Nonnull @Nonempty final String sTagName)
  {
    ValueEnforcer.notEmpty (sTagName, "TagName");

    if (aObject == null)
      return null;

    // Lookup converter
    final Class <T> aSrcClass = GenericReflection.uncheckedCast (aObject.getClass ());
    final IMicroTypeConverter <T> aConverter = MicroTypeConverterRegistry.getInstance ()
                                                                         .getConverterToMicroElement (aSrcClass);
    if (aConverter == null)
      throw new TypeConverterException (aSrcClass, IMicroElement.class, EReason.NO_CONVERTER_FOUND);

    // Perform conversion
    final IMicroElement ret = aConverter.convertToMicroElement (aObject, sNamespaceURI, sTagName);
    if (ret == null)
      throw new TypeConverterException (aSrcClass, IMicroElement.class, EReason.CONVERSION_FAILED);
    return ret;
  }

  @Nullable
  public static <DSTTYPE> DSTTYPE convertToNative (@Nullable final IMicroElement aElement,
                                                   @Nonnull final Class <DSTTYPE> aDstClass)
  {
    return convertToNative (aElement, aDstClass, null);
  }

  @Nullable
  public static <DSTTYPE> DSTTYPE convertToNative (@Nullable final IMicroElement aElement,
                                                   @Nonnull final Class <DSTTYPE> aDstClass,
                                                   @Nullable final DSTTYPE aNullValue)
  {
    ValueEnforcer.notNull (aDstClass, "DestClass");

    if (aElement == null)
      return aNullValue;

    // Lookup converter
    final IMicroTypeConverter <DSTTYPE> aConverter = MicroTypeConverterRegistry.getInstance ()
                                                                               .getConverterToNative (aDstClass);
    if (aConverter == null)
      throw new TypeConverterException (IMicroElement.class, aDstClass, EReason.NO_CONVERTER_FOUND);

    // Perform conversion
    final DSTTYPE ret = aConverter.convertToNative (aElement);
    if (ret == null)
      throw new TypeConverterException (IMicroElement.class, aDstClass, EReason.CONVERSION_FAILED);
    return ret;
  }
}

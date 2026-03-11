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
package com.helger.xml.microdom.convert;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.reflection.GenericReflection;
import com.helger.typeconvert.TypeConverterException;
import com.helger.typeconvert.TypeConverterException.EReason;
import com.helger.xml.microdom.IMicroElement;

/**
 * A utility class for converting objects from and to {@link IMicroElement}.<br>
 * The functionality is a special case of the
 * {@link com.helger.typeconvert.impl.TypeConverter} as we need a parameter
 * for conversion in this case.<br>
 * All converters are registered in the {@link MicroTypeConverterRegistry}.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class MicroTypeConverter
{
  @PresentForCodeCoverage
  private static final MicroTypeConverter INSTANCE = new MicroTypeConverter ();

  private MicroTypeConverter ()
  {}

  /**
   * Convert an object to a micro element without namespace.
   *
   * @param <T>
   *        The source type
   * @param aObject
   *        The object to convert. May be <code>null</code>.
   * @param sTagName
   *        The tag name of the created element. May neither be <code>null</code> nor empty.
   * @return <code>null</code> if the input object is <code>null</code>.
   */
  @Nullable
  public static <T> IMicroElement convertToMicroElement (@Nullable final T aObject, @NonNull @Nonempty final String sTagName)
  {
    // Use a null namespace
    return convertToMicroElement (aObject, null, sTagName);
  }

  /**
   * Convert an object to a micro element with namespace.
   *
   * @param <T>
   *        The source type
   * @param aObject
   *        The object to convert. May be <code>null</code>.
   * @param sNamespaceURI
   *        The namespace URI to use. May be <code>null</code>.
   * @param sTagName
   *        The tag name of the created element. May neither be <code>null</code> nor empty.
   * @return <code>null</code> if the input object is <code>null</code>.
   */
  @Nullable
  public static <T> IMicroElement convertToMicroElement (@Nullable final T aObject,
                                                         @Nullable final String sNamespaceURI,
                                                         @NonNull @Nonempty final String sTagName)
  {
    ValueEnforcer.notEmpty (sTagName, "TagName");

    if (aObject == null)
      return null;

    // Lookup converter
    final Class <T> aSrcClass = GenericReflection.uncheckedCast (aObject.getClass ());
    final IMicroTypeConverter <T> aConverter = MicroTypeConverterRegistry.getInstance ().getConverterToMicroElement (aSrcClass);
    if (aConverter == null)
      throw new TypeConverterException (aSrcClass, IMicroElement.class, EReason.NO_CONVERTER_FOUND);

    // Perform conversion
    final IMicroElement ret = aConverter.convertToMicroElement (aObject, sNamespaceURI, sTagName);
    if (ret == null)
      throw new TypeConverterException (aSrcClass, IMicroElement.class, EReason.CONVERSION_FAILED);
    return ret;
  }

  /**
   * Convert a micro element to a native object.
   *
   * @param <DSTTYPE>
   *        The destination type
   * @param aElement
   *        The micro element to convert. May be <code>null</code>.
   * @param aDstClass
   *        The destination class. May not be <code>null</code>.
   * @return <code>null</code> if the input element is <code>null</code>.
   */
  @Nullable
  public static <DSTTYPE> DSTTYPE convertToNative (@Nullable final IMicroElement aElement, @NonNull final Class <DSTTYPE> aDstClass)
  {
    return convertToNative (aElement, aDstClass, null);
  }

  /**
   * Convert a micro element to a native object with a custom null value.
   *
   * @param <DSTTYPE>
   *        The destination type
   * @param aElement
   *        The micro element to convert. May be <code>null</code>.
   * @param aDstClass
   *        The destination class. May not be <code>null</code>.
   * @param aNullValue
   *        The value to return if the element is <code>null</code>. May be <code>null</code>.
   * @return The converted value or the null value if the input element is <code>null</code>.
   */
  @Nullable
  public static <DSTTYPE> DSTTYPE convertToNative (@Nullable final IMicroElement aElement,
                                                   @NonNull final Class <DSTTYPE> aDstClass,
                                                   @Nullable final DSTTYPE aNullValue)
  {
    ValueEnforcer.notNull (aDstClass, "DestClass");

    if (aElement == null)
      return aNullValue;

    // Lookup converter
    final IMicroTypeConverter <DSTTYPE> aConverter = MicroTypeConverterRegistry.getInstance ().getConverterToNative (aDstClass);
    if (aConverter == null)
      throw new TypeConverterException (IMicroElement.class, aDstClass, EReason.NO_CONVERTER_FOUND);

    // Perform conversion
    final DSTTYPE ret = aConverter.convertToNative (aElement);
    if (ret == null)
      throw new TypeConverterException (IMicroElement.class, aDstClass, EReason.CONVERSION_FAILED);
    return ret;
  }
}

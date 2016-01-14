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
package com.helger.commons.xml.serialize.read;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.callback.exception.IExceptionCallback;
import com.helger.commons.xml.EXMLParserFeature;
import com.helger.commons.xml.EXMLParserProperty;

/**
 * Base Read-only interface for DOM and SAX reader settings.
 *
 * @author Philip Helger
 */
public interface IBaseXMLReaderSettings
{
  /**
   * @return <code>true</code> if at least one parser property is defined
   */
  boolean hasAnyProperties ();

  /**
   * Get the value of the specified parser property
   *
   * @param eProperty
   *        The property to use. May be <code>null</code>.
   * @return <code>null</code> if no such property was found.
   */
  @Nullable
  Object getPropertyValue (@Nullable EXMLParserProperty eProperty);

  /**
   * @return A copy of all contained parser property values. Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  Map <EXMLParserProperty, Object> getAllPropertyValues ();

  /**
   * @return The locale to be used for error messages. By default it is
   *         <code>null</code> and therefore the XML parser will use the system
   *         default locale.
   */
  @Nullable
  Locale getLocale ();

  /**
   * @return <code>true</code> if at least one parser feature is defined,
   *         <code>false</code> if not
   */
  boolean hasAnyFeature ();

  /**
   * Get the value of the specified parser feature
   *
   * @param eFeature
   *        The feature to search. May be <code>null</code>.
   * @return <code>null</code> if this feature is undefined.
   */
  @Nullable
  Boolean getFeatureValue (@Nullable EXMLParserFeature eFeature);

  /**
   * @return A copy of all defined parser features at the associated values.
   */
  @Nonnull
  @ReturnsMutableCopy
  Map <EXMLParserFeature, Boolean> getAllFeatureValues ();

  /**
   * @return A special error handler to be used or <code>null</code> if no
   *         special error handler is needed.
   */
  @Nullable
  ErrorHandler getErrorHandler ();

  /**
   * @return The special entity resolver to be used. May be <code>null</code>.
   */
  @Nullable
  EntityResolver getEntityResolver ();

  /**
   * @return A special exception handler to be used. Never <code>null</code>.
   */
  @Nonnull
  IExceptionCallback <Throwable> getExceptionHandler ();

  /**
   * Check if the current settings require a separate DOM
   * {@link javax.xml.parsers.DocumentBuilderFactory}/SAX
   * {@link org.xml.sax.XMLReader} object or if a pooled default object can be
   * used.
   *
   * @return <code>true</code> if a separate parser object is required,
   *         <code>false</code> if not.
   */
  boolean requiresNewXMLParser ();
}

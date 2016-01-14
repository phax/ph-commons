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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;

/**
 * Read-only interface for DOM reader settings.
 *
 * @author Philip Helger
 */
public interface IDOMReaderSettings extends IBaseXMLReaderSettings
{
  /**
   * @return <code>true</code> if the parser should be namespace aware,
   *         <code>false</code> if not.
   */
  boolean isNamespaceAware ();

  /**
   * @return <code>true</code> if the parser should be validating,
   *         <code>false</code> if not.
   */
  boolean isValidating ();

  /**
   * @return <code>true</code> if the parser should be element content
   *         whitespace ignoring, <code>false</code> if not.
   */
  boolean isIgnoringElementContentWhitespace ();

  /**
   * @return <code>true</code> if the parser should expand entity references,
   *         <code>false</code> if not.
   */
  boolean isExpandEntityReferences ();

  /**
   * @return <code>true</code> if the parser should ignore comments,
   *         <code>false</code> if not.
   */
  boolean isIgnoringComments ();

  /**
   * @return <code>true</code> if the parser should read CDATA as text,
   *         <code>false</code> if not.
   */
  boolean isCoalescing ();

  /**
   * @return A special XML schema to be used or <code>null</code> if none should
   *         be used.
   */
  @Nullable
  Schema getSchema ();

  /**
   * @return <code>true</code> if the parser should be XInclude aware,
   *         <code>false</code> if not.
   */
  boolean isXIncludeAware ();

  /**
   * @return <code>true</code> if a new XML parser is explicitly required for
   *         this instance.
   */
  boolean isRequiresNewXMLParserExplicitly ();

  /**
   * Check if the current settings require a separate
   * {@link javax.xml.parsers.DocumentBuilderFactory} or if a pooled default
   * object can be used.
   *
   * @return <code>true</code> if a separate
   *         {@link javax.xml.parsers.DocumentBuilderFactory} is required,
   *         <code>false</code> if not.
   */
  boolean requiresNewXMLParser ();

  /**
   * Apply settings of this object onto the specified
   * {@link DocumentBuilderFactory} object.
   *
   * @param aDBF
   *        The {@link DocumentBuilderFactory} to apply the settings onto. May
   *        not be <code>null</code>.
   */
  void applyToDocumentBuilderFactory (@Nonnull final DocumentBuilderFactory aDBF);

  /**
   * Apply settings of this object onto the specified {@link DocumentBuilder}
   * object.
   *
   * @param aDB
   *        The {@link DocumentBuilder} to apply the settings onto. May not be
   *        <code>null</code>.
   */
  void applyToDocumentBuilder (@Nonnull final DocumentBuilder aDB);
}

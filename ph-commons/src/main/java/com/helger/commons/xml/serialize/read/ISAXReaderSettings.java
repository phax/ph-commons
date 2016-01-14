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

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

/**
 * Read-only interface for the {@link SAXReaderSettings}. Implementations of
 * this interface are meant to define settings that can be used with
 * org.xml.sax.XMLReader instances.
 *
 * @author Philip Helger
 */
public interface ISAXReaderSettings extends IBaseXMLReaderSettings
{
  /**
   * @return The special DTD handler to be used. May be <code>null</code>.
   */
  @Nullable
  DTDHandler getDTDHandler ();

  /**
   * @return The special content handler to be used. May be <code>null</code>.
   */
  @Nullable
  ContentHandler getContentHandler ();

  /**
   * @return The special lexical handler to be used. May be <code>null</code>.
   */
  @Nullable
  LexicalHandler getLexicalHandler ();

  /**
   * @return The special DTD declaration event handler to be used. May be
   *         <code>null</code>.
   */
  @Nullable
  DeclHandler getDeclarationHandler ();

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
   * Apply all settings of this object onto the specified XMLReader object
   *
   * @param aParser
   *        The XML reader to apply the settings onto. May not be
   *        <code>null</code>.
   */
  void applyToSAXReader (@Nonnull final org.xml.sax.XMLReader aParser);
}

/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.xml.serialize;

import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.NamespaceContext;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.xml.EXMLIncorrectCharacterHandling;
import com.helger.commons.xml.EXMLVersion;

/**
 * Interface for the settings to be used for serializing XML nodes.
 * 
 * @author Philip Helger
 */
public interface IXMLWriterSettings
{
  /**
   * @return Create XML or HTML? Default is <code>XML</code>.
   */
  @Nonnull
  EXMLSerializeFormat getFormat ();

  /**
   * @return The XML version to use. Default is <code>1.0</code>
   */
  @Nonnull
  EXMLVersion getXMLVersion ();

  /**
   * @return Write document type? Default is <code>true</code>.
   */
  @Nonnull
  EXMLSerializeDocType getSerializeDocType ();

  /**
   * @return Write comments? Default is <code>true</code>.
   */
  @Nonnull
  EXMLSerializeComments getSerializeComments ();

  /**
   * @return Indent code? Default is <code>indent and aligned</code>.
   */
  @Nonnull
  EXMLSerializeIndent getIndent ();

  /**
   * @return The incorrect character handling to be used. May not be
   *         <code>null</code>.
   */
  @Nonnull
  EXMLIncorrectCharacterHandling getIncorrectCharacterHandling ();

  /**
   * @return The charset name to use. May never be <code>null</code>.
   */
  @Nonnull
  String getCharset ();

  /**
   * @return The charset to use. May never be <code>null</code>.
   */
  @Nonnull
  Charset getCharsetObj ();

  /**
   * @return The namespace context to be used. May be <code>null</code> to
   *         dynamically create the namespace prefixes.
   */
  @Nullable
  NamespaceContext getNamespaceContext ();

  /**
   * @return <code>true</code> if attribute values should be enclosed in double
   *         quotes, <code>false</code> if single quotes should be used.
   */
  boolean isUseDoubleQuotesForAttributes ();

  /**
   * Determine if a self closed element (an element having no children) should
   * be emitted with a space at the end (<code>&lt;br /&gt;</code>) or not (
   * <code>&lt;br/&gt;</code>)
   * 
   * @return <code>true</code> if a space should be emitted, <code>false</code>
   *         if no space should be emitted.
   */
  boolean isSpaceOnSelfClosedElement ();

  /**
   * @return The newline string to be used. By default it is the platform
   *         specific newline string. Never <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  String getNewlineString ();

  /**
   * @return The string to be used for indentation. By default it is 2 spaces.
   */
  @Nonnull
  @Nonempty
  String getIndentationString ();

  /**
   * Determine if namespaces should be emitted or not.
   * 
   * @return <code>true</code> if namespaces should be emitted,
   *         <code>false</code> if all created elements should reside in the
   *         default namespace.
   */
  boolean isEmitNamespaces ();

  /**
   * Check if the flag for putting all namespace prefixes specified in the
   * namespace context should be put in the root document. This setting only has
   * an effect if a namespace context is defined and if it implements the
   * {@link com.helger.commons.xml.namespace.IIterableNamespaceContext}
   * interface!
   * 
   * @return <code>true</code> if it is enabled, <code>false</code> if not.
   */
  boolean isPutNamespaceContextPrefixesInRoot ();
}

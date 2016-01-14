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
package com.helger.commons.xml.serialize.write;

import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.xml.namespace.NamespaceContext;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.system.ENewLineMode;
import com.helger.commons.xml.EXMLVersion;

/**
 * Interface for the settings to be used for serializing XML nodes.
 *
 * @author Philip Helger
 */
public interface IXMLWriterSettings
{
  /**
   * @return The XML version to use. Default is <code>1.0</code>
   */
  @Nonnull
  EXMLVersion getXMLVersion ();

  /**
   * @return The XML serialize version to use. Default is <code>XML 1.0</code>
   */
  @Nonnull
  EXMLSerializeVersion getSerializeVersion ();

  /**
   * @return Write XML declaration? Default is <code>true</code>.
   */
  @Nonnull
  EXMLSerializeXMLDeclaration getSerializeXMLDeclaration ();

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
   * @return Indent and/or align code? Default is
   *         <code>indent and aligned</code>.
   */
  @Nonnull
  EXMLSerializeIndent getIndent ();

  /**
   * @return The dynamic (per-element) indentation determinator. This must be
   *         changed when e.g. serializing HTML. Never <code>null</code>.
   */
  @Nonnull
  IXMLIndentDeterminator getIndentDeterminator ();

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
   * @return The namespace context to be used. Never <code>null</code>.
   */
  @Nonnull
  NamespaceContext getNamespaceContext ();

  /**
   * @return <code>true</code> if attribute values should be enclosed in double
   *         quotes, <code>false</code> if single quotes should be used.
   */
  boolean isUseDoubleQuotesForAttributes ();

  /**
   * @return The dynamic (per-element) bracket mode determinator to use. This
   *         must be changed when e.g. serializing HTML. Never <code>null</code>
   *         .
   */
  @Nonnull
  IXMLBracketModeDeterminator getBracketModeDeterminator ();

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
   * @return The newline mode to be used. By default it is the platform specific
   *         new line mode. Never <code>null</code>.
   */
  @Nonnull
  ENewLineMode getNewLineMode ();

  /**
   * @return The string representing the new line mode. A shortcut for
   *         <code>getNewLineMode ().getText()</code>.
   */
  @Nonnull
  @Nonempty
  String getNewLineString ();

  /**
   * @return The string to be used for indentation of a single level. By default
   *         it is 2 spaces.
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

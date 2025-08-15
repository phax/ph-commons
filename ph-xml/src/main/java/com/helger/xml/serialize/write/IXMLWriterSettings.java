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
package com.helger.xml.serialize.write;

import java.nio.charset.Charset;

import javax.xml.namespace.NamespaceContext;

import com.helger.annotation.Nonempty;
import com.helger.base.system.ENewLineMode;
import com.helger.xml.EXMLVersion;

import jakarta.annotation.Nonnull;

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
   * @return <code>true</code> if a newline should be added after the XML
   *         declaration or not. Defaults to <code>true</code>.
   * @since 9.3.5
   */
  boolean isNewLineAfterXMLDeclaration ();

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
   * @return The charset to use. May never be <code>null</code>.
   */
  @Nonnull
  Charset getCharset ();

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
  default String getNewLineString ()
  {
    return getNewLineMode ().getText ();
  }

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
   * {@link com.helger.xml.namespace.IIterableNamespaceContext} interface!
   *
   * @return <code>true</code> if it is enabled, <code>false</code> if not.
   */
  boolean isPutNamespaceContextPrefixesInRoot ();

  /**
   * Check if all CDATA children should be emitted as normal text or not. This
   * is required for XML canonicalization.
   *
   * @return <code>true</code> if CDATA should be written as text,
   *         <code>false</code> to keep CDATA as it is.
   * @since 9.1.4
   */
  boolean isWriteCDATAAsText ();

  /**
   * Determine if attributes of elements and namespaces should be emitted in
   * alphabetical order or not. This is required for XML canonicalization.
   *
   * @return <code>true</code> to order attributes, <code>false</code> to keep
   *         the original order.
   * @since 9.1.4
   */
  boolean isOrderAttributesAndNamespaces ();
}

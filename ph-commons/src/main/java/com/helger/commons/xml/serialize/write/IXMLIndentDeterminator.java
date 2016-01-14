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

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;

/**
 * Interface used in writing XML to determine the real indentation and align (as
 * defined in {@link EXMLSerializeIndent}) that should be used for a single
 * element.
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public interface IXMLIndentDeterminator
{
  /**
   * Determine the outer indentation mode (before the start tag and after the
   * closing tag).
   *
   * @param sParentNamespaceURI
   *        Optional namespace URI of the parent element (if any). May be
   *        <code>null</code>.
   * @param sParentTagName
   *        Tag name of the parent element (if any). May be <code>null</code>.
   * @param sNamespaceURI
   *        Optional namespace URI. May be <code>null</code>.
   * @param sTagName
   *        Tag name.
   * @param aAttrs
   *        Optional set of attributes.
   * @param bHasChildren
   *        <code>true</code> if the current element has children
   * @param eDefaultIndent
   *        The default indentation mode provided by the
   *        {@link IXMLWriterSettings}. Never <code>null</code>.
   * @return The indentation mode to be used. May not be <code>null</code>.
   */
  @Nonnull
  EXMLSerializeIndent getIndentOuter (@Nullable String sParentNamespaceURI,
                                      @Nullable String sParentTagName,
                                      @Nullable String sNamespaceURI,
                                      @Nonnull String sTagName,
                                      @Nullable Map <QName, String> aAttrs,
                                      boolean bHasChildren,
                                      @Nonnull EXMLSerializeIndent eDefaultIndent);

  /**
   * Determine the outer indentation mode (after the start tag and before the
   * closing tag).
   *
   * @param sParentNamespaceURI
   *        Optional namespace URI of the parent element (if any). May be
   *        <code>null</code>.
   * @param sParentTagName
   *        Tag name of the parent element (if any). May be <code>null</code>.
   * @param sNamespaceURI
   *        Optional namespace URI. May be <code>null</code>.
   * @param sTagName
   *        Tag name.
   * @param aAttrs
   *        Optional set of attributes.
   * @param bHasChildren
   *        <code>true</code> if the current element has children
   * @param eDefaultIndent
   *        The default indentation mode provided by the
   *        {@link IXMLWriterSettings}. Never <code>null</code>.
   * @return The indentation mode to be used. May not be <code>null</code>.
   */
  @Nonnull
  EXMLSerializeIndent getIndentInner (@Nullable String sParentNamespaceURI,
                                      @Nullable String sParentTagName,
                                      @Nullable String sNamespaceURI,
                                      @Nonnull String sTagName,
                                      @Nullable Map <QName, String> aAttrs,
                                      boolean bHasChildren,
                                      @Nonnull EXMLSerializeIndent eDefaultIndent);
}

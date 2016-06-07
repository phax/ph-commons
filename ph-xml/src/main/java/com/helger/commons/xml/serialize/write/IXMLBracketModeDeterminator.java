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
 * Interface used in writing XML to determine which of the bracket mode as
 * outlined in {@link EXMLSerializeBracketMode} should be used for a single
 * element.
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public interface IXMLBracketModeDeterminator
{
  /**
   * Determine the bracket mode for an XML element.
   *
   * @param sNamespaceURI
   *        Optional namespace URI. May be <code>null</code>.
   * @param sTagName
   *        Tag name
   * @param aAttrs
   *        Optional set of attributes.
   * @param bHasChildren
   *        <code>true</code> if the current element has children
   * @return The bracket mode to be used. May not be <code>null</code>.
   */
  @Nonnull
  EXMLSerializeBracketMode getBracketMode (@Nullable String sNamespaceURI,
                                           @Nonnull String sTagName,
                                           @Nullable Map <QName, String> aAttrs,
                                           boolean bHasChildren);
}

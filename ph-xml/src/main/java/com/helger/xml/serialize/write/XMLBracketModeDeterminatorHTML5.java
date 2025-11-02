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

import java.util.Locale;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.ICommonsSet;

/**
 * Implementation of {@link IXMLBracketModeDeterminator} for HTML5. This is not
 * applicable for XHTML or HTML4.
 *
 * @author Philip Helger
 */
public class XMLBracketModeDeterminatorHTML5 implements IXMLBracketModeDeterminator
{
  // Source: http://www.w3.org/TR/html-markup/syntax.html#void-element
  // Added myself: frame
  private static final ICommonsSet <String> VOID_ELEMENTS = new CommonsHashSet <> ("AREA",
                                                                                   "BASE",
                                                                                   "BR",
                                                                                   "COL",
                                                                                   "COMMAND",
                                                                                   "EMBED",
                                                                                   "FRAME",
                                                                                   "HR",
                                                                                   "IMG",
                                                                                   "INPUT",
                                                                                   "KEYGEN",
                                                                                   "LINK",
                                                                                   "META",
                                                                                   "PARAM",
                                                                                   "SOURCE",
                                                                                   "TRACKE",
                                                                                   "WBR");

  private static boolean _isVoidElement (@NonNull final String sTagName)
  {
    return VOID_ELEMENTS.contains (sTagName.toUpperCase (Locale.US));
  }

  @NonNull
  public EXMLSerializeBracketMode getBracketMode (@Nullable final String sNamespaceURI,
                                                  @NonNull final String sTagName,
                                                  @Nullable final Map <QName, String> aAttrs,
                                                  final boolean bHasChildren)
  {
    // In HTML all tags are closed, if not explicitly marked as empty
    if (!bHasChildren && _isVoidElement (sTagName))
      return EXMLSerializeBracketMode.SELF_CLOSED;

    // A non-void element must have an end tag, unless ...
    return EXMLSerializeBracketMode.OPEN_CLOSE;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    return true;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).getToString ();
  }
}

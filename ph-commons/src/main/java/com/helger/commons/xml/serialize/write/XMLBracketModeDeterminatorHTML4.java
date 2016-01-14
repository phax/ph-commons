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

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Implementation of {@link IXMLBracketModeDeterminator} for HTML4. This is not
 * applicable for XHTML or HTML5.
 *
 * @author Philip Helger
 */
public class XMLBracketModeDeterminatorHTML4 implements IXMLBracketModeDeterminator
{
  private static final Set <String> VOID_ELEMENTS = CollectionHelper.newSet ("AREA",
                                                                             "BASE",
                                                                             "BASEFONT",
                                                                             "BR",
                                                                             "COL",
                                                                             "FRAME",
                                                                             "HR",
                                                                             "IMG",
                                                                             "INPUT",
                                                                             "ISINDEX",
                                                                             "LINK",
                                                                             "META",
                                                                             "PARAM");

  private static boolean _isVoidElement (@Nonnull final String sTagName)
  {
    return VOID_ELEMENTS.contains (sTagName.toUpperCase (Locale.US));
  }

  @Nonnull
  public EXMLSerializeBracketMode getBracketMode (@Nullable final String sNamespaceURI,
                                                  @Nonnull final String sTagName,
                                                  @Nullable final Map <QName, String> aAttrs,
                                                  final boolean bHasChildren)
  {
    // In HTML all tags are closed, if not explicitly marked as empty
    if (!bHasChildren && _isVoidElement (sTagName))
      return EXMLSerializeBracketMode.OPEN_ONLY;

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
    return new ToStringGenerator (this).toString ();
  }
}

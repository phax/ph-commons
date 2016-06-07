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

import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Implementation of {@link IXMLIndentDeterminator} for real XML. It always uses
 * the default indent and align mode.
 *
 * @author Philip Helger
 */
public class XMLIndentDeterminatorXML implements IXMLIndentDeterminator
{
  @Nonnull
  public EXMLSerializeIndent getIndentOuter (@Nullable final String sParentNamespaceURI,
                                             @Nullable final String sParentTagName,
                                             @Nullable final String sNamespaceURI,
                                             @Nonnull final String sTagName,
                                             @Nullable final Map <QName, String> aAttrs,
                                             final boolean bHasChildren,
                                             @Nonnull final EXMLSerializeIndent eDefaultIndent)
  {
    // Always use the default
    return eDefaultIndent;
  }

  @Nonnull
  public EXMLSerializeIndent getIndentInner (@Nullable final String sParentNamespaceURI,
                                             @Nullable final String sParentTagName,
                                             @Nullable final String sNamespaceURI,
                                             @Nonnull final String sTagName,
                                             @Nullable final Map <QName, String> aAttrs,
                                             final boolean bHasChildren,
                                             @Nonnull final EXMLSerializeIndent eDefaultIndent)
  {
    // Always use the default
    return eDefaultIndent;
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

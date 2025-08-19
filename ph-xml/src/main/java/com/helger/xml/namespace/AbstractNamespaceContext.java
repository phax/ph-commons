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
package com.helger.xml.namespace;

import java.util.Iterator;

import javax.xml.XMLConstants;

import com.helger.collection.base.EmptyIterator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Represents an abstract namespace context that does the predefined mapping as
 * stated in the Javadoc.
 *
 * @author Philip Helger
 */
public abstract class AbstractNamespaceContext implements IIterableNamespaceContext
{
  private static final ICommonsList <String> XML_NS = new CommonsArrayList <> (XMLConstants.XML_NS_PREFIX);
  private static final ICommonsList <String> XMLNS_ATTRIBUTE_NS = new CommonsArrayList <> (XMLConstants.XMLNS_ATTRIBUTE);

  @Nullable
  public abstract String getDefaultNamespaceURI ();

  @Nullable
  protected abstract Iterator <String> getCustomPrefixes (@Nonnull String sNamespaceURI);

  @Nonnull
  public final Iterator <String> getPrefixes (@Nonnull final String sNamespaceURI)
  {
    // According to JavaDoc
    if (sNamespaceURI == null)
      throw new IllegalArgumentException ();
    if (sNamespaceURI.equals (XMLConstants.XML_NS_URI))
      return XML_NS.iterator ();
    if (sNamespaceURI.equals (XMLConstants.XMLNS_ATTRIBUTE_NS_URI))
      return XMLNS_ATTRIBUTE_NS.iterator ();

    final Iterator <String> ret = getCustomPrefixes (sNamespaceURI);
    return ret != null ? ret : new EmptyIterator <> ();
  }

  @Nullable
  protected abstract String getCustomPrefix (@Nonnull String sNamespaceURI);

  @Nullable
  public final String getPrefix (@Nonnull final String sNamespaceURI)
  {
    // According to JavaDoc
    if (sNamespaceURI == null)
      throw new IllegalArgumentException ();

    // special "xml:" namespace handling
    if (sNamespaceURI.equals (XMLConstants.XML_NS_URI))
      return XMLConstants.XML_NS_PREFIX;
    // special "xmlns:" namespace handling
    if (sNamespaceURI.equals (XMLConstants.XMLNS_ATTRIBUTE_NS_URI))
      return XMLConstants.XMLNS_ATTRIBUTE;
    // special "" namespace handling
    if (sNamespaceURI.equals (getDefaultNamespaceURI ()))
      return XMLConstants.DEFAULT_NS_PREFIX;

    return getCustomPrefix (sNamespaceURI);
  }

  @Nullable
  protected abstract String getCustomNamespaceURI (@Nonnull String sPrefix);

  @Nonnull
  public final String getNamespaceURI (@Nonnull final String sPrefix)
  {
    // According to JavaDoc
    if (sPrefix == null)
      throw new IllegalArgumentException ("null prefix is not allowed!");

    // special "xml:" namespace handling
    if (sPrefix.equals (XMLConstants.XML_NS_PREFIX))
      return XMLConstants.XML_NS_URI;
    // special "xmlns:" namespace handling
    if (sPrefix.equals (XMLConstants.XMLNS_ATTRIBUTE))
      return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;

    // special "" namespace handling
    if (sPrefix.equals (XMLConstants.DEFAULT_NS_PREFIX))
    {
      final String sDefNSURI = getDefaultNamespaceURI ();
      return sDefNSURI != null ? sDefNSURI : XMLConstants.NULL_NS_URI;
    }

    final String ret = getCustomNamespaceURI (sPrefix);
    return ret != null ? ret : XMLConstants.NULL_NS_URI;
  }
}

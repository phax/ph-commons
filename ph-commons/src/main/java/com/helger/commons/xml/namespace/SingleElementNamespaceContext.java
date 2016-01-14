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
package com.helger.commons.xml.namespace;

import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.XMLConstants;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.iterate.SingleElementIterator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Represents a namespace context having exactly 1 item.
 *
 * @author Philip Helger
 */
@Immutable
public class SingleElementNamespaceContext extends AbstractNamespaceContext
{
  private final String m_sPrefix;
  private final String m_sNamespaceURI;

  /**
   * Create a namespace context with the default (empty) prefix
   *
   * @param sNamespaceURI
   *        The namespace URI to use. May neither be <code>null</code> nor
   *        empty.
   */
  public SingleElementNamespaceContext (@Nonnull @Nonempty final String sNamespaceURI)
  {
    this (XMLConstants.DEFAULT_NS_PREFIX, sNamespaceURI);
  }

  public SingleElementNamespaceContext (@Nonnull final String sPrefix, @Nonnull @Nonempty final String sNamespaceURI)
  {
    m_sPrefix = ValueEnforcer.notNull (sPrefix, "Prefix");
    m_sNamespaceURI = ValueEnforcer.notEmpty (sNamespaceURI, "NamespaceURI");
  }

  @Override
  @Nullable
  public String getDefaultNamespaceURI ()
  {
    return m_sPrefix.equals (XMLConstants.DEFAULT_NS_PREFIX) ? m_sNamespaceURI : null;
  }

  @Override
  @Nullable
  protected Iterator <String> getCustomPrefixes (@Nullable final String sNamespaceURI)
  {
    return m_sNamespaceURI.equals (sNamespaceURI) ? SingleElementIterator.create (m_sPrefix) : null;
  }

  @Override
  @Nullable
  protected String getCustomPrefix (@Nullable final String sNamespaceURI)
  {
    return m_sNamespaceURI.equals (sNamespaceURI) ? m_sPrefix : null;
  }

  @Override
  @Nullable
  protected String getCustomNamespaceURI (@Nullable final String sPrefix)
  {
    return m_sPrefix.equals (sPrefix) ? m_sNamespaceURI : null;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Map <String, String> getPrefixToNamespaceURIMap ()
  {
    return CollectionHelper.newMap (m_sPrefix, m_sNamespaceURI);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("prefix", m_sPrefix)
                                       .append ("namespaceURI", m_sNamespaceURI)
                                       .toString ();
  }
}

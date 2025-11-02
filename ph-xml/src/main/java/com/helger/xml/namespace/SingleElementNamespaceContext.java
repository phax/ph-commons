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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.helper.CollectionHelperExt;

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
   *        The namespace URI to use. May neither be <code>null</code> nor empty.
   */
  public SingleElementNamespaceContext (@NonNull @Nonempty final String sNamespaceURI)
  {
    this (XMLConstants.DEFAULT_NS_PREFIX, sNamespaceURI);
  }

  public SingleElementNamespaceContext (@NonNull final String sPrefix, @NonNull @Nonempty final String sNamespaceURI)
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
    return m_sNamespaceURI.equals (sNamespaceURI) ? new CommonsArrayList <> (m_sPrefix).iterator () : null;
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

  @NonNull
  @ReturnsMutableCopy
  public ICommonsMap <String, String> getPrefixToNamespaceURIMap ()
  {
    return CollectionHelperExt.createMap (m_sPrefix, m_sNamespaceURI);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("prefix", m_sPrefix)
                                       .append ("namespaceURI", m_sNamespaceURI)
                                       .getToString ();
  }
}

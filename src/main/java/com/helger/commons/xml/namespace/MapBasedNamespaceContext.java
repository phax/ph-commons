/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.XMLConstants;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.collections.multimap.IMultiMapSetBased;
import com.helger.commons.collections.multimap.MultiHashMapHashSetBased;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Represents a namespace context a 1:n (namespace:prefix) mapping.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class MapBasedNamespaceContext extends AbstractNamespaceContext
{
  private String m_sDefaultNamespaceURI;
  private final Map <String, String> m_aPrefix2NS = new LinkedHashMap <String, String> ();
  private final IMultiMapSetBased <String, String> m_aNS2Prefix = new MultiHashMapHashSetBased <String, String> ();

  public MapBasedNamespaceContext ()
  {}

  @Override
  @Nullable
  public String getDefaultNamespaceURI ()
  {
    return m_sDefaultNamespaceURI;
  }

  /**
   * Set the default namespace URL
   * 
   * @param sNamespaceURI
   *        The namespace URI to be used as the default. May neither be
   *        <code>null</code> nor empty.
   * @return this
   */
  @Nonnull
  public MapBasedNamespaceContext setDefaultNamespaceURI (@Nonnull @Nonempty final String sNamespaceURI)
  {
    return addMapping (XMLConstants.DEFAULT_NS_PREFIX, sNamespaceURI);
  }

  /**
   * Add a new prefix to namespace mapping.
   * 
   * @param sPrefix
   *        The prefix to be used. May not be <code>null</code>. If it equals
   *        {@link XMLConstants#DEFAULT_NS_PREFIX} that the namespace is
   *        considered to be the default one.
   * @param sNamespaceURI
   *        The namespace URI to be mapped. May neither be <code>null</code> nor
   *        empty.
   * @return this
   */
  @Nonnull
  public MapBasedNamespaceContext addMapping (@Nonnull final String sPrefix,
                                              @Nonnull @Nonempty final String sNamespaceURI)
  {
    if (sPrefix == null)
      throw new IllegalArgumentException ("prefix may not be null");
    if (StringHelper.hasNoText (sNamespaceURI))
      throw new IllegalArgumentException ("namespaceURI may not be empty");
    if (m_aPrefix2NS.containsKey (sPrefix))
      throw new IllegalArgumentException ("The prefix '" + sPrefix + "' is already registered!");

    if (sPrefix.equals (XMLConstants.DEFAULT_NS_PREFIX))
      m_sDefaultNamespaceURI = sNamespaceURI;
    m_aPrefix2NS.put (sPrefix, sNamespaceURI);
    m_aNS2Prefix.putSingle (sNamespaceURI, sPrefix);
    return this;
  }

  @Override
  @Nullable
  protected Iterator <String> getCustomPrefixes (@Nonnull final String sNamespaceURI)
  {
    final Set <String> aAllPrefixes = m_aNS2Prefix.get (sNamespaceURI);
    return aAllPrefixes == null ? null : aAllPrefixes.iterator ();
  }

  @Override
  @Nullable
  protected String getCustomPrefix (@Nonnull final String sNamespaceURI)
  {
    final Set <String> aAllPrefixes = m_aNS2Prefix.get (sNamespaceURI);
    return ContainerHelper.getFirstElement (aAllPrefixes);
  }

  @Override
  @Nullable
  protected String getCustomNamespaceURI (@Nonnull final String sPrefix)
  {
    return m_aPrefix2NS.get (sPrefix);
  }

  @Nonnull
  @ReturnsMutableCopy
  public Map <String, String> getPrefixToNamespaceURIMap ()
  {
    return ContainerHelper.newOrderedMap (m_aPrefix2NS);
  }

  public boolean hasAnyMapping ()
  {
    return !m_aPrefix2NS.isEmpty ();
  }

  @Nonnegative
  public int getMappingCount ()
  {
    return m_aPrefix2NS.size ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).appendIfNotNull ("defaultNS", m_sDefaultNamespaceURI)
                                       .append ("prefix2NS", m_aPrefix2NS)
                                       .append ("ns2Prefix2", m_aNS2Prefix)
                                       .toString ();
  }
}

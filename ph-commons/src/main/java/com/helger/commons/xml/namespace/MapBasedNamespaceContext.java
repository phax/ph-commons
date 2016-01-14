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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.XMLConstants;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.multimap.IMultiMapSetBased;
import com.helger.commons.collection.multimap.MultiHashMapHashSetBased;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.lang.ICloneable;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

/**
 * Represents a namespace context a 1:n (namespace:prefix) mapping.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class MapBasedNamespaceContext extends AbstractNamespaceContext implements ICloneable <MapBasedNamespaceContext>
{
  private String m_sDefaultNamespaceURI;
  private final Map <String, String> m_aPrefix2NS = new LinkedHashMap <String, String> ();
  private final IMultiMapSetBased <String, String> m_aNS2Prefix = new MultiHashMapHashSetBased <String, String> ();

  public MapBasedNamespaceContext ()
  {}

  public MapBasedNamespaceContext (@Nullable final MapBasedNamespaceContext aOther)
  {
    if (aOther != null)
    {
      m_sDefaultNamespaceURI = aOther.m_sDefaultNamespaceURI;
      m_aPrefix2NS.putAll (aOther.m_aPrefix2NS);
      m_aNS2Prefix.putAll (aOther.m_aNS2Prefix);
    }
  }

  public MapBasedNamespaceContext (@Nullable final IIterableNamespaceContext aOther)
  {
    addMappings (aOther);
  }

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
   *        The namespace URI to be used as the default. May not be
   *        <code>null</code> but maybe empty.
   * @return this
   */
  @Nonnull
  public MapBasedNamespaceContext setDefaultNamespaceURI (@Nonnull final String sNamespaceURI)
  {
    return addMapping (XMLConstants.DEFAULT_NS_PREFIX, sNamespaceURI);
  }

  @Nonnull
  private MapBasedNamespaceContext _addMapping (@Nonnull final String sPrefix,
                                                @Nonnull final String sNamespaceURI,
                                                final boolean bAllowOverwrite)
  {
    ValueEnforcer.notNull (sPrefix, "Prefix");
    ValueEnforcer.notNull (sNamespaceURI, "NamespaceURI");
    if (!bAllowOverwrite && m_aPrefix2NS.containsKey (sPrefix))
      throw new IllegalArgumentException ("The prefix '" + sPrefix + "' is already registered!");

    if (sPrefix.equals (XMLConstants.DEFAULT_NS_PREFIX))
      m_sDefaultNamespaceURI = sNamespaceURI;
    m_aPrefix2NS.put (sPrefix, sNamespaceURI);
    m_aNS2Prefix.putSingle (sNamespaceURI, sPrefix);
    return this;
  }

  /**
   * Add a new prefix to namespace mapping. If a prefix is already present, an
   * IllegalArgumentException is thrown.
   *
   * @param sPrefix
   *        The prefix to be used. May not be <code>null</code>. If it equals
   *        {@link XMLConstants#DEFAULT_NS_PREFIX} that the namespace is
   *        considered to be the default one.
   * @param sNamespaceURI
   *        The namespace URI to be mapped. May not be <code>null</code> but
   *        maybe empty.
   * @return this
   * @throws IllegalArgumentException
   *         If another mapping for the passed prefix is already present
   * @see #setMapping(String, String)
   */
  @Nonnull
  public MapBasedNamespaceContext addMapping (@Nonnull final String sPrefix, @Nonnull final String sNamespaceURI)
  {
    return _addMapping (sPrefix, sNamespaceURI, false);
  }

  /**
   * Add a new prefix to namespace mapping. If a prefix is already present it is
   * overwritten.
   *
   * @param sPrefix
   *        The prefix to be used. May not be <code>null</code>. If it equals
   *        {@link XMLConstants#DEFAULT_NS_PREFIX} that the namespace is
   *        considered to be the default one.
   * @param sNamespaceURI
   *        The namespace URI to be mapped. May not be <code>null</code> but
   *        maybe empty.
   * @return this
   * @see #addMapping(String, String)
   */
  @Nonnull
  public MapBasedNamespaceContext setMapping (@Nonnull final String sPrefix, @Nonnull final String sNamespaceURI)
  {
    return _addMapping (sPrefix, sNamespaceURI, true);
  }

  @Nonnull
  public final MapBasedNamespaceContext addMappings (@Nullable final IIterableNamespaceContext aOther)
  {
    if (aOther != null)
      for (final Map.Entry <String, String> aEntry : aOther.getPrefixToNamespaceURIMap ().entrySet ())
        addMapping (aEntry.getKey (), aEntry.getValue ());
    return this;
  }

  @Nonnull
  public final MapBasedNamespaceContext setMappings (@Nullable final IIterableNamespaceContext aOther)
  {
    if (aOther != null)
      for (final Map.Entry <String, String> aEntry : aOther.getPrefixToNamespaceURIMap ().entrySet ())
        setMapping (aEntry.getKey (), aEntry.getValue ());
    return this;
  }

  @Nonnull
  public EChange removeMapping (@Nullable final String sPrefix)
  {
    final String sNamespaceURI = m_aPrefix2NS.remove (sPrefix);
    if (sNamespaceURI == null)
      return EChange.UNCHANGED;

    // Remove from namespace 2 prefix map as well
    if (m_aNS2Prefix.removeSingle (sNamespaceURI, sPrefix).isUnchanged ())
      throw new IllegalStateException ("Internal inconsistency removing '" + sPrefix + "' and '" + sNamespaceURI + "'");
    return EChange.CHANGED;
  }

  @Nonnull
  public EChange clear ()
  {
    if (m_aPrefix2NS.isEmpty ())
      return EChange.UNCHANGED;

    m_aPrefix2NS.clear ();
    m_aNS2Prefix.clear ();
    m_sDefaultNamespaceURI = null;
    return EChange.CHANGED;
  }

  @Override
  @Nullable
  public Iterator <String> getCustomPrefixes (@Nonnull final String sNamespaceURI)
  {
    final Set <String> aAllPrefixes = m_aNS2Prefix.get (sNamespaceURI);
    return aAllPrefixes == null ? null : aAllPrefixes.iterator ();
  }

  @Override
  @Nullable
  public String getCustomPrefix (@Nonnull final String sNamespaceURI)
  {
    final Set <String> aAllPrefixes = m_aNS2Prefix.get (sNamespaceURI);
    return CollectionHelper.getFirstElement (aAllPrefixes);
  }

  @Override
  @Nullable
  public String getCustomNamespaceURI (@Nonnull final String sPrefix)
  {
    return m_aPrefix2NS.get (sPrefix);
  }

  @Nonnull
  @ReturnsMutableCopy
  public Map <String, String> getPrefixToNamespaceURIMap ()
  {
    return CollectionHelper.newOrderedMap (m_aPrefix2NS);
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

  @Nonnull
  @ReturnsMutableCopy
  public MapBasedNamespaceContext getClone ()
  {
    return new MapBasedNamespaceContext (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MapBasedNamespaceContext rhs = (MapBasedNamespaceContext) o;
    return EqualsHelper.equals (m_sDefaultNamespaceURI, rhs.m_sDefaultNamespaceURI) &&
           m_aPrefix2NS.equals (rhs.m_aPrefix2NS) &&
           m_aNS2Prefix.equals (rhs.m_aNS2Prefix);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sDefaultNamespaceURI)
                                       .append (m_aPrefix2NS)
                                       .append (m_aNS2Prefix)
                                       .getHashCode ();
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

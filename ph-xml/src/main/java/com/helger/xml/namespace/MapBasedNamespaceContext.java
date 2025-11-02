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
import java.util.Map;

import javax.xml.XMLConstants;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.clone.ICloneable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.CollectionFind;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.collection.commons.ICommonsSet;

/**
 * Represents a namespace context a 1:n (namespace:prefix) mapping.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class MapBasedNamespaceContext extends AbstractNamespaceContext implements ICloneable <MapBasedNamespaceContext>
{
  private String m_sDefaultNamespaceURI;
  private final ICommonsOrderedMap <String, String> m_aPrefix2NS = new CommonsLinkedHashMap <> ();
  private final ICommonsMap <String, ICommonsSet <String>> m_aNS2Prefix = new CommonsHashMap <> ();

  /**
   * Default constructor.
   */
  public MapBasedNamespaceContext ()
  {}

  /**
   * Copy constructor.
   *
   * @param aOther
   *        Object to copy from. May be <code>null</code>.
   */
  public MapBasedNamespaceContext (@Nullable final MapBasedNamespaceContext aOther)
  {
    if (aOther != null)
    {
      m_sDefaultNamespaceURI = aOther.m_sDefaultNamespaceURI;
      m_aPrefix2NS.putAll (aOther.m_aPrefix2NS);

      // putAll is not enough here
      for (final Map.Entry <String, ICommonsSet <String>> aEntry : aOther.m_aNS2Prefix.entrySet ())
        m_aNS2Prefix.put (aEntry.getKey (), aEntry.getValue ().getClone ());
    }
  }

  /**
   * Constructor to copy from another {@link IIterableNamespaceContext}
   *
   * @param aOther
   *        Object to copy the data from. May be <code>null</code>.
   */
  public MapBasedNamespaceContext (@Nullable final IIterableNamespaceContext aOther)
  {
    addMappings (aOther);
  }

  /**
   * Constructor with prefix to namespace URL map
   *
   * @param aOther
   *        Map with prefix to namespace URL. May be <code>null</code>.
   * @since 8.5.3
   */
  public MapBasedNamespaceContext (@Nullable final Map <String, String> aOther)
  {
    addMappings (aOther);
  }

  @Override
  @Nullable
  public String getDefaultNamespaceURI ()
  {
    return m_sDefaultNamespaceURI;
  }

  @NonNull
  private MapBasedNamespaceContext _addMapping (@NonNull final String sPrefix,
                                                @NonNull final String sNamespaceURI,
                                                final boolean bAllowOverwrite)
  {
    ValueEnforcer.notNull (sPrefix, "Prefix");
    ValueEnforcer.notNull (sNamespaceURI, "NamespaceURI");
    if (!bAllowOverwrite && m_aPrefix2NS.containsKey (sPrefix))
      throw new IllegalArgumentException ("The prefix '" +
                                          sPrefix +
                                          "' is already registered to '" +
                                          m_aPrefix2NS.get (sPrefix) +
                                          "'!");

    if (sPrefix.equals (XMLConstants.DEFAULT_NS_PREFIX))
      m_sDefaultNamespaceURI = sNamespaceURI;
    m_aPrefix2NS.put (sPrefix, sNamespaceURI);
    m_aNS2Prefix.computeIfAbsent (sNamespaceURI, x -> new CommonsHashSet <> ()).add (sPrefix);
    return this;
  }

  /**
   * Add a new prefix to namespace mapping. If a prefix is already present, an
   * IllegalArgumentException is thrown.
   *
   * @param sPrefix
   *        The prefix to be used. May not be <code>null</code>. If it equals
   *        {@link XMLConstants#DEFAULT_NS_PREFIX} that the namespace is considered to be the
   *        default one.
   * @param sNamespaceURI
   *        The namespace URI to be mapped. May not be <code>null</code> but maybe empty.
   * @return this
   * @throws IllegalArgumentException
   *         If another mapping for the passed prefix is already present
   * @see #setMapping(String, String)
   */
  @NonNull
  public final MapBasedNamespaceContext addMapping (@NonNull final String sPrefix, @NonNull final String sNamespaceURI)
  {
    return _addMapping (sPrefix, sNamespaceURI, false);
  }

  /**
   * Add a new prefix to namespace mapping. If a prefix is already present it is overwritten.
   *
   * @param sPrefix
   *        The prefix to be used. May not be <code>null</code>. If it equals
   *        {@link XMLConstants#DEFAULT_NS_PREFIX} that the namespace is considered to be the
   *        default one.
   * @param sNamespaceURI
   *        The namespace URI to be mapped. May not be <code>null</code> but maybe empty.
   * @return this
   * @see #addMapping(String, String)
   */
  @NonNull
  public final MapBasedNamespaceContext setMapping (@NonNull final String sPrefix, @NonNull final String sNamespaceURI)
  {
    return _addMapping (sPrefix, sNamespaceURI, true);
  }

  @NonNull
  public final MapBasedNamespaceContext addMappings (@Nullable final IIterableNamespaceContext aOther)
  {
    if (aOther != null)
      addMappings (aOther.getPrefixToNamespaceURIMap ());
    return this;
  }

  @NonNull
  public final MapBasedNamespaceContext setMappings (@Nullable final IIterableNamespaceContext aOther)
  {
    if (aOther != null)
      setMappings (aOther.getPrefixToNamespaceURIMap ());
    return this;
  }

  @NonNull
  public final MapBasedNamespaceContext addMappings (@Nullable final Map <String, String> aOther)
  {
    if (aOther != null)
      for (final Map.Entry <String, String> aEntry : aOther.entrySet ())
        addMapping (aEntry.getKey (), aEntry.getValue ());
    return this;
  }

  @NonNull
  public final MapBasedNamespaceContext setMappings (@Nullable final Map <String, String> aOther)
  {
    if (aOther != null)
      for (final Map.Entry <String, String> aEntry : aOther.entrySet ())
        setMapping (aEntry.getKey (), aEntry.getValue ());
    return this;
  }

  /**
   * Add the default namespace URL, so the mapping to the default XML namespace prefix
   * (<code>""</code>).
   *
   * @param sNamespaceURI
   *        The namespace URI to be used as the default. May not be <code>null</code> but maybe
   *        empty.
   * @return this
   */
  @NonNull
  public final MapBasedNamespaceContext addDefaultNamespaceURI (@NonNull final String sNamespaceURI)
  {
    return addMapping (XMLConstants.DEFAULT_NS_PREFIX, sNamespaceURI);
  }

  /**
   * Set the default namespace URL, so the mapping to the default XML namespace prefix
   * (<code>""</code>).
   *
   * @param sNamespaceURI
   *        The namespace URI to be used as the default. May not be <code>null</code> but maybe
   *        empty.
   * @return this
   */
  @NonNull
  public final MapBasedNamespaceContext setDefaultNamespaceURI (@NonNull final String sNamespaceURI)
  {
    return setMapping (XMLConstants.DEFAULT_NS_PREFIX, sNamespaceURI);
  }

  @NonNull
  public MapBasedNamespaceContext removeMapping (@Nullable final String sPrefix)
  {
    final String sNamespaceURI = m_aPrefix2NS.remove (sPrefix);
    if (sNamespaceURI != null)
    {
      // Remove from namespace 2 prefix map as well
      final ICommonsSet <String> aSet = m_aNS2Prefix.get (sNamespaceURI);
      if (aSet == null || aSet.removeObject (sPrefix).isUnchanged ())
        throw new IllegalStateException ("Internal inconsistency removing '" +
                                         sPrefix +
                                         "' and '" +
                                         sNamespaceURI +
                                         "'");
    }
    return this;
  }

  public boolean isPrefixMapped (@Nullable final String sPrefix)
  {
    return m_aPrefix2NS.containsKey (sPrefix);
  }

  public boolean isNamespaceURIMapped (@Nullable final String sNamespaceURI)
  {
    return m_aNS2Prefix.containsKey (sNamespaceURI);
  }

  @NonNull
  public MapBasedNamespaceContext clear ()
  {
    if (m_aPrefix2NS.isNotEmpty ())
    {
      m_aPrefix2NS.clear ();
      m_aNS2Prefix.clear ();
      m_sDefaultNamespaceURI = null;
    }
    return this;
  }

  @Override
  @Nullable
  public Iterator <String> getCustomPrefixes (@NonNull final String sNamespaceURI)
  {
    final ICommonsSet <String> aAllPrefixes = m_aNS2Prefix.get (sNamespaceURI);
    return aAllPrefixes == null ? null : aAllPrefixes.iterator ();
  }

  @Override
  @Nullable
  public String getCustomPrefix (@NonNull final String sNamespaceURI)
  {
    final ICommonsSet <String> aAllPrefixes = m_aNS2Prefix.get (sNamespaceURI);
    return CollectionFind.getFirstElement (aAllPrefixes);
  }

  @Override
  @Nullable
  public String getCustomNamespaceURI (@NonNull final String sPrefix)
  {
    return m_aPrefix2NS.get (sPrefix);
  }

  @NonNull
  @ReturnsMutableCopy
  public ICommonsOrderedMap <String, String> getPrefixToNamespaceURIMap ()
  {
    return m_aPrefix2NS.getClone ();
  }

  public boolean hasAnyMapping ()
  {
    return m_aPrefix2NS.isNotEmpty ();
  }

  @Nonnegative
  public int getMappingCount ()
  {
    return m_aPrefix2NS.size ();
  }

  @NonNull
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
    final Object aObj1 = m_sDefaultNamespaceURI;
    return EqualsHelper.equals (aObj1, rhs.m_sDefaultNamespaceURI) &&
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
                                       .getToString ();
  }
}

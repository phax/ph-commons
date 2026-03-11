/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.xml.resourcebundle;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.WillClose;
import com.helger.annotation.WillNotClose;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.misc.DevelopersNote;
import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.collection.enumeration.EnumerationHelper;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.microdom.serialize.MicroReader;

/**
 * Helper class to handle XML based properties. It is read-only.<br>
 * See <a href= "http://docs.oracle.com/javase/6/docs/api/java/util/ResourceBundle.Control.html"
 * >Resource.Control</a> Javadocs
 *
 * @author Philip Helger
 */
@Immutable
public final class XMLResourceBundle extends ResourceBundle
{
  private final ICommonsOrderedMap <String, String> m_aValues;

  /**
   * Read data from a Java properties XML file format.
   *
   * @param aIS
   *        The input stream to read from. Will be closed. May not be <code>null</code>.
   * @return A mutable ordered map with all key-value pairs. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static ICommonsOrderedMap <String, String> readFromPropertiesXML (@NonNull @WillClose final InputStream aIS)
  {
    final ICommonsOrderedMap <String, String> ret = new CommonsLinkedHashMap <> ();
    final IMicroDocument aDoc = MicroReader.readMicroXML (aIS);
    if (aDoc != null)
      for (final IMicroElement eChild : aDoc.getDocumentElement ().getAllChildElements ("entry"))
        ret.put (eChild.getAttributeValue ("key"), eChild.getTextContent ());
    return ret;
  }

  /**
   * Convert a map of key-value pairs to a Java properties XML document.
   *
   * @param aMap
   *        The map to convert. May not be <code>null</code>.
   * @return The created micro document. Never <code>null</code>.
   */
  @NonNull
  public static IMicroDocument getAsPropertiesXML (@NonNull final Map <String, String> aMap)
  {
    final IMicroDocument ret = new MicroDocument ();
    final IMicroElement eRoot = ret.addElement ("properties");
    for (final Map.Entry <String, String> aEntry : aMap.entrySet ())
      eRoot.addElement ("entry").setAttribute ("key", aEntry.getKey ()).addText (aEntry.getValue ());
    return ret;
  }

  @DevelopersNote ("Don't use it manually - use the static methods of this class!")
  XMLResourceBundle (@NonNull @WillNotClose final InputStream aIS)
  {
    ValueEnforcer.notNull (aIS, "InputStream");

    // Read the main properties
    m_aValues = readFromPropertiesXML (aIS);
  }

  /**
   * @return A mutable copy of all contained key-value pairs. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsMap <String, String> getAllValues ()
  {
    return m_aValues.getClone ();
  }

  /**
   * More efficient version to retrieve the keySet
   *
   * @return all resource names
   */
  @Override
  @CodingStyleguideUnaware
  protected Set <String> handleKeySet ()
  {
    return m_aValues.keySet ();
  }

  /**
   * Main internal lookup
   *
   * @return the string matching the passed key
   */
  @Override
  protected String handleGetObject (@Nullable final String sKey)
  {
    return m_aValues.get (sKey);
  }

  /** {@inheritDoc} */
  @Override
  public Enumeration <String> getKeys ()
  {
    return EnumerationHelper.getEnumeration (m_aValues.keySet ());
  }

  /**
   * Get the XML resource bundle with the specified base name using the default locale.
   *
   * @param sBaseName
   *        The base name of the resource bundle. May not be <code>null</code>.
   * @return The XML resource bundle. Never <code>null</code>.
   */
  @NonNull
  public static XMLResourceBundle getXMLBundle (@NonNull final String sBaseName)
  {
    return getXMLBundle (sBaseName, Locale.getDefault ());
  }

  /**
   * Get the XML resource bundle with the specified base name and locale.
   *
   * @param sBaseName
   *        The base name of the resource bundle. May not be <code>null</code>.
   * @param aLocale
   *        The locale to use. May not be <code>null</code>.
   * @return The XML resource bundle. Never <code>null</code>.
   */
  @NonNull
  public static XMLResourceBundle getXMLBundle (@NonNull final String sBaseName, @NonNull final Locale aLocale)
  {
    return (XMLResourceBundle) ResourceBundle.getBundle (sBaseName, aLocale, new XMLResourceBundleControl ());
  }

  /**
   * Get the XML resource bundle with the specified base name, locale and class loader.
   *
   * @param sBaseName
   *        The base name of the resource bundle. May not be <code>null</code>.
   * @param aLocale
   *        The locale to use. May not be <code>null</code>.
   * @param aClassLoader
   *        The class loader to use. May not be <code>null</code>.
   * @return The XML resource bundle. Never <code>null</code>.
   */
  @NonNull
  public static XMLResourceBundle getXMLBundle (@NonNull final String sBaseName,
                                                @NonNull final Locale aLocale,
                                                @NonNull final ClassLoader aClassLoader)
  {
    return (XMLResourceBundle) ResourceBundle.getBundle (sBaseName,
                                                         aLocale,
                                                         aClassLoader,
                                                         new XMLResourceBundleControl ());
  }
}

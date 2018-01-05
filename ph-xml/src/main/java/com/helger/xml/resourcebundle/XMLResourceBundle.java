/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillClose;
import javax.annotation.WillNotClose;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.annotation.DevelopersNote;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.IteratorHelper;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.microdom.serialize.MicroReader;

/**
 * Helper class to handle XML based properties. It is read-only.<br>
 * See <a href=
 * "http://docs.oracle.com/javase/6/docs/api/java/util/ResourceBundle.Control.html"
 * >Resource.Control</a> Javadocs
 *
 * @author Philip Helger
 */
@Immutable
public final class XMLResourceBundle extends ResourceBundle
{
  private final ICommonsOrderedMap <String, String> m_aValues;

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsOrderedMap <String, String> readFromPropertiesXML (@Nonnull @WillClose final InputStream aIS)
  {
    final ICommonsOrderedMap <String, String> ret = new CommonsLinkedHashMap <> ();
    final IMicroDocument aDoc = MicroReader.readMicroXML (aIS);
    if (aDoc != null)
      for (final IMicroElement eChild : aDoc.getDocumentElement ().getAllChildElements ("entry"))
        ret.put (eChild.getAttributeValue ("key"), eChild.getTextContent ());
    return ret;
  }

  @Nonnull
  public static IMicroDocument getAsPropertiesXML (@Nonnull final ICommonsMap <String, String> aMap)
  {
    final IMicroDocument ret = new MicroDocument ();
    final IMicroElement eRoot = ret.appendElement ("properties");
    for (final Map.Entry <String, String> aEntry : aMap.entrySet ())
      eRoot.appendElement ("entry").setAttribute ("key", aEntry.getKey ()).appendText (aEntry.getValue ());
    return ret;
  }

  @DevelopersNote ("Don't use it manually - use the static methods of this class!")
  XMLResourceBundle (@Nonnull @WillNotClose final InputStream aIS)
  {
    ValueEnforcer.notNull (aIS, "InputStream");

    // Read the main properties
    m_aValues = readFromPropertiesXML (aIS);
  }

  @Nonnull
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

  @Override
  public Enumeration <String> getKeys ()
  {
    return IteratorHelper.getEnumeration (m_aValues.keySet ());
  }

  @Nonnull
  public static XMLResourceBundle getXMLBundle (@Nonnull final String sBaseName)
  {
    return getXMLBundle (sBaseName, Locale.getDefault ());
  }

  @Nonnull
  public static XMLResourceBundle getXMLBundle (@Nonnull final String sBaseName, @Nonnull final Locale aLocale)
  {
    return (XMLResourceBundle) ResourceBundle.getBundle (sBaseName, aLocale, new XMLResourceBundleControl ());
  }

  @Nonnull
  public static XMLResourceBundle getXMLBundle (@Nonnull final String sBaseName,
                                                @Nonnull final Locale aLocale,
                                                @Nonnull final ClassLoader aClassLoader)
  {
    return (XMLResourceBundle) ResourceBundle.getBundle (sBaseName,
                                                         aLocale,
                                                         aClassLoader,
                                                         new XMLResourceBundleControl ());
  }
}

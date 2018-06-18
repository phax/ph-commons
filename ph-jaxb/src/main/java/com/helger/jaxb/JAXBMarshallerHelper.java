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
package com.helger.jaxb;

import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.namespace.NamespaceContext;

/**
 * This class contains utility methods for JAXB {@link Marshaller} objects. It
 * allows for setting type-safe properties.
 *
 * @author Philip Helger
 */
@Immutable
public final class JAXBMarshallerHelper
{
  private static final String JAXB_EXTERNAL_CLASS_NAME = "com.sun.xml.bind.v2.runtime.MarshallerImpl";

  // Sun specific property name (ripped from
  // com.sun.xml.bind.v2.runtime.MarshallerImpl)
  private static final String SUN_INDENT_STRING = "com.sun.xml.bind.indentString";
  private static final String SUN_PREFIX_MAPPER = "com.sun.xml.bind.namespacePrefixMapper";
  private static final String SUN_ENCODING_HANDLER2 = "com.sun.xml.bind.marshaller.CharacterEscapeHandler";
  private static final String SUN_XML_HEADERS = "com.sun.xml.bind.xmlHeaders";
  private static final String SUN_C14N = "com.sun.xml.bind.c14n";
  private static final String SUN_OBJECT_IDENTITY_CYCLE_DETECTION = "com.sun.xml.bind.objectIdentitityCycleDetection";

  private JAXBMarshallerHelper ()
  {}

  private static void _setProperty (@Nonnull final Marshaller aMarshaller,
                                    @Nonnull final String sPropertyName,
                                    @Nullable final Object aValue)
  {
    try
    {
      aMarshaller.setProperty (sPropertyName, aValue);
    }
    catch (final PropertyException ex)
    {
      throw new IllegalArgumentException ("Failed to set JAXB property '" + sPropertyName + "' to " + aValue, ex);
    }
  }

  @Nullable
  private static Object _getProperty (@Nonnull final Marshaller aMarshaller, @Nonnull final String sPropertyName)
  {
    try
    {
      return aMarshaller.getProperty (sPropertyName);
    }
    catch (final PropertyException ex)
    {
      throw new IllegalArgumentException ("Failed to get JAXB property '" + sPropertyName + "'", ex);
    }
  }

  private static boolean _getBooleanProperty (@Nonnull final Marshaller aMarshaller,
                                              @Nonnull final String sPropertyName)
  {
    return ((Boolean) _getProperty (aMarshaller, sPropertyName)).booleanValue ();
  }

  @Nullable
  private static String _getStringProperty (@Nonnull final Marshaller aMarshaller, @Nonnull final String sPropertyName)
  {
    return (String) _getProperty (aMarshaller, sPropertyName);
  }

  /**
   * Set the standard property for the encoding charset.
   *
   * @param aMarshaller
   *        The marshaller to set the property. May not be <code>null</code>.
   * @param aEncoding
   *        the value to be set
   */
  public static void setEncoding (@Nonnull final Marshaller aMarshaller, @Nullable final Charset aEncoding)
  {
    setEncoding (aMarshaller, aEncoding == null ? null : aEncoding.name ());
  }

  /**
   * Set the standard property for the encoding charset.
   *
   * @param aMarshaller
   *        The marshaller to set the property. May not be <code>null</code>.
   * @param sEncoding
   *        the value to be set
   */
  public static void setEncoding (@Nonnull final Marshaller aMarshaller, @Nullable final String sEncoding)
  {
    _setProperty (aMarshaller, Marshaller.JAXB_ENCODING, sEncoding);
  }

  @Nullable
  public static String getEncoding (@Nonnull final Marshaller aMarshaller)
  {
    return _getStringProperty (aMarshaller, Marshaller.JAXB_ENCODING);
  }

  /**
   * Set the standard property for formatting the output or not.
   *
   * @param aMarshaller
   *        The marshaller to set the property. May not be <code>null</code>.
   * @param bFormattedOutput
   *        the value to be set
   */
  public static void setFormattedOutput (@Nonnull final Marshaller aMarshaller, final boolean bFormattedOutput)
  {
    _setProperty (aMarshaller, Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.valueOf (bFormattedOutput));
  }

  public static boolean isFormattedOutput (@Nonnull final Marshaller aMarshaller)
  {
    return _getBooleanProperty (aMarshaller, Marshaller.JAXB_FORMATTED_OUTPUT);
  }

  /**
   * Set the standard property for setting the namespace schema location
   *
   * @param aMarshaller
   *        The marshaller to set the property. May not be <code>null</code>.
   * @param sSchemaLocation
   *        the value to be set
   */
  public static void setSchemaLocation (@Nonnull final Marshaller aMarshaller, @Nullable final String sSchemaLocation)
  {
    _setProperty (aMarshaller, Marshaller.JAXB_SCHEMA_LOCATION, sSchemaLocation);
  }

  @Nullable
  public static String getSchemaLocation (@Nonnull final Marshaller aMarshaller)
  {
    return _getStringProperty (aMarshaller, Marshaller.JAXB_SCHEMA_LOCATION);
  }

  /**
   * Set the standard property for setting the no-namespace schema location
   *
   * @param aMarshaller
   *        The marshaller to set the property. May not be <code>null</code>.
   * @param sSchemaLocation
   *        the value to be set
   */
  public static void setNoNamespaceSchemaLocation (@Nonnull final Marshaller aMarshaller,
                                                   @Nullable final String sSchemaLocation)
  {
    _setProperty (aMarshaller, Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, sSchemaLocation);
  }

  @Nullable
  public static String getNoNamespaceSchemaLocation (@Nonnull final Marshaller aMarshaller)
  {
    return _getStringProperty (aMarshaller, Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION);
  }

  /**
   * Set the standard property for marshalling a fragment only.
   *
   * @param aMarshaller
   *        The marshaller to set the property. May not be <code>null</code>.
   * @param bFragment
   *        the value to be set
   */
  public static void setFragment (@Nonnull final Marshaller aMarshaller, final boolean bFragment)
  {
    _setProperty (aMarshaller, Marshaller.JAXB_FRAGMENT, Boolean.valueOf (bFragment));
  }

  public static boolean isFragment (@Nonnull final Marshaller aMarshaller)
  {
    return _getBooleanProperty (aMarshaller, Marshaller.JAXB_FRAGMENT);
  }

  /**
   * Set the Sun specific property for the indent string.
   *
   * @param aMarshaller
   *        The marshaller to set the property. May not be <code>null</code>.
   * @param sIndentString
   *        the value to be set
   */
  public static void setSunIndentString (@Nonnull final Marshaller aMarshaller, @Nullable final String sIndentString)
  {
    final String sPropertyName = SUN_INDENT_STRING;
    _setProperty (aMarshaller, sPropertyName, sIndentString);
  }

  @Nullable
  public static String getSunIndentString (@Nonnull final Marshaller aMarshaller)
  {
    final String sPropertyName = SUN_INDENT_STRING;
    return _getStringProperty (aMarshaller, sPropertyName);
  }

  /**
   * Set the Sun specific encoding handler. Value must implement
   * com.sun.xml.bind.marshaller.CharacterEscapeHandler
   *
   * @param aMarshaller
   *        The marshaller to set the property. May not be <code>null</code>.
   * @param aCharacterEscapeHandler
   *        the value to be set
   */
  public static void setSunCharacterEscapeHandler (@Nonnull final Marshaller aMarshaller,
                                                   @Nonnull final Object aCharacterEscapeHandler)
  {
    final String sPropertyName = SUN_ENCODING_HANDLER2;
    _setProperty (aMarshaller, sPropertyName, aCharacterEscapeHandler);
  }

  @Nullable
  public static Object getSunCharacterEscapeHandler (@Nonnull final Marshaller aMarshaller)
  {
    final String sPropertyName = SUN_ENCODING_HANDLER2;
    return _getProperty (aMarshaller, sPropertyName);
  }

  /**
   * Set the Sun specific namespace prefix mapper based on a generic
   * {@link NamespaceContext}. This method instantiates an
   * {@link JAXBNamespacePrefixMapper}.
   *
   * @param aMarshaller
   *        The marshaller to set the property. May not be <code>null</code>.
   * @param aNamespaceContext
   *        The namespace context to be used. May not be <code>null</code>.
   * @throws IllegalArgumentException
   *         If the passed NamespaceContext cannot be set.
   */
  public static void setSunNamespacePrefixMapper (@Nonnull final Marshaller aMarshaller,
                                                  @Nonnull final NamespaceContext aNamespaceContext)
  {
    final JAXBNamespacePrefixMapper aNamespacePrefixMapper = new JAXBNamespacePrefixMapper (aNamespaceContext);
    setSunNamespacePrefixMapper (aMarshaller, aNamespacePrefixMapper);
  }

  /**
   * Set the Sun specific namespace prefix mapper. Value must implement either
   * <code>com.sun.xml.bind.marshaller.NamespacePrefixMapper</code> or
   * <code>com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper</code>
   * depending on the implementation type of <code>Marshaller</code>.
   *
   * @param aMarshaller
   *        The marshaller to set the property. May not be <code>null</code>.
   * @param aNamespacePrefixMapper
   *        the value to be set
   */
  public static void setSunNamespacePrefixMapper (@Nonnull final Marshaller aMarshaller,
                                                  @Nonnull final JAXBNamespacePrefixMapper aNamespacePrefixMapper)
  {
    final String sPropertyName = SUN_PREFIX_MAPPER;
    _setProperty (aMarshaller, sPropertyName, aNamespacePrefixMapper);
  }

  @Nullable
  public static JAXBNamespacePrefixMapper getSunNamespacePrefixMapper (@Nonnull final Marshaller aMarshaller)
  {
    final String sPropertyName = SUN_PREFIX_MAPPER;
    return (JAXBNamespacePrefixMapper) _getProperty (aMarshaller, sPropertyName);
  }

  /**
   * Set the Sun specific XML header string.
   *
   * @param aMarshaller
   *        The marshaller to set the property. May not be <code>null</code>.
   * @param sXMLHeaders
   *        the value to be set
   */
  public static void setSunXMLHeaders (@Nonnull final Marshaller aMarshaller, @Nonnull final String sXMLHeaders)
  {
    final String sPropertyName = SUN_XML_HEADERS;
    _setProperty (aMarshaller, sPropertyName, sXMLHeaders);
  }

  @Nullable
  public static String getSunXMLHeaders (@Nonnull final Marshaller aMarshaller)
  {
    final String sPropertyName = SUN_XML_HEADERS;
    return _getStringProperty (aMarshaller, sPropertyName);
  }

  /**
   * Set the Sun specific canonicalization property.
   *
   * @param aMarshaller
   *        The marshaller to set the property. May not be <code>null</code>.
   * @param bCanonicalize
   *        the value to be set
   */
  public static void setSunCanonicalization (@Nonnull final Marshaller aMarshaller, final boolean bCanonicalize)
  {
    final String sPropertyName = SUN_C14N;
    _setProperty (aMarshaller, sPropertyName, Boolean.valueOf (bCanonicalize));
  }

  public static boolean isSunCanonicalization (@Nonnull final Marshaller aMarshaller)
  {
    final String sPropertyName = SUN_C14N;
    return _getBooleanProperty (aMarshaller, sPropertyName);
  }

  /**
   * Set the Sun specific canonicalization property.
   *
   * @param aMarshaller
   *        The marshaller to set the property. May not be <code>null</code>.
   * @param bObjectIdentityCycleDetection
   *        the value to be set
   */
  public static void setSunObjectIdentityCycleDetection (@Nonnull final Marshaller aMarshaller,
                                                         final boolean bObjectIdentityCycleDetection)
  {
    final String sPropertyName = SUN_OBJECT_IDENTITY_CYCLE_DETECTION;
    _setProperty (aMarshaller, sPropertyName, Boolean.valueOf (bObjectIdentityCycleDetection));
  }

  public static boolean isSunObjectIdentityCycleDetection (@Nonnull final Marshaller aMarshaller)
  {
    final String sPropertyName = SUN_OBJECT_IDENTITY_CYCLE_DETECTION;
    return _getBooleanProperty (aMarshaller, sPropertyName);
  }

  /**
   * Check if the passed Marshaller is a Sun JAXB v2 marshaller. Use this method
   * to determined, whether the Sun specific methods may be invoked or not.
   *
   * @param aMarshaller
   *        The marshaller to be checked. May be <code>null</code>.
   * @return <code>true</code> if the passed marshaller is not <code>null</code>
   *         and is of the Sun class.
   */
  public static boolean isSunJAXB2Marshaller (@Nullable final Marshaller aMarshaller)
  {
    if (aMarshaller == null)
      return false;
    final String sClassName = aMarshaller.getClass ().getName ();
    return sClassName.equals (JAXB_EXTERNAL_CLASS_NAME);
  }
}

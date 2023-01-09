/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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
import javax.xml.namespace.NamespaceContext;

import org.glassfish.jaxb.runtime.api.JAXBRIContext;

import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.PropertyException;

/**
 * This class contains utility methods for JAXB {@link Marshaller} objects. It
 * allows for setting type-safe properties.
 *
 * @author Philip Helger
 */
@Immutable
public final class JAXBMarshallerHelper
{
  private static final String JAXB_EXTERNAL_CLASS_NAME = "org.glassfish.jaxb.runtime.v2.runtime.MarshallerImpl";

  // Jakarta specific property name (ripped from
  // org.glassfish.jaxb.runtime.v2.runtime.MarshallerImpl)
  private static final String JAKARTA_INDENT_STRING = "org.glassfish.jaxb.indentString";
  private static final String JAKARTA_PREFIX_MAPPER = "org.glassfish.jaxb.namespacePrefixMapper";
  private static final String JAKARTA_ENCODING_HANDLER2 = "org.glassfish.jaxb.marshaller.CharacterEscapeHandler";
  private static final String JAKARTA_XML_HEADERS = "org.glassfish.jaxb.xmlHeaders";
  private static final String JAKARTA_C14N = JAXBRIContext.CANONICALIZATION_SUPPORT;
  private static final String JAKARTA_OBJECT_IDENTITY_CYCLE_DETECTION = "org.glassfish.jaxb.objectIdentitityCycleDetection";

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

  private static boolean _getBooleanProperty (@Nonnull final Marshaller aMarshaller, @Nonnull final String sPropertyName)
  {
    return ((Boolean) _getProperty (aMarshaller, sPropertyName)).booleanValue ();
  }

  @Nullable
  private static String _getStringProperty (@Nonnull final Marshaller aMarshaller, @Nonnull final String sPropertyName)
  {
    return (String) _getProperty (aMarshaller, sPropertyName);
  }

  @Nullable
  public static String getEncoding (@Nonnull final Marshaller aMarshaller)
  {
    return _getStringProperty (aMarshaller, Marshaller.JAXB_ENCODING);
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

  public static boolean isFormattedOutput (@Nonnull final Marshaller aMarshaller)
  {
    return _getBooleanProperty (aMarshaller, Marshaller.JAXB_FORMATTED_OUTPUT);
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

  @Nullable
  public static String getSchemaLocation (@Nonnull final Marshaller aMarshaller)
  {
    return _getStringProperty (aMarshaller, Marshaller.JAXB_SCHEMA_LOCATION);
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
  public static String getNoNamespaceSchemaLocation (@Nonnull final Marshaller aMarshaller)
  {
    return _getStringProperty (aMarshaller, Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION);
  }

  /**
   * Set the standard property for setting the no-namespace schema location
   *
   * @param aMarshaller
   *        The marshaller to set the property. May not be <code>null</code>.
   * @param sSchemaLocation
   *        the value to be set
   */
  public static void setNoNamespaceSchemaLocation (@Nonnull final Marshaller aMarshaller, @Nullable final String sSchemaLocation)
  {
    _setProperty (aMarshaller, Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, sSchemaLocation);
  }

  public static boolean isFragment (@Nonnull final Marshaller aMarshaller)
  {
    return _getBooleanProperty (aMarshaller, Marshaller.JAXB_FRAGMENT);
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

  @Nullable
  public static String getJakartaIndentString (@Nonnull final Marshaller aMarshaller)
  {
    return _getStringProperty (aMarshaller, JAKARTA_INDENT_STRING);
  }

  /**
   * Set the Jakarta specific property for the indent string.
   *
   * @param aMarshaller
   *        The marshaller to set the property. May not be <code>null</code>.
   * @param sIndentString
   *        the value to be set
   */
  public static void setJakartaIndentString (@Nonnull final Marshaller aMarshaller, @Nullable final String sIndentString)
  {
    _setProperty (aMarshaller, JAKARTA_INDENT_STRING, sIndentString);
  }

  @Nullable
  public static Object getJakartaCharacterEscapeHandler (@Nonnull final Marshaller aMarshaller)
  {
    return _getProperty (aMarshaller, JAKARTA_ENCODING_HANDLER2);
  }

  /**
   * Set the Jakarta specific encoding handler. Value must implement
   * com.sun.xml.bind.marshaller.CharacterEscapeHandler
   *
   * @param aMarshaller
   *        The marshaller to set the property. May not be <code>null</code>.
   * @param aCharacterEscapeHandler
   *        the value to be set
   */
  public static void setJakartaCharacterEscapeHandler (@Nonnull final Marshaller aMarshaller, @Nonnull final Object aCharacterEscapeHandler)
  {
    _setProperty (aMarshaller, JAKARTA_ENCODING_HANDLER2, aCharacterEscapeHandler);
  }

  @Nullable
  public static JAXBNamespacePrefixMapper getJakartaNamespacePrefixMapper (@Nonnull final Marshaller aMarshaller)
  {
    return (JAXBNamespacePrefixMapper) _getProperty (aMarshaller, JAKARTA_PREFIX_MAPPER);
  }

  /**
   * Set the Jakarta specific namespace prefix mapper based on a generic
   * {@link NamespaceContext}. This method instantiates an
   * {@link JAXBNamespacePrefixMapper}.
   *
   * @param aMarshaller
   *        The marshaller to set the property. May not be <code>null</code>.
   * @param aNamespaceContext
   *        The namespace context to be used. May not be <code>null</code>.
   * @throws IllegalArgumentException
   *         If the passed NamespaceContext cannot be set.
   * @throws NoClassDefFoundError
   *         if the JAXB reference implementation was not found (requires the
   *         <code>com.sun.xml.bind:jaxb-impl</code> artefact)
   */
  public static void setJakartaNamespacePrefixMapper (@Nonnull final Marshaller aMarshaller,
                                                      @Nonnull final NamespaceContext aNamespaceContext)
  {
    final JAXBNamespacePrefixMapper aNamespacePrefixMapper = new JAXBNamespacePrefixMapper (aNamespaceContext);
    setJakartaNamespacePrefixMapper (aMarshaller, aNamespacePrefixMapper);
  }

  /**
   * Set the Jakarta specific namespace prefix mapper.
   *
   * @param aMarshaller
   *        The marshaller to set the property. May not be <code>null</code>.
   * @param aNamespacePrefixMapper
   *        the value to be set
   */
  public static void setJakartaNamespacePrefixMapper (@Nonnull final Marshaller aMarshaller,
                                                      @Nonnull final JAXBNamespacePrefixMapper aNamespacePrefixMapper)
  {
    _setProperty (aMarshaller, JAKARTA_PREFIX_MAPPER, aNamespacePrefixMapper);
  }

  @Nullable
  public static String getJakartaXMLHeaders (@Nonnull final Marshaller aMarshaller)
  {
    return _getStringProperty (aMarshaller, JAKARTA_XML_HEADERS);
  }

  /**
   * Set the Jakarta specific XML header string.
   *
   * @param aMarshaller
   *        The marshaller to set the property. May not be <code>null</code>.
   * @param sXMLHeaders
   *        the value to be set
   */
  public static void setJakartaXMLHeaders (@Nonnull final Marshaller aMarshaller, @Nonnull final String sXMLHeaders)
  {
    _setProperty (aMarshaller, JAKARTA_XML_HEADERS, sXMLHeaders);
  }

  public static boolean isJakartaCanonicalization (@Nonnull final Marshaller aMarshaller)
  {
    return _getBooleanProperty (aMarshaller, JAKARTA_C14N);
  }

  /**
   * Set the Jakarta specific canonicalization property.
   *
   * @param aMarshaller
   *        The marshaller to set the property. May not be <code>null</code>.
   * @param bCanonicalize
   *        the value to be set
   */
  public static void setJakartaCanonicalization (@Nonnull final Marshaller aMarshaller, final boolean bCanonicalize)
  {
    _setProperty (aMarshaller, JAKARTA_C14N, Boolean.valueOf (bCanonicalize));
  }

  public static boolean isJakartaObjectIdentityCycleDetection (@Nonnull final Marshaller aMarshaller)
  {
    return _getBooleanProperty (aMarshaller, JAKARTA_OBJECT_IDENTITY_CYCLE_DETECTION);
  }

  /**
   * Set the Jakarta specific canonicalization property.
   *
   * @param aMarshaller
   *        The marshaller to set the property. May not be <code>null</code>.
   * @param bObjectIdentityCycleDetection
   *        the value to be set
   */
  public static void setJakartaObjectIdentityCycleDetection (@Nonnull final Marshaller aMarshaller,
                                                             final boolean bObjectIdentityCycleDetection)
  {
    _setProperty (aMarshaller, JAKARTA_OBJECT_IDENTITY_CYCLE_DETECTION, Boolean.valueOf (bObjectIdentityCycleDetection));
  }

  /**
   * Check if the passed Marshaller is a Jakarta JAXB marshaller. Use this
   * method to determined, whether the Jakarta specific methods may be invoked
   * or not.
   *
   * @param aMarshaller
   *        The marshaller to be checked. May be <code>null</code>.
   * @return <code>true</code> if the passed marshaller is not <code>null</code>
   *         and is of the Jakarta class.
   */
  public static boolean isJakartaJAXBMarshaller (@Nullable final Marshaller aMarshaller)
  {
    if (aMarshaller == null)
      return false;
    final String sClassName = aMarshaller.getClass ().getName ();
    return sClassName.equals (JAXB_EXTERNAL_CLASS_NAME);
  }
}

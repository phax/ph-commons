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
package com.helger.jaxb;

import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.annotation.style.ReturnsMutableObject;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.helpers.DefaultHandler;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.callback.CallbackList;
import com.helger.commons.callback.exception.IExceptionCallback;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.error.list.ErrorList;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.lang.IHasClassLoader;
import com.helger.commons.state.ESuccess;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.jaxb.builder.JAXBBuilderDefaultSettings;
import com.helger.jaxb.validation.WrappedCollectingValidationEventHandler;
import com.helger.xml.schema.XMLSchemaCache;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.ValidationEventHandler;

/**
 * This is the generic reader and writer base class for JAXB enabled document types.
 *
 * @author Philip Helger
 * @param <JAXBTYPE>
 *        The JAXB type to be marshaled
 */
@NotThreadSafe
public class GenericJAXBMarshaller <JAXBTYPE> implements
                                   IHasClassLoader,
                                   IJAXBReader <JAXBTYPE>,
                                   IJAXBWriter <JAXBTYPE>,
                                   IJAXBValidator <JAXBTYPE>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (GenericJAXBMarshaller.class);

  private final Class <JAXBTYPE> m_aType;
  private final ICommonsList <ClassPathResource> m_aXSDs = new CommonsArrayList <> ();
  private final Function <? super JAXBTYPE, ? extends JAXBElement <JAXBTYPE>> m_aJAXBElementWrapper;
  private ValidationEventHandler m_aEventHandler = JAXBBuilderDefaultSettings.getDefaultValidationEventHandler ();
  private boolean m_bFormattedOutput = JAXBBuilderDefaultSettings.isDefaultFormattedOutput ();
  private NamespaceContext m_aNSContext = JAXBBuilderDefaultSettings.getDefaultNamespaceContext ();
  private Charset m_aCharset = JAXBBuilderDefaultSettings.getDefaultCharset ();
  private String m_sIndentString = JAXBBuilderDefaultSettings.getDefaultIndentString ();
  private boolean m_bUseSchema = JAXBBuilderDefaultSettings.isDefaultUseSchema ();
  private String m_sSchemaLocation = JAXBBuilderDefaultSettings.getDefaultSchemaLocation ();
  private String m_sNoNamespaceSchemaLocation = JAXBBuilderDefaultSettings.getDefaultNoNamespaceSchemaLocation ();
  private boolean m_bUseContextCache = JAXBBuilderDefaultSettings.isDefaultUseContextCache ();
  private WeakReference <ClassLoader> m_aClassLoader;
  private final CallbackList <IExceptionCallback <JAXBException>> m_aReadExceptionCallbacks = new CallbackList <> ();
  private final CallbackList <IExceptionCallback <JAXBException>> m_aWriteExceptionCallbacks = new CallbackList <> ();

  /**
   * Constructor without XSD paths.
   *
   * @param aType
   *        The class of the JAXB document implementation type. May not be <code>null</code>.
   * @param aQName
   *        The qualified name in which the object should be wrapped. May not be <code>null</code>.
   * @since 9.1.5
   * @see #createSimpleJAXBElement(QName, Class)
   */
  public GenericJAXBMarshaller (@Nonnull final Class <JAXBTYPE> aType, @Nonnull final QName aQName)
  {
    this (aType, createSimpleJAXBElement (aQName, aType));
  }

  /**
   * Constructor without XSD paths.
   *
   * @param aType
   *        The class of the JAXB document implementation type. May not be <code>null</code>.
   * @param aWrapper
   *        Wrap the passed domain object into a {@link JAXBElement} for marshalling (writing). This
   *        can usually be done using the respective's package ObjectFactory implementation. May not
   *        be <code>null</code>.
   */
  public GenericJAXBMarshaller (@Nonnull final Class <JAXBTYPE> aType,
                                @Nonnull final Function <? super JAXBTYPE, ? extends JAXBElement <JAXBTYPE>> aWrapper)
  {
    this (aType, null, aWrapper);
  }

  /**
   * Constructor with XSD paths.
   *
   * @param aType
   *        The class of the JAXB document implementation type. May not be <code>null</code>.
   * @param aXSDs
   *        The XSDs used to validate document. May be <code>null</code> or empty indicating, that
   *        no XSD check is needed.
   * @param aJAXBElementWrapper
   *        Wrap the passed domain object into a {@link JAXBElement} for marshalling (writing). This
   *        can usually be done using the respective's package ObjectFactory implementation. May not
   *        be <code>null</code>.
   */
  public GenericJAXBMarshaller (@Nonnull final Class <JAXBTYPE> aType,
                                @Nullable final List <? extends ClassPathResource> aXSDs,
                                @Nonnull final Function <? super JAXBTYPE, ? extends JAXBElement <JAXBTYPE>> aJAXBElementWrapper)
  {
    m_aType = ValueEnforcer.notNull (aType, "Type");
    if (aXSDs != null)
    {
      ValueEnforcer.notEmptyNoNullValue (aXSDs, "XSDs");
      m_aXSDs.addAll (aXSDs);
      for (final ClassPathResource aRes : m_aXSDs)
        ValueEnforcer.isTrue (aRes.hasClassLoader (),
                              () -> "ClassPathResource " + aRes + " should define its ClassLoader for OSGI handling!");
    }

    m_aJAXBElementWrapper = ValueEnforcer.notNull (aJAXBElementWrapper, "JAXBElementWrapper");
    // By default this class loader of the type to be marshaled should be used
    // This is important for OSGI application containers and ANT tasks
    m_aClassLoader = new WeakReference <> (aType.getClassLoader ());
    m_aReadExceptionCallbacks.add (LoggingJAXBReadExceptionHandler.INSTANCE);
    m_aWriteExceptionCallbacks.add (LoggingJAXBWriteExceptionHandler.INSTANCE);
  }

  /**
   * @return The type as passed in the constructor. Never <code>null</code>.
   * @since v9.4.2
   */
  @Nonnull
  protected final Class <JAXBTYPE> getType ()
  {
    return m_aType;
  }

  @Nullable
  public final ClassLoader getClassLoader ()
  {
    return m_aClassLoader.get ();
  }

  /**
   * @return The special JAXB validation event handler to be used. By default
   *         {@link JAXBBuilderDefaultSettings#getDefaultValidationEventHandler()} is used.
   */
  @Nullable
  public final ValidationEventHandler getValidationEventHandler ()
  {
    return m_aEventHandler;
  }

  /**
   * Set the JAXB validation event handler to be used. May be <code>null</code>.
   *
   * @param aEventHandler
   *        The event handler to be used. May be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  public final GenericJAXBMarshaller <JAXBTYPE> setValidationEventHandler (@Nullable final ValidationEventHandler aEventHandler)
  {
    m_aEventHandler = aEventHandler;
    return this;
  }

  /**
   * Special overload of {@link #setValidationEventHandler(ValidationEventHandler)} for the easy
   * version of just collecting the errors and additionally invoking the old validation handler.
   *
   * @param aErrorList
   *        The error list to fill. May not be <code>null</code>.
   * @return this for chaining
   * @since 11.0.0
   */
  @Nonnull
  public final GenericJAXBMarshaller <JAXBTYPE> setCollectErrors (@Nonnull final ErrorList aErrorList)
  {
    ValueEnforcer.notNull (aErrorList, "ErrorList");

    return setValidationEventHandler (new WrappedCollectingValidationEventHandler (aErrorList));
  }

  @Nullable
  public final NamespaceContext getNamespaceContext ()
  {
    return m_aNSContext;
  }

  /**
   * Set the namespace context (prefix to namespace URL mapping) to be used.
   *
   * @param aNSContext
   *        The namespace context to be used. May be <code>null</code>.
   * @return this for chaining
   * @since 8.5.3
   */
  @Nonnull
  public final GenericJAXBMarshaller <JAXBTYPE> setNamespaceContext (@Nullable final NamespaceContext aNSContext)
  {
    m_aNSContext = aNSContext;
    return this;
  }

  public final boolean isFormattedOutput ()
  {
    return m_bFormattedOutput;
  }

  /**
   * Change the way formatting happens when calling write.
   *
   * @param bWriteFormatted
   *        <code>true</code> to write formatted output.
   * @return this for chaining
   * @since 8.5.3
   */
  @Nonnull
  public final GenericJAXBMarshaller <JAXBTYPE> setFormattedOutput (final boolean bWriteFormatted)
  {
    m_bFormattedOutput = bWriteFormatted;
    return this;
  }

  @Nullable
  public final Charset getCharset ()
  {
    return m_aCharset;
  }

  /**
   * Set the charset to be used for writing JAXB objects.
   *
   * @param aCharset
   *        The charset to be used by default. May be <code>null</code>.
   * @return this for chaining
   * @since 8.5.3
   */
  @Nonnull
  public final GenericJAXBMarshaller <JAXBTYPE> setCharset (@Nullable final Charset aCharset)
  {
    m_aCharset = aCharset;
    return this;
  }

  @Nullable
  public final String getIndentString ()
  {
    return m_sIndentString;
  }

  /**
   * Set the indent string to be used for writing JAXB objects.
   *
   * @param sIndentString
   *        The indent string to be used. May be <code>null</code>.
   * @return this for chaining
   * @since 8.5.3
   */
  @Nonnull
  public final GenericJAXBMarshaller <JAXBTYPE> setIndentString (@Nullable final String sIndentString)
  {
    m_sIndentString = sIndentString;
    return this;
  }

  public final boolean isUseSchema ()
  {
    return m_bUseSchema;
  }

  /**
   * Enable or disable the usage of an eventually configured XML Schema.
   *
   * @param bUseSchema
   *        <code>true</code> to use an XML Schema, <code>false</code> to not use it.
   * @return this for chaining
   * @since 11.0.3
   */
  @Nonnull
  public final GenericJAXBMarshaller <JAXBTYPE> setUseSchema (final boolean bUseSchema)
  {
    m_bUseSchema = bUseSchema;
    return this;
  }

  @Nullable
  public final String getSchemaLocation ()
  {
    return m_sSchemaLocation;
  }

  /**
   * Set the schema location to be used for writing JAXB objects.
   *
   * @param sSchemaLocation
   *        The schema location to be used. May be <code>null</code>.
   * @return this for chaining
   * @since 8.6.0
   */
  @Nonnull
  public final GenericJAXBMarshaller <JAXBTYPE> setSchemaLocation (@Nullable final String sSchemaLocation)
  {
    m_sSchemaLocation = sSchemaLocation;
    return this;
  }

  @Nullable
  public final String getNoNamespaceSchemaLocation ()
  {
    return m_sNoNamespaceSchemaLocation;
  }

  /**
   * Set the no namespace schema location to be used for writing JAXB objects.
   *
   * @param sNoNamespaceSchemaLocation
   *        The no namespace schema location to be used. May be <code>null</code>.
   * @return this for chaining
   * @since 9.0.0
   */
  @Nonnull
  public final GenericJAXBMarshaller <JAXBTYPE> setNoNamespaceSchemaLocation (@Nullable final String sNoNamespaceSchemaLocation)
  {
    m_sNoNamespaceSchemaLocation = sNoNamespaceSchemaLocation;
    return this;
  }

  public final boolean isUseContextCache ()
  {
    return m_bUseContextCache;
  }

  /**
   * Change whether the context cache should be used or not. Since creating the JAXB context is
   * quite cost intensive it is recommended to leave it enabled.
   *
   * @param bUseContextCache
   *        <code>true</code> to use it (default), <code>false</code> if not.
   * @return this for chaining
   */
  @Nonnull
  public final GenericJAXBMarshaller <JAXBTYPE> setUseContextCache (final boolean bUseContextCache)
  {
    m_bUseContextCache = bUseContextCache;
    return this;
  }

  /**
   * @return Read exception callbacks. Never <code>null</code>.
   * @since 9.2.2
   */
  @Nonnull
  @ReturnsMutableObject
  public final CallbackList <IExceptionCallback <JAXBException>> readExceptionCallbacks ()
  {
    return m_aReadExceptionCallbacks;
  }

  /**
   * @return Write exception callbacks. Never <code>null</code>.
   * @since 9.2.2
   */
  @Nonnull
  @ReturnsMutableObject
  public final CallbackList <IExceptionCallback <JAXBException>> writeExceptionCallbacks ()
  {
    return m_aWriteExceptionCallbacks;
  }

  /**
   * @return A list of all XSD resources used for validation. Never <code>null</code> but maybe
   *         empty.
   */
  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  public final ICommonsList <ClassPathResource> getOriginalXSDs ()
  {
    return m_aXSDs.getClone ();
  }

  /**
   * @return The validation schema to be used. May be <code>null</code> indicating that no
   *         validation is required.
   * @see #isUseSchema()
   * @see #getOriginalXSDs()
   */
  @Nullable
  @OverrideOnDemand
  protected Schema createValidationSchema ()
  {
    // Schema use enabled?
    if (!m_bUseSchema)
      return null;

    // Any XSD provided?
    if (m_aXSDs.isEmpty ())
      return null;

    return XMLSchemaCache.getInstanceOfClassLoader (getClassLoader ()).getSchema (m_aXSDs);
  }

  /**
   * Create the JAXBContext - cached or uncached.
   *
   * @param aClassLoader
   *        The class loader to be used for XML schema resolving. May be <code>null</code>.
   * @return The created JAXBContext and never <code>null</code>.
   * @throws JAXBException
   *         In case creation fails
   * @since 9.4.2
   */
  @Nonnull
  @OverrideOnDemand
  protected JAXBContext getJAXBContext (@Nullable final ClassLoader aClassLoader) throws JAXBException
  {
    final Package aPackage = m_aType.getPackage ();
    if (m_bUseContextCache)
      return JAXBContextCache.getInstance ()
                             .getFromCache (JAXBContextCacheKey.createForPackage (aPackage, aClassLoader));
    return JAXBContext.newInstance (aPackage.getName (), aClassLoader);
  }

  /**
   * @param aClassLoader
   *        The class loader to be used for XML schema resolving. May be <code>null</code>.
   * @return The JAXB unmarshaller to use. Never <code>null</code>.
   * @throws JAXBException
   *         In case the creation fails.
   */
  @Nonnull
  private Unmarshaller _createUnmarshaller (@Nullable final ClassLoader aClassLoader) throws JAXBException
  {
    final JAXBContext aJAXBContext = getJAXBContext (aClassLoader);

    // create an Unmarshaller
    final Unmarshaller aUnmarshaller = aJAXBContext.createUnmarshaller ();
    if (m_aEventHandler != null)
      aUnmarshaller.setEventHandler (m_aEventHandler);

    // Set XSD (if any)
    final Schema aValidationSchema = createValidationSchema ();
    if (aValidationSchema != null)
      aUnmarshaller.setSchema (aValidationSchema);

    return aUnmarshaller;
  }

  /**
   * Customize the passed unmarshaller before unmarshalling (reading) something.
   *
   * @param aUnmarshaller
   *        The object to customize. Never <code>null</code>.
   */
  @OverrideOnDemand
  protected void customizeUnmarshaller (@Nonnull final Unmarshaller aUnmarshaller)
  {
    // empty
  }

  @Nullable
  public final JAXBTYPE read (@Nonnull final IJAXBUnmarshaller <JAXBTYPE> aHandler)
  {
    ValueEnforcer.notNull (aHandler, "Handler");

    try
    {
      final Unmarshaller aUnmarshaller = _createUnmarshaller (getClassLoader ());
      customizeUnmarshaller (aUnmarshaller);
      return aHandler.doUnmarshal (aUnmarshaller, m_aType).getValue ();
    }
    catch (final JAXBException ex)
    {
      m_aReadExceptionCallbacks.forEach (x -> x.onException (ex));
    }
    return null;
  }

  /**
   * @param aClassLoader
   *        The class loader to be used for XML schema resolving. May be <code>null</code>.
   * @return A marshaller for converting document to XML. Never <code>null</code>.
   * @throws JAXBException
   *         In case of an error.
   */
  @Nonnull
  private Marshaller _createMarshaller (@Nullable final ClassLoader aClassLoader) throws JAXBException
  {
    final JAXBContext aJAXBContext = getJAXBContext (aClassLoader);

    // create an Unmarshaller
    final Marshaller aMarshaller = aJAXBContext.createMarshaller ();
    if (m_aEventHandler != null)
      aMarshaller.setEventHandler (m_aEventHandler);

    if (m_aNSContext != null)
      try
      {
        JAXBMarshallerHelper.setJakartaNamespacePrefixMapper (aMarshaller, m_aNSContext);
      }
      catch (final Exception | NoClassDefFoundError ex)
      {
        // Might be an IllegalArgumentException or a NoClassDefFoundError
        LOGGER.error ("Failed to set the namespace context " +
                      m_aNSContext +
                      ": " +
                      ex.getClass ().getName () +
                      " -- " +
                      ex.getMessage ());
      }

    JAXBMarshallerHelper.setFormattedOutput (aMarshaller, m_bFormattedOutput);

    if (m_aCharset != null)
      JAXBMarshallerHelper.setEncoding (aMarshaller, m_aCharset);

    if (m_sIndentString != null)
      JAXBMarshallerHelper.setJakartaIndentString (aMarshaller, m_sIndentString);

    if (m_sSchemaLocation != null)
      JAXBMarshallerHelper.setSchemaLocation (aMarshaller, m_sSchemaLocation);

    if (m_sNoNamespaceSchemaLocation != null)
      JAXBMarshallerHelper.setNoNamespaceSchemaLocation (aMarshaller, m_sNoNamespaceSchemaLocation);

    // Set XSD (if any)
    final Schema aValidationSchema = createValidationSchema ();
    if (aValidationSchema != null)
      aMarshaller.setSchema (aValidationSchema);

    return aMarshaller;
  }

  /**
   * Customize the passed marshaller before marshalling something.
   *
   * @param aMarshaller
   *        The object to customize. Never <code>null</code>.
   */
  @OverrideOnDemand
  protected void customizeMarshaller (@Nonnull final Marshaller aMarshaller)
  {
    // empty
  }

  @Nonnull
  public final ESuccess write (@Nonnull final JAXBTYPE aObject,
                               @Nonnull final IJAXBMarshaller <JAXBTYPE> aMarshallerFunc)
  {
    ValueEnforcer.notNull (aObject, "Object");
    ValueEnforcer.notNull (aMarshallerFunc, "MarshallerFunc");

    try
    {
      final Marshaller aMarshaller = _createMarshaller (getClassLoader ());
      customizeMarshaller (aMarshaller);

      final JAXBElement <JAXBTYPE> aJAXBElement = m_aJAXBElementWrapper.apply (aObject);

      // Main writing
      aMarshallerFunc.doMarshal (aMarshaller, aJAXBElement);
      return ESuccess.SUCCESS;
    }
    catch (final JAXBException ex)
    {
      m_aWriteExceptionCallbacks.forEach (x -> x.onException (ex));
    }
    return ESuccess.FAILURE;
  }

  /**
   * Customize the passed marshaller before marshalling something.
   *
   * @param aMarshaller
   *        The object to customize. Never <code>null</code>.
   */
  @OverrideOnDemand
  protected void customizeMarshallerForValidation (@Nonnull final Marshaller aMarshaller)
  {
    // empty
  }

  public void validate (@Nonnull final JAXBTYPE aObject, @Nonnull final ErrorList aErrorList)
  {
    ValueEnforcer.notNull (aObject, "Object");
    ValueEnforcer.notNull (aErrorList, "ErrorList");

    final WrappedCollectingValidationEventHandler aEventHandler = new WrappedCollectingValidationEventHandler (aErrorList);
    try
    {
      // create a Marshaller
      final Marshaller aMarshaller = _createMarshaller (getClassLoader ());

      // Overwrite event handler
      aMarshaller.setEventHandler (aEventHandler);

      // Customize on demand
      customizeMarshallerForValidation (aMarshaller);

      if (aMarshaller.getSchema () == null)
        LOGGER.warn ("Running validation on JAXB object of type " +
                     aObject.getClass ().getName () +
                     " makes no sense, because no XML Schema is provided");

      // start marshalling
      final JAXBElement <?> aJAXBElement = m_aJAXBElementWrapper.apply (aObject);

      // DefaultHandler has very little overhead and does nothing
      aMarshaller.marshal (aJAXBElement, new DefaultHandler ());
    }
    catch (final JAXBException ex)
    {
      // Should already be contained as an entry in the event handler
    }
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Type", m_aType)
                                       .append ("XSDs", m_aXSDs)
                                       .append ("JAXBElementWrapper", m_aJAXBElementWrapper)
                                       .append ("EventHandler", m_aEventHandler)
                                       .append ("FormattedOutput", m_bFormattedOutput)
                                       .append ("NSContext", m_aNSContext)
                                       .append ("Charset", m_aCharset)
                                       .append ("IndentString",
                                                m_sIndentString == null ? null : StringHelper.getHexEncoded (
                                                                                                             m_sIndentString,
                                                                                                             StandardCharsets.ISO_8859_1))
                                       .append ("SchemaLocation", m_sSchemaLocation)
                                       .append ("NoNamespaceSchemaLocation", m_sNoNamespaceSchemaLocation)
                                       .append ("UseContextCache", m_bUseContextCache)
                                       .append ("ClassLoader", m_aClassLoader)
                                       .append ("ReadExceptionHandlers", m_aReadExceptionCallbacks)
                                       .append ("WriteExceptionHandlers", m_aWriteExceptionCallbacks)
                                       .getToString ();
  }

  /**
   * Helper function to create a supplier for {@link JAXBElement} objects.
   *
   * @param aQName
   *        QName to use. May not be <code>null</code>.
   * @param aClass
   *        The implementation class to use.
   * @return Never <code>null</code>.
   * @param <T>
   *        the type to wrap
   * @since 9.1.5
   * @see #GenericJAXBMarshaller(Class, QName)
   */
  @Nonnull
  public static <T> Function <T, JAXBElement <T>> createSimpleJAXBElement (@Nonnull final QName aQName,
                                                                           @Nonnull final Class <T> aClass)
  {
    return aValue -> new JAXBElement <> (aQName, aClass, null, aValue);
  }
}

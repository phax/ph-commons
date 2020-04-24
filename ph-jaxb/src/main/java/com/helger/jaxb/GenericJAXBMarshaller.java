/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.callback.CallbackList;
import com.helger.commons.callback.exception.IExceptionCallback;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.functional.IFunction;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.lang.IHasClassLoader;
import com.helger.commons.state.EChange;
import com.helger.commons.state.ESuccess;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.jaxb.builder.JAXBBuilderDefaultSettings;
import com.helger.jaxb.validation.IValidationEventHandlerFactory;
import com.helger.xml.namespace.INamespaceContext;
import com.helger.xml.schema.XMLSchemaCache;

/**
 * This is the generic reader and writer base class for JAXB enabled document
 * types.
 *
 * @author Philip Helger
 * @param <JAXBTYPE>
 *        The JAXB type to be marshaled
 */
@NotThreadSafe
public class GenericJAXBMarshaller <JAXBTYPE> implements IHasClassLoader, IJAXBReader <JAXBTYPE>, IJAXBWriter <JAXBTYPE>
{
  public static final boolean DEFAULT_READ_SECURE = true;

  private static final Logger LOGGER = LoggerFactory.getLogger (GenericJAXBMarshaller.class);

  private final Class <JAXBTYPE> m_aType;
  private final ICommonsList <ClassPathResource> m_aXSDs = new CommonsArrayList <> ();
  private final IFunction <? super JAXBTYPE, ? extends JAXBElement <JAXBTYPE>> m_aJAXBElementWrapper;
  private IValidationEventHandlerFactory m_aVEHFactory;
  private boolean m_bReadSecure = DEFAULT_READ_SECURE;
  private boolean m_bFormattedOutput = JAXBBuilderDefaultSettings.isDefaultFormattedOutput ();
  private INamespaceContext m_aNSContext = JAXBBuilderDefaultSettings.getDefaultNamespaceContext ();
  private Charset m_aCharset = JAXBBuilderDefaultSettings.getDefaultCharset ();
  private String m_sIndentString = JAXBBuilderDefaultSettings.getDefaultIndentString ();
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
   *        The class of the JAXB document implementation type. May not be
   *        <code>null</code>.
   * @param aQName
   *        The qualified name in which the object should be wrapped. May not be
   *        <code>null</code>.
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
   *        The class of the JAXB document implementation type. May not be
   *        <code>null</code>.
   * @param aWrapper
   *        Wrap the passed domain object into a {@link JAXBElement} for
   *        marshalling (writing). This can usually be done using the
   *        respective's package ObjectFactory implementation. May not be
   *        <code>null</code>.
   */
  public GenericJAXBMarshaller (@Nonnull final Class <JAXBTYPE> aType,
                                @Nonnull final IFunction <? super JAXBTYPE, ? extends JAXBElement <JAXBTYPE>> aWrapper)
  {
    this (aType, null, aWrapper);
  }

  /**
   * Constructor with XSD paths.
   *
   * @param aType
   *        The class of the JAXB document implementation type. May not be
   *        <code>null</code>.
   * @param aXSDs
   *        The XSDs used to validate document. May be <code>null</code> or
   *        empty indicating, that no XSD check is needed.
   * @param aJAXBElementWrapper
   *        Wrap the passed domain object into a {@link JAXBElement} for
   *        marshalling (writing). This can usually be done using the
   *        respective's package ObjectFactory implementation. May not be
   *        <code>null</code>.
   */
  public GenericJAXBMarshaller (@Nonnull final Class <JAXBTYPE> aType,
                                @Nullable final List <? extends ClassPathResource> aXSDs,
                                @Nonnull final IFunction <? super JAXBTYPE, ? extends JAXBElement <JAXBTYPE>> aJAXBElementWrapper)
  {
    m_aType = ValueEnforcer.notNull (aType, "Type");
    if (aXSDs != null)
    {
      ValueEnforcer.notEmptyNoNullValue (aXSDs, "XSDs");
      m_aXSDs.addAll (aXSDs);
    }
    for (final ClassPathResource aRes : m_aXSDs)
      ValueEnforcer.isTrue (aRes.hasClassLoader (),
                            () -> "ClassPathResource " + aRes + " should define its ClassLoader for OSGI handling!");

    m_aJAXBElementWrapper = ValueEnforcer.notNull (aJAXBElementWrapper, "JAXBElementWrapper");
    // By default this class loader of the type to be marshaled should be used
    // This is important for OSGI application containers and ANT tasks
    m_aClassLoader = new WeakReference <> (aType.getClassLoader ());
    m_aReadExceptionCallbacks.add (new LoggingJAXBReadExceptionHandler ());
    m_aWriteExceptionCallbacks.add (new LoggingJAXBWriteExceptionHandler ());
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
   * Set a factory to be used to create {@link ValidationEventHandler} objects.
   * By default none is present.
   *
   * @param aVEHFactory
   *        The new factory to be used. May be <code>null</code>.
   */
  public final void setValidationEventHandlerFactory (@Nullable final IValidationEventHandlerFactory aVEHFactory)
  {
    m_aVEHFactory = aVEHFactory;
  }

  /**
   * @return The currently used validation event handler factory. By default
   *         none is used. May be <code>null</code> if explicitly set.
   */
  @Nullable
  public final IValidationEventHandlerFactory getValidationEventHandlerFactory ()
  {
    return m_aVEHFactory;
  }

  public final boolean isReadSecure ()
  {
    return m_bReadSecure;
  }

  /**
   * Enable or disable secure reading. Secure reading means that documents are
   * checked for XXE and XML bombs (infinite entity expansions). By default
   * secure reading is enabled.
   *
   * @param bReadSecure
   *        <code>true</code> to read secure, <code>false</code> to disable
   *        secure reading.
   * @return {@link EChange}
   */
  @Nonnull
  public final EChange setReadSecure (final boolean bReadSecure)
  {
    if (bReadSecure == m_bReadSecure)
      return EChange.UNCHANGED;
    m_bReadSecure = bReadSecure;
    return EChange.CHANGED;
  }

  @Nullable
  public final INamespaceContext getNamespaceContext ()
  {
    return m_aNSContext;
  }

  /**
   * Set the namespace context (prefix to namespace URL mapping) to be used.
   *
   * @param aNSContext
   *        The namespace context to be used. May be <code>null</code>.
   * @return {@link EChange}
   * @since 8.5.3
   */
  @Nonnull
  public final EChange setNamespaceContext (@Nullable final INamespaceContext aNSContext)
  {
    if (EqualsHelper.equals (aNSContext, m_aNSContext))
      return EChange.UNCHANGED;
    m_aNSContext = aNSContext;
    return EChange.CHANGED;
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
   * @return {@link EChange}
   * @since 8.5.3
   */
  @Nonnull
  public final EChange setFormattedOutput (final boolean bWriteFormatted)
  {
    if (bWriteFormatted == m_bFormattedOutput)
      return EChange.UNCHANGED;
    m_bFormattedOutput = bWriteFormatted;
    return EChange.CHANGED;
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
   * @return {@link EChange}
   * @since 8.5.3
   */
  @Nonnull
  public final EChange setCharset (@Nullable final Charset aCharset)
  {
    if (EqualsHelper.equals (aCharset, m_aCharset))
      return EChange.UNCHANGED;
    m_aCharset = aCharset;
    return EChange.CHANGED;
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
   * @return {@link EChange}
   * @since 8.5.3
   */
  @Nonnull
  public final EChange setIndentString (@Nullable final String sIndentString)
  {
    if (EqualsHelper.equals (sIndentString, m_sIndentString))
      return EChange.UNCHANGED;
    m_sIndentString = sIndentString;
    return EChange.CHANGED;
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
   * @return {@link EChange}
   * @since 8.6.0
   */
  @Nonnull
  public final EChange setSchemaLocation (@Nullable final String sSchemaLocation)
  {
    if (EqualsHelper.equals (sSchemaLocation, m_sSchemaLocation))
      return EChange.UNCHANGED;
    m_sSchemaLocation = sSchemaLocation;
    return EChange.CHANGED;
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
   *        The no namespace schema location to be used. May be
   *        <code>null</code>.
   * @return {@link EChange}
   * @since 9.0.0
   */
  @Nonnull
  public final EChange setNoNamespaceSchemaLocation (@Nullable final String sNoNamespaceSchemaLocation)
  {
    if (EqualsHelper.equals (sNoNamespaceSchemaLocation, m_sNoNamespaceSchemaLocation))
      return EChange.UNCHANGED;
    m_sNoNamespaceSchemaLocation = sNoNamespaceSchemaLocation;
    return EChange.CHANGED;
  }

  /**
   * Change whether the context cache should be used or not. Since creating the
   * JAXB context is quite cost intensive it is recommended to leave it enabled.
   *
   * @param bUseContextCache
   *        <code>true</code> to use it (default), <code>false</code> if not.
   * @return {@link EChange}
   */
  @Nonnull
  public final EChange setUseContextCache (final boolean bUseContextCache)
  {
    if (bUseContextCache == m_bUseContextCache)
      return EChange.UNCHANGED;
    m_bUseContextCache = bUseContextCache;
    return EChange.CHANGED;
  }

  public final boolean isUseContextCache ()
  {
    return m_bUseContextCache;
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
   * @return A list of all XSD resources used for validation. Never
   *         <code>null</code>.
   */
  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  public final ICommonsList <ClassPathResource> getOriginalXSDs ()
  {
    return m_aXSDs.getClone ();
  }

  /**
   * @return The validation schema to be used. May be <code>null</code>
   *         indicating that no validation is required.
   */
  @Nullable
  protected Schema createValidationSchema ()
  {
    return m_aXSDs.isEmpty () ? null : XMLSchemaCache.getInstanceOfClassLoader (getClassLoader ()).getSchema (m_aXSDs);
  }

  /**
   * Create the JAXBContext - cached or uncached.
   *
   * @param aClassLoader
   *        The class loader to be used for XML schema resolving. May be
   *        <code>null</code>.
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
      return JAXBContextCache.getInstance ().getFromCache (aPackage, aClassLoader);
    return JAXBContext.newInstance (aPackage.getName (), aClassLoader);
  }

  /**
   * @param aClassLoader
   *        The class loader to be used for XML schema resolving. May be
   *        <code>null</code>.
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
    if (m_aVEHFactory != null)
    {
      // Create and set a new event handler
      final ValidationEventHandler aEvHdl = m_aVEHFactory.apply (aUnmarshaller.getEventHandler ());
      if (aEvHdl != null)
        aUnmarshaller.setEventHandler (aEvHdl);
    }

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
   *        The class loader to be used for XML schema resolving. May be
   *        <code>null</code>.
   * @return A marshaller for converting document to XML. Never
   *         <code>null</code>.
   * @throws JAXBException
   *         In case of an error.
   */
  @Nonnull
  private Marshaller _createMarshaller (@Nullable final ClassLoader aClassLoader) throws JAXBException
  {
    final JAXBContext aJAXBContext = getJAXBContext (aClassLoader);

    // create an Unmarshaller
    final Marshaller aMarshaller = aJAXBContext.createMarshaller ();
    if (m_aVEHFactory != null)
    {
      // Create and set the event handler
      final ValidationEventHandler aEvHdl = m_aVEHFactory.apply (aMarshaller.getEventHandler ());
      if (aEvHdl != null)
        aMarshaller.setEventHandler (aEvHdl);
    }

    if (m_aNSContext != null)
      try
      {
        JAXBMarshallerHelper.setSunNamespacePrefixMapper (aMarshaller, m_aNSContext);
      }
      catch (final Exception ex)
      {
        // Might be an IllegalArgumentException or a NoClassDefFoundError
        LOGGER.error ("Failed to set the namespace context " +
                      m_aNSContext +
                      ": " +
                      ex.getClass ().getName () +
                      " -- " +
                      ex.getMessage (),
                      GlobalDebug.isDebugMode () ? ex.getCause () : null);
      }

    JAXBMarshallerHelper.setFormattedOutput (aMarshaller, m_bFormattedOutput);

    if (m_aCharset != null)
      JAXBMarshallerHelper.setEncoding (aMarshaller, m_aCharset);

    if (m_sIndentString != null)
      JAXBMarshallerHelper.setSunIndentString (aMarshaller, m_sIndentString);

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
      aMarshallerFunc.doMarshal (aMarshaller, aJAXBElement);
      return ESuccess.SUCCESS;
    }
    catch (final JAXBException ex)
    {
      m_aWriteExceptionCallbacks.forEach (x -> x.onException (ex));
    }
    return ESuccess.FAILURE;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Type", m_aType)
                                       .append ("XSDs", m_aXSDs)
                                       .append ("JAXBElementWrapper", m_aJAXBElementWrapper)
                                       .append ("VEHFactory", m_aVEHFactory)
                                       .append ("ReadSecure", m_bReadSecure)
                                       .append ("FormattedOutput", m_bFormattedOutput)
                                       .append ("NSContext", m_aNSContext)
                                       .append ("Charset", m_aCharset)
                                       .append ("IndentString",
                                                StringHelper.getHexEncoded (m_sIndentString,
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
  public static <T> IFunction <T, JAXBElement <T>> createSimpleJAXBElement (@Nonnull final QName aQName,
                                                                            @Nonnull final Class <T> aClass)
  {
    return aValue -> new JAXBElement <> (aQName, aClass, null, aValue);
  }
}

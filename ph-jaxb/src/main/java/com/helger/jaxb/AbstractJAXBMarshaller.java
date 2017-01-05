/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.namespace.NamespaceContext;
import javax.xml.validation.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXParseException;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.lang.IHasClassLoader;
import com.helger.commons.state.EChange;
import com.helger.commons.state.ESuccess;
import com.helger.jaxb.builder.JAXBBuilderDefaultSettings;
import com.helger.jaxb.validation.CollectingLoggingValidationEventHandlerFactory;
import com.helger.jaxb.validation.IValidationEventHandlerFactory;
import com.helger.xml.schema.XMLSchemaCache;

/**
 * This is the abstract reader and writer base class for JAXB enabled document
 * types.
 *
 * @author Philip Helger
 * @param <JAXBTYPE>
 *        The JAXB type to be marshaled
 */
@NotThreadSafe
public abstract class AbstractJAXBMarshaller <JAXBTYPE>
                                             implements IHasClassLoader, IJAXBReader <JAXBTYPE>, IJAXBWriter <JAXBTYPE>
{
  public static final boolean DEFAULT_READ_SECURE = true;
  @Deprecated
  public static final boolean DEFAULT_WRITE_FORMATTED = JAXBBuilderDefaultSettings.DEFAULT_FORMATTED_OUTPUT;
  @Deprecated
  public static final boolean DEFAULT_USE_CONTEXT_CACHE = JAXBBuilderDefaultSettings.DEFAULT_USE_CONTEXT_CACHE;

  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractJAXBMarshaller.class);

  private final Class <JAXBTYPE> m_aType;
  private final ICommonsList <IReadableResource> m_aXSDs = new CommonsArrayList<> ();
  private final Function <JAXBTYPE, JAXBElement <JAXBTYPE>> m_aWrapper;
  private IValidationEventHandlerFactory m_aVEHFactory = new CollectingLoggingValidationEventHandlerFactory ();
  private boolean m_bReadSecure = DEFAULT_READ_SECURE;
  private boolean m_bFormattedOutput = JAXBBuilderDefaultSettings.DEFAULT_FORMATTED_OUTPUT;
  private NamespaceContext m_aNSContext;
  private Charset m_aCharset;
  private String m_sIndentString;
  private boolean m_bUseContextCache = JAXBBuilderDefaultSettings.DEFAULT_USE_CONTEXT_CACHE;
  private ClassLoader m_aClassLoader;

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
  protected AbstractJAXBMarshaller (@Nonnull final Class <JAXBTYPE> aType,
                                    @Nonnull final Function <JAXBTYPE, JAXBElement <JAXBTYPE>> aWrapper)
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
   * @param aWrapper
   *        Wrap the passed domain object into a {@link JAXBElement} for
   *        marshalling (writing). This can usually be done using the
   *        respective's package ObjectFactory implementation. May not be
   *        <code>null</code>.
   */
  protected AbstractJAXBMarshaller (@Nonnull final Class <JAXBTYPE> aType,
                                    @Nullable final List <? extends IReadableResource> aXSDs,
                                    @Nonnull final Function <JAXBTYPE, JAXBElement <JAXBTYPE>> aWrapper)
  {
    m_aType = ValueEnforcer.notNull (aType, "Type");
    if (aXSDs != null)
    {
      ValueEnforcer.notEmptyNoNullValue (aXSDs, "XSDs");
      m_aXSDs.addAll (aXSDs);
    }
    m_aWrapper = ValueEnforcer.notNull (aWrapper, "Wrapper");
  }

  /**
   * Set the class loader to be used for XSD schema validation. This method is
   * optional.
   *
   * @param aClassLoader
   *        The class loader to be used. May be <code>null</code>.
   */
  public final void setClassLoader (@Nullable final ClassLoader aClassLoader)
  {
    m_aClassLoader = aClassLoader;
  }

  @Nullable
  public final ClassLoader getClassLoader ()
  {
    return m_aClassLoader;
  }

  /**
   * Set another factory to be used to create {@link ValidationEventHandler}
   * objects. By default a
   * {@link CollectingLoggingValidationEventHandlerFactory} is used.
   *
   * @param aVEHFactory
   *        The new factory to be used. May be <code>null</code>.
   */
  public final void setValidationEventHandlerFactory (@Nullable final IValidationEventHandlerFactory aVEHFactory)
  {
    m_aVEHFactory = aVEHFactory;
  }

  /**
   * @return The currently used validation event handler factory. By default an
   *         instance of {@link CollectingLoggingValidationEventHandlerFactory}
   *         is used. May be <code>null</code> if explicitly set.
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
  public final NamespaceContext getNamespaceContext ()
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
  public EChange setNamespaceContext (@Nullable final NamespaceContext aNSContext)
  {
    if (EqualsHelper.equals (aNSContext, m_aNSContext))
      return EChange.UNCHANGED;
    m_aNSContext = aNSContext;
    return EChange.CHANGED;
  }

  /**
   * @return <code>true</code> if the JAXB output should be formatted. Default
   *         is <code>false</code>.
   * @deprecated Use {@link #isFormattedOutput()} instead
   */
  @Deprecated
  public final boolean isWriteFormatted ()
  {
    return isFormattedOutput ();
  }

  /**
   * Change the way formatting happens when calling write.
   *
   * @param bWriteFormatted
   *        <code>true</code> to write formatted output.
   * @return {@link EChange}
   * @deprecated Use {@link #setFormattedOutput(boolean)} instead
   */
  @Deprecated
  @Nonnull
  public final EChange setWriteFormatted (final boolean bWriteFormatted)
  {
    return setFormattedOutput (bWriteFormatted);
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
  public Charset getCharset ()
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
  public EChange setCharset (@Nullable final Charset aCharset)
  {
    if (EqualsHelper.equals (aCharset, m_aCharset))
      return EChange.UNCHANGED;
    m_aCharset = aCharset;
    return EChange.CHANGED;
  }

  @Nullable
  public String getIndentString ()
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
  public EChange setIndentString (@Nullable final String sIndentString)
  {
    if (EqualsHelper.equals (sIndentString, m_sIndentString))
      return EChange.UNCHANGED;
    m_sIndentString = sIndentString;
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
   * @return A list of all XSD resources used for validation. Never
   *         <code>null</code>.
   */
  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  public final ICommonsList <IReadableResource> getOriginalXSDs ()
  {
    return m_aXSDs.getClone ();
  }

  /**
   * @return The validation schema to be used. May be <code>null</code>
   *         indicating that no validation is required.
   */
  @Nullable
  @OverrideOnDemand
  protected Schema createValidationSchema ()
  {
    return m_aXSDs.isEmpty () ? null : XMLSchemaCache.getInstanceOfClassLoader (m_aClassLoader).getSchema (m_aXSDs);
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
    final Package aPackage = m_aType.getPackage ();
    final JAXBContext aJAXBContext = m_bUseContextCache ? JAXBContextCache.getInstance ().getFromCache (aPackage,
                                                                                                        aClassLoader)
                                                        : JAXBContext.newInstance (aPackage.getName (), aClassLoader);

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

  @OverrideOnDemand
  protected void handleReadException (@Nonnull final JAXBException ex)
  {
    if (ex instanceof UnmarshalException)
    {
      // The JAXB specification does not mandate how the JAXB provider
      // must behave when attempting to unmarshal invalid XML data. In
      // those cases, the JAXB provider is allowed to terminate the
      // call to unmarshal with an UnmarshalException.
      final Throwable aLinked = ((UnmarshalException) ex).getLinkedException ();
      if (aLinked instanceof SAXParseException)
        s_aLogger.error ("Failed to parse XML document: " + aLinked.getMessage ());
      else
        s_aLogger.error ("Unmarshal exception reading document", ex);
    }
    else
      s_aLogger.warn ("JAXB Exception reading document", ex);
  }

  @Nullable
  public final JAXBTYPE read (@Nonnull final IJAXBUnmarshaller <JAXBTYPE> aHandler)
  {
    ValueEnforcer.notNull (aHandler, "Handler");

    try
    {
      final Unmarshaller aUnmarshaller = _createUnmarshaller (m_aClassLoader);
      customizeUnmarshaller (aUnmarshaller);
      return aHandler.doUnmarshal (aUnmarshaller, m_aType).getValue ();
    }
    catch (final JAXBException ex)
    {
      handleReadException (ex);
    }
    return null;
  }

  /**
   * @return A marshaller for converting document to XML. Never
   *         <code>null</code>.
   * @throws JAXBException
   *         In case of an error.
   */
  @Nonnull
  private Marshaller _createMarshaller () throws JAXBException
  {
    final Package aPackage = m_aType.getPackage ();
    final JAXBContext aJAXBContext = m_bUseContextCache ? JAXBContextCache.getInstance ().getFromCache (aPackage,
                                                                                                        m_aClassLoader)
                                                        : JAXBContext.newInstance (aPackage.getName (), m_aClassLoader);

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
      catch (final Throwable t)
      {
        // Might be an IllegalArgumentException or a NoClassDefFoundError
        s_aLogger.error ("Failed to set the namespace context " +
                         m_aNSContext +
                         ": " +
                         t.getClass ().getName () +
                         " -- " +
                         t.getMessage (),
                         GlobalDebug.isDebugMode () ? t.getCause () : null);
      }

    JAXBMarshallerHelper.setFormattedOutput (aMarshaller, m_bFormattedOutput);

    if (m_aCharset != null)
      JAXBMarshallerHelper.setEncoding (aMarshaller, m_aCharset);

    if (m_sIndentString != null)
      JAXBMarshallerHelper.setSunIndentString (aMarshaller, m_sIndentString);

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

  @OverrideOnDemand
  protected void handleWriteException (@Nonnull final JAXBException ex)
  {
    if (ex instanceof MarshalException)
      s_aLogger.error ("Marshal exception writing object", ex);
    else
      s_aLogger.warn ("JAXB Exception writing object", ex);
  }

  @Nonnull
  public final ESuccess write (@Nonnull final JAXBTYPE aObject,
                               @Nonnull final IJAXBMarshaller <JAXBTYPE> aMarshallerFunc)
  {
    ValueEnforcer.notNull (aObject, "Object");
    ValueEnforcer.notNull (aMarshallerFunc, "MarshallerFunc");

    try
    {
      final Marshaller aMarshaller = _createMarshaller ();
      customizeMarshaller (aMarshaller);

      final JAXBElement <JAXBTYPE> aJAXBElement = m_aWrapper.apply (aObject);
      aMarshallerFunc.doMarshal (aMarshaller, aJAXBElement);
      return ESuccess.SUCCESS;
    }
    catch (final JAXBException ex)
    {
      handleWriteException (ex);
    }
    return ESuccess.FAILURE;
  }
}

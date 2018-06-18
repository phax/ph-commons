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
package com.helger.jaxb.builder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationEventHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.state.ESuccess;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.jaxb.IJAXBWriter;
import com.helger.jaxb.JAXBMarshallerHelper;
import com.helger.jaxb.validation.LoggingValidationEventHandler;
import com.helger.xml.namespace.INamespaceContext;

/**
 * Builder class for writing JAXB documents.
 *
 * @author Philip Helger
 * @param <JAXBTYPE>
 *        The JAXB implementation class to be written
 * @param <IMPLTYPE>
 *        The implementation class implementing this abstract class.
 */
@NotThreadSafe
public class JAXBWriterBuilder <JAXBTYPE, IMPLTYPE extends JAXBWriterBuilder <JAXBTYPE, IMPLTYPE>> extends
                               AbstractWritingJAXBBuilder <JAXBTYPE, IMPLTYPE> implements
                               IJAXBWriter <JAXBTYPE>
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (JAXBWriterBuilder.class);

  private ValidationEventHandler m_aEventHandler = JAXBBuilderDefaultSettings.getDefaultValidationEventHandler ();
  private INamespaceContext m_aNSContext = JAXBBuilderDefaultSettings.getDefaultNamespaceContext ();
  private boolean m_bFormattedOutput = JAXBBuilderDefaultSettings.isDefaultFormattedOutput ();
  private Charset m_aCharset = JAXBBuilderDefaultSettings.getDefaultCharset ();
  private String m_sIndentString = JAXBBuilderDefaultSettings.getDefaultIndentString ();
  private String m_sSchemaLocation = JAXBBuilderDefaultSettings.getDefaultSchemaLocation ();
  private String m_sNoNamespaceSchemaLocation = JAXBBuilderDefaultSettings.getDefaultNoNamespaceSchemaLocation ();

  public JAXBWriterBuilder (@Nonnull final IJAXBDocumentType aDocType)
  {
    super (aDocType);
  }

  /**
   * @return The special JAXB validation event handler to be used. By default
   *         {@link JAXBBuilderDefaultSettings#getDefaultValidationEventHandler()}
   *         is used.
   */
  @Nullable
  public ValidationEventHandler getValidationEventHandler ()
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
  public final IMPLTYPE setValidationEventHandler (@Nullable final ValidationEventHandler aEventHandler)
  {
    m_aEventHandler = aEventHandler;
    return thisAsT ();
  }

  /**
   * @return The special JAXB namespace context to be used. By default
   *         {@link JAXBBuilderDefaultSettings#getDefaultNamespaceContext()} is
   *         used.
   */
  @Nullable
  public INamespaceContext getNamespaceContext ()
  {
    return m_aNSContext;
  }

  /**
   * Set the namespace context (prefix to namespace URL mapping) to be used.
   *
   * @param aNSContext
   *        The namespace context to be used. May be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  public final IMPLTYPE setNamespaceContext (@Nullable final INamespaceContext aNSContext)
  {
    m_aNSContext = aNSContext;
    return thisAsT ();
  }

  public boolean isFormattedOutput ()
  {
    return m_bFormattedOutput;
  }

  /**
   * Enable or disable the formatting of the output.
   *
   * @param bFormattedOutput
   *        <code>true</code> to enable it, <code>false</code> to disable it.
   * @return this for chaining
   */
  @Nonnull
  public final IMPLTYPE setFormattedOutput (final boolean bFormattedOutput)
  {
    m_bFormattedOutput = bFormattedOutput;
    return thisAsT ();
  }

  /**
   * @return The special JAXB Charset to be used for writing. <code>null</code>
   *         by default.
   */
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
   * @return this for chaining
   */
  @Nonnull
  public final IMPLTYPE setCharset (@Nullable final Charset aCharset)
  {
    m_aCharset = aCharset;
    return thisAsT ();
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
   * @return this for chaining
   */
  @Nonnull
  public final IMPLTYPE setIndentString (@Nullable final String sIndentString)
  {
    m_sIndentString = sIndentString;
    return thisAsT ();
  }

  @Nullable
  public String getSchemaLocation ()
  {
    return m_sSchemaLocation;
  }

  /**
   * Set the Schema Location to be used for writing JAXB objects.
   *
   * @param sSchemaLocation
   *        The Schema Location to be used. May be <code>null</code>.
   * @return this for chaining
   * @since 8.6.0
   */
  @Nonnull
  public final IMPLTYPE setSchemaLocation (@Nullable final String sSchemaLocation)
  {
    m_sSchemaLocation = sSchemaLocation;
    return thisAsT ();
  }

  @Nullable
  public String getNoNamespaceSchemaLocation ()
  {
    return m_sNoNamespaceSchemaLocation;
  }

  /**
   * Set the no namespace Schema Location to be used for writing JAXB objects.
   *
   * @param sNoNamespaceSchemaLocation
   *        The no namespace Schema Location to be used. May be
   *        <code>null</code>.
   * @return this for chaining
   * @since 9.0.0
   */
  @Nonnull
  public final IMPLTYPE setNoNamespaceSchemaLocation (@Nullable final String sNoNamespaceSchemaLocation)
  {
    m_sNoNamespaceSchemaLocation = sNoNamespaceSchemaLocation;
    return thisAsT ();
  }

  @Override
  @Nonnull
  protected Marshaller createMarshaller () throws JAXBException
  {
    final Marshaller aMarshaller = super.createMarshaller ();

    if (m_aEventHandler != null)
      aMarshaller.setEventHandler (m_aEventHandler);
    else
      aMarshaller.setEventHandler (new LoggingValidationEventHandler ().andThen (aMarshaller.getEventHandler ()));

    if (m_aNSContext != null)
      try
      {
        JAXBMarshallerHelper.setSunNamespacePrefixMapper (aMarshaller, m_aNSContext);
      }
      catch (final Exception ex)
      {
        // Might be an IllegalArgumentException or a NoClassDefFoundError
        if (s_aLogger.isErrorEnabled ())
          s_aLogger.error ("Failed to set the namespace context " +
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

    return aMarshaller;
  }

  @Nonnull
  public ESuccess write (@Nonnull final JAXBTYPE aJAXBDocument,
                         @Nonnull final IJAXBMarshaller <JAXBTYPE> aMarshallerFunc)
  {
    ValueEnforcer.notNull (aJAXBDocument, "JAXBDocument");
    ValueEnforcer.notNull (aMarshallerFunc, "MarshallerFunc");

    // Avoid class cast exception later on
    if (!m_aDocType.getImplementationClass ().getPackage ().equals (aJAXBDocument.getClass ().getPackage ()))
    {
      if (s_aLogger.isErrorEnabled ())
        s_aLogger.error ("You cannot write a '" +
                         aJAXBDocument.getClass () +
                         "' as a " +
                         m_aDocType.getImplementationClass ().getPackage ().getName ());
      return ESuccess.FAILURE;
    }

    try
    {
      final Marshaller aMarshaller = createMarshaller ();

      // Customize on demand
      final Consumer <? super Marshaller> aCustomizer = getMarshallerCustomizer ();
      if (aCustomizer != null)
        aCustomizer.accept (aMarshaller);

      // start marshalling
      final JAXBElement <JAXBTYPE> aJAXBElement = createJAXBElement (aJAXBDocument);
      aMarshallerFunc.doMarshal (aMarshaller, aJAXBElement);
      return ESuccess.SUCCESS;
    }
    catch (final JAXBException ex)
    {
      exceptionCallbacks ().forEach (x -> x.onException (ex));
    }
    return ESuccess.FAILURE;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("EventHandler", m_aEventHandler)
                            .append ("NamespaceContext", m_aNSContext)
                            .append ("FormattedOutput", m_bFormattedOutput)
                            .append ("Charset", m_aCharset)
                            .append ("IndentString",
                                     StringHelper.getHexEncoded (m_sIndentString, StandardCharsets.ISO_8859_1))
                            .append ("SchemaLocation", m_sSchemaLocation)
                            .append ("NoNamespaceSchemaLocation", m_sNoNamespaceSchemaLocation)
                            .getToString ();
  }
}

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
package com.helger.jaxb.builder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.state.ESuccess;
import com.helger.commons.string.ToStringGenerator;
import com.helger.jaxb.IJAXBWriter;
import com.helger.jaxb.JAXBMarshallerHelper;
import com.helger.jaxb.validation.LoggingValidationEventHandler;

/**
 * Abstract builder class for writing JAXB documents.
 *
 * @author Philip Helger
 * @param <JAXBTYPE>
 *        The JAXB implementation class to be written
 * @param <IMPLTYPE>
 *        The implementation class implementing this abstract class.
 */
@NotThreadSafe
public abstract class AbstractJAXBWriterBuilder <JAXBTYPE, IMPLTYPE extends AbstractJAXBWriterBuilder <JAXBTYPE, IMPLTYPE>>
                                                extends AbstractWritingJAXBBuilder <JAXBTYPE, IMPLTYPE>
                                                implements IJAXBWriter <JAXBTYPE>
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractJAXBWriterBuilder.class);

  protected ValidationEventHandler m_aEventHandler = JAXBBuilderDefaultSettings.getDefaultValidationEventHandler ();
  protected NamespaceContext m_aNSContext = JAXBBuilderDefaultSettings.getDefaultNamespaceContext ();

  public AbstractJAXBWriterBuilder (@Nonnull final IJAXBDocumentType aDocType)
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
  public IMPLTYPE setValidationEventHandler (@Nullable final ValidationEventHandler aEventHandler)
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
  public NamespaceContext getNamespaceContext ()
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
  public IMPLTYPE setNamespaceContext (@Nullable final NamespaceContext aNSContext)
  {
    m_aNSContext = aNSContext;
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
      aMarshaller.setEventHandler (new LoggingValidationEventHandler (aMarshaller.getEventHandler ()));

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

    return aMarshaller;
  }

  /**
   * Convert the passed JAXB document to a custom {@link Result}.
   *
   * @param aJAXBDocument
   *        The source object to write. May not be <code>null</code>.
   * @param aResult
   *        The result object to write to. May not be <code>null</code>.
   * @return {@link ESuccess#SUCCESS} in case of success,
   *         {@link ESuccess#FAILURE} in case of an error
   */
  @Nonnull
  public ESuccess write (@Nonnull final JAXBTYPE aJAXBDocument, @Nonnull final Result aResult)
  {
    ValueEnforcer.notNull (aJAXBDocument, "JAXBDocument");
    ValueEnforcer.notNull (aResult, "Result");

    // Avoid class cast exception later on
    if (!m_aDocType.getPackage ().equals (aJAXBDocument.getClass ().getPackage ()))
    {
      s_aLogger.error ("You cannot write a '" +
                       aJAXBDocument.getClass () +
                       "' as a " +
                       m_aDocType.getPackage ().getName ());
      return ESuccess.FAILURE;
    }

    try
    {
      final Marshaller aMarshaller = createMarshaller ();

      // Customize on demand
      customizeMarshaller (aMarshaller);

      // start marshalling
      final JAXBElement <?> aJAXBElement = _createJAXBElement (m_aDocType.getQName (), aJAXBDocument);
      aMarshaller.marshal (aJAXBElement, aResult);
      return ESuccess.SUCCESS;
    }
    catch (final JAXBException ex)
    {
      handleWriteException (ex);
    }
    return ESuccess.FAILURE;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("EventHandler", m_aEventHandler)
                            .append ("NamespaceContext", m_aNSContext)
                            .toString ();
  }
}

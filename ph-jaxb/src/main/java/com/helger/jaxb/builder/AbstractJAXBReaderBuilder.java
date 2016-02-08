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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXParseException;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.xml.XMLHelper;
import com.helger.jaxb.IJAXBReader;
import com.helger.jaxb.validation.LoggingValidationEventHandler;

/**
 * Abstract builder class for reading JAXB documents.
 *
 * @author Philip Helger
 * @param <JAXBTYPE>
 *        The JAXB implementation class to be read
 * @param <IMPLTYPE>
 *        The implementation class implementing this abstract class.
 */
@NotThreadSafe
public class AbstractJAXBReaderBuilder <JAXBTYPE, IMPLTYPE extends AbstractJAXBReaderBuilder <JAXBTYPE, IMPLTYPE>>
                                       extends AbstractJAXBBuilder <IMPLTYPE> implements IJAXBReader <JAXBTYPE>
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractJAXBReaderBuilder.class);

  private final Class <JAXBTYPE> m_aImplClass;
  private ValidationEventHandler m_aEventHandler = JAXBBuilderDefaultSettings.getDefaultValidationEventHandler ();

  public AbstractJAXBReaderBuilder (@Nonnull final IJAXBDocumentType aDocType)
  {
    this (aDocType, GenericReflection.uncheckedCast (aDocType.getImplementationClass ()));
  }

  public AbstractJAXBReaderBuilder (@Nonnull final IJAXBDocumentType aDocType,
                                    @Nonnull final Class <JAXBTYPE> aImplClass)
  {
    super (aDocType);
    m_aImplClass = ValueEnforcer.notNull (aImplClass, "ImplClass");
  }

  @Nonnull
  protected final Class <JAXBTYPE> getImplClass ()
  {
    return m_aImplClass;
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
   * @return this
   */
  @Nonnull
  public IMPLTYPE setValidationEventHandler (@Nullable final ValidationEventHandler aEventHandler)
  {
    m_aEventHandler = aEventHandler;
    return thisAsT ();
  }

  public final boolean isReadSecure ()
  {
    return true;
  }

  @Nonnull
  protected Unmarshaller createUnmarshaller () throws JAXBException
  {
    final JAXBContext aJAXBContext = getJAXBContext ();

    // create an Unmarshaller
    final Unmarshaller aUnmarshaller = aJAXBContext.createUnmarshaller ();
    if (m_aEventHandler != null)
      aUnmarshaller.setEventHandler (m_aEventHandler);
    else
      aUnmarshaller.setEventHandler (new LoggingValidationEventHandler (aUnmarshaller.getEventHandler ()));

    // Validating (if possible)
    final Schema aSchema = getSchema ();
    if (aSchema != null)
      aUnmarshaller.setSchema (aSchema);

    return aUnmarshaller;
  }

  /**
   * Customize the unmarshaller
   *
   * @param aUnmarshaller
   *        The unmarshaller to customize. Never <code>null</code>.
   */
  @OverrideOnDemand
  protected void customizeUnmarshaller (@Nonnull final Unmarshaller aUnmarshaller)
  {}

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

  /**
   * Convert the passed XML node into a domain object.<br>
   * Note: this is the generic API for reading all types of JAXB documents.
   *
   * @param aNode
   *        The XML node to be converted. May not be <code>null</code>.
   * @return <code>null</code> in case conversion to the specified class failed.
   *         See the log output for details.
   */
  @Nullable
  public JAXBTYPE read (@Nonnull final Node aNode)
  {
    ValueEnforcer.notNull (aNode, "Node");

    // Convert null to empty string to be comparable to the m_aDocType
    // implementation
    final String sNodeNamespaceURI = StringHelper.getNotNull (XMLHelper.getNamespaceURI (aNode));

    // Avoid class cast exception later on
    if (!m_aDocType.getNamespaceURI ().equals (sNodeNamespaceURI))
    {
      s_aLogger.error ("You cannot read a node with a namespace URI of '" +
                       sNodeNamespaceURI +
                       "' as a " +
                       m_aImplClass.getName ());
      return null;
    }

    JAXBTYPE ret = null;
    try
    {
      final Unmarshaller aUnmarshaller = createUnmarshaller ();

      // Customize on demand
      customizeUnmarshaller (aUnmarshaller);

      // start unmarshalling
      ret = aUnmarshaller.unmarshal (aNode, m_aImplClass).getValue ();
      if (ret == null)
        throw new IllegalStateException ("Failed to read JAXB document of class " +
                                         m_aImplClass.getName () +
                                         " - without exception!");
    }
    catch (final JAXBException ex)
    {
      handleReadException (ex);
    }

    return ret;
  }

  /**
   * Interpret the passed {@link Source} as a JAXB document.<br>
   * Note: this is the generic API for reading all types of JAXB documents.
   *
   * @param aSource
   *        The source to read from. May not be <code>null</code>.
   * @return The evaluated JAXB document or <code>null</code> in case of a
   *         parsing error
   */
  @Nullable
  public JAXBTYPE read (@Nonnull final Source aSource)
  {
    ValueEnforcer.notNull (aSource, "Source");

    // as we don't have a node, we need to trust the implementation class
    final Schema aSchema = getSchema ();
    if (aSchema == null)
    {
      s_aLogger.error ("Don't know how to read JAXB document of type " + m_aImplClass.getName ());
      return null;
    }

    JAXBTYPE ret = null;
    try
    {
      final Unmarshaller aUnmarshaller = createUnmarshaller ();

      // Customize on demand
      customizeUnmarshaller (aUnmarshaller);

      // start unmarshalling
      ret = aUnmarshaller.unmarshal (aSource, m_aImplClass).getValue ();
      if (ret == null)
        throw new IllegalStateException ("Failed to read JAXB document of class " +
                                         m_aImplClass.getName () +
                                         " - without exception!");
    }
    catch (final JAXBException ex)
    {
      handleReadException (ex);
    }

    return ret;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("ImplClass", m_aImplClass)
                            .append ("EventHandler", m_aEventHandler)
                            .toString ();
  }
}

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
package com.helger.jaxb.builder;

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.validation.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.string.ToStringGenerator;
import com.helger.jaxb.IJAXBReader;
import com.helger.jaxb.IJAXBUnmarshaller;
import com.helger.jaxb.LoggingJAXBReadExceptionHandler;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.ValidationEventHandler;

/**
 * Builder class for reading JAXB documents.
 *
 * @author Philip Helger
 * @param <JAXBTYPE>
 *        The JAXB implementation class to be read
 * @param <IMPLTYPE>
 *        The implementation class implementing this abstract class.
 * @deprecated Since v11 - use the GenericJAXBMarshaller instead - it deals with
 *             all of the stuff in one class. Will be removed in v12
 */
@NotThreadSafe
@Deprecated (forRemoval = true, since = "11.0.0")
public class JAXBReaderBuilder <JAXBTYPE, IMPLTYPE extends JAXBReaderBuilder <JAXBTYPE, IMPLTYPE>> extends
                               AbstractJAXBBuilder <IMPLTYPE> implements
                               IJAXBReader <JAXBTYPE>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (JAXBReaderBuilder.class);

  private final Class <JAXBTYPE> m_aImplClass;
  private ValidationEventHandler m_aEventHandler = JAXBBuilderDefaultSettings.getDefaultValidationEventHandler ();
  private Consumer <? super Unmarshaller> m_aUnmarshallerCustomizer;

  public JAXBReaderBuilder (@Nonnull final IJAXBDocumentType aDocType)
  {
    this (aDocType, GenericReflection.uncheckedCast (aDocType.getImplementationClass ()));
  }

  public JAXBReaderBuilder (@Nonnull final IJAXBDocumentType aDocType, @Nonnull final Class <JAXBTYPE> aImplClass)
  {
    super (aDocType);
    m_aImplClass = ValueEnforcer.notNull (aImplClass, "ImplClass");
    exceptionCallbacks ().add (new LoggingJAXBReadExceptionHandler ());
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
  public final ValidationEventHandler getValidationEventHandler ()
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
  public final IMPLTYPE setValidationEventHandler (@Nullable final ValidationEventHandler aEventHandler)
  {
    m_aEventHandler = aEventHandler;
    return thisAsT ();
  }

  @Nullable
  public final Consumer <? super Unmarshaller> getUnmarshallerCustomizer ()
  {
    return m_aUnmarshallerCustomizer;
  }

  @Nonnull
  public final IMPLTYPE setUnmarshallerCustomizer (@Nullable final Consumer <? super Unmarshaller> aUnmarshallerCustomizer)
  {
    m_aUnmarshallerCustomizer = aUnmarshallerCustomizer;
    return thisAsT ();
  }

  @Nonnull
  protected Unmarshaller createUnmarshaller () throws JAXBException
  {
    final JAXBContext aJAXBContext = getJAXBContext ();

    // create an Unmarshaller
    final Unmarshaller aUnmarshaller = aJAXBContext.createUnmarshaller ();
    if (m_aEventHandler != null)
      aUnmarshaller.setEventHandler (m_aEventHandler);

    // Validating (if possible)
    final Schema aSchema = getSchema ();
    if (aSchema != null)
      aUnmarshaller.setSchema (aSchema);

    return aUnmarshaller;
  }

  @Nullable
  public JAXBTYPE read (@Nonnull final IJAXBUnmarshaller <JAXBTYPE> aHandler)
  {
    ValueEnforcer.notNull (aHandler, "Handler");

    // as we don't have a node, we need to trust the implementation class
    final Schema aSchema = getSchema ();
    if (aSchema == null && isUseSchema ())
      LOGGER.warn ("Don't know how to read JAXB document of type " + m_aImplClass.getName ());

    JAXBTYPE ret = null;
    try
    {
      // Create unmarshaller
      final Unmarshaller aUnmarshaller = createUnmarshaller ();

      // Customize on demand
      if (m_aUnmarshallerCustomizer != null)
        m_aUnmarshallerCustomizer.accept (aUnmarshaller);

      // main unmarshalling
      final JAXBElement <JAXBTYPE> aElement = aHandler.doUnmarshal (aUnmarshaller, m_aImplClass);
      ret = aElement.getValue ();
      if (ret == null)
        throw new IllegalStateException ("Failed to read JAXB document of class " +
                                         m_aImplClass.getName () +
                                         " - without exception!");
    }
    catch (final JAXBException ex)
    {
      exceptionCallbacks ().forEach (x -> x.onException (ex));
    }

    return ret;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("ImplClass", m_aImplClass)
                            .append ("EventHandler", m_aEventHandler)
                            .append ("UnmarshallerCustomizer", m_aUnmarshallerCustomizer)
                            .getToString ();
  }
}

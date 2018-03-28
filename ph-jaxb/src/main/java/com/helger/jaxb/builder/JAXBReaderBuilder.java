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

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.validation.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXParseException;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.callback.CallbackList;
import com.helger.commons.callback.exception.IExceptionCallback;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.string.ToStringGenerator;
import com.helger.jaxb.IJAXBReader;
import com.helger.jaxb.validation.LoggingValidationEventHandler;

/**
 * Builder class for reading JAXB documents.
 *
 * @author Philip Helger
 * @param <JAXBTYPE>
 *        The JAXB implementation class to be read
 * @param <IMPLTYPE>
 *        The implementation class implementing this abstract class.
 */
@NotThreadSafe
public class JAXBReaderBuilder <JAXBTYPE, IMPLTYPE extends JAXBReaderBuilder <JAXBTYPE, IMPLTYPE>> extends
                               AbstractJAXBBuilder <IMPLTYPE> implements
                               IJAXBReader <JAXBTYPE>
{
  public static class DefaultExceptionHandler implements IExceptionCallback <JAXBException>
  {
    private static final Logger s_aLogger0 = LoggerFactory.getLogger (DefaultExceptionHandler.class);

    public void onException (@Nonnull final JAXBException ex)
    {
      if (ex instanceof UnmarshalException)
      {
        // The JAXB specification does not mandate how the JAXB provider
        // must behave when attempting to unmarshal invalid XML data. In
        // those cases, the JAXB provider is allowed to terminate the
        // call to unmarshal with an UnmarshalException.
        final Throwable aLinked = ((UnmarshalException) ex).getLinkedException ();
        if (aLinked instanceof SAXParseException)
          s_aLogger0.error ("Failed to parse XML document: " + aLinked.getMessage ());
        else
          s_aLogger0.error ("Unmarshal exception reading document", ex);
      }
      else
        s_aLogger0.warn ("JAXB Exception reading document", ex);
    }
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (JAXBReaderBuilder.class);

  private final Class <JAXBTYPE> m_aImplClass;
  private ValidationEventHandler m_aEventHandler = JAXBBuilderDefaultSettings.getDefaultValidationEventHandler ();
  private final CallbackList <IExceptionCallback <JAXBException>> m_aExceptionCallbacks = new CallbackList <> ();
  private Consumer <? super Unmarshaller> m_aUnmarshallerCustomizer;
  private boolean m_bReadSecure = true;

  public JAXBReaderBuilder (@Nonnull final IJAXBDocumentType aDocType)
  {
    this (aDocType, GenericReflection.uncheckedCast (aDocType.getImplementationClass ()));
  }

  public JAXBReaderBuilder (@Nonnull final IJAXBDocumentType aDocType, @Nonnull final Class <JAXBTYPE> aImplClass)
  {
    super (aDocType);
    m_aImplClass = ValueEnforcer.notNull (aImplClass, "ImplClass");
    m_aExceptionCallbacks.add (new DefaultExceptionHandler ());
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
  public final IMPLTYPE setValidationEventHandler (@Nullable final ValidationEventHandler aEventHandler)
  {
    m_aEventHandler = aEventHandler;
    return thisAsT ();
  }

  @Nonnull
  @ReturnsMutableObject
  public final CallbackList <IExceptionCallback <JAXBException>> exceptionCallbacks ()
  {
    return m_aExceptionCallbacks;
  }

  @Nullable
  public Consumer <? super Unmarshaller> getUnmarshallerCustomizer ()
  {
    return m_aUnmarshallerCustomizer;
  }

  @Nonnull
  public final IMPLTYPE setUnmarshallerCustomizer (@Nullable final Consumer <? super Unmarshaller> aUnmarshallerCustomizer)
  {
    m_aUnmarshallerCustomizer = aUnmarshallerCustomizer;
    return thisAsT ();
  }

  public final boolean isReadSecure ()
  {
    return m_bReadSecure;
  }

  @Nonnull
  public final IMPLTYPE setReadSecure (final boolean bReadSecure)
  {
    m_bReadSecure = bReadSecure;
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
    else
      aUnmarshaller.setEventHandler (new LoggingValidationEventHandler ().andThen (aUnmarshaller.getEventHandler ()));

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
      s_aLogger.warn ("Don't know how to read JAXB document of type " + m_aImplClass.getName ());

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
      m_aExceptionCallbacks.forEach (x -> x.onException (ex));
    }

    return ret;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("ImplClass", m_aImplClass)
                            .append ("EventHandler", m_aEventHandler)
                            .append ("ExceptionHandler", m_aExceptionCallbacks)
                            .append ("UnmarshallerCustomizer", m_aUnmarshallerCustomizer)
                            .append ("ReadSecure", m_bReadSecure)
                            .getToString ();
  }
}

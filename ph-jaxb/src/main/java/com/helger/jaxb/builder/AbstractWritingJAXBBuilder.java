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
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;

import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.lang.GenericReflection;
import com.helger.jaxb.LoggingJAXBWriteExceptionHandler;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

/**
 * Abstract builder base class for writing and validating JAXB documents.
 *
 * @author Philip Helger
 * @param <JAXBTYPE>
 *        The JAXB implementation class to be handled
 * @param <IMPLTYPE>
 *        The implementation class implementing this abstract class.
 */
@NotThreadSafe
@Deprecated (forRemoval = true, since = "11.0.3")
public abstract class AbstractWritingJAXBBuilder <JAXBTYPE, IMPLTYPE extends AbstractWritingJAXBBuilder <JAXBTYPE, IMPLTYPE>>
                                                 extends
                                                 AbstractJAXBBuilder <IMPLTYPE>
{
  private Consumer <? super Marshaller> m_aMarshallerCustomizer;

  protected AbstractWritingJAXBBuilder (@Nonnull final IJAXBDocumentType aDocType)
  {
    super (aDocType);
    exceptionCallbacks ().add (LoggingJAXBWriteExceptionHandler.INSTANCE);
  }

  @Nullable
  public final Consumer <? super Marshaller> getMarshallerCustomizer ()
  {
    return m_aMarshallerCustomizer;
  }

  @Nonnull
  public final IMPLTYPE setMarshallerCustomizer (@Nullable final Consumer <? super Marshaller> aMarshallerCustomizer)
  {
    m_aMarshallerCustomizer = aMarshallerCustomizer;
    return thisAsT ();
  }

  /**
   * Create the main marshaller with the contained settings.
   *
   * @return The Marshaller and never <code>null</code>.
   * @throws JAXBException
   *         In case creation fails
   */
  @Nonnull
  @OverrideOnDemand
  protected Marshaller createMarshaller () throws JAXBException
  {
    final JAXBContext aJAXBContext = getJAXBContext ();

    // create a Marshaller
    final Marshaller aMarshaller = aJAXBContext.createMarshaller ();

    // Validating (if possible)
    final Schema aSchema = getSchema ();
    if (aSchema != null)
      aMarshaller.setSchema (aSchema);

    return aMarshaller;
  }

  @Nonnull
  protected <T> JAXBElement <T> createJAXBElement (@Nonnull final T aValue)
  {
    return new JAXBElement <> (new QName (m_aDocType.getNamespaceURI (), m_aDocType.getLocalName ()),
                               GenericReflection.uncheckedCast (aValue.getClass ()),
                               null,
                               aValue);
  }
}

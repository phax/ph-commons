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
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.xml.sax.helpers.DefaultHandler;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.error.list.IErrorList;
import com.helger.jaxb.IJAXBValidator;
import com.helger.jaxb.validation.CollectingValidationEventHandler;

/**
 * Builder class for validating JAXB documents.
 *
 * @author Philip Helger
 * @param <JAXBTYPE>
 *        The JAXB implementation class to be validated
 * @param <IMPLTYPE>
 *        The implementation class implementing this abstract class.
 */
@NotThreadSafe
public class JAXBValidationBuilder <JAXBTYPE, IMPLTYPE extends JAXBValidationBuilder <JAXBTYPE, IMPLTYPE>> extends
                                   AbstractWritingJAXBBuilder <JAXBTYPE, IMPLTYPE> implements IJAXBValidator <JAXBTYPE>
{
  public JAXBValidationBuilder (@Nonnull final IJAXBDocumentType aDocType)
  {
    super (aDocType);
  }

  @Nonnull
  public IErrorList validate (@Nonnull final JAXBTYPE aJAXBDocument)
  {
    ValueEnforcer.notNull (aJAXBDocument, "JAXBDocument");

    // Avoid class cast exception later on
    if (!m_aDocType.getImplementationClass ().getPackage ().equals (aJAXBDocument.getClass ().getPackage ()))
    {
      throw new IllegalArgumentException ("You cannot validate a '" +
                                          aJAXBDocument.getClass () +
                                          "' as a " +
                                          m_aDocType.getImplementationClass ().getPackage ().getName ());
    }

    final CollectingValidationEventHandler aEventHandler = new CollectingValidationEventHandler ();
    try
    {
      // create a Marshaller
      final Marshaller aMarshaller = createMarshaller ();
      aMarshaller.setEventHandler (aEventHandler);

      // Customize on demand
      final Consumer <? super Marshaller> aCustomizer = getMarshallerCustomizer ();
      if (aCustomizer != null)
        aCustomizer.accept (aMarshaller);

      // start marshalling
      final JAXBElement <?> aJAXBElement = createJAXBElement (aJAXBDocument);

      // DefaultHandler has very little overhead and does nothing
      aMarshaller.marshal (aJAXBElement, new DefaultHandler ());
    }
    catch (final JAXBException ex)
    {
      // Should already be contained as an entry in the event handler
    }
    return aEventHandler.getErrorList ();
  }
}

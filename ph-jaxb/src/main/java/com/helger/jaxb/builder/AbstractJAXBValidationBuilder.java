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
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.xml.sax.helpers.DefaultHandler;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.error.IResourceErrorGroup;
import com.helger.jaxb.validation.CollectingValidationEventHandler;

/**
 * Abstract builder class for validating JAXB documents.
 *
 * @author Philip Helger
 * @param <T>
 *        The JAXB implementation class to be validated
 * @param <IMPLTYPE>
 *        The implementation class implementing this abstract class.
 */
@NotThreadSafe
public abstract class AbstractJAXBValidationBuilder <T, IMPLTYPE extends AbstractJAXBValidationBuilder <T, IMPLTYPE>>
                                                    extends AbstractWritingJAXBBuilder <T, IMPLTYPE>
{
  public AbstractJAXBValidationBuilder (@Nonnull final IJAXBDocumentType aDocType)
  {
    super (aDocType);
  }

  /**
   * Check if the passed JAXB document is valid according to the XSD or not.
   *
   * @param aJAXBDocument
   *        The JAXB document to be validated. May not be <code>null</code>.
   * @return <code>true</code> if the document is valid, <code>false</code> if
   *         not.
   * @see #validate(Object)
   */
  public boolean isValid (@Nonnull final T aJAXBDocument)
  {
    return validate (aJAXBDocument).containsNoError ();
  }

  /**
   * Validate the passed JAXB document.
   *
   * @param aJAXBDocument
   *        The JAXB document to be validated. May not be <code>null</code>.
   * @return The validation results. Never <code>null</code>.
   */
  @Nonnull
  public IResourceErrorGroup validate (@Nonnull final T aJAXBDocument)
  {
    ValueEnforcer.notNull (aJAXBDocument, "JAXBDocument");

    // Avoid class cast exception later on
    if (!m_aDocType.getPackage ().equals (aJAXBDocument.getClass ().getPackage ()))
    {
      throw new IllegalArgumentException ("You cannot validate a '" +
                                          aJAXBDocument.getClass () +
                                          "' as a " +
                                          m_aDocType.getPackage ().getName ());
    }

    final CollectingValidationEventHandler aEventHandler = new CollectingValidationEventHandler ();
    try
    {
      // create a Marshaller
      final Marshaller aMarshaller = createMarshaller ();
      aMarshaller.setEventHandler (aEventHandler);

      // Customize on demand
      customizeMarshaller (aMarshaller);

      // start marshalling
      final JAXBElement <?> aJAXBElement = _createJAXBElement (m_aDocType.getQName (), aJAXBDocument);
      // DefaultHandler has very little overhead
      aMarshaller.marshal (aJAXBElement, new DefaultHandler ());
    }
    catch (final JAXBException ex)
    {
      // Should already be contained as an entry in the event handler
    }
    return aEventHandler.getResourceErrors ();
  }

}

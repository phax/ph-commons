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

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.bind.JAXBElement;

import com.helger.commons.io.resource.IReadableResource;

/**
 * This is the generic reader and writer base class for JAXB enabled document
 * types.
 *
 * @author Philip Helger
 * @param <JAXBTYPE>
 *        The JAXB type to be marshaled
 * @since 8.5.3
 */
@NotThreadSafe
public class GenericJAXBMarshaller <JAXBTYPE> extends AbstractJAXBMarshaller <JAXBTYPE>
{
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
  public GenericJAXBMarshaller (@Nonnull final Class <JAXBTYPE> aType,
                                @Nullable final List <? extends IReadableResource> aXSDs,
                                @Nonnull final Function <JAXBTYPE, JAXBElement <JAXBTYPE>> aWrapper)
  {
    super (aType, aXSDs, aWrapper);
  }
}

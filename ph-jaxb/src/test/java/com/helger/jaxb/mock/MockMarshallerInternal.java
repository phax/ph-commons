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
package com.helger.jaxb.mock;

import javax.annotation.Nonnull;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import com.helger.jaxb.AbstractJAXBMarshaller;
import com.helger.jaxb.JAXBMarshallerHelper;
import com.helger.xml.namespace.MapBasedNamespaceContext;

public final class MockMarshallerInternal extends AbstractJAXBMarshaller <com.helger.jaxb.mock.internal.MockJAXBArchive>
{
  public MockMarshallerInternal ()
  {
    // No XSD available
    super (com.helger.jaxb.mock.internal.MockJAXBArchive.class,
           null,
           o -> new JAXBElement <> (new QName ("urn:test:internal", "any"),
                                    com.helger.jaxb.mock.internal.MockJAXBArchive.class,
                                    o));
  }

  @Override
  protected void customizeMarshaller (@Nonnull final Marshaller aMarshaller)
  {
    JAXBMarshallerHelper.setFormattedOutput (aMarshaller, true);
    JAXBMarshallerHelper.setSunNamespacePrefixMapper (aMarshaller,
                                                      new MapBasedNamespaceContext ().addMapping ("def",
                                                                                                  "urn:test:internal"));
  }
}

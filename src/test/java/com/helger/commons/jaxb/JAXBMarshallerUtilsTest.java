/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.jaxb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Test;

import com.helger.commons.charset.CCharset;
import com.helger.commons.xml.namespace.MapBasedNamespaceContext;

/**
 * Test class for class {@link JAXBMarshallerUtils}.
 *
 * @author Philip Helger
 */
public final class JAXBMarshallerUtilsTest
{
  @Test
  public void testAll () throws JAXBException
  {
    final JAXBContext aCtx = JAXBContextCache.getInstance ().getFromCache (MockJAXBArchive.class);
    assertNotNull (aCtx);

    final Marshaller aMarshaller = aCtx.createMarshaller ();
    assertTrue (JAXBMarshallerUtils.isSunJAXB2Marshaller (aMarshaller));

    // Encoding
    assertEquals (CCharset.CHARSET_UTF_8, JAXBMarshallerUtils.getEncoding (aMarshaller));
    JAXBMarshallerUtils.setEncoding (aMarshaller, CCharset.CHARSET_ISO_8859_1);
    assertEquals (CCharset.CHARSET_ISO_8859_1, JAXBMarshallerUtils.getEncoding (aMarshaller));

    // Formatted output?
    assertFalse (JAXBMarshallerUtils.isFormattedOutput (aMarshaller));
    JAXBMarshallerUtils.setFormattedOutput (aMarshaller, true);
    assertTrue (JAXBMarshallerUtils.isFormattedOutput (aMarshaller));

    // Schema location
    assertNull (JAXBMarshallerUtils.getSchemaLocation (aMarshaller));
    JAXBMarshallerUtils.setSchemaLocation (aMarshaller, "any");
    assertEquals ("any", JAXBMarshallerUtils.getSchemaLocation (aMarshaller));

    // no-namespace Schema location
    assertNull (JAXBMarshallerUtils.getNoNamespaceSchemaLocation (aMarshaller));
    JAXBMarshallerUtils.setNoNamespaceSchemaLocation (aMarshaller, "any");
    assertEquals ("any", JAXBMarshallerUtils.getNoNamespaceSchemaLocation (aMarshaller));

    // Fragment?
    assertFalse (JAXBMarshallerUtils.isFragment (aMarshaller));
    JAXBMarshallerUtils.setFragment (aMarshaller, true);
    assertTrue (JAXBMarshallerUtils.isFragment (aMarshaller));

    // Namespace prefix mapper
    assertNull (JAXBMarshallerUtils.getSunNamespacePrefixMapper (aMarshaller));
    JAXBMarshallerUtils.setSunNamespacePrefixMapper (aMarshaller,
                                                     new MapBasedNamespaceContext ().addMapping ("p1",
                                                                                                 "http://www.helger.com/namespace1")
                                                                                    .addMapping ("p2",
                                                                                                 "http://www.helger.com/namespace2"));
    assertNotNull (JAXBMarshallerUtils.getSunNamespacePrefixMapper (aMarshaller));
  }
}

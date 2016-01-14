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
package com.helger.jaxb.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.xml.transform.TransformSourceFactory;
import com.helger.jaxb.JAXBContextCache;
import com.helger.jaxb.mock.external.MockJAXBArchive;

/**
 * Test class for class {@link CollectingValidationEventHandler}.
 *
 * @author Philip Helger
 */
public final class CollectingValidationEventHandlerTest
{
  @Test
  public void testReadWrite () throws JAXBException
  {
    final JAXBContext aCtx = JAXBContextCache.getInstance ().getFromCache (MockJAXBArchive.class);
    CollectingValidationEventHandler evh = new CollectingValidationEventHandler (new LoggingValidationEventHandler (null));
    // is equal to:
    evh = new CollectingValidationEventHandlerFactory ().create (new LoggingValidationEventHandlerFactory ().create (null));
    final Unmarshaller um = aCtx.createUnmarshaller ();
    um.setEventHandler (evh);

    // read valid
    JAXBElement <MockJAXBArchive> o = um.unmarshal (TransformSourceFactory.create (new FileSystemResource ("src/test/resources/xml/test-archive-01.xml")),
                                                    MockJAXBArchive.class);
    assertNotNull (o);
    assertTrue (evh.getResourceErrors ().isEmpty ());

    // read invalid
    evh = new CollectingValidationEventHandler ();
    um.setEventHandler (evh);
    o = um.unmarshal (TransformSourceFactory.create (new FileSystemResource ("src/test/resources/xml/buildinfo.xml")),
                      MockJAXBArchive.class);
    assertNotNull (o);
    assertTrue (!evh.getResourceErrors ().isEmpty ());

    // Read invalid (but close to valid)
    evh = new CollectingValidationEventHandler (new LoggingValidationEventHandler ());
    um.setEventHandler (evh);
    o = um.unmarshal (TransformSourceFactory.create (new FileSystemResource ("src/test/resources/xml/test-archive-03.xml")),
                      MockJAXBArchive.class);
    assertNotNull (o);
    assertEquals (1, evh.getResourceErrors ().getSize ());

    // For code coverage completion
    CommonsTestHelper.testToStringImplementation (evh);
  }
}

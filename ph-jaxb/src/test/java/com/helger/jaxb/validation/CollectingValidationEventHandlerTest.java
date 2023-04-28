/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.jaxb.JAXBContextCache;
import com.helger.jaxb.JAXBContextCacheKey;
import com.helger.jaxb.mock.external.MockJAXBArchive;
import com.helger.xml.transform.TransformSourceFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

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
    final JAXBContext aCtx = JAXBContextCache.getInstance ()
                                             .getFromCache (JAXBContextCacheKey.createForClass (MockJAXBArchive.class));
    final Unmarshaller um = aCtx.createUnmarshaller ();

    {
      final CollectingValidationEventHandler cveh = new CollectingValidationEventHandler ();
      um.setEventHandler (cveh.andThen (LoggingValidationEventHandler.DEFAULT_INSTANCE));

      // read valid
      final JAXBElement <MockJAXBArchive> o = um.unmarshal (TransformSourceFactory.create (new FileSystemResource ("src/test/resources/xml/test-archive-01.xml")),
                                                            MockJAXBArchive.class);
      assertNotNull (o);
      assertTrue (cveh.getErrorList ().isEmpty ());
    }

    // read invalid
    {
      final CollectingValidationEventHandler cveh = new CollectingValidationEventHandler ();
      um.setEventHandler (cveh);
      final JAXBElement <MockJAXBArchive> o = um.unmarshal (TransformSourceFactory.create (new FileSystemResource ("src/test/resources/xml/buildinfo.xml")),
                                                            MockJAXBArchive.class);
      assertNotNull (o);
      assertFalse (cveh.getErrorList ().isEmpty ());
    }

    // Read invalid (but close to valid)
    {
      final CollectingValidationEventHandler cveh = new CollectingValidationEventHandler ();
      um.setEventHandler (cveh.andThen (LoggingValidationEventHandler.DEFAULT_INSTANCE));
      final JAXBElement <MockJAXBArchive> o = um.unmarshal (TransformSourceFactory.create (new FileSystemResource ("src/test/resources/xml/test-archive-03.xml")),
                                                            MockJAXBArchive.class);
      assertNotNull (o);
      assertEquals (1, cveh.getErrorList ().size ());

      // For code coverage completion
      CommonsTestHelper.testToStringImplementation (cveh);
    }
  }
}

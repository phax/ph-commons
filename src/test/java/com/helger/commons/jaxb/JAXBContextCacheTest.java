/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.junit.Test;

import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.streams.NonBlockingStringWriter;
import com.helger.commons.xml.transform.TransformSourceFactory;

/**
 * Test class for class {@link JAXBContextCache}.
 * 
 * @author Philip Helger
 */
public final class JAXBContextCacheTest
{
  @Test
  public void testBasic ()
  {
    assertNotNull (JAXBContextCache.getInstance ());

    try
    {
      // null not allowed
      JAXBContextCache.getInstance ().getFromCache ((Class <?>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null not allowed
      JAXBContextCache.getInstance ().getFromCache ((Package) null);
      fail ();
    }
    catch (final IllegalStateException ex)
    {}
  }

  @Test
  public void testReadWrite () throws JAXBException
  {
    JAXBContext aCtx = JAXBContextCache.getInstance ().getFromCache (MockJAXBArchive.class);
    assertNotNull (aCtx);

    // retrieve again
    assertSame (aCtx, JAXBContextCache.getInstance ().getFromCache (MockJAXBArchive.class));

    final Unmarshaller um = aCtx.createUnmarshaller ();

    // read valid
    JAXBElement <MockJAXBArchive> o = um.unmarshal (TransformSourceFactory.create (new ClassPathResource ("xml/test-archive-01.xml")),
                                                    MockJAXBArchive.class);
    assertNotNull (o);

    // read invalid
    o = um.unmarshal (TransformSourceFactory.create (new ClassPathResource ("xml/buildinfo.xml")),
                      MockJAXBArchive.class);
    assertNotNull (o);

    // Read invalid (but close to valid)
    o = um.unmarshal (TransformSourceFactory.create (new ClassPathResource ("xml/test-archive-03.xml")),
                      MockJAXBArchive.class);
    assertNotNull (o);

    // Clear cache
    assertTrue (JAXBContextCache.getInstance ().clearCache ().isChanged ());
    assertFalse (JAXBContextCache.getInstance ().clearCache ().isChanged ());

    // Get context again
    aCtx = JAXBContextCache.getInstance ().getFromCache (MockJAXBArchive.class);
    assertNotNull (aCtx);

    // And remove manually
    assertTrue (JAXBContextCache.getInstance ().removeFromCache (MockJAXBArchive.class.getPackage ()).isChanged ());
    assertFalse (JAXBContextCache.getInstance ().removeFromCache (MockJAXBArchive.class.getPackage ()).isChanged ());
  }

  @Test
  public void testJavaLang () throws JAXBException
  {
    final String sMsg = "Hello world";
    final JAXBContext aCtx = JAXBContextCache.getInstance ().getFromCache (String.class);
    final Marshaller m = aCtx.createMarshaller ();
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    m.marshal (new JAXBElement <String> (new QName ("element"), String.class, sMsg), aSW);
    assertEquals ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><element>" + sMsg + "</element>",
                  aSW.getAsString ());
  }
}

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
package com.helger.jaxb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.xml.namespace.QName;

import org.junit.Test;

import com.helger.base.classloader.ClassLoaderHelper;
import com.helger.base.io.nonblocking.NonBlockingStringWriter;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.xml.transform.TransformSourceFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

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
      JAXBContextCache.getInstance ().getFromCache (JAXBContextCacheKey.createForClass (null));
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null not allowed
      JAXBContextCache.getInstance ().getFromCache (JAXBContextCacheKey.createForPackage (null));
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  private <T> void _testReadWrite (final Class <T> aClass) throws JAXBException
  {
    final JAXBContextCacheKey ck = JAXBContextCacheKey.createForClass (aClass);

    JAXBContext aCtx = JAXBContextCache.getInstance ().getFromCache (ck);
    assertNotNull (aCtx);

    // retrieve again
    assertSame (aCtx, JAXBContextCache.getInstance ().getFromCache (ck));

    final Unmarshaller um = aCtx.createUnmarshaller ();

    // read valid
    JAXBElement <T> o = um.unmarshal (TransformSourceFactory.create (new ClassPathResource ("xml/test-archive-01.xml")),
                                      aClass);
    assertNotNull (o);

    // read invalid
    o = um.unmarshal (TransformSourceFactory.create (new ClassPathResource ("xml/buildinfo.xml")), aClass);
    assertNotNull (o);

    // Read invalid (but close to valid)
    o = um.unmarshal (TransformSourceFactory.create (new ClassPathResource ("xml/test-archive-03.xml")), aClass);
    assertNotNull (o);

    // Clear cache
    assertTrue (JAXBContextCache.getInstance ().clearCache ().isChanged ());
    assertFalse (JAXBContextCache.getInstance ().clearCache ().isChanged ());

    // Get context again
    aCtx = JAXBContextCache.getInstance ().getFromCache (ck);
    assertNotNull (aCtx);

    // And remove manually
    assertTrue (JAXBContextCache.getInstance ().removeFromCache (ck).isChanged ());
    assertFalse (JAXBContextCache.getInstance ().removeFromCache (ck).isChanged ());
  }

  @Test
  public void testReadWrite () throws JAXBException
  {
    _testReadWrite (com.helger.jaxb.mock.external.MockJAXBArchive.class);
  }

  @Test
  public void testMarshalString () throws JAXBException
  {
    final String sMsg = "Hello world";
    final JAXBContext aCtx = JAXBContextCache.getInstance ()
                                             .getFromCache (JAXBContextCacheKey.createForClass (String.class));
    final Marshaller m = aCtx.createMarshaller ();
    try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter ())
    {
      m.marshal (new JAXBElement <> (new QName ("element"), String.class, sMsg), aSW);
      assertEquals ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><element>" + sMsg + "</element>",
                    aSW.getAsString ());
    }
  }

  @Test
  public void testMarshalStringWithClassLoader () throws JAXBException
  {
    final String sMsg = "Hello world";
    final JAXBContext aCtx = JAXBContextCache.getInstance ()
                                             .getFromCache (JAXBContextCacheKey.createForClass (String.class,
                                                                                                ClassLoaderHelper.getSystemClassLoader ()));
    final Marshaller m = aCtx.createMarshaller ();
    try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter ())
    {
      m.marshal (new JAXBElement <> (new QName ("element"), String.class, sMsg), aSW);
      assertEquals ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><element>" + sMsg + "</element>",
                    aSW.getAsString ());
    }
  }
}

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
package com.helger.jaxb;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.mutable.MutableBoolean;
import com.helger.jaxb.mock.MockMarshallerExternal;
import com.helger.jaxb.mock.external.MockJAXBArchive;
import com.helger.jaxb.mock.external.MockJAXBCollection;
import com.helger.xml.namespace.MapBasedNamespaceContext;

/**
 * Test class for class {@link JAXBMarshallerHelper}.
 *
 * @author Philip Helger
 */
public final class JAXBMarshallerHelperTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (JAXBMarshallerHelperTest.class);

  private void _testAll (@Nonnull final Class <?> aClass) throws JAXBException
  {
    final JAXBContext aCtx = JAXBContextCache.getInstance ().getFromCache (aClass);
    assertNotNull (aCtx);

    final Marshaller aMarshaller = aCtx.createMarshaller ();
    assertTrue (JAXBMarshallerHelper.isSunJAXB2Marshaller (aMarshaller));

    // Encoding
    assertEquals (StandardCharsets.UTF_8.name (), JAXBMarshallerHelper.getEncoding (aMarshaller));
    JAXBMarshallerHelper.setEncoding (aMarshaller, StandardCharsets.ISO_8859_1.name ());
    assertEquals (StandardCharsets.ISO_8859_1.name (), JAXBMarshallerHelper.getEncoding (aMarshaller));

    // Formatted output?
    assertFalse (JAXBMarshallerHelper.isFormattedOutput (aMarshaller));
    JAXBMarshallerHelper.setFormattedOutput (aMarshaller, true);
    assertTrue (JAXBMarshallerHelper.isFormattedOutput (aMarshaller));

    // Schema location
    assertNull (JAXBMarshallerHelper.getSchemaLocation (aMarshaller));
    JAXBMarshallerHelper.setSchemaLocation (aMarshaller, "any");
    assertEquals ("any", JAXBMarshallerHelper.getSchemaLocation (aMarshaller));

    // no-namespace Schema location
    assertNull (JAXBMarshallerHelper.getNoNamespaceSchemaLocation (aMarshaller));
    JAXBMarshallerHelper.setNoNamespaceSchemaLocation (aMarshaller, "any");
    assertEquals ("any", JAXBMarshallerHelper.getNoNamespaceSchemaLocation (aMarshaller));

    // Fragment?
    assertFalse (JAXBMarshallerHelper.isFragment (aMarshaller));
    JAXBMarshallerHelper.setFragment (aMarshaller, true);
    assertTrue (JAXBMarshallerHelper.isFragment (aMarshaller));

    // Namespace prefix mapper
    assertNull (JAXBMarshallerHelper.getSunNamespacePrefixMapper (aMarshaller));
    JAXBMarshallerHelper.setSunNamespacePrefixMapper (aMarshaller,
                                                      new MapBasedNamespaceContext ().addMapping ("p1",
                                                                                                  "http://www.helger.com/namespace1")
                                                                                     .addMapping ("p2",
                                                                                                  "http://www.helger.com/namespace2"));
    assertNotNull (JAXBMarshallerHelper.getSunNamespacePrefixMapper (aMarshaller));
  }

  @Test
  public void testAll () throws JAXBException
  {
    _testAll (com.helger.jaxb.mock.external.MockJAXBArchive.class);
  }

  @Test
  public void testCloseExternalOnWriteToOutputStream ()
  {
    final MockMarshallerExternal m = new MockMarshallerExternal ();
    final MutableBoolean aClosed = new MutableBoolean (false);
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ()
    {
      @Override
      public void close ()
      {
        super.close ();
        aClosed.set (true);
      }
    };
    {
      final com.helger.jaxb.mock.external.MockJAXBArchive aArc = new com.helger.jaxb.mock.external.MockJAXBArchive ();
      aArc.setVersion ("1.23");
      m.write (aArc, aBAOS);
    }
    s_aLogger.info (aBAOS.getAsString (StandardCharsets.UTF_8));
    // Must be closed!
    assertTrue ("Not closed!", aClosed.booleanValue ());
  }

  @Test
  public void testCloseInternalOnWriteToOutputStream ()
  {
    final MockMarshallerExternal m = new MockMarshallerExternal ();
    final MutableBoolean aClosed = new MutableBoolean (false);
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ()
    {
      @Override
      public void close ()
      {
        super.close ();
        aClosed.set (true);
      }
    };
    {
      final MockJAXBArchive aArc = new MockJAXBArchive ();
      aArc.setVersion ("1.24");
      m.write (aArc, aBAOS);
    }
    s_aLogger.info (aBAOS.getAsString (StandardCharsets.UTF_8));
    // Must be closed!
    assertTrue ("Not closed!", aClosed.booleanValue ());
  }

  @Test
  public void testGetAsBytes ()
  {
    final MockMarshallerExternal m = new MockMarshallerExternal ();
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    byte [] aDirectBytes;
    {
      final MockJAXBArchive aArc = new MockJAXBArchive ();
      aArc.setVersion ("1.24");
      for (int i = 0; i < 100; ++i)
      {
        final MockJAXBCollection aCollection = new MockJAXBCollection ();
        aCollection.setDescription ("Internal bla foo");
        aCollection.setID (i);
        aArc.getCollection ().add (aCollection);
      }
      m.write (aArc, aBAOS);
      aDirectBytes = m.getAsBytes (aArc);
    }
    assertArrayEquals (aBAOS.toByteArray (), aDirectBytes);
  }
}

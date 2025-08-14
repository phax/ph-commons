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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.numeric.mutable.MutableBoolean;
import com.helger.commons.error.list.ErrorList;
import com.helger.jaxb.mock.JAXBMarshallerMockArchive;
import com.helger.jaxb.mock.external.MockJAXBArchive;
import com.helger.jaxb.mock.external.MockJAXBCollection;
import com.helger.jaxb.mock.external.MockJAXBIssue;
import com.helger.xml.namespace.MapBasedNamespaceContext;

import jakarta.annotation.Nonnull;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

/**
 * Test class for class {@link JAXBMarshallerHelper}.
 *
 * @author Philip Helger
 */
public final class JAXBMarshallerHelperTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (JAXBMarshallerHelperTest.class);

  private void _testAll (@Nonnull final Class <?> aClass) throws JAXBException
  {
    final JAXBContext aCtx = JAXBContextCache.getInstance ().getFromCache (JAXBContextCacheKey.createForClass (aClass));
    assertNotNull (aCtx);

    final Marshaller aMarshaller = aCtx.createMarshaller ();
    assertTrue (JAXBMarshallerHelper.isJakartaJAXBMarshaller (aMarshaller));

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
    assertNull (JAXBMarshallerHelper.getJakartaNamespacePrefixMapper (aMarshaller));
    JAXBMarshallerHelper.setJakartaNamespacePrefixMapper (aMarshaller,
                                                          new MapBasedNamespaceContext ().addMapping ("p1",
                                                                                                      "http://www.helger.com/namespace1")
                                                                                         .addMapping ("p2",
                                                                                                      "http://www.helger.com/namespace2"));
    assertNotNull (JAXBMarshallerHelper.getJakartaNamespacePrefixMapper (aMarshaller));
  }

  @Test
  public void testAll () throws JAXBException
  {
    _testAll (com.helger.jaxb.mock.external.MockJAXBArchive.class);
  }

  @Test
  public void testCloseExternalOnWriteToOutputStream ()
  {
    final JAXBMarshallerMockArchive m = new JAXBMarshallerMockArchive ();
    assertNotNull (m.toString ());
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
      final MockJAXBCollection aColl = new MockJAXBCollection ();
      final MockJAXBIssue aIssue = new MockJAXBIssue ();
      aIssue.setTitle (BigDecimal.ONE);
      aColl.getIssue ().add (aIssue);
      aArc.getCollection ().add (aColl);
      m.write (aArc, aBAOS);
    }
    LOGGER.info (aBAOS.getAsString (StandardCharsets.UTF_8));
    // Must be closed!
    assertTrue ("Not closed!", aClosed.booleanValue ());
  }

  @Test
  public void testCloseInternalOnWriteToOutputStream ()
  {
    final JAXBMarshallerMockArchive m = new JAXBMarshallerMockArchive ();
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
      final MockJAXBCollection aColl = new MockJAXBCollection ();
      final MockJAXBIssue aIssue = new MockJAXBIssue ();
      aIssue.setTitle (BigDecimal.ONE);
      aColl.getIssue ().add (aIssue);
      aArc.getCollection ().add (aColl);
      m.write (aArc, aBAOS);
    }
    LOGGER.info (aBAOS.getAsString (StandardCharsets.UTF_8));
    // Must be closed!
    assertTrue ("Not closed!", aClosed.booleanValue ());
  }

  @Test
  public void testGetAsBytes ()
  {
    final JAXBMarshallerMockArchive m = new JAXBMarshallerMockArchive ();
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    final byte [] aDirectBytes;
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

  @Test
  public void testRead ()
  {
    final JAXBMarshallerMockArchive m = new JAXBMarshallerMockArchive ();

    {
      final String sXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                          "<Root Version=\"1.23\">\n" +
                          "  <Collection ID=\"0\" Description=\"InternalDesc-0\">\n" +
                          "    <Issue ID=\"0\" CollectionID=\"0\" PageCount=\"0\" ArticleCount=\"0\" DirAbsolute=\"0\">\n" +
                          "      <Title>10000</Title>\n" +
                          "      <SubTitle>Test0</SubTitle>\n" +
                          "    </Issue>\n" +
                          "  </Collection>\n" +
                          "  <Collection ID=\"1\" Description=\"InternalDesc-1\">\n" +
                          "    <Issue ID=\"0\" CollectionID=\"0\" PageCount=\"0\" ArticleCount=\"0\" DirAbsolute=\"0\">\n" +
                          "      <Title>10001</Title>\n" +
                          "      <SubTitle>Test1</SubTitle>\n" +
                          "    </Issue>\n" +
                          "  </Collection>\n" +
                          "</Root>";
      final MockJAXBArchive aArc = m.read (sXML);
      assertNotNull (aArc);

      // Validate valid object
      final ErrorList aEL = new ErrorList ();
      m.validate (aArc, aEL);
      assertTrue ("Should be empty: " + aEL, aEL.containsNoFailure ());
    }

    // Validate a derived object - should work as well
    {
      final MockJAXBArchive aArc = new MockJAXBArchive ()
      {
        @Override
        public void setVersion (final String sValue)
        {
          super.setVersion (sValue);
        }
      };
      {
        aArc.setVersion ("1.24");
        final MockJAXBCollection aColl = new MockJAXBCollection ();
        final MockJAXBIssue aIssue = new MockJAXBIssue ();
        aIssue.setTitle (BigDecimal.ONE);
        aColl.getIssue ().add (aIssue);
        aArc.getCollection ().add (aColl);
      }

      final ErrorList aEL = new ErrorList ();
      m.validate (aArc, aEL);
      assertTrue ("Should be empty: " + aEL, aEL.containsNoFailure ());
    }

    // Validate invalid object
    {
      final ErrorList aEL = new ErrorList ();
      final MockJAXBArchive aArc = new MockJAXBArchive ();
      aArc.setVersion ("3.2.1");

      // Collection is required
      m.validate (aArc, aEL);
      assertTrue (aEL.containsAtLeastOneFailure ());
      assertEquals (1, aEL.getErrorCount ());

      final String sError = aEL.getAllErrors ().getFirstOrNull ().getErrorText (Locale.ROOT);
      assertTrue (sError, sError.contains ("'{Collection}'"));
    }
  }
}

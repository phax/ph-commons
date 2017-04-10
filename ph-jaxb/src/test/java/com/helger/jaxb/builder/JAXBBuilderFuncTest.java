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
package com.helger.jaxb.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.junit.Test;
import org.w3c.dom.Document;

import com.helger.commons.io.file.FileHelper;
import com.helger.xml.microdom.IMicroDocument;

public final class JAXBBuilderFuncTest
{
  @Test
  public void testExternalNoXSD ()
  {
    final com.helger.jaxb.mock.external.MockJAXBArchive aArc = new com.helger.jaxb.mock.external.MockJAXBArchive ();
    aArc.setVersion ("1.23");

    final MockExternalArchiveWriterBuilder aWriter = new MockExternalArchiveWriterBuilder ();
    final Document aDoc = aWriter.getAsDocument (aArc);
    assertNotNull (aDoc);
    final IMicroDocument aDoc2 = aWriter.getAsMicroDocument (aArc);
    assertNotNull (aDoc2);

    final com.helger.jaxb.mock.external.MockJAXBArchive aArc2 = new MockExternalArchiveReaderBuilder ().read (aDoc);
    assertNotNull (aArc2);

    assertEquals (aWriter.getAsString (aArc), aWriter.getAsString (aArc2));
  }

  @Test
  public void testExternalCharset ()
  {
    final com.helger.jaxb.mock.external.MockJAXBArchive aArc = new com.helger.jaxb.mock.external.MockJAXBArchive ();
    aArc.setVersion ("1.23");
    for (int i = 0; i < 5; ++i)
    {
      final com.helger.jaxb.mock.external.MockJAXBCollection aCollection = new com.helger.jaxb.mock.external.MockJAXBCollection ();
      aCollection.setDescription ("Internal bla foo");
      aCollection.setID (i);
      aArc.getCollection ().add (aCollection);
    }

    final MockExternalArchiveWriterBuilder aWriter = new MockExternalArchiveWriterBuilder ().setCharset (StandardCharsets.ISO_8859_1);
    String sText = aWriter.getAsString (aArc);
    assertTrue (sText, sText.contains ("encoding=\"ISO-8859-1\""));

    sText = aWriter.setCharset (null).getAsString (aArc);
    assertTrue (sText, sText.contains ("encoding=\"UTF-8\""));
  }

  @Test
  public void testExternalFormatted ()
  {
    final com.helger.jaxb.mock.external.MockJAXBArchive aArc = new com.helger.jaxb.mock.external.MockJAXBArchive ();
    aArc.setVersion ("1.23");
    for (int i = 0; i < 5; ++i)
    {
      final com.helger.jaxb.mock.external.MockJAXBCollection aCollection = new com.helger.jaxb.mock.external.MockJAXBCollection ();
      aCollection.setDescription ("Internal bla foo");
      aCollection.setID (i);
      aArc.getCollection ().add (aCollection);
    }

    final MockExternalArchiveWriterBuilder aWriter = new MockExternalArchiveWriterBuilder ().setFormattedOutput (true);
    String sText = aWriter.getAsString (aArc);
    assertTrue (sText, sText.contains ("    <Collection"));

    sText = aWriter.setFormattedOutput (false).getAsString (aArc);
    assertFalse (sText, sText.contains ("    <Collection"));

    sText = aWriter.setFormattedOutput (true).setIndentString ("\t").getAsString (aArc);
    assertTrue (sText, sText.contains ("\t<Collection"));
    assertFalse (sText, sText.contains ("    <Collection"));
  }

  @Test
  public void testInternalNoXSD ()
  {
    final com.helger.jaxb.mock.internal.MockJAXBArchive aArc = new com.helger.jaxb.mock.internal.MockJAXBArchive ();
    aArc.setVersion ("1.23");

    final MockInternalArchiveWriterBuilder aWriter = new MockInternalArchiveWriterBuilder ();
    final Document aDoc = aWriter.getAsDocument (aArc);
    assertNotNull (aDoc);
    final IMicroDocument aDoc2 = aWriter.getAsMicroDocument (aArc);
    assertNotNull (aDoc2);

    final com.helger.jaxb.mock.internal.MockJAXBArchive aArc2 = new MockInternalArchiveReaderBuilder ().read (aDoc);
    assertNotNull (aArc2);

    assertEquals (aWriter.getAsString (aArc), aWriter.getAsString (aArc2));
  }

  @Test
  public void testStreamWriter () throws XMLStreamException
  {
    final XMLOutputFactory aOF = XMLOutputFactory.newInstance ();
    final XMLStreamWriter aSW = aOF.createXMLStreamWriter (FileHelper.getBufferedWriter (new File ("target/stream-writer-test.xml"),
                                                                                         StandardCharsets.UTF_8));

    final com.helger.jaxb.mock.internal.MockJAXBArchive aArc = new com.helger.jaxb.mock.internal.MockJAXBArchive ();
    aArc.setVersion ("1.23");
    for (int i = 0; i < 5; ++i)
    {
      final com.helger.jaxb.mock.internal.MockJAXBCollection aCollection = new com.helger.jaxb.mock.internal.MockJAXBCollection ();
      aCollection.setDescription ("Internal bla foo");
      aCollection.setID (i);
      aArc.getCollection ().add (aCollection);
    }

    final MockInternalArchiveWriterBuilder aWriter = new MockInternalArchiveWriterBuilder ();
    aWriter.write (aArc, aSW);
  }
}

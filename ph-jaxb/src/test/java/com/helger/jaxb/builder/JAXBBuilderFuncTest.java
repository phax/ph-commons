package com.helger.jaxb.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.w3c.dom.Document;

import com.helger.commons.charset.CCharset;
import com.helger.commons.collection.IteratorHelper;

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

    final com.helger.jaxb.mock.external.MockJAXBArchive aArc2 = new MockExternalArchiveReaderBuilder ().read (aDoc);
    assertNotNull (aArc2);

    assertEquals (aWriter.getAsString (aArc), aWriter.getAsString (aArc2));
  }

  @Test
  public void testExternalCharset ()
  {
    final com.helger.jaxb.mock.external.MockJAXBArchive aArc = new com.helger.jaxb.mock.external.MockJAXBArchive ();
    aArc.setVersion ("1.23");
    IteratorHelper.forEach (5, i -> {
      final com.helger.jaxb.mock.external.MockJAXBCollection aCollection = new com.helger.jaxb.mock.external.MockJAXBCollection ();
      aCollection.setDescription ("Internal bla foo");
      aCollection.setID (i);
      aArc.getCollection ().add (aCollection);
    });

    final MockExternalArchiveWriterBuilder aWriter = new MockExternalArchiveWriterBuilder ().setCharset (CCharset.CHARSET_ISO_8859_1_OBJ);
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
    IteratorHelper.forEach (5, i -> {
      final com.helger.jaxb.mock.external.MockJAXBCollection aCollection = new com.helger.jaxb.mock.external.MockJAXBCollection ();
      aCollection.setDescription ("Internal bla foo");
      aCollection.setID (i);
      aArc.getCollection ().add (aCollection);
    });

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

    final com.helger.jaxb.mock.internal.MockJAXBArchive aArc2 = new MockInternalArchiveReaderBuilder ().read (aDoc);
    assertNotNull (aArc2);

    assertEquals (aWriter.getAsString (aArc), aWriter.getAsString (aArc2));
  }
}

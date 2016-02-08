package com.helger.jaxb.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.w3c.dom.Document;

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

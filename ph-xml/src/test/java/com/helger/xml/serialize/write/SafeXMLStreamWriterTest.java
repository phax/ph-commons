package com.helger.xml.serialize.write;

import static org.junit.Assert.assertEquals;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import com.helger.commons.io.stream.NonBlockingStringWriter;

/**
 * Test class for class {@link SafeXMLStreamWriter}
 *
 * @author Philip Helger
 */
public class SafeXMLStreamWriterTest
{
  @Test
  public void testBasic () throws XMLStreamException
  {
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    try (final SafeXMLStreamWriter aXSW = SafeXMLStreamWriter.create (aSW, new XMLWriterSettings ()))
    {
      aXSW.writeStartDocument ();
      aXSW.writeStartElement ("Root");
      aXSW.writeAttribute ("Version", "2.0");
      aXSW.writeStartElement ("Child");
      aXSW.writeCharacters ("Hello World");
      aXSW.writeEndElement ();
      aXSW.writeEndElement ();
      aXSW.writeEndDocument ();
      aXSW.flush ();
    }
    assertEquals ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
                  "<Root Version=\"2.0\"><Child>Hello World</Child></Root>",
                  aSW.getAsString ());
  }

  @Test
  public void testWithNS () throws XMLStreamException
  {
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    final String sNSURI = "urn:example.org/test";
    try (final SafeXMLStreamWriter aXSW = SafeXMLStreamWriter.create (aSW, new XMLWriterSettings ()))
    {
      aXSW.writeStartDocument ();
      aXSW.writeStartElement (sNSURI, "Root");
      aXSW.writeDefaultNamespace (sNSURI);
      aXSW.writeAttribute ("Version", "2.0");
      aXSW.writeStartElement (sNSURI, "Child");
      aXSW.writeCharacters ("Hello World");
      aXSW.writeEndElement ();
      aXSW.writeEndElement ();
      aXSW.writeEndDocument ();
      aXSW.flush ();
    }
    assertEquals ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
                  "<Root xmlns=\"urn:example.org/test\" Version=\"2.0\"><Child>Hello World</Child></Root>",
                  aSW.getAsString ());
  }
}

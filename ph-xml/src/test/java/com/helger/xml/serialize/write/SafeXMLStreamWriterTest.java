package com.helger.xml.serialize.write;

import static org.junit.Assert.assertEquals;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import com.helger.commons.io.stream.NonBlockingStringWriter;
import com.helger.commons.system.ENewLineMode;

/**
 * Test class for class {@link SafeXMLStreamWriter}
 *
 * @author Philip Helger
 */
public class SafeXMLStreamWriterTest
{
  @Test
  public void testBasic1 () throws XMLStreamException
  {
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    try (final SafeXMLStreamWriter aXSW = SafeXMLStreamWriter.create (aSW,
                                                                      new XMLWriterSettings ().setNewLineMode (ENewLineMode.UNIX)))
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
    assertEquals ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                  "<Root Version=\"2.0\">\n" +
                  "  <Child>Hello World</Child>\n" +
                  "</Root>",
                  aSW.getAsString ());
  }

  @Test
  public void testBasic1a () throws XMLStreamException
  {
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    try (final SafeXMLStreamWriter aXSW = SafeXMLStreamWriter.create (aSW,
                                                                      new XMLWriterSettings ().setNewLineMode (ENewLineMode.UNIX)))
    {
      aXSW.writeStartDocument ();
      aXSW.writeStartElement ("Root");
      aXSW.writeAttribute ("Version", "2.0");
      aXSW.writeStartElement ("Child");
      aXSW.writeCharacters ("Hello World");
      aXSW.writeEndElement ();
      aXSW.writeStartElement ("Child2");
      aXSW.writeCharacters ("Hello World");
      aXSW.writeEndElement ();
      aXSW.writeEndElement ();
      aXSW.writeEndDocument ();
      aXSW.flush ();
    }
    assertEquals ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                  "<Root Version=\"2.0\">\n" +
                  "  <Child>Hello World</Child>\n" +
                  "  <Child2>Hello World</Child2>\n" +
                  "</Root>",
                  aSW.getAsString ());
  }

  @Test
  public void testBasic2 () throws XMLStreamException
  {
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    try (final SafeXMLStreamWriter aXSW = SafeXMLStreamWriter.create (aSW,
                                                                      new XMLWriterSettings ().setNewLineMode (ENewLineMode.UNIX)))
    {
      aXSW.writeStartDocument ();
      aXSW.writeStartElement ("Root");
      aXSW.writeAttribute ("Version", "2.0");
      aXSW.writeStartElement ("Child");
      aXSW.writeStartElement ("Child2");
      aXSW.writeCharacters ("xyz");
      aXSW.writeEndElement ();
      aXSW.writeEndElement ();
      aXSW.writeEndElement ();
      aXSW.writeEndDocument ();
      aXSW.flush ();
    }
    assertEquals ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                  "<Root Version=\"2.0\">\n" +
                  "  <Child>\n" +
                  "    <Child2>xyz</Child2>\n" +
                  "  </Child>\n" +
                  "</Root>",
                  aSW.getAsString ());
  }

  @Test
  public void testBasic3 () throws XMLStreamException
  {
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    try (final SafeXMLStreamWriter aXSW = SafeXMLStreamWriter.create (aSW,
                                                                      new XMLWriterSettings ().setNewLineMode (ENewLineMode.UNIX)))
    {
      aXSW.writeStartDocument ();
      aXSW.writeStartElement ("Root");
      aXSW.writeAttribute ("Version", "2.0");
      aXSW.writeStartElement ("Child");
      aXSW.writeCharacters ("Hello World");
      aXSW.writeStartElement ("Child2");
      aXSW.writeCharacters ("xyz");
      aXSW.writeEndElement ();
      aXSW.writeEndElement ();
      aXSW.writeEndElement ();
      aXSW.writeEndDocument ();
      aXSW.flush ();
    }
    assertEquals ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                  "<Root Version=\"2.0\">\n" +
                  "  <Child>Hello World<Child2>xyz</Child2></Child>\n" +
                  "</Root>",
                  aSW.getAsString ());
  }

  @Test
  public void testWithNS () throws XMLStreamException
  {
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    final String sNSURI = "urn:example.org/test";
    try (final SafeXMLStreamWriter aXSW = SafeXMLStreamWriter.create (aSW,
                                                                      new XMLWriterSettings ().setNewLineMode (ENewLineMode.UNIX)))
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
    assertEquals ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                  "<Root xmlns=\"urn:example.org/test\" Version=\"2.0\">\n" +
                  "  <Child>Hello World</Child>\n" +
                  "</Root>",
                  aSW.getAsString ());
  }
}

/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.xml.stax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Reader;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jspecify.annotations.NonNull;
import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingStringReader;
import com.helger.base.io.stream.StreamHelper;
import com.helger.xml.XMLFactory;

/**
 * Test class for class {@link StAXTextContentReader}.
 *
 * @author Philip Helger
 */
public final class StAXTextContentReaderTest
{
  /**
   * @return A reader positioned on the first child element of the document element.
   */
  @NonNull
  private static XMLStreamReader _createOnFirstChild (@NonNull final String sXML) throws XMLStreamException
  {
    final XMLStreamReader aReader = XMLFactory.createNonCoalescingXMLInputFactory ()
                                              .createXMLStreamReader (new NonBlockingStringReader (sXML));
    aReader.nextTag ();
    aReader.nextTag ();
    return aReader;
  }

  @Test
  public void testBasic () throws Exception
  {
    final XMLStreamReader aReader = _createOnFirstChild ("<Root><Text>a&amp;b<!-- comment --><![CDATA[<c>]]><?pi data?>d</Text><Next/></Root>");
    try (final Reader aTextReader = new StAXTextContentReader (aReader))
    {
      assertEquals ("a&b<c>d", StreamHelper.getAllCharactersAsString (aTextReader));
      // Positioned on the end element
      assertEquals (XMLStreamConstants.END_ELEMENT, aReader.getEventType ());
      assertEquals ("Text", aReader.getLocalName ());
    }
  }

  @Test
  public void testEmpty () throws Exception
  {
    try (final Reader aTextReader = new StAXTextContentReader (_createOnFirstChild ("<Root><Text/></Root>")))
    {
      assertEquals (-1, aTextReader.read ());
    }
  }

  @Test
  public void testLargeText () throws Exception
  {
    final int nLen = 1_000_000;
    final StringBuilder aSB = new StringBuilder (nLen);
    for (int i = 0; i < nLen; ++i)
      aSB.append ((char) ('a' + i % 26));
    final String sText = aSB.toString ();
    try (final Reader aTextReader = new StAXTextContentReader (_createOnFirstChild ("<Root><Text>" +
                                                                                    sText +
                                                                                    "</Text></Root>")))
    {
      // Read in small chunks
      final StringBuilder aRead = new StringBuilder (nLen);
      final char [] aBuf = new char [777];
      int nRead;
      while ((nRead = aTextReader.read (aBuf, 0, aBuf.length)) >= 0)
        aRead.append (aBuf, 0, nRead);
      assertEquals (sText, aRead.toString ());
    }
  }

  @Test
  public void testChildElement () throws Exception
  {
    try (final Reader aTextReader = new StAXTextContentReader (_createOnFirstChild ("<Root><Text>a<b/></Text></Root>")))
    {
      while (aTextReader.read () >= 0)
      {
        // Read until the child element
      }
      fail ();
    }
    catch (final IOException ex)
    {
      // Expected
    }
  }

  @Test (expected = IllegalStateException.class)
  public void testNotOnStartElement () throws Exception
  {
    final XMLStreamReader aReader = XMLFactory.createNonCoalescingXMLInputFactory ()
                                              .createXMLStreamReader (new NonBlockingStringReader ("<Root/>"));
    try (final Reader aTextReader = new StAXTextContentReader (aReader))
    {
      fail ();
    }
  }
}

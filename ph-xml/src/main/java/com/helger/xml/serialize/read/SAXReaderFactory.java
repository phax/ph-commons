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
package com.helger.xml.serialize.read;

import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.helger.commons.exception.InitializationException;
import com.helger.xml.XMLFactory;

public final class SAXReaderFactory implements Supplier <XMLReader>
{
  @Nonnull
  public XMLReader get ()
  {
    return createXMLReader ();
  }

  @Nonnull
  public static XMLReader createXMLReader ()
  {
    try
    {
      final SAXParserFactory aFactory = XMLFactory.createDefaultSAXParserFactory ();
      return aFactory.newSAXParser ().getXMLReader ();
    }
    catch (final ParserConfigurationException | SAXException ex)
    {
      throw new InitializationException ("Failed to instantiate XML SAX reader", ex);
    }
  }
}

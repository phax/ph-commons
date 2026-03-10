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
package com.helger.xml.serialize.read;

import java.util.function.Supplier;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.jspecify.annotations.NonNull;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.helger.base.exception.InitializationException;
import com.helger.xml.XMLFactory;

/**
 * Factory for creating {@link XMLReader} instances from the default
 * {@link SAXParserFactory}.
 *
 * @author Philip Helger
 */
public final class SAXReaderFactory implements Supplier <XMLReader>
{
  /**
   * {@inheritDoc} Creates a new {@link XMLReader} using the default {@link SAXParserFactory}.
   *
   * @return A new {@link XMLReader} instance. Never <code>null</code>.
   */
  @NonNull
  public XMLReader get ()
  {
    return createXMLReader ();
  }

  /**
   * Create a new {@link XMLReader} using the default {@link SAXParserFactory}.
   *
   * @return A new {@link XMLReader} instance. Never <code>null</code>.
   * @throws com.helger.base.exception.InitializationException
   *         if the SAX parser cannot be created.
   */
  @NonNull
  public static XMLReader createXMLReader ()
  {
    final SAXParserFactory aFactory = XMLFactory.createDefaultSAXParserFactory ();
    return createXMLReader (aFactory);
  }

  /**
   * Create a new {@link XMLReader} from the provided {@link SAXParserFactory}.
   *
   * @param aFactory
   *        The SAX parser factory to use. May not be <code>null</code>.
   * @return A new {@link XMLReader} instance. Never <code>null</code>.
   * @throws com.helger.base.exception.InitializationException
   *         if the SAX parser cannot be created.
   */
  @NonNull
  public static XMLReader createXMLReader (@NonNull final SAXParserFactory aFactory)
  {
    try
    {
      return aFactory.newSAXParser ().getXMLReader ();
    }
    catch (final ParserConfigurationException | SAXException ex)
    {
      throw new InitializationException ("Failed to instantiate XML SAX reader", ex);
    }
  }
}

/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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

import com.helger.commons.exception.InitializationException;

public final class SAXReaderFactory implements Supplier <org.xml.sax.XMLReader>
{
  /** SAXParserFactory is by default not namespace aware */
  public static final boolean DEFAULT_SAX_NAMESPACE_AWARE = true;
  /** SAXParserFactory is by default not DTD validating */
  public static final boolean DEFAULT_SAX_VALIDATING = false;
  /** SAXParserFactory is by default not XInclude aware */
  public static final boolean DEFAULT_SAX_XINCLUDE_AWARE = false;

  @Nonnull
  public org.xml.sax.XMLReader get ()
  {
    return createXMLReader ();
  }

  @Nonnull
  public static org.xml.sax.XMLReader createXMLReader ()
  {
    try
    {
      final SAXParserFactory aFactory = SAXParserFactory.newDefaultInstance ();
      aFactory.setNamespaceAware (DEFAULT_SAX_NAMESPACE_AWARE);
      aFactory.setValidating (DEFAULT_SAX_VALIDATING);
      aFactory.setXIncludeAware (DEFAULT_SAX_XINCLUDE_AWARE);
      return aFactory.newSAXParser ().getXMLReader ();
    }
    catch (final ParserConfigurationException | SAXException ex)
    {
      throw new InitializationException ("Failed to instantiate XML SAX reader!", ex);
    }
  }
}

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.helpers.DefaultHandler;

import com.helger.base.state.EChange;
import com.helger.collection.commons.CommonsEnumMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.xml.EXMLParserFeature;
import com.helger.xml.EXMLParserProperty;
import com.helger.xml.sax.LoggingSAXErrorHandler;

/**
 * Test class for class {@link SAXReaderDefaultSettings}.
 *
 * @author Philip Helger
 */
public final class SAXReaderDefaultSettingsTest
{
  /**
   * These are static settings shared by the whole JVM, so they must be restored after every test.
   */
  @After
  public void restoreDefaults ()
  {
    SAXReaderDefaultSettings.setEntityResolver (null);
    SAXReaderDefaultSettings.setDTDHandler (null);
    SAXReaderDefaultSettings.setContentHandler (null);
    SAXReaderDefaultSettings.setErrorHandler (new LoggingSAXErrorHandler ());
    SAXReaderDefaultSettings.setLexicalHandler (null);
    SAXReaderDefaultSettings.setDeclarationHandler (null);
    SAXReaderDefaultSettings.removeAllPropertyValues ();
    SAXReaderDefaultSettings.removeAllFeatures ();
    SAXReaderDefaultSettings.setRequiresNewXMLParserExplicitly (SAXReaderDefaultSettings.DEFAULT_REQUIRES_NEW_XML_PARSER_EXPLICITLY);
  }

  @Test
  public void testHandlers ()
  {
    assertNull (SAXReaderDefaultSettings.getEntityResolver ());
    assertNull (SAXReaderDefaultSettings.getDTDHandler ());
    assertNull (SAXReaderDefaultSettings.getContentHandler ());
    assertNotNull (SAXReaderDefaultSettings.getErrorHandler ());
    assertNull (SAXReaderDefaultSettings.getLexicalHandler ());
    assertNull (SAXReaderDefaultSettings.getDeclarationHandler ());
    assertNotNull (SAXReaderDefaultSettings.exceptionCallbacks ());

    final DefaultHandler aHandler = new DefaultHandler ();
    SAXReaderDefaultSettings.setEntityResolver (aHandler);
    assertSame (aHandler, SAXReaderDefaultSettings.getEntityResolver ());

    SAXReaderDefaultSettings.setDTDHandler (aHandler);
    assertSame (aHandler, SAXReaderDefaultSettings.getDTDHandler ());

    SAXReaderDefaultSettings.setContentHandler (aHandler);
    assertSame (aHandler, SAXReaderDefaultSettings.getContentHandler ());

    final ErrorHandler aEH = new LoggingSAXErrorHandler ();
    SAXReaderDefaultSettings.setErrorHandler (aEH);
    assertSame (aEH, SAXReaderDefaultSettings.getErrorHandler ());
  }

  @Test
  public void testProperties ()
  {
    assertFalse (SAXReaderDefaultSettings.hasAnyProperties ());
    assertTrue (SAXReaderDefaultSettings.getAllPropertyValues ().isEmpty ());
    assertNull (SAXReaderDefaultSettings.getPropertyValue (null));
    assertNull (SAXReaderDefaultSettings.getPropertyValue (EXMLParserProperty.GENERAL_XML_STRING));
    assertFalse (SAXReaderDefaultSettings.requiresNewXMLParser ());

    SAXReaderDefaultSettings.setPropertyValue (EXMLParserProperty.GENERAL_XML_STRING, "any");
    assertTrue (SAXReaderDefaultSettings.hasAnyProperties ());
    assertEquals ("any", SAXReaderDefaultSettings.getPropertyValue (EXMLParserProperty.GENERAL_XML_STRING));
    assertTrue (SAXReaderDefaultSettings.requiresNewXMLParser ());

    // A null value removes the property
    SAXReaderDefaultSettings.setPropertyValue (EXMLParserProperty.GENERAL_XML_STRING, null);
    assertFalse (SAXReaderDefaultSettings.hasAnyProperties ());

    final ICommonsMap <EXMLParserProperty, Object> aProps = new CommonsEnumMap <> (EXMLParserProperty.class);
    aProps.put (EXMLParserProperty.GENERAL_XML_STRING, "any");
    SAXReaderDefaultSettings.setPropertyValues (aProps);
    assertTrue (SAXReaderDefaultSettings.hasAnyProperties ());
    SAXReaderDefaultSettings.setPropertyValues (null);
    assertTrue (SAXReaderDefaultSettings.hasAnyProperties ());

    assertSame (EChange.UNCHANGED, SAXReaderDefaultSettings.removePropertyValue (null));
    assertSame (EChange.CHANGED, SAXReaderDefaultSettings.removePropertyValue (EXMLParserProperty.GENERAL_XML_STRING));
    assertSame (EChange.UNCHANGED, SAXReaderDefaultSettings.removeAllPropertyValues ());
  }

  @Test
  public void testFeatures ()
  {
    assertFalse (SAXReaderDefaultSettings.hasAnyFeature ());
    assertTrue (SAXReaderDefaultSettings.getAllFeatureValues ().isEmpty ());
    assertNull (SAXReaderDefaultSettings.getFeatureValue (null));
    assertNull (SAXReaderDefaultSettings.getFeatureValue (EXMLParserFeature.NAMESPACES));

    SAXReaderDefaultSettings.setFeatureValue (EXMLParserFeature.NAMESPACES, true);
    assertTrue (SAXReaderDefaultSettings.hasAnyFeature ());
    assertEquals (Boolean.TRUE, SAXReaderDefaultSettings.getFeatureValue (EXMLParserFeature.NAMESPACES));
    assertTrue (SAXReaderDefaultSettings.requiresNewXMLParser ());

    SAXReaderDefaultSettings.setFeatureValue (EXMLParserFeature.NAMESPACES, (Boolean) null);
    assertFalse (SAXReaderDefaultSettings.hasAnyFeature ());

    final ICommonsMap <EXMLParserFeature, Boolean> aFeatures = new CommonsEnumMap <> (EXMLParserFeature.class);
    aFeatures.put (EXMLParserFeature.NAMESPACES, Boolean.TRUE);
    SAXReaderDefaultSettings.setFeatureValues (aFeatures);
    assertTrue (SAXReaderDefaultSettings.hasAnyFeature ());
    SAXReaderDefaultSettings.setFeatureValues (null);
    assertTrue (SAXReaderDefaultSettings.hasAnyFeature ());

    assertSame (EChange.UNCHANGED, SAXReaderDefaultSettings.removeFeature (null));
    assertSame (EChange.CHANGED, SAXReaderDefaultSettings.removeFeature (EXMLParserFeature.NAMESPACES));
    assertSame (EChange.UNCHANGED, SAXReaderDefaultSettings.removeAllFeatures ());
  }

  @Test
  public void testRequiresNewXMLParserExplicitly ()
  {
    assertEquals (Boolean.valueOf (SAXReaderDefaultSettings.DEFAULT_REQUIRES_NEW_XML_PARSER_EXPLICITLY),
                  Boolean.valueOf (SAXReaderDefaultSettings.isRequiresNewXMLParserExplicitly ()));
    assertFalse (SAXReaderDefaultSettings.requiresNewXMLParser ());

    SAXReaderDefaultSettings.setRequiresNewXMLParserExplicitly (true);
    assertTrue (SAXReaderDefaultSettings.isRequiresNewXMLParserExplicitly ());
    assertTrue (SAXReaderDefaultSettings.requiresNewXMLParser ());
  }

  @Test
  public void testEntityResolverRequiresNewXMLParser ()
  {
    assertFalse (SAXReaderDefaultSettings.requiresNewXMLParser ());
    SAXReaderDefaultSettings.setEntityResolver ((sPublicId, sSystemId) -> null);
    assertTrue (SAXReaderDefaultSettings.requiresNewXMLParser ());
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      SAXReaderDefaultSettings.setPropertyValue (null, "any");
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      SAXReaderDefaultSettings.setFeatureValue (null, true);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}

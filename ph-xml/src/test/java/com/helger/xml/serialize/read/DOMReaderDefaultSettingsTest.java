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

import com.helger.base.state.EChange;
import com.helger.collection.commons.CommonsEnumMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.xml.EXMLParserFeature;
import com.helger.xml.EXMLParserProperty;
import com.helger.xml.XMLFactory;
import com.helger.xml.sax.LoggingSAXErrorHandler;

/**
 * Test class for class {@link DOMReaderDefaultSettings}.
 *
 * @author Philip Helger
 */
public final class DOMReaderDefaultSettingsTest
{
  /**
   * These are static settings shared by the whole JVM, so they must be restored after every test.
   */
  @After
  public void restoreDefaults ()
  {
    DOMReaderDefaultSettings.setNamespaceAware (XMLFactory.DEFAULT_DOM_NAMESPACE_AWARE);
    DOMReaderDefaultSettings.setValidating (XMLFactory.DEFAULT_DOM_VALIDATING);
    DOMReaderDefaultSettings.setIgnoringElementContentWhitespace (XMLFactory.DEFAULT_DOM_IGNORING_ELEMENT_CONTENT_WHITESPACE);
    DOMReaderDefaultSettings.setExpandEntityReferences (XMLFactory.DEFAULT_DOM_EXPAND_ENTITY_REFERENCES);
    DOMReaderDefaultSettings.setIgnoringComments (XMLFactory.DEFAULT_DOM_IGNORING_COMMENTS);
    DOMReaderDefaultSettings.setCoalescing (XMLFactory.DEFAULT_DOM_COALESCING);
    DOMReaderDefaultSettings.setSchema (null);
    DOMReaderDefaultSettings.setXIncludeAware (XMLFactory.DEFAULT_DOM_XINCLUDE_AWARE);
    DOMReaderDefaultSettings.removeAllPropertyValues ();
    DOMReaderDefaultSettings.removeAllFeatures ();
    DOMReaderDefaultSettings.setEntityResolver (null);
    DOMReaderDefaultSettings.setErrorHandler (new LoggingSAXErrorHandler ());
    DOMReaderDefaultSettings.setRequiresNewXMLParserExplicitly (DOMReaderDefaultSettings.DEFAULT_REQUIRES_NEW_XML_PARSER_EXPLICITLY);
  }

  @Test
  public void testBooleanSettings ()
  {
    assertEquals (Boolean.valueOf (XMLFactory.DEFAULT_DOM_NAMESPACE_AWARE),
                  Boolean.valueOf (DOMReaderDefaultSettings.isNamespaceAware ()));
    DOMReaderDefaultSettings.setNamespaceAware (!XMLFactory.DEFAULT_DOM_NAMESPACE_AWARE);
    assertEquals (Boolean.valueOf (!XMLFactory.DEFAULT_DOM_NAMESPACE_AWARE),
                  Boolean.valueOf (DOMReaderDefaultSettings.isNamespaceAware ()));

    assertEquals (Boolean.valueOf (XMLFactory.DEFAULT_DOM_VALIDATING),
                  Boolean.valueOf (DOMReaderDefaultSettings.isValidating ()));
    DOMReaderDefaultSettings.setValidating (!XMLFactory.DEFAULT_DOM_VALIDATING);
    assertEquals (Boolean.valueOf (!XMLFactory.DEFAULT_DOM_VALIDATING),
                  Boolean.valueOf (DOMReaderDefaultSettings.isValidating ()));

    assertEquals (Boolean.valueOf (XMLFactory.DEFAULT_DOM_IGNORING_ELEMENT_CONTENT_WHITESPACE),
                  Boolean.valueOf (DOMReaderDefaultSettings.isIgnoringElementContentWhitespace ()));
    DOMReaderDefaultSettings.setIgnoringElementContentWhitespace (!XMLFactory.DEFAULT_DOM_IGNORING_ELEMENT_CONTENT_WHITESPACE);
    assertEquals (Boolean.valueOf (!XMLFactory.DEFAULT_DOM_IGNORING_ELEMENT_CONTENT_WHITESPACE),
                  Boolean.valueOf (DOMReaderDefaultSettings.isIgnoringElementContentWhitespace ()));

    assertEquals (Boolean.valueOf (XMLFactory.DEFAULT_DOM_EXPAND_ENTITY_REFERENCES),
                  Boolean.valueOf (DOMReaderDefaultSettings.isExpandEntityReferences ()));
    DOMReaderDefaultSettings.setExpandEntityReferences (!XMLFactory.DEFAULT_DOM_EXPAND_ENTITY_REFERENCES);
    assertEquals (Boolean.valueOf (!XMLFactory.DEFAULT_DOM_EXPAND_ENTITY_REFERENCES),
                  Boolean.valueOf (DOMReaderDefaultSettings.isExpandEntityReferences ()));

    assertEquals (Boolean.valueOf (XMLFactory.DEFAULT_DOM_IGNORING_COMMENTS),
                  Boolean.valueOf (DOMReaderDefaultSettings.isIgnoringComments ()));
    DOMReaderDefaultSettings.setIgnoringComments (!XMLFactory.DEFAULT_DOM_IGNORING_COMMENTS);
    assertEquals (Boolean.valueOf (!XMLFactory.DEFAULT_DOM_IGNORING_COMMENTS),
                  Boolean.valueOf (DOMReaderDefaultSettings.isIgnoringComments ()));

    assertEquals (Boolean.valueOf (XMLFactory.DEFAULT_DOM_COALESCING),
                  Boolean.valueOf (DOMReaderDefaultSettings.isCoalescing ()));
    DOMReaderDefaultSettings.setCoalescing (!XMLFactory.DEFAULT_DOM_COALESCING);
    assertEquals (Boolean.valueOf (!XMLFactory.DEFAULT_DOM_COALESCING),
                  Boolean.valueOf (DOMReaderDefaultSettings.isCoalescing ()));

    assertEquals (Boolean.valueOf (XMLFactory.DEFAULT_DOM_XINCLUDE_AWARE),
                  Boolean.valueOf (DOMReaderDefaultSettings.isXIncludeAware ()));
    DOMReaderDefaultSettings.setXIncludeAware (!XMLFactory.DEFAULT_DOM_XINCLUDE_AWARE);
    assertEquals (Boolean.valueOf (!XMLFactory.DEFAULT_DOM_XINCLUDE_AWARE),
                  Boolean.valueOf (DOMReaderDefaultSettings.isXIncludeAware ()));
  }

  @Test
  public void testSchema ()
  {
    assertNull (DOMReaderDefaultSettings.getSchema ());
    // A new XML parser is needed as soon as a Schema is present
    assertFalse (DOMReaderDefaultSettings.requiresNewXMLParser ());
  }

  @Test
  public void testProperties ()
  {
    assertFalse (DOMReaderDefaultSettings.hasAnyProperties ());
    assertTrue (DOMReaderDefaultSettings.getAllPropertyValues ().isEmpty ());
    assertNull (DOMReaderDefaultSettings.getPropertyValue (null));
    assertNull (DOMReaderDefaultSettings.getPropertyValue (EXMLParserProperty.GENERAL_XML_STRING));

    DOMReaderDefaultSettings.setPropertyValue (EXMLParserProperty.GENERAL_XML_STRING, "any");
    assertTrue (DOMReaderDefaultSettings.hasAnyProperties ());
    assertEquals ("any", DOMReaderDefaultSettings.getPropertyValue (EXMLParserProperty.GENERAL_XML_STRING));
    assertEquals (1, DOMReaderDefaultSettings.getAllPropertyValues ().size ());
    assertTrue (DOMReaderDefaultSettings.requiresNewXMLParser ());

    // A null value removes the property
    DOMReaderDefaultSettings.setPropertyValue (EXMLParserProperty.GENERAL_XML_STRING, null);
    assertFalse (DOMReaderDefaultSettings.hasAnyProperties ());

    final ICommonsMap <EXMLParserProperty, Object> aProps = new CommonsEnumMap <> (EXMLParserProperty.class);
    aProps.put (EXMLParserProperty.GENERAL_XML_STRING, "any");
    DOMReaderDefaultSettings.setPropertyValues (aProps);
    assertTrue (DOMReaderDefaultSettings.hasAnyProperties ());
    // null is simply ignored
    DOMReaderDefaultSettings.setPropertyValues (null);
    assertTrue (DOMReaderDefaultSettings.hasAnyProperties ());

    assertSame (EChange.UNCHANGED, DOMReaderDefaultSettings.removePropertyValue (null));
    assertSame (EChange.CHANGED, DOMReaderDefaultSettings.removePropertyValue (EXMLParserProperty.GENERAL_XML_STRING));
    assertSame (EChange.UNCHANGED,
                DOMReaderDefaultSettings.removePropertyValue (EXMLParserProperty.GENERAL_XML_STRING));
    assertSame (EChange.UNCHANGED, DOMReaderDefaultSettings.removeAllPropertyValues ());
  }

  @Test
  public void testFeatures ()
  {
    assertFalse (DOMReaderDefaultSettings.hasAnyFeature ());
    assertTrue (DOMReaderDefaultSettings.getAllFeatureValues ().isEmpty ());
    assertNull (DOMReaderDefaultSettings.getFeatureValue (null));
    assertNull (DOMReaderDefaultSettings.getFeatureValue (EXMLParserFeature.NAMESPACES));

    DOMReaderDefaultSettings.setFeatureValue (EXMLParserFeature.NAMESPACES, true);
    assertTrue (DOMReaderDefaultSettings.hasAnyFeature ());
    assertEquals (Boolean.TRUE, DOMReaderDefaultSettings.getFeatureValue (EXMLParserFeature.NAMESPACES));
    assertEquals (1, DOMReaderDefaultSettings.getAllFeatureValues ().size ());
    assertTrue (DOMReaderDefaultSettings.requiresNewXMLParser ());

    // A null value removes the feature
    DOMReaderDefaultSettings.setFeatureValue (EXMLParserFeature.NAMESPACES, (Boolean) null);
    assertFalse (DOMReaderDefaultSettings.hasAnyFeature ());

    final ICommonsMap <EXMLParserFeature, Boolean> aFeatures = new CommonsEnumMap <> (EXMLParserFeature.class);
    aFeatures.put (EXMLParserFeature.NAMESPACES, Boolean.TRUE);
    DOMReaderDefaultSettings.setFeatureValues (aFeatures);
    assertTrue (DOMReaderDefaultSettings.hasAnyFeature ());
    DOMReaderDefaultSettings.setFeatureValues (null);
    assertTrue (DOMReaderDefaultSettings.hasAnyFeature ());

    assertSame (EChange.UNCHANGED, DOMReaderDefaultSettings.removeFeature (null));
    assertSame (EChange.CHANGED, DOMReaderDefaultSettings.removeFeature (EXMLParserFeature.NAMESPACES));
    assertSame (EChange.UNCHANGED, DOMReaderDefaultSettings.removeFeature (EXMLParserFeature.NAMESPACES));
    assertSame (EChange.UNCHANGED, DOMReaderDefaultSettings.removeAllFeatures ());
  }

  @Test
  public void testHandlers ()
  {
    assertNull (DOMReaderDefaultSettings.getEntityResolver ());
    assertNotNull (DOMReaderDefaultSettings.getErrorHandler ());
    assertNotNull (DOMReaderDefaultSettings.exceptionCallbacks ());
    assertTrue (DOMReaderDefaultSettings.exceptionCallbacks ().isNotEmpty ());

    final ErrorHandler aEH = new LoggingSAXErrorHandler ();
    DOMReaderDefaultSettings.setErrorHandler (aEH);
    assertSame (aEH, DOMReaderDefaultSettings.getErrorHandler ());

    // An entity resolver forces a new XML parser
    assertFalse (DOMReaderDefaultSettings.requiresNewXMLParser ());
    DOMReaderDefaultSettings.setEntityResolver ((sPublicId, sSystemId) -> null);
    assertNotNull (DOMReaderDefaultSettings.getEntityResolver ());
    assertTrue (DOMReaderDefaultSettings.requiresNewXMLParser ());
  }

  @Test
  public void testRequiresNewXMLParserExplicitly ()
  {
    assertEquals (Boolean.valueOf (DOMReaderDefaultSettings.DEFAULT_REQUIRES_NEW_XML_PARSER_EXPLICITLY),
                  Boolean.valueOf (DOMReaderDefaultSettings.isRequiresNewXMLParserExplicitly ()));
    assertFalse (DOMReaderDefaultSettings.requiresNewXMLParser ());

    DOMReaderDefaultSettings.setRequiresNewXMLParserExplicitly (true);
    assertTrue (DOMReaderDefaultSettings.isRequiresNewXMLParserExplicitly ());
    assertTrue (DOMReaderDefaultSettings.requiresNewXMLParser ());
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      DOMReaderDefaultSettings.setPropertyValue (null, "any");
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      DOMReaderDefaultSettings.setFeatureValue (null, true);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}

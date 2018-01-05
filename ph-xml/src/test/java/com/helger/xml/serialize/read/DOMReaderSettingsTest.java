/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.helger.commons.mock.CommonsAssert;
import com.helger.xml.XMLFactory;

/**
 * Test class for {@link DOMReaderSettings}
 *
 * @author Philip Helger
 */
public final class DOMReaderSettingsTest
{
  @BeforeClass
  public static void bc ()
  {
    DOMReaderDefaultSettings.exceptionCallbacks ().removeAll ();
  }

  @AfterClass
  public static void ac ()
  {
    DOMReaderDefaultSettings.exceptionCallbacks ().set (new XMLLoggingExceptionCallback ());
  }

  @Test
  public void testDefault ()
  {
    final DOMReaderSettings aDRS = new DOMReaderSettings ();
    assertNotNull (aDRS);
    assertFalse (aDRS.requiresNewXMLParser ());
    CommonsAssert.assertEquals (XMLFactory.DEFAULT_DOM_NAMESPACE_AWARE, aDRS.isNamespaceAware ());
    CommonsAssert.assertEquals (XMLFactory.DEFAULT_DOM_VALIDATING, aDRS.isValidating ());
    CommonsAssert.assertEquals (XMLFactory.DEFAULT_DOM_IGNORING_ELEMENT_CONTENT_WHITESPACE,
                                aDRS.isIgnoringElementContentWhitespace ());
    CommonsAssert.assertEquals (XMLFactory.DEFAULT_DOM_EXPAND_ENTITY_REFERENCES, aDRS.isExpandEntityReferences ());
    CommonsAssert.assertEquals (XMLFactory.DEFAULT_DOM_IGNORING_COMMENTS, aDRS.isIgnoringComments ());
    CommonsAssert.assertEquals (XMLFactory.DEFAULT_DOM_COALESCING, aDRS.isCoalescing ());
    assertNull (aDRS.getSchema ());
    CommonsAssert.assertEquals (XMLFactory.DEFAULT_DOM_XINCLUDE_AWARE, aDRS.isXIncludeAware ());
    assertNull (aDRS.getEntityResolver ());
    assertNotNull (aDRS.getErrorHandler ());
    assertNotNull (aDRS.exceptionCallbacks ());
    assertFalse (aDRS.isRequiresNewXMLParserExplicitly ());

    assertFalse (aDRS.requiresNewXMLParser ());
    aDRS.setRequiresNewXMLParserExplicitly (true);
    assertTrue (aDRS.isRequiresNewXMLParserExplicitly ());
    assertTrue (aDRS.requiresNewXMLParser ());
    aDRS.setRequiresNewXMLParserExplicitly (false);
    assertFalse (aDRS.isRequiresNewXMLParserExplicitly ());
    assertFalse (aDRS.requiresNewXMLParser ());
  }
}

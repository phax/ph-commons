/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.xml.serialize.read;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.xml.XMLFactory;

/**
 * Test class for {@link DOMReaderDefaultSettings}
 *
 * @author Philip Helger
 */
public final class DOMReaderDefaultSettingsTest
{
  @Test
  public void testDefault ()
  {
    assertFalse (DOMReaderDefaultSettings.requiresNewXMLParser ());
    assertTrue (XMLFactory.DEFAULT_DOM_NAMESPACE_AWARE == DOMReaderDefaultSettings.isNamespaceAware ());
    assertTrue (XMLFactory.DEFAULT_DOM_VALIDATING == DOMReaderDefaultSettings.isValidating ());
    assertTrue (XMLFactory.DEFAULT_DOM_IGNORING_ELEMENT_CONTENT_WHITESPACE == DOMReaderDefaultSettings.isIgnoringElementContentWhitespace ());
    assertTrue (XMLFactory.DEFAULT_DOM_EXPAND_ENTITY_REFERENCES == DOMReaderDefaultSettings.isExpandEntityReferences ());
    assertTrue (XMLFactory.DEFAULT_DOM_IGNORING_COMMENTS == DOMReaderDefaultSettings.isIgnoringComments ());
    assertTrue (XMLFactory.DEFAULT_DOM_COALESCING == DOMReaderDefaultSettings.isCoalescing ());
    assertNull (DOMReaderDefaultSettings.getSchema ());
    assertTrue (XMLFactory.DEFAULT_DOM_XINCLUDE_AWARE == DOMReaderDefaultSettings.isXIncludeAware ());
    assertNull (DOMReaderDefaultSettings.getEntityResolver ());
    assertNotNull (DOMReaderDefaultSettings.getErrorHandler ());
    assertNotNull (DOMReaderDefaultSettings.getExceptionHandler ());
    assertFalse (DOMReaderDefaultSettings.isRequiresNewXMLParserExplicitly ());

    assertFalse (DOMReaderDefaultSettings.requiresNewXMLParser ());
    DOMReaderDefaultSettings.setRequiresNewXMLParserExplicitly (true);
    assertTrue (DOMReaderDefaultSettings.isRequiresNewXMLParserExplicitly ());
    assertTrue (DOMReaderDefaultSettings.requiresNewXMLParser ());
    DOMReaderDefaultSettings.setRequiresNewXMLParserExplicitly (false);
    assertFalse (DOMReaderDefaultSettings.isRequiresNewXMLParserExplicitly ());
    assertFalse (DOMReaderDefaultSettings.requiresNewXMLParser ());
  }
}

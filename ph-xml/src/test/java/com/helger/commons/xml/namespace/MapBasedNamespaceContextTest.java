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
package com.helger.commons.xml.namespace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.xml.XMLConstants;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link MapBasedNamespaceContext}.
 *
 * @author Philip Helger
 */
public final class MapBasedNamespaceContextTest
{
  @Test
  public void testAll ()
  {
    final MapBasedNamespaceContext c = new MapBasedNamespaceContext ();
    assertNull (c.getDefaultNamespaceURI ());
    assertNotNull (c.getPrefixes ("http://1"));
    assertFalse (c.getPrefixes ("http://1").hasNext ());
    assertTrue (c.getPrefixes (XMLConstants.XML_NS_URI).hasNext ());
    assertTrue (c.getPrefixes (XMLConstants.XMLNS_ATTRIBUTE_NS_URI).hasNext ());

    assertNull (c.getPrefix ("http://1"));
    assertEquals (XMLConstants.XML_NS_PREFIX, c.getPrefix (XMLConstants.XML_NS_URI));
    assertEquals (XMLConstants.XMLNS_ATTRIBUTE, c.getPrefix (XMLConstants.XMLNS_ATTRIBUTE_NS_URI));

    assertEquals (XMLConstants.NULL_NS_URI, c.getNamespaceURI (XMLConstants.DEFAULT_NS_PREFIX));
    assertEquals (XMLConstants.XML_NS_URI, c.getNamespaceURI (XMLConstants.XML_NS_PREFIX));
    assertEquals (XMLConstants.XMLNS_ATTRIBUTE_NS_URI, c.getNamespaceURI (XMLConstants.XMLNS_ATTRIBUTE));
    assertEquals (XMLConstants.NULL_NS_URI, c.getNamespaceURI ("tns"));

    // Add any mapping
    c.addMapping ("tns", "http://1");
    assertNull (c.getDefaultNamespaceURI ());
    assertTrue (c.getPrefixes ("http://1").hasNext ());
    assertTrue (c.getPrefixes (XMLConstants.XML_NS_URI).hasNext ());
    assertTrue (c.getPrefixes (XMLConstants.XMLNS_ATTRIBUTE_NS_URI).hasNext ());

    assertEquals ("tns", c.getPrefix ("http://1"));
    assertEquals (XMLConstants.XML_NS_PREFIX, c.getPrefix (XMLConstants.XML_NS_URI));
    assertEquals (XMLConstants.XMLNS_ATTRIBUTE, c.getPrefix (XMLConstants.XMLNS_ATTRIBUTE_NS_URI));

    assertEquals (XMLConstants.NULL_NS_URI, c.getNamespaceURI (XMLConstants.DEFAULT_NS_PREFIX));
    assertEquals (XMLConstants.XML_NS_URI, c.getNamespaceURI (XMLConstants.XML_NS_PREFIX));
    assertEquals (XMLConstants.XMLNS_ATTRIBUTE_NS_URI, c.getNamespaceURI (XMLConstants.XMLNS_ATTRIBUTE));
    assertEquals ("http://1", c.getNamespaceURI ("tns"));

    // Add default mapping
    c.addMapping (XMLConstants.DEFAULT_NS_PREFIX, "http://default");
    assertEquals ("http://default", c.getDefaultNamespaceURI ());
    assertTrue (c.getPrefixes ("http://1").hasNext ());
    assertTrue (c.getPrefixes (XMLConstants.XML_NS_URI).hasNext ());
    assertTrue (c.getPrefixes (XMLConstants.XMLNS_ATTRIBUTE_NS_URI).hasNext ());

    assertEquals (XMLConstants.DEFAULT_NS_PREFIX, c.getPrefix ("http://default"));
    assertEquals ("tns", c.getPrefix ("http://1"));
    assertEquals (XMLConstants.XML_NS_PREFIX, c.getPrefix (XMLConstants.XML_NS_URI));
    assertEquals (XMLConstants.XMLNS_ATTRIBUTE, c.getPrefix (XMLConstants.XMLNS_ATTRIBUTE_NS_URI));

    assertEquals ("http://default", c.getNamespaceURI (XMLConstants.DEFAULT_NS_PREFIX));
    assertEquals (XMLConstants.XML_NS_URI, c.getNamespaceURI (XMLConstants.XML_NS_PREFIX));
    assertEquals (XMLConstants.XMLNS_ATTRIBUTE_NS_URI, c.getNamespaceURI (XMLConstants.XMLNS_ATTRIBUTE));
    assertEquals ("http://1", c.getNamespaceURI ("tns"));
    assertEquals ("http://default", c.getNamespaceURI (XMLConstants.DEFAULT_NS_PREFIX));

    CommonsTestHelper.testToStringImplementation (c);
    CommonsTestHelper.testDefaultSerialization (c);
  }
}

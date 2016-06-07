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
import static org.junit.Assert.fail;

import javax.xml.XMLConstants;

import org.junit.Test;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link SingleElementNamespaceContext}.
 *
 * @author Philip Helger
 */
public final class SingleElementNamespaceContextTest
{

  @Test
  @SuppressFBWarnings ({ "NP_NULL_PARAM_DEREF_NONVIRTUAL", "NP_NONNULL_PARAM_VIOLATION" })
  public void testCtor ()
  {
    try
    {
      new SingleElementNamespaceContext (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      new SingleElementNamespaceContext (null, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testAll ()
  {
    // Default namespace
    SingleElementNamespaceContext c = new SingleElementNamespaceContext ("myuri");
    assertEquals ("myuri", c.getDefaultNamespaceURI ());
    assertTrue (c.getPrefixes (XMLConstants.XML_NS_URI).hasNext ());
    assertTrue (c.getPrefixes (XMLConstants.XMLNS_ATTRIBUTE_NS_URI).hasNext ());
    assertTrue (c.getPrefixes ("myuri").hasNext ());
    assertFalse (c.getPrefixes ("myuri2").hasNext ());

    assertNull (c.getPrefix ("http://1"));
    assertEquals (XMLConstants.DEFAULT_NS_PREFIX, c.getPrefix ("myuri"));
    assertEquals (XMLConstants.XML_NS_PREFIX, c.getPrefix (XMLConstants.XML_NS_URI));
    assertEquals (XMLConstants.XMLNS_ATTRIBUTE, c.getPrefix (XMLConstants.XMLNS_ATTRIBUTE_NS_URI));

    assertEquals ("myuri", c.getNamespaceURI (XMLConstants.DEFAULT_NS_PREFIX));
    assertEquals (XMLConstants.XML_NS_URI, c.getNamespaceURI (XMLConstants.XML_NS_PREFIX));
    assertEquals (XMLConstants.XMLNS_ATTRIBUTE_NS_URI, c.getNamespaceURI (XMLConstants.XMLNS_ATTRIBUTE));
    assertEquals (XMLConstants.NULL_NS_URI, c.getNamespaceURI ("tns"));

    assertNotNull (c.toString ());

    // No default prefix
    c = new SingleElementNamespaceContext ("prefix", "myuri");
    assertNull (c.getDefaultNamespaceURI ());
    assertTrue (c.getPrefixes (XMLConstants.XML_NS_URI).hasNext ());
    assertTrue (c.getPrefixes (XMLConstants.XMLNS_ATTRIBUTE_NS_URI).hasNext ());
    assertTrue (c.getPrefixes ("myuri").hasNext ());
    assertFalse (c.getPrefixes ("myuri2").hasNext ());

    assertNull (c.getPrefix ("http://1"));
    assertEquals ("prefix", c.getPrefix ("myuri"));
    assertEquals (XMLConstants.XML_NS_PREFIX, c.getPrefix (XMLConstants.XML_NS_URI));
    assertEquals (XMLConstants.XMLNS_ATTRIBUTE, c.getPrefix (XMLConstants.XMLNS_ATTRIBUTE_NS_URI));

    assertEquals ("myuri", c.getNamespaceURI ("prefix"));
    assertEquals (XMLConstants.NULL_NS_URI, c.getNamespaceURI (XMLConstants.DEFAULT_NS_PREFIX));
    assertEquals (XMLConstants.XML_NS_URI, c.getNamespaceURI (XMLConstants.XML_NS_PREFIX));
    assertEquals (XMLConstants.XMLNS_ATTRIBUTE_NS_URI, c.getNamespaceURI (XMLConstants.XMLNS_ATTRIBUTE));
    assertEquals (XMLConstants.NULL_NS_URI, c.getNamespaceURI ("tns"));
  }
}

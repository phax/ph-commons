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
package com.helger.xml.xpath;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;

import com.helger.xml.namespace.MapBasedNamespaceContext;

/**
 * Test class for class {@link XPathHelper}.
 *
 * @author Philip Helger
 */
public final class XPathHelperTest
{
  private static final NamespaceContext NS_CTX = new MapBasedNamespaceContext ().addMapping ("x", "urn:example");

  @Test
  public void testGetDefaultXPathFactory ()
  {
    final XPathFactory aFactory = XPathHelper.getDefaultXPathFactory ();
    assertNotNull (aFactory);
    // Always the same instance
    assertSame (aFactory, XPathHelper.getDefaultXPathFactory ());
  }

  @Test
  public void testCreateXPathFactorySaxonFirst ()
  {
    // Saxon is not on the class path, so the JDK default is used
    assertNotNull (XPathHelper.createXPathFactorySaxonFirst ());
  }

  @Test
  public void testCreateNewXPath ()
  {
    final XPathFactory aFactory = XPathHelper.getDefaultXPathFactory ();
    final MapBasedXPathVariableResolver aVR = new MapBasedXPathVariableResolver ();
    final MapBasedXPathFunctionResolver aFR = new MapBasedXPathFunctionResolver ();

    assertNotNull (XPathHelper.createNewXPath ());
    assertNotNull (XPathHelper.createNewXPath (aFactory));
    assertNotNull (XPathHelper.createNewXPath (aVR));
    assertNotNull (XPathHelper.createNewXPath (aFactory, aVR));
    assertNotNull (XPathHelper.createNewXPath (aFR));
    assertNotNull (XPathHelper.createNewXPath (aFactory, aFR));
    assertNotNull (XPathHelper.createNewXPath (NS_CTX));
    assertNotNull (XPathHelper.createNewXPath (aFactory, NS_CTX));
    assertNotNull (XPathHelper.createNewXPath (aVR, NS_CTX));
    assertNotNull (XPathHelper.createNewXPath (aVR, aFR, NS_CTX));
    assertNotNull (XPathHelper.createNewXPath (aFactory, aVR, aFR, NS_CTX));

    // All optional parameters may be null
    assertNotNull (XPathHelper.createNewXPath (aFactory, null, null, null));
  }

  @Test
  public void testCreateNewXPathExpression ()
  {
    final MapBasedXPathVariableResolver aVR = new MapBasedXPathVariableResolver ();
    final MapBasedXPathFunctionResolver aFR = new MapBasedXPathFunctionResolver ();
    final XPath aXPath = XPathHelper.createNewXPath ();

    assertNotNull (XPathHelper.createNewXPathExpression ("/root"));
    assertNotNull (XPathHelper.createNewXPathExpression (aXPath, "/root"));
    assertNotNull (XPathHelper.createNewXPathExpression (aVR, "/root"));
    assertNotNull (XPathHelper.createNewXPathExpression (aFR, "/root"));
    assertNotNull (XPathHelper.createNewXPathExpression (NS_CTX, "/root"));
    assertNotNull (XPathHelper.createNewXPathExpression (aVR, aFR, NS_CTX, "/root"));
  }

  @Test
  public void testInvalidXPathExpression ()
  {
    try
    {
      XPathHelper.createNewXPathExpression ("this is /// not a valid xpath");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }
}

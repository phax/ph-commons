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
package com.helger.xml.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;

import org.junit.Test;

import com.helger.io.resource.ClassPathResource;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link DefaultTransformURIResolver}.
 *
 * @author Philip Helger
 */
public final class DefaultTransformURIResolverTest
{
  @Test
  public void testAll ()
  {
    for (int i = 0; i < 2; ++i)
    {
      final DefaultTransformURIResolver res = new DefaultTransformURIResolver (i == 0 ? null
                                                                                      : new LoggingTransformURIResolver ());
      final TransformerFactory fac = XMLTransformerFactory.createTransformerFactory (null, res);
      assertNotNull (fac);

      // Read valid XSLT
      Templates t1 = XMLTransformerFactory.newTemplates (fac, new ClassPathResource ("xml/test1.xslt"));
      assertNotNull (t1);

      // Read valid XSLT with valid include
      t1 = XMLTransformerFactory.newTemplates (fac, new ClassPathResource ("xml/test2.xslt"));
      assertNotNull (t1);

      // Read valid XSLT with invalid include
      t1 = XMLTransformerFactory.newTemplates (fac, new ClassPathResource ("xml/test3.xslt"));
      assertNull (t1);

      TestHelper.testToStringImplementation (res);
    }
  }

  @Test
  public void testRemoteSchemeBlocked () throws TransformerException
  {
    final DefaultTransformURIResolver res = new DefaultTransformURIResolver ();
    assertTrue (res.getAllAllowedRemoteSchemes ().isEmpty ());

    // Port 1 is never listening - the resolution must not even try to connect
    final Source aSource = res.resolve ("http://127.0.0.1:1/evil.xml", null);
    /*
     * Must not be null: a TransformerFactory that honours neither secure processing nor the
     * "accessExternalStylesheet" attribute treats null as "not handled" and opens the URI itself.
     */
    assertNotNull (aSource);
    assertTrue (aSource instanceof DOMSource);
    // An empty document - "document()" evaluates to an empty node set
    assertNull ( ((DOMSource) aSource).getNode ().getFirstChild ());
  }

  @Test
  public void testRemoteSchemeBlockedWrappedResolverNotCalled () throws TransformerException
  {
    // A blocked resource must not fall through to the wrapped resolver either
    final MockURIResolver aWrapped = new MockURIResolver ();
    final DefaultTransformURIResolver res = new DefaultTransformURIResolver (aWrapped);
    assertNotNull (res.resolve ("http://127.0.0.1:1/evil.xml", null));
    assertEquals (0, aWrapped.getInvocationCount ());
  }

  @Test
  public void testTransformerFactoryIsSecure ()
  {
    final TransformerFactory fac = XMLTransformerFactory.createTransformerFactory (null, null);
    assertNotNull (fac);
    assertTrue (fac.getFeature (XMLConstants.FEATURE_SECURE_PROCESSING));
    assertEquals ("", fac.getAttribute (XMLConstants.ACCESS_EXTERNAL_DTD));
    assertEquals ("", fac.getAttribute (XMLConstants.ACCESS_EXTERNAL_STYLESHEET));
  }

  private static final class MockURIResolver implements URIResolver
  {
    private int m_nCount = 0;

    public int getInvocationCount ()
    {
      return m_nCount;
    }

    public Source resolve (final String sHref, final String sBase)
    {
      m_nCount++;
      return null;
    }
  }
}

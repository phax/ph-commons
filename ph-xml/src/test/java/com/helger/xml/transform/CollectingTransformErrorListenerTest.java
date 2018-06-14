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
package com.helger.xml.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;

import org.junit.Test;
import org.w3c.dom.Document;

import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.xml.XMLFactory;

/**
 * Test class for class {@link CollectingTransformErrorListener}.
 *
 * @author Philip Helger
 */
public final class CollectingTransformErrorListenerTest
{
  public static final Locale L_EN = new Locale ("en");

  @Test
  public void testAll () throws TransformerConfigurationException, TransformerException
  {
    final CollectingTransformErrorListener el = new CollectingTransformErrorListener ();
    final TransformerFactory fac = XMLTransformerFactory.createTransformerFactory (el.andThen (new LoggingTransformErrorListener (L_EN)),
                                                                                   new LoggingTransformURIResolver ());
    assertNotNull (fac);

    // Read valid XSLT
    Templates t1 = XMLTransformerFactory.newTemplates (fac, new ClassPathResource ("xml/test1.xslt"));
    assertNotNull (t1);
    // May contain warning in JDK 1.7
    // (http://javax.xml.XMLConstants/property/accessExternalDTD is unknown)
    assertTrue (el.getErrorList ().containsNoError ());

    // Try a real transformation
    {
      final Document aDoc = XMLFactory.newDocument ();
      t1.newTransformer ().transform (TransformSourceFactory.create (new ClassPathResource ("xml/xslt1.xml")),
                                      new DOMResult (aDoc));
      assertNotNull (aDoc);
      assertNotNull (aDoc.getDocumentElement ());
      assertEquals ("html", aDoc.getDocumentElement ().getTagName ());
    }

    // Read valid XSLT (with import)
    t1 = XMLTransformerFactory.newTemplates (fac, new ClassPathResource ("xml/test2.xslt"));
    assertNotNull (t1);
    // May contain warning in JDK 1.7
    // (http://javax.xml.XMLConstants/property/accessExternalDTD is unknown)
    assertTrue (el.getErrorList ().containsNoError ());

    // Read invalid XSLT
    assertNull (XMLTransformerFactory.newTemplates (fac, new ClassPathResource ("test1.txt")));
    assertTrue (el.getErrorList ().containsAtLeastOneError ());

    CommonsTestHelper.testToStringImplementation (el);
  }

  @Test
  public void testSecure () throws TransformerConfigurationException, TransformerException
  {
    final CollectingTransformErrorListener el = new CollectingTransformErrorListener ();
    final TransformerFactory fac = XMLTransformerFactory.createTransformerFactory (el.andThen (new LoggingTransformErrorListener (L_EN)),
                                                                                   new LoggingTransformURIResolver ());
    assertNotNull (fac);

    // "file" access is needed for the tests
    XMLTransformerFactory.makeTransformerFactorySecure (fac, "file");

    // Read valid XSLT
    Templates t1 = XMLTransformerFactory.newTemplates (fac, new ClassPathResource ("xml/test1.xslt"));
    assertNotNull (t1);
    // May contain warning in JDK 1.7
    // (http://javax.xml.XMLConstants/property/accessExternalDTD is unknown)
    assertTrue (el.getErrorList ().containsNoError ());

    // Try a real transformation
    {
      final Document aDoc = XMLFactory.newDocument ();
      t1.newTransformer ().transform (TransformSourceFactory.create (new ClassPathResource ("xml/xslt1.xml")),
                                      new DOMResult (aDoc));
      assertNotNull (aDoc);
      assertNotNull (aDoc.getDocumentElement ());
      assertEquals ("html", aDoc.getDocumentElement ().getTagName ());
    }

    // Read valid XSLT (with import)
    t1 = XMLTransformerFactory.newTemplates (fac, new ClassPathResource ("xml/test2.xslt"));
    assertNotNull (t1);
    // May contain warning in JDK 1.7
    // (http://javax.xml.XMLConstants/property/accessExternalDTD is unknown)
    assertTrue (el.getErrorList ().containsNoError ());

    // Read invalid XSLT
    assertNull (XMLTransformerFactory.newTemplates (fac, new ClassPathResource ("test1.txt")));
    assertTrue (el.getErrorList ().containsAtLeastOneError ());

    CommonsTestHelper.testToStringImplementation (el);
  }
}

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
package com.helger.commons.xml.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.helger.commons.error.IResourceErrorGroup;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.xml.serialize.read.DOMReader;

/**
 * Test class for class {@link XMLSchemaValidationHelper}.
 *
 * @author Philip Helger
 */
public final class XMLSchemaValidationHelperTest
{
  private static final IReadableResource XSD1 = new ClassPathResource ("xml/schema1.xsd");
  private static final IReadableResource XSD2 = new ClassPathResource ("xml/schema2.xsd");
  private static final IReadableResource XML1 = new ClassPathResource ("xml/schema1-valid.xml");
  private static final IReadableResource XML2 = new ClassPathResource ("xml/schema1-invalid.xml");

  @Test
  public void testValidateReadableResource ()
  {
    // Valid
    IResourceErrorGroup aErrors = XMLSchemaValidationHelper.validate (XSD1, XML1);
    assertNotNull (aErrors);
    assertEquals (0, aErrors.getSize ());
    aErrors = XMLSchemaValidationHelper.validate (new IReadableResource [] { XSD2, XSD1 }, XML1);
    assertNotNull (aErrors);
    assertEquals (0, aErrors.getSize ());

    // Invalid
    aErrors = XMLSchemaValidationHelper.validate (XSD1, XML2);
    assertNotNull (aErrors);
    assertEquals (1, aErrors.getSize ());
    aErrors = XMLSchemaValidationHelper.validate (new IReadableResource [] { XSD2, XSD1 }, XML2);
    assertNotNull (aErrors);
    assertEquals (1, aErrors.getSize ());

    try
    {
      // null XML not allowed
      XMLSchemaValidationHelper.validate (XSD1, (IReadableResource) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // null XML not allowed
      XMLSchemaValidationHelper.validate (new IReadableResource [] { XSD2, XSD1 }, (IReadableResource) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // Non XML instance
      XMLSchemaValidationHelper.validate (new IReadableResource [] { XSD2, XSD1 }, new ClassPathResource ("test1.txt"));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      // Non XML XSD
      XMLSchemaValidationHelper.validate (new ClassPathResource ("test1.txt"), XML1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testValidateDOMSource () throws SAXException
  {
    // Different source type
    Document aDoc = DOMReader.readXMLDOM (XML1);
    IResourceErrorGroup aErrors = XMLSchemaValidationHelper.validate (XSD1, new DOMSource (aDoc));
    assertNotNull (aErrors);
    assertEquals (0, aErrors.getSize ());
    aErrors = XMLSchemaValidationHelper.validate (new IReadableResource [] { XSD2, XSD1 }, new DOMSource (aDoc));
    assertNotNull (aErrors);
    assertEquals (0, aErrors.getSize ());

    aDoc = DOMReader.readXMLDOM (XML2);
    aErrors = XMLSchemaValidationHelper.validate (XSD1, new DOMSource (aDoc));
    assertNotNull (aErrors);
    assertEquals (1, aErrors.getSize ());
    aErrors = XMLSchemaValidationHelper.validate (new IReadableResource [] { XSD2, XSD1 }, new DOMSource (aDoc));
    assertNotNull (aErrors);
    assertEquals (1, aErrors.getSize ());

    try
    {
      // null XML not allowed
      XMLSchemaValidationHelper.validate (XSD1, (Source) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // null XML not allowed
      XMLSchemaValidationHelper.validate (new IReadableResource [] { XSD2, XSD1 }, (Source) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testValidateSchemaPresent () throws SAXException
  {
    final Schema aSchema1 = XMLSchemaCache.getInstance ().getSchema (XSD1);
    final Schema aSchema2 = XMLSchemaCache.getInstance ().getSchema (XSD2, XSD1);

    // Different source type
    Document aDoc = DOMReader.readXMLDOM (XML1);
    IResourceErrorGroup aErrors = XMLSchemaValidationHelper.validate (aSchema1, new DOMSource (aDoc));
    assertNotNull (aErrors);
    assertEquals (0, aErrors.getSize ());
    aErrors = XMLSchemaValidationHelper.validate (aSchema2, new DOMSource (aDoc));
    assertNotNull (aErrors);
    assertEquals (0, aErrors.getSize ());

    aDoc = DOMReader.readXMLDOM (XML2);
    aErrors = XMLSchemaValidationHelper.validate (aSchema1, XML1);
    assertNotNull (aErrors);
    assertEquals (0, aErrors.getSize ());
    aErrors = XMLSchemaValidationHelper.validate (aSchema2, XML1);
    assertNotNull (aErrors);
    assertEquals (0, aErrors.getSize ());

    try
    {
      // null XML not allowed
      XMLSchemaValidationHelper.validate (aSchema1, (IReadableResource) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // null XML not allowed
      XMLSchemaValidationHelper.validate (aSchema1, (Source) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // null XML not allowed
      XMLSchemaValidationHelper.validate ((Schema) null, new DOMSource (aDoc));
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}

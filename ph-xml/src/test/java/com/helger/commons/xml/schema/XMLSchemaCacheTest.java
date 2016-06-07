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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.xml.validation.Schema;

import org.junit.Test;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.xml.ls.CollectingLSResourceResolver;
import com.helger.commons.xml.ls.LSResourceData;
import com.helger.commons.xml.ls.LoggingLSResourceResolver;
import com.helger.commons.xml.sax.LoggingSAXErrorHandler;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link XMLSchemaCache}.
 *
 * @author Philip Helger
 */
public final class XMLSchemaCacheTest
{
  @Test
  public void testDefault ()
  {
    final XMLSchemaCache sc = XMLSchemaCache.getInstance ();
    assertNotNull (sc);

    // Valid schema
    Schema aSchema = sc.getSchema (new ClassPathResource ("xml/schema1.xsd"));
    assertNotNull (aSchema);
    assertNotNull (sc.getValidatorFromSchema (aSchema));

    // Valid schema (with includes)
    aSchema = sc.getSchema (new ClassPathResource ("xml/schema2.xsd"));
    assertNotNull (aSchema);
    assertNotNull (sc.getValidatorFromSchema (aSchema));

    // clear in the middle
    // Note: cannot expect success, because a WeakHashMap is used!
    sc.clearCache ();
    assertFalse (sc.clearCache ().isChanged ());

    // Valid schema
    aSchema = sc.getSchema (new ClassPathResource ("xml/schema1.xsd"), new ClassPathResource ("xml/schema1.xsd"));
    assertNotNull (aSchema);
    assertNotNull (sc.getValidatorFromSchema (aSchema));

    // Valid schema (with includes)
    aSchema = sc.getSchema (new ClassPathResource ("xml/schema1.xsd"), new ClassPathResource ("xml/schema2.xsd"));
    assertNotNull (aSchema);
    assertNotNull (sc.getValidatorFromSchema (aSchema));

    // remove afterwards
    final IReadableResource aPath = new ClassPathResource ("xml/schema1.xsd");
    // Note: cannot expect success, because a WeakHashMap is used!
    sc.removeFromCache (aPath);
    assertFalse (sc.removeFromCache (aPath).isChanged ());

    // Not a schema
    try
    {
      sc.getSchema (new ClassPathResource ("test1.txt"));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      sc.getSchema ((IReadableResource) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testCustom ()
  {
    @PresentForCodeCoverage
    XMLSchemaCache sc = new XMLSchemaCache (new LoggingSAXErrorHandler ());
    assertNotNull (sc);

    final CollectingLSResourceResolver crr = new CollectingLSResourceResolver ();
    sc = new XMLSchemaCache (new LoggingLSResourceResolver ().setWrappedResourceResolver (crr));
    assertNotNull (sc);

    // Valid schema
    Schema aSchema = sc.getSchema (new ClassPathResource ("xml/schema1.xsd"));
    assertNotNull (aSchema);
    assertTrue (crr.getAllRequestedResources ().isEmpty ());

    // Valid schema (with includes)
    aSchema = sc.getSchema (new ClassPathResource ("xml/schema2.xsd"));
    assertNotNull (aSchema);
    assertEquals (1, crr.getAllRequestedResources ().size ());
    final LSResourceData rd = crr.getAllRequestedResources ().get (0);
    assertEquals ("http://www.w3.org/2001/XMLSchema", rd.getType ());
    assertEquals ("http://www.example.org/schema1", rd.getNamespaceURI ());
    assertNull (rd.getPublicID ());
    assertEquals ("schema1.xsd", rd.getSystemID ());
    assertNotNull (rd.getBaseURI ());
    assertTrue (rd.getBaseURI ().endsWith ("xml/schema2.xsd"));

    // Not a schema
    try
    {
      sc.getSchema (new ClassPathResource ("test1.txt"));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // empty
    try
    {
      sc.getSchema ((IReadableResource []) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      sc.getSchema (new IReadableResource [0]);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}

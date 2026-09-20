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
package com.helger.jaxb.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.xml.namespace.NamespaceContext;

import org.junit.After;
import org.junit.Test;

import com.helger.xml.namespace.MapBasedNamespaceContext;

import jakarta.xml.bind.ValidationEventHandler;

/**
 * Test class for class {@link JAXBBuilderDefaultSettings}.
 *
 * @author Philip Helger
 */
public final class JAXBBuilderDefaultSettingsTest
{
  @After
  public void restoreDefaults ()
  {
    JAXBBuilderDefaultSettings.setDefaultUseContextCache (JAXBBuilderDefaultSettings.DEFAULT_USE_CONTEXT_CACHE);
    JAXBBuilderDefaultSettings.setDefaultValidationEventHandler (JAXBBuilderDefaultSettings.DEFAULT_VALIDATION_EVENT_HANDLER);
    JAXBBuilderDefaultSettings.setDefaultNamespaceContext (null);
    JAXBBuilderDefaultSettings.setDefaultFormattedOutput (JAXBBuilderDefaultSettings.DEFAULT_FORMATTED_OUTPUT);
    JAXBBuilderDefaultSettings.setDefaultCharset (JAXBBuilderDefaultSettings.DEFAULT_CHARSET);
    JAXBBuilderDefaultSettings.setDefaultIndentString (null);
    JAXBBuilderDefaultSettings.setDefaultUseSchema (JAXBBuilderDefaultSettings.DEFAULT_USE_SCHEMA);
    JAXBBuilderDefaultSettings.setDefaultSchemaLocation (null);
    JAXBBuilderDefaultSettings.setDefaultNoNamespaceSchemaLocation (null);
  }

  @Test
  public void testUseContextCache ()
  {
    assertTrue (JAXBBuilderDefaultSettings.DEFAULT_USE_CONTEXT_CACHE == JAXBBuilderDefaultSettings.isDefaultUseContextCache ());
    JAXBBuilderDefaultSettings.setDefaultUseContextCache (false);
    assertFalse (JAXBBuilderDefaultSettings.isDefaultUseContextCache ());
    JAXBBuilderDefaultSettings.setDefaultUseContextCache (true);
    assertTrue (JAXBBuilderDefaultSettings.isDefaultUseContextCache ());
  }

  @Test
  public void testValidationEventHandler ()
  {
    assertSame (JAXBBuilderDefaultSettings.DEFAULT_VALIDATION_EVENT_HANDLER,
                JAXBBuilderDefaultSettings.getDefaultValidationEventHandler ());
    final ValidationEventHandler aVEH = aEvent -> true;
    JAXBBuilderDefaultSettings.setDefaultValidationEventHandler (aVEH);
    assertSame (aVEH, JAXBBuilderDefaultSettings.getDefaultValidationEventHandler ());
    JAXBBuilderDefaultSettings.setDefaultValidationEventHandler (null);
    assertNull (JAXBBuilderDefaultSettings.getDefaultValidationEventHandler ());
  }

  @Test
  public void testNamespaceContext ()
  {
    assertNull (JAXBBuilderDefaultSettings.getDefaultNamespaceContext ());
    final NamespaceContext aNC = new MapBasedNamespaceContext ().addMapping ("p", "urn:example");
    JAXBBuilderDefaultSettings.setDefaultNamespaceContext (aNC);
    assertSame (aNC, JAXBBuilderDefaultSettings.getDefaultNamespaceContext ());
  }

  @Test
  public void testFormattedOutput ()
  {
    assertTrue (JAXBBuilderDefaultSettings.DEFAULT_FORMATTED_OUTPUT == JAXBBuilderDefaultSettings.isDefaultFormattedOutput ());
    JAXBBuilderDefaultSettings.setDefaultFormattedOutput (true);
    assertTrue (JAXBBuilderDefaultSettings.isDefaultFormattedOutput ());
  }

  @Test
  public void testCharset ()
  {
    assertEquals (JAXBBuilderDefaultSettings.DEFAULT_CHARSET, JAXBBuilderDefaultSettings.getDefaultCharset ());
    final Charset aCS = StandardCharsets.ISO_8859_1;
    JAXBBuilderDefaultSettings.setDefaultCharset (aCS);
    assertEquals (aCS, JAXBBuilderDefaultSettings.getDefaultCharset ());
    JAXBBuilderDefaultSettings.setDefaultCharset (null);
    assertNull (JAXBBuilderDefaultSettings.getDefaultCharset ());
  }

  @Test
  public void testIndentString ()
  {
    assertNull (JAXBBuilderDefaultSettings.getDefaultIndentString ());
    JAXBBuilderDefaultSettings.setDefaultIndentString ("\t");
    assertEquals ("\t", JAXBBuilderDefaultSettings.getDefaultIndentString ());
  }

  @Test
  public void testUseSchema ()
  {
    assertTrue (JAXBBuilderDefaultSettings.DEFAULT_USE_SCHEMA == JAXBBuilderDefaultSettings.isDefaultUseSchema ());
    JAXBBuilderDefaultSettings.setDefaultUseSchema (false);
    assertFalse (JAXBBuilderDefaultSettings.isDefaultUseSchema ());
  }

  @Test
  public void testSchemaLocation ()
  {
    assertNull (JAXBBuilderDefaultSettings.getDefaultSchemaLocation ());
    JAXBBuilderDefaultSettings.setDefaultSchemaLocation ("urn:example file.xsd");
    assertEquals ("urn:example file.xsd", JAXBBuilderDefaultSettings.getDefaultSchemaLocation ());

    assertNull (JAXBBuilderDefaultSettings.getDefaultNoNamespaceSchemaLocation ());
    JAXBBuilderDefaultSettings.setDefaultNoNamespaceSchemaLocation ("file.xsd");
    assertEquals ("file.xsd", JAXBBuilderDefaultSettings.getDefaultNoNamespaceSchemaLocation ());
  }
}

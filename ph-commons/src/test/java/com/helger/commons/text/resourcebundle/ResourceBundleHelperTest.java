/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.commons.text.resourcebundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Locale;

import org.junit.Test;

/**
 * Test class for class {@link ResourceBundleHelper}.
 *
 * @author Philip Helger
 */
public final class ResourceBundleHelperTest
{
  private static final Locale L_DE = new Locale ("de");
  private static final Locale L_FR = new Locale ("fr");

  @Test
  public void testGetString ()
  {
    final String sBundle = "external/properties/test-iso8859";
    assertEquals ("äöü", ResourceBundleHelper.getString (sBundle, L_DE, "key1"));
    assertNull (ResourceBundleHelper.getString ("properties/test-iso8859-noway", L_DE, "key1"));
    assertEquals ("äöü", ResourceBundleHelper.getString (sBundle, L_FR, "key1"));
    assertNull (ResourceBundleHelper.getString (sBundle, L_DE, "key-noway"));
    assertEquals ("abc", ResourceBundleHelper.getString (sBundle, L_DE, "key2"));
    assertEquals ("colon", ResourceBundleHelper.getString (sBundle, L_DE, "key3"));
    assertEquals ("a:b", ResourceBundleHelper.getString (sBundle, L_DE, "key4"));
    assertEquals ("a=b", ResourceBundleHelper.getString (sBundle, L_DE, "key5"));
    assertEquals ("a#b", ResourceBundleHelper.getString (sBundle, L_DE, "key6"));
    assertEquals ("a:b", ResourceBundleHelper.getString (sBundle, L_DE, "key7"));
    assertEquals ("a=b", ResourceBundleHelper.getString (sBundle, L_DE, "key8"));
    assertEquals ("a#b", ResourceBundleHelper.getString (sBundle, L_DE, "key9"));
  }

  @Test
  public void testGetUtf8String ()
  {
    final String sBundle = "external/properties/test-utf8";
    assertEquals ("äöü", ResourceBundleHelper.getUtf8String (sBundle, L_DE, "key1"));
    assertNull (ResourceBundleHelper.getUtf8String ("properties/test-utf8-noway", L_DE, "key1"));
    assertEquals ("äöü", ResourceBundleHelper.getUtf8String (sBundle, L_FR, "key1"));
    assertNull (ResourceBundleHelper.getUtf8String (sBundle, L_DE, "key-noway"));
  }
}

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
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Test;

import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link ResourceBundleKey}.
 *
 * @author Philip Helger
 */
public final class ResourceBundleKeyTest
{
  private static final Locale L_DE = new Locale ("de");

  @Test
  public void testCtor ()
  {
    try
    {
      new ResourceBundleKey ("bundle", "");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      new ResourceBundleKey ("", "key");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    final String sBundle = "external/properties/test-iso8859";
    TestHelper.testDefaultImplementationWithEqualContentObject (new ResourceBundleKey (sBundle, "key1"),
                                                                       new ResourceBundleKey (sBundle, "key1"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (new ResourceBundleKey (sBundle, "key1"),
                                                                           new ResourceBundleKey ("external/properties/test-iso8859-1",
                                                                                                  "key1"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (new ResourceBundleKey (sBundle, "key1"),
                                                                           new ResourceBundleKey (sBundle, "key2"));

    ResourceBundleHelper.clearCache ();
  }

  @Test
  public void testISO8859 ()
  {
    final String sBundle = "external/properties/test-iso8859";
    final ResourceBundleKey key = new ResourceBundleKey (sBundle, "key1");
    assertEquals (sBundle, key.getBundleName ());
    assertEquals ("key1", key.getKey ());
    assertEquals ("äöü", key.getString (L_DE));
  }

  @Test
  public void testUTF8 ()
  {
    final String sBundle = "external/properties/test-utf8";
    final ResourceBundleKey key = new ResourceBundleKey (sBundle, "key1");
    assertEquals (sBundle, key.getBundleName ());
    assertEquals ("key1", key.getKey ());
    assertEquals ("äöü", key.getUtf8String (L_DE));
  }
}

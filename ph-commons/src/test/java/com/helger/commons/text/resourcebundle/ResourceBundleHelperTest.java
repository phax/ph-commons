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
package com.helger.commons.text.resourcebundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.helger.commons.mock.AbstractCommonsTestCase;

/**
 * Test class for class {@link ResourceBundleHelper}.
 *
 * @author Philip Helger
 */
public final class ResourceBundleHelperTest extends AbstractCommonsTestCase
{
  @Test
  public void testGetString ()
  {
    assertEquals ("äöü", ResourceBundleHelper.getString ("properties/test-iso8859", L_DE, "key1"));
    assertNull (ResourceBundleHelper.getString ("properties/test-iso8859-noway", L_DE, "key1"));
    assertEquals ("äöü", ResourceBundleHelper.getString ("properties/test-iso8859", L_FR, "key1"));
    assertNull (ResourceBundleHelper.getString ("properties/test-iso8859", L_DE, "key-noway"));
  }

  @Test
  public void testGetUtf8String ()
  {
    assertEquals ("äöü", ResourceBundleHelper.getUtf8String ("properties/test-utf8", L_DE, "key1"));
    assertNull (ResourceBundleHelper.getUtf8String ("properties/test-utf8-noway", L_DE, "key1"));
    assertEquals ("äöü", ResourceBundleHelper.getUtf8String ("properties/test-utf8", L_FR, "key1"));
    assertNull (ResourceBundleHelper.getUtf8String ("properties/test-utf8", L_DE, "key-noway"));
  }
}

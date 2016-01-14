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
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.mock.AbstractCommonsTestCase;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link ResourceBundleTextProvider}.
 *
 * @author Philip Helger
 */
public final class ResourceBundleTextProviderTest extends AbstractCommonsTestCase
{
  @Test
  public void testISO8859 ()
  {
    final ResourceBundleKey key = new ResourceBundleKey ("properties/test-iso8859", "key1");
    assertEquals ("properties/test-iso8859", key.getBundleName ());
    assertEquals ("key1", key.getKey ());
    assertEquals ("äöü", key.getString (L_DE));

    final ResourceBundleTextProvider aRBT = new ResourceBundleTextProvider (key);
    assertEquals ("äöü", aRBT.getText (L_DE));
    assertEquals ("äöü", aRBT.getTextWithArgs (L_DE, "egal"));

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new ResourceBundleTextProvider (key),
                                                                       new ResourceBundleTextProvider (key));
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new ResourceBundleTextProvider (key),
                                                                       new ResourceBundleTextProvider ("properties/test-iso8859",
                                                                                                       "key1"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new ResourceBundleTextProvider (key),
                                                                           new ResourceBundleTextProvider ("properties/test-iso8859",
                                                                                                           "key2"));

    try
    {
      new ResourceBundleTextProvider (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}

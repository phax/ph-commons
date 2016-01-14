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
package com.helger.commons.io.resourceprovider;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link ClassPathResourceProvider}.
 *
 * @author Philip Helger
 */
public final class ClassPathResourceProviderTest
{
  @Test
  public void testNoPrefix ()
  {
    final ClassPathResourceProvider aCPRP = new ClassPathResourceProvider ();
    assertTrue (aCPRP.supportsReading ("test1.txt"));
    assertNotNull (aCPRP.getReadableResource ("test1.txt"));
    assertTrue (aCPRP.getReadableResource ("test1.txt").exists ());

    try
    {
      // Fails because no prefix and no path are given
      aCPRP.getReadableResource ("");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // One with a prefix
    final ClassPathResourceProvider aCPRP2 = new ClassPathResourceProvider ("folder/");
    assertTrue (aCPRP2.supportsReading ("test1.txt"));
    assertNotNull (aCPRP2.getReadableResource (""));
    assertNotNull (aCPRP2.getReadableResource ("test1.txt"));
    assertFalse (aCPRP2.getReadableResource ("test1.txt").exists ());
    assertTrue (aCPRP2.getReadableResource ("test2.txt").exists ());
    assertTrue (aCPRP2.getReadableResource ("test2.txt").getResourceID ().endsWith ("folder/test2.txt"));

    try
    {
      aCPRP.getReadableResource (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testEqualsAndHashcode ()
  {
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new ClassPathResourceProvider (),
                                                                       new ClassPathResourceProvider ());
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new ClassPathResourceProvider (),
                                                                           new ClassPathResourceProvider ("folder"));
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new ClassPathResourceProvider ("folder"),
                                                                       new ClassPathResourceProvider ("folder"));
  }
}

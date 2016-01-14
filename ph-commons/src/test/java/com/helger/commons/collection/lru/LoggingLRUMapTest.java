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
package com.helger.commons.collection.lru;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link LoggingLRUMap}.
 *
 * @author Philip Helger
 */
public final class LoggingLRUMapTest
{
  @Test
  public void testAll ()
  {
    final LoggingLRUMap <String, String> c = new LoggingLRUMap <String, String> (5);
    assertNull (c.getMapName ());
    c.setMapName ("name");
    assertEquals ("name", c.getMapName ());
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (c,
                                                                       new LoggingLRUMap <String, String> (5).setMapName ("name"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (c,
                                                                           new LoggingLRUMap <String, String> (5).setMapName ("name2"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (c,
                                                                           new LoggingLRUMap <String, String> (6).setMapName ("name"));

    // Check overflow
    for (int i = 0; i < c.getMaxSize () + 1; ++i)
      c.put (Integer.toString (i), Integer.toString (i));
    assertEquals (c.getMaxSize (), c.size ());

    try
    {
      // Invalid name
      new LoggingLRUMap <String, String> (-1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}

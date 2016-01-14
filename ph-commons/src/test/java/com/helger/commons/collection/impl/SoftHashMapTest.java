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
package com.helger.commons.collection.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SoftHashMapTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SoftHashMapTest.class);

  @Test
  @Ignore ("Travis will fail if this test is run")
  public void testGarbageCollect ()
  {
    final SoftHashMap <Integer, BigDecimal> map = new SoftHashMap <Integer, BigDecimal> ();

    BigDecimal aOne = new BigDecimal ("+1.000");
    final Integer aKey = Integer.valueOf (1);
    map.put (aKey, aOne);
    s_aLogger.info ("Mapped value: " + map.get (aKey));
    assertNotNull (map.get (aKey));
    aOne = null;

    final Set <Map.Entry <Integer, BigDecimal>> aEntries = map.entrySet ();
    assertEquals (1, aEntries.size ());

    s_aLogger.info ("Filling memory please wait");
    try
    {
      final ArrayList <Object []> allocations = new ArrayList <Object []> ();
      int size;
      while ((size = Math.min (Math.abs ((int) Runtime.getRuntime ().freeMemory ()), Integer.MAX_VALUE)) > 0)
        allocations.add (new Object [size]);
    }
    catch (final OutOfMemoryError e)
    {
      // great!
    }
    s_aLogger.info ("Mapped value (should be null): " + map.get (aKey));
    assertNull (map.get (aKey));
  }

  @Test
  public void testEntrySetToArray ()
  {
    final SoftHashMap <Integer, BigDecimal> map = new SoftHashMap <Integer, BigDecimal> ();

    map.put (Integer.valueOf (1), new BigDecimal ("10"));
    assertEquals (1, map.entrySet ().size ());
    assertEquals (1, map.entrySet ().toArray ().length);
    assertEquals (1, map.entrySet ().toArray (new Map.Entry [0]).length);
    assertEquals (1, map.entrySet ().toArray (new Map.Entry [5]).length);
    assertEquals (5, map.entrySet ().toArray (new MapEntry [5]).length);
  }
}

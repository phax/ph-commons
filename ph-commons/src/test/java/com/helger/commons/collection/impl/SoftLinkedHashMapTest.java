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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.mutable.MutableBoolean;

public final class SoftLinkedHashMapTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SoftLinkedHashMapTest.class);

  @Test
  @Ignore ("Travis will fail if this test is run")
  public void testBasic ()
  {
    final MutableBoolean aChange = new MutableBoolean (false);
    final SoftLinkedHashMap <Integer, BigDecimal> map = new SoftLinkedHashMap <Integer, BigDecimal> (1)
    {
      @Override
      protected void onEntryRemoved (final Integer aKey)
      {
        s_aLogger.info ("Removed key " + aKey);
        aChange.set (true);
      }

      @Override
      protected void onRemoveEldestEntry (@Nonnegative final int nSize, @Nonnull final Map.Entry <Integer, BigDecimal> aEldest)
      {
        s_aLogger.info ("Removed eldest entry " + aEldest.getKey ());
      }
    };

    // Entry to be removed
    map.put (Integer.valueOf (0), BigDecimal.ZERO);

    BigDecimal aOne = new BigDecimal ("+1.000");
    final Integer aKey = Integer.valueOf (1);
    map.put (aKey, aOne);
    s_aLogger.info ("Mapped value: " + map.get (aKey));
    assertNotNull (map.get (aKey));
    assertEquals (1, map.size ());
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
    assertEquals (0, map.size ());
  }
}

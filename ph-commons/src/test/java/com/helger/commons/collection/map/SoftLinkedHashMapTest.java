/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.commons.collection.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.mutable.MutableBoolean;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class SoftLinkedHashMapTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (SoftLinkedHashMapTest.class);

  @Test
  @SuppressFBWarnings ("UC_USELESS_OBJECT")
  public void testBasic ()
  {
    final MutableBoolean aChange = new MutableBoolean (false);
    final SoftLinkedHashMap <Integer, BigDecimal> map = new SoftLinkedHashMap <> (1)
    {
      @Override
      protected void onEntryRemoved (final Integer aKey)
      {
        LOGGER.info ("Removed key " + aKey);
        aChange.set (true);
      }

      @Override
      protected void onRemoveEldestEntry (@Nonnegative final int nSize,
                                          @Nonnull final Map.Entry <Integer, BigDecimal> aEldest)
      {
        LOGGER.info ("Removed eldest entry " + aEldest.getKey ());
      }
    };

    // Entry to be removed
    map.put (Integer.valueOf (0), BigDecimal.ZERO);

    BigDecimal aOne = new BigDecimal ("+1.000");
    final Integer aKey = Integer.valueOf (1);
    map.put (aKey, aOne);
    LOGGER.info ("Mapped value: " + map.get (aKey));
    assertNotNull (map.get (aKey));
    assertEquals (1, map.size ());
    aOne = null;

    final Set <Map.Entry <Integer, BigDecimal>> aEntries = map.entrySet ();
    assertEquals (1, aEntries.size ());

    LOGGER.info ("Filling memory please wait");
    try
    {
      final List <Object []> aAllocations = new CommonsArrayList <> ();
      int nSize;
      while ((nSize = Math.min (Math.abs ((int) Runtime.getRuntime ().freeMemory ()), Integer.MAX_VALUE)) > 0)
        aAllocations.add (new Object [nSize]);
    }
    catch (final OutOfMemoryError e)
    {
      // great!
    }
    LOGGER.info ("Mapped value (should be null): " + map.get (aKey));
    assertNull (map.get (aKey));
    assertEquals (0, map.size ());
  }

  @Test
  public void testEntrySetToArray ()
  {
    final SoftLinkedHashMap <Integer, BigDecimal> aMap = new SoftLinkedHashMap <> (17);

    aMap.put (Integer.valueOf (1), BigDecimal.TEN);
    assertEquals (1, aMap.entrySet ().size ());
    assertEquals (1, aMap.entrySet ().toArray ().length);
    assertEquals (1, aMap.entrySet ().toArray (new Map.Entry [0]).length);
    assertEquals (1, aMap.entrySet ().toArray (new Map.Entry [5]).length);
    assertEquals (5, aMap.entrySet ().toArray (new MapEntry [5]).length);

    CommonsTestHelper.testGetClone (aMap);
  }
}

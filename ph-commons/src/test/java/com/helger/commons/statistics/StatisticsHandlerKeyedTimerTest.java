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
package com.helger.commons.statistics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigInteger;

import org.junit.Test;

import com.helger.commons.CGlobal;

/**
 * Test class for class {@link StatisticsHandlerKeyedTimer}.
 *
 * @author Philip Helger
 */
public final class StatisticsHandlerKeyedTimerTest
{
  @Test
  public void testAll ()
  {
    final StatisticsHandlerKeyedTimer sh = new StatisticsHandlerKeyedTimer ();
    assertEquals (0, sh.getInvocationCount ());
    assertEquals (CGlobal.ILLEGAL_ULONG, sh.getMin ("key1"));
    assertEquals (CGlobal.ILLEGAL_ULONG, sh.getMin ("key2"));

    sh.addTime ("key1", 100);
    assertEquals (1, sh.getInvocationCount ());
    assertEquals (1, sh.getInvocationCount ("key1"));
    assertEquals (100L, sh.getMin ("key1"));
    assertEquals (100L, sh.getMax ("key1"));
    assertEquals (BigInteger.valueOf (100L), sh.getSum ("key1"));
    assertEquals (100L, sh.getAverage ("key1"));
    assertEquals (CGlobal.ILLEGAL_UINT, sh.getInvocationCount ("key2"));
    assertEquals (CGlobal.ILLEGAL_ULONG, sh.getMin ("key2"));
    assertEquals (CGlobal.ILLEGAL_ULONG, sh.getMax ("key2"));
    assertNull (sh.getSum ("key2"));
    assertEquals (CGlobal.ILLEGAL_ULONG, sh.getAverage ("key2"));

    sh.addTime ("key1", 200L);
    assertEquals (2, sh.getInvocationCount ());
    assertEquals (2, sh.getInvocationCount ("key1"));
    assertEquals (100L, sh.getMin ("key1"));
    assertEquals (200L, sh.getMax ("key1"));
    assertEquals (BigInteger.valueOf (300L), sh.getSum ("key1"));
    assertEquals (150L, sh.getAverage ("key1"));
    assertEquals (CGlobal.ILLEGAL_ULONG, sh.getMin ("key2"));

    sh.addTime ("key2", 1000);
    assertEquals (3, sh.getInvocationCount ());
    assertEquals (2, sh.getInvocationCount ("key1"));
    assertEquals (100L, sh.getMin ("key1"));
    assertEquals (200L, sh.getMax ("key1"));
    assertEquals (BigInteger.valueOf (300L), sh.getSum ("key1"));
    assertEquals (150L, sh.getAverage ("key1"));
    assertEquals (1, sh.getInvocationCount ("key2"));
    assertEquals (1000L, sh.getMin ("key2"));
    assertEquals (1000L, sh.getMax ("key2"));
    assertEquals (BigInteger.valueOf (1000L), sh.getSum ("key2"));
    assertEquals (1000L, sh.getAverage ("key2"));

    assertEquals (2, sh.getAllKeys ().size ());
  }
}

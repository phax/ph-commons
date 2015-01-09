/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.helger.commons.CGlobal;

/**
 * Test class for class {@link StatisticsHandlerKeyedCounter}.
 *
 * @author Philip Helger
 */
@SuppressWarnings ("javadoc")
public final class StatisticsHandlerKeyedCounterTest
{
  @Test
  public void testAll ()
  {
    final StatisticsHandlerKeyedCounter sh = new StatisticsHandlerKeyedCounter ();
    assertEquals (0, sh.getInvocationCount ());
    assertEquals (CGlobal.ILLEGAL_ULONG, sh.getCount ("key1"));
    assertEquals (CGlobal.ILLEGAL_ULONG, sh.getCount ("key2"));
    sh.increment ("key1");
    assertEquals (1, sh.getInvocationCount ());
    assertEquals (1L, sh.getCount ("key1"));
    assertEquals (CGlobal.ILLEGAL_ULONG, sh.getCount ("key2"));
    sh.increment ("key1", 2);
    assertEquals (2, sh.getInvocationCount ());
    assertEquals (3L, sh.getCount ("key1"));
    assertEquals (CGlobal.ILLEGAL_ULONG, sh.getCount ("key2"));
    sh.increment ("key2");
    assertEquals (3, sh.getInvocationCount ());
    assertEquals (3L, sh.getCount ("key1"));
    assertEquals (1L, sh.getCount ("key2"));
    assertEquals (2, sh.getAllKeys ().size ());
    assertNotNull (sh.getAsString ());
  }
}

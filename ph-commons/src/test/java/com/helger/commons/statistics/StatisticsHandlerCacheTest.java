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

import org.junit.Test;

/**
 * Test class for class {@link StatisticsHandlerCache}.
 *
 * @author Philip Helger
 */
public final class StatisticsHandlerCacheTest
{
  @Test
  public void testAll ()
  {
    final StatisticsHandlerCache sh = new StatisticsHandlerCache ();
    assertEquals (0, sh.getInvocationCount ());
    assertEquals (0, sh.getHits ());
    assertEquals (0, sh.getMisses ());
    sh.cacheHit ();
    assertEquals (1, sh.getInvocationCount ());
    assertEquals (1, sh.getHits ());
    assertEquals (0, sh.getMisses ());
    sh.cacheMiss ();
    assertEquals (2, sh.getInvocationCount ());
    assertEquals (1, sh.getHits ());
    assertEquals (1, sh.getMisses ());
  }
}

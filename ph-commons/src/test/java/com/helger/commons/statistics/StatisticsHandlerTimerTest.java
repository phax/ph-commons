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

import java.math.BigInteger;

import org.junit.Test;

import com.helger.commons.CGlobal;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link StatisticsHandlerTimer}.
 *
 * @author Philip Helger
 */
public final class StatisticsHandlerTimerTest
{
  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
  @Test
  public void testAll ()
  {
    final StatisticsHandlerTimer sh = new StatisticsHandlerTimer ();
    assertEquals (0, sh.getInvocationCount ());
    assertEquals (CGlobal.ILLEGAL_UINT, sh.getMin ());
    assertEquals (CGlobal.ILLEGAL_UINT, sh.getAverage ());
    assertEquals (CGlobal.ILLEGAL_UINT, sh.getMax ());
    assertEquals (BigInteger.ZERO, sh.getSum ());
    sh.addTime (5);
    assertEquals (1, sh.getInvocationCount ());
    assertEquals (5, sh.getMin ());
    assertEquals (5, sh.getAverage ());
    assertEquals (5, sh.getMax ());
    assertEquals (BigInteger.valueOf (5), sh.getSum ());
    sh.addTime (45);
    assertEquals (2, sh.getInvocationCount ());
    assertEquals (5, sh.getMin ());
    assertEquals (25, sh.getAverage ());
    assertEquals (45, sh.getMax ());
    assertEquals (BigInteger.valueOf (50), sh.getSum ());
    sh.addTime (25);
    assertEquals (3, sh.getInvocationCount ());
    assertEquals (5, sh.getMin ());
    assertEquals (25, sh.getAverage ());
    assertEquals (45, sh.getMax ());
    assertEquals (BigInteger.valueOf (75), sh.getSum ());
    sh.addTime (-1);
  }
}

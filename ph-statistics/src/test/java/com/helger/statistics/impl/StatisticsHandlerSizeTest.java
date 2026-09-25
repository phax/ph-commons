/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.statistics.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

import com.helger.base.CGlobal;

/**
 * Test class for class {@link StatisticsHandlerSize}.
 *
 * @author Philip Helger
 */
public final class StatisticsHandlerSizeTest
{
  @Test
  public void testAll ()
  {
    final StatisticsHandlerSize sh = new StatisticsHandlerSize ();
    assertEquals (0, sh.getInvocationCount ());
    assertEquals (CGlobal.ILLEGAL_UINT, sh.getMin ());
    assertEquals (CGlobal.ILLEGAL_UINT, sh.getAverage ());
    assertEquals (CGlobal.ILLEGAL_UINT, sh.getMax ());
    assertEquals (BigInteger.ZERO, sh.getSum ());
    sh.addSize (5);
    assertEquals (1, sh.getInvocationCount ());
    assertEquals (5, sh.getMin ());
    assertEquals (5, sh.getAverage ());
    assertEquals (5, sh.getMax ());
    assertEquals (BigInteger.valueOf (5), sh.getSum ());
    sh.addSize (45);
    assertEquals (2, sh.getInvocationCount ());
    assertEquals (5, sh.getMin ());
    assertEquals (25, sh.getAverage ());
    assertEquals (45, sh.getMax ());
    assertEquals (BigInteger.valueOf (50), sh.getSum ());
    sh.addSize (25);
    assertEquals (3, sh.getInvocationCount ());
    assertEquals (5, sh.getMin ());
    assertEquals (25, sh.getAverage ());
    assertEquals (45, sh.getMax ());
    assertEquals (BigInteger.valueOf (75), sh.getSum ());
    sh.addSize (-1);
  }

  @Test
  public void testSumOverflow ()
  {
    final StatisticsHandlerSize sh = new StatisticsHandlerSize ();
    sh.addSize (Long.MAX_VALUE);
    assertEquals (BigInteger.valueOf (Long.MAX_VALUE), sh.getSum ());
    assertEquals (Long.MAX_VALUE, sh.getAverage ());

    // This exceeds the value range of a long
    sh.addSize (Long.MAX_VALUE);
    assertEquals (BigInteger.valueOf (Long.MAX_VALUE).shiftLeft (1), sh.getSum ());
    assertEquals (Long.MAX_VALUE, sh.getAverage ());

    sh.addSize (2);
    assertEquals (BigInteger.valueOf (Long.MAX_VALUE).shiftLeft (1).add (BigInteger.valueOf (2)), sh.getSum ());
    assertEquals (6148914691236517205L, sh.getAverage ());
    assertEquals (3, sh.getInvocationCount ());
    assertEquals (2, sh.getMin ());
    assertEquals (Long.MAX_VALUE, sh.getMax ());
  }
}

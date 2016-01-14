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
package com.helger.commons.supplementary.test.java;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class JavaBitOperationsFuncTest
{
  private static final int nA = 0x1;
  private static final int nB = 0x2;
  private static final int nC = 0x4;
  private static final int nD = 0x80;
  private static final int nE = 0x40;

  @Test
  public void testBitWise ()
  {
    assertEquals ("1", nA & nB, 0);
    assertEquals ("1b", nB & nA, 0);
    assertEquals ("2", nA & nC, 0);
    assertEquals ("2b", nC & nA, 0);
    assertEquals ("3", nA & nD, 0);
    assertEquals ("3b", nD & nA, 0);
    assertEquals ("4", nA & nE, 0);
    assertEquals ("4b", nE & nA, 0);
    assertEquals ("5", nB & nC, 0);
    assertEquals ("5b", nC & nB, 0);
    assertEquals ("6", nB & nD, 0);
    assertEquals ("6b", nD & nB, 0);
    assertEquals ("7", nB & nE, 0);
    assertEquals ("7b", nE & nB, 0);
    assertEquals ("8", nC & nD, 0);
    assertEquals ("8b", nD & nC, 0);
    assertEquals ("9", nC & nE, 0);
    assertEquals ("9b", nE & nC, 0);
    assertEquals ("10", nD & nE, 0);
    assertEquals ("10b", nE & nD, 0);
  }

  @Test
  public void testBitWiseOr ()
  {
    assertEquals ("1", nA | nB, 3); // 01 | 10 -> 11

    assertEquals ("check", (nA | nB) & nB, nB);
    assertEquals ("2", nA | nC, 5); // 01 | 100 -> 101
    assertEquals ("3", nA | nD, 129); // 01 | 10000000 -> 10000001
  }
}

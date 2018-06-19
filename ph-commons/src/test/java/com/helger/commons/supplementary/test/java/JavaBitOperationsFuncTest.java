/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
    assertEquals ("1", 0, nA & nB);
    assertEquals ("1b", 0, nB & nA);
    assertEquals ("2", 0, nA & nC);
    assertEquals ("2b", 0, nC & nA);
    assertEquals ("3", 0, nA & nD);
    assertEquals ("3b", 0, nD & nA);
    assertEquals ("4", 0, nA & nE);
    assertEquals ("4b", 0, nE & nA);
    assertEquals ("5", 0, nB & nC);
    assertEquals ("5b", 0, nC & nB);
    assertEquals ("6", 0, nB & nD);
    assertEquals ("6b", 0, nD & nB);
    assertEquals ("7", 0, nB & nE);
    assertEquals ("7b", 0, nE & nB);
    assertEquals ("8", 0, nC & nD);
    assertEquals ("8b", 0, nD & nC);
    assertEquals ("9", 0, nC & nE);
    assertEquals ("9b", 0, nE & nC);
    assertEquals ("10", 0, nD & nE);
    assertEquals ("10b", 0, nE & nD);
  }

  @Test
  public void testBitWiseOr ()
  {
    assertEquals ("1", 3, nA | nB); // 01 | 10 -> 11

    assertEquals ("check", nB, (nA | nB) & nB);
    assertEquals ("2", 5, nA | nC); // 01 | 100 -> 101
    assertEquals ("3", 129, nA | nD); // 01 | 10000000 -> 10000001
  }
}

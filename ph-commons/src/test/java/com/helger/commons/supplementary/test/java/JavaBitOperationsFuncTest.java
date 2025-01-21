/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
  private static final int A = 0x1;
  private static final int B = 0x2;
  private static final int C = 0x4;
  private static final int D = 0x80;
  private static final int E = 0x40;

  @Test
  public void testBitWise ()
  {
    assertEquals ("1", 0, A & B);
    assertEquals ("1b", 0, B & A);
    assertEquals ("2", 0, A & C);
    assertEquals ("2b", 0, C & A);
    assertEquals ("3", 0, A & D);
    assertEquals ("3b", 0, D & A);
    assertEquals ("4", 0, A & E);
    assertEquals ("4b", 0, E & A);
    assertEquals ("5", 0, B & C);
    assertEquals ("5b", 0, C & B);
    assertEquals ("6", 0, B & D);
    assertEquals ("6b", 0, D & B);
    assertEquals ("7", 0, B & E);
    assertEquals ("7b", 0, E & B);
    assertEquals ("8", 0, C & D);
    assertEquals ("8b", 0, D & C);
    assertEquals ("9", 0, C & E);
    assertEquals ("9b", 0, E & C);
    assertEquals ("10", 0, D & E);
    assertEquals ("10b", 0, E & D);
  }

  @Test
  public void testBitWiseOr ()
  {
    assertEquals ("1", 3, A | B); // 01 | 10 -> 11

    assertEquals ("check", B, (A | B) & B);
    assertEquals ("2", 5, A | C); // 01 | 100 -> 101
    assertEquals ("3", 129, A | D); // 01 | 10000000 -> 10000001
  }
}

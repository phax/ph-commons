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
package com.helger.commons.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Test class for class {@link JavaVersionHelper}.
 *
 * @author Philip Helger
 */
public class JavaVersionHelperTest
{
  @Test
  public void testGetVersion ()
  {
    int [] aParts;
    aParts = JavaVersionHelper.getAsUnifiedVersion ("1.8.0_144");
    assertNotNull (aParts);
    assertEquals (3, aParts.length);
    assertEquals (8, aParts[0]);
    assertEquals (144, aParts[1]);
    assertEquals (-1, aParts[2]);

    aParts = JavaVersionHelper.getAsUnifiedVersion ("1.8.0_181");
    assertNotNull (aParts);
    assertEquals (3, aParts.length);
    assertEquals (8, aParts[0]);
    assertEquals (181, aParts[1]);
    assertEquals (-1, aParts[2]);

    aParts = JavaVersionHelper.getAsUnifiedVersion ("9");
    assertNotNull (aParts);
    assertEquals (3, aParts.length);
    assertEquals (9, aParts[0]);
    assertEquals (0, aParts[1]);
    assertEquals (0, aParts[2]);

    aParts = JavaVersionHelper.getAsUnifiedVersion ("9.1");
    assertNotNull (aParts);
    assertEquals (3, aParts.length);
    assertEquals (9, aParts[0]);
    assertEquals (1, aParts[1]);
    assertEquals (0, aParts[2]);

    aParts = JavaVersionHelper.getAsUnifiedVersion ("9.0.4");
    assertNotNull (aParts);
    assertEquals (3, aParts.length);
    assertEquals (9, aParts[0]);
    assertEquals (0, aParts[1]);
    assertEquals (4, aParts[2]);

    aParts = JavaVersionHelper.getAsUnifiedVersion ("9-Ubuntu");
    assertNotNull (aParts);
    assertEquals (3, aParts.length);
    assertEquals (9, aParts[0]);
    assertEquals (0, aParts[1]);
    assertEquals (0, aParts[2]);

    aParts = JavaVersionHelper.getAsUnifiedVersion ("9.1-Ubuntu");
    assertNotNull (aParts);
    assertEquals (3, aParts.length);
    assertEquals (9, aParts[0]);
    assertEquals (1, aParts[1]);
    assertEquals (0, aParts[2]);

    aParts = JavaVersionHelper.getAsUnifiedVersion ("9.0.4-Ubuntu");
    assertNotNull (aParts);
    assertEquals (3, aParts.length);
    assertEquals (9, aParts[0]);
    assertEquals (0, aParts[1]);
    assertEquals (4, aParts[2]);

    aParts = JavaVersionHelper.getAsUnifiedVersion ("9.1.2+62");
    assertNotNull (aParts);
    assertEquals (3, aParts.length);
    assertEquals (9, aParts[0]);
    assertEquals (1, aParts[1]);
    assertEquals (2, aParts[2]);

    aParts = JavaVersionHelper.getAsUnifiedVersion ("9-ea+19");
    assertNotNull (aParts);
    assertEquals (3, aParts.length);
    assertEquals (9, aParts[0]);
    assertEquals (0, aParts[1]);
    assertEquals (0, aParts[2]);

    aParts = JavaVersionHelper.getAsUnifiedVersion ("1.8.0-adoptopenjdk",
                                                    "1.8.0-adoptopenjdk-_2018_05_19_00_59-b00",
                                                    true);
    assertNotNull (aParts);
    assertEquals (3, aParts.length);
    assertEquals (8, aParts[0]);
    assertEquals (172, aParts[1]);
    assertEquals (0, aParts[2]);

    aParts = JavaVersionHelper.getAsUnifiedVersion ("1.8.0-adoptopenjdk",
                                                    "1.8.0-adoptopenjdk-_2018_05_19_01_59-b00",
                                                    true);
    assertNotNull (aParts);
    assertEquals (3, aParts.length);
    assertEquals (8, aParts[0]);
    // Minutes since 1.1.2018
    assertEquals (198839, aParts[1]);
    assertEquals (0, aParts[2]);

    aParts = JavaVersionHelper.getAsUnifiedVersion ("11+28");
    assertNotNull (aParts);
    assertEquals (3, aParts.length);
    assertEquals (11, aParts[0]);
    assertEquals (0, aParts[1]);
    assertEquals (0, aParts[2]);

    aParts = JavaVersionHelper.getAsUnifiedVersion ("17.4.0.1");
    assertNotNull (aParts);
    assertEquals (3, aParts.length);
    assertEquals (17, aParts[0]);
    assertEquals (4, aParts[1]);
    assertEquals (0, aParts[2]);

    aParts = JavaVersionHelper.getAsUnifiedVersion ("11.0.16.1");
    assertNotNull (aParts);
    assertEquals (3, aParts.length);
    assertEquals (11, aParts[0]);
    assertEquals (0, aParts[1]);
    assertEquals (16, aParts[2]);
  }
}

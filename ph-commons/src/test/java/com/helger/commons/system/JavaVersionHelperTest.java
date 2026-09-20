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
package com.helger.commons.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Test class for class {@link JavaVersionHelper}.
 *
 * @author Philip Helger
 */
public final class JavaVersionHelperTest
{
  @Test
  public void testGetVersion ()
  {
    int [] aParts = JavaVersionHelper.getAsUnifiedVersion ("1.8.0_144");
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

  @Test
  public void testAdoptOpenJDK ()
  {
    // The well known build 172
    int [] aParts = JavaVersionHelper.getAsUnifiedVersion ("1.8.0-adoptopenjdk",
                                                           "1.8.0-adoptopenjdk-_2018_05_19_00_59-b00",
                                                           true);
    assertEquals (8, aParts[0]);
    assertEquals (172, aParts[1]);
    assertEquals (0, aParts[2]);

    // Another build - the minutes since the reference date (2018-01-01) are
    // used
    aParts = JavaVersionHelper.getAsUnifiedVersion ("1.8.0-adoptopenjdk",
                                                    "1.8.0-adoptopenjdk-_2018_05_19_01_59-b00",
                                                    true);
    assertEquals (8, aParts[0]);
    assertEquals (198839, aParts[1]);
    assertEquals (0, aParts[2]);

    // An unparsable date
    aParts = JavaVersionHelper.getAsUnifiedVersion ("1.8.0-adoptopenjdk", "1.8.0-adoptopenjdk-whatsoever", true);
    assertEquals (8, aParts[0]);
    assertEquals (-1, aParts[1]);
    assertEquals (-1, aParts[2]);

    // A runtime version not matching the Java version
    aParts = JavaVersionHelper.getAsUnifiedVersion ("1.8.0-adoptopenjdk", "something else", true);
    assertEquals (8, aParts[0]);
    assertEquals (-1, aParts[1]);
    assertEquals (-1, aParts[2]);
  }

  @Test
  public void testErrorsWithoutException ()
  {
    // No second dot
    int [] aParts = JavaVersionHelper.getAsUnifiedVersion ("1.8", null, false);
    assertEquals (0, aParts[0]);
    assertEquals (0, aParts[1]);
    assertEquals (0, aParts[2]);

    // Unparsable major version
    aParts = JavaVersionHelper.getAsUnifiedVersion ("1.x.0_144", null, false);
    assertEquals (0, aParts[0]);

    // Unparsable minor version
    aParts = JavaVersionHelper.getAsUnifiedVersion ("1.8.0_x", null, false);
    assertEquals (8, aParts[0]);
    assertEquals (0, aParts[1]);

    // Neither "_" nor "-" present
    aParts = JavaVersionHelper.getAsUnifiedVersion ("1.8.0", null, false);
    assertEquals (0, aParts[0]);
    assertEquals (0, aParts[1]);
    assertEquals (0, aParts[2]);

    // Unparsable major version (new scheme)
    aParts = JavaVersionHelper.getAsUnifiedVersion ("x.1.2", null, false);
    assertEquals (0, aParts[0]);

    // Unparsable minor version (new scheme)
    aParts = JavaVersionHelper.getAsUnifiedVersion ("9.x.2", null, false);
    assertEquals (9, aParts[0]);
    assertEquals (0, aParts[1]);

    // Unparsable micro version (new scheme)
    aParts = JavaVersionHelper.getAsUnifiedVersion ("9.1.x", null, false);
    assertEquals (9, aParts[0]);
    assertEquals (1, aParts[1]);
    assertEquals (0, aParts[2]);
  }

  @Test
  public void testErrorsWithException ()
  {
    for (final String sVersion : new String [] { "1.8", "1.x.0_144", "1.8.0_x", "1.8.0", "x.1.2", "9.x.2", "9.1.x" })
      try
      {
        JavaVersionHelper.getAsUnifiedVersion (sVersion, null, true);
        fail (sVersion);
      }
      catch (final IllegalStateException ex)
      {
        // expected
      }
  }

  @Test
  public void testIsAtLeast ()
  {
    // This library requires at least Java 17
    assertTrue (JavaVersionHelper.isAtLeast (17, 0));
    assertTrue (JavaVersionHelper.JAVA_MAJOR_VERSION >= 17);
    assertTrue (JavaVersionHelper.JAVA_MINOR_VERSION >= 0);
    assertFalse (JavaVersionHelper.isAtLeast (JavaVersionHelper.JAVA_MAJOR_VERSION + 1, 0));
    assertFalse (JavaVersionHelper.isAtLeast (JavaVersionHelper.JAVA_MAJOR_VERSION,
                                              JavaVersionHelper.JAVA_MINOR_VERSION + 1));
  }
}

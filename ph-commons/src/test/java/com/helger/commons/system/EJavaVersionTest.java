/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for class {@link EJavaVersion}.,
 *
 * @author Philip Helger
 */
public final class EJavaVersionTest
{
  @Test
  public void testBasic ()
  {
    for (final EJavaVersion e : EJavaVersion.values ())
      assertSame (e, EJavaVersion.valueOf (e.name ()));

    boolean bAny = false;
    for (final EJavaVersion e : EJavaVersion.values ())
      if (e.isOlderOrEqualsThan (EJavaVersion.JDK_10))
        assertFalse (e.isCurrentVersion ());
      else
        bAny = bAny || e.isCurrentVersion ();
    assertTrue (bAny);

    final EJavaVersion eJV = EJavaVersion.getCurrentVersion ();
    assertNotNull (eJV);
    assertTrue (eJV.isNewerOrEqualsThan (EJavaVersion.JDK_11));

    assertEquals (EJavaVersion.UNKNOWN, EJavaVersion.getFromVersionNumber (44.0));
    assertEquals (EJavaVersion.JDK_1_5, EJavaVersion.getFromMajorAndMinor (49, 0));
    assertEquals (EJavaVersion.JDK_1_5, EJavaVersion.getFromVersionNumber (49.0));
    assertEquals (EJavaVersion.JDK_1_6, EJavaVersion.getFromMajorAndMinor (50, 0));
    assertEquals (EJavaVersion.JDK_1_6, EJavaVersion.getFromVersionNumber (50.0));
    assertEquals (EJavaVersion.JDK_1_7, EJavaVersion.getFromVersionNumber (51.0));
    assertEquals (EJavaVersion.JDK_1_8, EJavaVersion.getFromVersionNumber (52.0));
    assertEquals (EJavaVersion.JDK_9, EJavaVersion.getFromVersionNumber (53.0));
    assertEquals (EJavaVersion.JDK_10, EJavaVersion.getFromVersionNumber (54.0));
    assertEquals (EJavaVersion.JDK_11, EJavaVersion.getFromVersionNumber (55.0));
    assertEquals (EJavaVersion.JDK_12, EJavaVersion.getFromVersionNumber (56.0));
    assertEquals (EJavaVersion.JDK_13, EJavaVersion.getFromVersionNumber (57.0));
    assertEquals (EJavaVersion.JDK_14, EJavaVersion.getFromVersionNumber (58.0));
    assertEquals (EJavaVersion.JDK_15, EJavaVersion.getFromVersionNumber (59.0));
    assertEquals (EJavaVersion.JDK_16, EJavaVersion.getFromVersionNumber (60.0));
    assertEquals (EJavaVersion.JDK_17, EJavaVersion.getFromVersionNumber (61.0));
    assertEquals (EJavaVersion.JDK_18, EJavaVersion.getFromVersionNumber (62.0));
    assertEquals (EJavaVersion.JDK_19, EJavaVersion.getFromVersionNumber (63.0));
    assertEquals (EJavaVersion.JDK_20, EJavaVersion.getFromVersionNumber (64.0));
    assertEquals (EJavaVersion.JDK_21, EJavaVersion.getFromVersionNumber (65.0));
    assertEquals (EJavaVersion.UNKNOWN, EJavaVersion.getFromVersionNumber (66.0));

    for (final EJavaVersion e : EJavaVersion.values ())
      if (e.isOlderOrEqualsThan (EJavaVersion.JDK_11))
        assertTrue (e.isSupportedVersion ());

    boolean bShouldBeSupported = true;
    for (final EJavaVersion e : EJavaVersion.values ())
      if (e != EJavaVersion.UNKNOWN)
      {
        if (bShouldBeSupported)
          assertTrue ("Should be supported: " + e, e.isSupportedVersion ());
        else
          assertFalse ("Should not be supported: " + e, e.isSupportedVersion ());
        if (e.isCurrentVersion ())
        {
          // All following versions are not supported
          bShouldBeSupported = false;
        }
      }
  }

  @Test
  public void testIsOlderOrEqualsThan ()
  {
    assertFalse (EJavaVersion.JDK_1_2.isOlderOrEqualsThan (EJavaVersion.JDK_1_1));
    assertTrue (EJavaVersion.JDK_1_2.isOlderOrEqualsThan (EJavaVersion.JDK_1_2));
    assertTrue (EJavaVersion.JDK_1_2.isOlderOrEqualsThan (EJavaVersion.JDK_1_7));

    assertFalse (EJavaVersion.JDK_1_7.isOlderOrEqualsThan (EJavaVersion.JDK_1_1));
    assertFalse (EJavaVersion.JDK_1_7.isOlderOrEqualsThan (EJavaVersion.JDK_1_6));
    assertTrue (EJavaVersion.JDK_1_7.isOlderOrEqualsThan (EJavaVersion.JDK_1_7));
    assertTrue (EJavaVersion.JDK_1_7.isOlderOrEqualsThan (EJavaVersion.JDK_1_8));
    assertTrue (EJavaVersion.JDK_1_7.isOlderOrEqualsThan (EJavaVersion.JDK_9));
    assertTrue (EJavaVersion.JDK_1_7.isOlderOrEqualsThan (EJavaVersion.JDK_10));
    assertTrue (EJavaVersion.JDK_1_7.isOlderOrEqualsThan (EJavaVersion.JDK_11));
    assertTrue (EJavaVersion.JDK_1_7.isOlderOrEqualsThan (EJavaVersion.JDK_12));
    assertTrue (EJavaVersion.JDK_1_7.isOlderOrEqualsThan (EJavaVersion.JDK_13));
    assertTrue (EJavaVersion.JDK_1_7.isOlderOrEqualsThan (EJavaVersion.JDK_14));
    assertTrue (EJavaVersion.JDK_1_7.isOlderOrEqualsThan (EJavaVersion.JDK_15));
    assertTrue (EJavaVersion.JDK_1_7.isOlderOrEqualsThan (EJavaVersion.JDK_16));
    assertTrue (EJavaVersion.JDK_1_7.isOlderOrEqualsThan (EJavaVersion.JDK_17));
    assertTrue (EJavaVersion.JDK_1_7.isOlderOrEqualsThan (EJavaVersion.JDK_18));
    assertTrue (EJavaVersion.JDK_1_7.isOlderOrEqualsThan (EJavaVersion.JDK_19));
    assertTrue (EJavaVersion.JDK_1_7.isOlderOrEqualsThan (EJavaVersion.JDK_20));
    assertTrue (EJavaVersion.JDK_1_7.isOlderOrEqualsThan (EJavaVersion.JDK_21));
  }

  @Test
  public void testIsNewerOrEqualsThan ()
  {
    assertTrue (EJavaVersion.JDK_1_1.isNewerOrEqualsThan (EJavaVersion.JDK_1_1));
    assertFalse (EJavaVersion.JDK_1_1.isNewerOrEqualsThan (EJavaVersion.JDK_1_2));
    assertFalse (EJavaVersion.JDK_1_1.isNewerOrEqualsThan (EJavaVersion.JDK_1_7));

    assertTrue (EJavaVersion.JDK_1_7.isNewerOrEqualsThan (EJavaVersion.JDK_1_1));
    assertTrue (EJavaVersion.JDK_1_7.isNewerOrEqualsThan (EJavaVersion.JDK_1_6));
    assertTrue (EJavaVersion.JDK_1_7.isNewerOrEqualsThan (EJavaVersion.JDK_1_7));
    assertFalse (EJavaVersion.JDK_1_7.isNewerOrEqualsThan (EJavaVersion.JDK_1_8));
    assertFalse (EJavaVersion.JDK_1_7.isNewerOrEqualsThan (EJavaVersion.JDK_9));
    assertFalse (EJavaVersion.JDK_1_7.isNewerOrEqualsThan (EJavaVersion.JDK_10));
    assertFalse (EJavaVersion.JDK_1_7.isNewerOrEqualsThan (EJavaVersion.JDK_11));
    assertFalse (EJavaVersion.JDK_1_7.isNewerOrEqualsThan (EJavaVersion.JDK_12));
    assertFalse (EJavaVersion.JDK_1_7.isNewerOrEqualsThan (EJavaVersion.JDK_13));
    assertFalse (EJavaVersion.JDK_1_7.isNewerOrEqualsThan (EJavaVersion.JDK_14));
    assertFalse (EJavaVersion.JDK_1_7.isNewerOrEqualsThan (EJavaVersion.JDK_15));
    assertFalse (EJavaVersion.JDK_1_7.isNewerOrEqualsThan (EJavaVersion.JDK_16));
    assertFalse (EJavaVersion.JDK_1_7.isNewerOrEqualsThan (EJavaVersion.JDK_17));
    assertFalse (EJavaVersion.JDK_1_7.isNewerOrEqualsThan (EJavaVersion.JDK_18));
    assertFalse (EJavaVersion.JDK_1_7.isNewerOrEqualsThan (EJavaVersion.JDK_19));
    assertFalse (EJavaVersion.JDK_1_7.isNewerOrEqualsThan (EJavaVersion.JDK_20));
    assertFalse (EJavaVersion.JDK_1_7.isNewerOrEqualsThan (EJavaVersion.JDK_21));
  }

  @Test
  public void testIsLTS ()
  {
    assertFalse (EJavaVersion.JDK_1_1.isLTS ());
    assertFalse (EJavaVersion.JDK_1_2.isLTS ());
    assertFalse (EJavaVersion.JDK_1_3.isLTS ());
    assertFalse (EJavaVersion.JDK_1_4.isLTS ());
    assertFalse (EJavaVersion.JDK_1_5.isLTS ());
    assertFalse (EJavaVersion.JDK_1_6.isLTS ());
    assertFalse (EJavaVersion.JDK_1_7.isLTS ());
    assertTrue (EJavaVersion.JDK_1_8.isLTS ());
    assertFalse (EJavaVersion.JDK_9.isLTS ());
    assertFalse (EJavaVersion.JDK_10.isLTS ());
    assertTrue (EJavaVersion.JDK_11.isLTS ());
    assertFalse (EJavaVersion.JDK_12.isLTS ());
    assertFalse (EJavaVersion.JDK_13.isLTS ());
    assertFalse (EJavaVersion.JDK_14.isLTS ());
    assertFalse (EJavaVersion.JDK_15.isLTS ());
    assertFalse (EJavaVersion.JDK_16.isLTS ());
    assertTrue (EJavaVersion.JDK_17.isLTS ());
    assertFalse (EJavaVersion.JDK_18.isLTS ());
    assertFalse (EJavaVersion.JDK_19.isLTS ());
    assertFalse (EJavaVersion.JDK_20.isLTS ());
    assertTrue (EJavaVersion.JDK_21.isLTS ());
  }
}

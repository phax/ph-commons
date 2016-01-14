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
package com.helger.commons.dimension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link SizeInt}.
 *
 * @author Philip Helger
 */
public final class SizeIntTest
{
  @Test
  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
  public void testCtor ()
  {
    final SizeInt aID = new SizeInt (320, 240);
    assertEquals (320, aID.getWidth ());
    assertEquals (240, aID.getHeight ());

    try
    {
      new SizeInt (300, -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      new SizeInt (-1, 300);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new SizeInt (320, 240), new SizeInt (320, 240));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new SizeInt (320, 240),
                                                                           new SizeInt (321, 240));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new SizeInt (320, 240),
                                                                           new SizeInt (320, 241));
  }

  @Test
  public void testGetBestMatchingSize ()
  {
    final SizeInt aID = new SizeInt (320, 240);

    // Width
    SizeInt aID2 = aID.getBestMatchingSize (160, 180);
    assertEquals (160, aID2.getWidth ());
    assertEquals (120, aID2.getHeight ());

    // Height
    aID2 = aID.getBestMatchingSize (260, 120);
    assertEquals (160, aID2.getWidth ());
    assertEquals (120, aID2.getHeight ());

    // WIdth
    aID2 = aID.getBestMatchingSize (48, 120);
    assertEquals (48, aID2.getWidth ());
    assertEquals (36, aID2.getHeight ());

    assertEquals (aID, aID.getBestMatchingSize (640, 481));
    assertEquals (aID, aID.getBestMatchingSize (641, 480));

    try
    {
      // <=0 not allowed
      aID2.getBestMatchingSize (0, 100);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      // <=0 not allowed
      final SizeInt aSize = aID2.getBestMatchingSize (100, 0);
      fail ();
      assertNull (aSize);
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testGetScaled ()
  {
    final SizeInt aID = new SizeInt (100, 150);
    SizeInt aID2 = aID.getScaledToWidth (150);
    assertEquals (150, aID2.getWidth ());
    assertEquals (225, aID2.getHeight ());
    aID2 = aID.getScaledToHeight (300);
    assertEquals (200, aID2.getWidth ());
    assertEquals (300, aID2.getHeight ());
    aID2 = aID.getScaledToWidth (50);
    assertEquals (50, aID2.getWidth ());
    assertEquals (75, aID2.getHeight ());
    assertSame (aID2, aID2.getScaledToWidth (50));
    aID2 = aID.getScaledToHeight (75);
    assertEquals (50, aID2.getWidth ());
    assertEquals (75, aID2.getHeight ());
    assertSame (aID2, aID2.getScaledToHeight (75));

    try
    {
      aID2.getScaledToWidth (0);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      aID2.getScaledToHeight (0);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}

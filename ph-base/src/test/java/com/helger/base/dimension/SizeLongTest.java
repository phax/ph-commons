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
package com.helger.base.dimension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.base.BaseTestHelper;

/**
 * Test class for class {@link SizeLong}.
 *
 * @author Philip Helger
 */
public final class SizeLongTest
{
  @Test
  public void testCtor ()
  {
    final SizeLong aID = new SizeLong (320L, 240L);
    assertEquals (320L, aID.getWidth ());
    assertEquals (240L, aID.getHeight ());
    assertNotNull (aID.toString ());

    try
    {
      new SizeLong (300L, -1L);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      new SizeLong (-1L, 300L);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testCopyCtors ()
  {
    assertEquals (320L, new SizeLong (new SizeInt (320, 240)).getWidth ());
    assertEquals (320L, new SizeLong (new SizeLong (320L, 240L)).getWidth ());
  }

  @Test
  public void testOrientation ()
  {
    assertTrue (new SizeLong (320L, 240L).isLandscape ());
    assertFalse (new SizeLong (320L, 240L).isPortrait ());
    assertFalse (new SizeLong (320L, 240L).isQuadratic ());

    assertTrue (new SizeLong (240L, 320L).isPortrait ());
    assertTrue (new SizeLong (240L, 240L).isQuadratic ());
  }

  @Test
  public void testGetBestMatchingSize ()
  {
    final SizeLong aID = new SizeLong (320L, 240L);

    SizeLong aResult = aID.getBestMatchingSize (160L, 200L);
    assertEquals (160L, aResult.getWidth ());
    assertEquals (120L, aResult.getHeight ());

    aResult = aID.getBestMatchingSize (400L, 120L);
    assertEquals (160L, aResult.getWidth ());
    assertEquals (120L, aResult.getHeight ());

    assertSame (aID, aID.getBestMatchingSize (400L, 300L));

    try
    {
      aID.getBestMatchingSize (0L, 100L);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      aID.getBestMatchingSize (100L, 0L);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testGetScaledToWidthAndHeight ()
  {
    final SizeLong aID = new SizeLong (320L, 240L);

    SizeLong aResult = aID.getScaledToWidth (160L);
    assertEquals (160L, aResult.getWidth ());
    assertEquals (120L, aResult.getHeight ());
    assertSame (aID, aID.getScaledToWidth (320L));

    aResult = aID.getScaledToHeight (120L);
    assertEquals (160L, aResult.getWidth ());
    assertEquals (120L, aResult.getHeight ());
    assertSame (aID, aID.getScaledToHeight (240L));

    try
    {
      aID.getScaledToWidth (0L);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      aID.getScaledToHeight (0L);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testAddedAndSubtracted ()
  {
    final SizeLong aID = new SizeLong (320L, 240L);

    SizeLong aResult = aID.getAdded (new SizeLong (10L, 20L));
    assertEquals (330L, aResult.getWidth ());
    assertEquals (260L, aResult.getHeight ());

    aResult = aID.getSubtracted (new SizeLong (10L, 20L));
    assertEquals (310L, aResult.getWidth ());
    assertEquals (220L, aResult.getHeight ());
  }

  @Test
  public void testEqualsHashcode ()
  {
    BaseTestHelper.testDefaultImplementationWithEqualContentObject (new SizeLong (320L, 240L),
                                                                    new SizeLong (320L, 240L));
    BaseTestHelper.testDefaultImplementationWithDifferentContentObject (new SizeLong (320L, 240L),
                                                                        new SizeLong (321L, 240L));
    BaseTestHelper.testDefaultImplementationWithDifferentContentObject (new SizeLong (320L, 240L),
                                                                        new SizeLong (320L, 241L));
  }
}

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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.base.BaseTestHelper;
import com.helger.base.mock.CommonsAssert;

/**
 * Test class for class {@link SizeFloat}.
 *
 * @author Philip Helger
 */
public final class SizeFloatTest
{
  @Test
  public void testCtor ()
  {
    final SizeFloat aID = new SizeFloat (320f, 240f);
    CommonsAssert.assertEquals (320f, aID.getWidth ());
    CommonsAssert.assertEquals (240f, aID.getHeight ());
    assertNotNull (aID.toString ());

    try
    {
      new SizeFloat (300f, -1f);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      new SizeFloat (-1f, 300f);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testCopyCtors ()
  {
    CommonsAssert.assertEquals (320f, new SizeFloat (new SizeInt (320, 240)).getWidth ());
    CommonsAssert.assertEquals (320f, new SizeFloat (new SizeLong (320L, 240L)).getWidth ());
    CommonsAssert.assertEquals (320f, new SizeFloat (new SizeFloat (320f, 240f)).getWidth ());
  }

  @Test
  public void testOrientation ()
  {
    assertTrue (new SizeFloat (320f, 240f).isLandscape ());
    assertFalse (new SizeFloat (320f, 240f).isPortrait ());
    assertFalse (new SizeFloat (320f, 240f).isQuadratic ());

    assertTrue (new SizeFloat (240f, 320f).isPortrait ());
    assertFalse (new SizeFloat (240f, 320f).isLandscape ());

    assertTrue (new SizeFloat (240f, 240f).isQuadratic ());
    assertFalse (new SizeFloat (240f, 240f).isLandscape ());
    assertFalse (new SizeFloat (240f, 240f).isPortrait ());
  }

  @Test
  public void testGetBestMatchingSize ()
  {
    final SizeFloat aID = new SizeFloat (320f, 240f);

    // Width is the limiting factor
    SizeFloat aResult = aID.getBestMatchingSize (160f, 200f);
    CommonsAssert.assertEquals (160f, aResult.getWidth ());
    CommonsAssert.assertEquals (120f, aResult.getHeight ());

    // Height is the limiting factor
    aResult = aID.getBestMatchingSize (400f, 120f);
    CommonsAssert.assertEquals (160f, aResult.getWidth ());
    CommonsAssert.assertEquals (120f, aResult.getHeight ());

    // Already small enough - the same instance is returned
    assertSame (aID, aID.getBestMatchingSize (400f, 300f));

    try
    {
      aID.getBestMatchingSize (0f, 100f);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      aID.getBestMatchingSize (100f, 0f);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testGetScaledToWidthAndHeight ()
  {
    final SizeFloat aID = new SizeFloat (320f, 240f);

    SizeFloat aResult = aID.getScaledToWidth (160f);
    CommonsAssert.assertEquals (160f, aResult.getWidth ());
    CommonsAssert.assertEquals (120f, aResult.getHeight ());
    // The very same width returns the same instance
    assertSame (aID, aID.getScaledToWidth (320f));

    aResult = aID.getScaledToHeight (120f);
    CommonsAssert.assertEquals (160f, aResult.getWidth ());
    CommonsAssert.assertEquals (120f, aResult.getHeight ());
    assertSame (aID, aID.getScaledToHeight (240f));

    try
    {
      aID.getScaledToWidth (0f);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      aID.getScaledToHeight (0f);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testAddedAndSubtracted ()
  {
    final SizeFloat aID = new SizeFloat (320f, 240f);

    SizeFloat aResult = aID.getAdded (new SizeInt (10, 20));
    CommonsAssert.assertEquals (330f, aResult.getWidth ());
    CommonsAssert.assertEquals (260f, aResult.getHeight ());

    aResult = aID.getAdded (new SizeFloat (10f, 20f));
    CommonsAssert.assertEquals (330f, aResult.getWidth ());
    CommonsAssert.assertEquals (260f, aResult.getHeight ());

    aResult = aID.getSubtracted (new SizeInt (10, 20));
    CommonsAssert.assertEquals (310f, aResult.getWidth ());
    CommonsAssert.assertEquals (220f, aResult.getHeight ());

    aResult = aID.getSubtracted (new SizeFloat (10f, 20f));
    CommonsAssert.assertEquals (310f, aResult.getWidth ());
    CommonsAssert.assertEquals (220f, aResult.getHeight ());
  }

  @Test
  public void testEqualsHashcode ()
  {
    BaseTestHelper.testDefaultImplementationWithEqualContentObject (new SizeFloat (320f, 240f),
                                                                    new SizeFloat (320f, 240f));
    BaseTestHelper.testDefaultImplementationWithDifferentContentObject (new SizeFloat (320f, 240f),
                                                                        new SizeFloat (321f, 240f));
    BaseTestHelper.testDefaultImplementationWithDifferentContentObject (new SizeFloat (320f, 240f),
                                                                        new SizeFloat (320f, 241f));
  }
}

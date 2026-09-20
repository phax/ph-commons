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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.base.mock.CommonsAssert;

/**
 * Additional test class for {@link SizeDouble} and {@link SizeInt}, covering the copy constructors,
 * the orientation helpers and the scaling.
 *
 * @author Philip Helger
 */
public final class SizeDoubleAndIntExtTest
{
  @Test
  public void testSizeDoubleCopyCtors ()
  {
    CommonsAssert.assertEquals (320d, new SizeDouble (new SizeInt (320, 240)).getWidth ());
    CommonsAssert.assertEquals (320d, new SizeDouble (new SizeLong (320L, 240L)).getWidth ());
    CommonsAssert.assertEquals (320d, new SizeDouble (new SizeFloat (320f, 240f)).getWidth ());
    CommonsAssert.assertEquals (320d, new SizeDouble (new SizeDouble (320d, 240d)).getWidth ());
  }

  @Test
  public void testSizeDoubleOrientation ()
  {
    assertTrue (new SizeDouble (320d, 240d).isLandscape ());
    assertFalse (new SizeDouble (320d, 240d).isPortrait ());
    assertTrue (new SizeDouble (240d, 320d).isPortrait ());
    assertTrue (new SizeDouble (240d, 240d).isQuadratic ());
  }

  @Test
  public void testSizeDoubleScaling ()
  {
    final SizeDouble aID = new SizeDouble (320d, 240d);

    SizeDouble aResult = aID.getBestMatchingSize (160d, 200d);
    CommonsAssert.assertEquals (160d, aResult.getWidth ());
    CommonsAssert.assertEquals (120d, aResult.getHeight ());

    aResult = aID.getBestMatchingSize (400d, 120d);
    CommonsAssert.assertEquals (160d, aResult.getWidth ());
    assertSame (aID, aID.getBestMatchingSize (400d, 300d));

    aResult = aID.getScaledToWidth (160d);
    CommonsAssert.assertEquals (120d, aResult.getHeight ());
    assertSame (aID, aID.getScaledToWidth (320d));

    aResult = aID.getScaledToHeight (120d);
    CommonsAssert.assertEquals (160d, aResult.getWidth ());
    assertSame (aID, aID.getScaledToHeight (240d));

    try
    {
      aID.getScaledToWidth (0d);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      aID.getScaledToHeight (0d);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      aID.getBestMatchingSize (0d, 100d);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      aID.getBestMatchingSize (100d, 0d);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testSizeDoubleAddedAndSubtracted ()
  {
    final SizeDouble aID = new SizeDouble (320d, 240d);

    CommonsAssert.assertEquals (330d, aID.getAdded (new SizeInt (10, 20)).getWidth ());
    CommonsAssert.assertEquals (330d, aID.getAdded (new SizeFloat (10f, 20f)).getWidth ());
    CommonsAssert.assertEquals (330d, aID.getAdded (new SizeDouble (10d, 20d)).getWidth ());

    CommonsAssert.assertEquals (310d, aID.getSubtracted (new SizeInt (10, 20)).getWidth ());
    CommonsAssert.assertEquals (310d, aID.getSubtracted (new SizeFloat (10f, 20f)).getWidth ());
    CommonsAssert.assertEquals (310d, aID.getSubtracted (new SizeDouble (10d, 20d)).getWidth ());
  }

  @Test
  public void testSizeIntOrientationAndCopyCtor ()
  {
    assertTrue (new SizeInt (320, 240).isLandscape ());
    assertFalse (new SizeInt (320, 240).isPortrait ());
    assertTrue (new SizeInt (240, 320).isPortrait ());
    assertTrue (new SizeInt (240, 240).isQuadratic ());

    CommonsAssert.assertEquals (320, new SizeInt (new SizeInt (320, 240)).getWidth ());
  }
}

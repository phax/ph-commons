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
package com.helger.commons.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.math.RoundingMode;
import java.util.Locale;

import org.junit.Test;

import com.helger.commons.math.RoundHelper.EDecimalType;
import com.helger.commons.mock.CommonsAssert;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link RoundHelper}.
 *
 * @author Philip Helger
 */
public final class RoundHelperTest
{
  @Test
  public void testDecimalType ()
  {
    for (final EDecimalType e : EDecimalType.values ())
      assertSame (e, EDecimalType.valueOf (e.name ()));
  }

  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
  @Test
  public void testGetRounded ()
  {
    final double d = 1.1;
    try
    {
      // Negative scale
      RoundHelper.getRounded (d, -1, RoundingMode.CEILING, EDecimalType.FIX);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      RoundHelper.getRounded (d, 1, null, EDecimalType.FIX);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // Negative scale
      RoundHelper.getRounded (d, 1, RoundingMode.CEILING, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetRoundedUpFix ()
  {
    CommonsAssert.assertEquals (1, RoundHelper.getRoundedUpFix (0.9, 0));
    CommonsAssert.assertEquals (1, RoundHelper.getRoundedUpFix (1.0, 0));
    CommonsAssert.assertEquals (1, RoundHelper.getRoundedUpFix (1.1, 0));
    CommonsAssert.assertEquals (1, RoundHelper.getRoundedUpFix (1.4, 0));
    CommonsAssert.assertEquals (1, RoundHelper.getRoundedUpFix (1.4999, 0));
    CommonsAssert.assertEquals (1.50, RoundHelper.getRoundedUpFix (1.4999, 2));
    CommonsAssert.assertEquals (1.50, RoundHelper.getRoundedUpFix2 (1.4999));
    CommonsAssert.assertEquals (2, RoundHelper.getRoundedUpFix (1.5, 0));
    CommonsAssert.assertEquals (Double.NaN, RoundHelper.getRoundedUpFix (Double.NaN, 0));
    CommonsAssert.assertEquals (Double.POSITIVE_INFINITY, RoundHelper.getRoundedUpFix (Double.POSITIVE_INFINITY, 0));
    CommonsAssert.assertEquals (Double.NEGATIVE_INFINITY, RoundHelper.getRoundedUpFix (Double.NEGATIVE_INFINITY, 0));
  }

  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
  @Test
  public void testGetFormatted ()
  {
    final double d = 1.1;
    try
    {
      RoundHelper.getFormatted (d, -1, EDecimalType.FIX, Locale.US);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      RoundHelper.getFormatted (d, 1, null, Locale.US);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      RoundHelper.getFormatted (d, 1, EDecimalType.EXP, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetRoundedEvenExp ()
  {
    CommonsAssert.assertEquals (9E-1, RoundHelper.getRoundedEvenExp (0.9, 0));
    CommonsAssert.assertEquals (1E0, RoundHelper.getRoundedEvenExp (1.0, 0));
    CommonsAssert.assertEquals (1E0, RoundHelper.getRoundedEvenExp (1.1, 0));
    CommonsAssert.assertEquals (1E0, RoundHelper.getRoundedEvenExp (1.4, 0));
    CommonsAssert.assertEquals (1E0, RoundHelper.getRoundedEvenExp (1.4999, 0));
    CommonsAssert.assertEquals (1.50E0, RoundHelper.getRoundedEvenExp (1.4999, 2));
    CommonsAssert.assertEquals (1.50E0, RoundHelper.getRoundedEvenExp2 (1.4999));
    CommonsAssert.assertEquals (2E0, RoundHelper.getRoundedEvenExp (1.5, 0));
  }

  @Test
  public void testGetFormattedFix ()
  {
    assertEquals ("1", RoundHelper.getFormattedFix (0.9, 0, Locale.US));
    assertEquals ("1", RoundHelper.getFormattedFix (1.0, 0, Locale.US));
    assertEquals ("1", RoundHelper.getFormattedFix (1.1, 0, Locale.US));
    assertEquals ("1", RoundHelper.getFormattedFix (1.4, 0, Locale.US));
    assertEquals ("1", RoundHelper.getFormattedFix (1.4999, 0, Locale.US));
    assertEquals ("2", RoundHelper.getFormattedFix (1.5, 0, Locale.US));

    assertEquals ("0.90", RoundHelper.getFormattedFix2 (0.9, Locale.US));
    assertEquals ("0,90", RoundHelper.getFormattedFix2 (0.9, Locale.GERMANY));
    assertEquals ("1.00", RoundHelper.getFormattedFix2 (1.0, Locale.US));
    assertEquals ("1.10", RoundHelper.getFormattedFix2 (1.1, Locale.US));
    assertEquals ("1.40", RoundHelper.getFormattedFix2 (1.4, Locale.US));
    assertEquals ("1.50", RoundHelper.getFormattedFix2 (1.4999, Locale.US));
    assertEquals ("1.50", RoundHelper.getFormattedFix2 (1.5, Locale.US));
    assertEquals (Double.toString (Double.NaN), RoundHelper.getFormattedFix (Double.NaN, 0, Locale.US));
    assertEquals (Double.toString (Double.POSITIVE_INFINITY),
                  RoundHelper.getFormattedFix (Double.POSITIVE_INFINITY, 0, Locale.US));
    assertEquals (Double.toString (Double.NEGATIVE_INFINITY),
                  RoundHelper.getFormattedFix (Double.NEGATIVE_INFINITY, 0, Locale.US));
  }

  @Test
  public void testGetFormattedExp ()
  {
    assertEquals ("9E-1", RoundHelper.getFormattedExp (0.9, 0, Locale.US));
    assertEquals ("1E0", RoundHelper.getFormattedExp (1.0, 0, Locale.US));
    assertEquals ("1E0", RoundHelper.getFormattedExp (1.1, 0, Locale.US));
    assertEquals ("1E0", RoundHelper.getFormattedExp (1.4, 0, Locale.US));
    assertEquals ("1E0", RoundHelper.getFormattedExp (1.4999, 0, Locale.US));
    assertEquals ("2E0", RoundHelper.getFormattedExp (1.5, 0, Locale.US));

    assertEquals ("9.00E-1", RoundHelper.getFormattedExp2 (0.9, Locale.US));
    assertEquals ("1.00E0", RoundHelper.getFormattedExp2 (1.0, Locale.US));
    assertEquals ("1.10E0", RoundHelper.getFormattedExp2 (1.1, Locale.US));
    assertEquals ("1.40E0", RoundHelper.getFormattedExp2 (1.4, Locale.US));
    assertEquals ("1.50E0", RoundHelper.getFormattedExp2 (1.4999, Locale.US));
    assertEquals ("1.50E0", RoundHelper.getFormattedExp2 (1.5, Locale.US));
  }
}

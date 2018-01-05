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
package com.helger.commons.mock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.equals.EqualsHelper;

/**
 * Extensions for the default JUnit assertions.
 *
 * @author Philip Helger
 */
@Immutable
public final class CommonsAssert
{
  public static final float FLOAT_ALLOWED_ROUNDING_DIFFERENCE = 0.001f;
  public static final double DOUBLE_ALLOWED_ROUNDING_DIFFERENCE = 0.001;

  @PresentForCodeCoverage
  private static final CommonsAssert s_aInstance = new CommonsAssert ();

  private CommonsAssert ()
  {}

  public static void fail ()
  {
    fail ("Forced fail");
  }

  public static void fail (@Nonnull final String sMsg)
  {
    throw new IllegalArgumentException (sMsg);
  }

  public static void assertEquals (final boolean x, final boolean y)
  {
    assertEquals ((String) null, x, y);
  }

  public static void assertEquals (final boolean x, @Nonnull final Boolean y)
  {
    ValueEnforcer.notNull (y, "y");
    assertEquals ((String) null, x, y.booleanValue ());
  }

  public static void assertEquals (@Nullable final String sUserMsg, final boolean x, final boolean y)
  {
    if (x != y)
      fail ("<" +
            x +
            "> is not equal to <" +
            y +
            ">" +
            (sUserMsg != null && sUserMsg.length () > 0 ? ": " + sUserMsg : ""));
  }

  public static void assertEquals (final double x, final double y)
  {
    assertEquals ((String) null, x, y);
  }

  public static void assertEquals (final double x, @Nonnull final Double y)
  {
    ValueEnforcer.notNull (y, "y");
    assertEquals ((String) null, x, y.doubleValue ());
  }

  public static void assertEquals (@Nullable final String sUserMsg, final double x, final double y)
  {
    // Do not call MathHelper.abs in here, because this class should be as close
    // to the runtime as possible!
    if (Double.compare (x, y) != 0 && Math.abs (x - y) > DOUBLE_ALLOWED_ROUNDING_DIFFERENCE)
      fail ("<" +
            x +
            "> is not equal to <" +
            y +
            ">" +
            (sUserMsg != null && sUserMsg.length () > 0 ? ": " + sUserMsg : ""));
  }

  public static void assertEquals (final float x, final float y)
  {
    assertEquals ((String) null, x, y);
  }

  public static void assertEquals (final float x, @Nonnull final Float y)
  {
    ValueEnforcer.notNull (y, "y");
    assertEquals ((String) null, x, y.floatValue ());
  }

  public static void assertEquals (@Nullable final String sUserMsg, final float x, final float y)
  {
    // Do not call MathHelper.abs in here, because this class should be as close
    // to the runtime as possible!
    if (Float.compare (x, y) != 0 && Math.abs (x - y) > FLOAT_ALLOWED_ROUNDING_DIFFERENCE)
      fail ("<" +
            x +
            "> is not equal to <" +
            y +
            ">" +
            (sUserMsg != null && sUserMsg.length () > 0 ? ": " + sUserMsg : ""));
  }

  public static void assertEquals (@Nullable final float [] x, @Nullable final float [] y)
  {
    // Same object - check first
    if (x == y)
      return;

    // Is only one value null?
    if (x == null)
      fail ("First parameter is null");
    if (y == null)
      fail ("Second parameter is null");

    if (x.length != y.length)
      fail ("Length mismatch");

    for (int i = 0; i < x.length; ++i)
      assertEquals ("Element [" + i + "] mismatch", x[i], y[i]);
  }

  public static void assertEquals (@Nullable final double [] x, @Nullable final double [] y)
  {
    // Same object - check first
    if (x == y)
      return;

    // Is only one value null?
    if (x == null)
      fail ("First parameter is null");
    if (y == null)
      fail ("Second parameter is null");

    if (x.length != y.length)
      fail ("Length mismatch");

    for (int i = 0; i < x.length; ++i)
      assertEquals ("Element [" + i + "] mismatch", x[i], y[i]);
  }

  public static <T> void assertEquals (@Nullable final T x, @Nullable final T y)
  {
    assertEquals ((String) null, x, y);
  }

  public static <T> void assertEquals (@Nullable final String sUserMsg, @Nullable final T x, @Nullable final T y)
  {
    if (!EqualsHelper.equals (x, y))
      fail ("<" +
            x +
            "> is not equal to <" +
            y +
            ">" +
            (sUserMsg != null && sUserMsg.length () > 0 ? ": " + sUserMsg : ""));
  }

  public static <T> void assertNotEquals (@Nullable final T x, @Nullable final T y)
  {
    assertNotEquals ((String) null, x, y);
  }

  public static <T> void assertNotEquals (@Nullable final String sUserMsg, @Nullable final T x, @Nullable final T y)
  {
    if (EqualsHelper.equals (x, y))
      fail ("<" +
            x +
            "> is equal to <" +
            y +
            ">" +
            (sUserMsg != null && sUserMsg.length () > 0 ? ": " + sUserMsg : ""));
  }
}

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
package com.helger.commons.mock;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.mock.CommonsAssert;
import com.helger.commons.equals.EqualsHelperExt;

import jakarta.annotation.Nullable;

/**
 * Extensions for the default JUnit assertions.
 *
 * @author Philip Helger
 */
@Immutable
public final class CommonsAssertExt extends CommonsAssert
{
  @PresentForCodeCoverage
  private static final CommonsAssertExt INSTANCE = new CommonsAssertExt ();

  private CommonsAssertExt ()
  {}

  public static void fail ()
  {
    fail ("Forced fail");
  }

  /**
   * Like JUnit assertEquals but using {@link EqualsHelperExt}.
   *
   * @param x
   *        Fist object. May be <code>null</code>
   * @param y
   *        Second object. May be <code>null</code>.
   * @param <T>
   *        data type to compare
   */
  public static <T> void assertEquals (@Nullable final T x, @Nullable final T y)
  {
    assertEquals ((String) null, x, y);
  }

  /**
   * Like JUnit assertEquals but using {@link EqualsHelperExt}.
   *
   * @param sUserMsg
   *        Optional user message. May be <code>null</code>.
   * @param x
   *        Fist object. May be <code>null</code>
   * @param y
   *        Second object. May be <code>null</code>.
   * @param <T>
   *        data type to compare
   */
  public static <T> void assertEquals (@Nullable final String sUserMsg, @Nullable final T x, @Nullable final T y)
  {
    if (!EqualsHelperExt.extEquals (x, y))
      fail ("<" +
            x +
            "> is not equal to <" +
            y +
            ">" +
            (sUserMsg != null && sUserMsg.length () > 0 ? ": " + sUserMsg : ""));
  }

  /**
   * Like JUnit assertNotEquals but using {@link EqualsHelperExt}.
   *
   * @param x
   *        Fist object. May be <code>null</code>
   * @param y
   *        Second object. May be <code>null</code>.
   * @param <T>
   *        data type to compare
   */
  public static <T> void assertNotEquals (@Nullable final T x, @Nullable final T y)
  {
    assertNotEquals ((String) null, x, y);
  }

  /**
   * Like JUnit assertNotEquals but using {@link EqualsHelperExt}.
   *
   * @param sUserMsg
   *        Optional user message. May be <code>null</code>.
   * @param x
   *        Fist object. May be <code>null</code>
   * @param y
   *        Second object. May be <code>null</code>.
   * @param <T>
   *        data type to compare
   */
  public static <T> void assertNotEquals (@Nullable final String sUserMsg, @Nullable final T x, @Nullable final T y)
  {
    if (EqualsHelperExt.extEquals (x, y))
      fail ("<" +
            x +
            "> is equal to <" +
            y +
            ">" +
            (sUserMsg != null && sUserMsg.length () > 0 ? ": " + sUserMsg : ""));
  }
}

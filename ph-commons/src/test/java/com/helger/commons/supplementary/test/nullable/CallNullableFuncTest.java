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
package com.helger.commons.supplementary.test.nullable;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class CallNullableFuncTest
{
  private CallNullableFuncTest ()
  {}

  @SuppressFBWarnings ("NP_NONNULL_PARAM_VIOLATION")
  public static void testCorrect ()
  {
    final MockNullableTestCorrect n = new MockNullableTestCorrect ();
    n.paramUndefined (null);
    // FindBugs complains here:
    n.paramNonnull (null);
    n.paramNonnullAlways (null);
    n.paramNonnullMaybe (null);
    n.paramNonnullNever (null);
    n.paramNonnullUnknown (null);
  }

  @SuppressFBWarnings ("NP_NONNULL_PARAM_VIOLATION")
  public static void testCorrect2 ()
  {
    final IMockNullableTest n = new MockNullableTestCorrect ();
    n.paramUndefined (null);
    // FindBugs complains here:
    n.paramNonnull (null);
    n.paramNonnullAlways (null);
    n.paramNonnullMaybe (null);
    n.paramNonnullNever (null);
    n.paramNonnullUnknown (null);
  }

  @SuppressFBWarnings ("NP_NONNULL_PARAM_VIOLATION")
  public static void testNonNull ()
  {
    final MockNullableTestNonNull n = new MockNullableTestNonNull ();
    n.paramUndefined (null);
    // FindBugs complains here:
    n.paramNonnull (null);
    n.paramNonnullAlways (null);
    n.paramNonnullMaybe (null);
    n.paramNonnullNever (null);
    n.paramNonnullUnknown (null);
  }

  @SuppressFBWarnings ("NP_NONNULL_PARAM_VIOLATION")
  public static void testNonNull2 ()
  {
    final IMockNullableTest n = new MockNullableTestNonNull ();
    n.paramUndefined (null);
    // FindBugs complains here:
    n.paramNonnull (null);
    n.paramNonnullAlways (null);
    n.paramNonnullMaybe (null);
    n.paramNonnullNever (null);
    n.paramNonnullUnknown (null);
  }

  public static void testNullable ()
  {
    final MockNullableTestNullable n = new MockNullableTestNullable ();
    n.paramUndefined (null);
    n.paramNonnull (null);
    n.paramNonnullAlways (null);
    n.paramNonnullMaybe (null);
    n.paramNonnullNever (null);
    n.paramNonnullUnknown (null);
  }

  @SuppressFBWarnings ("NP_NONNULL_PARAM_VIOLATION")
  public static void testNullable2 ()
  {
    final IMockNullableTest n = new MockNullableTestNullable ();
    n.paramUndefined (null);
    // FindBugs complains here:
    n.paramNonnull (null);
    n.paramNonnullAlways (null);
    n.paramNonnullMaybe (null);
    n.paramNonnullNever (null);
    n.paramNonnullUnknown (null);
  }
}

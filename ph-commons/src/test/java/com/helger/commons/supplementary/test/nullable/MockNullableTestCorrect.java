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
package com.helger.commons.supplementary.test.nullable;

import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.annotation.meta.When;

public class MockNullableTestCorrect implements IMockNullableTest
{
  public void paramUndefined (final String s)
  {}

  public void paramNonnull (@Nonnull final String s)
  {}

  public void paramNonnullAlways (@Nonnull (when = When.ALWAYS) final String s)
  {}

  public void paramNonnullMaybe (@Nonnull (when = When.MAYBE) final String s)
  {}

  public void paramNonnullNever (@Nonnull (when = When.NEVER) final String s)
  {}

  public void paramNonnullUnknown (@Nonnull (when = When.UNKNOWN) final String s)
  {}

  public void paramNullable (@Nullable final String s)
  {}
}

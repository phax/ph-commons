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

import javax.annotation.Nonnull;

public class MockNullableTestNonNull implements IMockNullableTest
{
  public void paramUndefined (@Nonnull final String s)
  {}

  public void paramNonnull (@Nonnull final String s)
  {}

  public void paramNonnullAlways (@Nonnull final String s)
  {}

  public void paramNonnullMaybe (@Nonnull final String s)
  {}

  public void paramNonnullNever (@Nonnull final String s)
  {}

  public void paramNonnullUnknown (@Nonnull final String s)
  {}

  // Eclipse complains here, because in the interface the parameter is declared
  // with @Nullable
  public void paramNullable (@Nonnull final String s)
  {}
}

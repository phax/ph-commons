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
package com.helger.commons.equals;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.equals.EqualsHelper;

import jakarta.annotation.Nullable;

/**
 * A small helper class that provides helper methods for easy <code>equals</code> method generation
 *
 * @author Philip Helger
 */
@Immutable
public final class EqualsHelperExt extends EqualsHelper
{
  @PresentForCodeCoverage
  private static final EqualsHelperExt INSTANCE = new EqualsHelperExt ();

  private EqualsHelperExt ()
  {}

  /**
   * Check if two values are equal. This method only exists, so that no type differentiation is
   * needed.
   *
   * @param aObj1
   *        First value
   * @param aObj2
   *        Second value
   * @return <code>true</code> if they are equal, <code>false</code> otherwise.
   * @see EqualsImplementationRegistry#areEqual(Object, Object)
   */
  public static boolean extEquals (@Nullable final Object aObj1, @Nullable final Object aObj2)
  {
    return EqualsImplementationRegistry.areEqual (aObj1, aObj2);
  }
}

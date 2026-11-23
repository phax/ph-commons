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
package com.helger.unittest.property;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;

/**
 * Assumptions inside a property. A failed assumption discards the current sample instead of failing
 * the property.
 *
 * @author Philip Helger
 * @since 12.8.0
 */
@Immutable
public final class PropertyAssume
{
  @PresentForCodeCoverage
  private static final PropertyAssume INSTANCE = new PropertyAssume ();

  private PropertyAssume ()
  {}

  /**
   * Discard the current sample if the condition is not met.
   *
   * @param bCondition
   *        The condition that must be <code>true</code> for the sample to be checked.
   * @throws SampleRejectedException
   *         If the condition is <code>false</code>
   */
  public static void that (final boolean bCondition)
  {
    if (!bCondition)
      throw new SampleRejectedException ("Assumption not met");
  }
}

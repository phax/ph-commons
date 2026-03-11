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
package com.helger.base.state;

import org.jspecify.annotations.NonNull;

/**
 * Small enum for setter method to identify whether a value is mandatory or not.
 *
 * @author Philip Helger
 */
public enum EMandatory implements IMandatoryIndicator
{
  MANDATORY,
  OPTIONAL;

  /** {@inheritDoc} */
  public boolean isMandatory ()
  {
    return this == MANDATORY;
  }

  /**
   * Convert a boolean value to the corresponding {@link EMandatory} enum value.
   *
   * @param bMandatory
   *        <code>true</code> for {@link #MANDATORY}, <code>false</code> for
   *        {@link #OPTIONAL}.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static EMandatory valueOf (final boolean bMandatory)
  {
    return bMandatory ? MANDATORY : OPTIONAL;
  }

  /**
   * Convert an {@link IMandatoryIndicator} to the corresponding
   * {@link EMandatory} enum value.
   *
   * @param aMandatoryIndicator
   *        The mandatory indicator to convert. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static EMandatory valueOf (@NonNull final IMandatoryIndicator aMandatoryIndicator)
  {
    return valueOf (aMandatoryIndicator.isMandatory ());
  }
}

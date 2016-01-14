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
package com.helger.commons.state;

import javax.annotation.Nonnull;

/**
 * Small enum for checks to indicate valid or invalid.
 *
 * @author Philip Helger
 */
public enum EValidity implements IValidityIndicator
{
  VALID,
  INVALID;

  public boolean isValid ()
  {
    return this == VALID;
  }

  @Nonnull
  public static EValidity valueOf (final boolean bValidity)
  {
    return bValidity ? VALID : INVALID;
  }

  @Nonnull
  public static EValidity valueOf (@Nonnull final IValidityIndicator aValidityIndicator)
  {
    return valueOf (aValidityIndicator.isValid ());
  }
}

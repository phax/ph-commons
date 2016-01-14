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
 * Very simple interface for an object that has mandatory/optional indication.
 *
 * @author Philip Helger
 */
public interface IMandatoryIndicator
{
  /**
   * @return <code>true</code> if mandatory and <code>false</code> if not
   *         mandatory.
   */
  boolean isMandatory ();

  /**
   * @return <code>true</code> if optional and <code>false</code> if not
   *         optional.
   */
  default boolean isOptional ()
  {
    return !isMandatory ();
  }

  @Nonnull
  default EMandatory or (@Nonnull final IMandatoryIndicator aMandatory)
  {
    return EMandatory.valueOf (isMandatory () || aMandatory.isMandatory ());
  }

  @Nonnull
  default EMandatory and (@Nonnull final IMandatoryIndicator aMandatory)
  {
    return EMandatory.valueOf (isMandatory () && aMandatory.isMandatory ());
  }
}

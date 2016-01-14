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
 * Simple interface for all objects that can be enabled and disabled.
 *
 * @author Philip Helger
 */
public interface IEnabledIndicator
{
  /**
   * @return <code>true</code> if the object is currently enabled,
   *         <code>false</code> otherwise.
   */
  boolean isEnabled ();

  /**
   * @return <code>true</code> if the object is currently disabled,
   *         <code>false</code> otherwise.
   */
  default boolean isDisabled ()
  {
    return !isEnabled ();
  }

  @Nonnull
  default EEnabled or (@Nonnull final IEnabledIndicator aEnabled)
  {
    return EEnabled.valueOf (isEnabled () || aEnabled.isEnabled ());
  }

  @Nonnull
  default EEnabled and (@Nonnull final IEnabledIndicator aEnabled)
  {
    return EEnabled.valueOf (isEnabled () && aEnabled.isEnabled ());
  }
}

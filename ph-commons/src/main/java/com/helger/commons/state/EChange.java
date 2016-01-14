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
 * Small enum for setter method to identify whether a value has changed or not.
 *
 * @author Philip Helger
 */
public enum EChange implements IChangeIndicator
{
  CHANGED,
  UNCHANGED;

  public boolean isChanged ()
  {
    return this == CHANGED;
  }

  @Nonnull
  public static EChange valueOf (final boolean bChanged)
  {
    return bChanged ? CHANGED : UNCHANGED;
  }

  @Nonnull
  public static EChange valueOf (@Nonnull final IChangeIndicator aChangeIndicator)
  {
    return valueOf (aChangeIndicator.isChanged ());
  }
}

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
 * Small enum for manager actions to indicate success or failure.
 *
 * @author Philip Helger
 */
public enum ESuccess implements ISuccessIndicator
{
  SUCCESS,
  FAILURE;

  public boolean isSuccess ()
  {
    return this == SUCCESS;
  }

  @Nonnull
  public static ESuccess valueOf (final boolean bSuccess)
  {
    return bSuccess ? SUCCESS : FAILURE;
  }

  @Nonnull
  public static ESuccess valueOf (@Nonnull final ISuccessIndicator aSuccessIndicator)
  {
    return valueOf (aSuccessIndicator.isSuccess ());
  }

  @Nonnull
  public static ESuccess valueOfChange (@Nonnull final IChangeIndicator aChange)
  {
    return valueOf (aChange.isChanged ());
  }
}

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
 * Small enum for manager actions to indicate success or failure.
 *
 * @author Philip Helger
 */
public enum ESuccess implements ISuccessIndicator
{
  SUCCESS,
  FAILURE;

  /** {@inheritDoc} */
  public boolean isSuccess ()
  {
    return this == SUCCESS;
  }

  /**
   * Convert a boolean value to the corresponding {@link ESuccess} enum value.
   *
   * @param bSuccess
   *        <code>true</code> for {@link #SUCCESS}, <code>false</code> for
   *        {@link #FAILURE}.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static ESuccess valueOf (final boolean bSuccess)
  {
    return bSuccess ? SUCCESS : FAILURE;
  }

  /**
   * Convert an {@link ISuccessIndicator} to the corresponding {@link ESuccess}
   * enum value.
   *
   * @param aSuccessIndicator
   *        The success indicator to convert. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static ESuccess valueOf (@NonNull final ISuccessIndicator aSuccessIndicator)
  {
    return valueOf (aSuccessIndicator.isSuccess ());
  }

  /**
   * Convert an {@link IChangeIndicator} to the corresponding {@link ESuccess}
   * enum value where changed maps to success.
   *
   * @param aChange
   *        The change indicator to convert. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static ESuccess valueOfChange (@NonNull final IChangeIndicator aChange)
  {
    return valueOf (aChange.isChanged ());
  }
}

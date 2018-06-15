/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.error.level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.state.IErrorIndicator;
import com.helger.commons.state.ISuccessIndicator;

/**
 * Interface representing an object having an error level.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IHasErrorLevel extends ISuccessIndicator, IErrorIndicator
{
  /**
   * @return The error level of this object. May not be <code>null</code>.
   */
  @Nonnull
  IErrorLevel getErrorLevel ();

  /**
   * Check if this object has the provided error level.
   *
   * @param aErrorLevel
   *        The error level to check. May be <code>null</code>.
   * @return <code>true</code> if {@link #getErrorLevel()} and the passed error
   *         level are equal, <code>false</code> otherwise.
   */
  default boolean hasErrorLevel (@Nullable final IErrorLevel aErrorLevel)
  {
    return getErrorLevel ().equals (aErrorLevel);
  }

  default boolean isSuccess ()
  {
    return getErrorLevel ().isSuccess ();
  }

  @Override
  default boolean isFailure ()
  {
    return getErrorLevel ().isFailure ();
  }

  default boolean isError ()
  {
    return getErrorLevel ().isError ();
  }

  @Override
  default boolean isNoError ()
  {
    return getErrorLevel ().isNoError ();
  }
}

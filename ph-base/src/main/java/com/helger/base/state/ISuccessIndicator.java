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
 * Very simple interface for an object that has success/failure indication.
 *
 * @author Philip Helger
 */
public interface ISuccessIndicator
{
  /**
   * @return <code>true</code> on success and <code>false</code> on failure.
   */
  boolean isSuccess ();

  /**
   * @return <code>true</code> on failure and <code>false</code> on success.
   */
  default boolean isFailure ()
  {
    return !isSuccess ();
  }

  /**
   * Logical OR of this success indicator with another one.
   *
   * @param aSuccess
   *        The other success indicator. May not be <code>null</code>.
   * @return {@link ESuccess#SUCCESS} if either this or the other indicator is
   *         successful.
   */
  @NonNull
  default ESuccess or (@NonNull final ISuccessIndicator aSuccess)
  {
    return ESuccess.valueOf (isSuccess () || aSuccess.isSuccess ());
  }

  /**
   * Logical AND of this success indicator with another one.
   *
   * @param aSuccess
   *        The other success indicator. May not be <code>null</code>.
   * @return {@link ESuccess#SUCCESS} if both this and the other indicator are
   *         successful.
   */
  @NonNull
  default ESuccess and (@NonNull final ISuccessIndicator aSuccess)
  {
    return ESuccess.valueOf (isSuccess () && aSuccess.isSuccess ());
  }
}

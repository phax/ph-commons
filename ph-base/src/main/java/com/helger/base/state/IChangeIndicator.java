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
 * Very simple interface for an object that has changed/unchanged indication.
 *
 * @author Philip Helger
 */
public interface IChangeIndicator
{
  /**
   * @return <code>true</code> if changed and <code>false</code> if unchanged.
   */
  boolean isChanged ();

  /**
   * @return <code>true</code> if unchanged and <code>false</code> if changed.
   */
  default boolean isUnchanged ()
  {
    return !isChanged ();
  }

  /**
   * Logical OR of this change indicator with another one.
   *
   * @param aChange
   *        The other change indicator. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if either this or the other indicator is
   *         changed.
   */
  @NonNull
  default EChange or (@NonNull final IChangeIndicator aChange)
  {
    return or (aChange.isChanged ());
  }

  /**
   * Logical OR of this change indicator with a boolean value.
   *
   * @param bChange
   *        The other change value.
   * @return {@link EChange#CHANGED} if either this indicator is changed or the
   *         passed value is <code>true</code>.
   */
  @NonNull
  default EChange or (final boolean bChange)
  {
    return EChange.valueOf (isChanged () || bChange);
  }

  /**
   * Logical AND of this change indicator with another one.
   *
   * @param aChange
   *        The other change indicator. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if both this and the other indicator are
   *         changed.
   */
  @NonNull
  default EChange and (@NonNull final IChangeIndicator aChange)
  {
    return and (aChange.isChanged ());
  }

  /**
   * Logical AND of this change indicator with a boolean value.
   *
   * @param bChange
   *        The other change value.
   * @return {@link EChange#CHANGED} if both this indicator is changed and the
   *         passed value is <code>true</code>.
   */
  @NonNull
  default EChange and (final boolean bChange)
  {
    return EChange.valueOf (isChanged () && bChange);
  }
}

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
package com.helger.datetime.expiration;

import java.time.LocalDateTime;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.state.EChange;

/**
 * Interface for objects that can expire.
 *
 * @author Philip Helger
 */
public interface IMutableExpirable extends IExpirable
{
  /**
   * Change the expiration date time of this object. If you want to remove the
   * expiration, please call {@link #resetExpiration()} instead.
   *
   * @param aExpirationDateTime
   *        The new expiration date time. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if the expiration date time changed,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @Nonnull
  EChange setExpirationDateTime (@Nullable LocalDateTime aExpirationDateTime);

  /**
   * Remove any available expiration data.
   *
   * @return {@link EChange#CHANGED} if the expiration was reset,
   *         {@link EChange#UNCHANGED} if no expiration was defined.
   */
  @Nonnull
  default EChange resetExpiration ()
  {
    return setExpirationDateTime (null);
  }
}

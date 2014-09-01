/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.collections.flags;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.state.EChange;
import com.helger.commons.state.IClearable;

/**
 * This is the writable extension of the {@link IReadonlyFlagContainer}.
 * 
 * @author Philip Helger
 */
public interface IFlagContainer extends IReadonlyFlagContainer, IClearable
{
  /**
   * Add a flag if it is not yet present.
   * 
   * @param sName
   *        The name of the flag. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see #removeFlag(String)
   */
  @Nonnull
  EChange addFlag (@Nonnull String sName);

  /**
   * Add an arbitrary number of flags if they are not yet present.
   * 
   * @param aValues
   *        The collections of flags to be set. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see #addFlag(String)
   */
  @Nonnull
  EChange addFlags (@Nullable Collection <String> aValues);

  /**
   * Add an arbitrary number of flags if they are not yet present.
   * 
   * @param aValues
   *        The collections of flags to be set. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   * @see #addFlag(String)
   */
  @Nonnull
  EChange addFlags (@Nullable String... aValues);

  /**
   * Remove the specified flag from the container.
   * 
   * @param sName
   *        The flag name to be removed. If it is <code>null</code> nothing
   *        happens.
   * @return {@link EChange#CHANGED} if something changed,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @Nonnull
  EChange removeFlag (@Nullable String sName);
}

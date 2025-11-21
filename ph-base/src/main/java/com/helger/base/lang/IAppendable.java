/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.base.lang;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;

/**
 * Defines a generic interface for appending objects to something.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        The implementation type.
 */
public interface IAppendable <IMPLTYPE extends IAppendable <IMPLTYPE>>
{
  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @NonNull
  IMPLTYPE append (boolean x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @NonNull
  IMPLTYPE append (byte x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @NonNull
  IMPLTYPE append (char x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @NonNull
  IMPLTYPE append (double x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @NonNull
  IMPLTYPE append (float x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @NonNull
  IMPLTYPE append (int x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @NonNull
  IMPLTYPE append (long x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @NonNull
  IMPLTYPE append (short x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @NonNull
  IMPLTYPE append (@Nullable Object x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @NonNull
  IMPLTYPE append (boolean @Nullable [] x);

  /**
   * @param x
   *        Array to add
   * @param nOfs
   *        Offset to start from. Must be &ge; 0.
   * @param nLen
   *        Number of array items to use. Must be &ge; 0.
   * @return this
   */
  @NonNull
  IMPLTYPE append (boolean @Nullable [] x, @Nonnegative int nOfs, @Nonnegative int nLen);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @NonNull
  IMPLTYPE append (byte @Nullable [] x);

  /**
   * @param x
   *        Array to add
   * @param nOfs
   *        Offset to start from. Must be &ge; 0.
   * @param nLen
   *        Number of array items to use. Must be &ge; 0.
   * @return this
   */
  @NonNull
  IMPLTYPE append (byte @Nullable [] x, @Nonnegative int nOfs, @Nonnegative int nLen);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @NonNull
  IMPLTYPE append (char @Nullable [] x);

  /**
   * @param x
   *        Array to add
   * @param nOfs
   *        Offset to start from. Must be &ge; 0.
   * @param nLen
   *        Number of array items to use. Must be &ge; 0.
   * @return this
   */
  @NonNull
  IMPLTYPE append (char @Nullable [] x, @Nonnegative int nOfs, @Nonnegative int nLen);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @NonNull
  IMPLTYPE append (double @Nullable [] x);

  /**
   * @param x
   *        Array to add
   * @param nOfs
   *        Offset to start from. Must be &ge; 0.
   * @param nLen
   *        Number of array items to use. Must be &ge; 0.
   * @return this
   */
  @NonNull
  IMPLTYPE append (double @Nullable [] x, @Nonnegative int nOfs, @Nonnegative int nLen);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @NonNull
  IMPLTYPE append (float @Nullable [] x);

  /**
   * @param x
   *        Array to add
   * @param nOfs
   *        Offset to start from. Must be &ge; 0.
   * @param nLen
   *        Number of array items to use. Must be &ge; 0.
   * @return this
   */
  @NonNull
  IMPLTYPE append (float @Nullable [] x, @Nonnegative int nOfs, @Nonnegative int nLen);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @NonNull
  IMPLTYPE append (int @Nullable [] x);

  /**
   * @param x
   *        Array to add
   * @param nOfs
   *        Offset to start from. Must be &ge; 0.
   * @param nLen
   *        Number of array items to use. Must be &ge; 0.
   * @return this
   */
  @NonNull
  IMPLTYPE append (int @Nullable [] x, @Nonnegative int nOfs, @Nonnegative int nLen);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @NonNull
  IMPLTYPE append (long @Nullable [] x);

  /**
   * @param x
   *        Array to add
   * @param nOfs
   *        Offset to start from. Must be &ge; 0.
   * @param nLen
   *        Number of array items to use. Must be &ge; 0.
   * @return this
   */
  @NonNull
  IMPLTYPE append (long @Nullable [] x, @Nonnegative int nOfs, @Nonnegative int nLen);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @NonNull
  IMPLTYPE append (short @Nullable [] x);

  /**
   * @param x
   *        Array to add
   * @param nOfs
   *        Offset to start from. Must be &ge; 0.
   * @param nLen
   *        Number of array items to use. Must be &ge; 0.
   * @return this
   */
  @NonNull
  IMPLTYPE append (short @Nullable [] x, @Nonnegative int nOfs, @Nonnegative int nLen);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @NonNull
  IMPLTYPE append (@Nullable Object [] x);

  /**
   * @param x
   *        Array to add
   * @param nOfs
   *        Offset to start from. Must be &ge; 0.
   * @param nLen
   *        Number of array items to use. Must be &ge; 0.
   * @return this
   */
  @NonNull
  IMPLTYPE append (@Nullable Object [] x, @Nonnegative int nOfs, @Nonnegative int nLen);
}

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
package com.helger.commons.lang;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.Node;

/**
 * Defines a generic interface for appending objects to something.
 *
 * @author Philip Helger
 * @param <THISTYPE>
 *        The implementation type.
 */
public interface IAppendable <THISTYPE extends IAppendable <THISTYPE>>
{
  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @Nonnull
  THISTYPE append (boolean x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @Nonnull
  THISTYPE append (byte x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @Nonnull
  THISTYPE append (char x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @Nonnull
  THISTYPE append (double x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @Nonnull
  THISTYPE append (float x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @Nonnull
  THISTYPE append (int x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @Nonnull
  THISTYPE append (long x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @Nonnull
  THISTYPE append (short x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @Nonnull
  THISTYPE append (@Nullable Object x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @Nonnull
  THISTYPE append (@Nullable Enum <?> x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @Nonnull
  THISTYPE append (@Nullable boolean [] x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @Nonnull
  THISTYPE append (@Nullable byte [] x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @Nonnull
  THISTYPE append (@Nullable char [] x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @Nonnull
  THISTYPE append (@Nullable double [] x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @Nonnull
  THISTYPE append (@Nullable float [] x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @Nonnull
  THISTYPE append (@Nullable int [] x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @Nonnull
  THISTYPE append (@Nullable long [] x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @Nonnull
  THISTYPE append (@Nullable short [] x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @Nonnull
  THISTYPE append (@Nullable Object [] x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @Nonnull
  THISTYPE append (@Nullable Enum <?> [] x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @Nonnull
  THISTYPE append (@Nullable Iterable <?> x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @Nonnull
  THISTYPE append (@Nullable Map <?, ?> x);

  /**
   * @param x
   *        value to be appended
   * @return this
   */
  @Nonnull
  THISTYPE append (@Nullable Node x);
}

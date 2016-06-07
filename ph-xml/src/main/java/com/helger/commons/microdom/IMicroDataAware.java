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
package com.helger.commons.microdom;

import java.io.Serializable;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is a helper interface that allows for setting character data. This is
 * required by text and comment nodes.
 *
 * @author Philip Helger
 */
public interface IMicroDataAware extends Serializable
{
  /**
   * @return The currently assigned character data. May not be <code>null</code>
   *         .
   */
  @Nonnull
  CharSequence getData ();

  /**
   * Change the character sequence. Any previously set characters are lost.
   *
   * @param sData
   *        The new character sequence to be set.
   */
  void setData (@Nullable CharSequence sData);

  /**
   * Append characters to the string.
   *
   * @param sData
   *        The characters to be appended.
   */
  void appendData (@Nullable CharSequence sData);

  /**
   * Append characters to the string.
   *
   * @param aChars
   *        Base character array. May not be <code>null</code>.
   * @param nOfs
   *        Offset to start copying. Must be &ge; 0.
   * @param nLen
   *        Number of chars to take. Must be &ge; 0.
   */
  void appendData (@Nonnull char [] aChars, @Nonnegative int nOfs, @Nonnegative int nLen);

  /**
   * Append a character to the string.
   *
   * @param cChar
   *        The character to append
   */
  void appendData (char cChar);

  /**
   * Add characters to the beginning of the current data.
   *
   * @param sData
   *        The characters to be added at the front.
   */
  void prependData (@Nullable CharSequence sData);

  /**
   * Add characters to the beginning of the current data.
   *
   * @param aChars
   *        Base character array. May not be <code>null</code>.
   * @param nOfs
   *        Offset to start copying. Must be &ge; 0.
   * @param nLen
   *        Number of chars to take. Must be &ge; 0.
   */
  void prependData (@Nonnull char [] aChars, @Nonnegative int nOfs, @Nonnegative int nLen);

  /**
   * Add a character to the beginning of the current data.
   *
   * @param cChar
   *        The character to preprend
   */
  void prependData (char cChar);
}

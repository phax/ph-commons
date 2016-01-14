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
import javax.annotation.Nullable;

/**
 * Base interface for the tri state.
 *
 * @author Philip Helger
 */
public interface ITriState
{
  /**
   * @return <code>true</code> if the value is <code>true</code>.
   */
  boolean isTrue ();

  /**
   * @return <code>true</code> if the value is <code>false</code>
   */
  boolean isFalse ();

  /**
   * @return <code>true</code> if the value is not undefined (if it is either
   *         <code>true</code> or <code>false</code>)
   */
  boolean isDefined ();

  /**
   * @return <code>true</code> if the value is undefined (if it is neither
   *         <code>true</code> nor <code>false</code>)
   */
  default boolean isUndefined ()
  {
    return !isDefined ();
  }

  /**
   * Convert the tri state value into a boolean value. If it is undefined, an
   * {@link IllegalStateException} is thrown.
   *
   * @return <code>true</code> if {@link #isTrue()} is true, <code>false</code>
   *         if {@link #isFalse()} is true, or an exception otherwise!
   * @throws IllegalStateException
   *         If this is undefined
   * @see #getAsBooleanValue(boolean)
   */
  boolean getAsBooleanValue () throws IllegalStateException;

  /**
   * Convert the tri state value into a boolean value, depending on what
   * "undefined" means.
   *
   * @param bUndefinedValue
   *        The boolean representation of undefined.
   * @return <code>true</code> if {@link #isTrue()} is true, <code>false</code>
   *         if {@link #isFalse()} is true, or otherwise the passed parameter!
   * @see #getAsBooleanValue()
   */
  boolean getAsBooleanValue (boolean bUndefinedValue);

  /**
   * Convert the tri state value into a {@link Boolean} value, depending on what
   * "undefined" means.
   *
   * @return {@link Boolean#TRUE} if {@link #isTrue()} is <code>true</code>,
   *         {@link Boolean#FALSE} if {@link #isFalse()} is <code>true</code>,
   *         or <code>null</code>!
   */
  @Nullable
  Boolean getAsBooleanObj ();

  /**
   * Convert the tri state value into a {@link Boolean} value, depending on what
   * "undefined" means.
   *
   * @param bUndefinedValue
   *        The {@link boolean} representation of undefined.
   * @return {@link Boolean#TRUE} if {@link #isTrue()} is true,
   *         {@link Boolean#FALSE} if {@link #isFalse()} is true, or otherwise
   *         the passed parameter!
   */
  @Nonnull
  default Boolean getAsBooleanObj (final boolean bUndefinedValue)
  {
    return getAsBooleanObj (Boolean.valueOf (bUndefinedValue));
  }

  /**
   * Convert the tri state value into a {@link Boolean} value, depending on what
   * "undefined" means.
   *
   * @param aUndefinedValue
   *        The {@link Boolean} representation of undefined.
   * @return {@link Boolean#TRUE} if {@link #isTrue()} is true,
   *         {@link Boolean#FALSE} if {@link #isFalse()} is true, or otherwise
   *         the passed parameter!
   */
  @Nullable
  Boolean getAsBooleanObj (@Nullable Boolean aUndefinedValue);
}

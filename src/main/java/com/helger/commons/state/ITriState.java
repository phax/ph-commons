/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
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
package com.helger.commons.state;

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
  boolean isUndefined ();

  /**
   * Convert the tri state value into a boolean value, depending on what
   * "undefined" means.
   * 
   * @param bUndefinedValue
   *        The boolean representation of undefined.
   * @return <code>true</code> if {@link #isTrue()} is true, <code>false</code>
   *         if {@link #isFalse()} is true, or otherwise the passed parameter!
   */
  boolean getAsBooleanValue (boolean bUndefinedValue);

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

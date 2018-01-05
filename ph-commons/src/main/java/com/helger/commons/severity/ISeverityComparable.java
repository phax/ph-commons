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
package com.helger.commons.severity;

import java.io.Serializable;

import javax.annotation.Nonnull;

/**
 * Interface for comparable objects based on their severity.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        The implementation type
 */
public interface ISeverityComparable <IMPLTYPE extends ISeverityComparable <IMPLTYPE>> extends Serializable
{
  /**
   * Check if this object is of the same level (= equal important) than the
   * passed object.
   *
   * @param aOther
   *        The object to compare to.
   * @return <code>true</code> if this object is equally important than the
   *         passed object!
   */
  boolean isEQ (@Nonnull IMPLTYPE aOther);

  /**
   * Check if this object is of a different level (= different importance) than
   * the passed object.
   *
   * @param aOther
   *        The object to compare to.
   * @return <code>true</code> if this object is not equally important than the
   *         passed object!
   * @since 8.6.5
   */
  default boolean isNE (@Nonnull final IMPLTYPE aOther)
  {
    return !isEQ (aOther);
  }

  /**
   * Check if this object is of lower level (= less important) than the passed
   * object.
   *
   * @param aOther
   *        The object to compare to.
   * @return <code>true</code> if this object is less important than the passed
   *         object!
   */
  boolean isLT (@Nonnull IMPLTYPE aOther);

  /**
   * Check if this object is of equal or lower level (= equally or less
   * important) than the passed object.
   *
   * @param aOther
   *        The object to compare to.
   * @return <code>true</code> if this object is equally or less important than
   *         the passed object!
   */
  boolean isLE (@Nonnull IMPLTYPE aOther);

  /**
   * Check if this object is of higher level (= more important) than the passed
   * object.
   *
   * @param aOther
   *        The object to compare to.
   * @return <code>true</code> if this object is more important than the passed
   *         object!
   */
  boolean isGT (@Nonnull IMPLTYPE aOther);

  /**
   * Check if this object is of equal or higher level (= equally or more
   * important) than the passed object.
   *
   * @param aOther
   *        The object to compare to.
   * @return <code>true</code> if this object is equally or more important than
   *         the passed object!
   */
  boolean isGE (@Nonnull IMPLTYPE aOther);
}

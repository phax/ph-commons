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
package com.helger.commons.error;

import javax.annotation.Nonnull;

/**
 * Interface for comparable objects based on their severity.
 *
 * @author Philip Helger
 * @param <THISTYPE>
 *        The implementation type
 */
public interface ISeverityComparable <THISTYPE extends ISeverityComparable <THISTYPE>>
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
  boolean isEqualSevereThan (@Nonnull THISTYPE aOther);

  /**
   * Check if this object is of lower level (= less important) than the passed
   * object.
   *
   * @param aOther
   *        The object to compare to.
   * @return <code>true</code> if this object is less important than the passed
   *         object!
   */
  boolean isLessSevereThan (@Nonnull THISTYPE aOther);

  /**
   * Check if this object is of equal or lower level (= equally or less
   * important) than the passed object.
   *
   * @param aOther
   *        The object to compare to.
   * @return <code>true</code> if this object is equally or less important than
   *         the passed object!
   */
  boolean isLessOrEqualSevereThan (@Nonnull THISTYPE aOther);

  /**
   * Check if this object is of higher level (= more important) than the passed
   * object.
   *
   * @param aOther
   *        The object to compare to.
   * @return <code>true</code> if this object is more important than the passed
   *         object!
   */
  boolean isMoreSevereThan (@Nonnull THISTYPE aOther);

  /**
   * Check if this object is of equal or higher level (= equally or more
   * important) than the passed object.
   *
   * @param aOther
   *        The object to compare to.
   * @return <code>true</code> if this object is equally or more important than
   *         the passed object!
   */
  boolean isMoreOrEqualSevereThan (@Nonnull THISTYPE aOther);
}

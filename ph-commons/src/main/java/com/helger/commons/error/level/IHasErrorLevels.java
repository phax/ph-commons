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
package com.helger.commons.error.level;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.collection.impl.ICommonsIterable;

/**
 * Interface representing an object having multiple objects with an error level
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IHasErrorLevels <IMPLTYPE extends IHasErrorLevel> extends ICommonsIterable <IMPLTYPE>
{
  /**
   * Check if this group contains only success messages. If no item is present,
   * <code>false</code> is returned, since no success message is contained. If
   * you want to check, if the list is empty or contains only success messages,
   * use {@link #containsNoFailure()} instead.
   *
   * @return <code>true</code> if at least one item is present, and if all items
   *         have the error level success, <code>false</code> otherwise.
   * @see #containsNoFailure()
   */
  default boolean containsOnlySuccess ()
  {
    return containsOnly (IHasErrorLevel::isSuccess);
  }

  /**
   * Check if this group contains at least one success message.
   *
   * @return <code>true</code> if at least one success item is present,
   *         <code>false</code> otherwise.
   */
  default boolean containsAtLeastOneSuccess ()
  {
    return containsAny (IHasErrorLevel::isSuccess);
  }

  /**
   * Check if this group contains no success message. This is also true, if the
   * list is empty!
   *
   * @return <code>true</code> if no success item is present, <code>false</code>
   *         otherwise.
   */
  default boolean containsNoSuccess ()
  {
    return containsNone (IHasErrorLevel::isSuccess);
  }

  /**
   * @return The number of contained success messages. Always &ge; 0.
   */
  @Nonnegative
  default int getSuccessCount ()
  {
    return getCount (IHasErrorLevel::isSuccess);
  }

  /**
   * Check if this group contains only failure messages. If no item is present,
   * <code>false</code> is returned. All error levels except
   * {@link EErrorLevel#SUCCESS} are considered to be a failure!
   *
   * @return <code>true</code> if at least one item is present, and if all items
   *         have an error level indicating failure, <code>false</code>
   *         otherwise.
   */
  default boolean containsOnlyFailure ()
  {
    return containsOnly (IHasErrorLevel::isFailure);
  }

  /**
   * Check if this group contains at least one failure message. All error levels
   * except {@link EErrorLevel#SUCCESS} are considered to be a failure!
   *
   * @return <code>true</code> if at least one failure item is present,
   *         <code>false</code> otherwise.
   */
  default boolean containsAtLeastOneFailure ()
  {
    return containsAny (IHasErrorLevel::isFailure);
  }

  /**
   * Check if this group contains no failure message. All error levels except
   * {@link EErrorLevel#SUCCESS} are considered to be a failure!
   *
   * @return <code>true</code> if no failure item is present, <code>false</code>
   *         otherwise.
   */
  default boolean containsNoFailure ()
  {
    return containsNone (IHasErrorLevel::isFailure);
  }

  /**
   * @return The number of contained failure messages. Always &ge; 0.
   */
  @Nonnegative
  default int getFailureCount ()
  {
    return getCount (IHasErrorLevel::isFailure);
  }

  /**
   * Check if this group contains only error or fatal error messages. If no item
   * is present, <code>false</code> is returned. All error levels &ge;
   * {@link EErrorLevel#ERROR} are considered to be an error!
   *
   * @return <code>true</code> if at least one item is present, and if all items
   *         have an error level indicating error or fatal error,
   *         <code>false</code> otherwise.
   */
  default boolean containsOnlyError ()
  {
    return containsOnly (IHasErrorLevel::isError);
  }

  /**
   * Check if this group contains at least one error or fatal error message. All
   * error levels &ge; {@link EErrorLevel#ERROR} are considered to be an error!
   *
   * @return <code>true</code> if at least one error or fatal error item is
   *         present, <code>false</code> otherwise.
   */
  default boolean containsAtLeastOneError ()
  {
    return containsAny (IHasErrorLevel::isError);
  }

  /**
   * Check if this group contains no error or fatal error message. All error
   * levels &ge; {@link EErrorLevel#ERROR} are considered to be an error!
   *
   * @return <code>true</code> if no error or fatal error item is present,
   *         <code>false</code> otherwise.
   */
  default boolean containsNoError ()
  {
    return containsNone (IHasErrorLevel::isError);
  }

  /**
   * @return The number of contained error messages. Always &ge; 0. All error
   *         levels &ge; {@link EErrorLevel#ERROR} are considered to be an
   *         error!
   */
  @Nonnegative
  default int getErrorCount ()
  {
    return getCount (IHasErrorLevel::isError);
  }

  /**
   * @return <code>true</code> if at least 1 item of level warning or at least 1
   *         item of level error is contained.
   */
  default boolean containsAtLeastOneWarningOrError ()
  {
    return containsAny (x -> x.getErrorLevel ().isGE (EErrorLevel.WARN));
  }

  /**
   * Get the most severe error level within this object. The severity is defined
   * by {@link EErrorLevel}'s severity model.
   *
   * @return {@link EErrorLevel#SUCCESS} if no error is contained, the most
   *         severe contained error level otherwise.
   */
  @Nonnull
  default IErrorLevel getMostSevereErrorLevel ()
  {
    IErrorLevel ret = EErrorLevel.LOWEST;
    for (final IMPLTYPE aItem : this)
    {
      final IErrorLevel aCur = aItem.getErrorLevel ();
      if (aCur.isGT (ret))
      {
        ret = aCur;
        if (ret.isHighest ())
          break;
      }
    }
    return ret;
  }
}

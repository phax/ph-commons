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
package com.helger.commons.compare;

import java.io.Serializable;

import javax.annotation.Nonnull;

/**
 * A special interface extension to the {@link Comparable} interface that makes
 * objects serializable and adds some default comparison methods.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The data type to be compared
 * @since 8.6.3
 */
public interface IComparable <DATATYPE> extends Comparable <DATATYPE>, Serializable
{
  /**
   * @param aOther
   *        value to compare to
   * @return <code>true</code> if this value is greater than the provided
   *         values, <code>false</code> if not.
   * @since 8.6.5
   */
  default boolean isGT (@Nonnull final DATATYPE aOther)
  {
    return compareTo (aOther) > 0;
  }

  /**
   * @param aOther
   *        value to compare to
   * @return <code>true</code> if this value is greater or equal than the
   *         provided values, <code>false</code> if not.
   * @since 8.6.5
   */
  default boolean isGE (@Nonnull final DATATYPE aOther)
  {
    return compareTo (aOther) >= 0;
  }

  /**
   * @param aOther
   *        value to compare to
   * @return <code>true</code> if this value is lower than the provided values,
   *         <code>false</code> if not.
   * @since 8.6.5
   */
  default boolean isLT (@Nonnull final DATATYPE aOther)
  {
    return compareTo (aOther) < 0;
  }

  /**
   * @param aOther
   *        value to compare to
   * @return <code>true</code> if this value is lower or equal than the provided
   *         values, <code>false</code> if not.
   * @since 8.6.5
   */
  default boolean isLE (@Nonnull final DATATYPE aOther)
  {
    return compareTo (aOther) <= 0;
  }

  /**
   * @param aOther
   *        value to compare to
   * @return <code>true</code> if the values are equal, <code>false</code>
   *         otherwise.
   * @since 8.6.5
   */
  default boolean isEQ (@Nonnull final DATATYPE aOther)
  {
    return compareTo (aOther) == 0;
  }

  /**
   * @param aOther
   *        value to compare to
   * @return <code>true</code> if the values are not equal, <code>false</code>
   *         otherwise.
   * @since 8.6.5
   */
  default boolean isNE (@Nonnull final DATATYPE aOther)
  {
    return compareTo (aOther) != 0;
  }
}

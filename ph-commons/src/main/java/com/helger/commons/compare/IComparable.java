/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
 */
public interface IComparable <DATATYPE> extends Comparable <DATATYPE>, Serializable
{
  default boolean isGreaterThan (@Nonnull final DATATYPE aOther)
  {
    return compareTo (aOther) > 0;
  }

  default boolean isGreaterOrEqualThan (@Nonnull final DATATYPE aOther)
  {
    return compareTo (aOther) >= 0;
  }

  default boolean isLowerThan (@Nonnull final DATATYPE aOther)
  {
    return compareTo (aOther) < 0;
  }

  default boolean isLowerOrEqualThan (@Nonnull final DATATYPE aOther)
  {
    return compareTo (aOther) <= 0;
  }

  default boolean isEqualTo (@Nonnull final DATATYPE aOther)
  {
    return compareTo (aOther) == 0;
  }
}

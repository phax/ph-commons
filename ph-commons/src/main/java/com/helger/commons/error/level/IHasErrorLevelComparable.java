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

import javax.annotation.Nonnull;

import com.helger.commons.severity.ISeverityComparable;
import com.helger.commons.traits.IGenericImplTrait;

/**
 * Base interface for objects that have a severity based on {@link IErrorLevel}.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IHasErrorLevelComparable <IMPLTYPE extends IHasErrorLevelComparable <IMPLTYPE>> extends
                                          IHasErrorLevel,
                                          ISeverityComparable <IMPLTYPE>,
                                          IGenericImplTrait <IMPLTYPE>
{
  default int compareTo (@Nonnull final IMPLTYPE aOther)
  {
    return getErrorLevel ().compareTo (aOther.getErrorLevel ());
  }

  default boolean isEQ (@Nonnull final IMPLTYPE aOther)
  {
    return getErrorLevel ().isEQ (aOther.getErrorLevel ());
  }

  @Override
  default boolean isNE (@Nonnull final IMPLTYPE aOther)
  {
    return getErrorLevel ().isNE (aOther.getErrorLevel ());
  }

  default boolean isLT (@Nonnull final IMPLTYPE aOther)
  {
    return getErrorLevel ().isLT (aOther.getErrorLevel ());
  }

  default boolean isLE (@Nonnull final IMPLTYPE aOther)
  {
    return getErrorLevel ().isLE (aOther.getErrorLevel ());
  }

  default boolean isGT (@Nonnull final IMPLTYPE aOther)
  {
    return getErrorLevel ().isGT (aOther.getErrorLevel ());
  }

  default boolean isGE (@Nonnull final IMPLTYPE aOther)
  {
    return getErrorLevel ().isGE (aOther.getErrorLevel ());
  }
}

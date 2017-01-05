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
package com.helger.commons.error.level;

import java.io.Serializable;

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
                                          IGenericImplTrait <IMPLTYPE>,
                                          Serializable
{
  default boolean isEqualSevereThan (@Nonnull final IMPLTYPE aOther)
  {
    return getErrorLevel ().isEqualSevereThan (aOther.getErrorLevel ());
  }

  default boolean isLessSevereThan (@Nonnull final IMPLTYPE aOther)
  {
    return getErrorLevel ().isLessSevereThan (aOther.getErrorLevel ());
  }

  default boolean isLessOrEqualSevereThan (@Nonnull final IMPLTYPE aOther)
  {
    return getErrorLevel ().isLessOrEqualSevereThan (aOther.getErrorLevel ());
  }

  default boolean isMoreSevereThan (@Nonnull final IMPLTYPE aOther)
  {
    return getErrorLevel ().isMoreSevereThan (aOther.getErrorLevel ());
  }

  default boolean isMoreOrEqualSevereThan (@Nonnull final IMPLTYPE aOther)
  {
    return getErrorLevel ().isMoreOrEqualSevereThan (aOther.getErrorLevel ());
  }
}

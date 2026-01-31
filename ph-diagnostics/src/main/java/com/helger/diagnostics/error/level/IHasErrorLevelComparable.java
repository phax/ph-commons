/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.diagnostics.error.level;

import org.jspecify.annotations.NonNull;

import com.helger.base.trait.IGenericImplTrait;
import com.helger.diagnostics.severity.ISeverityComparable;

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
  default int compareTo (@NonNull final IMPLTYPE aOther)
  {
    return getErrorLevel ().compareTo (aOther.getErrorLevel ());
  }

  default boolean isEQ (@NonNull final IMPLTYPE aOther)
  {
    return getErrorLevel ().isEQ (aOther.getErrorLevel ());
  }

  @Override
  default boolean isNE (@NonNull final IMPLTYPE aOther)
  {
    return getErrorLevel ().isNE (aOther.getErrorLevel ());
  }

  default boolean isLT (@NonNull final IMPLTYPE aOther)
  {
    return getErrorLevel ().isLT (aOther.getErrorLevel ());
  }

  default boolean isLE (@NonNull final IMPLTYPE aOther)
  {
    return getErrorLevel ().isLE (aOther.getErrorLevel ());
  }

  default boolean isGT (@NonNull final IMPLTYPE aOther)
  {
    return getErrorLevel ().isGT (aOther.getErrorLevel ());
  }

  default boolean isGE (@NonNull final IMPLTYPE aOther)
  {
    return getErrorLevel ().isGE (aOther.getErrorLevel ());
  }
}

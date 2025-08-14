/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.base.traits;

import com.helger.base.reflection.GenericReflection;

import jakarta.annotation.Nonnull;

/**
 * A trait to convert this to a generic implementation.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        The real implementation type.
 */
public interface IGenericImplTrait <IMPLTYPE extends IGenericImplTrait <IMPLTYPE>>
{
  /**
   * @return <code>this</code> casted to <code>IMPLTYPE</code>. Never
   *         <code>null</code>.
   */
  @Nonnull
  default IMPLTYPE thisAsT ()
  {
    return GenericReflection.uncheckedCast (this);
  }
}

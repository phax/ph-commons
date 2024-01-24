/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.commons.typeconvert;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Special interface that is used to convert between values of different types.
 *
 * @author Philip Helger
 * @param <SRC>
 *        source type
 * @param <DST>
 *        destination type
 */
@FunctionalInterface
public interface ITypeConverter <SRC, DST> extends Function <SRC, DST>
{
  /**
   * Convert the passed source object to the destination type.
   *
   * @param aSource
   *        The source object to be converted. Cannot be <code>null</code>
   *        because the type converter already filters <code>null</code> values!
   * @return The converted value. May be <code>null</code>.
   */
  @Nullable
  DST apply (@Nonnull SRC aSource);
}

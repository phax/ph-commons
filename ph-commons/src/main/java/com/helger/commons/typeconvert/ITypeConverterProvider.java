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
package com.helger.commons.typeconvert;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Find the correct type converter provider from a source class to a destination
 * class.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface ITypeConverterProvider
{
  /**
   * Find a type converter from the source class to the destination class.
   *
   * @param aSrcClass
   *        The source class. May not be <code>null</code>.
   * @param aDstClass
   *        The destination class. May not be <code>null</code>.
   * @return <code>null</code> if no such type converter was found
   */
  @Nullable
  ITypeConverter <Object, Object> getTypeConverter (@Nonnull Class <?> aSrcClass, @Nonnull Class <?> aDstClass);
}

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
package com.helger.typeconvert;

import org.jspecify.annotations.NonNull;

import com.helger.base.state.EContinue;

/**
 * A callback interface that is used in several places of the type conversion
 * engine.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface ITypeConverterCallback
{
  /**
   * Callback method invoked for each type converter registration.
   *
   * @param aSrcClass
   *        The source class of the converter. Never <code>null</code>.
   * @param aDstClass
   *        The destination class of the converter. Never <code>null</code>.
   * @param aConverter
   *        The type converter itself. Never <code>null</code>.
   * @return {@link EContinue#CONTINUE} to continue iteration,
   *         {@link EContinue#BREAK} to stop.
   */
  @NonNull
  EContinue call (@NonNull Class <?> aSrcClass, @NonNull Class <?> aDstClass, @NonNull ITypeConverter <?, ?> aConverter);
}

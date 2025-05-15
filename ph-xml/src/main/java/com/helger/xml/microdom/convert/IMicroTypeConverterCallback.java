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
package com.helger.xml.microdom.convert;

import com.helger.annotation.Nonnull;

import com.helger.commons.state.EContinue;

/**
 * A callback interface that is used to iterate all available micro type
 * converters.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IMicroTypeConverterCallback
{
  /**
   * Invoked for each converter.
   *
   * @param aClass
   *        The class for which the converter was registered.
   * @param aConverter
   *        The main converter object. Never <code>null</code>.
   * @return {@link EContinue#CONTINUE} to continue iteration,
   *         {@link EContinue#BREAK} to stop iteration.
   */
  @Nonnull
  EContinue call (@Nonnull Class <?> aClass, @Nonnull IMicroTypeConverter <?> aConverter);
}

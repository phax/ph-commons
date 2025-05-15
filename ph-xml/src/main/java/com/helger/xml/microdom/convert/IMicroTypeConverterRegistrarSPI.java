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
import com.helger.annotation.style.IsSPIInterface;

/**
 * SPI interface to be implemented by other modules wishing to register their
 * own micro-type converters.
 *
 * @author Philip Helger
 */
@IsSPIInterface
public interface IMicroTypeConverterRegistrarSPI
{
  /**
   * Register all your dynamic micro type converters in the passed interface
   *
   * @param aRegistry
   *        The registry to register your converters. Never <code>null</code>.
   */
  void registerMicroTypeConverter (@Nonnull IMicroTypeConverterRegistry aRegistry);
}

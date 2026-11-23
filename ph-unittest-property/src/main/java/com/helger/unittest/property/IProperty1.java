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
package com.helger.unittest.property;

import org.jspecify.annotations.Nullable;

/**
 * A property with 1 generated argument(s). It fails by throwing any exception, usually an
 * {@link AssertionError} from a JUnit assertion.
 *
 * @author Philip Helger
 * @since 12.8.0
 * @param <A>
 *        The type of the first argument
 */
@FunctionalInterface
public interface IProperty1 <A>
{
  /**
   * Check the property for the passed argument(s).
   *
   * @param aArg1
   *        The first generated argument
   * @throws Exception
   *         To indicate that the property does not hold
   */
  void check (@Nullable A aArg1) throws Exception;
}

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
 * A property with 4 generated argument(s). It fails by throwing any exception, usually an
 * {@link AssertionError} from a JUnit assertion.
 *
 * @author Philip Helger
 * @param <A>
 *        The type of the first argument
 * @param <B>
 *        The type of the second argument
 * @param <C>
 *        The type of the third argument
 * @param <D>
 *        The type of the fourth argument
 */
@FunctionalInterface
public interface IProperty4 <A, B, C, D>
{
  /**
   * Check the property for the passed argument(s).
   *
   * @param aArg1
   *        The first generated argument
   * @param aArg2
   *        The second generated argument
   * @param aArg3
   *        The third generated argument
   * @param aArg4
   *        The fourth generated argument
   * @throws Exception
   *         To indicate that the property does not hold
   */
  void check (@Nullable A aArg1, @Nullable B aArg2, @Nullable C aArg3, @Nullable D aArg4) throws Exception;
}

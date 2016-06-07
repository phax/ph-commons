/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.function;

import javax.annotation.Nonnull;

import com.helger.commons.state.EContinue;

/**
 * A special Consumer-like interface that allows for stopping the iteration.
 *
 * @author Philip Helger
 * @param <T>
 *        Type to be consumed
 */
public interface IBreakableConsumer <T>
{
  /**
   * Handle the current element
   *
   * @param aElement
   *        Current element.
   * @return {@link EContinue#CONTINUE} to continue or {@link EContinue#BREAK}
   *         to stop iteration.
   */
  @Nonnull
  EContinue accept (T aElement);
}

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
package com.helger.commons.concurrent;

import java.util.concurrent.ExecutorService;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Factory for creating {@link ExecutorService} instances.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IExecutorServiceFactory
{
  /**
   * Get an {@link ExecutorService} for the given number of parallel tasks. It
   * is up to the implementation to interpret the value or not. The number of
   * parallel tasks can therefore considered a hint to the implementation.
   *
   * @param nParallelTasks
   *        The number of parallel tasks to perform. Needs to be &gt; 0.
   * @return A non-<code>null</code> {@link ExecutorService} object.
   */
  @Nonnull
  ExecutorService getExecutorService (@Nonnegative int nParallelTasks);
}

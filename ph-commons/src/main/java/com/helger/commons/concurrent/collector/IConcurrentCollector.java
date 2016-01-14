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
package com.helger.commons.concurrent.collector;

import javax.annotation.Nonnegative;

/**
 * Base interface for a concurrent queue worker.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type of objects to be queued
 */
public interface IConcurrentCollector <DATATYPE>
{
  /**
   * @return <code>true</code> if the queue is empty, <code>false</code>
   *         otherwise.
   */
  boolean isQueueEmpty ();

  /**
   * @return The number of objects currently in the queue.
   */
  @Nonnegative
  int getQueueLength ();

  /**
   * Check if this collector is already stopped.
   *
   * @return <code>true</code> if the collector is stopped, <code>false</code>
   *         otherwise.
   */
  boolean isStopped ();
}

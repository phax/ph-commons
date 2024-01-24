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
package com.helger.commons.concurrent.collector;

import javax.annotation.Nonnull;

import com.helger.commons.state.ESuccess;

/**
 * Base interface for a concurrent queue worker. It asynchronously collects
 * objects to handle (via the {@link #queueObject(Object)} method).
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type of objects to be queued
 */
public interface IMutableConcurrentCollector <DATATYPE> extends IConcurrentCollector
{
  /**
   * Submit an object to the queue.
   *
   * @param aObject
   *        The object to submit. May not be <code>null</code>.
   * @return {@link ESuccess}
   * @throws IllegalStateException
   *         If the queue is already stopped
   */
  @Nonnull
  ESuccess queueObject (@Nonnull DATATYPE aObject);

  /**
   * Stop taking new objects in the collector. Returns directly and does not
   * wait until the processing finished.
   *
   * @return {@link ESuccess}
   */
  @Nonnull
  ESuccess stopQueuingNewObjects ();

  /**
   * This method starts the collector by taking objects from the internal queue.
   * So this method blocks and must be invoked from a separate thread. This
   * method runs until {@link #stopQueuingNewObjects()} is new called and the
   * queue is empty.
   */
  void collect ();
}

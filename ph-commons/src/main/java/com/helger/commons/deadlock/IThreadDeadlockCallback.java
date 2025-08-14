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
package com.helger.commons.deadlock;

import com.helger.annotation.Nonempty;
import com.helger.base.callback.ICallback;

import jakarta.annotation.Nonnull;

/**
 * This is called whenever a problem with threads is detected.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IThreadDeadlockCallback extends ICallback
{
  /**
   * Callback to be invoked on a deadlock
   *
   * @param aDeadlockedThreads
   *        Information on the dead-locked threads. Neither <code>null</code>
   *        nor empty.
   */
  void onDeadlockDetected (@Nonnull @Nonempty ThreadDeadlockInfo [] aDeadlockedThreads);
}

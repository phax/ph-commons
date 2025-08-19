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
package com.helger.base.cleanup;

import jakarta.annotation.Nonnull;

/**
 * Callback interface for registering new type converters.
 *
 * @author Philip Helger
 */
public interface ICleanUpRegistry
{
  int PRIORITY_MIN = Integer.MIN_VALUE;
  int PRIORITY_DEFAULT = 0;
  int PRIORITY_MAX = Integer.MAX_VALUE;

  /**
   * Register a cleanup action.
   *
   * @param nPriority
   *        Priority - the higher the earlier it is called
   * @param aRunnable
   *        The runnable to be executed. May not be <code>null</code>.
   */
  void registerCleanup (int nPriority, @Nonnull Runnable aRunnable);
}

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

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.lang.EnumHelper;
import com.helger.base.system.SystemProperties;

/**
 * The sole purpose of this class to clear all caches, that reside in this library.
 *
 * @author Philip Helger
 */
@Immutable
public final class BaseCleanup
{
  @PresentForCodeCoverage
  private static final BaseCleanup INSTANCE = new BaseCleanup ();

  private BaseCleanup ()
  {}

  /**
   * Cleanup all custom caches contained in this library. Loaded SPI implementations are not
   * affected by this method!
   */
  public static void cleanup ()
  {
    EnumHelper.clearCache ();
    SystemProperties.clearWarnedPropertyNames ();
  }
}

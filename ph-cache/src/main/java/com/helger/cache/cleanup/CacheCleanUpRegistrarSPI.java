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
package com.helger.cache.cleanup;

import com.helger.annotation.style.IsSPIImplementation;
import com.helger.base.cleanup.ICleanUpRegistrarSPI;
import com.helger.base.cleanup.ICleanUpRegistry;
import com.helger.cache.regex.RegExCache;

import jakarta.annotation.Nonnull;

/**
 * The sole purpose of this class to clear all caches, that reside in this library.
 *
 * @author Philip Helger
 */
@IsSPIImplementation
public final class CacheCleanUpRegistrarSPI implements ICleanUpRegistrarSPI
{
  public void registerCleanUpAction (@Nonnull final ICleanUpRegistry aRegistry)
  {
    aRegistry.registerCleanup (ICleanUpRegistry.PRIORITY_MIN + 200, () -> {
      if (RegExCache.isInstantiated ())
        RegExCache.getInstance ().clearCache ();
    });
  }
}

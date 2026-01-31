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
package com.helger.base.cleanup;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.style.IsSPIImplementation;
import com.helger.base.lang.EnumHelper;
import com.helger.base.system.SystemProperties;
import com.helger.base.thirdparty.ThirdPartyModuleRegistry;

@IsSPIImplementation
public final class BaseCleanUpRegistrarSPI implements ICleanUpRegistrarSPI
{
  public void registerCleanUpAction (@NonNull final ICleanUpRegistry aRegistry)
  {
    aRegistry.registerCleanup (ICleanUpRegistry.PRIORITY_MIN, () -> {
      if (ThirdPartyModuleRegistry.isInstantiated ())
        ThirdPartyModuleRegistry.getInstance ().reinitialize ();

      EnumHelper.clearCache ();
      SystemProperties.clearWarnedPropertyNames ();

      // Re-initialize itself ;-)
      CleanUpRegistry.getInstance ().reinitialize ();
    });
  }
}

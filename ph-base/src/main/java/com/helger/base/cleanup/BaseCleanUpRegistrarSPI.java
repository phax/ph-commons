package com.helger.base.cleanup;

import com.helger.annotation.style.IsSPIImplementation;
import com.helger.base.lang.EnumHelper;
import com.helger.base.system.SystemProperties;

import jakarta.annotation.Nonnull;

@IsSPIImplementation
public final class BaseCleanUpRegistrarSPI implements ICleanUpRegistrarSPI
{
  public void registerCleanUpAction (@Nonnull final ICleanUpRegistry aRegistry)
  {
    aRegistry.registerCleanup (ICleanUpRegistry.PRIORITY_MIN, () -> {
      EnumHelper.clearCache ();
      SystemProperties.clearWarnedPropertyNames ();

      // Re-initialize itself ;-)
      CleanUpRegistry.getInstance ().reinitialize ();
    });
  }
}

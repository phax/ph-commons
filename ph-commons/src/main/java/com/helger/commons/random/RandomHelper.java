/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
package com.helger.commons.random;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.system.SystemProperties;

/**
 * A helper class that centrally works around the issue of slow
 * {@link SecureRandom} implementations on certain platforms in certain
 * constellations (usually on Linux with JDK 6). This class centrally allows to
 * decide between {@link VerySecureRandom} and regular {@link Random}.
 *
 * @author Philip Helger
 * @deprecated Just don't use. Use {@link Random}, {@link ThreadLocalRandom} or
 *             {@link VerySecureRandom} depending on your needs.
 */
@Immutable
@Deprecated
public final class RandomHelper
{
  private static final Logger LOGGER = LoggerFactory.getLogger (RandomHelper.class);
  private static final AtomicBoolean s_aUseSecureRandom;

  static
  {
    // Since v9.3.2
    final String sPropValue = SystemProperties.getPropertyValueOrNull ("ph.disable-securerandom");
    final boolean bUseSecureRandom = sPropValue == null || !"true".equalsIgnoreCase (sPropValue);
    if (sPropValue != null)
    {
      // Log only, if a system property is present
      if (LOGGER.isInfoEnabled ())
        LOGGER.info ("Usage of SecureRandom inside ph-* libraries is by default " + (bUseSecureRandom ? "enabled" : "disabled"));
    }
    s_aUseSecureRandom = new AtomicBoolean (bUseSecureRandom);
  }

  private RandomHelper ()
  {}

  public static void setUseSecureRandom (final boolean bUseSecureRandom)
  {
    s_aUseSecureRandom.set (bUseSecureRandom);
    if (LOGGER.isInfoEnabled ())
      LOGGER.info ("Usage of SecureRandom inside ph-* libraries is now " + (bUseSecureRandom ? "enabled" : "disabled"));
  }

  public static boolean isUseSecureRandom ()
  {
    return s_aUseSecureRandom.get ();
  }

  /**
   * @return Either a {@link SecureRandom} (if enabled) or a regular
   *         {@link Random}. Never <code>null</code>.
   * @see #isUseSecureRandom()
   */
  @Nonnull
  public static Random getRandom ()
  {
    if (isUseSecureRandom ())
      return VerySecureRandom.getInstance ();
    return new Random ();
  }

  /**
   * @return Either a {@link SecureRandom} (if enabled) or <code>null</code>.
   * @see #isUseSecureRandom()
   */
  @Nullable
  public static SecureRandom getSecureRandom ()
  {
    if (isUseSecureRandom ())
      return VerySecureRandom.getInstance ();
    return null;
  }
}

/*
 * Original copyright by Apache Software Foundation
 * Copyright (C) 2017-2026 Philip Helger (www.helger.com)
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
package com.helger.bc;

import java.security.Provider;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;

/**
 * One and only BouncyCastle (BC) Provider Provider
 *
 * @author Philip Helger
 */
@Immutable
public final class PBCProvider
{
  /**
   * Special provider name for the non-FIPS version of BouncyCastle. Certain
   * APIs take the provider name instead of the object. The constant is here for
   * the sake of completeness only.
   */
  public static final String PROVIDER_NAME_BC = BouncyCastleProvider.PROVIDER_NAME;

  /**
   * Special provider name for the FIPS version of BouncyCastle. As there is no
   * Maven artefact, the constant is here for the sake of completeness only.
   */
  public static final String PROVIDER_NAME_BC_FIPS = "BCFIPS";

  private static final Logger LOGGER = LoggerFactory.getLogger (PBCProvider.class);

  // Singleton instance
  private static final Provider PROVIDER_BC;

  static
  {
    Provider aProvider = Security.getProvider (PROVIDER_NAME_BC);
    if (aProvider == null)
    {
      // Create and add a new one
      aProvider = new BouncyCastleProvider ();
      Security.addProvider (aProvider);
      LOGGER.info ("Just added the BouncyCastleProvider to the list of Security providers");
    }
    else
    {
      // Check if existing one is from BC
      if (!(aProvider instanceof BouncyCastleProvider))
        LOGGER.warn ("Security provider '" +
                     PROVIDER_NAME_BC +
                     "' is not of type org.bouncycastle.jce.provider.BouncyCastleProvider but it is a '" +
                     aProvider.getClass ().getName () +
                     "'");
    }
    PROVIDER_BC = aProvider;
  }

  @PresentForCodeCoverage
  private static final PBCProvider INSTANCE = new PBCProvider ();

  private PBCProvider ()
  {}

  /**
   * @return The non-<code>null</code> BouncyCastleProvider instance. Never
   *         <code>null</code>.
   */
  @NonNull
  public static Provider getProvider ()
  {
    return PROVIDER_BC;
  }
}

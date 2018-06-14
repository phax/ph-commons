/**
 * Original copyright by Apache Software Foundation
 * Copyright (C) 2017-2018 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.exception.InitializationException;

/**
 * One and only BouncyCastle (BC) Provider Provider
 *
 * @author Philip Helger
 */
@Immutable
public final class PBCProvider
{
  // Singleton instance
  private static final Provider PROVIDER;

  static
  {
    Provider aProvider = Security.getProvider (BouncyCastleProvider.PROVIDER_NAME);
    if (aProvider == null)
    {
      // Create and add a new one
      aProvider = new BouncyCastleProvider ();
      Security.addProvider (aProvider);
    }
    else
    {
      // Check if existing one is from BC
      if (!(aProvider instanceof BouncyCastleProvider))
        throw new InitializationException ("Invalid BouncyCastleProvider present!");
    }
    PROVIDER = aProvider;
  }

  @PresentForCodeCoverage
  private static final PBCProvider s_aInstance = new PBCProvider ();

  private PBCProvider ()
  {}

  /**
   * @return The non-<code>null</code> BouncyCastleProvider instance
   */
  @Nonnull
  public static Provider getProvider ()
  {
    return PROVIDER;
  }
}

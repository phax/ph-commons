/**
 * Copyright (C) 2015-2017 difi (www.difi.no)
 * Copyright (C) 2018 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * This Source Code Form is subject to the terms of the
 * Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */
package com.helger.bc;

import java.security.Provider;
import java.security.Security;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * One and only BouncyCastle (BC) Provider Provider
 *
 * @author Philip Helger
 */
@Immutable
public final class PBCProvider
{
  private static final Provider PROVIDER;

  static
  {
    Provider aProvider = Security.getProvider (BouncyCastleProvider.PROVIDER_NAME);
    if (aProvider != null)
    {
      // Provider already present
      PROVIDER = aProvider;
    }
    else
    {
      // Create and add a new one
      PROVIDER = aProvider = new BouncyCastleProvider ();
      Security.addProvider (PROVIDER);
    }
  }

  /**
   * @return The non-<code>null</code> BouncyCastleProvider instance
   */
  @Nonnull
  public static Provider getProvider ()
  {
    return PROVIDER;
  }
}

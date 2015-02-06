/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotations.PresentForCodeCoverage;

/**
 * A secure random generator initialized with another secure random generator.
 *
 * @author Philip Helger
 */
@Immutable
public final class VerySecureRandom
{
  private static final SecureRandom s_aSecureRandom;

  private static SecureRandom _getSecureRandomInstance ()
  {
    SecureRandom aSecureRandom;
    try
    {
      // IBM JCE
      // http://www.ibm.com/developerworks/java/jdk/security/50/secguides/JceDocs/api_users_guide.html
      aSecureRandom = SecureRandom.getInstance ("IBMSecureRandom", "IBMJCE");
    }
    catch (final Throwable t)
    {
      try
      {
        // SUN
        // http://docs.oracle.com/javase/6/docs/technotes/guides/security/crypto/CryptoSpec.html
        // http://docs.oracle.com/javase/7/docs/technotes/guides/security/crypto/CryptoSpec.html
        aSecureRandom = SecureRandom.getInstance ("SHA1PRNG");
      }
      catch (final Throwable t2)
      {
        // Default
        aSecureRandom = new SecureRandom ();
      }
    }
    return aSecureRandom;
  }

  static
  {
    // Initialize SecureRandom
    // This is a lengthy operation, to be done only upon
    // initialization of the application. Especial with Java <= 1.5 this whole
    // block takes more or less forever.
    final SecureRandom aSecureRandom = _getSecureRandomInstance ();

    // Get 128 random bytes
    aSecureRandom.nextBytes (new byte [128]);

    // Create secure number generators with the random seed
    final byte [] aSeed = aSecureRandom.generateSeed (10);

    // Initialize main secure random
    s_aSecureRandom = _getSecureRandomInstance ();
    s_aSecureRandom.setSeed (aSeed);
  }

  @PresentForCodeCoverage
  private static final VerySecureRandom s_aInstance = new VerySecureRandom ();

  private VerySecureRandom ()
  {}

  @Nonnull
  public static SecureRandom getInstance ()
  {
    return s_aSecureRandom;
  }
}

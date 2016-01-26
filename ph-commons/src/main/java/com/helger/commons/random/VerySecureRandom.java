/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.PresentForCodeCoverage;

/**
 * A secure random generator initialized with another secure random generator.
 *
 * @author Philip Helger
 */
@Immutable
public final class VerySecureRandom
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (VerySecureRandom.class);

  private static final SecureRandom s_aSecureRandom;

  /**
   * Create a new {@link SecureRandom} instance. First the IBM secure random is
   * tried, than the SHA1PRNG secure random and finally if the previous ones
   * failed, the default instance is used. In certain circumstances (Linux +
   * some Java version) this initialization takes forever and that's why the
   * debug statements are added.
   *
   * @return A new {@link SecureRandom} instance. Never <code>null</code>.
   */
  @Nonnull
  private static SecureRandom _createSecureRandomInstance ()
  {
    SecureRandom aSecureRandom;
    try
    {
      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug ("Trying to get SecureRandom: IBMSecureRandom, IBMJCE");

      // IBM JCE
      // http://www.ibm.com/developerworks/java/jdk/security/50/secguides/JceDocs/api_users_guide.html
      aSecureRandom = SecureRandom.getInstance ("IBMSecureRandom", "IBMJCE");

      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug ("Using SecureRandom: IBMSecureRandom, IBMJCE");
    }
    catch (final Throwable t)
    {
      try
      {
        if (s_aLogger.isDebugEnabled ())
          s_aLogger.debug ("Trying to get SecureRandom: SHA1PRNG");

        // Oracle
        // http://docs.oracle.com/javase/6/docs/technotes/guides/security/crypto/CryptoSpec.html
        // http://docs.oracle.com/javase/7/docs/technotes/guides/security/crypto/CryptoSpec.html
        // http://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html
        aSecureRandom = SecureRandom.getInstance ("SHA1PRNG");

        if (s_aLogger.isDebugEnabled ())
          s_aLogger.debug ("Using SecureRandom: SHA1PRNG");
      }
      catch (final Throwable t2)
      {
        if (s_aLogger.isDebugEnabled ())
          s_aLogger.debug ("Trying to get default SecureRandom");

        // Default
        aSecureRandom = new SecureRandom ();

        if (s_aLogger.isDebugEnabled ())
          s_aLogger.debug ("Using default SecureRandom");
      }
    }
    return aSecureRandom;
  }

  static
  {
    // Find a good description that states how it is done this way:
    // https://www.cigital.com/blog/proper-use-of-javas-securerandom/

    // Initialize SecureRandom
    // This is a lengthy operation, to be done only upon
    // initialization of the application. Especial with Java <= 1.5 this whole
    // block takes more or less forever.
    final SecureRandom aSecureRandom = _createSecureRandomInstance ();

    // Seed first with the current time
    aSecureRandom.setSeed (System.currentTimeMillis ());

    // Get 128 random bytes
    aSecureRandom.nextBytes (new byte [128]);

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Generating intial seed for VerySecureRandom");

    // Create secure number generators with the random seed
    final byte [] aSeed = aSecureRandom.generateSeed (16);

    // Initialize main secure random
    s_aSecureRandom = _createSecureRandomInstance ();
    s_aSecureRandom.setSeed (aSeed);
  }

  @PresentForCodeCoverage
  private static final VerySecureRandom s_aInstance = new VerySecureRandom ();

  private VerySecureRandom ()
  {}

  private static final AtomicInteger s_aCounter = new AtomicInteger (0);

  /**
   * @return The {@link SecureRandom} instance that does the hard work. Never
   *         <code>null</code>.
   */
  @Nonnull
  public static SecureRandom getInstance ()
  {
    if ((s_aCounter.incrementAndGet () % 20) == 0)
    {
      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug ("Re-seeding VerySecureRandom");

      // Re-seed sometimes :)
      s_aSecureRandom.setSeed (s_aSecureRandom.generateSeed (10));
    }

    return s_aSecureRandom;
  }
}

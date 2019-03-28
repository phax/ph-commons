/**
 * Copyright (C) 2014-2019 Philip Helger (www.helger.com)
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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.lang.TimeValue;
import com.helger.commons.string.StringParser;
import com.helger.commons.system.SystemProperties;
import com.helger.commons.timing.StopWatch;

/**
 * A secure random generator initialized with another secure random
 * generator.<br>
 * Using <code>/dev/random</code> may require waiting for the result as it uses
 * so-called entropy pool, where random data may not be available at the moment.
 * <code>/dev/urandom</code> returns as many bytes as user requested and thus it
 * is less random than /dev/random.<br>
 * <ul>
 * <li><strong>random</strong> When read, the /dev/random device will only
 * return random bytes within the estimated number of bits of noise in the
 * entropy pool. /dev/random should be suitable for uses that need very high
 * quality randomness such as one-time pad or key generation. When the entropy
 * pool is empty, reads from /dev/random will block until additional
 * environmental noise is gathered.</li>
 * <li><strong>urandom</strong> A read from the /dev/urandom device will not
 * block waiting for more entropy. As a result, if there is not sufficient
 * entropy in the entropy pool, the returned values are theoretically vulnerable
 * to a cryptographic attack on the algorithms used by the driver. Knowledge of
 * how to do this is not available in the current unclassified literature, but
 * it is theoretically possible that such an attack may exist. If this is a
 * concern in your application, use /dev/random instead.</li>
 * </ul>
 * <p>
 * Use <code>-Djava.security.egd=file:/dev/./urandom</code> on the commandline
 * to use urandom
 * </p>
 *
 * @author Philip Helger
 */
@Immutable
public final class VerySecureRandom
{
  public static final int DEFAULT_RE_SEED_INTERVAL = 20;
  private static final Logger LOGGER = LoggerFactory.getLogger (VerySecureRandom.class);

  private static final int SEED_BYTE_COUNT = 64;
  private static final int WARNING_MILLISECONDS_THRESHOLD = 500;
  private static final SecureRandom s_aSecureRandom;
  private static final AtomicInteger s_aReSeedInterval = new AtomicInteger (DEFAULT_RE_SEED_INTERVAL);
  private static final AtomicInteger s_aCounter = new AtomicInteger (0);

  static
  {
    // Since v9.3.2
    final String sPropValue = SystemProperties.getPropertyValueOrNull ("ph.securerandom-reseed-interval");
    final Integer aReSeedInterval = StringParser.parseIntObj (sPropValue);
    if (aReSeedInterval != null)
    {
      final int nReSeedInterval = aReSeedInterval.intValue ();
      if (nReSeedInterval >= 0)
      {
        // Log only, if a system property is present
        if (LOGGER.isInfoEnabled ())
          LOGGER.info ("VerySecureRandom uses by default re-seed interval " + nReSeedInterval);
        s_aReSeedInterval.set (nReSeedInterval);
      }
    }
  }

  /**
   * Create a new {@link SecureRandom} instance. First the IBM secure random is
   * tried, than the SHA1PRNG secure random and finally if the previous ones
   * failed, the default instance is used. In certain circumstances (Linux +
   * some Java version; most likely using the blocking '/dev/random') this
   * initialization takes forever and that's why the debug statements are added.
   *
   * @return A new {@link SecureRandom} instance. Never <code>null</code>.
   */
  @Nonnull
  private static SecureRandom _createSecureRandomInstance ()
  {
    SecureRandom aSecureRandom;
    try
    {
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Trying to get SecureRandom: IBMSecureRandom, IBMJCE");

      // IBM JCE
      // http://www.ibm.com/developerworks/java/jdk/security/50/secguides/JceDocs/api_users_guide.html
      aSecureRandom = SecureRandom.getInstance ("IBMSecureRandom", "IBMJCE");

      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Using SecureRandom: IBMSecureRandom, IBMJCE");
    }
    catch (final Exception ex)
    {
      try
      {
        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Trying to get SecureRandom: SHA1PRNG");

        // Oracle
        // http://docs.oracle.com/javase/6/docs/technotes/guides/security/crypto/CryptoSpec.html
        // http://docs.oracle.com/javase/7/docs/technotes/guides/security/crypto/CryptoSpec.html
        // http://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html
        aSecureRandom = SecureRandom.getInstance ("SHA1PRNG");

        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Using SecureRandom: SHA1PRNG");
      }
      catch (final Exception ex2)
      {
        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Trying to get default SecureRandom");

        // Default
        aSecureRandom = new SecureRandom ();

        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Using default SecureRandom");
      }
    }
    return aSecureRandom;
  }

  static
  {
    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Strong SecureRandoms: " + java.security.Security.getProperty ("securerandom.strongAlgorithms"));

    // Find a good description that states how it is done this way:
    // https://www.cigital.com/blog/proper-use-of-javas-securerandom/
    // Updated to
    // https://www.synopsys.com/blogs/software-security/proper-use-of-javas-securerandom/

    // Initialize SecureRandom
    // This is a lengthy operation, to be done only upon
    // initialization of the application. Especial with Java <= 1.5 on Linux
    // this whole block takes more or less forever (when using /dev/random).
    SecureRandom aNativeRandom;
    try
    {
      // Try to use /dev/urandom first - on Linux machines this is preferred
      // On Windows this throws a NoSuchAlgorithmException
      aNativeRandom = SecureRandom.getInstance ("NativePRNGNonBlocking");
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("SecureRandom.getInstance ('NativePRNGNonBLocking') was successful");
    }
    catch (final NoSuchAlgorithmException ex)
    {
      // fallback
      aNativeRandom = new SecureRandom ();
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Falling back to default SecureRandom for initialization");
    }

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Generating intial seed for VerySecureRandom - " + SEED_BYTE_COUNT + " bytes");

    final StopWatch aSW = StopWatch.createdStarted ();
    // Generate seed
    final byte [] aSeed = aNativeRandom.generateSeed (SEED_BYTE_COUNT);

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Finished generating intial seed for VerySecureRandom");

    // Initialize main secure random
    s_aSecureRandom = _createSecureRandomInstance ();
    s_aSecureRandom.setSeed (aSeed);

    final long nDurationMillis = aSW.stopAndGetMillis ();
    if (nDurationMillis > WARNING_MILLISECONDS_THRESHOLD)
      if (LOGGER.isWarnEnabled ())
        LOGGER.warn ("Initially seeding VerySecureRandom took too long (" +
                     nDurationMillis +
                     " milliseconds) - you may consider using '/dev/urandom'");
  }

  @PresentForCodeCoverage
  private static final VerySecureRandom s_aInstance = new VerySecureRandom ();

  private VerySecureRandom ()
  {}

  /**
   * Set the interval of {@link #getInstance()} calls after which the random
   * should be re-seeded.
   *
   * @param nReseedInterval
   *        The re-seed interval. Must be &ge; 0. The value of 0 means: don't
   *        re-seed. The default value is {@value #DEFAULT_RE_SEED_INTERVAL}.
   */
  public static void setReSeedInterval (@Nonnegative final int nReseedInterval)
  {
    ValueEnforcer.isGE0 (nReseedInterval, "ReseedInterval");
    s_aReSeedInterval.set (nReseedInterval);
  }

  /**
   * Get the interval of {@link #getInstance()} calls after which the random
   * should be re-seeded.
   *
   * @return The re-seed interval. Always &ge; 0. The value of 0 means: don't
   *         re-seed. The default value is {@value #DEFAULT_RE_SEED_INTERVAL}.
   */
  @Nonnegative
  public static int getReSeedInterval ()
  {
    return s_aReSeedInterval.get ();
  }

  /**
   * @return The {@link SecureRandom} instance that does the hard work. Never
   *         <code>null</code>.
   */
  @Nonnull
  public static SecureRandom getInstance ()
  {
    final int nReSeedInterval = getReSeedInterval ();
    if (nReSeedInterval > 0)
      if ((s_aCounter.incrementAndGet () % nReSeedInterval) == 0)
      {
        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Re-seeding VerySecureRandom started");

        // Re-seed
        final TimeValue aDuration = StopWatch.runMeasured ( () -> s_aSecureRandom.setSeed (s_aSecureRandom.generateSeed (SEED_BYTE_COUNT)));
        if (aDuration.getAsMillis () > WARNING_MILLISECONDS_THRESHOLD)
          if (LOGGER.isWarnEnabled ())
            LOGGER.warn ("Re-seeding VerySecureRandom took too long (" +
                         aDuration.getAsMillis () +
                         " milliseconds) - you may consider using '/dev/urandom'");
      }

    return s_aSecureRandom;
  }
}

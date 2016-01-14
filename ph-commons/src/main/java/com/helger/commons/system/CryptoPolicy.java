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
package com.helger.commons.system;

import java.security.InvalidKeyException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.concurrent.ThreadSafe;
import javax.crypto.Cipher;
import javax.crypto.ExemptionMechanism;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;

/**
 * Helper class to see if unlimited strength crypto is available. If it is not,
 * then symmetric encryption algorithms are restricted to 128-bit key size or
 * the encryption must provide key weakening or key escrow.
 * <p>
 * This program attempts to generate a 256-bit AES key and use it to do to a
 * simple encryption. If the encryption succeeds, the assumption is that the JVM
 * being used has the "unlimited" strength JCE jurisdiction policy files
 * installed.
 * </p>
 * <p>
 * We use this for JUnit tests. If unlimited strength crypto is not available,
 * we simply skip certain JUnit tests that would require it.
 * </p>
 * Based on owasp-esapi-java source.<br>
 * http://code.google.com/p/owasp-esapi-java/
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class CryptoPolicy
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (CryptoPolicy.class);
  private static final AtomicBoolean s_aChecked = new AtomicBoolean (false);
  private static boolean s_bUnlimitedStrength;

  @PresentForCodeCoverage
  private static final CryptoPolicy s_aInstance = new CryptoPolicy ();

  private CryptoPolicy ()
  {}

  private static boolean _isUnlimitedStrengthAvailable ()
  {
    try
    {
      // Max sym key size is 128 unless unlimited
      // strength jurisdiction policy files installed.
      final KeyGenerator aKeyGen = KeyGenerator.getInstance ("AES");
      aKeyGen.init (256);
      final SecretKey aSecretKey = aKeyGen.generateKey ();
      final byte [] raw = aSecretKey.getEncoded ();
      final SecretKey aSecretKeySpec = new SecretKeySpec (raw, "AES");
      final Cipher aCipher = Cipher.getInstance ("AES/ECB/NoPadding");

      // This usually will throw InvalidKeyException unless the
      // unlimited jurisdiction policy files are installed. However,
      // it can succeed even if it's not a provider chooses to use
      // an exemption mechanism such as key escrow, key recovery, or
      // key weakening for this cipher instead.
      aCipher.init (Cipher.ENCRYPT_MODE, aSecretKeySpec);

      // Try the encryption on dummy string to make sure it works.
      // Not using padding so # bytes must be multiple of AES cipher
      // block size which is 16 bytes. Also, OK not to use UTF-8 here.
      final byte [] aEncrypted = aCipher.doFinal (CharsetManager.getAsBytes ("1234567890123456",
                                                                             CCharset.CHARSET_ISO_8859_1_OBJ));
      if (aEncrypted == null)
        throw new IllegalStateException ("Encryption of test string failed!");
      final ExemptionMechanism aExempt = aCipher.getExemptionMechanism ();
      if (aExempt != null)
      {
        // This is actually an indeterminate case, but we can't bank on it at
        // least for this (default) provider.
        s_aLogger.info ("Cipher uses exemption mechanism " + aExempt.getName ());
        return false;
      }
    }
    catch (final InvalidKeyException ex)
    {
      s_aLogger.info ("Invalid key size - unlimited strength crypto NOT installed!");
      return false;
    }
    catch (final Exception ex)
    {
      s_aLogger.info ("Failed to determine unlimited strength crypto state", ex);
      return false;
    }
    return true;
  }

  /**
   * Check to see if unlimited strength crypto is available. There is an
   * implicit assumption that the JCE jurisdiction policy files are not going to
   * be changing while this given JVM is running.
   *
   * @return <code>true</code> if we can provide keys longer than 128 bits,
   *         <code>false</code> otherwise
   */
  public static boolean isUnlimitedStrengthCryptoAvailable ()
  {
    if (!s_aChecked.getAndSet (true))
    {
      // Double initialisation in case of parallel access is just a minor
      // performance penalty
      s_bUnlimitedStrength = _isUnlimitedStrengthAvailable ();
    }
    return s_bUnlimitedStrength;
  }
}

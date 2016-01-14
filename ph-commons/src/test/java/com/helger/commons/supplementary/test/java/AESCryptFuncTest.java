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
package com.helger.commons.supplementary.test.java;

import static org.junit.Assert.assertArrayEquals;

import java.security.GeneralSecurityException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.Test;

import com.helger.commons.CGlobal;
import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.random.VerySecureRandom;
import com.helger.commons.system.CryptoPolicy;

public final class AESCryptFuncTest
{
  public static final class AESSecretKey implements SecretKey
  {
    private final byte [] m_aBytes;

    /**
     * Create a new AES key from the given byte array. Please note that for more
     * than 16 bytes, you need the
     * "unlimited strength jurisdiction policy files".
     *
     * @param aBytes
     *        Byte array with 16, 24 or 32 bytes. May not be <code>null</code>.
     */
    public AESSecretKey (final byte [] aBytes)
    {
      if (aBytes == null)
        throw new NullPointerException ("bytes");
      if (aBytes.length != 16 && aBytes.length != 24 && aBytes.length != 32)
        throw new IllegalArgumentException ("Key byte array must be 16, 24 or 32 bytes!");
      m_aBytes = ArrayHelper.getCopy (aBytes);
    }

    @Nonnull
    public String getAlgorithm ()
    {
      return "AES";
    }

    @Nonnull
    public String getFormat ()
    {
      return "RAW";
    }

    @Nonnull
    public byte [] getEncoded ()
    {
      return ArrayHelper.getCopy (m_aBytes);
    }

    @Nonnull
    public static SecretKey generateKey ()
    {
      final int nKeyLengthBytes = CryptoPolicy.isUnlimitedStrengthCryptoAvailable () ? 32 : 16;
      try
      {
        // Use JCA
        final KeyGenerator aKeyGen = KeyGenerator.getInstance ("AES");
        aKeyGen.init (nKeyLengthBytes * CGlobal.BITS_PER_BYTE);
        return aKeyGen.generateKey ();
      }
      catch (final GeneralSecurityException ex)
      {
        // Why so ever - create an AES key on our own!
        final byte [] aBytes = new byte [nKeyLengthBytes];
        VerySecureRandom.getInstance ().nextBytes (aBytes);
        return new AESSecretKey (aBytes);
      }
    }
  }

  @NotThreadSafe
  public static final class AESCrypter
  {
    private final SecretKey m_aKey;
    private Cipher m_aCipher;

    public AESCrypter (@Nonnull final SecretKey aKey)
    {
      if (aKey == null)
        throw new NullPointerException ("key");
      m_aKey = aKey;
      try
      {
        // Create the cipher
        // The standard algorithm name for AES is "AES", the standard name for
        // the Electronic Codebook mode is "ECB", and the standard name for
        // PKCS5-style padding is "PKCS5Padding":

        // PKCS5: The padding scheme described in RSA Laboratories,
        // "PKCS5: Password-Based Encryption Standard," version 1.5, November
        // 1993.
        m_aCipher = Cipher.getInstance ("AES/ECB/PKCS5Padding");
      }
      catch (final GeneralSecurityException ex)
      {
        throw new IllegalStateException ("Failed to init AES cipher!", ex);
      }
    }

    public byte [] encrypt (final byte [] aRaw)
    {
      try
      {
        // Initialize the cipher for encryption
        m_aCipher.init (Cipher.ENCRYPT_MODE, m_aKey);

        // Encrypt the cleartext
        return m_aCipher.doFinal (aRaw);
      }
      catch (final GeneralSecurityException ex)
      {
        throw new IllegalStateException ("Failed to encrypt using AES cipher!", ex);
      }
    }

    public byte [] decrypt (final byte [] aEncrypted)
    {
      try
      {
        // Initialize the cipher for encryption
        m_aCipher.init (Cipher.DECRYPT_MODE, m_aKey);

        // Encrypt the cleartext
        return m_aCipher.doFinal (aEncrypted);
      }
      catch (final GeneralSecurityException ex)
      {
        throw new IllegalStateException ("Failed to decrypt using AES cipher!", ex);
      }
    }
  }

  @Test
  public void testMe () throws Exception
  {
    final SecretKey aMyKey = AESSecretKey.generateKey ();

    final AESCrypter aCrypter = new AESCrypter (aMyKey);

    // Our cleartext
    final byte [] cleartext = CharsetManager.getAsBytes ("This is just än example", CCharset.CHARSET_ISO_8859_1_OBJ);

    // Encrypt the cleartext
    final byte [] ciphertext = aCrypter.encrypt (cleartext);
    final byte [] ciphertext2 = aCrypter.encrypt (cleartext);
    assertArrayEquals (ciphertext, ciphertext2);

    // Decrypt the ciphertext
    final byte [] cleartext1 = aCrypter.decrypt (ciphertext);
    assertArrayEquals (cleartext, cleartext1);
    final byte [] cleartext2 = aCrypter.decrypt (ciphertext2);
    assertArrayEquals (cleartext, cleartext2);
  }
}

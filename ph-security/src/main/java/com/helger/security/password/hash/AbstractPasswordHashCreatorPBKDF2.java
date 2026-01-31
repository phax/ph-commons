/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.security.password.hash;

import java.security.GeneralSecurityException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.base.CGlobal;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHex;
import com.helger.security.password.salt.IPasswordSalt;

/**
 * Base class for {@link IPasswordHashCreator} using the PBKDF2 algorithm.
 *
 * @author Philip Helger
 */
public abstract class AbstractPasswordHashCreatorPBKDF2 extends AbstractPasswordHashCreator
{
  protected final String m_sPBKDF2AlgorithmName;
  protected final int m_nIterations;
  protected final int m_nHashBytes;

  protected AbstractPasswordHashCreatorPBKDF2 (@NonNull @Nonempty final String sAlgorithmName,
                                               @NonNull @Nonempty final String sPBKDF2AlgorithmName,
                                               @Nonnegative final int nIterations,
                                               @Nonnegative final int nBytes)
  {
    super (sAlgorithmName);
    m_sPBKDF2AlgorithmName = ValueEnforcer.notEmpty (sPBKDF2AlgorithmName, "PBKDF2AlgorithmName");
    m_nIterations = ValueEnforcer.isGT0 (nIterations, "Iterations");
    m_nHashBytes = ValueEnforcer.isGT0 (nBytes, "Bytes");
  }

  public final boolean requiresSalt ()
  {
    return true;
  }

  /**
   * Computes the PBKDF2 hash of a password.
   *
   * @param aPassword
   *        the password to hash.
   * @param aSalt
   *        the salt
   * @param nIterations
   *        the iteration count (slowness factor)
   * @param nBytes
   *        the length of the hash to compute in bytes
   * @param sPBKDF2AlgorithmName
   *        The SecretKeyFactory parameter to use. May neither be <code>null</code> nor empty.
   * @return the PBDKF2 hash of the password
   */

  protected static final byte @NonNull [] pbkdf2 (final char @NonNull [] aPassword,
                                                  final byte @NonNull [] aSalt,
                                                  @Nonnegative final int nIterations,
                                                  @Nonnegative final int nBytes,
                                                  @NonNull @Nonempty final String sPBKDF2AlgorithmName)
  {
    try
    {
      final PBEKeySpec spec = new PBEKeySpec (aPassword, aSalt, nIterations, nBytes * CGlobal.BITS_PER_BYTE);
      final SecretKeyFactory skf = SecretKeyFactory.getInstance (sPBKDF2AlgorithmName);
      return skf.generateSecret (spec).getEncoded ();
    }
    catch (final GeneralSecurityException ex)
    {
      throw new IllegalStateException ("Failed to apply PBKDF2 algorithm '" +
                                       sPBKDF2AlgorithmName +
                                       "' with " +
                                       nIterations +
                                       " iterations and " +
                                       nBytes +
                                       " bytes",
                                       ex);
    }
  }

  @NonNull
  public String createPasswordHash (@NonNull final IPasswordSalt aSalt, @NonNull final String sPlainTextPassword)
  {
    ValueEnforcer.notNull (aSalt, "Salt");
    ValueEnforcer.notNull (sPlainTextPassword, "PlainTextPassword");

    final byte [] aDigest = pbkdf2 (sPlainTextPassword.toCharArray (),
                                    aSalt.getSaltBytes (),
                                    m_nIterations,
                                    m_nHashBytes,
                                    m_sPBKDF2AlgorithmName);
    return StringHex.getHexEncoded (aDigest);
  }
}

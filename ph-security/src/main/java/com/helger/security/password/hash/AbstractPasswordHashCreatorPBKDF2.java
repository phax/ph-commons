/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.string.StringHelper;
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

  protected AbstractPasswordHashCreatorPBKDF2 (@Nonnull @Nonempty final String sAlgorithmName,
                                               @Nonnull @Nonempty final String sPBKDF2AlgorithmName,
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
   *        The SecretKeyFactory parameter to use. May neither be
   *        <code>null</code> nor empty.
   * @return the PBDKF2 hash of the password
   */
  @Nonnull
  protected static final byte [] pbkdf2 (@Nonnull final char [] aPassword,
                                         @Nonnull final byte [] aSalt,
                                         @Nonnegative final int nIterations,
                                         @Nonnegative final int nBytes,
                                         @Nonnull @Nonempty final String sPBKDF2AlgorithmName)
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

  @Nonnull
  public String createPasswordHash (@Nonnull final IPasswordSalt aSalt, @Nonnull final String sPlainTextPassword)
  {
    ValueEnforcer.notNull (aSalt, "Salt");
    ValueEnforcer.notNull (sPlainTextPassword, "PlainTextPassword");

    final byte [] aDigest = pbkdf2 (sPlainTextPassword.toCharArray (),
                                    aSalt.getSaltBytes (),
                                    m_nIterations,
                                    m_nHashBytes,
                                    m_sPBKDF2AlgorithmName);
    return StringHelper.getHexEncoded (aDigest);
  }
}

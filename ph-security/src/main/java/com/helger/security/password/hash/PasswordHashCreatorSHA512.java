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

import java.nio.charset.StandardCharsets;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.security.messagedigest.EMessageDigestAlgorithm;
import com.helger.security.messagedigest.MessageDigestValue;
import com.helger.security.password.salt.IPasswordSalt;

/**
 * An implementation of {@link IPasswordHashCreator} using a single, <b>unsalted</b> SHA512 pass.
 * This is a fast hash without an iteration count and does not use the provided salt, so it is
 * vulnerable to brute force and rainbow table attacks and must <b>not</b> be used to hash new
 * passwords. Use {@link PasswordHashCreatorPBKDF2_SHA256_600000_48} or
 * {@link PasswordHashCreatorBCrypt} for new password hashing. This class is kept only to verify
 * password hashes that were created with it in the past.
 *
 * @author Philip Helger
 * @deprecated Use {@link PasswordHashCreatorPBKDF2_SHA256_600000_48} or
 *             {@link PasswordHashCreatorBCrypt} instead. Keep this class only for verifying
 *             existing password hashes.
 */
@Deprecated (since = "12.3.2")
public final class PasswordHashCreatorSHA512 extends AbstractPasswordHashCreator
{
  /** Hashing algorithm to use for user passwords - never change it! */
  @Deprecated (since = "12.3.2")
  public static final EMessageDigestAlgorithm MESSAGE_DIGEST_ALGO = EMessageDigestAlgorithm.SHA_512;

  /** Algorithm name. Prior to v12 this was "default" */
  @Deprecated (since = "12.3.2")
  public static final String ALGORITHM = MESSAGE_DIGEST_ALGO.getAlgorithm ();

  /**
   * Constructor using the {@link #ALGORITHM} name.
   */
  @Deprecated (since = "12.3.2")
  public PasswordHashCreatorSHA512 ()
  {
    super (ALGORITHM);
  }

  /**
   * {@inheritDoc}
   */
  @Deprecated (since = "12.3.2")
  public boolean requiresSalt ()
  {
    return false;
  }

  /** {@inheritDoc} */
  @Deprecated (since = "12.3.2")
  @NonNull
  public String createPasswordHash (@Nullable final IPasswordSalt aSalt, @NonNull final String sPlainTextPassword)
  {
    ValueEnforcer.notNull (sPlainTextPassword, "PlainTextPassword");

    return MessageDigestValue.create (sPlainTextPassword.getBytes (StandardCharsets.UTF_8), MESSAGE_DIGEST_ALGO)
                             .getHexEncodedDigestString ();
  }
}

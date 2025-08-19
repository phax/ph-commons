/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import com.helger.base.enforce.ValueEnforcer;
import com.helger.security.messagedigest.EMessageDigestAlgorithm;
import com.helger.security.messagedigest.MessageDigestValue;
import com.helger.security.password.salt.IPasswordSalt;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * The default implementation of {@link IPasswordHashCreator} using salted SHA512 hashes.
 *
 * @author Philip Helger
 */
public final class PasswordHashCreatorSHA512 extends AbstractPasswordHashCreator
{
  /** Hashing algorithm to use for user passwords - never change it! */
  public static final EMessageDigestAlgorithm MESSAGE_DIGEST_ALGO = EMessageDigestAlgorithm.SHA_512;

  /** Algorithm name. Prior to v12 this was "default" */
  public static final String ALGORITHM = MESSAGE_DIGEST_ALGO.getAlgorithm ();

  public PasswordHashCreatorSHA512 ()
  {
    super (ALGORITHM);
  }

  public boolean requiresSalt ()
  {
    return false;
  }

  @Nonnull
  public String createPasswordHash (@Nullable final IPasswordSalt aSalt, @Nonnull final String sPlainTextPassword)
  {
    ValueEnforcer.notNull (sPlainTextPassword, "PlainTextPassword");

    return MessageDigestValue.create (sPlainTextPassword.getBytes (StandardCharsets.UTF_8), MESSAGE_DIGEST_ALGO)
                             .getHexEncodedDigestString ();
  }
}

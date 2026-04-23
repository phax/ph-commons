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

/**
 * The old default implementation of {@link IPasswordHashCreator} that requires a salt and uses the
 * PBKDF2 algorithm with 1000 iterations and 48 bytes hash size. 1000 iterations is considered
 * insufficient by modern standards. Use {@link PasswordHashCreatorPBKDF2_SHA256_600000_48} for new
 * password hashing.
 *
 * @author Philip Helger
 * @since 10.0.0
 * @deprecated Use {@link PasswordHashCreatorPBKDF2_SHA256_600000_48} instead. Keep this class only
 *             for verifying existing password hashes.
 */
@Deprecated (since = "12.2.1", forRemoval = true)
public final class PasswordHashCreatorPBKDF2_SHA256_1000_48 extends AbstractPasswordHashCreatorPBKDF2
{
  @Deprecated (since = "12.2.1", forRemoval = true)
  public static final String ALGORITHM = "PBKDF2_SHA256_1000_48";
  @Deprecated (since = "12.2.1", forRemoval = true)
  public static final int PBKDF2_ITERATIONS = 1_000;
  @Deprecated (since = "12.2.1", forRemoval = true)
  public static final int HASH_BYTE_SIZE = 48;

  /**
   * Constructor using the predefined algorithm name, iterations and hash byte size.
   */
  @Deprecated (since = "12.2.1", forRemoval = true)
  public PasswordHashCreatorPBKDF2_SHA256_1000_48 ()
  {
    super (ALGORITHM, "PBKDF2WithHmacSHA256", PBKDF2_ITERATIONS, HASH_BYTE_SIZE);
  }
}

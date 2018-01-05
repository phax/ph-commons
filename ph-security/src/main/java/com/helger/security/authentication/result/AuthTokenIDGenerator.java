/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.security.authentication.result;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.random.RandomHelper;
import com.helger.commons.string.StringHelper;

/**
 * Create authentication token IDs.
 *
 * @author Philip Helger
 */
@Immutable
public final class AuthTokenIDGenerator
{
  /** By default a 16 bytes token is created */
  public static final int DEFAULT_TOKEN_BYTES = 16;

  private AuthTokenIDGenerator ()
  {}

  @Nonnull
  public static String generateNewTokenID ()
  {
    return generateNewTokenID (DEFAULT_TOKEN_BYTES);
  }

  @Nonnull
  public static String generateNewTokenID (@Nonnegative final int nBytes)
  {
    final byte [] aID = new byte [nBytes];
    RandomHelper.getRandom ().nextBytes (aID);
    return StringHelper.getHexEncoded (aID);
  }
}

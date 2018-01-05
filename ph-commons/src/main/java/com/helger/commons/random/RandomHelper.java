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
package com.helger.commons.random;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * A helper class that centrally works around the issue of slow
 * {@link SecureRandom} implementations on certain platforms in certain
 * constellations (usually on Linux with JDK 6). This class centrally allows to
 * decide between {@link VerySecureRandom} and regular {@link Random}.
 *
 * @author Philip Helger
 */
@Immutable
public final class RandomHelper
{
  private static final AtomicBoolean s_aUseSecureRandom = new AtomicBoolean (true);

  private RandomHelper ()
  {}

  public static void setUseSecureRandom (final boolean bUseSecureRandom)
  {
    s_aUseSecureRandom.set (bUseSecureRandom);
  }

  public static boolean isUseSecureRandom ()
  {
    return s_aUseSecureRandom.get ();
  }

  @Nonnull
  public static Random getRandom ()
  {
    if (isUseSecureRandom ())
      return VerySecureRandom.getInstance ();
    return new Random ();
  }

  @Nullable
  public static SecureRandom getSecureRandom ()
  {
    if (isUseSecureRandom ())
      return VerySecureRandom.getInstance ();
    return null;
  }
}

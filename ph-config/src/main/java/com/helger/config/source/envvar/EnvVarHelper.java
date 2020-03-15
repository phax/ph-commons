/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
package com.helger.config.source.envvar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;

/**
 * Helper functions for environment variables
 *
 * @author Philip Helger
 */
@Immutable
public final class EnvVarHelper
{
  public static final char DEFAULT_REPLACEMENT_CHAR = '_';
  // REGEX_ENV_VAR = "[a-zA-Z_]+[a-zA-Z0-9_]*";

  private EnvVarHelper ()
  {}

  public static boolean isValidFirstChar (final char c)
  {
    return (c >= 'A' && c <= 'Z') || c == '_';
  }

  public static boolean isValidFollowingChar (final char c)
  {
    return isValidFirstChar (c) || (c >= '0' && c <= '9');
  }

  @Nullable
  public static String getUnifiedSysEnvName (@Nonnull @Nonempty final String sName, final char cReplacement)
  {
    ValueEnforcer.notEmpty (sName, "Name");

    final StringBuilder ret = new StringBuilder (sName.length ());
    int nIndex = 0;
    for (final char c : sName.toCharArray ())
    {
      // Manually upper case for quicker tests afterwards
      final char cUpper = Character.toUpperCase (c);
      final boolean bIsValid = nIndex == 0 ? isValidFirstChar (cUpper) : isValidFollowingChar (cUpper);
      ret.append (bIsValid ? cUpper : cReplacement);
      nIndex++;
    }
    return ret.toString ();
  }
}

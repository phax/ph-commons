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
package com.helger.config.source.envvar;

import javax.annotation.Nonnull;
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
  public static final char REPLACEMENT_CHAR_TO_SKIP = 0;

  private EnvVarHelper ()
  {}

  /**
   * Check if the provided character is a valid first character for an
   * environment variable.
   *
   * @param c
   *        The character to check
   * @return <code>true</code> if it is valid, <code>false</code> if not
   */
  public static boolean isValidFirstChar (final char c)
  {
    return (c >= 'A' && c <= 'Z') || c == '_';
  }

  /**
   * Check if the provided character is a valid second or following character
   * for an environment variable.
   *
   * @param c
   *        The character to check
   * @return <code>true</code> if it is valid, <code>false</code> if not
   */
  public static boolean isValidFollowingChar (final char c)
  {
    return isValidFirstChar (c) || (c >= '0' && c <= '9');
  }

  /**
   * Get a unified system environment variable name from the provided property
   * name. This means uppercasing all characters and replacing invalid chars
   * with a replacement character.
   *
   * @param sName
   *        The property name to convert. May neither be <code>null</code> nor
   *        empty.
   * @param cReplacement
   *        The character that should be used to replace invalid characters. See
   *        {@link #DEFAULT_REPLACEMENT_CHAR}. If character `\u0000´ is used,
   *        the invalid character is simply ignored. See
   *        {@link #REPLACEMENT_CHAR_TO_SKIP}.
   * @return The
   */
  @Nonnull
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
      if (bIsValid)
        ret.append (cUpper);
      else
        if (cReplacement != REPLACEMENT_CHAR_TO_SKIP)
          ret.append (cReplacement);
      nIndex++;
    }
    return ret.toString ();
  }
}

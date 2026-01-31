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
package com.helger.base.url;

import java.util.regex.Pattern;

import org.jspecify.annotations.Nullable;

import com.helger.annotation.RegEx;
import com.helger.base.string.StringHelper;

/**
 * Helper class for dealing with URNs
 *
 * @author Philip Helger
 */
public final class URNHelper
{
  private URNHelper ()
  {}

  @RegEx
  public static final String REGEX_URN = "^\\Qurn:\\E" +
                                         "[a-zA-Z0-9][a-zA-Z0-9-]{0,31}" +
                                         "\\Q:\\E" +
                                         "[a-zA-Z0-9()+,\\-.:=@;$_!*'%/?#]+" +
                                         "$";

  private static final Pattern PATTERN_URN = Pattern.compile (REGEX_URN);

  /**
   * Check if the provided string is valid according to RFC 2141. Leading and trailing spaces of the
   * value to check will result in a negative result.
   *
   * @param sURN
   *        the URN to be validated. May be <code>null</code>.
   * @return <code>true</code> if the provided URN is not empty and matches the regular expression
   *         {@link #REGEX_URN}.
   * @since 10.0.0
   */
  public static boolean isValidURN (@Nullable final String sURN)
  {
    if (StringHelper.isEmpty (sURN))
      return false;
    return PATTERN_URN.matcher (sURN).matches ();
  }
}

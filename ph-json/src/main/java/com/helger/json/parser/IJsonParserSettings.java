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
package com.helger.json.parser;

import com.helger.annotation.Nonnegative;

/**
 * This class contains the read-only API for the settings for the JSON parser.
 *
 * @author Philip Helger
 * @since v12.0.0
 */
public interface IJsonParserSettings
{
  /**
   * @return <code>true</code> if position tracking is enabled, <code>false</code> if not.
   */
  boolean isTrackPosition ();

  @Nonnegative
  int getTabSize ();

  boolean isAlwaysUseBigNumber ();

  boolean isRequireStringQuotes ();

  boolean isAllowSpecialCharsInStrings ();

  /**
   * @return <code>true</code> if a check for end of input should be performed, <code>false</code>
   *         if not.
   */
  boolean isCheckForEOI ();

  /**
   * @return The maximum nesting depth of the JSON to read. Always &gt; 0.
   */
  @Nonnegative
  int getMaxNestingDepth ();

  /**
   * @return The maximum allowed absolute value of a numeric exponent (the part after
   *         <code>e</code>/<code>E</code>). Always &gt; 0. This limits the amount of memory that a
   *         single number token can force to be allocated - e.g. <code>1e999999999</code>. See
   *         {@link JsonParserSettings#DEFAULT_MAX_EXPONENT}.
   * @since v12.3.3
   */
  @Nonnegative
  int getMaxExponent ();
}

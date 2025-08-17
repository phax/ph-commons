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
package com.helger.json.parser;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;

import jakarta.annotation.Nonnull;

/**
 * This class contains the settings for the JSON parser. Prior to v12 they were contained directly
 * inside {@link JsonParser}.
 *
 * @author Philip Helger
 * @since v12.0.0
 */
@NotThreadSafe
public final class JsonParserSettings implements IJsonParserSettings
{
  // Default values
  public static final boolean DEFAULT_TRACK_POSITION = false;
  public static final int DEFAULT_TAB_SIZE = 8;
  public static final boolean DEFAULT_ALWAYS_USE_BIG_NUMBER = false;
  public static final boolean DEFAULT_REQUIRE_STRING_QUOTES = true;
  public static final boolean DEFAULT_ALLOW_SPECIAL_CHARS_IN_STRING = false;
  public static final boolean DEFAULT_CHECK_FOR_EOI = true;
  public static final int DEFAULT_MAX_NESTING_DEPTH = 1000;

  // Settings
  private boolean m_bTrackPosition = DEFAULT_TRACK_POSITION;
  private int m_nTabSize = DEFAULT_TAB_SIZE;
  private boolean m_bAlwaysUseBigNumber = DEFAULT_ALWAYS_USE_BIG_NUMBER;
  private boolean m_bRequireStringQuotes = DEFAULT_REQUIRE_STRING_QUOTES;
  private boolean m_bAllowSpecialCharsInStrings = DEFAULT_ALLOW_SPECIAL_CHARS_IN_STRING;
  private boolean m_bCheckForEOI = DEFAULT_CHECK_FOR_EOI;
  private int m_nMaxNestingDepth = DEFAULT_MAX_NESTING_DEPTH;

  public JsonParserSettings ()
  {}

  public JsonParserSettings (@Nonnull final IJsonParserSettings rhs)
  {
    assignFrom (rhs);
  }

  /**
   * @return <code>true</code> if position tracking is enabled, <code>false</code> if not. By
   *         default it is disabled - see {@link #DEFAULT_TRACK_POSITION}
   */
  public boolean isTrackPosition ()
  {
    return m_bTrackPosition;
  }

  /**
   * @param bTrackPosition
   *        <code>true</code> to track the position, <code>false</code> if not.
   * @return this for chaining
   */
  @Nonnull
  public JsonParserSettings setTrackPosition (final boolean bTrackPosition)
  {
    m_bTrackPosition = bTrackPosition;
    return this;
  }

  @Nonnegative
  public int getTabSize ()
  {
    return m_nTabSize;
  }

  /**
   * @param nTabSize
   *        The tab-size to use. Must be &gt; 0.
   * @return this for chaining
   */
  @Nonnull
  public JsonParserSettings setTabSize (@Nonnegative final int nTabSize)
  {
    ValueEnforcer.isGT0 (nTabSize, "TabSize");
    m_nTabSize = nTabSize;
    return this;
  }

  public boolean isAlwaysUseBigNumber ()
  {
    return m_bAlwaysUseBigNumber;
  }

  /**
   * @param bAlwaysUseBigNumber
   *        <code>true</code> to always use <code>BigDecimal</code> or <code>BigInteger</code> when
   *        reading numerical values.
   * @return this for chaining
   */
  @Nonnull
  public JsonParserSettings setAlwaysUseBigNumber (final boolean bAlwaysUseBigNumber)
  {
    m_bAlwaysUseBigNumber = bAlwaysUseBigNumber;
    return this;
  }

  public boolean isRequireStringQuotes ()
  {
    return m_bRequireStringQuotes;
  }

  /**
   * @param bRequireStringQuotes
   *        <code>true</code> if string values always needs to be quoted, <code>false</code> if
   *        certain non-quoted values are also supported.
   * @return this for chaining
   */
  @Nonnull
  public JsonParserSettings setRequireStringQuotes (final boolean bRequireStringQuotes)
  {
    m_bRequireStringQuotes = bRequireStringQuotes;
    return this;
  }

  public boolean isAllowSpecialCharsInStrings ()
  {
    return m_bAllowSpecialCharsInStrings;
  }

  /**
   * @param bAllowSpecialCharsInStrings
   *        <code>true</code> if special characters are allowed in a JSON string, <code>false</code>
   *        if not.
   * @return this for chaining
   */
  @Nonnull
  public JsonParserSettings setAllowSpecialCharsInStrings (final boolean bAllowSpecialCharsInStrings)
  {
    m_bAllowSpecialCharsInStrings = bAllowSpecialCharsInStrings;
    return this;
  }

  /**
   * @return <code>true</code> if a check for end of input should be performed, <code>false</code>
   *         if not. By default it is enabled for backwards compatibility - see
   *         {@link #DEFAULT_CHECK_FOR_EOI}
   */
  public boolean isCheckForEOI ()
  {
    return m_bCheckForEOI;
  }

  /**
   * Enable or disable the check for end of input. This can be helpful, if an InputStream contains
   * several JSON objects in a row, to read all of them in a single flow.
   *
   * @param bCheckForEOI
   *        <code>true</code> to check for EOI, <code>false</code> if not.
   * @return this for chaining
   */
  @Nonnull
  public JsonParserSettings setCheckForEOI (final boolean bCheckForEOI)
  {
    m_bCheckForEOI = bCheckForEOI;
    return this;
  }

  /**
   * @return The maximum nesting depth of the JSON to read. Always &gt; 0.
   */
  @Nonnegative
  public int getMaxNestingDepth ()
  {
    return m_nMaxNestingDepth;
  }

  /**
   * @param nMaxNestingDepth
   *        The maximum nesting depth of the JSON to read. Must be &gt; 0.
   * @return this for chaining
   */
  @Nonnull
  public JsonParserSettings setMaxNestingDepth (@Nonnegative final int nMaxNestingDepth)
  {
    ValueEnforcer.isGT0 (nMaxNestingDepth, "MaxNestingDepth");
    m_nMaxNestingDepth = nMaxNestingDepth;
    return this;
  }

  public void assignFrom (@Nonnull final IJsonParserSettings rhs)
  {
    ValueEnforcer.notNull (rhs, "rhs");
    setTrackPosition (rhs.isTrackPosition ());
    setTabSize (rhs.getTabSize ());
    setAlwaysUseBigNumber (rhs.isAlwaysUseBigNumber ());
    setRequireStringQuotes (rhs.isRequireStringQuotes ());
    setAllowSpecialCharsInStrings (rhs.isAllowSpecialCharsInStrings ());
    setCheckForEOI (rhs.isCheckForEOI ());
    setMaxNestingDepth (rhs.getMaxNestingDepth ());
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("TrackPosition", m_bTrackPosition)
                                       .append ("TabSize", m_nTabSize)
                                       .append ("AlwaysUseBugBumber", m_bAlwaysUseBigNumber)
                                       .append ("RequireStringQuotes", m_bRequireStringQuotes)
                                       .append ("AllowSpecialCharsInStrings", m_bAllowSpecialCharsInStrings)
                                       .append ("CheckForEOI", m_bCheckForEOI)
                                       .append ("MaxNestingDepth", m_nMaxNestingDepth)
                                       .getToString ();
  }
}

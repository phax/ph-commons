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

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;

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
  /** By default the parse position (line and column) is not tracked. */
  public static final boolean DEFAULT_TRACK_POSITION = false;
  /** The default tab size used for column counting during position tracking. */
  public static final int DEFAULT_TAB_SIZE = 8;
  /**
   * By default numeric values are read as the smallest matching type instead of always using
   * <code>BigInteger</code> / <code>BigDecimal</code>.
   */
  public static final boolean DEFAULT_ALWAYS_USE_BIG_NUMBER = false;
  /** By default JSON string values must be enclosed in quotes. */
  public static final boolean DEFAULT_REQUIRE_STRING_QUOTES = true;
  /** By default special characters (e.g. raw line breaks) are not allowed inside JSON strings. */
  public static final boolean DEFAULT_ALLOW_SPECIAL_CHARS_IN_STRING = false;
  /**
   * By default the parser checks that no non-whitespace content follows the top-level JSON value.
   */
  public static final boolean DEFAULT_CHECK_FOR_EOI = true;
  /**
   * The default maximum nesting depth of arrays and objects. This limits the stack usage caused by
   * deeply nested input.
   */
  public static final int DEFAULT_MAX_NESTING_DEPTH = 1000;
  /**
   * The default maximum absolute value of a numeric exponent. The value of 100.000 is aligned with
   * the <code>MAX_BIGINT_SCALE_MAGNITUDE</code> constant of the Jackson JSON parser.
   */
  public static final int DEFAULT_MAX_EXPONENT = 100_000;

  // Settings
  private boolean m_bTrackPosition = DEFAULT_TRACK_POSITION;
  private int m_nTabSize = DEFAULT_TAB_SIZE;
  private boolean m_bAlwaysUseBigNumber = DEFAULT_ALWAYS_USE_BIG_NUMBER;
  private boolean m_bRequireStringQuotes = DEFAULT_REQUIRE_STRING_QUOTES;
  private boolean m_bAllowSpecialCharsInStrings = DEFAULT_ALLOW_SPECIAL_CHARS_IN_STRING;
  private boolean m_bCheckForEOI = DEFAULT_CHECK_FOR_EOI;
  private int m_nMaxNestingDepth = DEFAULT_MAX_NESTING_DEPTH;
  private int m_nMaxExponent = DEFAULT_MAX_EXPONENT;

  /**
   * Default constructor with default settings.
   */
  public JsonParserSettings ()
  {}

  /**
   * Copy constructor.
   *
   * @param rhs
   *        The settings to copy from. May not be <code>null</code>.
   */
  public JsonParserSettings (@NonNull final IJsonParserSettings rhs)
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
  @NonNull
  public JsonParserSettings setTrackPosition (final boolean bTrackPosition)
  {
    m_bTrackPosition = bTrackPosition;
    return this;
  }

  /**
   * @return The tab size used for position tracking. Always &gt; 0. Default is
   *         {@link #DEFAULT_TAB_SIZE}.
   */
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
  @NonNull
  public JsonParserSettings setTabSize (@Nonnegative final int nTabSize)
  {
    ValueEnforcer.isGT0 (nTabSize, "TabSize");
    m_nTabSize = nTabSize;
    return this;
  }

  /**
   * @return <code>true</code> if <code>BigDecimal</code> / <code>BigInteger</code> should always be
   *         used for numeric values, <code>false</code> if not. Default is
   *         {@link #DEFAULT_ALWAYS_USE_BIG_NUMBER}.
   */
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
  @NonNull
  public JsonParserSettings setAlwaysUseBigNumber (final boolean bAlwaysUseBigNumber)
  {
    m_bAlwaysUseBigNumber = bAlwaysUseBigNumber;
    return this;
  }

  /**
   * @return <code>true</code> if string values always need to be quoted, <code>false</code> if
   *         certain non-quoted values are also supported. Default is
   *         {@link #DEFAULT_REQUIRE_STRING_QUOTES}.
   */
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
  @NonNull
  public JsonParserSettings setRequireStringQuotes (final boolean bRequireStringQuotes)
  {
    m_bRequireStringQuotes = bRequireStringQuotes;
    return this;
  }

  /**
   * @return <code>true</code> if special characters are allowed in JSON strings,
   *         <code>false</code> if not. Default is {@link #DEFAULT_ALLOW_SPECIAL_CHARS_IN_STRING}.
   */
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
  @NonNull
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
  @NonNull
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
  @NonNull
  public JsonParserSettings setMaxNestingDepth (@Nonnegative final int nMaxNestingDepth)
  {
    ValueEnforcer.isGT0 (nMaxNestingDepth, "MaxNestingDepth");
    m_nMaxNestingDepth = nMaxNestingDepth;
    return this;
  }

  /**
   * @return The maximum allowed absolute value of a numeric exponent. Always &gt; 0. Default is
   *         {@link #DEFAULT_MAX_EXPONENT}.
   * @since v12.3.3
   */
  @Nonnegative
  public int getMaxExponent ()
  {
    return m_nMaxExponent;
  }

  /**
   * Set the maximum allowed absolute value of a numeric exponent. This limits the amount of memory a
   * single number token like <code>1e999999999</code> can force to be allocated.
   *
   * @param nMaxExponent
   *        The maximum allowed absolute exponent value. Must be &gt; 0.
   * @return this for chaining
   * @since v12.3.3
   */
  @NonNull
  public JsonParserSettings setMaxExponent (@Nonnegative final int nMaxExponent)
  {
    ValueEnforcer.isGT0 (nMaxExponent, "MaxExponent");
    m_nMaxExponent = nMaxExponent;
    return this;
  }

  /**
   * Assign all settings from the provided source.
   *
   * @param rhs
   *        The settings to copy from. May not be <code>null</code>.
   */
  public void assignFrom (@NonNull final IJsonParserSettings rhs)
  {
    ValueEnforcer.notNull (rhs, "rhs");
    setTrackPosition (rhs.isTrackPosition ());
    setTabSize (rhs.getTabSize ());
    setAlwaysUseBigNumber (rhs.isAlwaysUseBigNumber ());
    setRequireStringQuotes (rhs.isRequireStringQuotes ());
    setAllowSpecialCharsInStrings (rhs.isAllowSpecialCharsInStrings ());
    setCheckForEOI (rhs.isCheckForEOI ());
    setMaxNestingDepth (rhs.getMaxNestingDepth ());
    setMaxExponent (rhs.getMaxExponent ());
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
                                       .append ("MaxExponent", m_nMaxExponent)
                                       .getToString ();
  }
}

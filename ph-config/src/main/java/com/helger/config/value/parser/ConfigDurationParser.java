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
package com.helger.config.value.parser;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.string.StringHelper;

/**
 * Parses human friendly duration strings (e.g. <code>"5s"</code>, <code>"2d5m23ms"</code>,
 * <code>"3d 4h 22m 5s"</code>) into a {@link Duration}.
 * <p>
 * <b>Format</b>
 * <ul>
 * <li>One or more <code>[sign]&lt;number&gt;&lt;unit&gt;</code> segments, summed together</li>
 * <li>Each segment may carry an optional sign (<code>+</code> or <code>-</code>) that applies only
 * to that segment</li>
 * <li>Whitespace between segments, between sign and number, and between number and unit is
 * permitted but not required</li>
 * <li>Unit suffixes are matched case-insensitively</li>
 * <li>The same unit may appear more than once (the values are added with their respective
 * signs)</li>
 * <li>Decimal values are <b>not</b> accepted — write <code>"1h 30m"</code> instead of
 * <code>"1.5h"</code></li>
 * <li>Inputs longer than {@link #MAX_VALUE_LENGTH} characters are rejected without further
 * processing — this bounds the parser's work and mitigates denial-of-service / overflow attacks
 * via overly long configuration values</li>
 * </ul>
 * <b>Supported units</b>
 * <table>
 * <caption>Supported units</caption>
 * <tr>
 * <th>Suffix</th>
 * <th>Meaning</th>
 * </tr>
 * <tr>
 * <td><code>ns</code></td>
 * <td>nanoseconds</td>
 * </tr>
 * <tr>
 * <td><code>us</code></td>
 * <td>microseconds</td>
 * </tr>
 * <tr>
 * <td><code>ms</code></td>
 * <td>milliseconds</td>
 * </tr>
 * <tr>
 * <td><code>s</code></td>
 * <td>seconds</td>
 * </tr>
 * <tr>
 * <td><code>m</code></td>
 * <td>minutes</td>
 * </tr>
 * <tr>
 * <td><code>h</code></td>
 * <td>hours</td>
 * </tr>
 * <tr>
 * <td><code>d</code></td>
 * <td>days</td>
 * </tr>
 * </table>
 * <b>Examples</b>
 * <ul>
 * <li><code>"5ms"</code> → 5 milliseconds</li>
 * <li><code>"2d5m23ms"</code> → 2 days + 5 minutes + 23 milliseconds</li>
 * <li><code>"3d 4h 22m 5s"</code> → 3 days + 4 hours + 22 minutes + 5 seconds</li>
 * <li><code>"5h -10m"</code> → 5 hours − 10 minutes (= 4 hours 50 minutes)</li>
 * <li><code>"-5m -10s"</code> → minus (5 minutes + 10 seconds)</li>
 * </ul>
 *
 * @author Philip Helger
 * @since 12.2.5
 */
@Immutable
public final class ConfigDurationParser
{
  /**
   * The maximum accepted length of the input string. Values exceeding this length are rejected
   * without further processing to bound the parser's work and to mitigate denial-of-service /
   * overflow attacks via overly long configuration values. A legitimate duration string covering
   * every supported unit at the maximum {@code long} magnitude fits comfortably below this limit.
   */
  public static final int MAX_VALUE_LENGTH = 100;

  @PresentForCodeCoverage
  private static final ConfigDurationParser INSTANCE = new ConfigDurationParser ();

  private ConfigDurationParser ()
  {}

  private static void _onError (@Nullable final Consumer <String> aParseErrorHdl, @NonNull final String sMsg)
  {
    if (aParseErrorHdl != null)
      aParseErrorHdl.accept (sMsg);
  }

  @Nullable
  private static ChronoUnit _resolveUnit (@NonNull final String sUnitLowerCase)
  {
    return switch (sUnitLowerCase)
    {
      case "ns" -> ChronoUnit.NANOS;
      case "us" -> ChronoUnit.MICROS;
      case "ms" -> ChronoUnit.MILLIS;
      case "s" -> ChronoUnit.SECONDS;
      case "m" -> ChronoUnit.MINUTES;
      case "h" -> ChronoUnit.HOURS;
      case "d" -> ChronoUnit.DAYS;
      default -> null;
    };
  }

  /**
   * Parse a duration string. Equivalent to {@link #parseDuration(String, Consumer)} with a
   * <code>null</code> error handler — i.e. parse failures are reported only via a <code>null</code>
   * return value.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @return <code>null</code> if the input is <code>null</code>, blank, or malformed; otherwise the
   *         parsed {@link Duration}.
   */
  @Nullable
  public static Duration parseDuration (@Nullable final String sValue)
  {
    return parseDuration (sValue, null);
  }

  /**
   * Parse a duration string. The format is described in the class JavaDoc.
   * <p>
   * A <code>null</code> or blank input returns <code>null</code> without invoking the error handler
   * — that case is treated as "no value", not "invalid value". Any other parse failure invokes the
   * error handler (if non-<code>null</code>) with a descriptive message and returns
   * <code>null</code>.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @param aParseErrorHdl
   *        Invoked once with a descriptive message if parsing fails. May be <code>null</code> in
   *        which case parse failures are silent.
   * @return <code>null</code> if the input is <code>null</code>, blank, or malformed; otherwise the
   *         parsed {@link Duration}.
   */
  @Nullable
  public static Duration parseDuration (@Nullable final String sValue, @Nullable final Consumer <String> aParseErrorHdl)
  {
    if (StringHelper.isEmpty (sValue))
      return null;

    if (sValue.length () > MAX_VALUE_LENGTH)
    {
      _onError (aParseErrorHdl,
                "Duration value is too long (" +
                                sValue.length () +
                                " characters, maximum is " +
                                MAX_VALUE_LENGTH +
                                ")");
      return null;
    }

    final String sTrimmed = sValue.trim ();
    final int nLen = sTrimmed.length ();
    if (nLen == 0)
      return null;

    final char [] aTrimmed = sTrimmed.toCharArray ();
    Duration ret = Duration.ZERO;
    boolean bSawAny = false;
    int nIdx = 0;

    while (nIdx < nLen)
    {
      // Skip whitespace before the next segment
      while (nIdx < nLen && Character.isWhitespace (aTrimmed[nIdx]))
        nIdx++;
      if (nIdx >= nLen)
        break;

      // Optional per-segment sign
      boolean bNegate = false;
      final char cSign = aTrimmed[nIdx];
      if (cSign == '-' || cSign == '+')
      {
        bNegate = cSign == '-';
        nIdx++;

        // Optional whitespace between sign and number
        while (nIdx < nLen && Character.isWhitespace (aTrimmed[nIdx]))
          nIdx++;

        if (nIdx >= nLen)
        {
          _onError (aParseErrorHdl, "Trailing sign '" + cSign + "' without a number in '" + sValue + "'");
          return null;
        }
      }

      // Read the numeric part — digits only
      final int nNumStart = nIdx;
      while (nIdx < nLen && Character.isDigit (aTrimmed[nIdx]))
        nIdx++;

      if (nIdx == nNumStart)
      {
        _onError (aParseErrorHdl,
                  "Expected a digit at position " + nIdx + " but found '" + aTrimmed[nIdx] + "' in '" + sValue + "'");
        return null;
      }

      final long nNumber;
      try
      {
        nNumber = Long.parseLong (sTrimmed.substring (nNumStart, nIdx));
      }
      catch (final NumberFormatException ex)
      {
        _onError (aParseErrorHdl,
                  "Numeric value '" + sTrimmed.substring (nNumStart, nIdx) + "' is too large in '" + sValue + "'");
        return null;
      }

      // Optional whitespace between number and unit
      while (nIdx < nLen && Character.isWhitespace (aTrimmed[nIdx]))
        nIdx++;

      // Read the unit (1-2 ASCII letters)
      final int nUnitStart = nIdx;
      while (nIdx < nLen && Character.isLetter (aTrimmed[nIdx]))
        nIdx++;

      if (nIdx == nUnitStart)
      {
        _onError (aParseErrorHdl, "Missing unit suffix after number '" + nNumber + "' in '" + sValue + "'");
        return null;
      }

      // Resolve the unit
      final String sUnit = sTrimmed.substring (nUnitStart, nIdx).toLowerCase (Locale.ROOT);
      final ChronoUnit eUnit = _resolveUnit (sUnit);
      if (eUnit == null)
      {
        _onError (aParseErrorHdl, "Unknown duration unit '" + sUnit + "' in '" + sValue + "'");
        return null;
      }

      // Remember number and unit in result object
      try
      {
        ret = ret.plus (bNegate ? -nNumber : nNumber, eUnit);
      }
      catch (final ArithmeticException ex)
      {
        _onError (aParseErrorHdl,
                  "Duration overflow when " +
                                  (bNegate ? "subtracting" : "adding") +
                                  " " +
                                  nNumber +
                                  " " +
                                  sUnit +
                                  " in '" +
                                  sValue +
                                  "': " +
                                  ex.getMessage ());
        return null;
      }
      bSawAny = true;
    }

    if (!bSawAny)
    {
      _onError (aParseErrorHdl, "No duration components found in '" + sValue + "'");
      return null;
    }

    return ret;
  }
}

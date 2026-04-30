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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

import com.helger.base.wrapper.Wrapper;

/**
 * Test class for class {@link ConfigDurationParser}.
 *
 * @author Philip Helger
 */
public final class ConfigDurationParserTest
{
  @Test
  public void testNullAndBlank ()
  {
    final Wrapper <String> aErr = new Wrapper <> ();
    assertNull (ConfigDurationParser.parseDuration (null, aErr::set));
    assertNull (ConfigDurationParser.parseDuration ("", aErr::set));
    assertNull (ConfigDurationParser.parseDuration ("   ", aErr::set));
    assertNull (ConfigDurationParser.parseDuration ("\t\n  ", aErr::set));
    // null/blank input must NOT invoke the error handler
    assertFalse (aErr.isSet ());
  }

  @Test
  public void testSingleUnits ()
  {
    assertEquals (Duration.ofNanos (5), ConfigDurationParser.parseDuration ("5ns"));
    assertEquals (Duration.of (5, ChronoUnit.MICROS), ConfigDurationParser.parseDuration ("5us"));
    assertEquals (Duration.ofMillis (5), ConfigDurationParser.parseDuration ("5ms"));
    assertEquals (Duration.ofSeconds (5), ConfigDurationParser.parseDuration ("5s"));
    assertEquals (Duration.ofMinutes (5), ConfigDurationParser.parseDuration ("5m"));
    assertEquals (Duration.ofHours (5), ConfigDurationParser.parseDuration ("5h"));
    assertEquals (Duration.ofDays (5), ConfigDurationParser.parseDuration ("5d"));
    assertEquals (Duration.ZERO, ConfigDurationParser.parseDuration ("0s"));
  }

  @Test
  public void testCompoundNoSpaces ()
  {
    final Duration aExpected = Duration.ofDays (2).plusMinutes (5).plusMillis (23);
    assertEquals (aExpected, ConfigDurationParser.parseDuration ("2d5m23ms"));
  }

  @Test
  public void testCompoundWithSpaces ()
  {
    final Duration aExpected = Duration.ofDays (3).plusHours (4).plusMinutes (22).plusSeconds (5);
    assertEquals (aExpected, ConfigDurationParser.parseDuration ("3d 4h 22m 5s"));
    // Multiple spaces and tabs
    assertEquals (aExpected, ConfigDurationParser.parseDuration ("  3d   4h\t22m  5s  "));
    // Whitespace between number and unit
    assertEquals (aExpected, ConfigDurationParser.parseDuration ("3 d 4 h 22 m 5 s"));
  }

  @Test
  public void testCaseInsensitive ()
  {
    final Duration aExpected = Duration.ofMillis (5);
    assertEquals (aExpected, ConfigDurationParser.parseDuration ("5ms"));
    assertEquals (aExpected, ConfigDurationParser.parseDuration ("5MS"));
    assertEquals (aExpected, ConfigDurationParser.parseDuration ("5Ms"));
    assertEquals (aExpected, ConfigDurationParser.parseDuration ("5mS"));
  }

  @Test
  public void testRepeatedUnitsAreSummed ()
  {
    assertEquals (Duration.ofDays (3), ConfigDurationParser.parseDuration ("1d 2d"));
    assertEquals (Duration.ofSeconds (3), ConfigDurationParser.parseDuration ("1s1s1s"));
    assertEquals (Duration.ofSeconds (3), ConfigDurationParser.parseDuration ("1s 1s 1s"));
    assertEquals (Duration.ZERO, ConfigDurationParser.parseDuration ("1s-1s"));
  }

  @Test
  public void testNegationSingleSegment ()
  {
    assertEquals (Duration.ofSeconds (-5), ConfigDurationParser.parseDuration ("-5s"));
    assertEquals (Duration.ofSeconds (5), ConfigDurationParser.parseDuration ("+5s"));
  }

  @Test
  public void testPerSegmentSign ()
  {
    // Mixed signs: 5h - 10m = 4h 50m
    assertEquals (Duration.ofHours (5).minusMinutes (10), ConfigDurationParser.parseDuration ("5h -10m"));
    assertEquals (Duration.ofHours (5).minusMinutes (10), ConfigDurationParser.parseDuration ("5h-10m"));

    // First segment negated, second positive: -5m + 10s = -4m 50s
    assertEquals (Duration.ofMinutes (-5).plusSeconds (10), ConfigDurationParser.parseDuration ("-5m 10s"));
    assertEquals (Duration.ofMinutes (-5).plusSeconds (10), ConfigDurationParser.parseDuration ("-5m +10s"));

    // All segments negated: equivalent to old whole-string negation
    assertEquals (Duration.ofMinutes (-5).plusSeconds (-10), ConfigDurationParser.parseDuration ("-5m -10s"));

    // Three-segment compound
    assertEquals (Duration.ofDays (-1).plusHours (2).minusMinutes (3),
                  ConfigDurationParser.parseDuration ("-1d +2h -3m"));

    // Whitespace between sign and number is allowed
    assertEquals (Duration.ofSeconds (5), ConfigDurationParser.parseDuration ("- 5s").negated ());
    assertEquals (Duration.ofMinutes (5).minusSeconds (10), ConfigDurationParser.parseDuration ("+5m - 10s"));

    // Same unit appearing twice with different signs
    assertEquals (Duration.ofHours (4), ConfigDurationParser.parseDuration ("5h -1h"));
  }

  @Test
  public void testTrailingSign ()
  {
    final Wrapper <String> aErr1 = new Wrapper <> ();
    assertNull (ConfigDurationParser.parseDuration ("5s -", aErr1::set));
    assertNotNull (aErr1.get ());

    final Wrapper <String> aErr2 = new Wrapper <> ();
    assertNull (ConfigDurationParser.parseDuration ("-", aErr2::set));
    assertNotNull (aErr2.get ());
  }

  @Test
  public void testDoubleSignRejected ()
  {
    // "--5s", "+-5s", "++5s" must all fail — only one sign per segment
    final Wrapper <String> aErr1 = new Wrapper <> ();
    assertNull (ConfigDurationParser.parseDuration ("--5s", aErr1::set));
    assertNotNull (aErr1.get ());

    final Wrapper <String> aErr2 = new Wrapper <> ();
    assertNull (ConfigDurationParser.parseDuration ("+-5s", aErr2::set));
    assertNotNull (aErr2.get ());

    final Wrapper <String> aErr3 = new Wrapper <> ();
    assertNull (ConfigDurationParser.parseDuration ("5s --10m", aErr3::set));
    assertNotNull (aErr3.get ());
  }

  @Test
  public void testNoErrorHandler ()
  {
    // Must not throw NPE when error handler is null and input is malformed
    assertNull (ConfigDurationParser.parseDuration ("nonsense", null));
    assertNull (ConfigDurationParser.parseDuration ("5x", null));
  }

  @Test
  public void testParseErrorsInvokeHandler ()
  {
    // Unknown unit
    final Wrapper <String> aErr1 = new Wrapper <> ();
    assertNull (ConfigDurationParser.parseDuration ("5x", aErr1::set));
    assertNotNull (aErr1.get ());

    // Unit without number
    final Wrapper <String> aErr2 = new Wrapper <> ();
    assertNull (ConfigDurationParser.parseDuration ("ms", aErr2::set));
    assertNotNull (aErr2.get ());

    // Number without unit
    final Wrapper <String> aErr3 = new Wrapper <> ();
    assertNull (ConfigDurationParser.parseDuration ("5", aErr3::set));
    assertNotNull (aErr3.get ());

    // Trailing garbage after a valid segment
    final Wrapper <String> aErr4 = new Wrapper <> ();
    assertNull (ConfigDurationParser.parseDuration ("5s!", aErr4::set));
    assertNotNull (aErr4.get ());

    // Decimals are not supported
    final Wrapper <String> aErr5 = new Wrapper <> ();
    assertNull (ConfigDurationParser.parseDuration ("1.5h", aErr5::set));
    assertNotNull (aErr5.get ());

    // Three-letter unit is rejected
    final Wrapper <String> aErr6 = new Wrapper <> ();
    assertNull (ConfigDurationParser.parseDuration ("5sec", aErr6::set));
    assertNotNull (aErr6.get ());

    // Crap only is rejected
    final Wrapper <String> aErr7 = new Wrapper <> ();
    assertNull (ConfigDurationParser.parseDuration (" phi lip ", aErr7::set));
    assertNotNull (aErr7.get ());
  }

  @Test
  public void testNumberOverflow ()
  {
    final Wrapper <String> aErr = new Wrapper <> ();
    // Long.MAX_VALUE + 1 — too large for long parsing
    assertNull (ConfigDurationParser.parseDuration ("99999999999999999999d", aErr::set));
    assertTrue (aErr.isSet ());

    // Maximum long value
    aErr.set (null);
    assertNotNull (ConfigDurationParser.parseDuration ("9223372036854775807s", aErr::set));
    assertFalse (aErr.isSet ());

    // Overflow immediately
    assertNull (ConfigDurationParser.parseDuration ("9223372036854775808s", aErr::set));
    assertTrue (aErr.isSet ());

    // Overflow with plus
    aErr.set (null);
    assertNull (ConfigDurationParser.parseDuration ("9223372036854775807s +1s", aErr::set));
    assertTrue (aErr.isSet ());

    // Minimum long value
    aErr.set (null);
    assertNotNull (ConfigDurationParser.parseDuration ("-9223372036854775807s", aErr::set));
    assertFalse (aErr.isSet ());

    // Overflow with minus
    aErr.set (null);
    assertNull (ConfigDurationParser.parseDuration ("-9223372036854775807s -2s", aErr::set));
    assertTrue (aErr.isSet ());
  }

  @Test
  public void testZeroAndPositiveSign ()
  {
    assertEquals (Duration.ZERO, ConfigDurationParser.parseDuration ("0d"));
    assertEquals (Duration.ZERO, ConfigDurationParser.parseDuration ("+0s"));
    assertEquals (Duration.ZERO, ConfigDurationParser.parseDuration ("-0s"));
  }

  @Test
  public void testMaxLength ()
  {
    // Exactly MAX_VALUE_LENGTH characters is still valid
    final StringBuilder aBoundary = new StringBuilder ();
    while (aBoundary.length () + 2 <= ConfigDurationParser.MAX_VALUE_LENGTH)
      aBoundary.append ("1s");
    // Pad with whitespace to exactly MAX_VALUE_LENGTH if needed
    while (aBoundary.length () < ConfigDurationParser.MAX_VALUE_LENGTH)
      aBoundary.insert (0, ' ');
    assertEquals (ConfigDurationParser.MAX_VALUE_LENGTH, aBoundary.length ());
    assertNotNull (ConfigDurationParser.parseDuration (aBoundary.toString ()));

    // One character beyond the limit must be rejected
    final String sTooLong = "0".repeat (ConfigDurationParser.MAX_VALUE_LENGTH) + "s";
    assertEquals (ConfigDurationParser.MAX_VALUE_LENGTH + 1, sTooLong.length ());
    final Wrapper <String> aErr = new Wrapper <> ();
    assertNull (ConfigDurationParser.parseDuration (sTooLong, aErr::set));
    assertNotNull (aErr.get ());

    // Even pure whitespace beyond the limit is rejected (no expensive trim is performed first)
    final String sLongBlank = " ".repeat (ConfigDurationParser.MAX_VALUE_LENGTH + 1);
    final Wrapper <String> aErr2 = new Wrapper <> ();
    assertNull (ConfigDurationParser.parseDuration (sLongBlank, aErr2::set));
    assertNotNull (aErr2.get ());
  }
}

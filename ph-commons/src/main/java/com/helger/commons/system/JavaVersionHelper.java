/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.commons.system;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.CGlobal;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.datetime.PDTFromString;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;

/**
 * Helper class that holds the current class version. Must be a separate class
 * to maintain the correct initialization order.
 *
 * @author Philip Helger
 */
@Immutable
public final class JavaVersionHelper
{
  /** The global Java class version as a double value. */
  public static final double JAVA_CLASS_VERSION = StringParser.parseDouble (SystemProperties.getJavaClassVersion (),
                                                                            CGlobal.ILLEGAL_DOUBLE);
  // 1.8.0_144 => 8
  // 9.0.4 => 9
  public static final int JAVA_MAJOR_VERSION;
  // 1.8.0_144 => 144
  // 9.0.4 => 0
  public static final int JAVA_MINOR_VERSION;
  // 1.8.0_144 => -1
  // 9.0.4 => 4
  public static final int JAVA_MICRO_VERSION;

  private static final Logger LOGGER = LoggerFactory.getLogger (JavaVersionHelper.class);

  private static final LocalDateTime REFERENCE_DATE = PDTFactory.createLocalDateTime (2018, Month.JANUARY, 1, 0, 0, 0);
  private static final LocalDateTime ADOPTOPENJDK_BUILD_172 = PDTFactory.createLocalDateTime (2018, Month.MAY, 19, 0, 59, 0);

  @Nonnull
  @ReturnsMutableCopy
  static int [] getAsUnifiedVersion (@Nonnull final String sOriginalJavaVersion)
  {
    return getAsUnifiedVersion (sOriginalJavaVersion, SystemProperties.getJavaRuntimeVersion ());
  }

  @Nonnull
  @ReturnsMutableCopy
  static int [] getAsUnifiedVersion (@Nonnull final String sOriginalJavaVersion, @Nullable final String sJavaRuntimeVersion)
  {
    int nMajor = 0;
    int nMinor = 0;
    int nMicro = 0;

    String s = sOriginalJavaVersion;
    if (s.startsWith ("1."))
    {
      // Old up to and including v8. E.g.
      // 1.8.0_144
      // 1.8.0-adoptopenjdk

      // Skip "1."
      s = s.substring (2);
      // All up to first "."
      final int nSecondDot = s.indexOf ('.');
      if (nSecondDot < 0)
        throw new IllegalStateException ("Unexpected Java version string '" + sOriginalJavaVersion + "'");
      nMajor = StringParser.parseInt (s.substring (0, nSecondDot), -1);
      if (nMajor < 0)
        throw new IllegalStateException ("Failed to determine Java major version from '" + sOriginalJavaVersion + "'");

      final int nUnderscore = s.indexOf ('_');
      if (nUnderscore >= 0)
      {
        // Everything after "_"
        nMinor = StringParser.parseInt (s.substring (nUnderscore + 1), -1);
        if (nMinor < 0)
          throw new IllegalStateException ("Failed to determine Java minor version from '" + sOriginalJavaVersion + "'");

        // Micro part is not present
        nMicro = -1;
      }
      else
      {
        final int nDash = s.indexOf ('-');
        if (nDash >= 0)
        {
          // 1.8.0-adoptopenjdk

          // 1.8.0-adoptopenjdk-_2018_05_19_00_59-b00 == b172
          if (sJavaRuntimeVersion.startsWith (sOriginalJavaVersion))
          {
            // Use data as "minor"
            String sData = sJavaRuntimeVersion.substring (sOriginalJavaVersion.length ());
            sData = StringHelper.removeAll (sData, '_');
            sData = StringHelper.removeAll (sData, '-');
            final int nB = sData.indexOf ('b');
            if (nB >= 0)
              sData = sData.substring (0, nB);
            final LocalDateTime aDateTime = PDTFromString.getLocalDateTimeFromString (sData, "uuuuMMddHHmm");
            if (aDateTime != null)
            {
              // Check known versions
              if (aDateTime.equals (ADOPTOPENJDK_BUILD_172))
                nMinor = 172;
              else
              {
                // Minutes since reference date
                nMinor = Math.toIntExact (Duration.between (REFERENCE_DATE, aDateTime).toMinutes ());
              }
            }
            else
            {
              // Open...
              if (LOGGER.isWarnEnabled ())
                LOGGER.warn ("Unknown runtime version '" + sJavaRuntimeVersion + "' compared to java version '" + sOriginalJavaVersion);
              nMinor = -1;
              nMicro = -1;
            }
          }
          else
          {
            // Unknown runtime version
            if (LOGGER.isWarnEnabled ())
              LOGGER.warn ("Unknown runtime version '" + sJavaRuntimeVersion + "' compared to java version '" + sOriginalJavaVersion);
            nMinor = -1;
            nMicro = -1;
          }
        }
        else
          throw new IllegalStateException ("Unexpected Java version string '" + sOriginalJavaVersion + "'");
      }

    }
    else
    {
      // New since v9. E.g.:
      // 9.0.4
      // See http://openjdk.java.net/jeps/223

      // E.g 9-Ubuntu
      final int nFirstDash = s.indexOf ('-');
      if (nFirstDash > 0)
      {
        // '-' indicates "prerelease identifier"
        // Cut everything including and after the dash
        s = s.substring (0, nFirstDash);
      }

      // E.g. 9.1.2+62
      final int nFirstPlus = s.indexOf ('+');
      if (nFirstPlus > 0)
      {
        // "+" indicates the build number
        // Cut everything including and after the plus
        s = s.substring (0, nFirstPlus);
      }

      final int nFirstDot = s.indexOf ('.');
      if (nFirstDot < 0)
      {
        // No dot at all as in "9-Ubuntu"
        nMajor = StringParser.parseInt (s, -1);
      }
      else
        nMajor = StringParser.parseInt (s.substring (0, nFirstDot), -1);
      if (nMajor < 0)
        throw new IllegalStateException ("Failed to determine Java major version from '" + sOriginalJavaVersion + "'");

      if (nFirstDot >= 0)
      {
        final int nSecondDot = s.indexOf ('.', nFirstDot + 1);
        if (nSecondDot < 0)
        {
          // No second dot as in "9.1"
          nMinor = StringParser.parseInt (s.substring (nFirstDot + 1), -1);
        }
        else
          nMinor = StringParser.parseInt (s.substring (nFirstDot + 1, nSecondDot), -1);
        if (nMinor < 0)
          throw new IllegalStateException ("Failed to determine Java minor version from '" + sOriginalJavaVersion + "'");

        if (nSecondDot >= 0)
        {
          nMicro = StringParser.parseInt (s.substring (nSecondDot + 1), -1);
          if (nMicro < 0)
            throw new IllegalStateException ("Failed to determine Java micro version from '" + sOriginalJavaVersion + "'");
        }
      }
    }

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Java version '" + sOriginalJavaVersion + "' split into " + nMajor + "." + nMinor + (nMicro >= 0 ? "." + nMicro : ""));
    return new int [] { nMajor, nMinor, nMicro };
  }

  static
  {
    final int [] aParts = getAsUnifiedVersion (SystemProperties.getJavaVersion (), SystemProperties.getJavaRuntimeVersion ());
    JAVA_MAJOR_VERSION = aParts[0];
    JAVA_MINOR_VERSION = aParts[1];
    JAVA_MICRO_VERSION = aParts[2];
  }

  @PresentForCodeCoverage
  private static final JavaVersionHelper INSTANCE = new JavaVersionHelper ();

  private JavaVersionHelper ()
  {}

  public static boolean isAtLeast (final int nRequestedJavaMajor, final int nRequestedJavaMinor)
  {
    if (JAVA_MAJOR_VERSION > nRequestedJavaMajor)
      return true;
    if (JAVA_MAJOR_VERSION == nRequestedJavaMajor)
      return JAVA_MINOR_VERSION >= nRequestedJavaMinor;

    // JAVA_MAJOR_VERSION < nRequestedJavaMajor
    return false;
  }
}

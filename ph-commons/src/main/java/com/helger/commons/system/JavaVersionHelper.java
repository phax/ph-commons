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
package com.helger.commons.system;

import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.CGlobal;
import com.helger.commons.annotation.PresentForCodeCoverage;
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
  public static final int JAVA_MAJOR_VERSION;
  // 1.8.0_144 => 144
  public static final int JAVA_MINOR_VERSION;

  private static final Logger s_aLogger = LoggerFactory.getLogger (JavaVersionHelper.class);

  static
  {
    String sJavaVersion = SystemProperties.getJavaVersion ();
    if (!sJavaVersion.startsWith ("1."))
      throw new IllegalStateException ("Java version does not start with '1.': '" + sJavaVersion + "'");
    sJavaVersion = sJavaVersion.substring (2);
    JAVA_MAJOR_VERSION = StringParser.parseInt (sJavaVersion.substring (0, sJavaVersion.indexOf ('.')), -1);
    if (JAVA_MAJOR_VERSION < 0)
      throw new IllegalStateException ("Failed to determine Java major version from '" + sJavaVersion + "'");
    JAVA_MINOR_VERSION = StringParser.parseInt (sJavaVersion.substring (sJavaVersion.indexOf ('_') + 1), -1);
    if (JAVA_MINOR_VERSION < 0)
      throw new IllegalStateException ("Failed to determine Java minor version from '" + sJavaVersion + "'");

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Java version '" +
                       SystemProperties.getJavaVersion () +
                       "' split into MAJOR=" +
                       JAVA_MAJOR_VERSION +
                       " and MINOR=" +
                       JAVA_MINOR_VERSION);
  }

  @PresentForCodeCoverage
  private static final JavaVersionHelper s_aInstance = new JavaVersionHelper ();

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

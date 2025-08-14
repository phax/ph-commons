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
package com.helger.commons.system;

import com.helger.annotation.Nonnegative;
import com.helger.base.CGlobal;
import com.helger.base.equals.EqualsHelper;
import com.helger.commons.string.StringParser;

import jakarta.annotation.Nonnull;

/**
 * Enumeration for representing the current Java JDK version.
 *
 * @author Philip Helger
 */
public enum EJavaVersion
{
  UNKNOWN (),
  JDK_1_1 (45.3, 46.0, false),
  JDK_1_2 (46.0, 47.0, false),
  JDK_1_3 (47.0, 48.0, false),
  JDK_1_4 (48.0, 49.0, false),
  JDK_1_5 (49.0, 50.0, false),
  JDK_1_6 (50.0, 51.0, false),
  JDK_1_7 (51.0, 52.0, false),
  JDK_1_8 (52.0, 53.0, true),
  JDK_9 (53.0, 54.0, false),
  JDK_10 (54.0, 55.0, false),
  JDK_11 (55.0, 56.0, true),
  JDK_12 (56.0, 57.0, false),
  JDK_13 (57.0, 58.0, false),
  JDK_14 (58.0, 59.0, false),
  JDK_15 (59.0, 60.0, false),
  JDK_16 (60.0, 61.0, false),
  JDK_17 (61.0, 62.0, true),
  JDK_18 (62.0, 63.0, false),
  JDK_19 (63.0, 64.0, false),
  JDK_20 (64.0, 65.0, false),
  JDK_21 (65.0, 66.0, true),
  JDK_22 (66.0, 67.0, false),
  JDK_23 (67.0, 68.0, false),
  JDK_24 (68.0, 69.0, true);

  /** The current version. */
  private static final EJavaVersion INSTANCE;

  static
  {
    EJavaVersion ret = UNKNOWN;
    for (final EJavaVersion eVersion : values ())
      if (eVersion.m_bIsIt)
      {
        ret = eVersion;
        break;
      }
    INSTANCE = ret;
  }

  private final double m_dMinVersionIncl;
  private final double m_dMaxVersionExcl;
  private final boolean m_bLTS;
  private final boolean m_bIsIt;

  /**
   * Constructor for the UNKNOWN element
   */
  EJavaVersion ()
  {
    m_dMinVersionIncl = CGlobal.ILLEGAL_DOUBLE;
    m_dMaxVersionExcl = CGlobal.ILLEGAL_DOUBLE;
    m_bLTS = false;
    m_bIsIt = EqualsHelper.equals (CGlobal.ILLEGAL_DOUBLE, JavaVersionHelper.JAVA_CLASS_VERSION);
  }

  /**
   * Default constructor.
   *
   * @param dMinVersionIncl
   *        Minimum version (inclusive)
   * @param dMaxVersionExcl
   *        Maximum version (exclusive)
   * @param bLTS
   *        <code>true</code> if it is a "Long Term Support" (LTS) version, <code>false</code> if
   *        not
   */
  EJavaVersion (@Nonnegative final double dMinVersionIncl,
                @Nonnegative final double dMaxVersionExcl,
                final boolean bLTS)
  {
    m_dMinVersionIncl = dMinVersionIncl;
    m_dMaxVersionExcl = dMaxVersionExcl;
    m_bLTS = bLTS;
    m_bIsIt = isMatchingVersion (JavaVersionHelper.JAVA_CLASS_VERSION);
  }

  protected boolean isMatchingVersion (final double dVersion)
  {
    return dVersion >= m_dMinVersionIncl && dVersion < m_dMaxVersionExcl;
  }

  /**
   * @return <code>true</code> if this is the current version, <code>false</code> otherwise
   */
  public boolean isCurrentVersion ()
  {
    return m_bIsIt;
  }

  /**
   * @return <code>true</code> if this Java version is supported by the current Java Version. It is
   *         expected that all versions are backward compatible.
   */
  public boolean isSupportedVersion ()
  {
    return m_dMinVersionIncl < getCurrentVersion ().m_dMaxVersionExcl;
  }

  /**
   * Check if this java version is older or equals than the passed version
   *
   * @param eJavaVersion
   *        the Java version to be checked. May not be <code>null</code>.
   * @return <code>true</code> if this Java version is old or equal than the passed version.
   */
  public boolean isOlderOrEqualsThan (@Nonnull final EJavaVersion eJavaVersion)
  {
    return m_dMinVersionIncl <= eJavaVersion.m_dMinVersionIncl;
  }

  /**
   * Check if this java version is newer or equals than the passed version
   *
   * @param eJavaVersion
   *        the Java version to be checked. May not be <code>null</code>.
   * @return <code>true</code> if this Java version is newer or equal than the passed version.
   */
  public boolean isNewerOrEqualsThan (@Nonnull final EJavaVersion eJavaVersion)
  {
    return m_dMinVersionIncl >= eJavaVersion.m_dMinVersionIncl;
  }

  /**
   * @return <code>true</code> if it is a "Long Term Support" (LTS) version, <code>false</code> if
   *         not
   */
  public boolean isLTS ()
  {
    return m_bLTS;
  }

  /**
   * @return The current Java version. If the Java version could not be determined, {@link #UNKNOWN}
   *         is returned and never <code>null</code>.
   */
  @Nonnull
  public static EJavaVersion getCurrentVersion ()
  {
    return INSTANCE;
  }

  /**
   * Get the matching Java version from a class version.
   *
   * @param nMajor
   *        Major version number
   * @param nMinor
   *        Minor version number
   * @return {@link #UNKNOWN} if the version could not be determined.
   */
  @Nonnull
  public static EJavaVersion getFromMajorAndMinor (final int nMajor, final int nMinor)
  {
    final double dVersion = StringParser.parseBigDecimal (nMajor + "." + nMinor).doubleValue ();
    return getFromVersionNumber (dVersion);
  }

  @Nonnull
  public static EJavaVersion getFromVersionNumber (final double dVersion)
  {
    for (final EJavaVersion eVersion : values ())
      if (eVersion.isMatchingVersion (dVersion))
        return eVersion;
    return UNKNOWN;
  }
}

/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.CGlobal;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.string.StringParser;

/**
 * Enumeration for representing the current Java JDK version.
 *
 * @author Philip Helger
 */
public enum EJavaVersion
{
  UNKNOWN (),
  JDK_11 (45.3, 46.0),
  JDK_12 (46.0, 47.0),
  JDK_13 (47.0, 48.0),
  JDK_14 (48.0, 49.0),
  JDK_15 (49.0, 50.0),
  JDK_16 (50.0, 51.0),
  JDK_17 (51.0, 52.0),
  JDK_18 (52.0, 53.0);

  /** The current version. */
  private static volatile EJavaVersion s_aInstance = null;

  private final double m_dMinVersionIncl;
  private final double m_dMaxVersionExcl;
  private final boolean m_bIsIt;

  /**
   * Constructor for the UNKNOWN element
   */
  private EJavaVersion ()
  {
    m_dMinVersionIncl = CGlobal.ILLEGAL_DOUBLE;
    m_dMaxVersionExcl = CGlobal.ILLEGAL_DOUBLE;
    m_bIsIt = EqualsHelper.equals (CGlobal.ILLEGAL_DOUBLE, JavaVersionConstants.CLASS_VERSION);
  }

  /**
   * Default constructor.
   *
   * @param dMinVersionIncl
   *        Minimum version (inclusive)
   * @param dMaxVersionExcl
   *        Maximum version (exclusive)
   */
  private EJavaVersion (@Nonnegative final double dMinVersionIncl, @Nonnegative final double dMaxVersionExcl)
  {
    m_dMinVersionIncl = dMinVersionIncl;
    m_dMaxVersionExcl = dMaxVersionExcl;
    m_bIsIt = isMatchingVersion (JavaVersionConstants.CLASS_VERSION);
  }

  protected boolean isMatchingVersion (final double dVersion)
  {
    return dVersion >= m_dMinVersionIncl && dVersion < m_dMaxVersionExcl;
  }

  /**
   * @return <code>true</code> if this is the current version,
   *         <code>false</code> otherwise
   */
  public boolean isCurrentVersion ()
  {
    return m_bIsIt;
  }

  /**
   * @return <code>true</code> if this Java version is supported by the current
   *         Java Version. It is expected that all versions are backward
   *         compatible.
   */
  public boolean isSupportedVersion ()
  {
    return m_dMinVersionIncl < getCurrentVersion ().m_dMaxVersionExcl;
  }

  /**
   * Check if this java version is newer or equals than the passed version
   *
   * @param eJavaVersion
   *        the Java version to be checked. May not be <code>null</code>.
   * @return <code>true</code> if this Java version is supported by the current
   *         Java Version. It is expected that all versions are backward
   *         compatible.
   */
  public boolean isNewerOrEqualsThan (@Nonnull final EJavaVersion eJavaVersion)
  {
    return m_dMinVersionIncl >= eJavaVersion.m_dMinVersionIncl;
  }

  /**
   * @return The current Java version. If the Java version could not be
   *         determined, {@link #UNKNOWN} is returned and never
   *         <code>null</code>.
   */
  @Nonnull
  public static EJavaVersion getCurrentVersion ()
  {
    EJavaVersion ret = s_aInstance;
    if (ret == null)
    {
      // Note: double initialization doesn't matter here
      for (final EJavaVersion eVersion : values ())
        if (eVersion.m_bIsIt)
        {
          ret = eVersion;
          break;
        }
      if (ret == null)
        ret = UNKNOWN;
      s_aInstance = ret;
    }
    return ret;
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

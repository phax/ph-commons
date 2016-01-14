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
package com.helger.commons.version;

import java.io.Serializable;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.lang.IHasStringRepresentation;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;
import com.helger.commons.string.ToStringGenerator;

/**
 * This class represents a single version object. It supports 4 elements: major
 * version (integer), minor version (integer), micro version (integer) and a
 * qualifier (string).
 *
 * @author Philip Helger
 */
@Immutable
public final class Version implements Comparable <Version>, IHasStringRepresentation, Serializable
{
  /** default version if nothing is specified. */
  public static final String DEFAULT_VERSION_STRING = "0";

  /** Default value for printing zero elements in getAsString */
  public static final boolean DEFAULT_PRINT_ZERO_ELEMENTS = false;

  /** major version. */
  private final int m_nMajor;

  /** minor version. */
  private final int m_nMinor;

  /** micro version. */
  private final int m_nMicro;

  /** version build qualifier. */
  private final String m_sQualifier;

  /**
   * Create a new version with major version only.
   *
   * @param nMajor
   *        major version
   * @throws IllegalArgumentException
   *         if the parameter is &lt; 0
   */
  public Version (@Nonnegative final int nMajor)
  {
    this (nMajor, 0, 0, null);
  }

  /**
   * Create a new version with major and minor version only.
   *
   * @param nMajor
   *        major version
   * @param nMinor
   *        minor version
   * @throws IllegalArgumentException
   *         if any of the parameters is &lt; 0
   */
  public Version (@Nonnegative final int nMajor, @Nonnegative final int nMinor)
  {
    this (nMajor, nMinor, 0, null);
  }

  /**
   * Create a new version with major, minor and micro version number. The
   * qualifier remains null.
   *
   * @param nMajor
   *        major version
   * @param nMinor
   *        minor version
   * @param nMicro
   *        micro version
   * @throws IllegalArgumentException
   *         if any of the parameters is &lt; 0
   */
  public Version (@Nonnegative final int nMajor, @Nonnegative final int nMinor, @Nonnegative final int nMicro)
  {
    this (nMajor, nMinor, nMicro, null);
  }

  /**
   * Create a new version with 3 integer values and a qualifier.
   *
   * @param nMajor
   *        major version
   * @param nMinor
   *        minor version
   * @param nMicro
   *        micro version
   * @param sQualifier
   *        the version qualifier - may be null. If a qualifier is supplied, it
   *        may neither contain the "." or the "," character since they are used
   *        to determine the fields of a version and to separate 2 versions in a
   *        VersionRange.
   * @throws IllegalArgumentException
   *         if any of the numeric parameters is &lt; 0 or if the qualifier
   *         contains a forbidden character
   */
  public Version (@Nonnegative final int nMajor,
                  @Nonnegative final int nMinor,
                  @Nonnegative final int nMicro,
                  @Nullable final String sQualifier)
  {
    ValueEnforcer.isGE0 (nMajor, "Major");
    ValueEnforcer.isGE0 (nMinor, "Minor");
    ValueEnforcer.isGE0 (nMicro, "Micro");
    m_nMajor = nMajor;
    m_nMinor = nMinor;
    m_nMicro = nMicro;
    m_sQualifier = StringHelper.hasNoText (sQualifier) ? null : sQualifier;
  }

  /**
   * Construct a version object from a string.<br>
   * EBNF:<br>
   * version ::= major( '.' minor ( '.' micro ( ( '.' | '-' ) qualifier )? )? )?
   * <br>
   * major ::= number<br>
   * minor ::= number<br>
   * micro ::= number<br>
   * qualifier ::= .+
   *
   * @param sVersionString
   *        the version string to be interpreted as a version
   * @throws IllegalArgumentException
   *         if any of the parameters is &lt; 0
   */
  public Version (@Nullable final String sVersionString)
  {
    // For backward compatibility the old version should be used
    this (sVersionString, true);
  }

  @Nonnull
  @ReturnsMutableCopy
  private static String [] _extSplit (@Nonnull final String s)
  {
    final String [] aDotParts = StringHelper.getExplodedArray ('.', s, 2);
    if (aDotParts.length == 2)
    {
      // Dots always take precedence
      return aDotParts;
    }

    if (StringParser.isInt (aDotParts[0]))
    {
      // If it is numeric, use the dot parts anyway (e.g. for "5" or "-1")
      return aDotParts;
    }

    final String [] aDashParts = StringHelper.getExplodedArray ('-', s, 2);
    if (aDashParts.length == 1)
    {
      // Neither dot nor dash present
      return aDotParts;
    }

    // More matches for dash split! (e.g. "0-RC1")
    return aDashParts;
  }

  /**
   * Construct a version object from a string.
   *
   * @param sVersionString
   *        the version string to be interpreted as a version
   * @param bOldVersion
   *        <code>true</code> to use the old version to parse a string, meaning
   *        splitting only by dot; or <code>false</code> to indicate that the
   *        more complex parsing should be used.
   * @throws IllegalArgumentException
   *         if any of the parameters is &lt; 0
   */
  public Version (@Nullable final String sVersionString, final boolean bOldVersion)
  {
    String s = sVersionString == null ? "" : sVersionString.trim ();
    if (s.length () == 0)
      s = DEFAULT_VERSION_STRING;

    if (bOldVersion)
    {
      // old version

      // split each token
      final String [] aParts = StringHelper.getExplodedArray ('.', s, 4);
      if (aParts.length > 0)
        m_nMajor = StringParser.parseInt (aParts[0], 0);
      else
        m_nMajor = 0;
      if (aParts.length > 1)
        m_nMinor = StringParser.parseInt (aParts[1], 0);
      else
        m_nMinor = 0;
      if (aParts.length > 2)
        m_nMicro = StringParser.parseInt (aParts[2], 0);
      else
        m_nMicro = 0;
      if (aParts.length > 3)
        m_sQualifier = StringHelper.hasNoText (aParts[3]) ? null : aParts[3];
      else
        m_sQualifier = null;
    }
    else
    {
      // Complex parsing
      Integer aMajor;
      Integer aMinor = null;
      Integer aMicro = null;
      String sQualifier = null;
      boolean bDone = false;

      // Extract major version number
      String [] aParts = _extSplit (s);
      aMajor = StringParser.parseIntObj (aParts[0]);
      if (aMajor == null && StringHelper.hasText (aParts[0]))
      {
        // Major version is not numeric, so everything is the qualifier
        sQualifier = s;
        bDone = true;
      }

      String sRest = !bDone && aParts.length > 1 ? aParts[1] : null;
      if (StringHelper.hasText (sRest))
      {
        // Parse minor version number part
        aParts = _extSplit (sRest);
        aMinor = StringParser.parseIntObj (aParts[0]);
        if (aMinor == null && StringHelper.hasText (aParts[0]))
        {
          // Minor version is not numeric, so everything is the qualifier
          sQualifier = sRest;
          bDone = true;
        }

        sRest = !bDone && aParts.length > 1 ? aParts[1] : null;
        if (StringHelper.hasText (sRest))
        {
          // Parse micro version number part
          aParts = _extSplit (sRest);
          aMicro = StringParser.parseIntObj (aParts[0]);
          if (aMicro == null && StringHelper.hasText (aParts[0]))
          {
            // Micro version is not numeric, so everything is the qualifier
            sQualifier = sRest;
            bDone = true;
          }

          if (!bDone && aParts.length > 1)
          {
            // Some qualifier left!
            sQualifier = aParts[1];
          }
        }
      }

      m_nMajor = aMajor == null ? 0 : aMajor.intValue ();
      m_nMinor = aMinor == null ? 0 : aMinor.intValue ();
      m_nMicro = aMicro == null ? 0 : aMicro.intValue ();
      m_sQualifier = StringHelper.hasNoText (sQualifier) ? null : sQualifier;
    }

    // check consistency
    ValueEnforcer.isGE0 (m_nMajor, "Major");
    ValueEnforcer.isGE0 (m_nMinor, "Minor");
    ValueEnforcer.isGE0 (m_nMicro, "Micro");
  }

  @Nonnegative
  public int getMajor ()
  {
    return m_nMajor;
  }

  @Nonnegative
  public int getMinor ()
  {
    return m_nMinor;
  }

  @Nonnegative
  public int getMicro ()
  {
    return m_nMicro;
  }

  @Nullable
  public String getQualifier ()
  {
    return m_sQualifier;
  }

  /**
   * Compares two Version objects.
   *
   * @param rhs
   *        the version to compare to
   * @return &lt; 0 if this is less than rhs; &gt; 0 if this is greater than
   *         rhs, and 0 if they are equal.
   * @throws IllegalArgumentException
   *         if the parameter is null
   */
  public int compareTo (@Nonnull final Version rhs)
  {
    ValueEnforcer.notNull (rhs, "Rhs");

    // compare major version
    int ret = m_nMajor - rhs.m_nMajor;
    if (ret == 0)
    {
      // compare minor version
      ret = m_nMinor - rhs.m_nMinor;
      if (ret == 0)
      {
        // compare micro version
        ret = m_nMicro - rhs.m_nMicro;
        if (ret == 0)
        {
          // check qualifier
          if (m_sQualifier != null)
          {
            if (rhs.m_sQualifier != null)
            {
              ret = m_sQualifier.compareTo (rhs.m_sQualifier);

              // convert to -1/0/+1
              if (ret < 0)
                ret = -1;
              else
                if (ret > 0)
                  ret = +1;
            }
            else
              ret = 1;
          }
          else
            if (rhs.m_sQualifier != null)
            {
              // only this qualifier == null
              ret = -1;
            }
            else
            {
              // both qualifier are null
              ret = 0;
            }
        }
      }
    }
    return ret;
  }

  public boolean isGreaterThan (@Nonnull final Version aVersion)
  {
    return compareTo (aVersion) > 0;
  }

  public boolean isGreaterOrEqualThan (@Nonnull final Version aVersion)
  {
    return compareTo (aVersion) >= 0;
  }

  public boolean isLowerThan (@Nonnull final Version aVersion)
  {
    return compareTo (aVersion) < 0;
  }

  public boolean isLowerOrEqualThan (@Nonnull final Version aVersion)
  {
    return compareTo (aVersion) <= 0;
  }

  @Nonnull
  public String getAsString ()
  {
    return getAsString (DEFAULT_PRINT_ZERO_ELEMENTS);
  }

  /**
   * Get the string representation of the version number.
   *
   * @param bPrintZeroElements
   *        If <code>true</code> than trailing zeroes are printed, otherwise
   *        printed zeroes are not printed.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public String getAsString (final boolean bPrintZeroElements)
  {
    return getAsString (bPrintZeroElements, false);
  }

  /**
   * Get the string representation of the version number.
   *
   * @param bPrintZeroElements
   *        If <code>true</code> than trailing zeroes are printed, otherwise
   *        printed zeroes are not printed.
   * @param bPrintAtLeastMajorAndMinor
   *        <code>true</code> if major and minor part should always be printed,
   *        independent of their value
   * @return Never <code>null</code>.
   */
  @Nonnull
  public String getAsString (final boolean bPrintZeroElements, final boolean bPrintAtLeastMajorAndMinor)
  {
    // Build from back to front
    final StringBuilder aSB = new StringBuilder (m_sQualifier != null ? m_sQualifier : "");
    if (m_nMicro > 0 || aSB.length () > 0 || bPrintZeroElements)
    {
      // Micro version
      if (aSB.length () > 0)
        aSB.insert (0, '.');
      aSB.insert (0, m_nMicro);
    }
    if (bPrintAtLeastMajorAndMinor || m_nMinor > 0 || aSB.length () > 0 || bPrintZeroElements)
    {
      // Minor version
      if (aSB.length () > 0)
        aSB.insert (0, '.');
      aSB.insert (0, m_nMinor);
    }
    if (bPrintAtLeastMajorAndMinor || m_nMajor > 0 || aSB.length () > 0 || bPrintZeroElements)
    {
      // Major version
      if (aSB.length () > 0)
        aSB.insert (0, '.');
      aSB.insert (0, m_nMajor);
    }
    return aSB.length () > 0 ? aSB.toString () : DEFAULT_VERSION_STRING;
  }

  /**
   * Get the string representation of the version number but only major and
   * minor version number.
   *
   * @return Never <code>null</code>.
   */
  @Nonnull
  @Nonempty
  public String getAsStringMajorMinor ()
  {
    return m_nMajor + "." + m_nMinor;
  }

  /**
   * Get the string representation of the version number but only major and
   * minor and micro version number.
   *
   * @return Never <code>null</code>.
   */
  @Nonnull
  @Nonempty
  public String getAsStringMajorMinorMicro ()
  {
    return m_nMajor + "." + m_nMinor + "." + m_nMicro;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final Version rhs = (Version) o;
    return m_nMajor == rhs.m_nMajor &&
           m_nMinor == rhs.m_nMinor &&
           m_nMicro == rhs.m_nMicro &&
           EqualsHelper.equals (m_sQualifier, rhs.m_sQualifier);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_nMajor)
                                       .append (m_nMinor)
                                       .append (m_nMicro)
                                       .append (m_sQualifier)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("major", m_nMajor)
                                       .append ("minor", m_nMinor)
                                       .append ("micro", m_nMicro)
                                       .appendIfNotNull ("qualifier", m_sQualifier)
                                       .toString ();
  }
}

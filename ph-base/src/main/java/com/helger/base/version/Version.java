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
package com.helger.base.version;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.compare.IComparable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.string.StringHelper;
import com.helger.base.string.StringParser;
import com.helger.base.tostring.ToStringGenerator;

/**
 * This class represents a single version object. It supports 4 elements: major version (integer),
 * minor version (integer), micro version (integer) and a qualifier (string).
 *
 * @author Philip Helger
 */
@Immutable
public class Version implements IComparable <Version>
{
  /** default version if nothing is specified. */
  public static final String DEFAULT_VERSION_STRING = "0";

  public static final Version DEFAULT_VERSION = new Version (0, 0, 0, null);

  /** Default value for printing zero elements in getAsString */
  public static final boolean DEFAULT_PRINT_ZERO_ELEMENTS = false;

  /**
   * The character that separates the qualifier from the numeric version parts in the strict layout.
   *
   * @since v12.3.5
   */
  public static final char STRICT_QUALIFIER_SEPARATOR = '-';

  /**
   * The character that separates the numeric version parts from each other.
   *
   * @since v12.3.5
   */
  public static final char NUMERIC_PART_SEPARATOR = '.';

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
   * Create a new version with major, minor and micro version number. The qualifier remains null.
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
   *        the version qualifier - may be null. If a qualifier is supplied, it may neither contain
   *        the "." or the "," character since they are used to determine the fields of a version
   *        and to separate 2 versions in a VersionRange.
   * @throws IllegalArgumentException
   *         if any of the numeric parameters is &lt; 0 or if the qualifier contains a forbidden
   *         character
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
    m_sQualifier = StringHelper.isEmpty (sQualifier) ? null : sQualifier;
  }

  /**
   * @return The major version number. Always &ge; 0.
   */
  @Nonnegative
  public final int getMajor ()
  {
    return m_nMajor;
  }

  /**
   * @return The minor version number. Always &ge; 0.
   */
  @Nonnegative
  public final int getMinor ()
  {
    return m_nMinor;
  }

  /**
   * @return The micro version number. Always &ge; 0.
   */
  @Nonnegative
  public final int getMicro ()
  {
    return m_nMicro;
  }

  /**
   * @return The version qualifier string. May be <code>null</code>.
   */
  @Nullable
  public final String getQualifier ()
  {
    return m_sQualifier;
  }

  /**
   * @return <code>true</code> if a qualifier is present, <code>false</code> otherwise.
   */
  public final boolean hasQualifier ()
  {
    return StringHelper.isNotEmpty (m_sQualifier);
  }

  /**
   * Compares two Version objects.
   *
   * @param rhs
   *        the version to compare to
   * @return &lt; 0 if this is less than rhs; &gt; 0 if this is greater than rhs, and 0 if they are
   *         equal.
   * @throws IllegalArgumentException
   *         if the parameter is null
   */
  public int compareTo (@NonNull final Version rhs)
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

  /**
   * Get the string representation of the version number using the default setting for printing zero
   * elements.
   *
   * @return Never <code>null</code>.
   */
  @NonNull
  public String getAsString ()
  {
    return getAsString (DEFAULT_PRINT_ZERO_ELEMENTS);
  }

  /**
   * Get the string representation of the version number.
   *
   * @param bPrintZeroElements
   *        If <code>true</code> than trailing zeroes are printed, otherwise printed zeroes are not
   *        printed.
   * @return Never <code>null</code>.
   */
  @NonNull
  public String getAsString (final boolean bPrintZeroElements)
  {
    return getAsString (bPrintZeroElements, false);
  }

  /**
   * Get the string representation of the version number.
   *
   * @param bPrintZeroElements
   *        If <code>true</code> than trailing zeroes are printed, otherwise printed zeroes are not
   *        printed.
   * @param bPrintAtLeastMajorAndMinor
   *        <code>true</code> if major and minor part should always be printed, independent of their
   *        value
   * @return Never <code>null</code>.
   */
  @NonNull
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
   * Get the string representation of the version number using the strict layout
   * <code>major[.minor[.micro]][-qualifier]</code>. Trailing zero elements are omitted, but the
   * major version is always printed. The qualifier - if present - is always separated with a
   * <code>-</code> character.<br>
   * The result of this method can always be read back with {@link #parseStrictOrNull(String)},
   * contrary to the combination of {@link #getAsString()} and {@link #parse(String)}.
   *
   * @return Never <code>null</code> nor empty.
   * @see #parseStrictOrNull(String)
   * @since v12.3.5
   */
  @NonNull
  @Nonempty
  public String getAsStringStrict ()
  {
    final StringBuilder aSB = new StringBuilder ().append (m_nMajor);
    if (m_nMinor > 0 || m_nMicro > 0)
    {
      aSB.append ('.').append (m_nMinor);
      if (m_nMicro > 0)
        aSB.append ('.').append (m_nMicro);
    }
    if (StringHelper.isNotEmpty (m_sQualifier))
      aSB.append (STRICT_QUALIFIER_SEPARATOR).append (m_sQualifier);
    return aSB.toString ();
  }

  /**
   * Get the string representation of the version number but only major and minor version number.
   *
   * @return Never <code>null</code>.
   */
  @NonNull
  @Nonempty
  public String getAsStringMajorMinor ()
  {
    return m_nMajor + "." + m_nMinor;
  }

  /**
   * Get the string representation of the version number but only major and minor and micro version
   * number.
   *
   * @return Never <code>null</code>.
   */
  @NonNull
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
                                       .getToString ();
  }

  @NonNull
  @ReturnsMutableCopy
  private static String [] _extSplit (@NonNull final String s)
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
   * Construct a version object from a string.<br>
   * EBNF:<br>
   * version ::= major( '.' minor ( '.' micro ( ( '.' | '-' ) qualifier )? )? )? <br>
   * major ::= number<br>
   * minor ::= number<br>
   * micro ::= number<br>
   * qualifier ::= .+
   *
   * @param sVersionString
   *        the version string to be interpreted as a version
   * @return The parsed {@link Version} object.
   * @throws IllegalArgumentException
   *         if any of the parameters is &lt; 0
   */
  @NonNull
  public static Version parse (@Nullable final String sVersionString)
  {
    final String s = sVersionString == null ? "" : sVersionString.trim ();
    if (s.length () == 0)
      return DEFAULT_VERSION;

    // Complex parsing
    Integer aMajor;
    Integer aMinor = null;
    Integer aMicro = null;
    String sQualifier;
    boolean bDone = false;

    // Extract major version number
    String [] aParts = _extSplit (s);
    aMajor = StringParser.parseIntObj (aParts[0]);
    if (aMajor == null && StringHelper.isNotEmpty (aParts[0]))
    {
      // Major version is not numeric, so everything is the qualifier
      sQualifier = s;
      bDone = true;
    }
    else
      sQualifier = null;

    String sRest = !bDone && aParts.length > 1 ? aParts[1] : null;
    final String sStr = sRest;
    if (StringHelper.isNotEmpty (sStr))
    {
      // Parse minor version number part
      aParts = _extSplit (sRest);
      aMinor = StringParser.parseIntObj (aParts[0]);
      if (aMinor == null && StringHelper.isNotEmpty (aParts[0]))
      {
        // Minor version is not numeric, so everything is the qualifier
        sQualifier = sRest;
        bDone = true;
      }

      sRest = !bDone && aParts.length > 1 ? aParts[1] : null;
      final String sStr1 = sRest;
      if (StringHelper.isNotEmpty (sStr1))
      {
        // Parse micro version number part
        aParts = _extSplit (sRest);
        aMicro = StringParser.parseIntObj (aParts[0]);
        if (aMicro == null && StringHelper.isNotEmpty (aParts[0]))
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

    final int nMajor = aMajor == null ? 0 : aMajor.intValue ();
    final int nMinor = aMinor == null ? 0 : aMinor.intValue ();
    final int nMicro = aMicro == null ? 0 : aMicro.intValue ();
    sQualifier = StringHelper.isEmpty (sQualifier) ? null : sQualifier;

    return new Version (nMajor, nMinor, nMicro, sQualifier);
  }

  /**
   * Construct a version object from a string.<br>
   * EBNF:<br>
   * version ::= major( '.' minor ( '.' micro ( '.' qualifier )? )? )? <br>
   * major ::= number<br>
   * minor ::= number<br>
   * micro ::= number<br>
   * qualifier ::= .+
   *
   * @param sVersionString
   *        the version string to be interpreted as a version
   * @return The parsed {@link Version} object.
   * @throws IllegalArgumentException
   *         if any of the parameters is &lt; 0
   * @since v10.0.1
   */
  @NonNull
  public static Version parseDotOnly (@Nullable final String sVersionString)
  {
    final String s = sVersionString == null ? "" : sVersionString.trim ();
    if (s.length () == 0)
      return DEFAULT_VERSION;

    final int nMajor;
    final int nMinor;
    final int nMicro;
    final String sQualifier;

    // split each token
    final String [] aParts = StringHelper.getExplodedArray ('.', s, 4);
    if (aParts.length > 0)
      nMajor = StringParser.parseInt (aParts[0], 0);
    else
      nMajor = 0;
    if (aParts.length > 1)
      nMinor = StringParser.parseInt (aParts[1], 0);
    else
      nMinor = 0;
    if (aParts.length > 2)
      nMicro = StringParser.parseInt (aParts[2], 0);
    else
      nMicro = 0;
    if (aParts.length > 3)
      sQualifier = StringHelper.isEmpty (aParts[3]) ? null : aParts[3];
    else
      sQualifier = null;

    return new Version (nMajor, nMinor, nMicro, sQualifier);
  }

  /**
   * Check if the provided string is a numeric version part, meaning it consists of digits only and
   * has no superfluous leading zeroes. The latter is required so that the parsing is the exact
   * inverse of {@link #getAsStringStrict()}.
   *
   * @param s
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if it is a valid numeric version part.
   */
  private static boolean _isStrictNumericPart (@Nullable final String s)
  {
    final int nLen = StringHelper.getLength (s);
    if (nLen == 0)
      return false;

    // No leading zero, except for "0" itself
    if (nLen > 1 && s.charAt (0) == '0')
      return false;

    for (int i = 0; i < nLen; ++i)
      if (!Character.isDigit (s.charAt (i)))
        return false;
    return true;
  }

  /**
   * Construct a version object from a string using a strict layout. This is the exact inverse of
   * {@link #getAsStringStrict()}.<br>
   * EBNF:<br>
   * version ::= major ( '.' minor ( '.' micro )? )? ( '-' qualifier )? <br>
   * major ::= number<br>
   * minor ::= number<br>
   * micro ::= number<br>
   * qualifier ::= .+
   * <p>
   * Contrary to {@link #parse(String)} the qualifier is always introduced by the first
   * <code>-</code> character. That way a purely numeric qualifier is retained as such, whereas
   * {@link #parse(String)} takes it as the micro version number instead - e.g. <code>1.4-03</code>
   * is parsed to <code>1.4.0-03</code> by this method but to <code>1.4.3</code> by
   * {@link #parse(String)}.
   * </p>
   * <p>
   * Additionally this method is strict about its input and returns <code>null</code> instead of
   * silently falling back to a default value. Numeric version parts must not have superfluous
   * leading zeroes, so that <code>1.04</code> is rejected instead of being read as
   * <code>1.4</code>. Trailing zero elements are accepted though, so <code>1</code>,
   * <code>1.0</code> and <code>1.0.0</code> all lead to the same version.
   * </p>
   *
   * @param sVersionString
   *        the version string to be interpreted as a version. May be <code>null</code>.
   * @return <code>null</code> if the provided string does not match the layout above.
   * @see #getAsStringStrict()
   * @since v12.3.5
   */
  @Nullable
  public static Version parseStrictOrNull (@Nullable final String sVersionString)
  {
    if (sVersionString == null)
      return null;

    final String s = sVersionString.trim ();
    if (s.length () == 0)
      return null;

    // Split of the qualifier at the first separator - the qualifier itself may
    // contain further separators
    final String sNumbers;
    final String sQualifier;
    final int nSepIdx = s.indexOf (STRICT_QUALIFIER_SEPARATOR);
    if (nSepIdx < 0)
    {
      sNumbers = s;
      sQualifier = null;
    }
    else
    {
      sNumbers = s.substring (0, nSepIdx);
      sQualifier = s.substring (nSepIdx + 1);
      // Neither "1.2-" nor "-bla" are valid
      if (sNumbers.length () == 0 || sQualifier.length () == 0)
        return null;
    }

    final String [] aParts = StringHelper.getExplodedArray (NUMERIC_PART_SEPARATOR, sNumbers);
    if (aParts.length == 0 || aParts.length > 3)
      return null;

    for (final String sPart : aParts)
      if (!_isStrictNumericPart (sPart))
        return null;

    // Returns null on overflow, so that no negative number can arise
    final Integer aMajor = StringParser.parseIntObj (aParts[0]);
    final Integer aMinor = aParts.length > 1 ? StringParser.parseIntObj (aParts[1]) : Integer.valueOf (0);
    final Integer aMicro = aParts.length > 2 ? StringParser.parseIntObj (aParts[2]) : Integer.valueOf (0);
    if (aMajor == null || aMinor == null || aMicro == null)
      return null;

    // Trailing zero elements are accepted but not canonical, so "1", "1.0" and
    // "1.0.0" all lead to the same Version
    return new Version (aMajor.intValue (), aMinor.intValue (), aMicro.intValue (), sQualifier);
  }
}

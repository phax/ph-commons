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
package com.helger.commons.regex;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.RegEx;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.DevelopersNote;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * This class encapsulates a String and a set of options to be used in Pattern
 * compilation
 *
 * @author Philip Helger
 */
@ThreadSafe
@DevelopersNote ("The mutable m_aHashCode does not contradict thread safety")
public final class RegExPattern implements Serializable
{
  private static final AtomicBoolean s_aCheckConsistencyEnabled = new AtomicBoolean (false);

  private final String m_sRegEx;
  private final int m_nOptions;
  private Pattern m_aPattern;

  // Status vars
  private Integer m_aHashCode;

  public static boolean areDebugConsistencyChecksEnabled ()
  {
    return s_aCheckConsistencyEnabled.get ();
  }

  public static void enableDebugConsistencyChecks (final boolean bEnable)
  {
    s_aCheckConsistencyEnabled.set (bEnable);
  }

  public static void checkPatternConsistency (@Nonnull @RegEx final String sRegEx) throws IllegalArgumentException
  {
    // Check if a '$' is escaped if no digits follow
    int nIndex = 0;
    while (nIndex >= 0)
    {
      nIndex = sRegEx.indexOf ('$', nIndex);
      if (nIndex != -1)
      {
        if (nIndex == sRegEx.length () - 1)
        {
          // '$' at end of String is OK!
        }
        else
          // Is the "$" followed by an int (would indicate a replacement group)
          if (!Character.isDigit (sRegEx.charAt (nIndex + 1)))
          {
            if (nIndex + 1 < sRegEx.length () && sRegEx.charAt (nIndex + 1) == ')')
            {
              // "$" is the last char in a group "(...$)"
            }
            else
              if (nIndex > 0 && sRegEx.charAt (nIndex - 1) == '\\')
              {
                // '$' is quoted
              }
              else
                throw new IllegalArgumentException ("The passed regex '" +
                                                    sRegEx +
                                                    "' contains an unquoted '$' sign at index " +
                                                    nIndex +
                                                    "!");
          }

        // Move beyond the current $
        nIndex++;
      }
    }
  }

  public RegExPattern (@Nonnull @Nonempty @RegEx final String sRegEx) throws IllegalArgumentException
  {
    // Default: no options
    this (sRegEx, 0);
  }

  public RegExPattern (@Nonnull @Nonempty @RegEx final String sRegEx,
                       @Nonnegative final int nOptions) throws IllegalArgumentException
  {
    ValueEnforcer.notEmpty (sRegEx, "RegEx");
    ValueEnforcer.isGE0 (nOptions, "Options");
    m_sRegEx = sRegEx;
    m_nOptions = nOptions;

    if (areDebugConsistencyChecksEnabled ())
      checkPatternConsistency (sRegEx);

    try
    {
      m_aPattern = Pattern.compile (m_sRegEx, m_nOptions);
    }
    catch (final PatternSyntaxException ex)
    {
      throw new IllegalArgumentException ("Regular expression '" +
                                          m_sRegEx +
                                          "' is illegal" +
                                          (m_nOptions == 0 ? "" : " with options " + m_nOptions),
                                          ex);
    }
  }

  /**
   * @return The source regular expression string. Neither <code>null</code> nor
   *         empty.
   */
  @Nonnull
  @Nonempty
  @RegEx
  public String getRegEx ()
  {
    return m_sRegEx;
  }

  /**
   * @return The RegEx options provided in the constructor. 0 means no options.
   */
  @Nonnegative
  public int getOptions ()
  {
    return m_nOptions;
  }

  /**
   * @return The precompiled pattern. Never <code>null</code>.
   */
  @Nonnull
  public Pattern getAsPattern ()
  {
    return m_aPattern;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final RegExPattern rhs = (RegExPattern) o;
    // m_aPattern is a state variable
    return m_sRegEx.equals (rhs.m_sRegEx) && m_nOptions == rhs.m_nOptions;
  }

  @Override
  public int hashCode ()
  {
    // m_aPattern is a state variable
    if (m_aHashCode == null)
      m_aHashCode = new HashCodeGenerator (this).append (m_sRegEx).append (m_nOptions).getHashCodeObj ();
    return m_aHashCode.intValue ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("regex", m_sRegEx).append ("options", m_nOptions).toString ();
  }
}

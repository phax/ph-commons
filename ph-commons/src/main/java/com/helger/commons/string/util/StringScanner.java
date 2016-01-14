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
package com.helger.commons.string.util;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

/**
 * Simple string scanner.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class StringScanner
{
  private final String m_sInput;
  private final int m_nMaxIndex;
  private int m_nCurIndex = 0;

  public StringScanner (@Nonnull final String sInput)
  {
    m_sInput = ValueEnforcer.notNull (sInput, "Input");
    m_nMaxIndex = sInput.length ();
  }

  @Nonnegative
  public int getCurrentIndex ()
  {
    return m_nCurIndex;
  }

  @Nonnegative
  public int getRemainingChars ()
  {
    return m_nMaxIndex - m_nCurIndex;
  }

  @Nonnull
  public StringScanner skipWhitespaces ()
  {
    while (m_nCurIndex < m_nMaxIndex && Character.isWhitespace (getCurrentChar ()))
      m_nCurIndex++;
    return this;
  }

  @Nonnull
  public StringScanner skipbackWhitespaces ()
  {
    while (m_nCurIndex > 0 && m_nCurIndex < m_nMaxIndex && Character.isWhitespace (getCurrentChar ()))
      m_nCurIndex--;
    return this;
  }

  @Nonnull
  public StringScanner skip (final int nCount)
  {
    m_nCurIndex = Math.min (Math.max (m_nCurIndex + nCount, 0), m_nMaxIndex);
    return this;
  }

  public int findFirstIndex (@Nonnull final char... aChars)
  {
    int ret = -1;
    for (final char c : aChars)
    {
      final int nIndex = m_sInput.indexOf (c, m_nCurIndex);
      if (nIndex != -1 && (ret == -1 || nIndex < ret))
        ret = nIndex;
    }
    return ret;
  }

  public char getCharAtIndex (final int nIndex)
  {
    try
    {
      return m_sInput.charAt (nIndex);
    }
    catch (final StringIndexOutOfBoundsException ex)
    {
      throw new IllegalArgumentException ("At end of string: position " + nIndex + " of " + m_nMaxIndex);
    }
  }

  public char getCurrentChar ()
  {
    return getCharAtIndex (m_nCurIndex);
  }

  public boolean isCurrentChar (final char c)
  {
    return getCurrentChar () == c;
  }

  @Nonnull
  public StringScanner setIndex (@Nonnegative final int nIndex)
  {
    ValueEnforcer.isBetweenInclusive (nIndex, "Index", 0, m_nMaxIndex);
    m_nCurIndex = nIndex;
    return this;
  }

  /**
   * Get all remaining chars, and set the index to the end of the input string
   *
   * @return The remaining string. May not be <code>null</code> but may be
   *         empty.
   */
  @Nonnull
  public String getRest ()
  {
    final String ret = m_sInput.substring (m_nCurIndex);
    m_nCurIndex = m_nMaxIndex;
    return ret;
  }

  @Nonnull
  public String getUntilIndex (final int nEndIndex)
  {
    final String ret = m_sInput.substring (m_nCurIndex, nEndIndex);
    setIndex (nEndIndex);
    return ret;
  }

  @Nonnull
  public String getUntilWhiteSpace ()
  {
    final int nStart = m_nCurIndex;
    while (m_nCurIndex < m_nMaxIndex && !Character.isWhitespace (getCurrentChar ()))
      ++m_nCurIndex;
    return m_sInput.substring (nStart, m_nCurIndex);
  }

  /**
   * Get the string until the specified end character, but excluding the end
   * character.
   *
   * @param cEndExcl
   *        The end character to search.
   * @return A non-<code>null</code> string with all characters from the current
   *         index until the end character, but not including the end character.
   */
  @Nonnull
  public String getUntil (final char cEndExcl)
  {
    final int nStart = m_nCurIndex;
    while (m_nCurIndex < m_nMaxIndex && getCurrentChar () != cEndExcl)
      ++m_nCurIndex;
    return m_sInput.substring (nStart, m_nCurIndex);
  }

  @Nonnull
  public String getUntilBalanced (final int nStartLevel, final char cOpenChar, final char cCloseChar)
  {
    final int nStart = m_nCurIndex;
    int nLevel = nStartLevel;
    while (m_nCurIndex < m_nMaxIndex && nLevel > 0)
    {
      final char aChar = getCurrentChar ();
      if (aChar == cOpenChar)
        ++nLevel;
      else
        if (aChar == cCloseChar)
          --nLevel;
      ++m_nCurIndex;
    }
    if (m_nCurIndex == nStart)
      return "";
    return m_sInput.substring (nStart, m_nCurIndex - 1);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("input", m_sInput)
                                       .append ("maxIndex", m_nMaxIndex)
                                       .append ("curIndex", m_nCurIndex)
                                       .toString ();
  }
}

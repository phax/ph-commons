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
package com.helger.base.string;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;

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

  /**
   * Constructor.
   *
   * @param sInput
   *        The input string to scan. May not be <code>null</code>.
   */
  public StringScanner (@NonNull final String sInput)
  {
    m_sInput = ValueEnforcer.notNull (sInput, "Input");
    m_nMaxIndex = sInput.length ();
  }

  /**
   * @return The current scan position index. Always &ge; 0.
   */
  @Nonnegative
  public int getCurrentIndex ()
  {
    return m_nCurIndex;
  }

  /**
   * @return The number of remaining characters from the current position to the end. Always &ge; 0.
   */
  @Nonnegative
  public int getRemainingChars ()
  {
    return m_nMaxIndex - m_nCurIndex;
  }

  /**
   * Skip all whitespace characters from the current position forward.
   *
   * @return this for chaining.
   */
  @NonNull
  public StringScanner skipWhitespaces ()
  {
    while (m_nCurIndex < m_nMaxIndex && Character.isWhitespace (getCurrentChar ()))
      m_nCurIndex++;
    return this;
  }

  /**
   * Skip all whitespace characters from the current position backward.
   *
   * @return this for chaining.
   */
  @NonNull
  public StringScanner skipbackWhitespaces ()
  {
    while (m_nCurIndex > 0 && m_nCurIndex < m_nMaxIndex && Character.isWhitespace (getCurrentChar ()))
      m_nCurIndex--;
    return this;
  }

  /**
   * Skip the specified number of characters forward or backward.
   *
   * @param nCount
   *        The number of characters to skip. May be negative to go backward.
   * @return this for chaining.
   */
  @NonNull
  public StringScanner skip (final int nCount)
  {
    m_nCurIndex = Math.min (Math.max (m_nCurIndex + nCount, 0), m_nMaxIndex);
    return this;
  }

  /**
   * Find the first index of any of the specified characters, starting from the current position.
   *
   * @param aChars
   *        The characters to search for.
   * @return The index of the first match, or -1 if none of the characters was found.
   */
  public int findFirstIndex (final char... aChars)
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

  /**
   * Get the character at the specified absolute index.
   *
   * @param nIndex
   *        The absolute index in the input string.
   * @return The character at the specified index.
   * @throws IllegalArgumentException
   *         if the index is out of bounds.
   */
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

  /**
   * @return The character at the current scan position.
   */
  public char getCurrentChar ()
  {
    return getCharAtIndex (m_nCurIndex);
  }

  /**
   * Check if the current character matches the specified character.
   *
   * @param c
   *        The character to compare against.
   * @return <code>true</code> if the current character equals the specified character.
   */
  public boolean isCurrentChar (final char c)
  {
    return getCurrentChar () == c;
  }

  /**
   * Set the current scan position to the specified index.
   *
   * @param nIndex
   *        The new index. Must be between 0 and the length of the input string (inclusive).
   * @return this for chaining.
   */
  @NonNull
  public StringScanner setIndex (@Nonnegative final int nIndex)
  {
    ValueEnforcer.isBetweenInclusive (nIndex, "Index", 0, m_nMaxIndex);
    m_nCurIndex = nIndex;
    return this;
  }

  /**
   * Get all remaining chars, and set the index to the end of the input string
   *
   * @return The remaining string. May not be <code>null</code> but may be empty.
   */
  @NonNull
  public String getRest ()
  {
    final String ret = m_sInput.substring (m_nCurIndex);
    m_nCurIndex = m_nMaxIndex;
    return ret;
  }

  /**
   * Get the substring from the current position to the specified end index, and advance the current
   * position to the end index.
   *
   * @param nEndIndex
   *        The end index (exclusive).
   * @return The substring. Never <code>null</code>.
   */
  @NonNull
  public String getUntilIndex (final int nEndIndex)
  {
    final String ret = m_sInput.substring (m_nCurIndex, nEndIndex);
    setIndex (nEndIndex);
    return ret;
  }

  /**
   * Get all characters from the current position until the next whitespace character, and advance
   * the position accordingly.
   *
   * @return The non-whitespace substring. Never <code>null</code>.
   */
  @NonNull
  public String getUntilWhiteSpace ()
  {
    final int nStart = m_nCurIndex;
    while (m_nCurIndex < m_nMaxIndex && !Character.isWhitespace (getCurrentChar ()))
      ++m_nCurIndex;
    return m_sInput.substring (nStart, m_nCurIndex);
  }

  /**
   * Get the string until the specified end character, but excluding the end character.
   *
   * @param cEndExcl
   *        The end character to search.
   * @return A non-<code>null</code> string with all characters from the current index until the end
   *         character, but not including the end character.
   */
  @NonNull
  public String getUntil (final char cEndExcl)
  {
    final int nStart = m_nCurIndex;
    while (m_nCurIndex < m_nMaxIndex && getCurrentChar () != cEndExcl)
      ++m_nCurIndex;
    return m_sInput.substring (nStart, m_nCurIndex);
  }

  /**
   * Get all characters from the current position until the bracket nesting level reaches zero, and
   * advance the position accordingly.
   *
   * @param nStartLevel
   *        The initial nesting level.
   * @param cOpenChar
   *        The opening bracket character that increases the nesting level.
   * @param cCloseChar
   *        The closing bracket character that decreases the nesting level.
   * @return The content between the balanced brackets. Never <code>null</code>.
   */
  @NonNull
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
    return new ToStringGenerator (this).append ("Input", m_sInput)
                                       .append ("MaxIndex", m_nMaxIndex)
                                       .append ("CurIndex", m_nCurIndex)
                                       .getToString ();
  }
}

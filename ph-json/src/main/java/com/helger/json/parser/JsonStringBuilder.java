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
package com.helger.json.parser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.tostring.ToStringGenerator;

/**
 * A special StringBuilder implementation that supports conversion to numeric
 * values in a more efficient way.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class JsonStringBuilder
{
  protected char [] m_aBuf;
  protected int m_nLen;
  // Status vars
  private String m_sCache;

  /**
   * Default constructor with an initial capacity of 16.
   */
  public JsonStringBuilder ()
  {
    this (16);
  }

  /**
   * Constructor with a custom initial capacity.
   *
   * @param nCapacity
   *        The initial capacity. Must be &ge; 0.
   */
  public JsonStringBuilder (@Nonnegative final int nCapacity)
  {
    m_aBuf = new char [nCapacity];
    m_nLen = 0;
  }

  private void _expandCapacity (@Nonnegative final int nMinimumCapacity)
  {
    int nNewCapacity = (m_aBuf.length + 1) * 2;
    if (nNewCapacity < 0)
      nNewCapacity = Integer.MAX_VALUE;
    else
      if (nMinimumCapacity > nNewCapacity)
        nNewCapacity = nMinimumCapacity;
    m_aBuf = Arrays.copyOf (m_aBuf, nNewCapacity);
  }

  /**
   * Append a single character.
   *
   * @param c
   *        The character to append.
   */
  public void append (final char c)
  {
    m_sCache = null;
    final int nNewLen = m_nLen + 1;
    if (nNewLen > m_aBuf.length)
      _expandCapacity (nNewLen);
    m_aBuf[m_nLen++] = c;
  }

  /**
   * @return <code>true</code> if this builder has content, <code>false</code> if it is empty.
   */
  public boolean hasContent ()
  {
    return m_nLen > 0;
  }

  /**
   * @return The current number of characters in this builder.
   */
  @Nonnegative
  public int getLength ()
  {
    return m_nLen;
  }

  /**
   * Get the character at the specified index.
   *
   * @param nIndex
   *        The index of the character to retrieve. Must be &ge; 0 and &lt; {@link #getLength()}.
   * @return The character at the given index.
   */
  public char charAt (@Nonnegative final int nIndex)
  {
    if (nIndex < 0 || nIndex >= m_nLen)
      throw new IllegalArgumentException ("Invalid index provided: " + nIndex + ". Allowed range is 0-" + m_nLen);
    return m_aBuf[nIndex];
  }

  /**
   * Reset this builder to an empty state.
   *
   * @return this for chaining
   */
  @NonNull
  public JsonStringBuilder reset ()
  {
    m_nLen = 0;
    m_sCache = null;
    return this;
  }

  /**
   * Remove the last n characters from this builder.
   *
   * @param n
   *        The number of characters to remove from the end.
   */
  public void backup (final int n)
  {
    m_nLen -= n;
  }

  /**
   * @return The content of this builder as a {@link BigDecimal}. Never <code>null</code>.
   */
  @NonNull
  public BigDecimal getAsBigDecimal ()
  {
    return new BigDecimal (m_aBuf, 0, m_nLen);
  }

  /**
   * @return The content of this builder as a {@link BigInteger}. Never <code>null</code>.
   */
  @NonNull
  public BigInteger getAsBigInteger ()
  {
    return new BigInteger (getAsString (), 10);
  }

  /**
   * @return The content of this builder parsed as a {@link Double}. Never <code>null</code>.
   */
  @NonNull
  public Double getAsDouble ()
  {
    return Double.valueOf (Double.parseDouble (getAsString ()));
  }

  /**
   * @return The content of this builder as a String. Never <code>null</code>. The result is cached
   *         internally.
   */
  @NonNull
  public String getAsString ()
  {
    String ret = m_sCache;
    if (ret == null)
    {
      ret = new String (m_aBuf, 0, m_nLen);
      m_sCache = ret;
    }
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Len", m_nLen).append ("asString", getAsString ()).getToString ();
  }
}

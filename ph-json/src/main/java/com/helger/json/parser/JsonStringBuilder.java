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
package com.helger.json.parser;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.string.ToStringGenerator;

/**
 * A special StringBuilder implementation that supports conversion to numeric
 * values in a more efficient way.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class JsonStringBuilder implements Serializable
{
  protected char [] m_aBuf;
  protected int m_nLen;
  private transient String m_sCache;

  public JsonStringBuilder ()
  {
    this (16);
  }

  public JsonStringBuilder (@Nonnegative final int nCapacity)
  {
    m_aBuf = new char [nCapacity];
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

  public void append (final char c)
  {
    m_sCache = null;
    final int nNewLen = m_nLen + 1;
    if (nNewLen > m_aBuf.length)
      _expandCapacity (nNewLen);
    m_aBuf[m_nLen++] = c;
  }

  public boolean hasContent ()
  {
    return m_nLen > 0;
  }

  @Nonnegative
  public int getLength ()
  {
    return m_nLen;
  }

  public char charAt (@Nonnegative final int nIndex)
  {
    if (nIndex >= m_nLen)
      throw new IllegalArgumentException ("Invalid index provided: " + nIndex);
    return m_aBuf[nIndex];
  }

  public void reset ()
  {
    m_nLen = 0;
  }

  public void backup (final int n)
  {
    m_nLen -= n;
  }

  @Nonnull
  public BigDecimal getAsBigDecimal ()
  {
    return new BigDecimal (m_aBuf, 0, m_nLen);
  }

  @Nonnull
  public BigInteger getAsBigInteger ()
  {
    return new BigInteger (getAsString (), 10);
  }

  @Nonnull
  public Double getAsDouble ()
  {
    return Double.valueOf (Double.parseDouble (getAsString ()));
  }

  @Nonnull
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
    return new ToStringGenerator (this).append ("len", m_nLen).append ("asString", getAsString ()).getToString ();
  }
}

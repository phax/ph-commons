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
package com.helger.lesscommons.i18n;

import javax.annotation.Nonnull;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @author Apache Abdera
 */
public class CodepointIteratorCharArray extends AbstractCodepointIterator
{
  private final char [] m_aBuffer;

  public CodepointIteratorCharArray (@Nonnull final char [] aBuffer)
  {
    this (aBuffer, 0, aBuffer.length);
  }

  @SuppressFBWarnings ("EI_EXPOSE_REP2")
  public CodepointIteratorCharArray (@Nonnull final char [] aBuffer, final int nOfs, final int nLen)
  {
    super (nOfs, Math.min (aBuffer.length - nOfs, nLen));
    m_aBuffer = aBuffer;
  }

  @Override
  protected char get ()
  {
    return m_nPosition < m_nLimit ? m_aBuffer[m_nPosition++] : (char) -1;
  }

  @Override
  protected char get (final int index)
  {
    if (index < 0 || index >= m_nLimit)
      throw new ArrayIndexOutOfBoundsException (index);
    return m_aBuffer[index];
  }
}

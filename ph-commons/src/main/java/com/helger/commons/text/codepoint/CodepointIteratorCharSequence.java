/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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
package com.helger.commons.text.codepoint;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;

/**
 * @author Apache Abdera
 */
public class CodepointIteratorCharSequence extends AbstractCodepointIterator
{
  private final CharSequence m_aBuffer;

  public CodepointIteratorCharSequence (@Nonnull final CharSequence aBuffer)
  {
    this (aBuffer, 0, aBuffer.length ());
  }

  public CodepointIteratorCharSequence (@Nonnull final CharSequence aBuffer,
                                        @Nonnegative final int nOfs,
                                        @Nonnegative final int nLen)
  {
    super (nOfs, Math.min (aBuffer.length () - nOfs, nLen));
    m_aBuffer = ValueEnforcer.notNull (aBuffer, "Buffer");
  }

  @Override
  protected char get ()
  {
    return m_aBuffer.charAt (m_nPosition++);
  }

  @Override
  protected char get (final int nIndex)
  {
    return m_aBuffer.charAt (nIndex);
  }
}

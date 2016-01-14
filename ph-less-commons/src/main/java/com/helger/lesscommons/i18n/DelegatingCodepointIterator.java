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

import java.util.function.IntPredicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;

/**
 * Base implementation of a CodepointIterator that filters the output of another
 * {@link ICodepointIterator}
 *
 * @author Apache Abdera
 */
public class DelegatingCodepointIterator implements ICodepointIterator
{
  private final AbstractCodepointIterator m_aInternal;

  protected DelegatingCodepointIterator (@Nonnull final AbstractCodepointIterator aInternal)
  {
    m_aInternal = ValueEnforcer.notNull (aInternal, "Internal");
  }

  @Nonnull
  public AbstractCodepointIterator getInternalIterator ()
  {
    return m_aInternal;
  }

  protected char get ()
  {
    return m_aInternal.get ();
  }

  protected char get (final int index)
  {
    return m_aInternal.get (index);
  }

  public boolean hasNext ()
  {
    return m_aInternal.hasNext ();
  }

  public int lastPosition ()
  {
    return m_aInternal.lastPosition ();
  }

  public boolean isHigh (final int index)
  {
    return m_aInternal.isHigh (index);
  }

  public boolean isLow (final int index)
  {
    return m_aInternal.isLow (index);
  }

  public int limit ()
  {
    return m_aInternal.limit ();
  }

  @Nullable
  public Codepoint next ()
  {
    return m_aInternal.next ();
  }

  @Nullable
  public char [] nextChars ()
  {
    return m_aInternal.nextChars ();
  }

  @Nullable
  public Codepoint peek ()
  {
    return m_aInternal.peek ();
  }

  @Nullable
  public Codepoint peek (final int index)
  {
    return m_aInternal.peek (index);
  }

  @Nullable
  public char [] peekChars ()
  {
    return m_aInternal.peekChars ();
  }

  public int position ()
  {
    return m_aInternal.position ();
  }

  public int remaining ()
  {
    return m_aInternal.remaining ();
  }

  public void position (final int position)
  {
    m_aInternal.position (position);
  }

  public void remove ()
  {
    m_aInternal.remove ();
  }

  @Nonnull
  public CodepointIteratorRestricted restrict (@Nonnull final IntPredicate aFilter)
  {
    return m_aInternal.restrict (aFilter);
  }

  @Nonnull
  public CodepointIteratorRestricted restrict (@Nonnull final IntPredicate aFilter, final boolean bScanning)
  {
    return m_aInternal.restrict (aFilter, bScanning);
  }

  @Nonnull
  public CodepointIteratorRestricted restrict (@Nonnull final IntPredicate aFilter,
                                               final boolean bScanning,
                                               final boolean bInvert)
  {
    return m_aInternal.restrict (aFilter, bScanning, bInvert);
  }
}

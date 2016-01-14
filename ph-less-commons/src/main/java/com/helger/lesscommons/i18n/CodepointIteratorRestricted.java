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
 * @author Apache Abdera
 */
public class CodepointIteratorRestricted extends DelegatingCodepointIterator
{
  private final IntPredicate m_aFilter;
  private final boolean m_bScanningOnly;
  private final boolean m_bInvert;

  public CodepointIteratorRestricted (@Nonnull final AbstractCodepointIterator aInternal,
                                      @Nonnull final IntPredicate aFilter)
  {
    this (aInternal, aFilter, false);
  }

  public CodepointIteratorRestricted (@Nonnull final AbstractCodepointIterator aInternal,
                                      @Nonnull final IntPredicate aFilter,
                                      final boolean bScanningOnly)
  {
    this (aInternal, aFilter, bScanningOnly, false);
  }

  public CodepointIteratorRestricted (@Nonnull final AbstractCodepointIterator aInternal,
                                      @Nonnull final IntPredicate aFilter,
                                      final boolean bScanningOnly,
                                      final boolean bInvert)
  {
    super (aInternal);
    m_aFilter = ValueEnforcer.notNull (aFilter, "Filter");
    m_bScanningOnly = bScanningOnly;
    m_bInvert = bInvert;
  }

  @Override
  public boolean hasNext ()
  {
    final boolean b = super.hasNext ();
    if (m_bScanningOnly)
    {
      try
      {
        final Codepoint aCP = peek (position ());
        final int cp = aCP == null ? -1 : aCP.getValue ();
        if (b && cp != -1 && _doFilter (cp))
          return false;
      }
      catch (final InvalidCharacterException e)
      {
        return false;
      }
    }
    return b;
  }

  @Override
  public Codepoint next ()
  {
    final Codepoint cp = super.next ();
    final int v = cp.getValue ();
    if (v != -1 && _doFilter (v))
    {
      if (m_bScanningOnly)
      {
        position (position () - 1);
        return null;
      }
      throw new InvalidCharacterException (v);
    }
    return cp;
  }

  private boolean _doFilter (final int cp)
  {
    final boolean bAccept = m_aFilter.test (cp);
    return m_bInvert ? !bAccept : bAccept;
  }

  @Override
  @Nullable
  public char [] nextChars ()
  {
    final char [] chars = super.nextChars ();
    if (chars != null && chars.length > 0)
    {
      if (chars.length == 1 && _doFilter (chars[0]))
      {
        if (m_bScanningOnly)
        {
          position (position () - 1);
          return null;
        }
        throw new InvalidCharacterException (chars[0]);
      }

      if (chars.length == 2)
      {
        final int cp = Character.toCodePoint (chars[0], chars[1]);
        if (_doFilter (cp))
        {
          if (m_bScanningOnly)
          {
            position (position () - 2);
            return null;
          }
          throw new InvalidCharacterException (cp);
        }
      }
    }
    return chars;
  }
}

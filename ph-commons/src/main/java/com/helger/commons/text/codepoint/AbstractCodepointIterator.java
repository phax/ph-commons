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
package com.helger.commons.text.codepoint;

import java.util.NoSuchElementException;
import java.util.function.IntPredicate;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides an iterator over Unicode Codepoints
 *
 * @author Apache Abdera
 * @author Philip Helger
 */
public abstract class AbstractCodepointIterator implements ICodepointIterator
{
  protected int m_nPosition = -1;
  protected int m_nLimit = -1;

  protected AbstractCodepointIterator ()
  {}

  protected AbstractCodepointIterator (final int nPosition, final int nLimit)
  {
    m_nPosition = nPosition;
    m_nLimit = nLimit;
  }

  /**
   * @return Get the next char
   */
  protected abstract char get ();

  /**
   * @param nIndex
   *        Index to be retrieved
   * @return Get the specified char
   */
  protected abstract char get (int nIndex);

  @CheckForSigned
  public int lastPosition ()
  {
    final int nPos = position ();
    if (nPos < 0)
      return -1;
    return nPos >= limit () ? nPos : nPos - 1;
  }

  @Nullable
  public char [] nextChars ()
  {
    if (hasNext ())
    {
      if (_isNextSurrogate ())
      {
        final char c1 = get ();
        if (Character.isHighSurrogate (c1) && position () < limit ())
        {
          final char c2 = get ();
          if (Character.isLowSurrogate (c2))
            return new char [] { c1, c2 };
          throw new InvalidCharacterException (c2);
        }
        else
          if (Character.isLowSurrogate (c1) && position () > 0)
          {
            final char c2 = get (position () - 2);
            if (Character.isHighSurrogate (c2))
              return new char [] { c1, c2 };
            throw new InvalidCharacterException (c2);
          }
      }
      return new char [] { get () };
    }
    return null;
  }

  @Nullable
  public char [] peekChars ()
  {
    return _peekChars (position ());
  }

  /**
   * Peek the specified chars in the iterator. If the codepoint is not
   * supplemental, the char array will have a single member. If the codepoint is
   * supplemental, the char array will have two members, representing the high
   * and low surrogate chars
   */
  @Nullable
  private char [] _peekChars (@Nonnegative final int nPos)
  {
    if (nPos < 0 || nPos >= limit ())
      return null;

    final char c1 = get (nPos);
    if (Character.isHighSurrogate (c1) && nPos < limit ())
    {
      final char c2 = get (nPos + 1);
      if (Character.isLowSurrogate (c2))
        return new char [] { c1, c2 };
      throw new InvalidCharacterException (c2);
    }

    if (Character.isLowSurrogate (c1) && nPos > 1)
    {
      final char c2 = get (nPos - 1);
      if (Character.isHighSurrogate (c2))
        return new char [] { c2, c1 };
      throw new InvalidCharacterException (c2);
    }

    return new char [] { c1 };
  }

  @Nonnull
  public Codepoint next ()
  {
    final Codepoint ret = _toCodepoint (nextChars ());
    if (ret == null)
      throw new NoSuchElementException ();
    return ret;
  }

  @Nullable
  public Codepoint peek ()
  {
    return _toCodepoint (peekChars ());
  }

  @Nullable
  public Codepoint peek (final int nIndex)
  {
    return _toCodepoint (_peekChars (nIndex));
  }

  @Nullable
  private static Codepoint _toCodepoint (@Nullable final char [] aChars)
  {
    if (aChars == null || aChars.length == 0)
      return null;
    return new Codepoint (aChars);
  }

  private void _checkLimit (@Nonnegative final int n)
  {
    if (n < 0 || n > limit ())
      throw new ArrayIndexOutOfBoundsException (n);
  }

  public void position (@Nonnegative final int n)
  {
    _checkLimit (n);
    m_nPosition = n;
  }

  @Nonnegative
  public int position ()
  {
    return m_nPosition;
  }

  @Nonnegative
  public int limit ()
  {
    return m_nLimit;
  }

  @Nonnegative
  public int remaining ()
  {
    return m_nLimit - position ();
  }

  private boolean _isNextSurrogate ()
  {
    if (!hasNext ())
      return false;
    final char c = get (position ());
    return Character.isHighSurrogate (c) || Character.isLowSurrogate (c);
  }

  /**
   * Returns true if the char at the specified index is a high surrogate
   */
  public boolean isHigh (@Nonnegative final int index)
  {
    _checkLimit (index);
    return Character.isHighSurrogate (get (index));
  }

  /**
   * Returns true if the char at the specified index is a low surrogate
   */
  public boolean isLow (@Nonnegative final int index)
  {
    _checkLimit (index);
    return Character.isLowSurrogate (get (index));
  }

  @Nonnull
  public CodepointIteratorRestricted restrict (@Nonnull final IntPredicate aFilter,
                                               final boolean bScanning,
                                               final boolean bInvert)
  {
    return new CodepointIteratorRestricted (this, aFilter, bScanning, bInvert);
  }
}

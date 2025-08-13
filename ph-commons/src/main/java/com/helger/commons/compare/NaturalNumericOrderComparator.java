/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.commons.compare;

import java.util.Comparator;

import com.helger.commons.valueenforcer.ValueEnforcer;

import jakarta.annotation.Nonnull;

/**
 * A special comparator that ensures that e.g. "3.10" is sorted after "3.9".
 *
 * @author Philip Helger
 * @since 10.1.7
 */
public class NaturalNumericOrderComparator implements Comparator <String>
{
  private final Comparator <? super String> m_aOtherComp;

  /**
   * Constructor
   *
   * @param aOtherComp
   *        The comparator to be used, if there is no numeric stuff to compare.
   */
  public NaturalNumericOrderComparator (@Nonnull final Comparator <String> aOtherComp)
  {
    ValueEnforcer.notNull (aOtherComp, "OtherComp");
    m_aOtherComp = aOtherComp;
  }

  @Nonnull
  public final Comparator <? super String> getOtherComparator ()
  {
    return m_aOtherComp;
  }

  private static boolean _isDigit (final char c)
  {
    return Character.isDigit (c) || c == '.' || c == ',';
  }

  private static char _charAt (final String s, final int i)
  {
    return i >= s.length () ? 0 : s.charAt (i);
  }

  private int _compareRight (final String a, final String b)
  {
    int bias = 0;
    int ia = 0;
    int ib = 0;

    // The longest run of digits wins. That aside, the greatest
    // value wins, but we can't know that it will until we've scanned
    // both numbers to know that they have the same magnitude, so we
    // remember it in BIAS.
    for (;; ia++, ib++)
    {
      final char ca = _charAt (a, ia);
      final char cb = _charAt (b, ib);

      if (!_isDigit (ca) && !_isDigit (cb))
        return bias;
      if (!_isDigit (ca))
        return -1;
      if (!_isDigit (cb))
        return +1;
      if (ca == 0 && cb == 0)
        return bias;

      if (bias == 0)
      {
        if (ca < cb)
          bias = -1;
        else
          if (ca > cb)
            bias = +1;
      }
    }
  }

  private int _compareEqual (final String a, final String b, final int nza, final int nzb)
  {
    if (nza - nzb != 0)
      return nza - nzb;

    if (a.length () == b.length ())
      return m_aOtherComp.compare (a, b);

    return a.length () - b.length ();
  }

  public int compare (@Nonnull final String a, @Nonnull final String b)
  {
    int ia = 0;
    int ib = 0;
    int nZerosA = 0;
    int nZeroB = 0;
    char ca;
    char cb;

    while (true)
    {
      // Only count the number of zeroes leading the last number compared
      nZerosA = nZeroB = 0;

      ca = _charAt (a, ia);
      cb = _charAt (b, ib);

      // skip over leading spaces or zeros
      while (Character.isSpaceChar (ca) || ca == '0')
      {
        if (ca == '0')
          nZerosA++;
        else
        {
          // Only count consecutive zeroes
          nZerosA = 0;
        }

        ca = _charAt (a, ++ia);
      }

      while (Character.isSpaceChar (cb) || cb == '0')
      {
        if (cb == '0')
          nZeroB++;
        else
        {
          // Only count consecutive zeroes
          nZeroB = 0;
        }

        cb = _charAt (b, ++ib);
      }

      // Process run of digits
      if (Character.isDigit (ca) && Character.isDigit (cb))
      {
        final int bias = _compareRight (a.substring (ia), b.substring (ib));
        if (bias != 0)
          return bias;
      }

      if (ca == 0 && cb == 0)
      {
        // The strings compare the same. Perhaps the caller
        // will want to call strcmp to break the tie.
        return _compareEqual (a, b, nZerosA, nZeroB);
      }
      if (ca < cb)
        return -1;
      if (ca > cb)
        return +1;

      ++ia;
      ++ib;
    }
  }
}

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
package com.helger.commons.text.util;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.commons.codec.DecodeException;
import com.helger.commons.codec.EncodeException;
import com.helger.commons.text.codepoint.CodepointHelper;
import com.helger.commons.text.codepoint.CodepointIteratorCharArray;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Implementation of the Punycode encoding scheme used by IDNA and RFC 3492<br>
 * Source: https://www.ietf.org/rfc/rfc3492.txt
 *
 * @author Apache Abdera
 */
@Immutable
public final class Punycode
{
  private static final int BASE = 0x24; // 36
  private static final int TMIN = 0x01; // 1
  private static final int TMAX = 0x1A; // 26
  private static final int SKEW = 0x26; // 38
  private static final int DAMP = 0x02BC; // 700
  private static final int INITIAL_BIAS = 0x48; // 72
  private static final int INITIAL_N = 0x80; // 0x80
  private static final int DELIMITER = 0x2D; // 0x2D

  @PresentForCodeCoverage
  private static final Punycode INSTANCE = new Punycode ();

  private Punycode ()
  {}

  private static boolean _basic (final int cp)
  {
    return cp < 0x80;
  }

  private static boolean _delim (final int cp)
  {
    return cp == DELIMITER;
  }

  private static boolean _flagged (final int bcp)
  {
    return (bcp - 'A') < 26;
  }

  private static int _decode_digit (final int cp)
  {
    if (cp - '0' < 10)
      return cp - 22;
    if (cp - 'A' < 26)
      return cp - 'A';
    if (cp - 'a' < 26)
      return cp - 'a';
    return BASE;
  }

  private static int _t (final boolean c)
  {
    return c ? 1 : 0;
  }

  private static int _encode_digit (final int d, final boolean upper)
  {
    return (d + 22 + 75 * _t (d < 26)) - (_t (upper) << 5);
  }

  private static int _adapt (final int pdelta, final int numpoints, final boolean firsttime)
  {
    int delta = pdelta;
    int k;
    delta = (firsttime) ? delta / DAMP : delta >> 1;
    delta += delta / numpoints;
    for (k = 0; delta > ((BASE - TMIN) * TMAX) / 2; k += BASE)
    {
      delta /= BASE - TMIN;
    }
    return k + (BASE - TMIN + 1) * delta / (delta + SKEW);
  }

  @Nullable
  public static String getEncoded (@Nullable final String s)
  {
    if (s == null)
      return null;
    return getEncoded (s.toCharArray (), null);
  }

  public static String getEncoded (@Nonnull final char [] aChars, @Nullable final boolean [] aCaseFlags)
  {
    final StringBuilder aSB = new StringBuilder ();
    final CodepointIteratorCharArray aIter = new CodepointIteratorCharArray (aChars);
    int n = INITIAL_N;
    int delta = 0;
    int bias = INITIAL_BIAS;
    while (aIter.hasNext ())
    {
      final int i = aIter.next ().getValue ();
      if (_basic (i))
        if (aCaseFlags == null)
          aSB.append ((char) i);
    }
    final int b = aSB.length ();
    int h = b;
    if (b > 0)
      aSB.append ((char) DELIMITER);
    while (h < aChars.length)
    {
      aIter.position (0);
      int m = Integer.MAX_VALUE;
      while (aIter.hasNext ())
      {
        final int i = aIter.next ().getValue ();
        if (i >= n && i < m)
          m = i;
      }
      if (m - n > (Integer.MAX_VALUE - delta) / (h + 1))
        throw new EncodeException ("Overflow");
      delta += (m - n) * (h + 1);
      n = m;
      aIter.position (0);
      while (aIter.hasNext ())
      {
        final int i = aIter.next ().getValue ();
        if (i < n)
        {
          ++delta;
          if (delta == 0)
            throw new EncodeException ("Overflow");
        }
        if (i == n)
        {
          int q = delta;
          int k = BASE;
          while (true)
          {
            final int t;
            if (k <= bias)
              t = TMIN;
            else
              if (k >= bias + TMAX)
                t = TMAX;
              else
                t = k - bias;
            if (q < t)
              break;
            aSB.append ((char) _encode_digit (t + (q - t) % (BASE - t), false));
            q = (q - t) / (BASE - t);
            k += BASE;
          }
          aSB.append ((char) _encode_digit (q, aCaseFlags != null && aCaseFlags[aIter.position () - 1]));
          bias = _adapt (delta, h + 1, h == b);
          delta = 0;
          ++h;
        }
      }
      ++delta;
      ++n;
    }
    return aSB.toString ();
  }

  @Nullable
  public static String getDecoded (@Nullable final String s)
  {
    if (s == null)
      return null;
    return getDecoded (s.toCharArray (), null);
  }

  @Nonnull
  public static String getDecoded (@Nonnull final char [] aChars, @Nullable final boolean [] aCaseFlags)
  {
    final StringBuilder aSB = new StringBuilder ();
    int n = INITIAL_N;
    int out = 0;
    int i = 0;
    int bias = INITIAL_BIAS;
    int b = 0;
    for (int j = 0; j < aChars.length; ++j)
      if (_delim (aChars[j]))
        b = j;
    for (int j = 0; j < b; ++j)
    {
      if (aCaseFlags != null)
        aCaseFlags[out] = _flagged (aChars[j]);
      if (!_basic (aChars[j]))
        throw new DecodeException ("Bad Input");
      aSB.append (aChars[j]);
    }
    out = aSB.length ();
    for (int in = (b > 0) ? b + 1 : 0; in < aChars.length; ++out)
    {
      final int oldi = i;
      int w = 1;
      for (int k = BASE;; k += BASE)
      {
        if (in >= aChars.length)
          throw new DecodeException ("Bad input");
        final int digit = _decode_digit (aChars[in]);
        in++;
        if (digit >= BASE)
          throw new DecodeException ("Bad input");
        if (digit > (Integer.MAX_VALUE - i) / w)
          throw new DecodeException ("Overflow");
        i += digit * w;
        final int t;
        if (k <= bias)
          t = TMIN;
        else
          if (k >= bias + TMAX)
            t = TMAX;
          else
            t = k - bias;
        if (digit < t)
          break;
        if (w > Integer.MAX_VALUE / (BASE - t))
          throw new DecodeException ("Overflow");
        w *= (BASE - t);
      }
      bias = _adapt (i - oldi, out + 1, oldi == 0);
      if (i / (out + 1) > Integer.MAX_VALUE - n)
        throw new DecodeException ("Overflow");
      n += i / (out + 1);
      i %= (out + 1);
      if (aCaseFlags != null)
      {
        // not sure if this is right
        System.arraycopy (aCaseFlags, i, aCaseFlags, i + Character.charCount (n), aCaseFlags.length - i);
      }
      CodepointHelper.insert (aSB, i++, n);
    }
    return aSB.toString ();
  }
}

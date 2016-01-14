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
package com.helger.commons.lang;

import java.util.BitSet;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;

/**
 * Helper class to work with bit sets.
 *
 * @author Philip Helger
 */
@Immutable
public final class BitSetHelper
{
  @PresentForCodeCoverage
  private static final BitSetHelper s_aInstance = new BitSetHelper ();

  private BitSetHelper ()
  {}

  /**
   * Convert the passed byte value to an bit set of size 8.
   *
   * @param nValue
   *        The value to be converted to a bit set.
   * @return The non-<code>null</code> bit set.
   */
  @Nonnull
  public static BitSet createBitSet (final byte nValue)
  {
    final BitSet ret = new BitSet (CGlobal.BITS_PER_BYTE);
    for (int i = 0; i < CGlobal.BITS_PER_BYTE; ++i)
      ret.set (i, ((nValue >> i) & 1) == 1);
    return ret;
  }

  /**
   * Convert the passed short value to an bit set of size 16.
   *
   * @param nValue
   *        The value to be converted to a bit set.
   * @return The non-<code>null</code> bit set.
   */
  @Nonnull
  public static BitSet createBitSet (final short nValue)
  {
    final BitSet ret = new BitSet (CGlobal.BITS_PER_SHORT);
    for (int i = 0; i < CGlobal.BITS_PER_SHORT; ++i)
      ret.set (i, ((nValue >> i) & 1) == 1);
    return ret;
  }

  /**
   * Convert the passed int value to an bit set of size 32.
   *
   * @param nValue
   *        The value to be converted to a bit set.
   * @return The non-<code>null</code> bit set.
   */
  @Nonnull
  public static BitSet createBitSet (final int nValue)
  {
    final BitSet ret = new BitSet (CGlobal.BITS_PER_INT);
    for (int i = 0; i < CGlobal.BITS_PER_INT; ++i)
      ret.set (i, ((nValue >> i) & 1) == 1);
    return ret;
  }

  /**
   * Convert the passed long value to an bit set of size 64.
   *
   * @param nValue
   *        The value to be converted to a bit set.
   * @return The non-<code>null</code> bit set.
   */
  @Nonnull
  public static BitSet createBitSet (final long nValue)
  {
    final BitSet ret = new BitSet (CGlobal.BITS_PER_LONG);
    for (int i = 0; i < CGlobal.BITS_PER_LONG; ++i)
      ret.set (i, ((nValue >> i) & 1) == 1);
    return ret;
  }

  /**
   * Extract the int representation of the passed bit set. To avoid loss of
   * data, the bit set may not have more than 32 bits.
   *
   * @param aBS
   *        The bit set to extract the value from. May not be <code>null</code>.
   * @return The extracted value. May be negative if the bit set has 32
   *         elements, the highest order bit is set.
   */
  public static int getExtractedIntValue (@Nonnull final BitSet aBS)
  {
    ValueEnforcer.notNull (aBS, "BitSet");

    final int nMax = aBS.length ();
    if (nMax > CGlobal.BITS_PER_INT)
      throw new IllegalArgumentException ("Can extract only up to " + CGlobal.BITS_PER_INT + " bits");

    int ret = 0;
    for (int i = nMax - 1; i >= 0; --i)
    {
      ret <<= 1;
      if (aBS.get (i))
        ret += CGlobal.BIT_SET;
    }
    return ret;
  }

  /**
   * Extract the long representation of the passed bit set. To avoid loss of
   * data, the bit set may not have more than 64 bits.
   *
   * @param aBS
   *        The bit set to extract the value from. May not be <code>null</code>.
   * @return The extracted value. May be negative if the bit set has 64
   *         elements, the highest order bit is set.
   */
  public static long getExtractedLongValue (@Nonnull final BitSet aBS)
  {
    ValueEnforcer.notNull (aBS, "BitSet");

    final int nMax = aBS.length ();
    if (nMax > CGlobal.BITS_PER_LONG)
      throw new IllegalArgumentException ("Can extract only up to " + CGlobal.BITS_PER_LONG + " bits");

    long ret = 0;
    for (int i = nMax - 1; i >= 0; --i)
    {
      ret <<= 1;
      if (aBS.get (i))
        ret += CGlobal.BIT_SET;
    }
    return ret;
  }
}

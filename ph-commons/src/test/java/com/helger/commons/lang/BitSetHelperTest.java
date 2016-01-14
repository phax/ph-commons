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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.BitSet;

import org.junit.Test;

import com.helger.commons.random.VerySecureRandom;

/**
 * Test class for class {@link BitSetHelper}.
 *
 * @author Philip Helger
 */
public final class BitSetHelperTest
{
  @Test
  public void testByte ()
  {
    final BitSet aBS = BitSetHelper.createBitSet ((byte) 0x80);
    assertEquals (8, aBS.length ());
    assertEquals (1, aBS.cardinality ());
    assertEquals (0x80, BitSetHelper.getExtractedIntValue (aBS));

    for (int i = 0; i < 256; ++i)
      assertEquals (i, BitSetHelper.getExtractedIntValue (BitSetHelper.createBitSet ((byte) i)));
  }

  @Test
  public void testShort ()
  {
    final BitSet aBS = BitSetHelper.createBitSet ((short) 0x8000);
    assertEquals (16, aBS.length ());
    assertEquals (1, aBS.cardinality ());
    assertEquals (0x8000, BitSetHelper.getExtractedIntValue (aBS));

    for (int i = 0; i < (2 * Short.MAX_VALUE); i += 256)
      assertEquals (i, BitSetHelper.getExtractedIntValue (BitSetHelper.createBitSet ((short) i)));
  }

  @Test
  public void testInt ()
  {
    final BitSet aBS = BitSetHelper.createBitSet (0x80000000);
    assertEquals (32, aBS.length ());
    assertEquals (1, aBS.cardinality ());
    assertEquals (0x80000000, BitSetHelper.getExtractedIntValue (aBS));

    for (int i = 0; i < 1024; ++i)
    {
      final int nValue = VerySecureRandom.getInstance ().nextInt ();
      assertEquals (nValue, BitSetHelper.getExtractedIntValue (BitSetHelper.createBitSet (nValue)));
    }

    try
    {
      BitSetHelper.getExtractedIntValue (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      BitSetHelper.getExtractedIntValue (BitSetHelper.createBitSet (0xffffffffl + 1));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testLong ()
  {
    BitSet aBS = BitSetHelper.createBitSet (0x8000000000000000L);
    assertEquals (64, aBS.length ());
    assertEquals (1, aBS.cardinality ());
    assertEquals (0x8000000000000000L, BitSetHelper.getExtractedLongValue (aBS));

    for (int i = 0; i < 1024; ++i)
    {
      final long nValue = VerySecureRandom.getInstance ().nextLong ();
      assertEquals (nValue, BitSetHelper.getExtractedLongValue (BitSetHelper.createBitSet (nValue)));
    }

    try
    {
      BitSetHelper.getExtractedLongValue (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      aBS = new BitSet (65);
      aBS.set (64, true);
      BitSetHelper.getExtractedLongValue (aBS);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}

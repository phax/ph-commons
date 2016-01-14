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
package com.helger.commons.io.stream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Random;

import org.junit.Test;

import com.helger.commons.CGlobal;
import com.helger.commons.random.VerySecureRandom;

/**
 * Test class for class {@link BitOutputStream}.
 *
 * @author Philip Helger
 */
public final class BitOutputStreamTest
{
  @Test
  public void testSemantics () throws IOException
  {
    try
    {
      new BitOutputStream (null, ByteOrder.LITTLE_ENDIAN);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    try (final BitOutputStream aBOS = new BitOutputStream (aBAOS, ByteOrder.LITTLE_ENDIAN))
    {
      aBOS.writeBit (CGlobal.BIT_SET);
      try
      {
        aBOS.writeBit (-1);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
      try
      {
        aBOS.writeBits (1, 0);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
      try
      {
        aBOS.writeBits (1, 33);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
      aBOS.close ();
      try
      {
        aBOS.writeBit (CGlobal.BIT_NOT_SET);
        fail ();
      }
      catch (final IllegalStateException ex)
      {}
      assertNotNull (aBOS.toString ());
    }
  }

  @Test
  public void testWriteBitLittleEndian () throws IOException
  {
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    final BitOutputStream aBOS = new BitOutputStream (aBAOS, ByteOrder.LITTLE_ENDIAN);
    assertEquals (ByteOrder.LITTLE_ENDIAN, aBOS.getByteOrder ());
    aBOS.writeBit (1);
    for (int i = 0; i < 7; ++i)
      aBOS.writeBit (0);

    assertTrue (aBAOS.getSize () > 0);
    final int aByte = aBAOS.toByteArray ()[0] & 0xff;
    assertEquals (128, aByte);
  }

  @Test
  public void testWriteBitBigEndian () throws IOException
  {
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    final BitOutputStream aBOS = new BitOutputStream (aBAOS, ByteOrder.BIG_ENDIAN);
    assertEquals (ByteOrder.BIG_ENDIAN, aBOS.getByteOrder ());
    for (int i = 0; i < 7; ++i)
      aBOS.writeBit (0);
    aBOS.writeBit (1);

    assertTrue (aBAOS.getSize () > 0);
    final int aByte = aBAOS.toByteArray ()[0] & 0xff;
    assertEquals (128, aByte);
  }

  @Test
  public void testWriteManyLittleEndian () throws IOException
  {
    final Random aRandom = VerySecureRandom.getInstance ();
    for (int i = 0; i < 200; i += 3)
    {
      final byte [] buf = new byte [i * 100];
      aRandom.nextBytes (buf);

      final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
      final BitOutputStream aBOS = new BitOutputStream (aBAOS, ByteOrder.LITTLE_ENDIAN);
      for (final byte b : buf)
        aBOS.writeBits (b & 0xff, 8);

      final byte [] written = aBAOS.toByteArray ();
      // only for high order bit
      assertArrayEquals (buf, written);

      final BitInputStream aBIS = new BitInputStream (new NonBlockingByteArrayInputStream (written), ByteOrder.LITTLE_ENDIAN);
      aBAOS.reset ();
      for (final byte element : written)
      {
        final int nRead = aBIS.readBits (8);
        assertEquals (element & 0xff, nRead);
        aBAOS.write (nRead);
      }

      final byte [] read = aBAOS.toByteArray ();
      assertArrayEquals (written, read);
      assertArrayEquals (buf, read);
    }
  }

  @Test
  public void testWriteManyBigEndian () throws IOException
  {
    final Random aRandom = VerySecureRandom.getInstance ();
    for (int i = 0; i < 200; i += 3)
    {
      final byte [] buf = new byte [i * 100];
      aRandom.nextBytes (buf);

      final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
      final BitOutputStream aBOS = new BitOutputStream (aBAOS, ByteOrder.BIG_ENDIAN);
      for (final byte b : buf)
        aBOS.writeBits (b & 0xff, 8);
      aBOS.flush ();

      final byte [] written = aBAOS.toByteArray ();
      // not the same as input

      final BitInputStream aBIS = new BitInputStream (new NonBlockingByteArrayInputStream (written), ByteOrder.BIG_ENDIAN);
      aBAOS.reset ();
      for (final byte element : written)
      {
        // The value in little endian!
        final int nRead = aBIS.readBits (8);
        assertEquals (element & 0xff, (Integer.reverse (nRead) >> 24) & 0xff);
        aBAOS.write (nRead);
      }

      final byte [] read = aBAOS.toByteArray ();
      assertArrayEquals (buf, read);
    }
  }

  @Test
  public void testReadWriteRandom () throws IOException
  {
    final Random aRandom = VerySecureRandom.getInstance ();
    for (int i = 0; i < 200; i += 3)
    {
      final byte [] buf = new byte [i * 100];
      aRandom.nextBytes (buf);

      final BitInputStream aBIS = new BitInputStream (new NonBlockingByteArrayInputStream (buf), ByteOrder.LITTLE_ENDIAN);
      final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
      final BitOutputStream aBOS = new BitOutputStream (aBAOS, ByteOrder.LITTLE_ENDIAN);

      int nBitCount = buf.length * 8;
      while (nBitCount > 0)
      {
        final int nBits = Math.min (nBitCount, Math.max (1, aRandom.nextInt (13)));
        aBOS.writeBits (aBIS.readBits (nBits), nBits);
        nBitCount -= nBits;
      }

      final byte [] read = aBAOS.toByteArray ();
      assertArrayEquals (buf, read);
    }
  }

  @Test
  public void testWriteManual () throws IOException
  {
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    try (final BitOutputStream aBOS = new BitOutputStream (aBAOS, ByteOrder.LITTLE_ENDIAN))
    {
      aBOS.writeBits (15, 4);
      aBOS.writeBits (15, 4);
      aBOS.writeBits (0, 8);
      aBOS.writeBits (255, 8);
    }
    assertArrayEquals (new byte [] { (byte) 0xff, 0, (byte) 0xff }, aBAOS.toByteArray ());
  }
}

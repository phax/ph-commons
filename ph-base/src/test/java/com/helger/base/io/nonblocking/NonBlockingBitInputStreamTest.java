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
package com.helger.base.io.nonblocking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteOrder;

import org.junit.Test;

import com.helger.base.CGlobal;

/**
 * Test class for class {@link NonBlockingBitInputStream}.
 *
 * @author Philip Helger
 */
public final class NonBlockingBitInputStreamTest
{
  @SuppressWarnings ("resource")
  @Test
  public void testSemantics () throws IOException
  {
    try
    {
      new NonBlockingBitInputStream (null, ByteOrder.LITTLE_ENDIAN);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try (final NonBlockingByteArrayInputStream aBAOS = new NonBlockingByteArrayInputStream (new byte [10]);
         final NonBlockingBitInputStream aBOS = new NonBlockingBitInputStream (aBAOS, ByteOrder.LITTLE_ENDIAN))
    {
      for (int i = 0; i < 5 * CGlobal.BITS_PER_BYTE; ++i)
        assertEquals (CGlobal.BIT_NOT_SET, aBOS.readBit ());
      try
      {
        aBOS.readBits (0);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
      try
      {
        aBOS.readBits (33);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
      for (int i = 0; i < 5; ++i)
        assertEquals (0, aBOS.readBits (CGlobal.BITS_PER_BYTE));
      try
      {
        aBOS.readBit ();
        fail ();
      }
      catch (final EOFException ex)
      {}
      aBOS.close ();
      try
      {
        aBOS.readBit ();
        fail ();
      }
      catch (final IOException ex)
      {}
    }
  }

  @Test
  public void testReadBitLittleEndian () throws IOException
  {
    try (final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream (new byte [] { (byte) 0x80 });
         final NonBlockingBitInputStream aBIS = new NonBlockingBitInputStream (aBAIS, ByteOrder.LITTLE_ENDIAN))
    {
      assertEquals (1, aBIS.readBit ());
      for (int i = 0; i < 7; ++i)
        assertEquals (0, aBIS.readBit ());
    }
  }

  @Test
  public void testReadBitBigEndian () throws IOException
  {
    try (final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream (new byte [] { (byte) 0x80 });
         final NonBlockingBitInputStream aBIS = new NonBlockingBitInputStream (aBAIS, ByteOrder.BIG_ENDIAN))
    {
      for (int i = 0; i < 7; ++i)
        assertEquals (0, aBIS.readBit ());
      assertEquals (1, aBIS.readBit ());
    }
  }
}

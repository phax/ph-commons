/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.base.io.stream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;

/**
 * Test class for class {@link ByteBufferOutputStream}.
 *
 * @author Philip Helger
 */
public final class ByteBufferOutputStreamTest
{
  private static final byte [] PAYLOAD = "Hello World".getBytes (StandardCharsets.ISO_8859_1);

  @Test
  public void testDefaultCtor ()
  {
    try (final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream ())
    {
      assertTrue (aBBOS.canGrow ());
      assertTrue (ByteBufferOutputStream.DEFAULT_CAN_GROW == aBBOS.canGrow ());
      assertEquals (0, aBBOS.size ());
      assertNotNull (aBBOS.getBuffer ());
      assertNotNull (aBBOS.toString ());
    }
  }

  @Test
  public void testSizedCtors ()
  {
    try (final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream (16))
    {
      assertTrue (aBBOS.canGrow ());
    }
    try (final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream (16, false))
    {
      assertFalse (aBBOS.canGrow ());
    }
  }

  @Test
  public void testArrayCtors ()
  {
    // The array is wrapped as the backing buffer - "size" is the write
    // position, so it starts at 0
    try (final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream (PAYLOAD))
    {
      assertEquals (0, aBBOS.size ());
      assertFalse (aBBOS.canGrow ());
      assertArrayEquals (new byte [0], aBBOS.getAsByteArray ());
    }
    try (final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream (PAYLOAD, 0, 5))
    {
      assertEquals (0, aBBOS.size ());
      aBBOS.write (PAYLOAD, 0, 5);
      assertEquals (5, aBBOS.size ());
    }
    try (final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream (ByteBuffer.wrap (PAYLOAD), false))
    {
      assertEquals (0, aBBOS.size ());
    }
  }

  @Test
  public void testWriteAndRead ()
  {
    try (final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream ())
    {
      aBBOS.write (PAYLOAD[0]);
      aBBOS.write (PAYLOAD, 1, PAYLOAD.length - 1);
      assertEquals (PAYLOAD.length, aBBOS.size ());
      assertArrayEquals (PAYLOAD, aBBOS.getAsByteArray ());
      assertEquals ("Hello World", aBBOS.getAsString (StandardCharsets.ISO_8859_1));
    }

    try (final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream ())
    {
      aBBOS.write (ByteBuffer.wrap (PAYLOAD));
      assertArrayEquals (PAYLOAD, aBBOS.getAsByteArray ());
    }
  }

  @Test
  public void testReset ()
  {
    try (final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream ())
    {
      aBBOS.write (PAYLOAD, 0, PAYLOAD.length);
      assertEquals (PAYLOAD.length, aBBOS.size ());
      aBBOS.reset ();
      assertEquals (0, aBBOS.size ());
    }
  }

  @Test
  public void testGetAsByteArrayInputStream ()
  {
    try (final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream ())
    {
      aBBOS.write (PAYLOAD, 0, PAYLOAD.length);

      try (final NonBlockingByteArrayInputStream aIS = aBBOS.getAsByteArrayInputStream (true))
      {
        assertArrayEquals (PAYLOAD, StreamHelper.getAllBytes (aIS));
      }
      try (final NonBlockingByteArrayInputStream aIS = aBBOS.getAsByteArrayInputStream (false))
      {
        assertNotNull (aIS);
      }
    }
  }

  @Test
  public void testWriteToByteBuffer ()
  {
    try (final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream ())
    {
      aBBOS.write (PAYLOAD, 0, PAYLOAD.length);

      final ByteBuffer aDest = ByteBuffer.allocate (PAYLOAD.length);
      aBBOS.writeTo (aDest, true);
      assertEquals (PAYLOAD.length, aDest.position ());
    }
    try (final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream ())
    {
      aBBOS.write (PAYLOAD, 0, PAYLOAD.length);
      final ByteBuffer aDest2 = ByteBuffer.allocate (PAYLOAD.length);
      aBBOS.writeTo (aDest2);
      assertEquals (PAYLOAD.length, aDest2.position ());
    }
  }

  @Test
  public void testWriteToByteArray ()
  {
    // Each writeTo consumes the buffer, so start from a fresh stream each time
    try (final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream ())
    {
      aBBOS.write (PAYLOAD, 0, PAYLOAD.length);
      final byte [] aDest = new byte [PAYLOAD.length];
      aBBOS.writeTo (aDest, false);
      assertArrayEquals (PAYLOAD, aDest);
    }
    try (final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream ())
    {
      aBBOS.write (PAYLOAD, 0, PAYLOAD.length);
      final byte [] aDest = new byte [PAYLOAD.length];
      aBBOS.writeTo (aDest, 0, PAYLOAD.length, false);
      assertArrayEquals (PAYLOAD, aDest);
    }
    try (final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream ())
    {
      aBBOS.write (PAYLOAD, 0, PAYLOAD.length);
      final byte [] aDest = new byte [PAYLOAD.length];
      aBBOS.writeTo (aDest);
      assertArrayEquals (PAYLOAD, aDest);
    }
    try (final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream ())
    {
      aBBOS.write (PAYLOAD, 0, PAYLOAD.length);
      final byte [] aDest = new byte [PAYLOAD.length];
      aBBOS.writeTo (aDest, 0, PAYLOAD.length);
      assertArrayEquals (PAYLOAD, aDest);
    }
  }

  @Test
  public void testWriteToOutputStream () throws IOException
  {
    try (final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream ())
    {
      aBBOS.write (PAYLOAD, 0, PAYLOAD.length);

      try (final NonBlockingByteArrayOutputStream aOS = new NonBlockingByteArrayOutputStream ())
      {
        aBBOS.writeTo (aOS, false);
        assertArrayEquals (PAYLOAD, aOS.toByteArray ());
      }
    }
    try (final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream ())
    {
      aBBOS.write (PAYLOAD, 0, PAYLOAD.length);
      try (final NonBlockingByteArrayOutputStream aOS = new NonBlockingByteArrayOutputStream ())
      {
        aBBOS.writeTo (aOS);
        assertArrayEquals (PAYLOAD, aOS.toByteArray ());
      }
    }
  }

  @Test
  public void testNonGrowingOverflow ()
  {
    try (final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream (2, false))
    {
      try
      {
        aBBOS.write (PAYLOAD, 0, PAYLOAD.length);
        fail ();
      }
      catch (final RuntimeException ex)
      {
        // expected - the buffer cannot grow
      }
    }
  }

  @Test
  public void testGrowing ()
  {
    try (final ByteBufferOutputStream aBBOS = new ByteBufferOutputStream (2, true))
    {
      aBBOS.write (PAYLOAD, 0, PAYLOAD.length);
      assertEquals (PAYLOAD.length, aBBOS.size ());
      assertArrayEquals (PAYLOAD, aBBOS.getAsByteArray ());
    }
  }
}

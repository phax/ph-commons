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
package com.helger.base.io.nonblocking;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

/**
 * Test class for class {@link NonBlockingPushbackReader} and
 * {@link NonBlockingPushbackInputStream}.
 *
 * @author Philip Helger
 */
public final class NonBlockingPushbackStreamsTest
{
  private static final String TEXT = "Hello World";
  private static final byte [] PAYLOAD = TEXT.getBytes (StandardCharsets.ISO_8859_1);

  @Test
  public void testReaderSingleChar () throws IOException
  {
    try (final NonBlockingPushbackReader aReader = new NonBlockingPushbackReader (new NonBlockingStringReader (TEXT)))
    {
      assertEquals (0, aReader.getUnreadCount ());
      assertFalse (aReader.hasUnreadChars ());

      assertEquals ('H', aReader.read ());
      aReader.unread ('H');
      assertEquals (1, aReader.getUnreadCount ());
      assertTrue (aReader.hasUnreadChars ());
      assertEquals ('H', aReader.read ());
      assertEquals (0, aReader.getUnreadCount ());

      assertTrue (aReader.ready ());
      assertFalse (aReader.markSupported ());
    }
  }

  @Test
  public void testReaderBuffer () throws IOException
  {
    try (final NonBlockingPushbackReader aReader = new NonBlockingPushbackReader (new NonBlockingStringReader (TEXT),
                                                                                  16))
    {
      final char [] aBuf = new char [5];
      assertEquals (5, aReader.read (aBuf, 0, 5));
      assertArrayEquals ("Hello".toCharArray (), aBuf);

      aReader.unread (aBuf, 0, 5);
      assertEquals (5, aReader.getUnreadCount ());

      final char [] aBuf2 = new char [5];
      assertEquals (5, aReader.read (aBuf2, 0, 5));
      assertArrayEquals ("Hello".toCharArray (), aBuf2);

      aReader.unread (aBuf2);
      assertEquals (5, aReader.getUnreadCount ());
      assertEquals (5, aReader.read (new char [5], 0, 5));

      // A zero length read returns 0
      assertEquals (0, aReader.read (new char [0], 0, 0));
    }
  }

  @Test
  public void testReaderSkipAndOverflow () throws IOException
  {
    try (final NonBlockingPushbackReader aReader = new NonBlockingPushbackReader (new NonBlockingStringReader (TEXT),
                                                                                  2))
    {
      assertEquals (2, aReader.skip (2));

      aReader.unread ('a');
      aReader.unread ('b');
      // The push back buffer is full now
      try
      {
        aReader.unread ('c');
        fail ();
      }
      catch (final IOException ex)
      {
        // expected
      }
    }
  }

  @Test
  public void testReaderMarkAndResetUnsupported () throws IOException
  {
    try (final NonBlockingPushbackReader aReader = new NonBlockingPushbackReader (new NonBlockingStringReader (TEXT)))
    {
      try
      {
        aReader.mark (10);
        fail ();
      }
      catch (final IOException ex)
      {
        // expected
      }
      try
      {
        aReader.reset ();
        fail ();
      }
      catch (final IOException ex)
      {
        // expected
      }
    }
  }

  @Test
  public void testReaderClosed () throws IOException
  {
    final NonBlockingPushbackReader aReader = new NonBlockingPushbackReader (new NonBlockingStringReader (TEXT));
    aReader.close ();
    try
    {
      aReader.read ();
      fail ();
    }
    catch (final IOException ex)
    {
      // expected
    }
  }

  @Test
  public void testInputStreamSingleByte () throws IOException
  {
    try (final NonBlockingPushbackInputStream aIS = new NonBlockingPushbackInputStream (new NonBlockingByteArrayInputStream (PAYLOAD)))
    {
      assertEquals (0, aIS.getUnreadCount ());
      assertFalse (aIS.hasUnreadBytes ());

      assertEquals ('H', aIS.read ());
      aIS.unread ('H');
      assertEquals (1, aIS.getUnreadCount ());
      assertTrue (aIS.hasUnreadBytes ());
      assertEquals ('H', aIS.read ());

      assertTrue (aIS.available () > 0);
      assertFalse (aIS.markSupported ());
    }
  }

  @Test
  public void testInputStreamBuffer () throws IOException
  {
    try (final NonBlockingPushbackInputStream aIS = new NonBlockingPushbackInputStream (new NonBlockingByteArrayInputStream (PAYLOAD),
                                                                                        16))
    {
      final byte [] aBuf = new byte [5];
      assertEquals (5, aIS.read (aBuf, 0, 5));

      aIS.unread (aBuf, 0, 5);
      assertEquals (5, aIS.getUnreadCount ());

      final byte [] aBuf2 = new byte [5];
      assertEquals (5, aIS.read (aBuf2, 0, 5));
      assertArrayEquals (aBuf, aBuf2);

      aIS.unread (aBuf2);
      assertEquals (5, aIS.getUnreadCount ());
      assertEquals (5, aIS.read (new byte [5], 0, 5));

      assertEquals (0, aIS.read (new byte [0], 0, 0));
    }
  }

  @Test
  public void testInputStreamSkipAndOverflow () throws IOException
  {
    try (final NonBlockingPushbackInputStream aIS = new NonBlockingPushbackInputStream (new NonBlockingByteArrayInputStream (PAYLOAD),
                                                                                        2))
    {
      assertEquals (2, aIS.skip (2));

      aIS.unread ('a');
      aIS.unread ('b');
      try
      {
        aIS.unread ('c');
        fail ();
      }
      catch (final IOException ex)
      {
        // expected
      }
    }
  }

  @Test
  public void testInputStreamMarkAndReset () throws IOException
  {
    try (final NonBlockingPushbackInputStream aIS = new NonBlockingPushbackInputStream (new NonBlockingByteArrayInputStream (PAYLOAD)))
    {
      // mark is a no-op, reset always fails
      aIS.mark (10);
      try
      {
        aIS.reset ();
        fail ();
      }
      catch (final IOException ex)
      {
        // expected
      }
    }
  }

  @Test
  public void testInputStreamClosed () throws IOException
  {
    final NonBlockingPushbackInputStream aIS = new NonBlockingPushbackInputStream (new NonBlockingByteArrayInputStream (PAYLOAD));
    aIS.close ();
    try
    {
      aIS.read ();
      fail ();
    }
    catch (final IOException ex)
    {
      // expected
    }
  }

  @Test
  public void testBufferedOutputStream () throws IOException
  {
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
         final NonBlockingBufferedOutputStream aOS = new NonBlockingBufferedOutputStream (aBAOS, 4))
    {
      aOS.write (PAYLOAD[0]);
      aOS.write (PAYLOAD, 1, PAYLOAD.length - 1);
      aOS.flush ();
      assertArrayEquals (PAYLOAD, aBAOS.toByteArray ());
    }

    // The default buffer size
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
         final NonBlockingBufferedOutputStream aOS = new NonBlockingBufferedOutputStream (aBAOS))
    {
      aOS.write (PAYLOAD, 0, PAYLOAD.length);
      aOS.flush ();
      assertArrayEquals (PAYLOAD, aBAOS.toByteArray ());
    }

    // A write larger than the buffer goes straight through
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
         final NonBlockingBufferedOutputStream aOS = new NonBlockingBufferedOutputStream (aBAOS, 2))
    {
      aOS.write (PAYLOAD, 0, PAYLOAD.length);
      aOS.flush ();
      assertArrayEquals (PAYLOAD, aBAOS.toByteArray ());
    }
  }
}

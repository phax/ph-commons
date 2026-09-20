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
package com.helger.base.codec.base64;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.string.StringHelper;

/**
 * Test class for class {@link Base64InputStream} and {@link Base64OutputStream}.
 *
 * @author Philip Helger
 */
public final class Base64StreamsTest
{
  private static final String DECODED = "Hello world - this is a test of the Base64 streams!";
  private static final byte [] DECODED_BYTES = DECODED.getBytes (StandardCharsets.ISO_8859_1);
  private static final String ENCODED = Base64.encodeBytes (DECODED_BYTES);

  @Test
  public void testOutputStreamEncode () throws IOException
  {
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
    {
      try (final Base64OutputStream aOS = new Base64OutputStream (aBAOS))
      {
        // Write a single byte and the rest as an array
        aOS.write (DECODED_BYTES[0]);
        aOS.write (DECODED_BYTES, 1, DECODED_BYTES.length - 1);
      }
      assertEquals (ENCODED, aBAOS.getAsString (StandardCharsets.ISO_8859_1));
    }
  }

  @Test
  public void testOutputStreamDecode () throws IOException
  {
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
    {
      try (final Base64OutputStream aOS = new Base64OutputStream (aBAOS, Base64.DECODE))
      {
        aOS.write (ENCODED.getBytes (StandardCharsets.ISO_8859_1));
      }
      assertArrayEquals (DECODED_BYTES, aBAOS.toByteArray ());
    }
  }

  @Test
  public void testOutputStreamBreakLines () throws IOException
  {
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
    {
      try (final Base64OutputStream aOS = new Base64OutputStream (aBAOS, Base64.ENCODE | Base64.DO_BREAK_LINES))
      {
        // Enough data for more than one line
        for (int i = 0; i < 10; ++i)
          aOS.write (DECODED_BYTES);
      }
      final String sEncoded = aBAOS.getAsString (StandardCharsets.ISO_8859_1);
      assertTrue (sEncoded, sEncoded.indexOf ('\n') > 0);

      // Decoding ignores the newlines
      assertEquals (StringHelper.getRepeated (DECODED, 10),
                    new String (Base64.decode (sEncoded), StandardCharsets.ISO_8859_1));
    }

    // With a custom new line
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
    {
      try (final Base64OutputStream aOS = new Base64OutputStream (aBAOS, Base64.ENCODE | Base64.DO_BREAK_LINES))
      {
        aOS.setNewLineBytes ("\r\n".getBytes (StandardCharsets.ISO_8859_1));
        for (int i = 0; i < 10; ++i)
          aOS.write (DECODED_BYTES);
      }
      assertTrue (aBAOS.getAsString (StandardCharsets.ISO_8859_1).contains ("\r\n"));
    }
  }

  @Test
  public void testOutputStreamSuspendEncoding () throws IOException
  {
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
    {
      try (final Base64OutputStream aOS = new Base64OutputStream (aBAOS))
      {
        aOS.write (DECODED_BYTES);
        // Everything in between is written as is
        aOS.suspendEncoding ();
        aOS.write ("-raw-".getBytes (StandardCharsets.ISO_8859_1));
        aOS.resumeEncoding ();
        aOS.write (DECODED_BYTES);
      }
      final String sEncoded = aBAOS.getAsString (StandardCharsets.ISO_8859_1);
      assertEquals (ENCODED + "-raw-" + ENCODED, sEncoded);
    }
  }

  @Test
  public void testOutputStreamDecodeNotPadded ()
  {
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
    {
      // "QUJD" is "ABC" - a single character cannot be decoded
      final Base64OutputStream aOS = new Base64OutputStream (aBAOS, Base64.DECODE);
      aOS.write ('Q');
      aOS.close ();
      fail ();
    }
    catch (final IOException ex)
    {
      // expected - "Base64 input not properly padded."
    }
  }

  @Test
  public void testInputStreamDecode () throws IOException
  {
    // Read single bytes
    try (final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream (ENCODED.getBytes (StandardCharsets.ISO_8859_1));
         final Base64InputStream aIS = new Base64InputStream (aBAIS);
         final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
    {
      int b;
      while ((b = aIS.read ()) != -1)
        aBAOS.write (b);
      assertArrayEquals (DECODED_BYTES, aBAOS.toByteArray ());
    }

    // Read into an array
    try (final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream (ENCODED.getBytes (StandardCharsets.ISO_8859_1));
         final Base64InputStream aIS = new Base64InputStream (aBAIS))
    {
      assertArrayEquals (DECODED_BYTES, StreamHelper.getAllBytes (aIS));
    }
  }

  @Test
  public void testInputStreamEncode () throws IOException
  {
    try (final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream (DECODED_BYTES);
         final Base64InputStream aIS = new Base64InputStream (aBAIS, Base64.ENCODE))
    {
      assertEquals (ENCODED, new String (StreamHelper.getAllBytes (aIS), StandardCharsets.ISO_8859_1));
    }

    // Encode with line breaks
    try (final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream (DECODED_BYTES);
         final Base64InputStream aIS = new Base64InputStream (aBAIS, Base64.ENCODE | Base64.DO_BREAK_LINES))
    {
      final byte [] aEncoded = StreamHelper.getAllBytes (aIS);
      assertArrayEquals (DECODED_BYTES, Base64.decode (new String (aEncoded, StandardCharsets.ISO_8859_1)));
    }
  }

  @Test
  public void testInputStreamEmpty () throws IOException
  {
    try (final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream (new byte [0]);
         final Base64InputStream aIS = new Base64InputStream (aBAIS))
    {
      assertEquals (-1, aIS.read ());
      assertEquals (0, StreamHelper.getAllBytes (aIS).length);
    }

    try (final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream (new byte [0]);
         final Base64InputStream aIS = new Base64InputStream (aBAIS, Base64.ENCODE))
    {
      assertEquals (-1, aIS.read ());
    }
  }
}

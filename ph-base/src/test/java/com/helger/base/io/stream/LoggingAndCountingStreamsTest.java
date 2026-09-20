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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.base.io.iface.IHasInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.io.nonblocking.NonBlockingStringReader;
import com.helger.base.io.nonblocking.NonBlockingStringWriter;

/**
 * Test class for the logging and counting stream wrappers {@link LoggingInputStream},
 * {@link LoggingOutputStream}, {@link LoggingReader}, {@link LoggingWriter},
 * {@link CountingReader}, {@link CountingWriter}, {@link NullOutputStream},
 * {@link NonClosingReader} and {@link NonClosingWriter}.
 *
 * @author Philip Helger
 */
public final class LoggingAndCountingStreamsTest
{
  private static final String TEXT = "Hello World";
  private static final byte [] PAYLOAD = TEXT.getBytes (StandardCharsets.ISO_8859_1);

  @Test
  public void testLoggingInputStream () throws IOException
  {
    try (final LoggingInputStream aIS = new LoggingInputStream (new NonBlockingByteArrayInputStream (PAYLOAD)))
    {
      assertEquals (0, aIS.getPosition ());
      assertEquals (PAYLOAD[0], aIS.read ());
      assertEquals (1, aIS.getPosition ());

      final byte [] aBuf = new byte [4];
      assertEquals (4, aIS.read (aBuf, 0, 4));
      assertEquals (5, aIS.getPosition ());

      assertEquals (2, aIS.skip (2));
      assertEquals (7, aIS.getPosition ());

      aIS.mark (10);
      aIS.reset ();
      assertNotNull (aIS.toString ());
    }
  }

  @Test
  public void testLoggingOutputStream () throws IOException
  {
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
         final LoggingOutputStream aOS = new LoggingOutputStream (aBAOS))
    {
      assertEquals (0, aOS.getBytesWritten ());
      aOS.write (PAYLOAD[0]);
      assertEquals (1, aOS.getBytesWritten ());
      aOS.write (PAYLOAD, 1, PAYLOAD.length - 1);
      assertEquals (PAYLOAD.length, aOS.getBytesWritten ());
      assertNotNull (aOS.toString ());
    }
  }

  @Test
  public void testLoggingReader () throws IOException
  {
    try (final LoggingReader aReader = new LoggingReader (new NonBlockingStringReader (TEXT)))
    {
      assertEquals (0, aReader.getPosition ());
      assertEquals (TEXT.charAt (0), aReader.read ());
      assertEquals (1, aReader.getPosition ());

      final char [] aBuf = new char [4];
      assertEquals (4, aReader.read (aBuf, 0, 4));
      assertEquals (5, aReader.getPosition ());

      assertEquals (2, aReader.skip (2));
      assertEquals (7, aReader.getPosition ());

      aReader.mark (10);
      aReader.reset ();
      assertNotNull (aReader.toString ());
    }
  }

  @Test
  public void testLoggingWriter () throws IOException
  {
    try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
         final LoggingWriter aWriter = new LoggingWriter (aSW))
    {
      assertEquals (0, aWriter.getBytesWritten ());
      aWriter.write ('a');
      assertEquals (1, aWriter.getBytesWritten ());
      aWriter.write (TEXT.toCharArray (), 0, TEXT.length ());
      assertEquals (1 + TEXT.length (), aWriter.getBytesWritten ());
      assertNotNull (aWriter.toString ());
    }
  }

  @Test
  public void testCountingReader () throws IOException
  {
    try (final CountingReader aReader = new CountingReader (new NonBlockingStringReader (TEXT)))
    {
      assertEquals (0, aReader.getCharsRead ());
      assertEquals (0, aReader.getPosition ());

      assertEquals (TEXT.charAt (0), aReader.read ());
      assertEquals (1, aReader.getCharsRead ());

      final char [] aBuf = new char [4];
      assertEquals (4, aReader.read (aBuf, 0, 4));
      assertEquals (5, aReader.getCharsRead ());

      // "charsRead" counts only real reads, "position" also counts skips
      assertEquals (2, aReader.skip (2));
      assertEquals (5, aReader.getCharsRead ());
      assertEquals (7, aReader.getPosition ());

      aReader.mark (10);
      assertEquals (7, aReader.getMark ());
      aReader.reset ();
      assertEquals (7, aReader.getPosition ());
      assertNotNull (aReader.toString ());
    }
  }

  @Test
  public void testCountingWriter () throws IOException
  {
    try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
         final CountingWriter aWriter = new CountingWriter (aSW))
    {
      assertEquals (0, aWriter.getCharsWritten ());
      aWriter.write ('a');
      assertEquals (1, aWriter.getCharsWritten ());
      aWriter.write (TEXT.toCharArray (), 0, TEXT.length ());
      assertEquals (1 + TEXT.length (), aWriter.getCharsWritten ());
      assertNotNull (aWriter.toString ());
      aWriter.flush ();
    }
  }

  @Test
  public void testNullOutputStream () throws IOException
  {
    assertNotNull (NullOutputStream.NULL_OUTPUT_STREAM);
    try (final NullOutputStream aOS = new NullOutputStream ())
    {
      aOS.write (1);
      aOS.write (PAYLOAD);
      aOS.write (PAYLOAD, 0, PAYLOAD.length);
      aOS.flush ();
    }
  }

  @Test
  public void testNonClosingReaderAndWriter () throws IOException
  {
    final NonBlockingStringReader aSR = new NonBlockingStringReader (TEXT);
    try (final Reader aReader = new NonClosingReader (aSR))
    {
      assertEquals (TEXT.charAt (0), aReader.read ());
    }
    // The wrapped reader is still usable
    assertEquals (TEXT.charAt (1), aSR.read ());

    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    try (final Writer aWriter = new NonClosingWriter (aSW))
    {
      aWriter.write ("a");
    }
    // The wrapped writer is still usable
    aSW.write ("b");
    assertEquals ("ab", aSW.getAsString ());
  }

  @Test
  public void testHasInputStream () throws IOException
  {
    final HasInputStream aMultiple = HasInputStream.multiple ( () -> new NonBlockingByteArrayInputStream (PAYLOAD));
    assertTrue (aMultiple.isReadMultiple ());
    assertNotNull (aMultiple.getInputStream ());
    assertNotNull (aMultiple.toString ());

    final HasInputStream aOnce = HasInputStream.once ( () -> new NonBlockingByteArrayInputStream (PAYLOAD));
    assertEquals (false, aOnce.isReadMultiple ());
    assertNotNull (aOnce.getInputStream ());

    // The byte array based variant
    final IHasInputStream aBytes = HasInputStream.create (PAYLOAD);
    assertTrue (aBytes.isReadMultiple ());
    assertNotNull (aBytes.getInputStream ());
    assertNotNull (aBytes.toString ());

    final IHasInputStream aBytesRange = HasInputStream.create (PAYLOAD, 0, 5);
    assertNotNull (aBytesRange.getInputStream ());

    // The NonBlockingByteArrayOutputStream based variant
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ())
    {
      aBAOS.write (PAYLOAD, 0, PAYLOAD.length);
      final IHasInputStream aFromBAOS = HasInputStream.create (aBAOS);
      assertTrue (aFromBAOS.isReadMultiple ());
      assertNotNull (aFromBAOS.getInputStream ());
      assertNotNull (aFromBAOS.toString ());
    }
  }
}

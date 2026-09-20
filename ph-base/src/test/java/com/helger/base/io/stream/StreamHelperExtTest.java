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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import com.helger.base.io.iface.IHasInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.io.nonblocking.NonBlockingStringReader;
import com.helger.base.io.nonblocking.NonBlockingStringWriter;
import com.helger.base.numeric.mutable.MutableLong;
import com.helger.base.state.ESuccess;

/**
 * Additional test class for class {@link StreamHelper}, covering the buffering helpers, the copy
 * builders and the safe UTF handling.
 *
 * @author Philip Helger
 */
public final class StreamHelperExtTest
{
  private static final String TEXT = "Hello World - this is the payload";
  private static final byte [] PAYLOAD = TEXT.getBytes (StandardCharsets.ISO_8859_1);

  @Test
  public void testCreateBuffers ()
  {
    assertEquals (StreamHelper.DEFAULT_BUFSIZE, StreamHelper.createDefaultCopyBufferBytes ().length);
    assertEquals (StreamHelper.DEFAULT_BUFSIZE, StreamHelper.createDefaultCopyBufferChars ().length);
  }

  @Test
  public void testCreateReaderAndWriter () throws IOException
  {
    assertNotNull (StreamHelper.createReader (TEXT));
    assertNotNull (StreamHelper.createReader (TEXT.toCharArray ()));
    assertNull (StreamHelper.createReader ((InputStream) null, StandardCharsets.ISO_8859_1));
    assertNull (StreamHelper.createWriter ((OutputStream) null, StandardCharsets.ISO_8859_1));

    try (final NonBlockingByteArrayInputStream aIS = new NonBlockingByteArrayInputStream (PAYLOAD))
    {
      assertNotNull (StreamHelper.createReader (aIS, StandardCharsets.ISO_8859_1));
    }
    try (final NonBlockingByteArrayOutputStream aOS = new NonBlockingByteArrayOutputStream ())
    {
      assertNotNull (StreamHelper.createWriter (aOS, StandardCharsets.ISO_8859_1));
    }
  }

  @Test
  public void testIsKnownEOFException ()
  {
    assertFalse (StreamHelper.isKnownEOFException ((Throwable) null));
    assertFalse (StreamHelper.isKnownEOFException ((Class <?>) null));
    assertTrue (StreamHelper.isKnownEOFException (new EOFException ()));
    assertTrue (StreamHelper.isKnownEOFException (EOFException.class));
    assertFalse (StreamHelper.isKnownEOFException (new IllegalStateException ()));
  }

  @Test
  public void testCloseAndFlush () throws IOException
  {
    assertSame (ESuccess.FAILURE, StreamHelper.close (null));
    assertSame (ESuccess.FAILURE, StreamHelper.closeWithoutFlush (null));
    assertSame (ESuccess.FAILURE, StreamHelper.flush (null));

    final NonBlockingByteArrayOutputStream aOS = new NonBlockingByteArrayOutputStream ();
    assertSame (ESuccess.SUCCESS, StreamHelper.flush (aOS));
    assertSame (ESuccess.SUCCESS, StreamHelper.close (aOS));

    final NonBlockingByteArrayOutputStream aOS2 = new NonBlockingByteArrayOutputStream ();
    assertSame (ESuccess.SUCCESS, StreamHelper.closeWithoutFlush (aOS2));
  }

  @Test
  public void testIsBufferedAndGetBuffered () throws IOException
  {
    assertFalse (StreamHelper.isBuffered ((InputStream) null));
    assertNull (StreamHelper.getBuffered ((InputStream) null));
    assertFalse (StreamHelper.isBuffered ((OutputStream) null));
    assertNull (StreamHelper.getBuffered ((OutputStream) null));
    assertFalse (StreamHelper.isBuffered ((Reader) null));
    assertNull (StreamHelper.getBuffered ((Reader) null));
    assertFalse (StreamHelper.isBuffered ((Writer) null));
    assertNull (StreamHelper.getBuffered ((Writer) null));

    try (final NonBlockingByteArrayInputStream aIS = new NonBlockingByteArrayInputStream (PAYLOAD))
    {
      final InputStream aBuffered = StreamHelper.getBuffered (aIS);
      assertTrue (StreamHelper.isBuffered (aBuffered));
      // An already buffered stream is returned as is
      assertSame (aBuffered, StreamHelper.getBuffered (aBuffered));
    }
    try (final NonBlockingByteArrayOutputStream aOS = new NonBlockingByteArrayOutputStream ())
    {
      final OutputStream aBuffered = StreamHelper.getBuffered (aOS);
      assertTrue (StreamHelper.isBuffered (aBuffered));
      assertSame (aBuffered, StreamHelper.getBuffered (aBuffered));
    }
    try (final NonBlockingStringReader aReader = new NonBlockingStringReader (TEXT))
    {
      final Reader aBuffered = StreamHelper.getBuffered (aReader);
      assertTrue (StreamHelper.isBuffered (aBuffered));
      assertSame (aBuffered, StreamHelper.getBuffered (aBuffered));
    }
    try (final NonBlockingStringWriter aWriter = new NonBlockingStringWriter ())
    {
      final Writer aBuffered = StreamHelper.getBuffered (aWriter);
      assertTrue (StreamHelper.isBuffered (aBuffered));
      assertSame (aBuffered, StreamHelper.getBuffered (aBuffered));
    }
  }

  @Test
  public void testCopyByteStreamBuilder () throws IOException
  {
    // The full builder API
    final MutableLong aCount = new MutableLong (0);
    final AtomicLong aProgress = new AtomicLong (0);

    try (final NonBlockingByteArrayOutputStream aOS = new NonBlockingByteArrayOutputStream ())
    {
      assertSame (ESuccess.SUCCESS,
                  StreamHelper.copyByteStream ()
                              .from (new NonBlockingByteArrayInputStream (PAYLOAD))
                              .closeFrom (true)
                              .to (aOS)
                              .closeTo (false)
                              .buffer (new byte [8])
                              .unlimited ()
                              .copyByteCount (aCount)
                              .progressCallback (aProgress::set)
                              .exceptionCallback (ex -> fail ())
                              .build ());
      assertArrayEquals (PAYLOAD, aOS.toByteArray ());
      assertEquals (PAYLOAD.length, aCount.longValue ());
      assertTrue (aProgress.get () > 0);
    }

    // With a limit
    try (final NonBlockingByteArrayOutputStream aOS = new NonBlockingByteArrayOutputStream ())
    {
      StreamHelper.copyByteStream ().from (new NonBlockingByteArrayInputStream (PAYLOAD)).to (aOS).limit (5).build ();
      assertEquals (5, aOS.size ());
    }
    try (final NonBlockingByteArrayOutputStream aOS = new NonBlockingByteArrayOutputStream ())
    {
      StreamHelper.copyByteStream ()
                  .from (new NonBlockingByteArrayInputStream (PAYLOAD))
                  .to (aOS)
                  .limit (Long.valueOf (5))
                  .build ();
      assertEquals (5, aOS.size ());
    }

    // A null source or destination is a failure
    try (final NonBlockingByteArrayOutputStream aOS = new NonBlockingByteArrayOutputStream ())
    {
      assertSame (ESuccess.FAILURE, StreamHelper.copyByteStream ().from (null).to (aOS).build ());
      assertSame (ESuccess.FAILURE,
                  StreamHelper.copyByteStream ()
                              .from (new NonBlockingByteArrayInputStream (PAYLOAD))
                              .to (null)
                              .build ());
    }
  }

  @Test
  public void testCopyCharStreamBuilder () throws IOException
  {
    final MutableLong aCount = new MutableLong (0);
    final AtomicLong aProgress = new AtomicLong (0);

    try (final NonBlockingStringWriter aWriter = new NonBlockingStringWriter ())
    {
      assertSame (ESuccess.SUCCESS,
                  StreamHelper.copyCharStream ()
                              .from (new NonBlockingStringReader (TEXT))
                              .closeFrom (true)
                              .to (aWriter)
                              .closeTo (false)
                              .buffer (new char [8])
                              .unlimited ()
                              .copyCharCount (aCount)
                              .progressCallback (aProgress::set)
                              .exceptionCallback (ex -> fail ())
                              .build ());
      assertEquals (TEXT, aWriter.getAsString ());
      assertEquals (TEXT.length (), aCount.longValue ());
    }

    try (final NonBlockingStringWriter aWriter = new NonBlockingStringWriter ())
    {
      StreamHelper.copyCharStream ().from (new NonBlockingStringReader (TEXT)).to (aWriter).limit (5).build ();
      assertEquals (5, aWriter.getAsString ().length ());
    }
    try (final NonBlockingStringWriter aWriter = new NonBlockingStringWriter ())
    {
      StreamHelper.copyCharStream ()
                  .from (new NonBlockingStringReader (TEXT))
                  .to (aWriter)
                  .limit (Long.valueOf (5))
                  .build ();
      assertEquals (5, aWriter.getAsString ().length ());
    }

    try (final NonBlockingStringWriter aWriter = new NonBlockingStringWriter ())
    {
      assertSame (ESuccess.FAILURE, StreamHelper.copyCharStream ().from (null).to (aWriter).build ());
      assertSame (ESuccess.FAILURE,
                  StreamHelper.copyCharStream ().from (new NonBlockingStringReader (TEXT)).to (null).build ());
    }
  }

  @Test
  public void testGetCopyAndLimits () throws IOException
  {
    try (final NonBlockingByteArrayOutputStream aCopy = StreamHelper.getCopy (new NonBlockingByteArrayInputStream (PAYLOAD)))
    {
      assertArrayEquals (PAYLOAD, aCopy.toByteArray ());
    }
    try (final NonBlockingByteArrayOutputStream aCopy = StreamHelper.getCopyWithLimit (new NonBlockingByteArrayInputStream (PAYLOAD),
                                                                                       5))
    {
      assertEquals (5, aCopy.size ());
    }

    try (final NonBlockingStringWriter aCopy = StreamHelper.getCopy (new NonBlockingStringReader (TEXT)))
    {
      assertEquals (TEXT, aCopy.getAsString ());
    }
    try (final NonBlockingStringWriter aCopy = StreamHelper.getCopyWithLimit (new NonBlockingStringReader (TEXT), 5))
    {
      assertEquals (5, aCopy.getAsString ().length ());
    }
  }

  @Test
  public void testGetAllBytesAndCharacters ()
  {
    assertNull (StreamHelper.getAllBytes ((IHasInputStream) null));
    assertNull (StreamHelper.getAllBytes ((InputStream) null));
    assertNull (StreamHelper.getAllBytesAsString ((IHasInputStream) null, StandardCharsets.ISO_8859_1));
    assertNull (StreamHelper.getAllBytesAsString ((InputStream) null, StandardCharsets.ISO_8859_1));
    assertNull (StreamHelper.getAllCharacters (null));
    assertNull (StreamHelper.getAllCharactersAsString (null));

    assertArrayEquals (PAYLOAD, StreamHelper.getAllBytes (HasInputStream.create (PAYLOAD)));
    assertEquals (TEXT,
                  StreamHelper.getAllBytesAsString (HasInputStream.create (PAYLOAD), StandardCharsets.ISO_8859_1));
    assertArrayEquals (TEXT.toCharArray (), StreamHelper.getAllCharacters (new NonBlockingStringReader (TEXT)));
  }

  @Test
  public void testGetAvailable () throws IOException
  {
    assertEquals (0, StreamHelper.getAvailable (null));
    try (final NonBlockingByteArrayInputStream aIS = new NonBlockingByteArrayInputStream (PAYLOAD))
    {
      assertEquals (PAYLOAD.length, StreamHelper.getAvailable (aIS));
    }
  }

  @Test
  public void testWriteStream () throws IOException
  {
    try (final NonBlockingByteArrayOutputStream aOS = new NonBlockingByteArrayOutputStream ())
    {
      assertSame (ESuccess.SUCCESS, StreamHelper.writeStream (aOS, PAYLOAD));
      assertArrayEquals (PAYLOAD, aOS.toByteArray ());
    }
    try (final NonBlockingByteArrayOutputStream aOS = new NonBlockingByteArrayOutputStream ())
    {
      assertSame (ESuccess.SUCCESS, StreamHelper.writeStream (aOS, PAYLOAD, 0, 5));
      assertEquals (5, aOS.size ());
    }
    try (final NonBlockingByteArrayOutputStream aOS = new NonBlockingByteArrayOutputStream ())
    {
      assertSame (ESuccess.SUCCESS, StreamHelper.writeStream (aOS, TEXT, StandardCharsets.ISO_8859_1));
      assertArrayEquals (PAYLOAD, aOS.toByteArray ());
    }
  }

  @Test
  public void testSkipAndReadFully () throws IOException
  {
    try (final NonBlockingByteArrayInputStream aIS = new NonBlockingByteArrayInputStream (PAYLOAD))
    {
      StreamHelper.skipFully (aIS, 5);
      final byte [] aBuf = new byte [5];
      assertEquals (5, StreamHelper.readFully (aIS, aBuf));
      assertEquals (5, StreamHelper.readFully (aIS, aBuf, 0, 5));
    }

    // Skipping beyond the end fails
    try (final NonBlockingByteArrayInputStream aIS = new NonBlockingByteArrayInputStream (PAYLOAD))
    {
      try
      {
        StreamHelper.skipFully (aIS, PAYLOAD.length + 10);
        fail ();
      }
      catch (final EOFException ex)
      {
        // expected
      }
    }
  }

  @Test
  public void testReadUntilEOF () throws IOException
  {
    final AtomicLong aBytes = new AtomicLong (0);
    StreamHelper.readUntilEOF (new NonBlockingByteArrayInputStream (PAYLOAD),
                               (aBuf, nBytes) -> aBytes.addAndGet (nBytes));
    assertEquals (PAYLOAD.length, aBytes.get ());

    aBytes.set (0);
    StreamHelper.readUntilEOF (new NonBlockingByteArrayInputStream (PAYLOAD),
                               new byte [4],
                               (aBuf, nBytes) -> aBytes.addAndGet (nBytes));
    assertEquals (PAYLOAD.length, aBytes.get ());

    final AtomicLong aChars = new AtomicLong (0);
    StreamHelper.readUntilEOF (new NonBlockingStringReader (TEXT), (aBuf, nChars) -> aChars.addAndGet (nChars));
    assertEquals (TEXT.length (), aChars.get ());

    aChars.set (0);
    StreamHelper.readUntilEOF (new NonBlockingStringReader (TEXT),
                               new char [4],
                               (aBuf, nChars) -> aChars.addAndGet (nChars));
    assertEquals (TEXT.length (), aChars.get ());
  }

  @Test
  public void testSafeUTF () throws IOException
  {
    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
         final DataOutputStream aDOS = new DataOutputStream (aBAOS))
    {
      StreamHelper.writeSafeUTF (aDOS, TEXT);
      StreamHelper.writeSafeUTF (aDOS, null);
      StreamHelper.writeSafeUTF (aDOS, "");

      try (final DataInputStream aDIS = new DataInputStream (new NonBlockingByteArrayInputStream (aBAOS.toByteArray ())))
      {
        assertEquals (TEXT, StreamHelper.readSafeUTF (aDIS));
        assertNull (StreamHelper.readSafeUTF (aDIS));
        assertEquals ("", StreamHelper.readSafeUTF (aDIS));
      }
    }
  }

  @Test
  public void testCheckForInvalidFilterInputStream ()
  {
    assertNull (StreamHelper.checkForInvalidFilterInputStream (null));
    try (final NonBlockingByteArrayInputStream aIS = new NonBlockingByteArrayInputStream (PAYLOAD))
    {
      assertSame (aIS, StreamHelper.checkForInvalidFilterInputStream (aIS));
    }
  }
}

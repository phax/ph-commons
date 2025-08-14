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
package com.helger.commons.io.stream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Closeable;
import java.io.EOFException;
import java.io.FilterOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.base.CGlobal;
import com.helger.base.io.iface.IHasInputStream;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.io.stream.WrappedInputStream;
import com.helger.base.io.stream.WrappedReader;
import com.helger.base.mock.exception.MockIOException;
import com.helger.base.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.nonblocking.NonBlockingStringReader;
import com.helger.base.nonblocking.NonBlockingStringWriter;
import com.helger.base.numeric.mutable.MutableLong;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.streamprovider.ByteArrayInputStreamProvider;

/**
 * Test class for class {@link StreamHelperExt}.
 *
 * @author Philip Helger
 */
public final class StreamHelperTest
{
  @Test
  public void testIsKnownEOFException ()
  {
    assertFalse (StreamHelper.isKnownEOFException ((Throwable) null));
    assertFalse (StreamHelper.isKnownEOFException ((Class <?>) null));
    assertTrue (StreamHelper.isKnownEOFException (new EOFException ()));
    assertTrue (StreamHelper.isKnownEOFException (EOFException.class));
    assertFalse (StreamHelper.isKnownEOFException (new IllegalArgumentException ()));
    assertFalse (StreamHelper.isKnownEOFException (StreamHelperExt.class));
  }

  /**
   * Test method close
   */
  @Test
  public void testClose ()
  {
    // close null stream
    assertFalse (StreamHelper.close ((Closeable) null).isSuccess ());

    // close stream with exception
    assertFalse (StreamHelper.close (new MockThrowingCloseable ()).isSuccess ());

    // close without exception
    assertTrue (StreamHelper.close (new MockCloseable ()).isSuccess ());

    final MockCloseableWithState c = new MockCloseableWithState ();
    assertTrue (StreamHelper.close (c).isSuccess ());
    assertTrue (c.isClosed ());
    assertTrue (c.isFlushed ());
  }

  /**
   * Test method closeWithoutFlush
   */
  @Test
  public void testCloseWithoutFlush ()
  {
    // close null stream
    assertFalse (StreamHelper.closeWithoutFlush (null).isSuccess ());

    // close stream with exception
    assertFalse (StreamHelper.closeWithoutFlush (new MockThrowingCloseable ()).isSuccess ());

    // close without exception
    assertTrue (StreamHelper.closeWithoutFlush (new MockCloseable ()).isSuccess ());

    final MockCloseableWithState c = new MockCloseableWithState ();
    assertTrue (StreamHelper.closeWithoutFlush (c).isSuccess ());
    assertTrue (c.isClosed ());
    assertFalse (c.isFlushed ());
  }

  /**
   * Test method flush
   */
  @Test
  public void testFlush ()
  {
    // flush null stream
    assertFalse (StreamHelper.flush ((Flushable) null).isSuccess ());

    // flush stream with exception
    assertFalse (StreamHelper.flush (new MockThrowingFlushable ()).isSuccess ());

    // flush without exception
    assertTrue (StreamHelper.flush (new MockFlushable ()).isSuccess ());

    final MockCloseableWithState c = new MockCloseableWithState ();
    assertTrue (StreamHelper.flush (c).isSuccess ());
    assertFalse (c.isClosed ());
    assertTrue (c.isFlushed ());

    StreamHelper.close (new FilterOutputStream (null));
  }

  /**
   * Test method copyInputStreamToOutputStream
   */
  @Test
  public void testCopyInputStreamToOutputStream ()
  {
    final byte [] aInput = "Hallo".getBytes (StandardCharsets.ISO_8859_1);
    final NonBlockingByteArrayInputStream aBAIS = new NonBlockingByteArrayInputStream (aInput);
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    assertTrue (StreamHelperExt.copyInputStreamToOutputStream (aBAIS, aBAOS).isSuccess ());
    assertArrayEquals (aInput, aBAOS.toByteArray ());

    // try with null streams
    assertTrue (StreamHelperExt.copyInputStreamToOutputStream (aBAIS, null).isFailure ());
    assertTrue (StreamHelperExt.copyInputStreamToOutputStream (null, aBAOS).isFailure ());
    assertTrue (StreamHelperExt.copyByteStream ()
                               .from (aBAIS)
                               .closeFrom (true)
                               .to (aBAOS)
                               .closeTo (false)
                               .buffer (new byte [10])
                               .build ()
                               .isSuccess ());
    final MutableLong aML = new MutableLong (0);
    aBAIS.reset ();
    assertTrue (StreamHelperExt.copyByteStream ()
                               .from (aBAIS)
                               .closeFrom (true)
                               .to (aBAOS)
                               .closeTo (false)
                               .buffer (new byte [10])
                               .copyByteCount (aML)
                               .build ()
                               .isSuccess ());
    assertEquals (aML.longValue (), aInput.length);

    // Must be a ByteArrayInputStream so that an IOException can be thrown!
    assertTrue (StreamHelperExt.copyInputStreamToOutputStream (new WrappedInputStream (aBAIS)
    {
      @Override
      public int read (final byte [] aBuf, final int nOfs, final int nLen) throws IOException
      {
        throw new MockIOException ();
      }
    }, aBAOS).isFailure ());

    // null buffer is handled internally
    assertTrue (StreamHelperExt.copyByteStream ()
                               .from (aBAIS)
                               .to (aBAOS)
                               .buffer ((byte []) null)
                               .build ()
                               .isSuccess ());

    // empty buffer is handled internally
    assertTrue (StreamHelperExt.copyByteStream ()
                               .from (aBAIS)
                               .to (aBAOS)
                               .buffer (CGlobal.EMPTY_BYTE_ARRAY)
                               .build ()
                               .isSuccess ());
  }

  @Test
  public void testCopyInputStreamToOutputStreamWithLimit ()
  {
    final byte [] aInput = "Hello12Bytes".getBytes (StandardCharsets.ISO_8859_1);
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    assertTrue (StreamHelperExt.copyByteStream ()
                               .from (new NonBlockingByteArrayInputStream (aInput))
                               .to (aBAOS)
                               .limit (5)
                               .build ()
                               .isSuccess ());
    assertEquals ("Hello", aBAOS.getAsString (StandardCharsets.ISO_8859_1));
    aBAOS.reset ();
    assertTrue (StreamHelperExt.copyByteStream ()
                               .from (new NonBlockingByteArrayInputStream (aInput))
                               .to (aBAOS)
                               .limit (7)
                               .build ()
                               .isSuccess ());
    assertEquals ("Hello12", aBAOS.getAsString (StandardCharsets.ISO_8859_1));
    aBAOS.reset ();
    assertTrue (StreamHelperExt.copyByteStream ()
                               .from (new NonBlockingByteArrayInputStream (aInput))
                               .to (aBAOS)
                               .limit (0)
                               .build ()
                               .isSuccess ());
    assertEquals ("", aBAOS.getAsString (StandardCharsets.ISO_8859_1));
    aBAOS.reset ();
    assertTrue (StreamHelperExt.copyByteStream ()
                               .from (new NonBlockingByteArrayInputStream (aInput))
                               .to (aBAOS)
                               .limit (9999)
                               .build ()
                               .isSuccess ());
    assertEquals ("Hello12Bytes", aBAOS.getAsString (StandardCharsets.ISO_8859_1));
    aBAOS.reset ();

    // Negative limit is ignored internally
    assertTrue (StreamHelperExt.copyByteStream ()
                               .from (new NonBlockingByteArrayInputStream (aInput))
                               .to (aBAOS)
                               .limit (-1)
                               .build ()
                               .isSuccess ());
  }

  @Test
  public void testGetAvailable ()
  {
    final byte [] aInput = "Hallo".getBytes (StandardCharsets.ISO_8859_1);
    assertEquals (5, StreamHelperExt.getAvailable (new NonBlockingByteArrayInputStream (aInput)));
    assertEquals (0, StreamHelperExt.getAvailable ((InputStream) null));
    assertEquals (0, StreamHelperExt.getAvailable (new WrappedInputStream (new NonBlockingByteArrayInputStream (aInput))
    {
      @Override
      public int available () throws IOException
      {
        throw new MockIOException ();
      }
    }));
  }

  @Test
  public void testGetAllBytesCharset ()
  {
    final String sInput = "Hallo";
    final byte [] aInput = sInput.getBytes (StandardCharsets.ISO_8859_1);
    assertArrayEquals (aInput, StreamHelperExt.getAllBytes (new ByteArrayInputStreamProvider (aInput)));
    assertArrayEquals (aInput, StreamHelperExt.getAllBytes (new NonBlockingByteArrayInputStream (aInput)));
    assertNull (StreamHelperExt.getAllBytes ((IHasInputStream) null));
    assertNull (StreamHelperExt.getAllBytes ((InputStream) null));

    assertEquals (sInput,
                  StreamHelperExt.getAllBytesAsString (new ByteArrayInputStreamProvider (aInput),
                                                       StandardCharsets.ISO_8859_1));
    assertEquals (sInput,
                  StreamHelperExt.getAllBytesAsString (new NonBlockingByteArrayInputStream (aInput),
                                                       StandardCharsets.ISO_8859_1));
    assertNull (StreamHelperExt.getAllBytesAsString ((IHasInputStream) null, StandardCharsets.ISO_8859_1));
    assertNull (StreamHelperExt.getAllBytesAsString ((InputStream) null, StandardCharsets.ISO_8859_1));
    try
    {
      StreamHelperExt.getAllBytesAsString (new NonBlockingByteArrayInputStream (aInput), (Charset) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {
      // Expected
    }
  }

  @Test
  public void testReadLines ()
  {
    assertNull (StreamHelperExt.readStreamLines ((IReadableResource) null, StandardCharsets.ISO_8859_1));
    assertNull (StreamHelperExt.readStreamLines (new ClassPathResource ("gibts-ned"), StandardCharsets.ISO_8859_1));
    assertNull (StreamHelperExt.readStreamLines (ClassPathResource.getInputStream ("gibts-ned"),
                                                 StandardCharsets.ISO_8859_1));

    final IReadableResource aRes = new ClassPathResource ("streamutils-lines.txt");

    // Read all lines
    ICommonsList <String> aLines = StreamHelperExt.readStreamLines (aRes, StandardCharsets.UTF_8);
    assertNotNull (aLines);
    assertEquals (10, aLines.size ());
    for (int i = 0; i < 10; ++i)
      assertEquals (Integer.toString (i + 1), aLines.get (i));

    // Read only partial amount of lines
    aLines = StreamHelperExt.readStreamLines (aRes, StandardCharsets.UTF_8, 2, 4);
    assertNotNull (aLines);
    assertEquals (4, aLines.size ());
    for (int i = 0; i < 4; ++i)
      assertEquals (Integer.toString (i + 3), aLines.get (i));

    // Skip more than available
    aLines = StreamHelperExt.readStreamLines (aRes, StandardCharsets.UTF_8, Integer.MAX_VALUE, Integer.MAX_VALUE);
    assertNotNull (aLines);
    assertEquals (0, aLines.size ());

    // Try to read more than available
    aLines = StreamHelperExt.readStreamLines (aRes, StandardCharsets.UTF_8, 9, Integer.MAX_VALUE);
    assertNotNull (aLines);
    assertEquals (1, aLines.size ());

    // Read 0 lines
    aLines = StreamHelperExt.readStreamLines (aRes, StandardCharsets.UTF_8, 0, 0);
    assertNotNull (aLines);
    assertEquals (0, aLines.size ());

    try
    {
      // Lines to skip may not be negative
      StreamHelperExt.readStreamLines (aRes, StandardCharsets.UTF_8, -1, 4);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }

    try
    {
      // Lines to read may not be negative
      StreamHelperExt.readStreamLines (aRes, StandardCharsets.UTF_8, 0, -2);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testGetAllBytesAsString ()
  {
    final IReadableResource aRes = new ClassPathResource ("streamutils-bytes.txt");
    assertEquals ("abc", StreamHelperExt.getAllBytesAsString (aRes, StandardCharsets.UTF_8));
    // Non existing
    assertNull (StreamHelperExt.getAllBytesAsString (new ClassPathResource ("bla fasel"), StandardCharsets.UTF_8));
  }

  @Test
  public void testCopyReaderToWriter ()
  {
    final String sInput = "Hallo";
    NonBlockingStringReader aBAIS = new NonBlockingStringReader (sInput);
    final NonBlockingStringWriter aBAOS = new NonBlockingStringWriter ();
    assertTrue (StreamHelperExt.copyReaderToWriter (aBAIS, aBAOS).isSuccess ());
    assertEquals (sInput, aBAOS.getAsString ());

    // try with null streams
    aBAIS = new NonBlockingStringReader (sInput);
    assertTrue (StreamHelperExt.copyReaderToWriter (aBAIS, null).isFailure ());
    assertTrue (StreamHelperExt.copyReaderToWriter (null, aBAOS).isFailure ());
    aBAIS = new NonBlockingStringReader (sInput);
    assertTrue (StreamHelperExt.copyCharStream ().from (aBAIS).to (aBAOS).buffer (new char [10]).build ().isSuccess ());
    final MutableLong aML = new MutableLong (0);
    aBAIS = new NonBlockingStringReader (sInput);
    assertTrue (StreamHelperExt.copyCharStream ()
                               .from (aBAIS)
                               .closeFrom (false)
                               .to (aBAOS)
                               .closeTo (false)
                               .buffer (new char [10])
                               .copyCharCount (aML)
                               .build ()
                               .isSuccess ());
    assertEquals (aML.longValue (), sInput.length ());

    // Must be a ByteArrayReader so that an IOException can be thrown!
    aBAIS = new NonBlockingStringReader (sInput);
    assertTrue (StreamHelperExt.copyReaderToWriter (new WrappedReader (aBAIS)
    {
      @Override
      public int read (final char [] aBuf, final int nOfs, final int nLen) throws IOException
      {
        throw new MockIOException ();
      }
    }, aBAOS).isFailure ());

    // null buffer
    aBAIS = new NonBlockingStringReader (sInput);
    assertTrue (StreamHelperExt.copyCharStream ()
                               .from (aBAIS)
                               .to (aBAOS)
                               .buffer ((char []) null)
                               .build ()
                               .isSuccess ());

    // empty buffer
    aBAIS = new NonBlockingStringReader (sInput);
    assertTrue (StreamHelperExt.copyCharStream ().from (aBAIS).to (aBAOS).buffer (new char [0]).build ().isSuccess ());
  }

  @Test
  public void testCopyReaderToWriterWithLimit ()
  {
    final String sSrc = "Hello12Chars";
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    assertTrue (StreamHelperExt.copyCharStream ()
                               .from (StreamHelper.createReader (sSrc))
                               .to (aSW)
                               .limit (5)
                               .build ()
                               .isSuccess ());
    assertEquals ("Hello", aSW.getAsString ());
    aSW.reset ();
    assertTrue (StreamHelperExt.copyCharStream ()
                               .from (StreamHelper.createReader (sSrc))
                               .to (aSW)
                               .limit (7)
                               .build ()
                               .isSuccess ());
    assertEquals ("Hello12", aSW.getAsString ());
    aSW.reset ();
    assertTrue (StreamHelperExt.copyCharStream ()
                               .from (StreamHelper.createReader (sSrc))
                               .to (aSW)
                               .limit (0)
                               .build ()
                               .isSuccess ());
    assertEquals ("", aSW.getAsString ());
    aSW.reset ();
    assertTrue (StreamHelperExt.copyCharStream ()
                               .from (StreamHelper.createReader (sSrc))
                               .to (aSW)
                               .limit (9999)
                               .build ()
                               .isSuccess ());
    assertEquals (sSrc, aSW.getAsString ());
    aSW.reset ();

    // negative limit is allowed!
    assertTrue (StreamHelperExt.copyCharStream ()
                               .from (StreamHelper.createReader (sSrc))
                               .to (aSW)
                               .limit (-1)
                               .build ()
                               .isSuccess ());
  }

  @Test
  public void testGetAllCharacters ()
  {
    final String sInput = "Hallo";
    assertEquals (sInput, StreamHelperExt.getAllCharactersAsString (new NonBlockingStringReader (sInput)));
    assertNull (StreamHelperExt.getAllCharactersAsString ((Reader) null));

    assertArrayEquals (sInput.toCharArray (), StreamHelperExt.getAllCharacters (new NonBlockingStringReader (sInput)));
    assertNull (StreamHelperExt.getAllCharacters ((Reader) null));
  }

  @Test
  public void testWriteStream ()
  {
    final byte [] buf = "abcde".getBytes (StandardCharsets.ISO_8859_1);
    final NonBlockingByteArrayOutputStream os = new NonBlockingByteArrayOutputStream ();
    assertTrue (StreamHelperExt.writeStream (os, buf).isSuccess ());
    assertTrue (StreamHelperExt.writeStream (os, buf, 0, buf.length).isSuccess ());
    assertTrue (StreamHelperExt.writeStream (os, "anyäöü", StandardCharsets.ISO_8859_1).isSuccess ());

    // The byte array version
    try
    {
      StreamHelperExt.writeStream (null, buf, 0, buf.length);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StreamHelperExt.writeStream (os, null, 0, buf.length);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StreamHelperExt.writeStream (os, buf, -1, buf.length);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      StreamHelperExt.writeStream (os, buf, 0, -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      StreamHelperExt.writeStream (os, buf, 0, buf.length + 1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      StreamHelperExt.writeStream (os, null, StandardCharsets.ISO_8859_1);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    // Throwing output stream
    final NonBlockingByteArrayOutputStream os2 = new NonBlockingByteArrayOutputStream ()
    {
      @Override
      public void flush () throws IOException
      {
        throw new MockIOException ();
      }
    };
    assertFalse (StreamHelperExt.writeStream (os2, buf).isSuccess ());
  }

  @Test
  public void testCreateReader ()
  {
    assertNull (StreamHelper.createReader (null, StandardCharsets.ISO_8859_1));
    final NonBlockingByteArrayInputStream is = new NonBlockingByteArrayInputStream (new byte [4]);
    assertNotNull (StreamHelper.createReader (is, StandardCharsets.ISO_8859_1));

    try
    {
      StreamHelper.createReader (is, (Charset) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testCreateWriter ()
  {
    assertNull (StreamHelper.createWriter (null, StandardCharsets.ISO_8859_1));
    final NonBlockingByteArrayOutputStream os = new NonBlockingByteArrayOutputStream ();
    assertNotNull (StreamHelper.createWriter (os, StandardCharsets.ISO_8859_1));

    try
    {
      StreamHelper.createWriter (os, (Charset) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}

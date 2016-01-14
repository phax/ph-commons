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
import java.util.List;

import org.junit.Test;

import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.exception.mock.MockIOException;
import com.helger.commons.io.IHasInputStream;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.streamprovider.ByteArrayInputStreamProvider;
import com.helger.commons.mutable.MutableLong;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link StreamHelper}.
 *
 * @author Philip Helger
 */
@SuppressFBWarnings ("SE_BAD_FIELD_INNER_CLASS")
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
    assertFalse (StreamHelper.isKnownEOFException (StreamHelper.class));
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
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testCopyInputStreamToOutputStream ()
  {
    final byte [] aInput = CharsetManager.getAsBytes ("Hallo", CCharset.CHARSET_ISO_8859_1_OBJ);
    final NonBlockingByteArrayInputStream bais = new NonBlockingByteArrayInputStream (aInput);
    final NonBlockingByteArrayOutputStream baos = new NonBlockingByteArrayOutputStream ();
    assertTrue (StreamHelper.copyInputStreamToOutputStream (bais, baos).isSuccess ());
    assertArrayEquals (aInput, baos.toByteArray ());

    // try with null streams
    assertTrue (StreamHelper.copyInputStreamToOutputStream (bais, null).isFailure ());
    assertTrue (StreamHelper.copyInputStreamToOutputStream (null, baos).isFailure ());
    assertTrue (StreamHelper.copyInputStreamToOutputStream (bais, baos, new byte [10]).isSuccess ());
    final MutableLong aML = new MutableLong ();
    bais.reset ();
    assertTrue (StreamHelper.copyInputStreamToOutputStream (bais, baos, new byte [10], aML).isSuccess ());
    assertEquals (aML.longValue (), aInput.length);

    // Must be a ByteArrayInputStream so that an IOException can be thrown!
    assertTrue (StreamHelper.copyInputStreamToOutputStream (new WrappedInputStream (bais)
    {
      @Override
      public int read (final byte [] aBuf, final int nOfs, final int nLen) throws IOException
      {
        throw new MockIOException ();
      }
    }, baos).isFailure ());

    try
    {
      // null buffer
      StreamHelper.copyInputStreamToOutputStream (bais, baos, (byte []) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // empty buffer
      StreamHelper.copyInputStreamToOutputStream (bais, baos, new byte [0]);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
  public void testCopyInputStreamToOutputStreamWithLimit ()
  {
    final byte [] aInput = CharsetManager.getAsBytes ("Hello12Bytes", CCharset.CHARSET_ISO_8859_1_OBJ);
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    assertTrue (StreamHelper.copyInputStreamToOutputStreamWithLimit (new NonBlockingByteArrayInputStream (aInput),
                                                                     aBAOS,
                                                                     5)
                            .isSuccess ());
    assertEquals ("Hello", aBAOS.getAsString (CCharset.CHARSET_ISO_8859_1_OBJ));
    aBAOS.reset ();
    assertTrue (StreamHelper.copyInputStreamToOutputStreamWithLimit (new NonBlockingByteArrayInputStream (aInput),
                                                                     aBAOS,
                                                                     7)
                            .isSuccess ());
    assertEquals ("Hello12", aBAOS.getAsString (CCharset.CHARSET_ISO_8859_1_OBJ));
    aBAOS.reset ();
    assertTrue (StreamHelper.copyInputStreamToOutputStreamWithLimit (new NonBlockingByteArrayInputStream (aInput),
                                                                     aBAOS,
                                                                     0)
                            .isSuccess ());
    assertEquals ("", aBAOS.getAsString (CCharset.CHARSET_ISO_8859_1_OBJ));
    aBAOS.reset ();
    assertTrue (StreamHelper.copyInputStreamToOutputStreamWithLimit (new NonBlockingByteArrayInputStream (aInput),
                                                                     aBAOS,
                                                                     9999)
                            .isSuccess ());
    assertEquals ("Hello12Bytes", aBAOS.getAsString (CCharset.CHARSET_ISO_8859_1_OBJ));
    aBAOS.reset ();

    try
    {
      // Negative limit is not allowed
      StreamHelper.copyInputStreamToOutputStreamWithLimit (new NonBlockingByteArrayInputStream (aInput), aBAOS, -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testGetAvailable ()
  {
    final byte [] aInput = CharsetManager.getAsBytes ("Hallo", CCharset.CHARSET_ISO_8859_1_OBJ);
    assertEquals (5, StreamHelper.getAvailable (new NonBlockingByteArrayInputStream (aInput)));
    assertEquals (0, StreamHelper.getAvailable ((InputStream) null));
    assertEquals (0, StreamHelper.getAvailable (new WrappedInputStream (new NonBlockingByteArrayInputStream (aInput))
    {
      @Override
      public int available () throws IOException
      {
        throw new MockIOException ();
      }
    }));
  }

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testGetAllBytesCharset ()
  {
    final String sInput = "Hallo";
    final byte [] aInput = CharsetManager.getAsBytes (sInput, CCharset.CHARSET_ISO_8859_1_OBJ);
    assertArrayEquals (aInput, StreamHelper.getAllBytes (new ByteArrayInputStreamProvider (aInput)));
    assertArrayEquals (aInput, StreamHelper.getAllBytes (new NonBlockingByteArrayInputStream (aInput)));
    assertNull (StreamHelper.getAllBytes ((IHasInputStream) null));
    assertNull (StreamHelper.getAllBytes ((InputStream) null));

    assertEquals (sInput,
                  StreamHelper.getAllBytesAsString (new ByteArrayInputStreamProvider (aInput),
                                                    CCharset.CHARSET_ISO_8859_1_OBJ));
    assertEquals (sInput,
                  StreamHelper.getAllBytesAsString (new NonBlockingByteArrayInputStream (aInput),
                                                    CCharset.CHARSET_ISO_8859_1_OBJ));
    assertNull (StreamHelper.getAllBytesAsString ((IHasInputStream) null, CCharset.CHARSET_ISO_8859_1_OBJ));
    assertNull (StreamHelper.getAllBytesAsString ((InputStream) null, CCharset.CHARSET_ISO_8859_1_OBJ));
    try
    {
      StreamHelper.getAllBytesAsString (new NonBlockingByteArrayInputStream (aInput), (Charset) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
  public void testReadLines ()
  {
    assertNull (StreamHelper.readStreamLines ((IReadableResource) null, CCharset.CHARSET_ISO_8859_1_OBJ));
    assertNull (StreamHelper.readStreamLines (new ClassPathResource ("gibts-ned"), CCharset.CHARSET_ISO_8859_1_OBJ));
    assertNull (StreamHelper.readStreamLines (ClassPathResource.getInputStream ("gibts-ned"),
                                              CCharset.CHARSET_ISO_8859_1_OBJ));

    final IReadableResource aRes = new ClassPathResource ("streamutils-lines");

    // Read all lines
    List <String> aLines = StreamHelper.readStreamLines (aRes, CCharset.CHARSET_UTF_8_OBJ);
    assertNotNull (aLines);
    assertEquals (10, aLines.size ());
    for (int i = 0; i < 10; ++i)
      assertEquals (Integer.toString (i + 1), aLines.get (i));

    // Read only partial amount of lines
    aLines = StreamHelper.readStreamLines (aRes, CCharset.CHARSET_UTF_8_OBJ, 2, 4);
    assertNotNull (aLines);
    assertEquals (4, aLines.size ());
    for (int i = 0; i < 4; ++i)
      assertEquals (Integer.toString (i + 3), aLines.get (i));

    // Skip more than available
    aLines = StreamHelper.readStreamLines (aRes, CCharset.CHARSET_UTF_8_OBJ, Integer.MAX_VALUE, Integer.MAX_VALUE);
    assertNotNull (aLines);
    assertEquals (0, aLines.size ());

    // Try to read more than available
    aLines = StreamHelper.readStreamLines (aRes, CCharset.CHARSET_UTF_8_OBJ, 9, Integer.MAX_VALUE);
    assertNotNull (aLines);
    assertEquals (1, aLines.size ());

    // Read 0 lines
    aLines = StreamHelper.readStreamLines (aRes, CCharset.CHARSET_UTF_8_OBJ, 0, 0);
    assertNotNull (aLines);
    assertEquals (0, aLines.size ());

    try
    {
      // Lines to skip may not be negative
      StreamHelper.readStreamLines (aRes, CCharset.CHARSET_UTF_8_OBJ, -1, 4);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }

    try
    {
      // Lines to read may not be negative
      StreamHelper.readStreamLines (aRes, CCharset.CHARSET_UTF_8_OBJ, 0, -2);
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
    final IReadableResource aRes = new ClassPathResource ("streamutils-bytes");
    assertEquals ("abc", StreamHelper.getAllBytesAsString (aRes, CCharset.CHARSET_UTF_8_OBJ));
    // Non existing
    assertNull (StreamHelper.getAllBytesAsString (new ClassPathResource ("bla fasel"), CCharset.CHARSET_UTF_8_OBJ));
  }

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testCopyReaderToWriter ()
  {
    final String sInput = "Hallo";
    NonBlockingStringReader bais = new NonBlockingStringReader (sInput);
    final NonBlockingStringWriter baos = new NonBlockingStringWriter ();
    assertTrue (StreamHelper.copyReaderToWriter (bais, baos).isSuccess ());
    assertEquals (sInput, baos.getAsString ());

    // try with null streams
    bais = new NonBlockingStringReader (sInput);
    assertTrue (StreamHelper.copyReaderToWriter (bais, null).isFailure ());
    assertTrue (StreamHelper.copyReaderToWriter (null, baos).isFailure ());
    bais = new NonBlockingStringReader (sInput);
    assertTrue (StreamHelper.copyReaderToWriter (bais, baos, new char [10]).isSuccess ());
    final MutableLong aML = new MutableLong ();
    bais = new NonBlockingStringReader (sInput);
    assertTrue (StreamHelper.copyReaderToWriter (bais, baos, new char [10], aML).isSuccess ());
    assertEquals (aML.longValue (), sInput.length ());

    // Must be a ByteArrayReader so that an IOException can be thrown!
    assertTrue (StreamHelper.copyReaderToWriter (new WrappedReader (bais)
    {
      @Override
      public int read (final char [] aBuf, final int nOfs, final int nLen) throws IOException
      {
        throw new MockIOException ();
      }
    }, baos).isFailure ());

    try
    {
      // null buffer
      StreamHelper.copyReaderToWriter (bais, baos, (char []) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // empty buffer
      StreamHelper.copyReaderToWriter (bais, baos, new char [0]);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testCopyReaderToWriterWithLimit ()
  {
    final String sSrc = "Hello12Chars";
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    assertTrue (StreamHelper.copyReaderToWriterWithLimit (StreamHelper.createReader (sSrc), aSW, 5).isSuccess ());
    assertEquals ("Hello", aSW.getAsString ());
    aSW.reset ();
    assertTrue (StreamHelper.copyReaderToWriterWithLimit (StreamHelper.createReader (sSrc), aSW, 7).isSuccess ());
    assertEquals ("Hello12", aSW.getAsString ());
    aSW.reset ();
    assertTrue (StreamHelper.copyReaderToWriterWithLimit (StreamHelper.createReader (sSrc), aSW, 0).isSuccess ());
    assertEquals ("", aSW.getAsString ());
    aSW.reset ();
    assertTrue (StreamHelper.copyReaderToWriterWithLimit (StreamHelper.createReader (sSrc), aSW, 9999).isSuccess ());
    assertEquals (sSrc, aSW.getAsString ());
    aSW.reset ();

    try
    {
      // negative limit is not allowed!
      StreamHelper.copyReaderToWriterWithLimit (StreamHelper.createReader (sSrc), aSW, -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testGetAllCharacters ()
  {
    final String sInput = "Hallo";
    assertEquals (sInput, StreamHelper.getAllCharactersAsString (new NonBlockingStringReader (sInput)));
    assertNull (StreamHelper.getAllCharactersAsString ((Reader) null));

    assertArrayEquals (sInput.toCharArray (), StreamHelper.getAllCharacters (new NonBlockingStringReader (sInput)));
    assertNull (StreamHelper.getAllCharacters ((Reader) null));
  }

  @Test
  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
  public void testWriteStream ()
  {
    final byte [] buf = CharsetManager.getAsBytes ("abcde", CCharset.CHARSET_ISO_8859_1_OBJ);
    final NonBlockingByteArrayOutputStream os = new NonBlockingByteArrayOutputStream ();
    assertTrue (StreamHelper.writeStream (os, buf).isSuccess ());
    assertTrue (StreamHelper.writeStream (os, buf, 0, buf.length).isSuccess ());
    assertTrue (StreamHelper.writeStream (os, "anyäöü", CCharset.CHARSET_ISO_8859_1_OBJ).isSuccess ());

    // The byte array version
    try
    {
      StreamHelper.writeStream (null, buf, 0, buf.length);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StreamHelper.writeStream (os, null, 0, buf.length);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      StreamHelper.writeStream (os, buf, -1, buf.length);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      StreamHelper.writeStream (os, buf, 0, -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      StreamHelper.writeStream (os, buf, 0, buf.length + 1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      StreamHelper.writeStream (os, null, CCharset.CHARSET_ISO_8859_1_OBJ);
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
    assertFalse (StreamHelper.writeStream (os2, buf).isSuccess ());
  }

  @Test
  public void testCreateReader ()
  {
    assertNull (StreamHelper.createReader (null, CCharset.CHARSET_ISO_8859_1_OBJ));
    final NonBlockingByteArrayInputStream is = new NonBlockingByteArrayInputStream (new byte [4]);
    assertNotNull (StreamHelper.createReader (is, CCharset.CHARSET_ISO_8859_1_OBJ));

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
    assertNull (StreamHelper.createWriter (null, CCharset.CHARSET_ISO_8859_1_OBJ));
    final NonBlockingByteArrayOutputStream os = new NonBlockingByteArrayOutputStream ();
    assertNotNull (StreamHelper.createWriter (os, CCharset.CHARSET_ISO_8859_1_OBJ));

    try
    {
      StreamHelper.createWriter (os, (Charset) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}

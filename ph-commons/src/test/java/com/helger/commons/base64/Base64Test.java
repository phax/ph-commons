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
package com.helger.commons.base64;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import org.junit.Test;

import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.FileOperations;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.commons.io.stream.NonBlockingByteArrayInputStream;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.string.StringHelper;

/**
 * Test class for class {@link Base64}.<br>
 * Base64 test code.<br>
 * Partly source:
 * http://iharder.sourceforge.net/current/java/base64/Base64Test.java
 */
public final class Base64Test
{
  @Test
  public void testEncodeBytes () throws IOException
  {
    final String sSource = "Hallo Welt! Ümläüte";
    final String sEncoded = Base64.encodeBytes (sSource.getBytes (StandardCharsets.ISO_8859_1));
    final byte [] aDecoded = Base64.decode (sEncoded);

    assertArrayEquals (sSource.getBytes (StandardCharsets.ISO_8859_1), aDecoded);
    final byte [] aSrc = "Hallo Wält".getBytes (StandardCharsets.UTF_8);
    final String sDst = Base64.encodeBytes (aSrc, 0, aSrc.length);
    assertEquals ("Hallo Wält", Base64.safeDecodeAsString (sDst, StandardCharsets.UTF_8));
  }

  @Test
  public void testEncodeWithBreakLines ()
  {
    final byte [] aSource = StringHelper.getRepeated ('a', 100).getBytes (StandardCharsets.ISO_8859_1);
    String sEncoded = Base64.safeEncodeBytes (aSource, Base64.DO_BREAK_LINES);
    assertEquals ("YWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFh\n" +
                  "YWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYQ==",
                  sEncoded);

    // Check that it can be read again
    byte [] aReadBytes = StreamHelper.getAllBytes (new Base64InputStream (new NonBlockingByteArrayInputStream (sEncoded.getBytes (Base64.PREFERRED_ENCODING))));
    assertArrayEquals (aSource, aReadBytes);

    sEncoded = Base64.safeEncodeBytes (aSource, Base64.DO_BREAK_LINES | Base64.DO_NEWLINE_CRLF);
    assertEquals ("YWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFh\r\n" +
                  "YWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYQ==",
                  sEncoded);

    // Check that it can be read again
    aReadBytes = StreamHelper.getAllBytes (new Base64InputStream (new NonBlockingByteArrayInputStream (sEncoded.getBytes (Base64.PREFERRED_ENCODING))));
    assertArrayEquals (aSource, aReadBytes);
  }

  @Test
  public void testEncodeBytesGZIP () throws IOException
  {
    final String sSource = "Hallo Welt! Ümläüte";
    final String sEncoded = Base64.encodeBytes (sSource.getBytes (StandardCharsets.ISO_8859_1), Base64.GZIP);
    final byte [] aDecoded = Base64.decode (sEncoded);
    assertArrayEquals (sSource.getBytes (StandardCharsets.ISO_8859_1), aDecoded);
  }

  private byte [] _createData (final int length)
  {
    final byte [] bytes = new byte [length];
    new SecureRandom ().nextBytes (bytes);
    return bytes;
  }

  private void _runStreamTest (final int length) throws Exception
  {
    final byte [] aData = _createData (length);

    try (final NonBlockingByteArrayOutputStream aOutBytes = new NonBlockingByteArrayOutputStream ())
    {
      try (final Base64OutputStream aOS = new Base64OutputStream (aOutBytes))
      {
        aOS.write (aData);
        aOS.suspendEncoding ();
        aOS.resumeEncoding ();
      }
      final byte [] aEncoded = aOutBytes.toByteArray ();
      byte [] aDecoded = Base64.decode (aEncoded);
      assertArrayEquals (aData, aDecoded);

      aOutBytes.reset ();
      try (final Base64InputStream aIS = new Base64InputStream (new NonBlockingByteArrayInputStream (aEncoded)))
      {
        final byte [] aBuffer = new byte [3];
        for (int n = aIS.read (aBuffer); n > 0; n = aIS.read (aBuffer))
          aOutBytes.write (aBuffer, 0, n);
      }
      aDecoded = aOutBytes.toByteArray ();
      assertArrayEquals (aData, aDecoded);
    }
  }

  @Test
  public void testStreams_0_100 () throws Exception
  {
    for (int i = 0; i < 100; ++i)
      _runStreamTest (i);
  }

  @Test
  public void testStreams_100_2000 () throws Exception
  {
    for (int i = 100; i < 2000; i += 250)
      _runStreamTest (i);
  }

  @Test
  public void testStreams_2000_80000 () throws Exception
  {
    for (int i = 2000; i < 80000; i += 1000)
      _runStreamTest (i);
  }

  @Test
  public void testEncodeFileToFile () throws IOException
  {
    final File f1 = new File ("base64.decoded");
    final File f2 = new File ("base64.encoded");
    try
    {
      assertFalse (FileHelper.existsFile (f2));
      SimpleFileIO.writeFile (f1, "Hallo Wält", StandardCharsets.UTF_8);
      Base64.encodeFileToFile (f1.getAbsolutePath (), f2.getAbsoluteFile ());
      assertTrue (FileHelper.existsFile (f2));
      final String sEncoded = SimpleFileIO.getFileAsString (f2, StandardCharsets.UTF_8);
      assertEquals ("Hallo Wält", Base64.safeDecodeAsString (sEncoded, StandardCharsets.UTF_8));
    }
    finally
    {
      FileOperations.deleteFile (f1);
      FileOperations.deleteFile (f2);
    }
  }

  @Test
  public void testEncodeToFile () throws IOException
  {
    final File f2 = new File ("base64.encoded");
    try
    {
      assertFalse (FileHelper.existsFile (f2));
      final String sDecoded = "Hallo Wält";
      Base64.encodeToFile (sDecoded.getBytes (StandardCharsets.UTF_8), f2.getAbsoluteFile ());
      assertTrue (FileHelper.existsFile (f2));
      final String sEncoded = SimpleFileIO.getFileAsString (f2, StandardCharsets.UTF_8);
      assertEquals ("Hallo Wält", Base64.safeDecodeAsString (sEncoded, StandardCharsets.UTF_8));
    }
    finally
    {
      FileOperations.deleteFile (f2);
    }
  }

  @Test
  public void testDecodeFileToFile () throws IOException
  {
    final File f1 = new File ("base64.encoded");
    final File f2 = new File ("base64.decoded");
    try
    {
      assertFalse (FileHelper.existsFile (f2));
      SimpleFileIO.writeFile (f1, Base64.safeEncode ("Hallo Wält", StandardCharsets.UTF_8).getBytes (StandardCharsets.ISO_8859_1));
      Base64.decodeFileToFile (f1.getAbsolutePath (), f2.getAbsoluteFile ());
      assertTrue (FileHelper.existsFile (f2));
      final String sDecoded = SimpleFileIO.getFileAsString (f2, StandardCharsets.UTF_8);
      assertEquals ("Hallo Wält", sDecoded);
    }
    finally
    {
      FileOperations.deleteFile (f1);
      FileOperations.deleteFile (f2);
    }
  }

  @Test
  public void testDecodeToFile () throws IOException
  {
    final File f2 = new File ("base64.decoded");
    try
    {
      assertFalse (FileHelper.existsFile (f2));
      final String sEncoded = Base64.safeEncode ("Hallo Wält", StandardCharsets.UTF_8);
      Base64.decodeToFile (sEncoded, f2.getAbsoluteFile ());
      assertTrue (FileHelper.existsFile (f2));
      final String sDecoded = SimpleFileIO.getFileAsString (f2, StandardCharsets.UTF_8);
      assertEquals ("Hallo Wält", sDecoded);
    }
    finally
    {
      FileOperations.deleteFile (f2);
    }
  }

  @Test
  public void testEncodeByteBuffer ()
  {
    final ByteBuffer aSrc = ByteBuffer.wrap ("Hallo Wält".getBytes (StandardCharsets.UTF_8));
    final ByteBuffer aDst = ByteBuffer.allocate (aSrc.capacity () * 2);
    Base64.encode (aSrc, aDst);
    assertEquals ("Hallo Wält", Base64.safeDecodeAsString (aDst.array (), aDst.arrayOffset (), aDst.position (), StandardCharsets.UTF_8));
  }

  @Test
  public void testEncodeCharBuffer ()
  {
    final ByteBuffer aSrc = ByteBuffer.wrap ("Hallo Wält".getBytes (StandardCharsets.UTF_8));
    final CharBuffer aDst = CharBuffer.allocate (aSrc.capacity () * 2);
    Base64.encode (aSrc, aDst);
    assertEquals ("Hallo Wält", Base64.safeDecodeAsString (new String (aDst.array ()), StandardCharsets.UTF_8));
  }

  @Test
  public void testEncodeByteToByte ()
  {
    final byte [] aSrc = "Hallo Wält".getBytes (StandardCharsets.UTF_8);
    final byte [] aDst = Base64.encodeBytesToBytes (aSrc);
    assertEquals ("Hallo Wält", Base64.safeDecodeAsString (aDst, StandardCharsets.UTF_8));
  }

  @Test
  public void testEncodeDecodeCharset ()
  {
    final String sSource = "dgMP$";
    final String sEncoded = Base64.safeEncode (sSource, StandardCharsets.ISO_8859_1);
    assertArrayEquals (sSource.getBytes (StandardCharsets.ISO_8859_1), Base64.safeDecode (sEncoded));
    assertArrayEquals (sSource.getBytes (StandardCharsets.ISO_8859_1), Base64.safeDecode (sEncoded.getBytes (StandardCharsets.ISO_8859_1)));
    assertEquals (sSource, Base64.safeDecodeAsString (sEncoded, StandardCharsets.ISO_8859_1));
    assertEquals (sSource, Base64.safeDecodeAsString (sEncoded.getBytes (StandardCharsets.ISO_8859_1), StandardCharsets.ISO_8859_1));
  }

  @Test
  public void testSafeDecode ()
  {
    assertEquals (0, Base64.safeDecode (new byte [0]).length);
    assertNull (Base64.safeDecode (new byte [1]));
    assertNull (Base64.safeDecode (new byte [2]));
    assertNull (Base64.safeDecode (new byte [3]));
    assertEquals (3, Base64.safeDecode (new byte [] { 'a', 'a', 'a', 'a' }).length);

    // Invalid input (1-3 chars fail)
    assertNull (Base64.safeDecode ("xyz"));
  }
}

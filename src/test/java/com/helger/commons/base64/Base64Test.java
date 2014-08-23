/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.SecureRandom;

import org.junit.Test;

import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.io.file.FileOperations;
import com.helger.commons.io.file.FileUtils;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.commons.io.streams.NonBlockingByteArrayInputStream;
import com.helger.commons.io.streams.NonBlockingByteArrayOutputStream;

/**
 * Test class for class {@link Base64}.<br>
 * Base64 test code.<br>
 * Partly source:
 * http://iharder.sourceforge.net/current/java/base64/Base64Test.java
 */
public final class Base64Test
{
  private static final class TestSerializable implements Serializable
  {
    private final int m_nValue;
    private final String m_sValue;

    public TestSerializable (final int nValue, final String sValue)
    {
      m_nValue = nValue;
      m_sValue = sValue;
    }

    @Override
    public boolean equals (final Object o)
    {
      if (o == this)
        return true;
      if (!(o instanceof TestSerializable))
        return false;
      final TestSerializable rhs = (TestSerializable) o;
      return m_nValue == rhs.m_nValue && m_sValue.equals (rhs.m_sValue);
    }

    @Override
    public int hashCode ()
    {
      return 31 * m_nValue + m_sValue.hashCode ();
    }
  }

  @Test
  public void testEncodeBytes () throws IOException
  {
    final String sSource = "Hallo Welt! Ümläüte";
    final String sEncoded = Base64.encodeBytes (CharsetManager.getAsBytes (sSource, CCharset.CHARSET_ISO_8859_1_OBJ));
    final byte [] aDecoded = Base64.decode (sEncoded);

    assertArrayEquals (CharsetManager.getAsBytes (sSource, CCharset.CHARSET_ISO_8859_1_OBJ), aDecoded);
    final byte [] aSrc = CharsetManager.getAsBytes ("Hallo Wält", CCharset.CHARSET_UTF_8_OBJ);
    final String sDst = Base64.encodeBytes (aSrc, 0, aSrc.length);
    assertEquals ("Hallo Wält", Base64Helper.safeDecodeAsString (sDst, CCharset.CHARSET_UTF_8_OBJ));
  }

  @Test
  public void testEncodeBytesGZIP () throws IOException
  {
    final String sSource = "Hallo Welt! Ümläüte";
    final String sEncoded = Base64.encodeBytes (CharsetManager.getAsBytes (sSource, CCharset.CHARSET_ISO_8859_1_OBJ),
                                                Base64.GZIP);
    final byte [] aDecoded = Base64.decode (sEncoded);
    assertArrayEquals (CharsetManager.getAsBytes (sSource, CCharset.CHARSET_ISO_8859_1_OBJ), aDecoded);
  }

  @Test
  public void testEncodeObject () throws IOException, ClassNotFoundException
  {
    final TestSerializable aSource = new TestSerializable (4711, "Sträßle");
    final String sEncoded = Base64.encodeObject (aSource);
    TestSerializable aDest = (TestSerializable) Base64.decodeToObject (sEncoded);
    assertEquals (aSource, aDest);
    aDest = (TestSerializable) Base64.decodeToObject (sEncoded, Base64.NO_OPTIONS, Base64Test.class.getClassLoader ());
    assertEquals (aSource, aDest);
  }

  @Test
  public void testEncodeObjectGZIP () throws IOException, ClassNotFoundException
  {
    final TestSerializable aSource = new TestSerializable (4711, "Sträßle");
    final String sEncoded = Base64.encodeObject (aSource, Base64.GZIP);
    TestSerializable aDest = (TestSerializable) Base64.decodeToObject (sEncoded);
    assertEquals (aSource, aDest);
    aDest = (TestSerializable) Base64.decodeToObject (sEncoded, Base64.NO_OPTIONS, Base64Test.class.getClassLoader ());
    assertEquals (aSource, aDest);
  }

  private byte [] _createData (final int length) throws Exception
  {
    final byte [] bytes = new byte [length];
    new SecureRandom ().nextBytes (bytes);
    return bytes;
  }

  private void _runStreamTest (final int length) throws Exception
  {
    final byte [] aData = _createData (length);

    final NonBlockingByteArrayOutputStream aOutBytes = new NonBlockingByteArrayOutputStream ();
    final Base64OutputStream aOS = new Base64OutputStream (aOutBytes);
    aOS.write (aData);
    aOS.suspendEncoding ();
    aOS.resumeEncoding ();
    aOS.close ();
    final byte [] aEncoded = aOutBytes.toByteArray ();
    byte [] aDecoded = Base64.decode (aEncoded);
    assertArrayEquals (aData, aDecoded);

    aOutBytes.reset ();
    final Base64InputStream aIS = new Base64InputStream (new NonBlockingByteArrayInputStream (aEncoded));
    final byte [] aBuffer = new byte [3];
    for (int n = aIS.read (aBuffer); n > 0; n = aIS.read (aBuffer))
      aOutBytes.write (aBuffer, 0, n);
    aOutBytes.close ();
    aIS.close ();
    aDecoded = aOutBytes.toByteArray ();
    assertArrayEquals (aData, aDecoded);
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
      assertFalse (FileUtils.existsFile (f2));
      SimpleFileIO.writeFile (f1, "Hallo Wält", CCharset.CHARSET_UTF_8_OBJ);
      Base64.encodeFileToFile (f1.getAbsolutePath (), f2.getAbsolutePath ());
      assertTrue (FileUtils.existsFile (f2));
      final String sEncoded = SimpleFileIO.readFileAsString (f2, CCharset.CHARSET_UTF_8_OBJ);
      assertEquals ("Hallo Wält", Base64Helper.safeDecodeAsString (sEncoded, CCharset.CHARSET_UTF_8_OBJ));
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
      assertFalse (FileUtils.existsFile (f2));
      final String sDecoded = "Hallo Wält";
      Base64.encodeToFile (CharsetManager.getAsBytes (sDecoded, CCharset.CHARSET_UTF_8_OBJ), f2.getAbsolutePath ());
      assertTrue (FileUtils.existsFile (f2));
      final String sEncoded = SimpleFileIO.readFileAsString (f2, CCharset.CHARSET_UTF_8_OBJ);
      assertEquals ("Hallo Wält", Base64Helper.safeDecodeAsString (sEncoded, CCharset.CHARSET_UTF_8_OBJ));
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
      assertFalse (FileUtils.existsFile (f2));
      SimpleFileIO.writeFile (f1, CharsetManager.getAsBytes (Base64Helper.safeEncode ("Hallo Wält",
                                                                                      CCharset.CHARSET_UTF_8_OBJ),
                                                             CCharset.CHARSET_ISO_8859_1_OBJ));
      Base64.decodeFileToFile (f1.getAbsolutePath (), f2.getAbsolutePath ());
      assertTrue (FileUtils.existsFile (f2));
      final String sDecoded = SimpleFileIO.readFileAsString (f2, CCharset.CHARSET_UTF_8_OBJ);
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
      assertFalse (FileUtils.existsFile (f2));
      final String sEncoded = Base64Helper.safeEncode ("Hallo Wält", CCharset.CHARSET_UTF_8_OBJ);
      Base64.decodeToFile (sEncoded, f2.getAbsolutePath ());
      assertTrue (FileUtils.existsFile (f2));
      final String sDecoded = SimpleFileIO.readFileAsString (f2, CCharset.CHARSET_UTF_8_OBJ);
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
    final ByteBuffer aSrc = ByteBuffer.wrap (CharsetManager.getAsBytes ("Hallo Wält", CCharset.CHARSET_UTF_8_OBJ));
    final ByteBuffer aDst = ByteBuffer.allocate (aSrc.capacity () * 2);
    Base64.encode (aSrc, aDst);
    assertEquals ("Hallo Wält", Base64Helper.safeDecodeAsString (aDst.array (), CCharset.CHARSET_UTF_8_OBJ));
  }

  @Test
  public void testEncodeCharBuffer ()
  {
    final ByteBuffer aSrc = ByteBuffer.wrap (CharsetManager.getAsBytes ("Hallo Wält", CCharset.CHARSET_UTF_8_OBJ));
    final CharBuffer aDst = CharBuffer.allocate (aSrc.capacity () * 2);
    Base64.encode (aSrc, aDst);
    assertEquals ("Hallo Wält",
                  Base64Helper.safeDecodeAsString (new String (aDst.array ()), CCharset.CHARSET_UTF_8_OBJ));
  }

  @Test
  public void testEncodeByteToByte ()
  {
    final byte [] aSrc = CharsetManager.getAsBytes ("Hallo Wält", CCharset.CHARSET_UTF_8_OBJ);
    final byte [] aDst = Base64.encodeBytesToBytes (aSrc);
    assertEquals ("Hallo Wält", Base64Helper.safeDecodeAsString (aDst, CCharset.CHARSET_UTF_8_OBJ));
  }
}

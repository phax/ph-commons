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
package com.helger.commons.io.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.io.resource.ClassPathResource;

/**
 * Test class for class {@link SimpleFileIO}.
 *
 * @author Philip Helger
 */
public final class SimpleFileIOTest
{
  @Test
  public void testReadFileBytes ()
  {
    final String s = "äöü text";
    final File f = new File ("dummy.txt");
    assertTrue (SimpleFileIO.writeFile (f, CharsetManager.getAsBytes (s, CCharset.CHARSET_ISO_8859_1_OBJ))
                            .isSuccess ());
    try
    {
      final byte [] aBytes = SimpleFileIO.getAllFileBytes (f);
      assertNotNull (aBytes);
      assertTrue (Arrays.equals (aBytes, CharsetManager.getAsBytes (s, CCharset.CHARSET_ISO_8859_1_OBJ)));
      assertNull (SimpleFileIO.getAllFileBytes (null));
      assertNull (SimpleFileIO.getAllFileBytes (new File ("non existing file")));
    }
    finally
    {
      FileOperations.deleteFile (f);
    }
  }

  @Test
  public void testReaFileLines ()
  {
    assertNull (SimpleFileIO.getAllFileLines (null, CCharset.CHARSET_ISO_8859_1_OBJ));
    assertNull (SimpleFileIO.getAllFileLines (new File ("ha ha said the clown"), CCharset.CHARSET_ISO_8859_1_OBJ));
    final File aFile = ClassPathResource.getAsFile ("streamutils-lines");
    assertTrue (aFile.exists ());
    final List <String> lines = SimpleFileIO.getAllFileLines (aFile, CCharset.CHARSET_ISO_8859_1_OBJ);
    assertEquals (10, lines.size ());
  }

  @Test
  public void testReadFileAsString ()
  {
    final File aFile = new File ("umlaut-tests.txt");
    final String s = "defäöüabc";
    assertEquals ("Source encoding of the Java file must be UTF-8!", 9, s.length ());
    assertNull (SimpleFileIO.getFileAsString (null, CCharset.CHARSET_ISO_8859_1_OBJ));
    assertTrue (SimpleFileIO.writeFile (aFile, s, CCharset.CHARSET_UTF_8_OBJ).isSuccess ());
    try
    {
      final String t = SimpleFileIO.getFileAsString (aFile, CCharset.CHARSET_UTF_8_OBJ);
      assertEquals (s, t);
    }
    finally
    {
      assertTrue (FileOperations.deleteFile (aFile).isSuccess ());
    }
    assertTrue (SimpleFileIO.writeFile (aFile, s, CCharset.CHARSET_ISO_8859_1_OBJ).isSuccess ());
    try
    {
      final String t = SimpleFileIO.getFileAsString (aFile, CCharset.CHARSET_ISO_8859_1_OBJ);
      assertEquals (s, t);
    }
    finally
    {
      assertTrue (FileOperations.deleteFile (aFile).isSuccess ());
    }
  }

  @Test
  public void testWriteFile ()
  {
    final File aFile = new File ("hahatwf.txt");
    try
    {
      assertTrue (SimpleFileIO.writeFile (aFile, new byte [10]).isSuccess ());
      assertTrue (SimpleFileIO.writeFile (aFile, new byte [10], 0, 5).isSuccess ());
      assertTrue (SimpleFileIO.writeFile (aFile, "abc", CCharset.CHARSET_ISO_8859_1_OBJ).isSuccess ());
    }
    finally
    {
      FileOperations.deleteFile (aFile);
    }

    try
    {
      SimpleFileIO.writeFile (null, new byte [10]);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      SimpleFileIO.writeFile (null, new byte [10], 0, 5);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      SimpleFileIO.writeFile (null, "abc", CCharset.CHARSET_ISO_8859_1_OBJ);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}

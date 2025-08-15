/*
 * Copyright (C) 2015-2025 Philip Helger (www.helger.com)
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
package com.helger.io.file;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.collection.commons.ICommonsList;
import com.helger.io.resource.ClassPathResource;

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
    assertTrue (SimpleFileIO.writeFile (f, s.getBytes (StandardCharsets.ISO_8859_1)).isSuccess ());
    try
    {
      final byte [] aBytes = SimpleFileIO.getAllFileBytes (f);
      assertNotNull (aBytes);
      assertArrayEquals (aBytes, s.getBytes (StandardCharsets.ISO_8859_1));
      assertNull (SimpleFileIO.getAllFileBytes ((File) null));
      assertNull (SimpleFileIO.getAllFileBytes (new File ("non existing file")));
    }
    finally
    {
      FileOperations.deleteFile (f);
    }
  }

  @Test
  public void testReadFileLines ()
  {
    assertNull (SimpleFileIO.getAllFileLines (null, StandardCharsets.ISO_8859_1));
    assertNull (SimpleFileIO.getAllFileLines (new File ("ha ha said the clown"), StandardCharsets.ISO_8859_1));
    final File aFile = ClassPathResource.getAsFile ("streamutils-lines.txt");
    assertTrue (aFile.exists ());
    final ICommonsList <String> lines = SimpleFileIO.getAllFileLines (aFile, StandardCharsets.ISO_8859_1);
    assertEquals (10, lines.size ());
  }

  @Test
  public void testReadFileAsString ()
  {
    final File aFile = new File ("umlaut-tests.txt");
    final String s = "defäöüabc";
    assertEquals ("Source encoding of the Java file must be UTF-8!", 9, s.length ());
    assertNull (SimpleFileIO.getFileAsString (null, StandardCharsets.ISO_8859_1));
    assertTrue (SimpleFileIO.writeFile (aFile, s, StandardCharsets.UTF_8).isSuccess ());
    try
    {
      final String t = SimpleFileIO.getFileAsString (aFile, StandardCharsets.UTF_8);
      assertEquals (s, t);
    }
    finally
    {
      assertTrue (FileOperations.deleteFile (aFile).isSuccess ());
    }
    assertTrue (SimpleFileIO.writeFile (aFile, s, StandardCharsets.ISO_8859_1).isSuccess ());
    try
    {
      final String t = SimpleFileIO.getFileAsString (aFile, StandardCharsets.ISO_8859_1);
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
      assertTrue (SimpleFileIO.writeFile (aFile, "abc", StandardCharsets.ISO_8859_1).isSuccess ());
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
      SimpleFileIO.writeFile (null, "abc", StandardCharsets.ISO_8859_1);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}

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
package com.helger.io.relative;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.helger.base.io.EAppend;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.string.StringReplace;
import com.helger.io.file.FileOperations;
import com.helger.io.file.SimpleFileIO;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link FileRelativeIO} and the default methods of {@link IFileRelativeIO}.
 *
 * @author Philip Helger
 */
public final class FileRelativeIOTest
{
  private static final File BASE_PATH = new File ("target/junittest-relativeio").getAbsoluteFile ();

  private FileRelativeIO m_aIO;

  @Before
  public void createBasePath ()
  {
    m_aIO = new FileRelativeIO (BASE_PATH);
  }

  @After
  public void deleteBasePath ()
  {
    FileOperations.deleteDirRecursiveIfExisting (BASE_PATH);
  }

  @Test
  public void testBasePath ()
  {
    assertEquals (BASE_PATH, m_aIO.getBasePathFile ());
    assertEquals (BASE_PATH.getAbsolutePath (), m_aIO.getBasePath ());
    assertTrue (BASE_PATH.isDirectory ());
    assertNotNull (m_aIO.toString ());

    TestHelper.testDefaultImplementationWithEqualContentObject (m_aIO, new FileRelativeIO (BASE_PATH));
    TestHelper.testDefaultImplementationWithDifferentContentObject (m_aIO,
                                                                    new FileRelativeIO (new File ("target/junittest-relativeio2").getAbsoluteFile ()));
    FileOperations.deleteDirRecursiveIfExisting (new File ("target/junittest-relativeio2").getAbsoluteFile ());
  }

  @Test
  public void testGetFileAndResource ()
  {
    final File aFile = m_aIO.getFile ("sub/file.txt");
    assertEquals (new File (BASE_PATH, "sub/file.txt"), aFile);
    assertNotNull (m_aIO.getResource ("sub/file.txt"));
    assertEquals (StringReplace.replaceAll ("sub/file.txt", '/', File.separatorChar),
                  StringReplace.replaceAll (m_aIO.getRelativeFilename (aFile), '/', File.separatorChar));
  }

  @Test
  public void testExists ()
  {
    assertFalse (m_aIO.existsFile ("file.txt"));
    assertFalse (m_aIO.existsDir ("sub"));

    assertTrue (m_aIO.saveFile ("file.txt", "content", StandardCharsets.ISO_8859_1).isSuccess ());
    assertTrue (m_aIO.existsFile ("file.txt"));
    assertFalse (m_aIO.existsDir ("file.txt"));

    assertTrue (m_aIO.createDirectory ("sub", false).isSuccess ());
    assertTrue (m_aIO.existsDir ("sub"));
    assertFalse (m_aIO.existsFile ("sub"));
  }

  @Test
  public void testStreamsAndWriters ()
  {
    try (final OutputStream aOS = m_aIO.getOutputStream ("os.txt"))
    {
      assertNotNull (aOS);
    }
    catch (final java.io.IOException ex)
    {
      fail (ex.getMessage ());
    }
    assertNotNull (m_aIO.getOutputStream ("os.txt", EAppend.APPEND));

    final Writer aW = m_aIO.getWriter ("w.txt", StandardCharsets.ISO_8859_1);
    assertNotNull (aW);
    StreamHelper.close (aW);

    final Writer aW2 = m_aIO.getWriter ("w.txt", StandardCharsets.ISO_8859_1, EAppend.APPEND);
    assertNotNull (aW2);
    StreamHelper.close (aW2);
  }

  @Test
  public void testWriteSaveAppend ()
  {
    assertTrue (m_aIO.writeFile ("f1.txt", EAppend.TRUNCATE, "abc".getBytes (StandardCharsets.ISO_8859_1))
                     .isSuccess ());
    assertEquals ("abc", SimpleFileIO.getFileAsString (m_aIO.getFile ("f1.txt"), StandardCharsets.ISO_8859_1));

    assertTrue (m_aIO.saveFile ("f2.txt", "abc", StandardCharsets.ISO_8859_1).isSuccess ());
    assertTrue (m_aIO.saveFile ("f3.txt", "abc".getBytes (StandardCharsets.ISO_8859_1)).isSuccess ());

    assertTrue (m_aIO.appendFile ("f2.txt", "def", StandardCharsets.ISO_8859_1).isSuccess ());
    assertEquals ("abcdef", SimpleFileIO.getFileAsString (m_aIO.getFile ("f2.txt"), StandardCharsets.ISO_8859_1));

    assertTrue (m_aIO.appendFile ("f3.txt", "def".getBytes (StandardCharsets.ISO_8859_1)).isSuccess ());
    assertEquals ("abcdef", SimpleFileIO.getFileAsString (m_aIO.getFile ("f3.txt"), StandardCharsets.ISO_8859_1));
  }

  @Test
  public void testDirectoryAndFileOperations ()
  {
    assertTrue (m_aIO.createDirectory ("d1/d2", true).isSuccess ());
    assertTrue (m_aIO.existsDir ("d1/d2"));

    assertTrue (m_aIO.renameDir ("d1/d2", "d1/d3").isSuccess ());
    assertTrue (m_aIO.existsDir ("d1/d3"));

    assertTrue (m_aIO.deleteDirectory ("d1/d3", false).isSuccess ());
    assertFalse (m_aIO.existsDir ("d1/d3"));
    assertTrue (m_aIO.deleteDirectoryIfExisting ("d1/d3", false).isSuccess ());
    assertTrue (m_aIO.deleteDirectory ("d1", true).isSuccess ());

    assertTrue (m_aIO.saveFile ("old.txt", "abc", StandardCharsets.ISO_8859_1).isSuccess ());
    assertTrue (m_aIO.renameFile ("old.txt", "new.txt").isSuccess ());
    assertTrue (m_aIO.existsFile ("new.txt"));

    assertTrue (m_aIO.deleteFile ("new.txt").isSuccess ());
    assertFalse (m_aIO.existsFile ("new.txt"));
    assertTrue (m_aIO.deleteFileIfExisting ("new.txt").isSuccess ());
  }

  @Test
  public void testCreateForCurrentDir ()
  {
    final FileRelativeIO aIO = FileRelativeIO.createForCurrentDir ();
    assertNotNull (aIO);
    assertTrue (aIO.getBasePathFile ().isAbsolute ());
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      new FileRelativeIO (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      // Must be absolute
      new FileRelativeIO (new File ("target/relative"));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testInternalCheckAccessRights ()
  {
    assertTrue (m_aIO.saveFile ("f1.txt", "abc", StandardCharsets.ISO_8859_1).isSuccess ());
    assertTrue (m_aIO.createDirectory ("sub", false).isSuccess ());
    // Must not throw
    FileRelativeIO.internalCheckAccessRights (BASE_PATH);
  }
}

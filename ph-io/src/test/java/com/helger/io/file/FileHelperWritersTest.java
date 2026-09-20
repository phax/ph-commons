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
package com.helger.io.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.helger.base.io.EAppend;
import com.helger.base.io.stream.StreamHelper;

/**
 * Additional test class for the writer factories and the display helpers of {@link FileHelper}.
 *
 * @author Philip Helger
 */
public final class FileHelperWritersTest
{
  private static final File BASE = new File ("target/junittest-filehelper-writers");
  private static final File FILE = new File (BASE, "file.txt");

  @Before
  public void createBase ()
  {
    FileOperations.createDirRecursiveIfNotExisting (BASE);
    SimpleFileIO.writeFile (FILE, "content", StandardCharsets.ISO_8859_1);
  }

  @After
  public void deleteBase ()
  {
    FileHelper.setWarnOnIssues (true);
    FileOperations.deleteDirRecursiveIfExisting (BASE);
  }

  @Test
  public void testWarnOnIssues ()
  {
    assertTrue (FileHelper.isWarnOnIssues ());
    FileHelper.setWarnOnIssues (false);
    assertFalse (FileHelper.isWarnOnIssues ());
    FileHelper.setWarnOnIssues (true);
    assertTrue (FileHelper.isWarnOnIssues ());
  }

  @Test
  public void testWriters ()
  {
    final File aOut = new File (BASE, "out.txt");

    final Writer aW = FileHelper.getWriter (aOut, StandardCharsets.ISO_8859_1);
    assertNotNull (aW);
    StreamHelper.close (aW);

    final Writer aW2 = FileHelper.getWriter (aOut, EAppend.APPEND, StandardCharsets.ISO_8859_1);
    assertNotNull (aW2);
    StreamHelper.close (aW2);

    final Writer aW3 = FileHelper.getBufferedWriter (aOut, StandardCharsets.ISO_8859_1);
    assertNotNull (aW3);
    StreamHelper.close (aW3);

    final Writer aW4 = FileHelper.getBufferedWriter (aOut, EAppend.APPEND, StandardCharsets.ISO_8859_1);
    assertNotNull (aW4);
    StreamHelper.close (aW4);

    final PrintWriter aPW = FileHelper.getPrintWriter (aOut, StandardCharsets.ISO_8859_1);
    assertNotNull (aPW);
    StreamHelper.close (aPW);

    final PrintWriter aPW2 = FileHelper.getPrintWriter (aOut, EAppend.APPEND, StandardCharsets.ISO_8859_1);
    assertNotNull (aPW2);
    StreamHelper.close (aPW2);
  }

  @Test
  public void testRandomAccessFile ()
  {
    final RandomAccessFile aRAF = FileHelper.getRandomAccessFile (FILE, ERandomAccessFileMode.READ_WRITE);
    assertNotNull (aRAF);
    StreamHelper.close (aRAF);

    final RandomAccessFile aRAF2 = FileHelper.getRandomAccessFile (FILE.getPath (), ERandomAccessFileMode.READ_ONLY);
    assertNotNull (aRAF2);
    StreamHelper.close (aRAF2);

    // A directory cannot be opened
    assertNull (FileHelper.getRandomAccessFile (BASE, ERandomAccessFileMode.READ_WRITE));
  }

  @Test
  public void testGetFileSizeDisplay ()
  {
    assertEquals ("", FileHelper.getFileSizeDisplay ((File) null));
    assertEquals ("", FileHelper.getFileSizeDisplay (new File (BASE, "does-not-exist.txt")));
    assertNotNull (FileHelper.getFileSizeDisplay (FILE));
    assertNotNull (FileHelper.getFileSizeDisplay (FILE, 2));

    assertEquals ("0B", FileHelper.getFileSizeDisplay (0));
    assertEquals ("1KB", FileHelper.getFileSizeDisplay (1024));
    assertEquals ("1MB", FileHelper.getFileSizeDisplay (1024 * 1024));
    assertEquals ("1GB", FileHelper.getFileSizeDisplay (1024L * 1024 * 1024));
    assertNotNull (FileHelper.getFileSizeDisplay (1536L, 1));
  }

  @Test
  public void testGetSecureFile ()
  {
    assertNull (FileHelper.getSecureFile (null));
    assertNotNull (FileHelper.getSecureFile (FILE));
  }

  @Test
  public void testGetDirectoryObjectCount ()
  {
    assertEquals (1, FileHelper.getDirectoryObjectCount (BASE));
  }
}

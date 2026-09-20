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
package com.helger.io.id.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Test;

import com.helger.io.file.FileOperations;
import com.helger.io.file.SimpleFileIO;

/**
 * Test class for the recovery of the temporary files of {@link FileIntIDFactory} and
 * {@link FileLongIDFactory}.
 *
 * @author Philip Helger
 */
public final class FileIDFactoryRecoveryTest
{
  private static final File FILE = new File ("target/junittest-idfactory.ids");
  private static final File PREV_FILE = new File ("target/junittest-idfactory.ids.prev");
  private static final File NEW_FILE = new File ("target/junittest-idfactory.ids.new");

  @After
  public void deleteFiles ()
  {
    FileOperations.deleteFileIfExisting (FILE);
    FileOperations.deleteFileIfExisting (PREV_FILE);
    FileOperations.deleteFileIfExisting (NEW_FILE);
  }

  private static void _write (final File aFile, final String sValue)
  {
    assertTrue (SimpleFileIO.writeFile (aFile, sValue, FileIntIDFactory.CHARSET_TO_USE).isSuccess ());
  }

  @Test
  public void testIntNewFileIsSmaller ()
  {
    _write (FILE, "500");
    // The ".new" file contains a smaller value and is therefore deleted
    _write (NEW_FILE, "100");

    final FileIntIDFactory aFactory = new FileIntIDFactory (FILE);
    assertFalse (NEW_FILE.exists ());
    assertSame (FILE, aFactory.getFile ());
    assertEquals (500, aFactory.getNewID ());
  }

  @Test
  public void testIntNewFileIsBigger ()
  {
    _write (FILE, "100");
    // The ".new" file contains a bigger value and becomes authoritative
    _write (NEW_FILE, "500");

    final FileIntIDFactory aFactory = new FileIntIDFactory (FILE);
    assertFalse (NEW_FILE.exists ());
    assertTrue (FILE.exists ());
    assertEquals (500, aFactory.getNewID ());
  }

  @Test
  public void testIntPrevFileIsSmaller ()
  {
    _write (FILE, "500");
    _write (PREV_FILE, "100");

    final FileIntIDFactory aFactory = new FileIntIDFactory (FILE);
    assertFalse (PREV_FILE.exists ());
    assertEquals (500, aFactory.getNewID ());
  }

  @Test
  public void testIntPrevFileIsBigger ()
  {
    _write (FILE, "100");
    _write (PREV_FILE, "500");

    final FileIntIDFactory aFactory = new FileIntIDFactory (FILE);
    assertFalse (PREV_FILE.exists ());
    assertTrue (FILE.exists ());
    assertEquals (500, aFactory.getNewID ());
  }

  @Test
  public void testIntInvalidFileContent ()
  {
    // Not a number at all - is treated as 0
    _write (FILE, "this is not a number");

    final FileIntIDFactory aFactory = new FileIntIDFactory (FILE);
    assertEquals (0, aFactory.getNewID ());
    assertNotNull (aFactory.toString ());
  }

  @Test
  public void testLongNewFileIsSmaller ()
  {
    _write (FILE, "500");
    _write (NEW_FILE, "100");

    final FileLongIDFactory aFactory = new FileLongIDFactory (FILE);
    assertFalse (NEW_FILE.exists ());
    assertSame (FILE, aFactory.getFile ());
    assertEquals (500L, aFactory.getNewID ());
  }

  @Test
  public void testLongNewFileIsBigger ()
  {
    _write (FILE, "100");
    _write (NEW_FILE, "500");

    final FileLongIDFactory aFactory = new FileLongIDFactory (FILE);
    assertFalse (NEW_FILE.exists ());
    assertTrue (FILE.exists ());
    assertEquals (500L, aFactory.getNewID ());
  }

  @Test
  public void testLongPrevFileIsSmaller ()
  {
    _write (FILE, "500");
    _write (PREV_FILE, "100");

    final FileLongIDFactory aFactory = new FileLongIDFactory (FILE);
    assertFalse (PREV_FILE.exists ());
    assertEquals (500L, aFactory.getNewID ());
  }

  @Test
  public void testLongPrevFileIsBigger ()
  {
    _write (FILE, "100");
    _write (PREV_FILE, "500");

    final FileLongIDFactory aFactory = new FileLongIDFactory (FILE);
    assertFalse (PREV_FILE.exists ());
    assertTrue (FILE.exists ());
    assertEquals (500L, aFactory.getNewID ());
  }

  @Test
  public void testLongInvalidFileContent ()
  {
    _write (FILE, "this is not a number");

    final FileLongIDFactory aFactory = new FileLongIDFactory (FILE);
    assertEquals (0L, aFactory.getNewID ());
    assertNotNull (aFactory.toString ());
  }
}

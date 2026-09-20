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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Additional test class for the error paths of {@link FileOperations}.
 *
 * @author Philip Helger
 */
public final class FileOperationsErrorsTest
{
  private static final File BASE = new File ("target/junittest-fileoperations");
  private static final File FILE = new File (BASE, "file.txt");
  private static final File DIR = new File (BASE, "dir");
  private static final File MISSING = new File (BASE, "does-not-exist");

  @Before
  public void createBase ()
  {
    FileOperations.createDirRecursiveIfNotExisting (BASE);
    SimpleFileIO.writeFile (FILE, "content", StandardCharsets.ISO_8859_1);
    FileOperations.createDirIfNotExisting (DIR);
  }

  @After
  public void deleteBase ()
  {
    FileOperations.deleteDirRecursiveIfExisting (BASE);
  }

  @Test
  public void testCreateDirErrors ()
  {
    // The target already exists
    assertSame (EFileIOErrorCode.TARGET_ALREADY_EXISTS, FileOperations.createDir (DIR).getErrorCode ());
    assertTrue (FileOperations.createDirIfNotExisting (DIR).isSuccess ());

    assertSame (EFileIOErrorCode.TARGET_ALREADY_EXISTS, FileOperations.createDirRecursive (DIR).getErrorCode ());
    assertTrue (FileOperations.createDirRecursiveIfNotExisting (DIR).isSuccess ());
  }

  @Test
  public void testDeleteDirErrors ()
  {
    // Not existing
    assertSame (EFileIOErrorCode.SOURCE_DOES_NOT_EXIST, FileOperations.deleteDir (MISSING).getErrorCode ());
    assertTrue (FileOperations.deleteDirIfExisting (MISSING).isSuccess ());

    assertSame (EFileIOErrorCode.SOURCE_DOES_NOT_EXIST, FileOperations.deleteDirRecursive (MISSING).getErrorCode ());
    assertTrue (FileOperations.deleteDirRecursiveIfExisting (MISSING).isSuccess ());

    // A file is not a directory
    assertSame (EFileIOErrorCode.SOURCE_DOES_NOT_EXIST, FileOperations.deleteDir (FILE).getErrorCode ());
  }

  @Test
  public void testDeleteFileErrors ()
  {
    assertSame (EFileIOErrorCode.SOURCE_DOES_NOT_EXIST, FileOperations.deleteFile (MISSING).getErrorCode ());
    assertTrue (FileOperations.deleteFileIfExisting (MISSING).isSuccess ());

    // A directory is not a file
    assertSame (EFileIOErrorCode.SOURCE_DOES_NOT_EXIST, FileOperations.deleteFile (DIR).getErrorCode ());

    assertTrue (FileOperations.deleteFile (FILE).isSuccess ());
    assertFalse (FILE.exists ());
  }

  @Test
  public void testRenameErrors ()
  {
    // Source and target are the same
    assertSame (EFileIOErrorCode.SOURCE_EQUALS_TARGET, FileOperations.renameFile (FILE, FILE).getErrorCode ());
    assertSame (EFileIOErrorCode.SOURCE_EQUALS_TARGET, FileOperations.renameDir (DIR, DIR).getErrorCode ());

    // Source does not exist
    assertSame (EFileIOErrorCode.SOURCE_DOES_NOT_EXIST,
                FileOperations.renameFile (MISSING, new File (BASE, "new.txt")).getErrorCode ());

    // Target already exists
    final File aOther = new File (BASE, "other.txt");
    SimpleFileIO.writeFile (aOther, "content", StandardCharsets.ISO_8859_1);
    assertSame (EFileIOErrorCode.TARGET_ALREADY_EXISTS, FileOperations.renameFile (FILE, aOther).getErrorCode ());
  }

  @Test
  public void testCopyErrors ()
  {
    assertSame (EFileIOErrorCode.SOURCE_EQUALS_TARGET, FileOperations.copyFile (FILE, FILE).getErrorCode ());
    assertSame (EFileIOErrorCode.SOURCE_EQUALS_TARGET, FileOperations.copyDirRecursive (DIR, DIR).getErrorCode ());

    assertSame (EFileIOErrorCode.SOURCE_DOES_NOT_EXIST,
                FileOperations.copyFile (MISSING, new File (BASE, "copy.txt")).getErrorCode ());

    // A successful copy
    final File aCopy = new File (BASE, "copy.txt");
    assertTrue (FileOperations.copyFile (FILE, aCopy).isSuccess ());
    assertTrue (aCopy.exists ());

    // Target already exists
    assertSame (EFileIOErrorCode.TARGET_ALREADY_EXISTS, FileOperations.copyFile (FILE, aCopy).getErrorCode ());
  }
}

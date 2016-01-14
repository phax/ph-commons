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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;

/**
 * Test class for class {@link FileOperations}.
 *
 * @author Philip Helger
 */
public final class FileOperationsTest
{
  private static void _expectedSuccess (@Nonnull final FileIOError ec)
  {
    assertEquals ("Expected no error but got " + ec.getErrorCode (), EFileIOErrorCode.NO_ERROR, ec.getErrorCode ());
  }

  private static void _expectedError (@Nonnull final FileIOError ec, @Nonnull final EFileIOErrorCode eCode)
  {
    assertEquals ("Expected error " + eCode + " but got " + ec.getErrorCode (), eCode, ec.getErrorCode ());
  }

  @Test
  public void testCreateDir ()
  {
    final File aDir1 = new File ("TestDir");
    assertFalse (FileHelper.existsDir (aDir1));
    _expectedSuccess (FileOperations.createDir (aDir1));
    try
    {
      assertTrue (FileHelper.existsDir (aDir1));

      // directory already exists
      _expectedError (FileOperations.createDir (aDir1), EFileIOErrorCode.TARGET_ALREADY_EXISTS);
    }
    finally
    {
      _expectedSuccess (FileOperations.deleteDir (aDir1));
      assertFalse (FileHelper.existsDir (aDir1));
    }

    // Invalid directory name
    _expectedError (FileOperations.createDir (new File ("\0")), EFileIOErrorCode.OPERATION_FAILED);

    try
    {
      // null not allowed
      FileOperations.createDir (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testCreateDirIfNotExisting ()
  {
    final File aDir1 = new File ("TestDir");
    assertFalse (FileHelper.existsDir (aDir1));
    _expectedSuccess (FileOperations.createDirIfNotExisting (aDir1));
    try
    {
      assertTrue (FileHelper.existsDir (aDir1));

      // directory already exists
      _expectedSuccess (FileOperations.createDirIfNotExisting (aDir1));
    }
    finally
    {
      _expectedSuccess (FileOperations.deleteDir (aDir1));
      assertFalse (FileHelper.existsDir (aDir1));
    }

    // Invalid directory name
    _expectedError (FileOperations.createDirIfNotExisting (new File ("\0")), EFileIOErrorCode.OPERATION_FAILED);

    try
    {
      // null not allowed
      FileOperations.createDirIfNotExisting (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testCreateDirRecursive ()
  {
    final File aDir1 = new File ("TestDir");
    final File aDir11 = new File (aDir1, "TestSubDir");
    assertFalse (FileHelper.existsDir (aDir1));
    assertFalse (FileHelper.existsDir (aDir11));
    _expectedSuccess (FileOperations.createDirRecursive (aDir11));
    try
    {
      assertTrue (FileHelper.existsDir (aDir1));
      assertTrue (FileHelper.existsDir (aDir11));

      // directory already exists
      _expectedError (FileOperations.createDirRecursive (aDir1), EFileIOErrorCode.TARGET_ALREADY_EXISTS);
      _expectedError (FileOperations.createDirRecursive (aDir11), EFileIOErrorCode.TARGET_ALREADY_EXISTS);
    }
    finally
    {
      _expectedSuccess (FileOperations.deleteDirRecursive (aDir1));
      assertFalse (FileHelper.existsDir (aDir11));
      assertFalse (FileHelper.existsDir (aDir1));
      _expectedError (FileOperations.deleteDirRecursive (aDir1), EFileIOErrorCode.SOURCE_DOES_NOT_EXIST);
    }

    // Invalid directory name
    _expectedError (FileOperations.createDirRecursive (new File ("\0")), EFileIOErrorCode.OPERATION_FAILED);

    try
    {
      // null not allowed
      FileOperations.createDirRecursive (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testCreateDirRecursiveIfNotExisting ()
  {
    final File aDir1 = new File ("TestDir");
    final File aDir11 = new File (aDir1, "TestSubDir");
    assertFalse (FileHelper.existsDir (aDir1));
    assertFalse (FileHelper.existsDir (aDir11));
    _expectedSuccess (FileOperations.createDirRecursiveIfNotExisting (aDir11));
    try
    {
      assertTrue (FileHelper.existsDir (aDir1));
      assertTrue (FileHelper.existsDir (aDir11));

      // directory already exists
      _expectedSuccess (FileOperations.createDirRecursiveIfNotExisting (aDir1));
      _expectedSuccess (FileOperations.createDirRecursiveIfNotExisting (aDir11));
    }
    finally
    {
      _expectedSuccess (FileOperations.deleteDirRecursive (aDir1));
      assertFalse (FileHelper.existsDir (aDir11));
      assertFalse (FileHelper.existsDir (aDir1));
      _expectedError (FileOperations.deleteDirRecursive (aDir1), EFileIOErrorCode.SOURCE_DOES_NOT_EXIST);
    }

    // Invalid directory name
    _expectedError (FileOperations.createDirRecursiveIfNotExisting (new File ("\0")),
                    EFileIOErrorCode.OPERATION_FAILED);

    try
    {
      // null not allowed
      FileOperations.createDirRecursiveIfNotExisting (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testRenameDir ()
  {
    final File aSrcDir = new File ("SourceDir");
    final File aDstDir = new File ("DestDir");
    assertTrue (!aSrcDir.equals (aDstDir));
    assertFalse (FileHelper.existsDir (aSrcDir));
    assertFalse (FileHelper.existsDir (aDstDir));

    // create source directory
    _expectedSuccess (FileOperations.createDir (aSrcDir));
    try
    {
      assertTrue (FileHelper.existsDir (aSrcDir));
      assertFalse (FileHelper.existsDir (aDstDir));

      // rename
      _expectedSuccess (FileOperations.renameDir (aSrcDir, aDstDir));
      assertFalse (FileHelper.existsDir (aSrcDir));
      assertTrue (FileHelper.existsDir (aDstDir));

      // rename again
      _expectedError (FileOperations.renameDir (aSrcDir, aDstDir), EFileIOErrorCode.SOURCE_DOES_NOT_EXIST);
      assertFalse (FileHelper.existsDir (aSrcDir));
      assertTrue (FileHelper.existsDir (aDstDir));

      // rename same
      _expectedError (FileOperations.renameDir (aDstDir, aDstDir), EFileIOErrorCode.SOURCE_EQUALS_TARGET);
      assertFalse (FileHelper.existsDir (aSrcDir));
      assertTrue (FileHelper.existsDir (aDstDir));

      // rename back
      _expectedSuccess (FileOperations.renameDir (aDstDir, aSrcDir));
      assertTrue (FileHelper.existsDir (aSrcDir));
      assertFalse (FileHelper.existsDir (aDstDir));

      // create destination directory
      _expectedSuccess (FileOperations.createDir (aDstDir));
      try
      {
        // cannot rename to existing directory
        _expectedError (FileOperations.renameDir (aSrcDir, aDstDir), EFileIOErrorCode.TARGET_ALREADY_EXISTS);
      }
      finally
      {
        _expectedSuccess (FileOperations.deleteDir (aDstDir));
      }

      // create child of src folder
      final File aSrcDir11 = new File (aSrcDir, "Another");
      // cannot rename to child directory
      _expectedError (FileOperations.renameDir (aSrcDir, aSrcDir11), EFileIOErrorCode.TARGET_IS_CHILD_OF_SOURCE);
    }
    finally
    {
      // Don't know where we are in case of an error -> no expected result
      FileOperations.deleteDir (aSrcDir);
      FileOperations.deleteDir (aDstDir);
      assertFalse (FileHelper.existsDir (aSrcDir));
      assertFalse (FileHelper.existsDir (aDstDir));
    }

    try
    {
      // null not allowed
      FileOperations.renameDir (null, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null not allowed
      FileOperations.renameDir (null, aDstDir);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null not allowed
      FileOperations.renameDir (aSrcDir, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testRenameFile ()
  {
    final File aFile = new File ("dummy.txt");
    final File aFile2 = new File ("dummy2.txt");
    assertFalse (FileHelper.existsFile (aFile));
    try
    {
      SimpleFileIO.writeFile (aFile, CharsetManager.getAsBytes ("abc", CCharset.CHARSET_ISO_8859_1_OBJ));
      assertTrue (FileHelper.existsFile (aFile));

      SimpleFileIO.writeFile (aFile2, CharsetManager.getAsBytes ("abc", CCharset.CHARSET_ISO_8859_1_OBJ));
      _expectedError (FileOperations.renameFile (aFile, aFile2), EFileIOErrorCode.TARGET_ALREADY_EXISTS);
      _expectedSuccess (FileOperations.deleteFile (aFile2));

      _expectedSuccess (FileOperations.renameFile (aFile, aFile2));
      _expectedError (FileOperations.renameFile (aFile, aFile2), EFileIOErrorCode.SOURCE_DOES_NOT_EXIST);
      _expectedSuccess (FileOperations.renameFile (aFile2, aFile));
      _expectedError (FileOperations.renameFile (aFile, aFile), EFileIOErrorCode.SOURCE_EQUALS_TARGET);
      _expectedError (FileOperations.renameFile (aFile, new File ("target/../" + aFile.getName ())),
                      EFileIOErrorCode.SOURCE_EQUALS_TARGET);
      _expectedError (FileOperations.renameFile (aFile, new File ("dirnonexisting/../" + aFile.getName ())),
                      EFileIOErrorCode.SOURCE_EQUALS_TARGET);
    }
    finally
    {
      _expectedSuccess (FileOperations.deleteFile (aFile));
    }

    try
    {
      FileOperations.renameFile (aFile, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      FileOperations.renameFile (null, aFile);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testCopyFile ()
  {
    final File aFile = new File ("dummy.txt");
    final File aFile2 = new File ("dummy2.txt");
    try
    {
      assertFalse (FileHelper.existsFile (aFile));
      assertFalse (FileHelper.existsFile (aFile2));

      _expectedError (FileOperations.copyFile (aFile, aFile2), EFileIOErrorCode.SOURCE_DOES_NOT_EXIST);

      SimpleFileIO.writeFile (aFile, CharsetManager.getAsBytes ("abc", CCharset.CHARSET_ISO_8859_1_OBJ));
      assertTrue (FileHelper.existsFile (aFile));

      SimpleFileIO.writeFile (aFile2, CharsetManager.getAsBytes ("abc", CCharset.CHARSET_ISO_8859_1_OBJ));
      _expectedError (FileOperations.copyFile (aFile, aFile2), EFileIOErrorCode.TARGET_ALREADY_EXISTS);
      _expectedSuccess (FileOperations.deleteFile (aFile2));

      _expectedSuccess (FileOperations.copyFile (aFile, aFile2));
      assertTrue (FileHelper.existsFile (aFile));
      assertTrue (FileHelper.existsFile (aFile2));
      assertTrue (Arrays.equals (SimpleFileIO.getAllFileBytes (aFile), SimpleFileIO.getAllFileBytes (aFile2)));
      _expectedError (FileOperations.copyFile (aFile, aFile), EFileIOErrorCode.SOURCE_EQUALS_TARGET);
      _expectedError (FileOperations.copyFile (aFile, new File ("target/../" + aFile.getName ())),
                      EFileIOErrorCode.SOURCE_EQUALS_TARGET);
      _expectedError (FileOperations.copyFile (aFile, new File ("dirnonexisting/../" + aFile.getName ())),
                      EFileIOErrorCode.SOURCE_EQUALS_TARGET);
    }
    finally
    {
      FileOperations.deleteFile (aFile);
      FileOperations.deleteFile (aFile2);
    }

    try
    {
      FileOperations.copyFile (aFile, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      FileOperations.copyFile (null, aFile);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testCopyDir ()
  {
    final File fDir = new File ("copydirtest");
    final File fDir2 = new File ("copieddirtest");
    try
    {
      _expectedError (FileOperations.copyDirRecursive (fDir, fDir), EFileIOErrorCode.SOURCE_DOES_NOT_EXIST);
      _expectedSuccess (FileOperations.createDir (fDir));
      _expectedError (FileOperations.copyDirRecursive (fDir, fDir), EFileIOErrorCode.SOURCE_EQUALS_TARGET);
      _expectedError (FileOperations.copyDirRecursive (fDir, new File ("xx/..", fDir.getName ())),
                      EFileIOErrorCode.SOURCE_EQUALS_TARGET);
      _expectedError (FileOperations.copyDirRecursive (fDir, new File (".")), EFileIOErrorCode.TARGET_ALREADY_EXISTS);
      _expectedError (FileOperations.copyDirRecursive (fDir, new File (fDir, "bla")),
                      EFileIOErrorCode.TARGET_IS_CHILD_OF_SOURCE);
      _expectedError (FileOperations.copyDirRecursive (fDir, new File ("target")),
                      EFileIOErrorCode.TARGET_ALREADY_EXISTS);

      for (int i = 1; i <= 10; ++i)
        SimpleFileIO.writeFile (new File (fDir, "file" + i), "Hallo", CCharset.CHARSET_ISO_8859_1_OBJ);
      _expectedSuccess (FileOperations.createDir (new File (fDir, "subdir")));
      for (int i = 1; i <= 10; ++i)
        SimpleFileIO.writeFile (new File (fDir, "subdir/file" + i), "Hallo", CCharset.CHARSET_ISO_8859_1_OBJ);
      assertEquals (11, FileHelper.getDirectoryObjectCount (fDir));

      assertFalse (FileHelper.existsDir (fDir2));
      _expectedSuccess (FileOperations.copyDirRecursive (fDir, fDir2));
      assertTrue (FileHelper.existsDir (fDir2));
      assertEquals (11, FileHelper.getDirectoryObjectCount (fDir2));
    }
    finally
    {
      FileOperations.deleteDirRecursive (fDir);
      FileOperations.deleteDirRecursive (fDir2);
    }

    try
    {
      FileOperations.copyDirRecursive (fDir, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      FileOperations.copyDirRecursive (null, fDir);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testDeleteDir ()
  {
    final File aDir = new File ("deldir.test");
    try
    {
      _expectedError (FileOperations.deleteDir (aDir), EFileIOErrorCode.SOURCE_DOES_NOT_EXIST);
      assertFalse (FileHelper.existsDir (aDir));
      _expectedSuccess (FileOperations.createDir (aDir));
      assertEquals (0, FileHelper.getDirectoryObjectCount (aDir));
      _expectedSuccess (FileOperations.deleteDir (aDir));
      assertFalse (FileHelper.existsDir (aDir));
    }
    finally
    {
      FileOperations.deleteDirRecursive (aDir);
    }

    try
    {
      FileOperations.deleteDir (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testDeleteDirRecursive ()
  {
    final File aDir = new File ("deldirrec.test");
    try
    {
      assertFalse (FileHelper.existsDir (aDir));
      _expectedError (FileOperations.deleteDirRecursive (aDir), EFileIOErrorCode.SOURCE_DOES_NOT_EXIST);
      _expectedSuccess (FileOperations.createDir (aDir));
      for (int i = 0; i < 10; ++i)
      {
        SimpleFileIO.writeFile (new File (aDir, "test" + i),
                                "Inhalt von file test" + i,
                                CCharset.CHARSET_ISO_8859_1_OBJ);
        _expectedSuccess (FileOperations.createDir (new File (aDir, "subdir" + i)));
        SimpleFileIO.writeFile (new File (aDir, "subdir" + i + "/test" + i),
                                "Inhalt von file subdir/test" + i,
                                CCharset.CHARSET_ISO_8859_1_OBJ);
      }
      assertEquals (20, FileHelper.getDirectoryObjectCount (aDir));
      // Cannot use regular delete, because the directory is not empty!
      _expectedError (FileOperations.deleteDir (aDir), EFileIOErrorCode.OPERATION_FAILED);
      assertTrue (FileHelper.existsDir (aDir));
      _expectedSuccess (FileOperations.deleteDirRecursive (aDir));
      assertFalse (FileHelper.existsDir (aDir));
    }
    finally
    {
      FileOperations.deleteDirRecursive (aDir);
    }

    try
    {
      FileOperations.deleteDirRecursive (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testDeleteFile ()
  {
    _expectedError (FileOperations.deleteFile (new File ("warum sollte es diese Datei geben")),
                    EFileIOErrorCode.SOURCE_DOES_NOT_EXIST);
    final File f = new File ("delfile.test");
    try
    {
      SimpleFileIO.writeFile (f, CharsetManager.getAsBytes ("abc", CCharset.CHARSET_ISO_8859_1_OBJ));
      assertTrue (FileHelper.existsFile (f));
      _expectedSuccess (FileOperations.deleteFile (f));
      assertFalse (FileHelper.existsFile (f));
      _expectedError (FileOperations.deleteFile (f), EFileIOErrorCode.SOURCE_DOES_NOT_EXIST);
    }
    finally
    {
      FileOperations.deleteFile (f);
    }

    try
    {
      FileOperations.deleteFile (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}

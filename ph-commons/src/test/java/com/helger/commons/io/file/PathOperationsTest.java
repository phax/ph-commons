/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.Nonnull;

import org.junit.Test;

/**
 * Test class for class {@link PathOperations}.
 *
 * @author Philip Helger
 */
public final class PathOperationsTest
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
    final Path aDir1 = Paths.get ("TestDir");
    assertFalse (Files.isDirectory (aDir1));
    _expectedSuccess (PathOperations.createDir (aDir1));
    try
    {
      assertTrue (Files.isDirectory (aDir1));

      // directory already exists
      _expectedError (PathOperations.createDir (aDir1), EFileIOErrorCode.TARGET_ALREADY_EXISTS);
    }
    finally
    {
      _expectedSuccess (PathOperations.deleteDir (aDir1));
      assertFalse (Files.isDirectory (aDir1));
    }

    try
    {
      // null not allowed
      PathOperations.createDir (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testCreateDirIfNotExisting ()
  {
    final Path aDir1 = Paths.get ("TestDir");
    assertFalse (Files.isDirectory (aDir1));
    _expectedSuccess (PathOperations.createDirIfNotExisting (aDir1));
    try
    {
      assertTrue (Files.isDirectory (aDir1));

      // directory already exists
      _expectedSuccess (PathOperations.createDirIfNotExisting (aDir1));
    }
    finally
    {
      _expectedSuccess (PathOperations.deleteDir (aDir1));
      assertFalse (Files.isDirectory (aDir1));
    }

    try
    {
      // null not allowed
      PathOperations.createDirIfNotExisting (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testCreateDirRecursive ()
  {
    final Path aDir1 = Paths.get ("TestDir");
    final Path aDir11 = aDir1.resolve ("TestSubDir");
    assertFalse (Files.isDirectory (aDir1));
    assertFalse (Files.isDirectory (aDir11));
    _expectedSuccess (PathOperations.createDirRecursive (aDir11));
    try
    {
      assertTrue (Files.isDirectory (aDir1));
      assertTrue (Files.isDirectory (aDir11));

      // directory already exists
      _expectedError (PathOperations.createDirRecursive (aDir1), EFileIOErrorCode.TARGET_ALREADY_EXISTS);
      _expectedError (PathOperations.createDirRecursive (aDir11), EFileIOErrorCode.TARGET_ALREADY_EXISTS);
    }
    finally
    {
      _expectedSuccess (PathOperations.deleteDirRecursive (aDir1));
      assertFalse (Files.isDirectory (aDir11));
      assertFalse (Files.isDirectory (aDir1));
      _expectedError (PathOperations.deleteDirRecursive (aDir1), EFileIOErrorCode.SOURCE_DOES_NOT_EXIST);
    }

    // Invalid directory name
    try
    {
      PathOperations.createDirRecursive (Paths.get ("\0"));
      fail ();
    }
    catch (final InvalidPathException ex)
    {}

    try
    {
      // null not allowed
      PathOperations.createDirRecursive (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testCreateDirRecursiveIfNotExisting ()
  {
    final Path aDir1 = Paths.get ("TestDir");
    final Path aDir11 = aDir1.resolve ("TestSubDir");
    assertFalse (Files.isDirectory (aDir1));
    assertFalse (Files.isDirectory (aDir11));
    _expectedSuccess (PathOperations.createDirRecursiveIfNotExisting (aDir11));
    try
    {
      assertTrue (Files.isDirectory (aDir1));
      assertTrue (Files.isDirectory (aDir11));

      // directory already exists
      _expectedSuccess (PathOperations.createDirRecursiveIfNotExisting (aDir1));
      _expectedSuccess (PathOperations.createDirRecursiveIfNotExisting (aDir11));
    }
    finally
    {
      _expectedSuccess (PathOperations.deleteDirRecursive (aDir1));
      assertFalse (Files.isDirectory (aDir11));
      assertFalse (Files.isDirectory (aDir1));
      _expectedError (PathOperations.deleteDirRecursive (aDir1), EFileIOErrorCode.SOURCE_DOES_NOT_EXIST);
    }

    try
    {
      // Invalid directory name
      PathOperations.createDirRecursiveIfNotExisting (Paths.get ("\0"));
      fail ();
    }
    catch (final InvalidPathException ex)
    {}

    try
    {
      // null not allowed
      PathOperations.createDirRecursiveIfNotExisting (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testRenameDir ()
  {
    final Path aSrcDir = Paths.get ("SourceDir");
    final Path aDstDir = Paths.get ("DestDir");
    assertNotEquals (aSrcDir, aDstDir);
    assertFalse (Files.isDirectory (aSrcDir));
    assertFalse (Files.isDirectory (aDstDir));

    // create source directory
    _expectedSuccess (PathOperations.createDir (aSrcDir));
    try
    {
      assertTrue (Files.isDirectory (aSrcDir));
      assertFalse (Files.isDirectory (aDstDir));

      // rename
      _expectedSuccess (PathOperations.renameDir (aSrcDir, aDstDir));
      assertFalse (Files.isDirectory (aSrcDir));
      assertTrue (Files.isDirectory (aDstDir));

      // rename again
      _expectedError (PathOperations.renameDir (aSrcDir, aDstDir), EFileIOErrorCode.SOURCE_DOES_NOT_EXIST);
      assertFalse (Files.isDirectory (aSrcDir));
      assertTrue (Files.isDirectory (aDstDir));

      // rename same
      _expectedError (PathOperations.renameDir (aDstDir, aDstDir), EFileIOErrorCode.SOURCE_EQUALS_TARGET);
      assertFalse (Files.isDirectory (aSrcDir));
      assertTrue (Files.isDirectory (aDstDir));

      // rename back
      _expectedSuccess (PathOperations.renameDir (aDstDir, aSrcDir));
      assertTrue (Files.isDirectory (aSrcDir));
      assertFalse (Files.isDirectory (aDstDir));

      // create destination directory
      _expectedSuccess (PathOperations.createDir (aDstDir));
      try
      {
        // cannot rename to existing directory
        _expectedError (PathOperations.renameDir (aSrcDir, aDstDir), EFileIOErrorCode.TARGET_ALREADY_EXISTS);
      }
      finally
      {
        _expectedSuccess (PathOperations.deleteDir (aDstDir));
      }

      // create child of src folder
      final Path aSrcDir11 = aSrcDir.resolve ("Another");
      // cannot rename to child directory
      _expectedError (PathOperations.renameDir (aSrcDir, aSrcDir11), EFileIOErrorCode.TARGET_IS_CHILD_OF_SOURCE);
    }
    finally
    {
      // Don't know where we are in case of an error -> no expected result
      PathOperations.deleteDir (aSrcDir);
      PathOperations.deleteDir (aDstDir);
      assertFalse (Files.isDirectory (aSrcDir));
      assertFalse (Files.isDirectory (aDstDir));
    }

    try
    {
      // null not allowed
      PathOperations.renameDir (null, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null not allowed
      PathOperations.renameDir (null, aDstDir);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null not allowed
      PathOperations.renameDir (aSrcDir, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testRenameFile ()
  {
    final Path aPath = Paths.get ("dummy.txt");
    final Path aPath2 = Paths.get ("dummy2.txt");
    assertFalse (Files.isRegularFile (aPath));
    try
    {
      SimpleFileIO.writeFile (aPath.toFile (), "abc".getBytes (StandardCharsets.ISO_8859_1));
      assertTrue (Files.isRegularFile (aPath));

      SimpleFileIO.writeFile (aPath2.toFile (), "abc".getBytes (StandardCharsets.ISO_8859_1));
      _expectedError (PathOperations.renameFile (aPath, aPath2), EFileIOErrorCode.TARGET_ALREADY_EXISTS);
      _expectedSuccess (PathOperations.deleteFile (aPath2));

      _expectedSuccess (PathOperations.renameFile (aPath, aPath2));
      _expectedError (PathOperations.renameFile (aPath, aPath2), EFileIOErrorCode.SOURCE_DOES_NOT_EXIST);
      _expectedSuccess (PathOperations.renameFile (aPath2, aPath));
      _expectedError (PathOperations.renameFile (aPath, aPath), EFileIOErrorCode.SOURCE_EQUALS_TARGET);
      _expectedError (PathOperations.renameFile (aPath, Paths.get ("target/../" + aPath.toString ())),
                      EFileIOErrorCode.SOURCE_EQUALS_TARGET);
      _expectedError (PathOperations.renameFile (aPath, Paths.get ("dirnonexisting/../" + aPath.toString ())),
                      EFileIOErrorCode.SOURCE_EQUALS_TARGET);
    }
    finally
    {
      _expectedSuccess (PathOperations.deleteFile (aPath));
    }

    try
    {
      PathOperations.renameFile (aPath, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      PathOperations.renameFile (null, aPath);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testCopyFile ()
  {
    final Path aPath = Paths.get ("dummy.txt");
    final Path aPath2 = Paths.get ("dummy2.txt");
    try
    {
      assertFalse (Files.isRegularFile (aPath));
      assertFalse (Files.isRegularFile (aPath2));

      _expectedError (PathOperations.copyFile (aPath, aPath2), EFileIOErrorCode.SOURCE_DOES_NOT_EXIST);

      SimpleFileIO.writeFile (aPath.toFile (), "abc".getBytes (StandardCharsets.ISO_8859_1));
      assertTrue (Files.isRegularFile (aPath));

      SimpleFileIO.writeFile (aPath2.toFile (), "abc".getBytes (StandardCharsets.ISO_8859_1));
      _expectedError (PathOperations.copyFile (aPath, aPath2), EFileIOErrorCode.TARGET_ALREADY_EXISTS);
      _expectedSuccess (PathOperations.deleteFile (aPath2));

      _expectedSuccess (PathOperations.copyFile (aPath, aPath2));
      assertTrue (Files.isRegularFile (aPath));
      assertTrue (Files.isRegularFile (aPath2));
      assertArrayEquals (SimpleFileIO.getAllFileBytes (aPath.toFile ()),
                         SimpleFileIO.getAllFileBytes (aPath2.toFile ()));
      _expectedError (PathOperations.copyFile (aPath, aPath), EFileIOErrorCode.SOURCE_EQUALS_TARGET);
      _expectedError (PathOperations.copyFile (aPath, Paths.get ("target/../" + aPath.toString ())),
                      EFileIOErrorCode.SOURCE_EQUALS_TARGET);
      _expectedError (PathOperations.copyFile (aPath, Paths.get ("dirnonexisting/../" + aPath.toString ())),
                      EFileIOErrorCode.SOURCE_EQUALS_TARGET);
    }
    finally
    {
      PathOperations.deleteFile (aPath);
      PathOperations.deleteFile (aPath2);
    }

    try
    {
      PathOperations.copyFile (aPath, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      PathOperations.copyFile (null, aPath);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testCopyDir ()
  {
    final Path fDir = Paths.get ("copydirtest");
    final Path fDir2 = Paths.get ("copieddirtest");
    try
    {
      _expectedError (PathOperations.copyDirRecursive (fDir, fDir), EFileIOErrorCode.SOURCE_DOES_NOT_EXIST);
      _expectedSuccess (PathOperations.createDir (fDir));
      _expectedError (PathOperations.copyDirRecursive (fDir, fDir), EFileIOErrorCode.SOURCE_EQUALS_TARGET);
      _expectedError (PathOperations.copyDirRecursive (Paths.get ("xx/..").resolve (fDir), fDir),
                      EFileIOErrorCode.SOURCE_EQUALS_TARGET);
      _expectedError (PathOperations.copyDirRecursive (fDir, Paths.get ("xx/..").resolve (fDir)),
                      EFileIOErrorCode.SOURCE_EQUALS_TARGET);
      _expectedError (PathOperations.copyDirRecursive (fDir, Paths.get (".")), EFileIOErrorCode.TARGET_ALREADY_EXISTS);
      _expectedError (PathOperations.copyDirRecursive (fDir, fDir.resolve ("bla")),
                      EFileIOErrorCode.TARGET_IS_CHILD_OF_SOURCE);
      _expectedError (PathOperations.copyDirRecursive (fDir, Paths.get ("target")),
                      EFileIOErrorCode.TARGET_ALREADY_EXISTS);

      for (int i = 1; i <= 10; ++i)
        SimpleFileIO.writeFile (fDir.resolve ("file" + i).toFile (), "Hallo", StandardCharsets.ISO_8859_1);
      _expectedSuccess (PathOperations.createDir (fDir.resolve ("subdir")));
      for (int i = 1; i <= 10; ++i)
        SimpleFileIO.writeFile (fDir.resolve ("subdir/file" + i).toFile (), "Hallo", StandardCharsets.ISO_8859_1);
      assertEquals (11, PathHelper.getDirectoryObjectCount (fDir));

      assertFalse (Files.isDirectory (fDir2));
      _expectedSuccess (PathOperations.copyDirRecursive (fDir, fDir2));
      assertTrue (Files.isDirectory (fDir2));
      assertEquals (11, PathHelper.getDirectoryObjectCount (fDir2));
    }
    finally
    {
      PathOperations.deleteDirRecursive (fDir);
      PathOperations.deleteDirRecursive (fDir2);
    }

    try
    {
      PathOperations.copyDirRecursive (fDir, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      PathOperations.copyDirRecursive (null, fDir);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testDeleteDir ()
  {
    final Path aDir = Paths.get ("deldir.test");
    try
    {
      _expectedError (PathOperations.deleteDir (aDir), EFileIOErrorCode.SOURCE_DOES_NOT_EXIST);
      assertFalse (Files.isDirectory (aDir));
      _expectedSuccess (PathOperations.createDir (aDir));
      assertEquals (0, PathHelper.getDirectoryObjectCount (aDir));
      _expectedSuccess (PathOperations.deleteDir (aDir));
      assertFalse (Files.isDirectory (aDir));
    }
    finally
    {
      PathOperations.deleteDirRecursive (aDir);
    }

    try
    {
      PathOperations.deleteDir (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testDeleteDirRecursive ()
  {
    final Path aDir = Paths.get ("deldirrec2.test");
    try
    {
      assertFalse (Files.isDirectory (aDir));
      _expectedError (PathOperations.deleteDirRecursive (aDir), EFileIOErrorCode.SOURCE_DOES_NOT_EXIST);
      _expectedSuccess (PathOperations.createDir (aDir));
      for (int i = 0; i < 10; ++i)
      {
        SimpleFileIO.writeFile (aDir.resolve ("test" + i).toFile (),
                                "Inhalt von file test" + i,
                                StandardCharsets.ISO_8859_1);
        _expectedSuccess (PathOperations.createDir (aDir.resolve ("subdir" + i)));
        SimpleFileIO.writeFile (aDir.resolve ("subdir" + i + "/test" + i).toFile (),
                                "Inhalt von file subdir/test" + i,
                                StandardCharsets.ISO_8859_1);
      }
      assertEquals (20, PathHelper.getDirectoryObjectCount (aDir));
      // Cannot use regular delete, because the directory is not empty!
      _expectedError (PathOperations.deleteDir (aDir), EFileIOErrorCode.IO_ERROR);
      assertTrue (Files.isDirectory (aDir));
      _expectedSuccess (PathOperations.deleteDirRecursive (aDir));
      assertFalse (Files.isDirectory (aDir));
    }
    finally
    {
      PathOperations.deleteDirRecursive (aDir);
    }

    try
    {
      PathOperations.deleteDirRecursive (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testDeleteFile ()
  {
    _expectedError (PathOperations.deleteFile (Paths.get ("warum sollte es diese Datei geben")),
                    EFileIOErrorCode.SOURCE_DOES_NOT_EXIST);
    final Path f = Paths.get ("delfile.test");
    try
    {
      SimpleFileIO.writeFile (f.toFile (), "abc".getBytes (StandardCharsets.ISO_8859_1));
      assertTrue (Files.isRegularFile (f));
      _expectedSuccess (PathOperations.deleteFile (f));
      assertFalse (Files.isRegularFile (f));
      _expectedError (PathOperations.deleteFile (f), EFileIOErrorCode.SOURCE_DOES_NOT_EXIST);
    }
    finally
    {
      PathOperations.deleteFile (f);
    }

    try
    {
      PathOperations.deleteFile (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}

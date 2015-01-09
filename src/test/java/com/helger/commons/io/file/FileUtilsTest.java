/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.helger.commons.CGlobal;
import com.helger.commons.equals.EqualsUtils;
import com.helger.commons.io.EAppend;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.streams.StreamUtils;
import com.helger.commons.system.EOperatingSystem;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link FileUtils}.
 * 
 * @author Philip Helger
 */
public final class FileUtilsTest
{
  @Test
  public void testExistsFile ()
  {
    assertFalse (FileUtils.existsFile (new File ("oaaajeee")));
    assertTrue (FileUtils.existsFile (ClassPathResource.getAsFile ("streamutils-lines")));
    assertFalse (FileUtils.existsFile (new File ("src")));

    try
    {
      FileUtils.existsFile (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testExistsDir ()
  {
    assertFalse (FileUtils.existsDir (new File ("oaaajeee")));
    assertTrue (FileUtils.existsDir (new File ("src")));
    assertFalse (FileUtils.existsDir (ClassPathResource.getAsFile ("streamutils-lines")));

    try
    {
      FileUtils.existsDir (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  @SuppressFBWarnings (value = "DMI_HARDCODED_ABSOLUTE_FILENAME")
  public void testEnsureParentDirectoryIsPresent ()
  {
    // Root directory (no parent)
    assertTrue (FileUtils.ensureParentDirectoryIsPresent (new File ("/")).isUnchanged ());
    // Existing folder
    assertTrue (FileUtils.ensureParentDirectoryIsPresent (new File ("src")).isUnchanged ());
    // Existing file
    assertTrue (FileUtils.ensureParentDirectoryIsPresent (ClassPathResource.getAsFile ("streamutils-lines"))
                         .isUnchanged ());

    // Non existing object
    try
    {
      assertTrue (FileUtils.ensureParentDirectoryIsPresent (new File ("parent", "file")).isChanged ());
      assertTrue (FileUtils.existsDir (new File ("parent")));
      assertTrue (FileUtils.ensureParentDirectoryIsPresent (new File ("parent", "file2")).isUnchanged ());
    }
    finally
    {
      FileOperations.deleteDirRecursive (new File ("parent"));
    }

    try
    {
      FileUtils.ensureParentDirectoryIsPresent (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  private static void _expectedSuccess (final FileIOError ec)
  {
    assertEquals ("Expected no error but got " + ec.getErrorCode (), EFileIOErrorCode.NO_ERROR, ec.getErrorCode ());
  }

  /**
   * Test for method isParentDirectory
   */
  @Test
  public void testIsParentDirectory ()
  {
    // make test create directories in other directory or kick test
    final File aDir1 = new File ("Directory");
    final File aDir11 = new File (aDir1, "Subdirectory");
    final File aDir2 = new File ("Another Directory");

    FileOperations.createDir (aDir1);
    try
    {
      // Not a directory :)
      assertFalse (FileUtils.isParentDirectory (aDir1, new File ("pom.xml")));
      assertFalse (FileUtils.isParentDirectory (new File ("pom.xml"), aDir1));
      FileOperations.createDir (aDir11);
      try
      {
        FileOperations.createDir (aDir2);
        try
        {
          assertFalse (FileUtils.isParentDirectory (aDir1, aDir2));
          assertFalse (FileUtils.isParentDirectory (aDir2, aDir1));
          assertFalse (FileUtils.isParentDirectory (aDir11, aDir1));
          assertTrue (FileUtils.isParentDirectory (aDir1, aDir11));
        }
        finally
        {
          _expectedSuccess (FileOperations.deleteDir (aDir2));
        }
      }
      finally
      {
        _expectedSuccess (FileOperations.deleteDir (aDir11));
      }
    }
    finally
    {
      _expectedSuccess (FileOperations.deleteDir (aDir1));
    }

    try
    {
      FileUtils.isParentDirectory (null, aDir2);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      FileUtils.isParentDirectory (aDir1, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
  @Test
  public void testGetFileSizeDisplay ()
  {
    assertEquals ("0B", FileUtils.getFileSizeDisplay (0));
    assertEquals ("1B", FileUtils.getFileSizeDisplay (1));
    assertEquals ("1023B", FileUtils.getFileSizeDisplay (1023));
    assertEquals ("1KB", FileUtils.getFileSizeDisplay (1024));
    assertEquals ("1KB", FileUtils.getFileSizeDisplay (1025));
    assertEquals ("1MB", FileUtils.getFileSizeDisplay (CGlobal.BYTES_PER_MEGABYTE));
    assertEquals ("1GB", FileUtils.getFileSizeDisplay (CGlobal.BYTES_PER_GIGABYTE));
    assertEquals ("1TB", FileUtils.getFileSizeDisplay (CGlobal.BYTES_PER_TERABYTE));
    assertEquals ("1PB", FileUtils.getFileSizeDisplay (CGlobal.BYTES_PER_PETABYTE));

    assertEquals ("1023.0B", FileUtils.getFileSizeDisplay (1023, 1));
    assertEquals ("1.0KB", FileUtils.getFileSizeDisplay (1024, 1));
    assertEquals ("1023.0KB", FileUtils.getFileSizeDisplay (CGlobal.BYTES_PER_MEGABYTE - 1024, 1));
    assertEquals ("1.5KB", FileUtils.getFileSizeDisplay (1526, 1));
    assertEquals ("1.5KB", FileUtils.getFileSizeDisplay (1536, 1));
    assertEquals ("1.5KB", FileUtils.getFileSizeDisplay (1546, 1));

    assertEquals ("1.49KB", FileUtils.getFileSizeDisplay (1526, 2));
    assertEquals ("1.50KB", FileUtils.getFileSizeDisplay (1536, 2));
    assertEquals ("1.51KB", FileUtils.getFileSizeDisplay (1546, 2));

    assertEquals ("", FileUtils.getFileSizeDisplay (null));
    assertEquals ("", FileUtils.getFileSizeDisplay (new File ("do you think that file exists!")));
    assertTrue (FileUtils.getFileSizeDisplay (new File ("pom.xml")).length () > 0);

    try
    {
      FileUtils.getFileSizeDisplay (-1, 2);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      FileUtils.getFileSizeDisplay (1546, -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testGetSecureFile ()
  {
    assertNull (FileUtils.getSecureFile (null));
    assertEquals (new File ("abc.txt").getAbsoluteFile (), FileUtils.getSecureFile (new File ("abc.txt")));
    assertEquals (new File ("abc.txt").getAbsoluteFile (), FileUtils.getSecureFile (new File ("abc.txt\u0000.txx")));
    assertEquals (new File ("abc/abc.txt").getAbsoluteFile (),
                  FileUtils.getSecureFile (new File ("abc/abc.txt\u0000.txx")));
    assertEquals (new File ("abc.txt").getAbsoluteFile (),
                  FileUtils.getSecureFile (new File ("any/../abc.txt\u0000.txx")));
  }

  @Test
  public void testGetInputStream ()
  {
    try
    {
      FileUtils.getInputStream ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      FileUtils.getInputStream ((File) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final InputStream aIS = FileUtils.getInputStream ("pom.xml");
    assertNotNull (aIS);
    StreamUtils.close (aIS);
  }

  @Test
  public void testGetOutputStream ()
  {
    try
    {
      FileUtils.getOutputStream ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      FileUtils.getOutputStream ((String) null, EAppend.APPEND);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      FileUtils.getOutputStream ((File) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      FileUtils.getOutputStream ((File) null, EAppend.APPEND);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    // Linux allows for this filename to happen!
    if (EOperatingSystem.WINDOWS.isCurrentOS ())
    {
      final String sIllegalFilename = "  ";
      assertNull (FileUtils.getOutputStream (sIllegalFilename));
      assertNull (FileUtils.getOutputStream (new File (sIllegalFilename)));
      // target is an existing directory
      assertNull (FileUtils.getOutputStream (new File ("target", sIllegalFilename)));
      // pom.xml is an existing file (=non-directory)
      assertNull (FileUtils.getOutputStream (new File ("pom.xml", sIllegalFilename)));
    }
  }

  @Test
  public void testIsFileNewer () throws IOException
  {
    final File f = new File ("pom.xml");
    assertFalse (FileUtils.isFileNewer (f, f));
    assertTrue (FileUtils.isFileNewer (f, new File ("does not exist!")));
    assertFalse (FileUtils.isFileNewer (new File ("does not exist!"), f));

    // Create a new file
    final File f2 = new File ("deleteme.del");
    assertTrue (f2.createNewFile ());
    try
    {
      assertTrue (FileUtils.isFileNewer (f2, f));
      assertFalse (FileUtils.isFileNewer (f, f2));
    }
    finally
    {
      FileOperations.deleteFile (f2);
    }

    try
    {
      FileUtils.isFileNewer (f, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      FileUtils.isFileNewer (null, f);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetDirectoryObjectCount ()
  {
    try
    {
      FileUtils.getDirectoryObjectCount (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      FileUtils.getDirectoryObjectCount (new File ("pom.xml"));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    assertTrue (FileUtils.getDirectoryObjectCount (new File (".")) > 0);
    assertTrue (FileUtils.getDirectoryObjectCount (new File ("target")) > 0);
  }

  @Test
  public void testSafeEquals ()
  {
    assertTrue (EqualsUtils.equals (new File ("."), new File (".")));
    assertTrue (EqualsUtils.equals (new File ("."), new File ("")));
    assertTrue (EqualsUtils.equals (new File ("pom"), new File ("dir/../pom")));
    assertTrue (EqualsUtils.equals (new File ("./pom"), new File (".\\dir/../pom")));
    assertTrue (EqualsUtils.equals (null, null));
    assertFalse (EqualsUtils.equals (new File ("."), null));
    assertFalse (EqualsUtils.equals (null, new File (".")));
  }
}

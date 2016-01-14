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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.helger.commons.CGlobal;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.io.EAppend;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.system.EOperatingSystem;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link FileHelper}.
 *
 * @author Philip Helger
 */
public final class FileHelperTest
{
  @Test
  public void testExistsFile ()
  {
    assertFalse (FileHelper.existsFile (new File ("oaaajeee")));
    assertTrue (FileHelper.existsFile (ClassPathResource.getAsFile ("streamutils-lines")));
    assertFalse (FileHelper.existsFile (new File ("src")));
  }

  @Test
  public void testExistsDir ()
  {
    assertFalse (FileHelper.existsDir (new File ("oaaajeee")));
    assertTrue (FileHelper.existsDir (new File ("src")));
    assertFalse (FileHelper.existsDir (ClassPathResource.getAsFile ("streamutils-lines")));
  }

  @Test
  @SuppressFBWarnings (value = "DMI_HARDCODED_ABSOLUTE_FILENAME")
  public void testEnsureParentDirectoryIsPresent ()
  {
    // Root directory (no parent)
    assertTrue (FileHelper.ensureParentDirectoryIsPresent (new File ("/")).isUnchanged ());
    // Existing folder
    assertTrue (FileHelper.ensureParentDirectoryIsPresent (new File ("src")).isUnchanged ());
    // Existing file
    assertTrue (FileHelper.ensureParentDirectoryIsPresent (ClassPathResource.getAsFile ("streamutils-lines"))
                          .isUnchanged ());

    // Non existing object
    try
    {
      assertTrue (FileHelper.ensureParentDirectoryIsPresent (new File ("parent", "file")).isChanged ());
      assertTrue (FileHelper.existsDir (new File ("parent")));
      assertTrue (FileHelper.ensureParentDirectoryIsPresent (new File ("parent", "file2")).isUnchanged ());
    }
    finally
    {
      FileOperations.deleteDirRecursive (new File ("parent"));
    }

    try
    {
      FileHelper.ensureParentDirectoryIsPresent (null);
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
      assertFalse (FileHelper.isParentDirectory (aDir1, new File ("pom.xml")));
      assertFalse (FileHelper.isParentDirectory (new File ("pom.xml"), aDir1));
      FileOperations.createDir (aDir11);
      try
      {
        FileOperations.createDir (aDir2);
        try
        {
          assertFalse (FileHelper.isParentDirectory (aDir1, aDir2));
          assertFalse (FileHelper.isParentDirectory (aDir2, aDir1));
          assertFalse (FileHelper.isParentDirectory (aDir11, aDir1));
          assertTrue (FileHelper.isParentDirectory (aDir1, aDir11));
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
      FileHelper.isParentDirectory (null, aDir2);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      FileHelper.isParentDirectory (aDir1, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @SuppressFBWarnings ("TQ_NEVER_VALUE_USED_WHERE_ALWAYS_REQUIRED")
  @Test
  public void testGetFileSizeDisplay ()
  {
    assertEquals ("0B", FileHelper.getFileSizeDisplay (0));
    assertEquals ("1B", FileHelper.getFileSizeDisplay (1));
    assertEquals ("1023B", FileHelper.getFileSizeDisplay (1023));
    assertEquals ("1KB", FileHelper.getFileSizeDisplay (1024));
    assertEquals ("1KB", FileHelper.getFileSizeDisplay (1025));
    assertEquals ("1MB", FileHelper.getFileSizeDisplay (CGlobal.BYTES_PER_MEGABYTE));
    assertEquals ("1GB", FileHelper.getFileSizeDisplay (CGlobal.BYTES_PER_GIGABYTE));
    assertEquals ("1TB", FileHelper.getFileSizeDisplay (CGlobal.BYTES_PER_TERABYTE));
    assertEquals ("1PB", FileHelper.getFileSizeDisplay (CGlobal.BYTES_PER_PETABYTE));

    assertEquals ("1023.0B", FileHelper.getFileSizeDisplay (1023, 1));
    assertEquals ("1.0KB", FileHelper.getFileSizeDisplay (1024, 1));
    assertEquals ("1023.0KB", FileHelper.getFileSizeDisplay (CGlobal.BYTES_PER_MEGABYTE - 1024, 1));
    assertEquals ("1.5KB", FileHelper.getFileSizeDisplay (1526, 1));
    assertEquals ("1.5KB", FileHelper.getFileSizeDisplay (1536, 1));
    assertEquals ("1.5KB", FileHelper.getFileSizeDisplay (1546, 1));

    assertEquals ("1.49KB", FileHelper.getFileSizeDisplay (1526, 2));
    assertEquals ("1.50KB", FileHelper.getFileSizeDisplay (1536, 2));
    assertEquals ("1.51KB", FileHelper.getFileSizeDisplay (1546, 2));

    assertEquals ("", FileHelper.getFileSizeDisplay (null));
    assertEquals ("", FileHelper.getFileSizeDisplay (new File ("do you think that file exists!")));
    assertTrue (FileHelper.getFileSizeDisplay (new File ("pom.xml")).length () > 0);

    try
    {
      FileHelper.getFileSizeDisplay (-1, 2);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      FileHelper.getFileSizeDisplay (1546, -1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }

  @Test
  public void testGetSecureFile ()
  {
    assertNull (FileHelper.getSecureFile (null));
    assertEquals (new File ("abc.txt").getAbsoluteFile (), FileHelper.getSecureFile (new File ("abc.txt")));
    assertEquals (new File ("abc.txt").getAbsoluteFile (), FileHelper.getSecureFile (new File ("abc.txt\u0000.txx")));
    assertEquals (new File ("abc/abc.txt").getAbsoluteFile (),
                  FileHelper.getSecureFile (new File ("abc/abc.txt\u0000.txx")));
    assertEquals (new File ("abc.txt").getAbsoluteFile (),
                  FileHelper.getSecureFile (new File ("any/../abc.txt\u0000.txx")));
  }

  @Test
  public void testGetInputStream ()
  {
    try
    {
      FileHelper.getInputStream ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      FileHelper.getInputStream ((File) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final InputStream aIS = FileHelper.getInputStream ("pom.xml");
    assertNotNull (aIS);
    StreamHelper.close (aIS);
  }

  @Test
  public void testGetOutputStream ()
  {
    try
    {
      FileHelper.getOutputStream ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      FileHelper.getOutputStream ((String) null, EAppend.APPEND);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      FileHelper.getOutputStream ((File) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      FileHelper.getOutputStream ((File) null, EAppend.APPEND);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    // Linux allows for this filename to happen!
    if (EOperatingSystem.WINDOWS.isCurrentOS ())
    {
      final String sIllegalFilename = "  ";
      assertNull (FileHelper.getOutputStream (sIllegalFilename));
      assertNull (FileHelper.getOutputStream (new File (sIllegalFilename)));
      // target is an existing directory
      assertNull (FileHelper.getOutputStream (new File ("target", sIllegalFilename)));
      // pom.xml is an existing file (=non-directory)
      assertNull (FileHelper.getOutputStream (new File ("pom.xml", sIllegalFilename)));
    }
  }

  @Test
  public void testIsFileNewer () throws IOException
  {
    final File f = new File ("pom.xml");
    assertFalse (FileHelper.isFileNewer (f, f));
    assertTrue (FileHelper.isFileNewer (f, new File ("does not exist!")));
    assertFalse (FileHelper.isFileNewer (new File ("does not exist!"), f));

    // Create a new file
    final File f2 = new File ("deleteme.del");
    assertTrue (f2.createNewFile ());
    try
    {
      assertTrue (FileHelper.isFileNewer (f2, f));
      assertFalse (FileHelper.isFileNewer (f, f2));
    }
    finally
    {
      FileOperations.deleteFile (f2);
    }

    try
    {
      FileHelper.isFileNewer (f, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      FileHelper.isFileNewer (null, f);
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
      FileHelper.getDirectoryObjectCount (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      FileHelper.getDirectoryObjectCount (new File ("pom.xml"));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    assertTrue (FileHelper.getDirectoryObjectCount (new File (".")) > 0);
    assertTrue (FileHelper.getDirectoryObjectCount (new File ("target")) > 0);
  }

  @Test
  public void testSafeEquals ()
  {
    assertTrue (EqualsHelper.equals (new File ("."), new File (".")));
    assertTrue (EqualsHelper.equals (new File ("."), new File ("")));
    assertTrue (EqualsHelper.equals (new File ("pom"), new File ("dir/../pom")));
    assertTrue (EqualsHelper.equals (new File ("./pom"), new File (".\\dir/../pom")));
    assertTrue (EqualsHelper.equals (null, null));
    assertFalse (EqualsHelper.equals (new File ("."), null));
    assertFalse (EqualsHelper.equals (null, new File (".")));
  }
}

/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.io.file.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FilenameFilter;

import org.junit.Test;

import com.helger.commons.io.file.FileOperations;

/**
 * Test class for class {@link FilenameFilterPublicDirectory}.
 * 
 * @author Philip Helger
 */
public final class FilenameFilterPublicDirectoryTest
{
  @Test
  public void testAll ()
  {
    final FilenameFilter ff = FilenameFilterPublicDirectory.getInstance ();
    assertNotNull (ff);

    // null directory
    assertFalse (ff.accept (null, "file.html"));

    try
    {
      // null file
      ff.accept (new File ("dir"), null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final File aDir = new File ("directory");
    FileOperations.createDir (aDir);
    try
    {
      // subdir does not exist
      assertFalse (ff.accept (aDir, "subdir"));

      // create public subdir
      File aSubDir = new File (aDir, "subdir");
      FileOperations.createDir (aSubDir);
      try
      {
        // subdir exists
        assertTrue (ff.accept (aDir, "subdir"));
        assertFalse (ff.accept (aDir, "subdir2"));
      }
      finally
      {
        FileOperations.deleteDir (aSubDir);
      }

      // create hidden subdir
      aSubDir = new File (aDir, ".subdir");
      FileOperations.createDir (aSubDir);
      try
      {
        // subdir is hidden
        assertFalse (ff.accept (aDir, ".subdir"));
      }
      finally
      {
        FileOperations.deleteDir (aSubDir);
      }
    }
    finally
    {
      FileOperations.deleteDir (aDir);
    }
  }
}

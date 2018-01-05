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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

import org.junit.Test;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link IFileFilter}.
 *
 * @author Philip Helger
 */
public final class IFileFilterFuncTest
{
  @Test
  public void testGetFilterFileOnly ()
  {
    final FileFilter aFilter = IFileFilter.fileOnly ();
    assertNotNull (aFilter);

    // file
    assertTrue (aFilter.accept (new File ("pom.xml")));
    // not existing file
    assertFalse (aFilter.accept (new File ("file.htm")));
    // directory
    assertFalse (aFilter.accept (new File ("src")));
    // null
    assertFalse (aFilter.accept (null));
  }

  @Test
  public void testGetFilterDirectoryOnly ()
  {
    final FileFilter aFilter = IFileFilter.directoryOnly ();
    assertNotNull (aFilter);

    // file
    assertFalse (aFilter.accept (new File ("pom.xml")));
    // not existing file
    assertFalse (aFilter.accept (new File ("file.htm")));
    // directory
    assertTrue (aFilter.accept (new File ("src")));
    // null
    assertFalse (aFilter.accept (null));
  }

  @Test
  public void testGetFilterDirectoryPublic ()
  {
    final FileFilter aFilter = IFileFilter.directoryPublic ();
    assertNotNull (aFilter);

    // file
    assertFalse (aFilter.accept (new File ("pom.xml")));
    // not existing file
    assertFalse (aFilter.accept (new File ("file.htm")));
    // directory
    assertTrue (aFilter.accept (new File ("src")));
    // not existing directory
    assertFalse (aFilter.accept (new File (".internal")));
    // null
    assertFalse (aFilter.accept (null));
  }

  @Test
  public void testGetParentDirectoryPublic ()
  {
    final FileFilter aFilter = IFileFilter.parentDirectoryPublic ();
    assertNotNull (aFilter);

    // file
    assertTrue (aFilter.accept (new File ("pom.xml")));
    // not existing file
    assertTrue (aFilter.accept (new File ("file.htm")));
    // directory
    assertTrue (aFilter.accept (new File ("src")));
    // null
    assertFalse (aFilter.accept (null));
    // hidden file
    assertFalse (aFilter.accept (new File (".svn/pom.xml")));
  }

  @Test
  public void testGetFilterFilenameHidden ()
  {
    final FileFilter aFilter = IFileFilter.filenameHidden ();
    assertNotNull (aFilter);

    // file
    assertFalse (aFilter.accept (new File ("pom.xml")));
    // not existing file
    assertFalse (aFilter.accept (new File ("file.htm")));
    // directory
    assertFalse (aFilter.accept (new File ("src")));
    // not existing directory
    assertTrue (aFilter.accept (new File (".internal")));
    // null
    assertFalse (aFilter.accept (null));
  }

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testFilenameStartsWith ()
  {
    try
    {
      // null not allowed
      IFileFilter.filenameStartsWith (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final FilenameFilter ff = IFileFilter.filenameStartsWith ("file");
    assertNotNull (ff);
    assertTrue (ff.accept (null, "file.htm"));
    assertTrue (ff.accept (new File ("dir"), "file.htm"));
    assertFalse (ff.accept (null, "hello.html"));
    assertFalse (ff.accept (new File ("dir"), "hello.html"));
    assertFalse (ff.accept (null, null));
    assertFalse (ff.accept (null, ""));
  }

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testFilenameEndsWith ()
  {
    try
    {
      // null not allowed
      IFileFilter.filenameEndsWith (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final FilenameFilter ff = IFileFilter.filenameEndsWith (".htm");
    assertNotNull (ff);
    assertTrue (ff.accept (null, "file.htm"));
    assertTrue (ff.accept (new File ("dir"), "file.htm"));
    assertFalse (ff.accept (null, "file.html"));
    assertFalse (ff.accept (new File ("dir"), "file.html"));
    assertFalse (ff.accept (null, null));
    assertFalse (ff.accept (null, ""));
  }

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testFilenameEqualsIgnoreCase ()
  {
    try
    {
      // null not allowed
      IFileFilter.filenameEqualsIgnoreCase (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final FilenameFilter ff = IFileFilter.filenameEqualsIgnoreCase ("file.htm");
    assertNotNull (ff);
    assertTrue (ff.accept (null, "file.htm"));
    assertTrue (ff.accept (new File ("dir"), "file.htm"));
    assertTrue (ff.accept (null, "FILE.HTM"));
    assertTrue (ff.accept (new File ("dir"), "FILE.HTM"));
    assertFalse (ff.accept (null, "hello.html"));
    assertFalse (ff.accept (new File ("dir"), "hello.html"));
    assertFalse (ff.accept (null, "HELLO.HTML"));
    assertFalse (ff.accept (new File ("dir"), "HELLO.HTML"));
    assertFalse (ff.accept (null, null));
    assertFalse (ff.accept (null, ""));
  }

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testFilenameEquals ()
  {
    try
    {
      // null not allowed
      IFileFilter.filenameEquals (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final FilenameFilter ff = IFileFilter.filenameEquals ("file.htm");
    assertNotNull (ff);
    assertTrue (ff.accept (null, "file.htm"));
    assertTrue (ff.accept (new File ("dir"), "file.htm"));
    assertFalse (ff.accept (null, "hello.html"));
    assertFalse (ff.accept (new File ("dir"), "hello.html"));
    assertFalse (ff.accept (null, null));
    assertFalse (ff.accept (null, ""));
  }

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testFilenameMatchAnyRegEx ()
  {
    try
    {
      // null not allowed
      IFileFilter.filenameMatchAnyRegEx ((String []) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final FilenameFilter ff = IFileFilter.filenameMatchAnyRegEx (".*htm$");
    assertNotNull (ff);
    assertTrue (ff.accept (null, "file.htm"));
    assertTrue (ff.accept (new File ("dir"), "file.htm"));
    assertFalse (ff.accept (null, "file.html"));
    assertFalse (ff.accept (new File ("dir"), "file.html"));
    assertFalse (ff.accept (null, null));
    assertFalse (ff.accept (null, ""));
  }

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testFilenameMatchNoRegEx ()
  {
    try
    {
      // null not allowed
      IFileFilter.filenameMatchNoRegEx ((String []) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final FilenameFilter ff = IFileFilter.filenameMatchNoRegEx (".*html$");
    assertNotNull (ff);
    assertTrue (ff.accept (null, "file.htm"));
    assertTrue (ff.accept (new File ("dir"), "file.htm"));
    assertFalse (ff.accept (null, "file.html"));
    assertFalse (ff.accept (new File ("dir"), "file.html"));
    assertFalse (ff.accept (null, null));
    assertTrue (ff.accept (null, ""));
  }
}

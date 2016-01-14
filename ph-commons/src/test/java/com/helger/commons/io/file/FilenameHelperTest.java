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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import com.helger.commons.system.EOperatingSystem;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link FilenameHelper}.
 *
 * @author Philip Helger
 */
public final class FilenameHelperTest
{
  /**
   * Test method indexOfExtension
   */
  @Test
  public void testGetIndexOfExtension ()
  {
    assertEquals (-1, FilenameHelper.getIndexOfExtension (null));
    assertEquals (2, FilenameHelper.getIndexOfExtension ("ab.cde"));
    assertEquals (0, FilenameHelper.getIndexOfExtension (".abcde"));
    assertEquals (-1, FilenameHelper.getIndexOfExtension ("abcde"));
    assertEquals (5, FilenameHelper.getIndexOfExtension ("a/b/c.de"));
    assertEquals (5, FilenameHelper.getIndexOfExtension ("a/b/c.de"));
    assertEquals (9, FilenameHelper.getIndexOfExtension ("a/b/c.d/e.f"));
    assertEquals (-1, FilenameHelper.getIndexOfExtension ("a/b/c.d/ef"));
  }

  /**
   * Test method getWithoutExtension
   */
  @Test
  public void testGetWithoutExtension ()
  {
    assertNull (FilenameHelper.getWithoutExtension ((String) null));
    assertNull (FilenameHelper.getWithoutExtension ((File) null));
    assertEquals ("", FilenameHelper.getWithoutExtension (""));
    assertEquals ("xyz", FilenameHelper.getWithoutExtension ("xyz"));
    assertEquals ("xyz", FilenameHelper.getWithoutExtension ("xyz.sh"));
    assertEquals ("ABC", FilenameHelper.getWithoutExtension ("ABC.exe"));
    assertEquals ("a/b/c", FilenameHelper.getWithoutExtension ("a/b/c"));
    assertEquals ("a/b/c", FilenameHelper.getWithoutExtension ("a/b/c.exe"));
  }

  /**
   * Test method getBaseName
   */
  @Test
  public void testGetBaseName ()
  {
    assertNull (FilenameHelper.getBaseName ((String) null));
    assertNull (FilenameHelper.getBaseName ((File) null));
    assertEquals ("", FilenameHelper.getBaseName (""));
    assertEquals ("xyz", FilenameHelper.getBaseName ("xyz"));
    assertEquals ("xyz", FilenameHelper.getBaseName ("xyz.sh"));
    assertEquals ("ABC", FilenameHelper.getBaseName ("ABC.exe"));
    assertEquals ("c", FilenameHelper.getBaseName ("a/b/c"));
    assertEquals ("c", FilenameHelper.getBaseName ("a/b/c.exe"));
  }

  /**
   * Test method getExtension
   */
  @Test
  public void testGetExtensionString ()
  {
    assertEquals ("", FilenameHelper.getExtension ((String) null));
    assertEquals ("", FilenameHelper.getExtension ((File) null));
    assertEquals ("", FilenameHelper.getExtension (""));
    assertEquals ("", FilenameHelper.getExtension ("xyz"));
    assertEquals ("sh", FilenameHelper.getExtension ("xyz.sh"));
    assertEquals ("exe", FilenameHelper.getExtension ("ABC.exe"));
    assertEquals ("", FilenameHelper.getExtension ("a/b/c"));
    assertEquals ("exe", FilenameHelper.getExtension ("a/b/c.exe"));
  }

  @Test
  public void testGetExtensionFile ()
  {
    assertEquals ("", FilenameHelper.getExtension ((File) null));
    assertEquals ("", FilenameHelper.getExtension (new File ("")));
    assertEquals ("", FilenameHelper.getExtension (new File ("xyz")));
    assertEquals ("sh", FilenameHelper.getExtension (new File ("xyz.sh")));
    assertEquals ("exe", FilenameHelper.getExtension (new File ("ABC.exe")));
    assertEquals ("", FilenameHelper.getExtension (new File ("a/b/c")));
    assertEquals ("exe", FilenameHelper.getExtension (new File ("a/b/c.exe")));
  }

  /**
   * Test method indexOfLastSeparator
   */
  @Test
  public void testGetIndexOfLastSeparator ()
  {
    assertEquals (-1, FilenameHelper.getIndexOfLastSeparator (null));
    assertEquals (-1, FilenameHelper.getIndexOfLastSeparator (""));
    assertEquals (-1, FilenameHelper.getIndexOfLastSeparator ("xyz"));
    assertEquals (-1, FilenameHelper.getIndexOfLastSeparator ("xyz.sh"));
    assertEquals (-1, FilenameHelper.getIndexOfLastSeparator ("ABC.exe"));
    assertEquals (3, FilenameHelper.getIndexOfLastSeparator ("a/b/c"));
    assertEquals (3, FilenameHelper.getIndexOfLastSeparator ("a/b/c.exe"));
    assertEquals (9, FilenameHelper.getIndexOfLastSeparator ("a/b/c.exe/def"));
    assertEquals (9, FilenameHelper.getIndexOfLastSeparator ("a\\b\\c.exe/def"));
    assertEquals (9, FilenameHelper.getIndexOfLastSeparator ("a/b/c.exe\\def"));
    assertEquals (9, FilenameHelper.getIndexOfLastSeparator ("a\\b\\c.exe\\def"));
  }

  /**
   * Test method getWithoutPath
   */
  @Test
  public void testGetWithoutPath ()
  {
    assertNull (FilenameHelper.getWithoutPath ((String) null));
    assertNull (FilenameHelper.getWithoutPath ((File) null));
    assertEquals ("", FilenameHelper.getWithoutPath (""));
    assertEquals ("xyz", FilenameHelper.getWithoutPath ("xyz"));
    assertEquals ("xyz.sh", FilenameHelper.getWithoutPath ("xyz.sh"));
    assertEquals ("ABC.exe", FilenameHelper.getWithoutPath ("ABC.exe"));
    assertEquals ("c", FilenameHelper.getWithoutPath ("a/b/c"));
    assertEquals ("c.exe", FilenameHelper.getWithoutPath ("a/b/c.exe"));
    assertEquals ("c.exe", FilenameHelper.getWithoutPath ("a/b\\c.exe"));
    assertEquals ("c.exe", FilenameHelper.getWithoutPath ("a\\b/c.exe"));
    assertEquals ("c.exe", FilenameHelper.getWithoutPath ("a\\b\\c.exe"));
  }

  /**
   * Test method getPath
   */
  @Test
  public void testGetPath ()
  {
    assertNull (FilenameHelper.getPath (null));
    assertEquals ("", FilenameHelper.getPath (""));
    assertEquals ("", FilenameHelper.getPath ("xyz"));
    assertEquals ("", FilenameHelper.getPath ("xyz.sh"));
    assertEquals ("", FilenameHelper.getPath ("ABC.exe"));
    assertEquals ("a/b/", FilenameHelper.getPath ("a/b/c"));
    assertEquals ("a/b/", FilenameHelper.getPath ("a/b/c.exe"));
    assertEquals ("a/b\\", FilenameHelper.getPath ("a/b\\c.exe"));
    assertEquals ("a\\b/", FilenameHelper.getPath ("a\\b/c.exe"));
    assertEquals ("a\\b\\", FilenameHelper.getPath ("a\\b\\c.exe"));
  }

  /**
   * Test method isValidFilename
   */
  @Test
  public void testIsValidFilename ()
  {
    assertFalse (FilenameHelper.isValidFilename (null));
    assertFalse (FilenameHelper.isValidFilename (""));
    assertFalse (FilenameHelper.isValidFilename (" "));
    assertFalse (FilenameHelper.isValidFilename ("abc."));
    assertTrue (FilenameHelper.isValidFilename (".abc"));
    assertTrue (FilenameHelper.isValidFilename ("xyz"));
    assertTrue (FilenameHelper.isValidFilename ("xyz.sh"));
    assertTrue (FilenameHelper.isValidFilename ("ABC.exe"));
    assertFalse (FilenameHelper.isValidFilename ("CON"));
    assertFalse (FilenameHelper.isValidFilename ("con"));
    assertFalse (FilenameHelper.isValidFilename ("coN"));
    assertFalse (FilenameHelper.isValidFilename ("CON.def"));
    assertFalse (FilenameHelper.isValidFilename ("con.def"));
    assertFalse (FilenameHelper.isValidFilename ("coN.def"));
    assertTrue (FilenameHelper.isValidFilename ("coNdef"));

    // Contains path
    assertFalse (FilenameHelper.isValidFilename ("a/b"));
    assertFalse (FilenameHelper.isValidFilename ("a\\b"));
    assertFalse (FilenameHelper.isValidFilename ("a/b/c"));
    assertFalse (FilenameHelper.isValidFilename ("a/b/c.exe"));
    assertFalse (FilenameHelper.isValidFilename ("a/b/c.exe/def.com"));

    // illegal characters
    assertFalse (FilenameHelper.isValidFilename ("ab<c"));
    assertFalse (FilenameHelper.isValidFilename ("ab>c"));
    assertFalse (FilenameHelper.isValidFilename ("ab:c"));
    assertFalse (FilenameHelper.isValidFilename ("ab?c"));
    assertFalse (FilenameHelper.isValidFilename ("ab*c"));
    assertFalse (FilenameHelper.isValidFilename ("ab\"c"));
  }

  @Test
  public void testIsValidFilenameWithPaths ()
  {
    assertFalse (FilenameHelper.isValidFilenameWithPaths (null));
    assertFalse (FilenameHelper.isValidFilenameWithPaths (""));
    assertFalse (FilenameHelper.isValidFilenameWithPaths (" "));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("abc."));
    assertTrue (FilenameHelper.isValidFilenameWithPaths (".abc"));
    assertTrue (FilenameHelper.isValidFilenameWithPaths ("xyz"));
    assertTrue (FilenameHelper.isValidFilenameWithPaths ("xyz.sh"));
    assertTrue (FilenameHelper.isValidFilenameWithPaths ("ABC.exe"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("CON"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("con"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("coN"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("CON.def"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("con.def"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("coN.def"));
    assertTrue (FilenameHelper.isValidFilenameWithPaths ("coNdef"));

    // prefixed by path
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("abc/CON"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("abc/con"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("abc/coN"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("abc/CON.def"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("abc/con.def"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("abc/coN.def"));
    assertTrue (FilenameHelper.isValidFilenameWithPaths ("abc/coNdef"));

    // absolute path
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("/CON"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("/con"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("/coN"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("/CON.def"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("/con.def"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("/coN.def"));
    assertTrue (FilenameHelper.isValidFilenameWithPaths ("/coNdef"));

    // suffixed by path
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("CON/abc"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("con/abc"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("coN/abc"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("CON.def/abc"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("con.def/abc"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("coN.def/abc"));
    assertTrue (FilenameHelper.isValidFilenameWithPaths ("coNdef/abc"));

    // prefixed and suffixed by path
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("abc/CON/abc"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("abc/con/abc"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("abc/coN/abc"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("abc/CON.def/abc"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("abc/con.def/abc"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("abc/coN.def/abc"));
    assertTrue (FilenameHelper.isValidFilenameWithPaths ("abc/coNdef/abc"));

    // Contains path
    assertTrue (FilenameHelper.isValidFilenameWithPaths ("a/b"));
    if (EOperatingSystem.WINDOWS.isCurrentOS ())
      assertTrue (FilenameHelper.isValidFilenameWithPaths ("a\\b"));
    assertTrue (FilenameHelper.isValidFilenameWithPaths ("a/b/c"));
    assertTrue (FilenameHelper.isValidFilenameWithPaths ("a/b/c.exe"));
    assertTrue (FilenameHelper.isValidFilenameWithPaths ("a/b/c.exe/def.com"));

    // illegal characters
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("ab<c"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("ab>c"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("ab:c"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("ab?c"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("ab*c"));
    assertFalse (FilenameHelper.isValidFilenameWithPaths ("ab\"c"));
  }

  /**
   * Test method makeValidFilename
   */
  @Test
  public void testGetAsValidFilename ()
  {
    assertNull (FilenameHelper.getAsSecureValidFilename (null));
    assertNull (FilenameHelper.getAsSecureValidFilename (""));
    assertNull (FilenameHelper.getAsSecureValidFilename (" "));
    assertNull (FilenameHelper.getAsSecureValidFilename ("..."));
    assertNull (FilenameHelper.getAsSecureValidFilename (" . "));
    assertEquals ("abc", FilenameHelper.getAsSecureValidFilename ("abc."));
    assertEquals (".abc", FilenameHelper.getAsSecureValidFilename (".abc"));
    assertEquals ("xyz", FilenameHelper.getAsSecureValidFilename ("xyz"));
    assertEquals ("xyz.sh", FilenameHelper.getAsSecureValidFilename ("xyz.sh"));
    assertEquals ("ABC.exe", FilenameHelper.getAsSecureValidFilename ("ABC.exe"));
    assertEquals ("a/b/c", FilenameHelper.getAsSecureValidFilename ("a/b/c"));
    assertEquals ("a/b/c.exe", FilenameHelper.getAsSecureValidFilename ("a/b/c.exe"));
    assertEquals ("a/b/c.exe/def.com", FilenameHelper.getAsSecureValidFilename ("a/b/c.exe/def.com"));
    assertEquals ("_CON", FilenameHelper.getAsSecureValidFilename ("CON"));
    assertEquals ("_con", FilenameHelper.getAsSecureValidFilename ("con"));
    assertEquals ("_coN", FilenameHelper.getAsSecureValidFilename ("coN"));
    assertEquals ("_CON.def", FilenameHelper.getAsSecureValidFilename ("CON.def"));
    assertEquals ("_con.def", FilenameHelper.getAsSecureValidFilename ("con.def"));
    assertEquals ("_coN.def", FilenameHelper.getAsSecureValidFilename ("coN.def"));
    assertEquals ("coNdef", FilenameHelper.getAsSecureValidFilename ("coNdef"));

    // illegal characters
    assertEquals ("ab_c", FilenameHelper.getAsSecureValidFilename ("ab<c"));
    assertEquals ("ab_c", FilenameHelper.getAsSecureValidFilename ("ab>c"));
    assertEquals ("ab_c", FilenameHelper.getAsSecureValidFilename ("ab:c"));
    assertEquals ("ab_c", FilenameHelper.getAsSecureValidFilename ("ab?c"));
    assertEquals ("ab_c", FilenameHelper.getAsSecureValidFilename ("ab*c"));
    assertEquals ("ab_c", FilenameHelper.getAsSecureValidFilename ("ab\"c"));
    assertEquals ("ab______c", FilenameHelper.getAsSecureValidFilename ("ab<>:?*\"c"));
    assertEquals ("ab______c______d", FilenameHelper.getAsSecureValidFilename ("ab<>:?*\"c<>:?*\"d"));
  }

  /**
   * Test method makeValidFilename
   */
  @Test
  public void testGetAsValidASCIIFilename ()
  {
    assertNull (FilenameHelper.getAsSecureValidASCIIFilename (null));
    assertNull (FilenameHelper.getAsSecureValidASCIIFilename (""));
    assertNull (FilenameHelper.getAsSecureValidASCIIFilename (" "));
    assertNull (FilenameHelper.getAsSecureValidASCIIFilename ("..."));
    assertNull (FilenameHelper.getAsSecureValidASCIIFilename (" . "));
    assertEquals ("abc", FilenameHelper.getAsSecureValidASCIIFilename ("abc."));
    assertEquals (".abc", FilenameHelper.getAsSecureValidASCIIFilename (".abc"));
    assertEquals ("xyz", FilenameHelper.getAsSecureValidASCIIFilename ("xyz"));
    assertEquals ("xyz.sh", FilenameHelper.getAsSecureValidASCIIFilename ("xyz.sh"));
    assertEquals ("ABC.exe", FilenameHelper.getAsSecureValidASCIIFilename ("ABC.exe"));
    assertEquals ("a/b/c", FilenameHelper.getAsSecureValidASCIIFilename ("a/b/c"));
    assertEquals ("a/b/c.exe", FilenameHelper.getAsSecureValidASCIIFilename ("a/b/c.exe"));
    assertEquals ("a/b/c.exe/def.com", FilenameHelper.getAsSecureValidASCIIFilename ("a/b/c.exe/def.com"));
    assertEquals ("_CON", FilenameHelper.getAsSecureValidASCIIFilename ("CON"));
    assertEquals ("_con", FilenameHelper.getAsSecureValidASCIIFilename ("con"));
    assertEquals ("_coN", FilenameHelper.getAsSecureValidASCIIFilename ("coN"));
    assertEquals ("_CON.def", FilenameHelper.getAsSecureValidASCIIFilename ("CON.def"));
    assertEquals ("_con.def", FilenameHelper.getAsSecureValidASCIIFilename ("con.def"));
    assertEquals ("_coN.def", FilenameHelper.getAsSecureValidASCIIFilename ("coN.def"));
    assertEquals ("coNdef", FilenameHelper.getAsSecureValidASCIIFilename ("coNdef"));

    // illegal characters
    assertEquals ("ab_c", FilenameHelper.getAsSecureValidASCIIFilename ("ab<c"));
    assertEquals ("ab_c", FilenameHelper.getAsSecureValidASCIIFilename ("ab>c"));
    assertEquals ("ab_c", FilenameHelper.getAsSecureValidASCIIFilename ("ab:c"));
    assertEquals ("ab_c", FilenameHelper.getAsSecureValidASCIIFilename ("ab?c"));
    assertEquals ("ab_c", FilenameHelper.getAsSecureValidASCIIFilename ("ab*c"));
    assertEquals ("ab_c", FilenameHelper.getAsSecureValidASCIIFilename ("ab\"c"));

    // Test Umlauts
    assertEquals ("___", FilenameHelper.getAsSecureValidASCIIFilename ("äöü"));
    assertEquals ("h_user.txt", FilenameHelper.getAsSecureValidASCIIFilename ("häuser.txt"));
  }

  /**
   * Test method isPathSeparatorChar
   */
  @Test
  public void testIsPathSeparatorChar ()
  {
    assertTrue (FilenameHelper.isPathSeparatorChar ('\\'));
    assertTrue (FilenameHelper.isPathSeparatorChar ('/'));
    assertFalse (FilenameHelper.isPathSeparatorChar (';'));
  }

  /**
   * Test method startsWithPathSeparatorChar
   */
  @Test
  public void testStartsWithPathSeparatorChar ()
  {
    assertTrue (FilenameHelper.startsWithPathSeparatorChar ("/usr/local/perl"));
    assertTrue (FilenameHelper.startsWithPathSeparatorChar ("\\windows\\system32\\any.exe"));
    assertFalse (FilenameHelper.startsWithPathSeparatorChar ("~/desktop.ini"));
    assertFalse (FilenameHelper.startsWithPathSeparatorChar ("data\\registry.dat"));
    assertFalse (FilenameHelper.startsWithPathSeparatorChar (" "));
    assertFalse (FilenameHelper.startsWithPathSeparatorChar (""));
    assertFalse (FilenameHelper.startsWithPathSeparatorChar (null));
  }

  /**
   * Test method endsWithPathSeparatorChar
   */
  @Test
  public void testEndsWithPathSeparatorChar ()
  {
    assertTrue (FilenameHelper.endsWithPathSeparatorChar ("/usr/local/"));
    assertTrue (FilenameHelper.endsWithPathSeparatorChar ("\\windows\\system32\\"));
    assertFalse (FilenameHelper.endsWithPathSeparatorChar ("~/desktop.ini"));
    assertFalse (FilenameHelper.endsWithPathSeparatorChar ("data\\registry.dat"));
    assertFalse (FilenameHelper.endsWithPathSeparatorChar (" "));
    assertFalse (FilenameHelper.endsWithPathSeparatorChar (""));
    assertFalse (FilenameHelper.endsWithPathSeparatorChar (null));
  }

  /**
   * Test method endsWithPathSeparatorChar
   */
  @Test
  public void testContainsPathSeparatorChar ()
  {
    assertTrue (FilenameHelper.containsPathSeparatorChar ("local/"));
    assertTrue (FilenameHelper.containsPathSeparatorChar ("\\windows"));
    assertTrue (FilenameHelper.containsPathSeparatorChar ("~/desktop.ini"));
    assertTrue (FilenameHelper.containsPathSeparatorChar ("data\\registry.dat"));
    assertFalse (FilenameHelper.containsPathSeparatorChar (" "));
    assertFalse (FilenameHelper.containsPathSeparatorChar (""));
    assertFalse (FilenameHelper.containsPathSeparatorChar (null));
  }

  /**
   * Test method hasExtension
   */
  @Test
  public void testHasExtensionString ()
  {
    assertTrue (FilenameHelper.hasExtension ("/usr/local/myfile.htm", "htm"));
    assertTrue (FilenameHelper.hasExtension ("/usr/local/myfile.htm", "HTM"));
    assertFalse (FilenameHelper.hasExtension ("/usr/local/myfile.htm", "html"));
    assertTrue (FilenameHelper.hasExtension ("/usr/local/myfile.htm", "html", "htm"));
    assertTrue (FilenameHelper.hasExtension ("myfile.htm", "htm"));
    assertTrue (FilenameHelper.hasExtension (".htm", "htm"));
    assertFalse (FilenameHelper.hasExtension ("", "htm"));
    assertFalse (FilenameHelper.hasExtension ((String) null, "htm"));
    assertFalse (FilenameHelper.hasExtension ((String) null));
    assertTrue (FilenameHelper.hasExtension ("noext", ""));
    assertFalse (FilenameHelper.hasExtension ("filewith.ext", ""));
    assertFalse (FilenameHelper.hasExtension ("filewith.ext", (String) null));
    try
    {
      // null extension array not allowed
      FilenameHelper.hasExtension ("myfile.htm", (String []) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  @SuppressFBWarnings (value = "DMI_HARDCODED_ABSOLUTE_FILENAME")
  public void testHasExtensionFile ()
  {
    assertTrue (FilenameHelper.hasExtension (new File ("/usr/local/myfile.htm"), "htm"));
    assertTrue (FilenameHelper.hasExtension (new File ("/usr/local/myfile.htm"), "HTM"));
    assertFalse (FilenameHelper.hasExtension (new File ("/usr/local/myfile.htm"), "html"));
    assertTrue (FilenameHelper.hasExtension (new File ("/usr/local/myfile.htm"), "html", "htm"));
    assertTrue (FilenameHelper.hasExtension (new File ("myfile.htm"), "htm"));
    assertTrue (FilenameHelper.hasExtension (new File (".htm"), "htm"));
    assertFalse (FilenameHelper.hasExtension (new File (""), "htm"));
    assertFalse (FilenameHelper.hasExtension ((File) null, "htm"));
    assertFalse (FilenameHelper.hasExtension ((File) null));
    assertTrue (FilenameHelper.hasExtension (new File ("noext"), ""));
    assertFalse (FilenameHelper.hasExtension (new File ("filewith.ext"), ""));
    assertFalse (FilenameHelper.hasExtension (new File ("filewith.ext"), (String) null));
    try
    {
      // null extension array not allowed
      FilenameHelper.hasExtension (new File ("myfile.htm"), (String []) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  /**
   * Test method isSystemInternalDirectory
   */
  @Test
  public void testIsSystemInternalDirectory_String ()
  {
    assertFalse (FilenameHelper.isSystemInternalDirectory ((CharSequence) null));
    assertFalse (FilenameHelper.isSystemInternalDirectory (""));
    assertTrue (FilenameHelper.isSystemInternalDirectory ("."));
    assertTrue (FilenameHelper.isSystemInternalDirectory (".."));
    assertFalse (FilenameHelper.isSystemInternalDirectory ("x"));
    assertFalse (FilenameHelper.isSystemInternalDirectory ("$"));
    assertFalse (FilenameHelper.isSystemInternalDirectory ("svn"));
    assertFalse (FilenameHelper.isSystemInternalDirectory (".svn"));
  }

  @Test
  public void testIsSystemInternalDirectory_File ()
  {
    assertFalse (FilenameHelper.isSystemInternalDirectory ((File) null));
    assertFalse (FilenameHelper.isSystemInternalDirectory (new File ("pom.xml")));
    assertFalse (FilenameHelper.isSystemInternalDirectory (new File ("target")));
    assertTrue (FilenameHelper.isSystemInternalDirectory (new File (".")));
    assertTrue (FilenameHelper.isSystemInternalDirectory (new File ("..")));
  }

  @Test
  public void testGetCleanConcatenatedUrlPath ()
  {
    assertEquals ("http://server/ctx/imgs/a.gif",
                  FilenameHelper.getCleanConcatenatedUrlPath ("http://server/ctx/css/", "../imgs/a.gif"));
    assertEquals ("http://server/ctx/imgs/a.gif",
                  FilenameHelper.getCleanConcatenatedUrlPath ("http://server/ctx/css", "../imgs/a.gif"));
    assertEquals ("http://server/imgs/a.gif",
                  FilenameHelper.getCleanConcatenatedUrlPath ("http://server/css/", "../imgs/a.gif"));
    assertEquals ("http://server/imgs/a.gif",
                  FilenameHelper.getCleanConcatenatedUrlPath ("http://server/css", "../imgs/a.gif"));
    assertEquals ("http://server/a.gif", FilenameHelper.getCleanConcatenatedUrlPath ("http://server/ctx/", "../a.gif"));
    assertEquals ("http://server/a.gif", FilenameHelper.getCleanConcatenatedUrlPath ("http://server/ctx", "../a.gif"));
    assertEquals ("http://server/a.gif", FilenameHelper.getCleanConcatenatedUrlPath ("http://server/", "../a.gif"));
    assertEquals ("http://server/a.gif", FilenameHelper.getCleanConcatenatedUrlPath ("http://server", "../a.gif"));
    assertEquals ("/imgs/a.gif", FilenameHelper.getCleanConcatenatedUrlPath ("", "../imgs/a.gif"));
    assertEquals ("/imgs/a.gif", FilenameHelper.getCleanConcatenatedUrlPath ("", "/../imgs/a.gif"));

    try
    {
      FilenameHelper.getCleanConcatenatedUrlPath (null, "a.gif");
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      FilenameHelper.getCleanConcatenatedUrlPath ("http://localhost", null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetCleanPath_File ()
  {
    final String sBasePath = FilenameHelper.getCleanPath (new File (""));
    final String sRootPath = FilenameHelper.getCleanPath (new File ("/"));

    assertEquals (sBasePath + "/target/file", FilenameHelper.getCleanPath (new File ("target/file")));
    assertEquals (sBasePath + "/target/file", FilenameHelper.getCleanPath (new File ("target/./file")));
    assertEquals (sBasePath + "/target/file", FilenameHelper.getCleanPath (new File ("./target/./file/.")));
    assertEquals (sBasePath + "/target/file", FilenameHelper.getCleanPath (new File ("./target/////./file/.////")));
    assertEquals (sBasePath + "/target/file", FilenameHelper.getCleanPath (new File ("target/sub/../file")));
    assertEquals (sRootPath + "file", FilenameHelper.getCleanPath (new File ("/../file").getAbsoluteFile ()));
    assertEquals (sRootPath + "file", FilenameHelper.getCleanPath (new File ("", "/../file").getAbsoluteFile ()));
    assertEquals (sRootPath + "file", FilenameHelper.getCleanPath (new File ("/", "/../file").getAbsoluteFile ()));
    assertEquals (sRootPath + "dir/file", FilenameHelper.getCleanPath (new File ("/../dir/file").getAbsoluteFile ()));
    assertEquals (sRootPath +
                  "dir/file",
                  FilenameHelper.getCleanPath (new File ("", "/../dir/file").getAbsoluteFile ()));
    assertEquals (sRootPath +
                  "dir/file",
                  FilenameHelper.getCleanPath (new File ("/", "/../dir/file").getAbsoluteFile ()));
    assertEquals (sRootPath +
                  "dir/file.x",
                  FilenameHelper.getCleanPath (new File ("/../dir/file.x").getAbsoluteFile ()));
    assertEquals (sRootPath +
                  "dir/file.x",
                  FilenameHelper.getCleanPath (new File ("", "/../dir/file.x").getAbsoluteFile ()));
    assertEquals (sRootPath +
                  "dir/file.x",
                  FilenameHelper.getCleanPath (new File ("/", "/../dir/file.x").getAbsoluteFile ()));

    if (EOperatingSystem.WINDOWS.isCurrentOS ())
    {
      assertEquals ("\\\\server\\share\\dir\\file",
                    FilenameHelper.getCleanPath (new File ("\\\\server\\share\\dir\\file")));

      final File aBaseFile = new File ("pom.xml");
      assertTrue (aBaseFile.exists ());
      assertFalse (FilenameHelper.isWindowsLocalUNCPath (aBaseFile));

      // Prefix "\\.\" for a local UNC path
      File aFile = new File (FilenameHelper.WINDOWS_UNC_PREFIX_LOCAL1 + aBaseFile.getAbsolutePath ());
      assertTrue (aFile.exists ());
      assertTrue (FilenameHelper.isWindowsLocalUNCPath (aFile));
      assertEquals (FilenameHelper.WINDOWS_UNC_PREFIX_LOCAL1 +
                    aBaseFile.getAbsolutePath (),
                    FilenameHelper.getCleanPath (aFile));

      // Prefix "\\?\" for a local UNC path
      aFile = new File (FilenameHelper.WINDOWS_UNC_PREFIX_LOCAL2 + aBaseFile.getAbsolutePath ());
      assertTrue (aFile.exists ());
      assertTrue (FilenameHelper.isWindowsLocalUNCPath (aFile));
      assertEquals (FilenameHelper.WINDOWS_UNC_PREFIX_LOCAL2 +
                    aBaseFile.getAbsolutePath (),
                    FilenameHelper.getCleanPath (aFile));
    }

    try
    {
      FilenameHelper.getCleanPath ((File) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetCleanPath_String ()
  {
    assertNull (FilenameHelper.getCleanPath ((String) null));
    assertEquals ("", FilenameHelper.getCleanPath (""));
    assertEquals ("/", FilenameHelper.getCleanPath ("/"));
    assertEquals ("target/file", FilenameHelper.getCleanPath ("target/file"));
    assertEquals ("target/file", FilenameHelper.getCleanPath ("target/./file"));
    assertEquals ("target/file", FilenameHelper.getCleanPath ("./target/./file/."));
    assertEquals ("target/file", FilenameHelper.getCleanPath ("./target/////./file/.////"));
    assertEquals ("target/file", FilenameHelper.getCleanPath ("target/sub/../file"));
    assertEquals ("../x/target/file", FilenameHelper.getCleanPath (".././x/./target/./sub/.././file"));
    assertEquals ("../file", FilenameHelper.getCleanPath ("../file"));
    assertEquals ("/file", FilenameHelper.getCleanPath ("/../file"));
    assertEquals ("../dir/file", FilenameHelper.getCleanPath ("../dir/file"));
    assertEquals ("/dir/file", FilenameHelper.getCleanPath ("/../dir/file"));
    assertEquals ("../dir/file.x", FilenameHelper.getCleanPath ("../dir/file.x"));
    assertEquals ("/dir/file.x", FilenameHelper.getCleanPath ("/../dir/file.x"));
  }

  @Test
  public void testMatchPathsIgnoreSepType ()
  {
    assertTrue (FilenameHelper.isEqualIgnoreFileSeparator ("", ""));
    assertTrue (FilenameHelper.isEqualIgnoreFileSeparator (null, null));
    assertTrue (FilenameHelper.isEqualIgnoreFileSeparator ("a", "a"));
    assertTrue (FilenameHelper.isEqualIgnoreFileSeparator ("a/b", "a/b"));
    assertTrue (FilenameHelper.isEqualIgnoreFileSeparator ("a/b", "a\\b"));
    assertTrue (FilenameHelper.isEqualIgnoreFileSeparator ("a\\b", "a\\b"));
  }

  @Test
  public void testGetPathUsingUnixSeparator ()
  {
    assertNull (FilenameHelper.getPathUsingUnixSeparator ((String) null));
    assertEquals ("a", FilenameHelper.getPathUsingUnixSeparator ("a"));
    assertEquals ("a/b", FilenameHelper.getPathUsingUnixSeparator ("a/b"));
    assertEquals ("a/b", FilenameHelper.getPathUsingUnixSeparator ("a\\b"));

    assertNull (FilenameHelper.getPathUsingUnixSeparator ((File) null));
    assertEquals ("a", FilenameHelper.getPathUsingUnixSeparator (new File ("a")));
    assertEquals ("a/b", FilenameHelper.getPathUsingUnixSeparator (new File ("a/b")));
    assertEquals ("a/b", FilenameHelper.getPathUsingUnixSeparator (new File ("a\\b")));
    assertEquals ("a/b", FilenameHelper.getPathUsingUnixSeparator (new File ("a", "b")));
  }

  @Test
  public void testEnsurePathEndingWithoutSeparator ()
  {
    assertNull (FilenameHelper.ensurePathEndingWithoutSeparator (null));
    assertEquals ("", FilenameHelper.ensurePathEndingWithoutSeparator (""));
    assertEquals ("abc", FilenameHelper.ensurePathEndingWithoutSeparator ("abc"));
    assertEquals ("abc", FilenameHelper.ensurePathEndingWithoutSeparator ("abc/"));
    assertEquals ("abc", FilenameHelper.ensurePathEndingWithoutSeparator ("abc//"));
    assertEquals ("abc", FilenameHelper.ensurePathEndingWithoutSeparator ("abc\\"));
    assertEquals ("abc", FilenameHelper.ensurePathEndingWithoutSeparator ("abc\\\\"));
    assertEquals ("abc", FilenameHelper.ensurePathEndingWithoutSeparator ("abc\\/\\/"));
  }

  @Test
  public void testEnsurePathEndingWithSeparator ()
  {
    assertNull (FilenameHelper.ensurePathEndingWithSeparator (null));
    assertEquals ("" + File.separator, FilenameHelper.ensurePathEndingWithSeparator (""));
    assertEquals ("abc" + File.separator, FilenameHelper.ensurePathEndingWithSeparator ("abc"));
    assertEquals ("abc/", FilenameHelper.ensurePathEndingWithSeparator ("abc/"));
    assertEquals ("abc//", FilenameHelper.ensurePathEndingWithSeparator ("abc//"));
    assertEquals ("abc\\", FilenameHelper.ensurePathEndingWithSeparator ("abc\\"));
    assertEquals ("abc\\\\", FilenameHelper.ensurePathEndingWithSeparator ("abc\\\\"));
    assertEquals ("abc\\/\\/", FilenameHelper.ensurePathEndingWithSeparator ("abc\\/\\/"));
  }

  @Test
  public void testEnsurePathStartingWithSeparator ()
  {
    assertNull (FilenameHelper.ensurePathStartingWithSeparator (null));
    assertEquals (File.separator + "", FilenameHelper.ensurePathStartingWithSeparator (""));
    assertEquals (File.separator + "abc", FilenameHelper.ensurePathStartingWithSeparator ("abc"));
    assertEquals ("/abc", FilenameHelper.ensurePathStartingWithSeparator ("/abc"));
    assertEquals ("//abc", FilenameHelper.ensurePathStartingWithSeparator ("//abc"));
    assertEquals ("\\abc", FilenameHelper.ensurePathStartingWithSeparator ("\\abc"));
    assertEquals ("\\\\abc", FilenameHelper.ensurePathStartingWithSeparator ("\\\\abc"));
    assertEquals ("\\/\\/abc", FilenameHelper.ensurePathStartingWithSeparator ("\\/\\/abc"));
  }

  @Test
  public void testGetSecureFilename ()
  {
    assertNull (FilenameHelper.getSecureFilename (null));
    assertEquals ("", FilenameHelper.getSecureFilename (""));
    assertEquals ("abc.txt", FilenameHelper.getSecureFilename ("abc.txt"));
    assertEquals ("abc.txt", FilenameHelper.getSecureFilename ("abc.txt\u0000.xyz"));
    assertEquals ("abc/abc.txt", FilenameHelper.getSecureFilename ("abc/abc.txt\u0000.xyz"));
  }

  @Test
  public void testGetRelativeToParentDirectory ()
  {
    final File aParentDir = new File (".");
    assertEquals ("file.txt", FilenameHelper.getRelativeToParentDirectory (new File ("file.txt"), aParentDir));
    assertEquals ("dir/file.txt", FilenameHelper.getRelativeToParentDirectory (new File ("dir/file.txt"), aParentDir));
    assertEquals ("file.txt", FilenameHelper.getRelativeToParentDirectory (new File ("dir/.././file.txt"), aParentDir));
    assertNull (FilenameHelper.getRelativeToParentDirectory (new File ("../dir/file.txt"), aParentDir));
    assertTrue (FilenameHelper.getRelativeToParentDirectory (new File ("file.txt"), null).endsWith ("/file.txt"));

    try
    {
      FilenameHelper.getRelativeToParentDirectory (null, aParentDir);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetAbsoluteWithEnsuredParentDirectory ()
  {
    final File aParentDir = new File (".");
    final String sParentBaseDir = FilenameHelper.getCleanPath (aParentDir);
    assertEquals (sParentBaseDir +
                  "/test.txt",
                  FilenameHelper.getAbsoluteWithEnsuredParentDirectory (aParentDir, "test.txt"));
    // Fails on Linux!
    if (false)
      assertEquals (sParentBaseDir +
                    "/test.txt",
                    FilenameHelper.getAbsoluteWithEnsuredParentDirectory (aParentDir, "/test.txt"));
    assertEquals (sParentBaseDir +
                  "/test.txt",
                  FilenameHelper.getAbsoluteWithEnsuredParentDirectory (aParentDir, "./test.txt"));
    assertEquals (sParentBaseDir +
                  "/dir/test.txt",
                  FilenameHelper.getAbsoluteWithEnsuredParentDirectory (aParentDir, "dir/test.txt"));
    assertEquals (sParentBaseDir +
                  "/test.txt",
                  FilenameHelper.getAbsoluteWithEnsuredParentDirectory (aParentDir, "dir/../test.txt"));
    // Fails on Linux!
    if (false)
      assertEquals (sParentBaseDir +
                    "/test.txt",
                    FilenameHelper.getAbsoluteWithEnsuredParentDirectory (aParentDir, "/dir/../test.txt"));
    assertNull (FilenameHelper.getAbsoluteWithEnsuredParentDirectory (aParentDir, "../test.txt"));

    try
    {
      FilenameHelper.getAbsoluteWithEnsuredParentDirectory (aParentDir, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      FilenameHelper.getAbsoluteWithEnsuredParentDirectory (null, "file.txt");
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetAbsoluteWithEnsuredParentDirectoryAbsolute ()
  {
    final File aRelativeParentDir = new File ("");
    final File aAbsoluteParentDir = aRelativeParentDir.getAbsoluteFile ();
    final File aChildDir = new File (aAbsoluteParentDir.getAbsolutePath () + "/pom.xml");
    assertNull (FilenameHelper.getAbsoluteWithEnsuredParentDirectory (aRelativeParentDir,
                                                                      aChildDir.getAbsolutePath ()));
    assertEquals (FilenameHelper.getCleanPath (aChildDir.getAbsolutePath ()),
                  FilenameHelper.getAbsoluteWithEnsuredParentDirectory (aAbsoluteParentDir,
                                                                        aChildDir.getAbsolutePath ()));
    assertNull (FilenameHelper.getAbsoluteWithEnsuredParentDirectory (aAbsoluteParentDir,
                                                                      aAbsoluteParentDir.getParent ()));
  }
}

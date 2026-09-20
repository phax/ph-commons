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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.helger.base.io.EAppend;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.state.EChange;
import com.helger.collection.commons.ICommonsList;

/**
 * Additional test class for class {@link PathHelper}, covering the stream factories, the path
 * comparisons and the directory helpers.
 *
 * @author Philip Helger
 */
public final class PathHelperStreamsTest
{
  private static final Path BASE = Path.of ("target", "junittest-pathhelper");
  private static final Path FILE = BASE.resolve ("file.txt");
  private static final Path MISSING = BASE.resolve ("does-not-exist.txt");
  private static final String CONTENT = "Hello World";

  @Before
  public void createFiles ()
  {
    PathOperations.createDirRecursiveIfNotExisting (BASE);
    SimpleFileIO.writeFile (FILE.toFile (), CONTENT, StandardCharsets.ISO_8859_1);
  }

  @After
  public void deleteFiles ()
  {
    PathOperations.deleteDirRecursiveIfExisting (BASE);
  }

  @Test
  public void testEqualPaths ()
  {
    assertTrue (PathHelper.equalPaths (FILE, BASE.resolve ("file.txt")));
    assertFalse (PathHelper.equalPaths (FILE, BASE.resolve ("other.txt")));
    // Neither exists, so the plain equals is used
    assertTrue (PathHelper.equalPaths (MISSING, BASE.resolve ("does-not-exist.txt")));
  }

  @Test
  public void testCanReadAndWriteFile ()
  {
    assertFalse (PathHelper.canReadAndWriteFile (null));
    assertTrue (PathHelper.canReadAndWriteFile (FILE));
    // Does not exist yet, but the parent directory is writable
    assertTrue (PathHelper.canReadAndWriteFile (MISSING));
  }

  @Test
  public void testEnsureParentDirectoryIsPresent ()
  {
    final Path aDeep = BASE.resolve ("a").resolve ("b").resolve ("file.txt");
    assertSame (EChange.CHANGED, PathHelper.ensureParentDirectoryIsPresent (aDeep));
    assertTrue (java.nio.file.Files.isDirectory (aDeep.getParent ()));
    // Already present
    assertSame (EChange.UNCHANGED, PathHelper.ensureParentDirectoryIsPresent (aDeep));

    try
    {
      PathHelper.ensureParentDirectoryIsPresent (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testCanonicalPaths () throws IOException
  {
    assertNull (PathHelper.getCanonicalFile (null));
    assertNotNull (PathHelper.getCanonicalFile (FILE));
    assertNull (PathHelper.getCanonicalFileOrNull (null));
    assertNotNull (PathHelper.getCanonicalFileOrNull (FILE));

    assertNull (PathHelper.getCanonicalPath (null));
    assertNotNull (PathHelper.getCanonicalPath (FILE));
    assertNull (PathHelper.getCanonicalPathOrNull (null));
    assertNotNull (PathHelper.getCanonicalPathOrNull (FILE));
  }

  @Test
  public void testIsParentDirectory ()
  {
    assertTrue (PathHelper.isParentDirectory (BASE, FILE));
    assertFalse (PathHelper.isParentDirectory (FILE, BASE));
  }

  @Test
  public void testInputStreams ()
  {
    final InputStream aIS = PathHelper.getInputStream (FILE);
    assertNotNull (aIS);
    assertEquals (CONTENT, StreamHelper.getAllBytesAsString (aIS, StandardCharsets.ISO_8859_1));

    assertNotNull (PathHelper.getBufferedInputStream (FILE));
    assertNull (PathHelper.getInputStream (MISSING));

    final Reader aReader = PathHelper.getReader (FILE, StandardCharsets.ISO_8859_1);
    assertNotNull (aReader);
    assertEquals (CONTENT, StreamHelper.getAllCharactersAsString (aReader));

    assertNotNull (PathHelper.getBufferedReader (FILE, StandardCharsets.ISO_8859_1));
    assertNull (PathHelper.getReader (MISSING, StandardCharsets.ISO_8859_1));
  }

  @Test
  public void testOutputStreams ()
  {
    final Path aOut = BASE.resolve ("out.txt");

    final OutputStream aOS = PathHelper.getOutputStream (aOut);
    assertNotNull (aOS);
    StreamHelper.close (aOS);

    final OutputStream aOS2 = PathHelper.getOutputStream (aOut, EAppend.APPEND);
    assertNotNull (aOS2);
    StreamHelper.close (aOS2);

    final OutputStream aOS3 = PathHelper.getBufferedOutputStream (aOut);
    assertNotNull (aOS3);
    StreamHelper.close (aOS3);

    final OutputStream aOS4 = PathHelper.getBufferedOutputStream (aOut, EAppend.APPEND);
    assertNotNull (aOS4);
    StreamHelper.close (aOS4);

    final Writer aW = PathHelper.getWriter (aOut, EAppend.TRUNCATE, StandardCharsets.ISO_8859_1);
    assertNotNull (aW);
    StreamHelper.close (aW);

    final Writer aW2 = PathHelper.getBufferedWriter (aOut, EAppend.APPEND, StandardCharsets.ISO_8859_1);
    assertNotNull (aW2);
    StreamHelper.close (aW2);
  }

  @Test
  public void testIsFileNewer ()
  {
    assertFalse (PathHelper.isFileNewer (FILE, FILE));
    // The first file does not exist
    assertFalse (PathHelper.isFileNewer (MISSING, FILE));
    // The second file does not exist
    assertTrue (PathHelper.isFileNewer (FILE, MISSING));
  }

  @Test
  public void testDirectoryContent ()
  {
    assertEquals (1, PathHelper.getDirectoryObjectCount (BASE));

    final ICommonsList <Path> aContent = PathHelper.getDirectoryContent (BASE);
    assertEquals (1, aContent.size ());

    final ICommonsList <Path> aFiltered = PathHelper.getDirectoryContent (BASE, x -> false);
    assertTrue (aFiltered.isEmpty ());
  }

  @Test
  public void testGetAsURL ()
  {
    assertNotNull (PathHelper.getAsURL (FILE));
  }
}

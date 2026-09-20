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
package com.helger.io.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.helger.base.io.EAppend;
import com.helger.base.io.stream.StreamHelper;
import com.helger.io.file.FileOperations;
import com.helger.io.file.SimpleFileIO;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link FileSystemCharStreamProvider}.
 *
 * @author Philip Helger
 */
public final class FileSystemCharStreamProviderTest
{
  private static final File BASE_PATH = new File ("target/junittest-charstreamprovider");
  private static final String CONTENT = "Hello World";

  private FileSystemCharStreamProvider m_aProvider;

  @Before
  public void createBasePath ()
  {
    FileOperations.createDirIfNotExisting (BASE_PATH);
    SimpleFileIO.writeFile (new File (BASE_PATH, "file.txt"), CONTENT, StandardCharsets.ISO_8859_1);
    m_aProvider = new FileSystemCharStreamProvider (BASE_PATH, StandardCharsets.ISO_8859_1);
  }

  @After
  public void deleteBasePath ()
  {
    FileOperations.deleteDirRecursiveIfExisting (BASE_PATH);
  }

  @Test
  public void testBasic ()
  {
    assertEquals (BASE_PATH, m_aProvider.getBasePath ());
    assertSame (StandardCharsets.ISO_8859_1, m_aProvider.getCharset ());
    assertNotNull (m_aProvider.toString ());
  }

  @Test
  public void testStringBasePathCtor ()
  {
    final FileSystemCharStreamProvider aProvider = new FileSystemCharStreamProvider (BASE_PATH.getPath (),
                                                                                     StandardCharsets.ISO_8859_1);
    assertEquals (BASE_PATH, aProvider.getBasePath ());
  }

  @Test
  public void testGetInputStreamAndReader ()
  {
    assertNotNull (m_aProvider.getInputStream ("file.txt"));

    final Reader aReader = m_aProvider.getReader ("file.txt");
    assertNotNull (aReader);
    assertEquals (CONTENT, StreamHelper.getAllCharactersAsString (aReader));

    assertNull (m_aProvider.getInputStream ("does-not-exist.txt"));
    assertNull (m_aProvider.getReader ("does-not-exist.txt"));
  }

  @Test
  public void testGetOutputStreamAndWriter ()
  {
    assertNotNull (m_aProvider.getOutputStream ("out.txt", EAppend.TRUNCATE));

    final Writer aWriter = m_aProvider.getWriter ("out2.txt", EAppend.TRUNCATE);
    assertNotNull (aWriter);
    StreamHelper.close (aWriter);
    assertTrue (new File (BASE_PATH, "out2.txt").exists ());

    final Writer aWriter2 = m_aProvider.getWriter ("out2.txt", EAppend.APPEND);
    assertNotNull (aWriter2);
    StreamHelper.close (aWriter2);
  }

  @Test
  public void testEqualsHashcode ()
  {
    TestHelper.testDefaultImplementationWithEqualContentObject (m_aProvider,
                                                                new FileSystemCharStreamProvider (BASE_PATH,
                                                                                                  StandardCharsets.ISO_8859_1));
    TestHelper.testDefaultImplementationWithDifferentContentObject (m_aProvider,
                                                                    new FileSystemCharStreamProvider (BASE_PATH,
                                                                                                      StandardCharsets.UTF_8));
    TestHelper.testDefaultImplementationWithDifferentContentObject (m_aProvider,
                                                                    new FileSystemCharStreamProvider (new File ("target"),
                                                                                                      StandardCharsets.ISO_8859_1));
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      new FileSystemCharStreamProvider (BASE_PATH, null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}

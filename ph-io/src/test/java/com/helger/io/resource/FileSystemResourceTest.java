/*
 * Copyright (C) 2015-2025 Philip Helger (www.helger.com)
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
package com.helger.io.resource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.junit.Ignore;
import org.junit.Test;

import com.helger.base.io.EAppend;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.system.EOperatingSystem;
import com.helger.io.file.FileOperations;
import com.helger.io.file.FilenameHelper;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link FileSystemResource}.
 *
 * @author Philip Helger
 */
public final class FileSystemResourceTest
{
  @Test
  public void testCtor ()
  {
    assertNotNull (new FileSystemResource ("file.txt"));
    assertNotNull (new FileSystemResource ("dir/file.text"));
    assertNotNull (new FileSystemResource ("/file.text"));
    assertNotNull (new FileSystemResource ("/dir/file2.txt"));
    assertNotNull (new FileSystemResource ("dir", "file2.txt"));
    assertNotNull (new FileSystemResource (new File ("file2.txt")));
    assertNotNull (new FileSystemResource (new File ("dir"), "file2.txt"));

    try
    {
      new FileSystemResource ((File) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  @Ignore ("See https://bugs.openjdk.org/browse/JDK-8285445")
  public void testUNC ()
  {
    if (EOperatingSystem.getCurrentOS ().isWindowsBased ())
    {
      // Use local UNC prefix for testing
      FileSystemResource aRes = new FileSystemResource (new File (FilenameHelper.WINDOWS_UNC_PREFIX_LOCAL1 +
                                                                  new File ("pom.xml").getAbsolutePath ()));
      assertTrue (aRes.exists ());
      assertTrue (aRes.getResourceID ().endsWith ("pom.xml"));
      assertTrue (aRes.getPath ().endsWith ("pom.xml"));

      aRes = new FileSystemResource (new File (FilenameHelper.WINDOWS_UNC_PREFIX_LOCAL2 +
                                               new File ("pom.xml").getAbsolutePath ()));
      assertTrue (aRes.exists ());
      assertTrue (aRes.getResourceID ().endsWith ("pom.xml"));
      assertTrue (aRes.getPath ().endsWith ("pom.xml"));
    }
  }

  @Test
  public void testAccess ()
  {
    FileSystemResource fr = new FileSystemResource ("pom.xml");
    assertTrue (fr.exists ());
    assertTrue (fr.getResourceID ().endsWith ("pom.xml"));
    assertTrue (fr.getPath ().endsWith ("pom.xml"));
    StreamHelper.close (fr.getReader (StandardCharsets.ISO_8859_1));
    final byte [] aBytes = StreamHelper.getAllBytes (fr);
    assertTrue (aBytes.length > 0);
    assertNotNull (fr.getAsURL ());
    assertNotNull (fr.getAsFile ());

    TestHelper.testDefaultImplementationWithEqualContentObject (fr, new FileSystemResource ("pom.xml"));
    TestHelper.testDefaultImplementationWithEqualContentObject (fr, fr.getReadableCloneForPath ("pom.xml"));
    TestHelper.testDefaultImplementationWithEqualContentObject (fr, fr.getWritableCloneForPath ("pom.xml"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (fr, new FileSystemResource ("pom.xml2"));

    fr = new FileSystemResource ("this file does not exist");
    assertFalse (fr.exists ());
    assertNull (fr.getInputStream ());
    assertNull (fr.getReader (StandardCharsets.ISO_8859_1));
  }

  @Test
  public void testWrite ()
  {
    final File f = new File ("restest.xxx");
    try
    {
      final FileSystemResource fr = new FileSystemResource (f);
      OutputStream aOS = fr.getOutputStream (EAppend.TRUNCATE);
      assertNotNull (aOS);
      StreamHelper.close (aOS);
      aOS = fr.getOutputStream (EAppend.APPEND);
      assertNotNull (aOS);
      StreamHelper.close (aOS);

      Writer w = fr.getWriter (StandardCharsets.ISO_8859_1, EAppend.TRUNCATE);
      assertNotNull (w);
      StreamHelper.close (w);
      w = fr.getWriter (StandardCharsets.ISO_8859_1, EAppend.APPEND);
      assertNotNull (w);
      StreamHelper.close (w);
    }
    finally
    {
      FileOperations.deleteFile (f);
    }
  }
}

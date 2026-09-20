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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.helger.base.io.EAppend;
import com.helger.base.io.stream.StreamHelper;

/**
 * Test class for class {@link FileChannelHelper}.
 *
 * @author Philip Helger
 */
public final class FileChannelHelperTest
{
  private static final String CONTENT = "Hello World - this is the content";
  private static final File FILE = new File ("target/junittest-filechannelhelper.txt");
  private static final File NON_EXISTING = new File ("target/junittest-filechannelhelper-does-not-exist.txt");

  @Before
  public void createFile ()
  {
    SimpleFileIO.writeFile (FILE, CONTENT, StandardCharsets.ISO_8859_1);
  }

  @After
  public void deleteFile ()
  {
    FileOperations.deleteFileIfExisting (FILE);
    FileOperations.deleteFileIfExisting (NON_EXISTING);
  }

  @Test
  public void testGetFileSize ()
  {
    assertEquals (-1, FileChannelHelper.getFileSize (null));

    final FileChannel aChannel = FileChannelHelper.getFileReadChannel (FILE);
    assertNotNull (aChannel);
    try
    {
      assertEquals (CONTENT.length (), FileChannelHelper.getFileSize (aChannel));
    }
    finally
    {
      StreamHelper.close (aChannel);
    }
    // A closed channel has no size
    assertEquals (-1, FileChannelHelper.getFileSize (aChannel));
  }

  @Test
  public void testGetFileReadChannel ()
  {
    final FileChannel aChannel = FileChannelHelper.getFileReadChannel (FILE.getPath ());
    assertNotNull (aChannel);
    StreamHelper.close (aChannel);

    assertNull (FileChannelHelper.getFileReadChannel (NON_EXISTING));
    assertNull (FileChannelHelper.getFileReadChannel (NON_EXISTING.getPath ()));

    try
    {
      FileChannelHelper.getFileReadChannel ((File) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testGetInputStream ()
  {
    // The file is way below 1 MB, so the regular stream is returned
    final InputStream aIS = FileChannelHelper.getInputStream (FILE);
    assertNotNull (aIS);
    assertEquals (CONTENT, StreamHelper.getAllBytesAsString (aIS, StandardCharsets.ISO_8859_1));

    assertNull (FileChannelHelper.getInputStream (NON_EXISTING));

    try
    {
      FileChannelHelper.getInputStream (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testGetMappedInputStream ()
  {
    final InputStream aIS = FileChannelHelper.getMappedInputStream (FILE);
    assertNotNull (aIS);
    assertEquals (CONTENT, StreamHelper.getAllBytesAsString (aIS, StandardCharsets.ISO_8859_1));

    assertNull (FileChannelHelper.getMappedInputStream (NON_EXISTING));
  }

  @Test
  public void testGetFileWriteChannel ()
  {
    for (final EAppend eAppend : EAppend.values ())
    {
      final FileChannel aChannel = FileChannelHelper.getFileWriteChannel (FILE, eAppend);
      assertNotNull (aChannel);
      StreamHelper.close (aChannel);

      final FileChannel aChannel2 = FileChannelHelper.getFileWriteChannel (FILE.getPath (), eAppend);
      assertNotNull (aChannel2);
      StreamHelper.close (aChannel2);
    }

    final FileChannel aDefault = FileChannelHelper.getFileWriteChannel (FILE);
    assertNotNull (aDefault);
    StreamHelper.close (aDefault);

    final FileChannel aDefault2 = FileChannelHelper.getFileWriteChannel (FILE.getPath ());
    assertNotNull (aDefault2);
    StreamHelper.close (aDefault2);

    try
    {
      FileChannelHelper.getFileWriteChannel ((File) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testGetMappedOutputStream ()
  {
    final OutputStream aOS = FileChannelHelper.getMappedOutputStream (FILE);
    assertNotNull (aOS);
    StreamHelper.close (aOS);

    final OutputStream aOS2 = FileChannelHelper.getMappedOutputStream (FILE.getPath ());
    assertNotNull (aOS2);
    StreamHelper.close (aOS2);

    for (final EAppend eAppend : EAppend.values ())
    {
      final OutputStream aOS3 = FileChannelHelper.getMappedOutputStream (FILE, eAppend);
      assertNotNull (aOS3);
      StreamHelper.close (aOS3);

      final OutputStream aOS4 = FileChannelHelper.getMappedOutputStream (FILE.getPath (), eAppend);
      assertNotNull (aOS4);
      StreamHelper.close (aOS4);
    }

    // A file whose parent is an existing file cannot be mapped
    assertNull (FileChannelHelper.getMappedOutputStream (new File (FILE, "child.txt")));

    try
    {
      FileChannelHelper.getMappedOutputStream ((File) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    assertTrue (FILE.exists ());
  }
}

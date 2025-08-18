/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.io.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.base.io.EAppend;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.io.stream.StringInputStream;
import com.helger.io.file.FileOperations;

/**
 * Test class for class {@link CountingFileOutputStream}.
 *
 * @author Philip Helger
 */
public final class CountingFileOutputStreamTest
{
  @Test
  public void testAll () throws IOException
  {
    final File f = new File ("CFOS.txt");
    try
    {
      try (final CountingFileOutputStream aCFOS = new CountingFileOutputStream (f))
      {
        assertEquals (0, aCFOS.getBytesWritten ());
        StreamHelper.copyInputStreamToOutputStream (new StringInputStream ("abc", StandardCharsets.ISO_8859_1), aCFOS);
        aCFOS.write ('a');
        aCFOS.write ("axy".getBytes (StandardCharsets.ISO_8859_1));
        assertEquals (7, aCFOS.getBytesWritten ());
      }
      try (final CountingFileOutputStream aCFOS = new CountingFileOutputStream (f, EAppend.APPEND))
      {
        aCFOS.write ("axy".getBytes (StandardCharsets.ISO_8859_1));
        assertEquals (3, aCFOS.getBytesWritten ());
      }
      try (final CountingFileOutputStream aCFOS = new CountingFileOutputStream (f.getAbsolutePath ()))
      {
        aCFOS.write ("axy".getBytes (StandardCharsets.ISO_8859_1));
        assertEquals (3, aCFOS.getBytesWritten ());
      }
      try (final CountingFileOutputStream aCFOS = new CountingFileOutputStream (f.getAbsolutePath (), EAppend.APPEND))
      {
        aCFOS.write ("axy".getBytes (StandardCharsets.ISO_8859_1));
        assertEquals (3, aCFOS.getBytesWritten ());
        assertNotNull (aCFOS.toString ());
      }
    }
    finally
    {
      FileOperations.deleteFile (f);
    }
  }
}

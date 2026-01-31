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
package com.helger.io.codec.base64;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.base.codec.base64.Base64;
import com.helger.io.file.FileHelper;
import com.helger.io.file.FileOperations;
import com.helger.io.file.SimpleFileIO;

/**
 * Test class for class {@link Base64File}.<br>
 * Base64 test code.<br>
 * Partly source: http://iharder.sourceforge.net/current/java/base64/Base64Test.java
 */
public final class Base64FileTest
{
  @Test
  public void testEncodeFileToFile () throws IOException
  {
    final File f1 = new File ("base64.decoded");
    final File f2 = new File ("base64.encoded");
    try
    {
      assertFalse (FileHelper.existsFile (f2));
      SimpleFileIO.writeFile (f1, "Hallo Wält", StandardCharsets.UTF_8);
      Base64File.encodeFileToFile (f1.getAbsolutePath (), f2.getAbsoluteFile ());
      assertTrue (FileHelper.existsFile (f2));
      final String sEncoded = SimpleFileIO.getFileAsString (f2, StandardCharsets.UTF_8);
      assertEquals ("Hallo Wält", Base64.safeDecodeAsString (sEncoded, StandardCharsets.UTF_8));
    }
    finally
    {
      FileOperations.deleteFile (f1);
      FileOperations.deleteFile (f2);
    }
  }

  @Test
  public void testEncodeToFile () throws IOException
  {
    final File f2 = new File ("base64.encoded");
    try
    {
      assertFalse (FileHelper.existsFile (f2));
      final String sDecoded = "Hallo Wält";
      Base64File.encodeToFile (sDecoded.getBytes (StandardCharsets.UTF_8), f2.getAbsoluteFile ());
      assertTrue (FileHelper.existsFile (f2));
      final String sEncoded = SimpleFileIO.getFileAsString (f2, StandardCharsets.UTF_8);
      assertEquals ("Hallo Wält", Base64.safeDecodeAsString (sEncoded, StandardCharsets.UTF_8));
    }
    finally
    {
      FileOperations.deleteFile (f2);
    }
  }

  @Test
  public void testDecodeFileToFile () throws IOException
  {
    final File f1 = new File ("base64.encoded");
    final File f2 = new File ("base64.decoded");
    try
    {
      assertFalse (FileHelper.existsFile (f2));
      SimpleFileIO.writeFile (f1,
                              Base64.safeEncode ("Hallo Wält", StandardCharsets.UTF_8)
                                    .getBytes (StandardCharsets.ISO_8859_1));
      Base64File.decodeFileToFile (f1.getAbsolutePath (), f2.getAbsoluteFile ());
      assertTrue (FileHelper.existsFile (f2));
      final String sDecoded = SimpleFileIO.getFileAsString (f2, StandardCharsets.UTF_8);
      assertEquals ("Hallo Wält", sDecoded);
    }
    finally
    {
      FileOperations.deleteFile (f1);
      FileOperations.deleteFile (f2);
    }
  }

  @Test
  public void testDecodeToFile () throws IOException
  {
    final File f2 = new File ("base64.decoded");
    try
    {
      assertFalse (FileHelper.existsFile (f2));
      final String sEncoded = Base64.safeEncode ("Hallo Wält", StandardCharsets.UTF_8);
      Base64File.decodeToFile (sEncoded, f2.getAbsoluteFile ());
      assertTrue (FileHelper.existsFile (f2));
      final String sDecoded = SimpleFileIO.getFileAsString (f2, StandardCharsets.UTF_8);
      assertEquals ("Hallo Wält", sDecoded);
    }
    finally
    {
      FileOperations.deleteFile (f2);
    }
  }
}

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
package com.helger.io.zip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jspecify.annotations.NonNull;
import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;

/**
 * Test class for class {@link LimitedZipInputStream}.
 *
 * @author Philip Helger
 */
public final class LimitedZipInputStreamTest
{
  private static byte @NonNull [] _zip (final Object @NonNull... aNamesAndContents) throws IOException
  {
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    try (final ZipOutputStream aZOS = new ZipOutputStream (aBAOS))
    {
      for (int i = 0; i < aNamesAndContents.length; i += 2)
      {
        aZOS.putNextEntry (new ZipEntry ((String) aNamesAndContents[i]));
        final Object aContent = aNamesAndContents[i + 1];
        if (aContent instanceof final String sContent)
          aZOS.write (sContent.getBytes (StandardCharsets.UTF_8));
        else
          aZOS.write ((byte []) aContent);
        aZOS.closeEntry ();
      }
    }
    return aBAOS.toByteArray ();
  }

  @NonNull
  private static LimitedZipInputStream _open (final byte @NonNull [] aZip, @NonNull final ZipWalkerSettings aSettings)
  {
    return new LimitedZipInputStream (new NonBlockingByteArrayInputStream (aZip), aSettings);
  }

  private static void _assertRefused (@NonNull final EZipWalkResult eExpected,
                                      final byte @NonNull [] aZip,
                                      @NonNull final ZipWalkerSettings aSettings,
                                      final boolean bReadEntries) throws IOException
  {
    try (final LimitedZipInputStream aZIS = _open (aZip, aSettings))
    {
      while (aZIS.getNextEntry () != null)
        if (bReadEntries)
          aZIS.readAllBytes ();
      fail ();
    }
    catch (final InvalidZipException ex)
    {
      assertEquals (ex.getMessage (), eExpected, ex.getReason ());
    }
  }

  @Test
  public void testBasic () throws IOException
  {
    try (final LimitedZipInputStream aZIS = _open (_zip ("a.txt", "a", "b.txt", "bb"), new ZipWalkerSettings ()))
    {
      ZipEntry aEntry = aZIS.getNextEntry ();
      assertNotNull (aEntry);
      assertEquals ("a.txt", aEntry.getName ());
      assertEquals ("a", new String (aZIS.readAllBytes (), StandardCharsets.UTF_8));
      aEntry = aZIS.getNextEntry ();
      assertNotNull (aEntry);
      assertEquals ("bb", new String (aZIS.readAllBytes (), StandardCharsets.UTF_8));
      assertNull (aZIS.getNextEntry ());
      assertEquals (2, aZIS.getEntryCount ());
      assertEquals (3, aZIS.getTotalUncompressedBytes ());
    }
  }

  @Test
  public void testEmptyArchive () throws IOException
  {
    try (final LimitedZipInputStream aZIS = _open (_zip (), new ZipWalkerSettings ()))
    {
      assertNull (aZIS.getNextEntry ());
    }
  }

  @Test
  public void testNotAZip () throws IOException
  {
    for (final String s : new String [] { "", "PK", "not a zip" })
      _assertRefused (EZipWalkResult.NOT_A_ZIP,
                      s.getBytes (StandardCharsets.ISO_8859_1),
                      new ZipWalkerSettings (),
                      false);
  }

  @Test
  public void testMaxEntryCount () throws IOException
  {
    final byte [] aZip = _zip ("a.txt", "a", "b.txt", "b");
    _assertRefused (EZipWalkResult.MAX_ENTRY_COUNT_EXCEEDED, aZip, new ZipWalkerSettings ().setMaxEntryCount (1), false);
    try (final LimitedZipInputStream aZIS = _open (aZip, new ZipWalkerSettings ().setMaxEntryCount (2)))
    {
      while (aZIS.getNextEntry () != null)
      {
        // Just iterate
      }
    }
  }

  @Test
  public void testMaxTotalUncompressedBytes () throws IOException
  {
    final byte [] aZip = _zip ("a.txt", "0123456789", "big.txt", new byte [100_000]);
    final ZipWalkerSettings aSettings = new ZipWalkerSettings ().setMaxTotalUncompressedBytes (100_009);

    // Read explicitly
    _assertRefused (EZipWalkResult.MAX_TOTAL_UNCOMPRESSED_BYTES_EXCEEDED, aZip, aSettings, true);
    // Not read - skipped by getNextEntry, which is counted as well
    _assertRefused (EZipWalkResult.MAX_TOTAL_UNCOMPRESSED_BYTES_EXCEEDED, aZip, aSettings, false);

    // Skipped explicitly
    try (final LimitedZipInputStream aZIS = _open (aZip, aSettings))
    {
      aZIS.getNextEntry ();
      aZIS.getNextEntry ();
      aZIS.skip (Long.MAX_VALUE);
      fail ();
    }
    catch (final InvalidZipException ex)
    {
      assertEquals (EZipWalkResult.MAX_TOTAL_UNCOMPRESSED_BYTES_EXCEEDED, ex.getReason ());
    }

    // Exactly the limit
    try (final LimitedZipInputStream aZIS = _open (aZip,
                                                   new ZipWalkerSettings ().setMaxTotalUncompressedBytes (100_010)))
    {
      while (aZIS.getNextEntry () != null)
      {
        // Just iterate
      }
      assertEquals (100_010, aZIS.getTotalUncompressedBytes ());
    }
  }
}

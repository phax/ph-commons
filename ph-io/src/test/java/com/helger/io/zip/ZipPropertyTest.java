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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jspecify.annotations.NonNull;
import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.state.EContinue;
import com.helger.unittest.property.Generators;
import com.helger.unittest.property.IGenerator;
import com.helger.unittest.property.PropertyAssume;
import com.helger.unittest.property.PropertyChecker;

/**
 * Property based tests for {@link ZipWalker} and {@link LimitedZipInputStream}. The archives are
 * generated from a model, so that the exact number of entries, bytes and nesting levels is known.
 *
 * @author Philip Helger
 */
public final class ZipPropertyTest
{
  /**
   * A generated archive with the facts the walker must find. Nested archives are entries whose name
   * ends with ".zip".
   */
  private static final class GeneratedZip
  {
    private final byte [] m_aBytes;
    /** All entries, including directories and the entries of nested archives */
    private final int m_nEntryCount;
    /** The sum of all file contents - only exact if there are no nested archives */
    private final long m_nContentBytes;
    /** The number of nested archive levels, 0 if there is no nested archive */
    private final int m_nNestingLevels;

    GeneratedZip (final byte @NonNull [] aBytes, final int nEntryCount, final long nContentBytes, final int nNestingLevels)
    {
      m_aBytes = aBytes;
      m_nEntryCount = nEntryCount;
      m_nContentBytes = nContentBytes;
      m_nNestingLevels = nNestingLevels;
    }

    @Override
    public String toString ()
    {
      return "zip with " +
             m_aBytes.length +
             " bytes, " +
             m_nEntryCount +
             " entries, " +
             m_nContentBytes +
             " content bytes, " +
             m_nNestingLevels +
             " nesting levels";
    }
  }

  /** The callback of the walk: descends into ".zip" entries and reads nothing itself */
  private static final IZipWalkerCallback CALLBACK = new IZipWalkerCallback ()
  {
    @Override
    public boolean isNestedArchive (@NonNull final String sPath, @NonNull final ZipEntry aEntry)
    {
      return sPath.endsWith (".zip");
    }

    @NonNull
    public EContinue onEntry (@NonNull final String sPath,
                              @NonNull final ZipEntry aEntry,
                              final int nDepth,
                              @NonNull final InputStream aIS)
    {
      return EContinue.CONTINUE;
    }
  };

  /** Name of an entry: 0 = file, 1 = directory, 2 = nested archive */
  private static final IGenerator <String> NAMES = Generators.strings ("ab/", 1, 6)
                                                             .filter (sName -> !sName.startsWith ("/") &&
                                                                               !sName.endsWith ("/") &&
                                                                               !sName.contains ("//"));

  @NonNull
  private static byte [] _zip (@NonNull final List <Object []> aEntries) throws IOException
  {
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    try (final ZipOutputStream aZOS = new ZipOutputStream (aBAOS))
    {
      int nIndex = 0;
      for (final Object [] aEntry : aEntries)
      {
        // A unique prefix, as duplicate names are not allowed
        final String sName = nIndex++ + "_" + aEntry[0];
        aZOS.putNextEntry (new ZipEntry (sName));
        if (aEntry[1] != null)
          aZOS.write ((byte []) aEntry[1]);
        aZOS.closeEntry ();
      }
    }
    return aBAOS.toByteArray ();
  }

  /**
   * Generate an archive with up to the passed number of nesting levels. Each entry is an Object[] of
   * name, content (null for directories), entry count, content bytes and nesting levels.
   */
  @NonNull
  private static IGenerator <GeneratedZip> _archive (final int nMaxNestingLevels)
  {
    final IGenerator <Object []> aFile = NAMES.flatMap (sName -> Generators.byteArrays (0, 200)
                                                                           .map (aContent -> new Object [] { sName,
                                                                                                             aContent,
                                                                                                             Integer.valueOf (1),
                                                                                                             Long.valueOf (aContent.length),
                                                                                                             Integer.valueOf (0) }));
    final IGenerator <Object []> aDir = NAMES.map (sName -> new Object [] { sName + "/",
                                                                            null,
                                                                            Integer.valueOf (1),
                                                                            Long.valueOf (0),
                                                                            Integer.valueOf (0) });
    final IGenerator <Object []> aEntry;
    if (nMaxNestingLevels == 0)
      aEntry = Generators.oneOf (aFile, aDir);
    else
    {
      final IGenerator <Object []> aNested = NAMES.flatMap (sName -> _archive (nMaxNestingLevels - 1).map (aZip -> new Object [] { sName +
                                                                                                                                  ".zip",
                                                                                                                                  aZip.m_aBytes,
                                                                                                                                  Integer.valueOf (1 +
                                                                                                                                                   aZip.m_nEntryCount),
                                                                                                                                  Long.valueOf (aZip.m_nContentBytes),
                                                                                                                                  Integer.valueOf (1 +
                                                                                                                                                   aZip.m_nNestingLevels) }));
      aEntry = Generators.oneOf (aFile, aDir, aNested);
    }
    return Generators.lists (aEntry, 0, 6).map (aEntries -> {
      int nEntryCount = 0;
      long nContentBytes = 0;
      int nNestingLevels = 0;
      for (final Object [] aEntryData : aEntries)
      {
        nEntryCount += ((Integer) aEntryData[2]).intValue ();
        nContentBytes += ((Long) aEntryData[3]).longValue ();
        nNestingLevels = Math.max (nNestingLevels, ((Integer) aEntryData[4]).intValue ());
      }
      try
      {
        return new GeneratedZip (_zip (aEntries), nEntryCount, nContentBytes, nNestingLevels);
      }
      catch (final IOException ex)
      {
        throw new IllegalStateException (ex);
      }
    });
  }

  private static final IGenerator <GeneratedZip> ARCHIVES = _archive (2);

  @NonNull
  private static ZipWalkResult _walk (final byte @NonNull [] aBytes, @NonNull final ZipWalkerSettings aSettings)
  {
    return ZipWalker.walk (new NonBlockingByteArrayInputStream (aBytes), aSettings, CALLBACK);
  }

  /**
   * Limits that equal the actual values must succeed, and the walk must report the actual values.
   */
  @Test
  public void testExactLimitsSucceed ()
  {
    new PropertyChecker ().forAll (ARCHIVES, aZip -> {
      // The entry count and byte limits must be > 0
      final ZipWalkerSettings aSettings = new ZipWalkerSettings ().setMaxNestingDepth (aZip.m_nNestingLevels);
      if (aZip.m_nEntryCount > 0)
        aSettings.setMaxEntryCount (aZip.m_nEntryCount);
      if (aZip.m_nNestingLevels == 0 && aZip.m_nContentBytes > 0)
        aSettings.setMaxTotalUncompressedBytes (aZip.m_nContentBytes);
      final ZipWalkResult aResult = _walk (aZip.m_aBytes, aSettings);
      assertEquals (aZip + ": " + aResult, EZipWalkResult.SUCCESS, aResult.getResult ());
      assertEquals (aZip.toString (), aZip.m_nEntryCount, aResult.getEntryCount ());
      if (aZip.m_nNestingLevels == 0)
        assertEquals (aZip.toString (), aZip.m_nContentBytes, aResult.getTotalUncompressedBytes ());
    });
  }

  /**
   * A limit one below the actual value must fail with the matching result.
   */
  @Test
  public void testLimitsOneBelowFail ()
  {
    new PropertyChecker ().forAll (ARCHIVES, aZip -> {
      if (aZip.m_nEntryCount > 1)
        assertEquals (aZip.toString (),
                      EZipWalkResult.MAX_ENTRY_COUNT_EXCEEDED,
                      _walk (aZip.m_aBytes, new ZipWalkerSettings ().setMaxEntryCount (aZip.m_nEntryCount - 1)).getResult ());
      if (aZip.m_nNestingLevels > 0)
        assertEquals (aZip.toString (),
                      EZipWalkResult.MAX_NESTING_DEPTH_EXCEEDED,
                      _walk (aZip.m_aBytes,
                             new ZipWalkerSettings ().setMaxNestingDepth (aZip.m_nNestingLevels - 1)).getResult ());
      if (aZip.m_nNestingLevels == 0 && aZip.m_nContentBytes > 1)
        assertEquals (aZip.toString (),
                      EZipWalkResult.MAX_TOTAL_UNCOMPRESSED_BYTES_EXCEEDED,
                      _walk (aZip.m_aBytes,
                             new ZipWalkerSettings ().setMaxTotalUncompressedBytes (aZip.m_nContentBytes - 1)).getResult ());
    });
  }

  /**
   * Broken archives (valid archives with random edits) never cause a runtime exception, and a
   * successful walk never exceeds the limits.
   */
  @Test
  public void testWalkBrokenArchives ()
  {
    final IGenerator <byte []> aMutated = Generators.mutatedByteArrays (ARCHIVES.map (aZip -> aZip.m_aBytes));
    new PropertyChecker ().setTries (1000).forAll (aMutated, aBytes -> {
      final ZipWalkerSettings aSettings = new ZipWalkerSettings ().setMaxEntryCount (50)
                                                                  .setMaxTotalUncompressedBytes (10_000)
                                                                  .setMaxNestingDepth (3);
      final ZipWalkResult aResult = _walk (aBytes, aSettings);
      assertTrue (aResult.toString (), aResult.getEntryCount () <= 50 + 1);
      if (aResult.isSuccess ())
      {
        assertTrue (aResult.toString (), aResult.getEntryCount () <= 50);
        assertTrue (aResult.toString (), aResult.getTotalUncompressedBytes () <= 10_000);
      }
    });
  }

  /**
   * Reading broken archives entry by entry with {@link LimitedZipInputStream} only throws
   * {@link IOException}s.
   */
  @Test
  public void testLimitedZipInputStreamBrokenArchives ()
  {
    final IGenerator <byte []> aMutated = Generators.mutatedByteArrays (ARCHIVES.map (aZip -> aZip.m_aBytes));
    new PropertyChecker ().setTries (1000).forAll (aMutated, aBytes -> {
      PropertyAssume.that (aBytes.length > 0);
      try (final LimitedZipInputStream aZIS = new LimitedZipInputStream (new NonBlockingByteArrayInputStream (aBytes)))
      {
        while (aZIS.getNextEntry () != null)
          aZIS.readAllBytes ();
      }
      catch (final IOException ex)
      {
        // expected for broken archives
      }
    });
  }
}

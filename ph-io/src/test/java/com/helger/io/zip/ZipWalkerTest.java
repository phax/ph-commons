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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jspecify.annotations.NonNull;
import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.state.EContinue;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

/**
 * Test class for class {@link ZipWalker}.
 *
 * @author Philip Helger
 */
public final class ZipWalkerTest
{
  /** Callback that collects the paths and depths, and descends into ".zip" entries */
  private static final class CollectingCallback implements IZipWalkerCallback
  {
    private final ICommonsList <String> m_aPaths = new CommonsArrayList <> ();
    private final ICommonsList <String> m_aDirs = new CommonsArrayList <> ();

    @Override
    @NonNull
    public EContinue onDirectoryEntry (@NonNull final String sPath, @NonNull final ZipEntry aEntry, final int nDepth)
    {
      m_aDirs.add (sPath + "@" + nDepth);
      return EContinue.CONTINUE;
    }

    @Override
    public boolean isNestedArchive (@NonNull final String sPath, @NonNull final ZipEntry aEntry)
    {
      return sPath.endsWith (".zip");
    }

    @NonNull
    public EContinue onEntry (@NonNull final String sPath,
                              @NonNull final ZipEntry aEntry,
                              final int nDepth,
                              @NonNull final InputStream aIS) throws IOException
    {
      m_aPaths.add (sPath + "@" + nDepth + "=" + new String (aIS.readAllBytes (), StandardCharsets.UTF_8));
      return EContinue.CONTINUE;
    }
  }

  /**
   * Create a ZIP archive.
   *
   * @param aNamesAndContents
   *        Alternating entry names and contents. A name ending with "/" is a directory and has no
   *        content.
   * @return The ZIP bytes
   */
  private static byte @NonNull [] _zip (final Object @NonNull... aNamesAndContents) throws IOException
  {
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    try (final ZipOutputStream aZOS = new ZipOutputStream (aBAOS))
    {
      int i = 0;
      while (i < aNamesAndContents.length)
      {
        final String sName = (String) aNamesAndContents[i++];
        aZOS.putNextEntry (new ZipEntry (sName));
        if (!sName.endsWith ("/"))
        {
          final Object aContent = aNamesAndContents[i++];
          if (aContent instanceof final String sContent)
            aZOS.write (sContent.getBytes (StandardCharsets.UTF_8));
          else
            aZOS.write ((byte []) aContent);
        }
        aZOS.closeEntry ();
      }
    }
    return aBAOS.toByteArray ();
  }

  @NonNull
  private static ZipWalkResult _walk (final byte @NonNull [] aBytes,
                                      @NonNull final ZipWalkerSettings aSettings,
                                      @NonNull final IZipWalkerCallback aCallback)
  {
    return ZipWalker.walk (new NonBlockingByteArrayInputStream (aBytes), aSettings, aCallback);
  }

  @Test
  public void testBasic () throws IOException
  {
    final CollectingCallback aCB = new CollectingCallback ();
    final ZipWalkResult aResult = _walk (_zip ("dir/", "dir/a.txt", "a", "b.txt", "bb"), new ZipWalkerSettings (), aCB);
    assertTrue (aResult.toString (), aResult.isSuccess ());
    assertEquals (EZipWalkResult.SUCCESS, aResult.getResult ());
    assertNull (aResult.getEntryPath ());
    assertNull (aResult.getException ());
    // The directory is counted and passed separately
    assertEquals (3, aResult.getEntryCount ());
    assertEquals (3, aResult.getTotalUncompressedBytes ());
    assertEquals (new CommonsArrayList <> ("dir/a.txt@0=a", "b.txt@0=bb"), aCB.m_aPaths);
    assertEquals (new CommonsArrayList <> ("dir/@0"), aCB.m_aDirs);
  }

  @Test
  public void testEmptyArchive () throws IOException
  {
    final ZipWalkResult aResult = _walk (_zip (), new ZipWalkerSettings (), new CollectingCallback ());
    assertTrue (aResult.toString (), aResult.isSuccess ());
    assertEquals (0, aResult.getEntryCount ());
  }

  @Test
  public void testNotAZip ()
  {
    for (final String s : new String [] { "", "P", "PK", "not a zip", "PK\u0001\u0002" })
    {
      final ZipWalkResult aResult = _walk (s.getBytes (StandardCharsets.ISO_8859_1),
                                           new ZipWalkerSettings (),
                                           new CollectingCallback ());
      assertEquals (s, EZipWalkResult.NOT_A_ZIP, aResult.getResult ());
      assertFalse (aResult.isSuccess ());
      assertNull (aResult.getEntryPath ());
    }
  }

  @Test
  public void testBroken () throws IOException
  {
    // Cut off in the middle of the compressed data
    final byte [] aZip = _zip ("a.txt", "Some content that is long enough to be cut. ".repeat (100));
    final byte [] aCut = new byte [aZip.length / 3];
    System.arraycopy (aZip, 0, aCut, 0, aCut.length);
    final ZipWalkResult aResult = _walk (aCut, new ZipWalkerSettings (), (p, e, d, is) -> EContinue.CONTINUE);
    assertEquals (aResult.toString (), EZipWalkResult.BROKEN, aResult.getResult ());
    assertEquals ("a.txt", aResult.getEntryPath ());
    assertNotNull (aResult.getException ());
  }

  @Test
  public void testNested () throws IOException
  {
    final byte [] aInner2 = _zip ("deep.txt", "d");
    final byte [] aInner = _zip ("x.txt", "x", "inner2.zip", aInner2);
    final CollectingCallback aCB = new CollectingCallback ();
    final ZipWalkResult aResult = _walk (_zip ("a.txt", "a", "inner.zip", aInner), new ZipWalkerSettings (), aCB);
    assertTrue (aResult.toString (), aResult.isSuccess ());
    assertEquals (new CommonsArrayList <> ("a.txt@0=a", "inner.zip/x.txt@1=x", "inner.zip/inner2.zip/deep.txt@2=d"),
                  aCB.m_aPaths);
    assertEquals (5, aResult.getEntryCount ());
    // Only the entries that are no archives
    assertEquals (3, aResult.getTotalUncompressedBytes ());

    // Without descending, the nested archive is a regular entry
    final ICommonsList <String> aNames = new CommonsArrayList <> ();
    assertTrue (_walk (_zip ("inner.zip", aInner), new ZipWalkerSettings (), (p, e, d, is) -> {
      aNames.add (p);
      return EContinue.CONTINUE;
    }).isSuccess ());
    assertEquals (new CommonsArrayList <> ("inner.zip"), aNames);
  }

  @Test
  public void testNestedDirectories () throws IOException
  {
    final byte [] aInner = _zip ("sub/", "sub/x.txt", "x");
    final CollectingCallback aCB = new CollectingCallback ();
    final ZipWalkResult aResult = _walk (_zip ("dir/", "inner.zip", aInner), new ZipWalkerSettings (), aCB);
    assertTrue (aResult.toString (), aResult.isSuccess ());
    assertEquals (new CommonsArrayList <> ("dir/@0", "inner.zip/sub/@1"), aCB.m_aDirs);
    assertEquals (new CommonsArrayList <> ("inner.zip/sub/x.txt@1=x"), aCB.m_aPaths);
  }

  @Test
  public void testStoppedByDirectoryCallback () throws IOException
  {
    final ICommonsList <String> aFiles = new CommonsArrayList <> ();
    final ZipWalkResult aResult = _walk (_zip ("a.txt", "a", "dir/", "dir/b.txt", "b"),
                                         new ZipWalkerSettings (),
                                         new IZipWalkerCallback ()
                                         {
                                           @Override
                                           @NonNull
                                           public EContinue onDirectoryEntry (@NonNull final String sPath,
                                                                              @NonNull final ZipEntry aEntry,
                                                                              final int nDepth)
                                           {
                                             return EContinue.BREAK;
                                           }

                                           @NonNull
                                           public EContinue onEntry (@NonNull final String sPath,
                                                                     @NonNull final ZipEntry aEntry,
                                                                     final int nDepth,
                                                                     @NonNull final InputStream aIS)
                                           {
                                             aFiles.add (sPath);
                                             return EContinue.CONTINUE;
                                           }
                                         });
    assertEquals (EZipWalkResult.STOPPED_BY_CALLBACK, aResult.getResult ());
    assertEquals ("dir/", aResult.getEntryPath ());
    assertEquals (new CommonsArrayList <> ("a.txt"), aFiles);
  }

  @Test
  public void testNestedNotAZip () throws IOException
  {
    final ZipWalkResult aResult = _walk (_zip ("inner.zip", "not a zip"),
                                         new ZipWalkerSettings (),
                                         new CollectingCallback ());
    assertEquals (EZipWalkResult.NOT_A_ZIP, aResult.getResult ());
    assertEquals ("inner.zip", aResult.getEntryPath ());
  }

  @Test
  public void testMaxNestingDepth () throws IOException
  {
    byte [] aZip = _zip ("a.txt", "a");
    for (int i = 0; i < 3; ++i)
      aZip = _zip ("level" + i + ".zip", aZip);

    // Exactly the maximum depth
    assertTrue (_walk (aZip, new ZipWalkerSettings ().setMaxNestingDepth (3), new CollectingCallback ()).isSuccess ());

    final ZipWalkResult aResult = _walk (aZip,
                                         new ZipWalkerSettings ().setMaxNestingDepth (2),
                                         new CollectingCallback ());
    assertEquals (EZipWalkResult.MAX_NESTING_DEPTH_EXCEEDED, aResult.getResult ());
    assertEquals ("level2.zip/level1.zip/level0.zip", aResult.getEntryPath ());

    // No nested archives at all
    assertEquals (EZipWalkResult.MAX_NESTING_DEPTH_EXCEEDED,
                  _walk (aZip, new ZipWalkerSettings ().setMaxNestingDepth (0), new CollectingCallback ()).getResult ());
  }

  @Test
  public void testMaxEntryCount () throws IOException
  {
    final byte [] aZip = _zip ("dir/", "a.txt", "a", "b.txt", "b");
    assertTrue (_walk (aZip, new ZipWalkerSettings ().setMaxEntryCount (3), new CollectingCallback ()).isSuccess ());

    final ZipWalkResult aResult = _walk (aZip, new ZipWalkerSettings ().setMaxEntryCount (2), new CollectingCallback ());
    assertEquals (EZipWalkResult.MAX_ENTRY_COUNT_EXCEEDED, aResult.getResult ());
    assertEquals ("b.txt", aResult.getEntryPath ());
  }

  @Test
  public void testMaxTotalUncompressedBytes () throws IOException
  {
    // Highly compressible
    final byte [] aZip = _zip ("a.txt", "0123456789", "big.txt", new byte [100_000]);
    assertTrue (_walk (aZip,
                       new ZipWalkerSettings ().setMaxTotalUncompressedBytes (100_010),
                       new CollectingCallback ()).isSuccess ());

    // The callback reads the entry
    ZipWalkResult aResult = _walk (aZip,
                                   new ZipWalkerSettings ().setMaxTotalUncompressedBytes (100_009),
                                   new CollectingCallback ());
    assertEquals (EZipWalkResult.MAX_TOTAL_UNCOMPRESSED_BYTES_EXCEEDED, aResult.getResult ());
    assertEquals ("big.txt", aResult.getEntryPath ());

    // The callback does not read the entry - it is counted nevertheless
    aResult = _walk (aZip,
                     new ZipWalkerSettings ().setMaxTotalUncompressedBytes (100_009),
                     (p, e, d, is) -> EContinue.CONTINUE);
    assertEquals (EZipWalkResult.MAX_TOTAL_UNCOMPRESSED_BYTES_EXCEEDED, aResult.getResult ());

    // The callback swallows the exception
    aResult = _walk (aZip, new ZipWalkerSettings ().setMaxTotalUncompressedBytes (100_009), (p, e, d, is) -> {
      try
      {
        is.readAllBytes ();
      }
      catch (final IOException ex)
      {
        // ignore
      }
      return EContinue.CONTINUE;
    });
    assertEquals (EZipWalkResult.MAX_TOTAL_UNCOMPRESSED_BYTES_EXCEEDED, aResult.getResult ());
    assertEquals ("big.txt", aResult.getEntryPath ());
  }

  @Test
  public void testStoppedByCallback () throws IOException
  {
    final ZipWalkResult aResult = _walk (_zip ("a.txt", "a", "b.txt", "b"),
                                         new ZipWalkerSettings (),
                                         (p, e, d, is) -> EContinue.valueOf (!p.equals ("a.txt")));
    assertEquals (EZipWalkResult.STOPPED_BY_CALLBACK, aResult.getResult ());
    assertEquals ("a.txt", aResult.getEntryPath ());
    assertEquals (1, aResult.getEntryCount ());
  }

  @Test
  public void testCallbackClosesStream () throws IOException
  {
    // Closing the entry stream must not close the archive
    final ICommonsList <byte []> aContents = new CommonsArrayList <> ();
    final ZipWalkResult aResult = _walk (_zip ("a.txt", "a", "b.txt", "b"), new ZipWalkerSettings (), (p, e, d, is) -> {
      try (final InputStream aIS = is)
      {
        aContents.add (aIS.readAllBytes ());
      }
      return EContinue.CONTINUE;
    });
    assertTrue (aResult.toString (), aResult.isSuccess ());
    assertEquals (2, aContents.size ());
    assertArrayEquals ("b".getBytes (StandardCharsets.UTF_8), aContents.get (1));
  }
}

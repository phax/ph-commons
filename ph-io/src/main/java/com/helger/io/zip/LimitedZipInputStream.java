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

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.nonblocking.NonBlockingPushbackInputStream;

/**
 * A {@link ZipInputStream} that applies the limits of {@link ZipWalkerSettings} while it is read
 * entry by entry ("pull" style), for readers that cannot use the callback based {@link ZipWalker}:
 * <ul>
 * <li>The content must start with a ZIP header - checked on the first call to
 * {@link #getNextEntry()}. A plain {@link ZipInputStream} would silently return no entries.</li>
 * <li>The number of entries (files and directories) is limited.</li>
 * <li>The total uncompressed size of all entries is limited. This includes the parts of entries
 * that are skipped, e.g. by {@link #closeEntry()} or the next {@link #getNextEntry()}.</li>
 * </ul>
 * A violation is reported with an {@link InvalidZipException}. The nesting depth of the settings
 * is not used, because this stream does not descend into nested archives.
 *
 * @author Philip Helger
 * @since 12.8.0
 */
@NotThreadSafe
public class LimitedZipInputStream extends ZipInputStream
{
  private final NonBlockingPushbackInputStream m_aPIS;
  private final ZipLimitCounter m_aCounter;
  private boolean m_bHeaderChecked;

  private LimitedZipInputStream (@NonNull final NonBlockingPushbackInputStream aPIS,
                                 @NonNull final ZipWalkerSettings aSettings)
  {
    super (aPIS);
    m_aPIS = aPIS;
    // Copy, so that later changes to the settings have no effect
    m_aCounter = new ZipLimitCounter (aSettings.getClone ());
  }

  /**
   * Constructor with the default {@link ZipWalkerSettings}.
   *
   * @param aIS
   *        The ZIP archive to read. May not be <code>null</code>.
   */
  public LimitedZipInputStream (@NonNull final InputStream aIS)
  {
    this (aIS, new ZipWalkerSettings ());
  }

  /**
   * Constructor.
   *
   * @param aIS
   *        The ZIP archive to read. May not be <code>null</code>.
   * @param aSettings
   *        The limits to apply. They are copied. May not be <code>null</code>.
   */
  public LimitedZipInputStream (@NonNull final InputStream aIS, @NonNull final ZipWalkerSettings aSettings)
  {
    this (new NonBlockingPushbackInputStream (ValueEnforcer.notNull (aIS, "InputStream"),
                                              ZipLimitCounter.ZIP_HEADER_LENGTH),
          ValueEnforcer.notNull (aSettings, "Settings"));
  }

  /**
   * {@inheritDoc}
   *
   * @throws InvalidZipException
   *         If the content is not a ZIP archive, or if the maximum number of entries or the maximum
   *         total uncompressed size is exceeded
   */
  @Override
  @Nullable
  public ZipEntry getNextEntry () throws IOException
  {
    if (!m_bHeaderChecked)
    {
      ZipLimitCounter.checkZipHeader (m_aPIS);
      m_bHeaderChecked = true;
    }

    // Skipping the rest of the previous entry is counted by read
    final ZipEntry ret = super.getNextEntry ();
    if (ret != null)
      m_aCounter.onEntry ();
    return ret;
  }

  /**
   * {@inheritDoc}
   *
   * @throws InvalidZipException
   *         If the maximum total uncompressed size is exceeded
   */
  @Override
  public int read (final byte [] aBuf, final int nOfs, final int nLen) throws IOException
  {
    // The single byte read, skip and closeEntry of ZipInputStream all end up here
    final int ret = super.read (aBuf, nOfs, nLen);
    m_aCounter.onBytes (ret);
    return ret;
  }

  /**
   * @return The limits applied. Never <code>null</code>.
   */
  @NonNull
  public final ZipWalkerSettings getSettings ()
  {
    return m_aCounter.getSettings ().getClone ();
  }

  /**
   * @return The number of entries read so far, including directories.
   */
  @Nonnegative
  public final int getEntryCount ()
  {
    return m_aCounter.getEntryCount ();
  }

  /**
   * @return The number of uncompressed bytes read so far, including skipped parts of entries.
   */
  @Nonnegative
  public final long getTotalUncompressedBytes ()
  {
    return m_aCounter.getTotalUncompressedBytes ();
  }
}

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

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.clone.ICloneable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;

/**
 * The limits applied by the {@link ZipWalker} while walking a ZIP archive. They protect against
 * ZIP bombs and similar archives that would exhaust memory, disk or stack.
 *
 * @author Philip Helger
 * @since 12.7.2
 */
@NotThreadSafe
public final class ZipWalkerSettings implements ICloneable <ZipWalkerSettings>
{
  /** The default maximum nesting depth of archives inside an archive. */
  public static final int DEFAULT_MAX_NESTING_DEPTH = 10;
  /** The default maximum number of entries, including all nested archives. */
  public static final int DEFAULT_MAX_ENTRY_COUNT = 100_000;
  /** The default maximum total uncompressed size in bytes (1 GiB), including all nested archives. */
  public static final long DEFAULT_MAX_TOTAL_UNCOMPRESSED_BYTES = 1024L * 1024 * 1024;

  private int m_nMaxNestingDepth = DEFAULT_MAX_NESTING_DEPTH;
  private int m_nMaxEntryCount = DEFAULT_MAX_ENTRY_COUNT;
  private long m_nMaxTotalUncompressedBytes = DEFAULT_MAX_TOTAL_UNCOMPRESSED_BYTES;

  /**
   * Default constructor with default settings.
   */
  public ZipWalkerSettings ()
  {}

  /**
   * Copy constructor.
   *
   * @param aOther
   *        The settings to copy from. May not be <code>null</code>.
   */
  public ZipWalkerSettings (@NonNull final ZipWalkerSettings aOther)
  {
    assignFrom (aOther);
  }

  /**
   * @return The maximum nesting depth of archives inside an archive. 0 means that nested archives
   *         are not allowed. Default is {@link #DEFAULT_MAX_NESTING_DEPTH}.
   */
  @Nonnegative
  public int getMaxNestingDepth ()
  {
    return m_nMaxNestingDepth;
  }

  /**
   * @param nMaxNestingDepth
   *        The maximum nesting depth of archives inside an archive. Must be &ge; 0. 0 means that
   *        nested archives are not allowed.
   * @return this for chaining
   */
  @NonNull
  public ZipWalkerSettings setMaxNestingDepth (@Nonnegative final int nMaxNestingDepth)
  {
    ValueEnforcer.isGE0 (nMaxNestingDepth, "MaxNestingDepth");
    m_nMaxNestingDepth = nMaxNestingDepth;
    return this;
  }

  /**
   * @return The maximum number of entries (files and directories), including all nested archives.
   *         Always &gt; 0. Default is {@link #DEFAULT_MAX_ENTRY_COUNT}.
   */
  @Nonnegative
  public int getMaxEntryCount ()
  {
    return m_nMaxEntryCount;
  }

  /**
   * @param nMaxEntryCount
   *        The maximum number of entries (files and directories), including all nested archives.
   *        Must be &gt; 0.
   * @return this for chaining
   */
  @NonNull
  public ZipWalkerSettings setMaxEntryCount (@Nonnegative final int nMaxEntryCount)
  {
    ValueEnforcer.isGT0 (nMaxEntryCount, "MaxEntryCount");
    m_nMaxEntryCount = nMaxEntryCount;
    return this;
  }

  /**
   * @return The maximum total uncompressed size in bytes of all entries that are not nested
   *         archives, including the entries of all nested archives. Always &gt; 0. Default is
   *         {@link #DEFAULT_MAX_TOTAL_UNCOMPRESSED_BYTES}.
   */
  @Nonnegative
  public long getMaxTotalUncompressedBytes ()
  {
    return m_nMaxTotalUncompressedBytes;
  }

  /**
   * @param nMaxTotalUncompressedBytes
   *        The maximum total uncompressed size in bytes of all entries that are not nested
   *        archives, including the entries of all nested archives. Must be &gt; 0.
   * @return this for chaining
   */
  @NonNull
  public ZipWalkerSettings setMaxTotalUncompressedBytes (@Nonnegative final long nMaxTotalUncompressedBytes)
  {
    ValueEnforcer.isGT0 (nMaxTotalUncompressedBytes, "MaxTotalUncompressedBytes");
    m_nMaxTotalUncompressedBytes = nMaxTotalUncompressedBytes;
    return this;
  }

  /**
   * Copy all values from the provided settings.
   *
   * @param aOther
   *        The settings to copy from. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public ZipWalkerSettings assignFrom (@NonNull final ZipWalkerSettings aOther)
  {
    ValueEnforcer.notNull (aOther, "Other");
    m_nMaxNestingDepth = aOther.m_nMaxNestingDepth;
    m_nMaxEntryCount = aOther.m_nMaxEntryCount;
    m_nMaxTotalUncompressedBytes = aOther.m_nMaxTotalUncompressedBytes;
    return this;
  }

  @NonNull
  @ReturnsMutableCopy
  public ZipWalkerSettings getClone ()
  {
    return new ZipWalkerSettings (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ZipWalkerSettings rhs = (ZipWalkerSettings) o;
    return m_nMaxNestingDepth == rhs.m_nMaxNestingDepth &&
           m_nMaxEntryCount == rhs.m_nMaxEntryCount &&
           m_nMaxTotalUncompressedBytes == rhs.m_nMaxTotalUncompressedBytes;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_nMaxNestingDepth)
                                       .append (m_nMaxEntryCount)
                                       .append (m_nMaxTotalUncompressedBytes)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("MaxNestingDepth", m_nMaxNestingDepth)
                                       .append ("MaxEntryCount", m_nMaxEntryCount)
                                       .append ("MaxTotalUncompressedBytes", m_nMaxTotalUncompressedBytes)
                                       .getToString ();
  }
}

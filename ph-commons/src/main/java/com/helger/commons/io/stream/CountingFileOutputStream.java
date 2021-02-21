/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.commons.io.stream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.io.EAppend;
import com.helger.commons.statistics.IMutableStatisticsHandlerCounter;
import com.helger.commons.statistics.IMutableStatisticsHandlerSize;
import com.helger.commons.statistics.StatisticsManager;
import com.helger.commons.string.ToStringGenerator;

/**
 * A special {@link FileOutputStream} sub class that keeps track of all written
 * bytes for the statistics handler.
 *
 * @author Philip Helger
 */
public class CountingFileOutputStream extends FileOutputStream
{
  /** By default append is enabled */
  public static final EAppend DEFAULT_APPEND = EAppend.DEFAULT;
  private static final IMutableStatisticsHandlerSize STATS_WRITE_SIZE = StatisticsManager.getSizeHandler (CountingFileOutputStream.class.getName () +
                                                                                                          "$write.bytes");
  private static final IMutableStatisticsHandlerCounter STATS_WRITE_FILES = StatisticsManager.getCounterHandler (CountingFileOutputStream.class.getName () +
                                                                                                                 "$write.files");
  private long m_nBytesWritten = 0;

  public CountingFileOutputStream (@Nonnull final File aFile) throws FileNotFoundException
  {
    this (aFile, DEFAULT_APPEND);
  }

  public CountingFileOutputStream (@Nonnull final File aFile, @Nonnull final EAppend eAppend) throws FileNotFoundException
  {
    super (aFile, eAppend.isAppend ());
    STATS_WRITE_FILES.increment ();
  }

  public CountingFileOutputStream (@Nonnull final String sFilename) throws FileNotFoundException
  {
    this (sFilename, DEFAULT_APPEND);
  }

  public CountingFileOutputStream (@Nonnull final String sFilename, @Nonnull final EAppend eAppend) throws FileNotFoundException
  {
    super (sFilename, eAppend.isAppend ());
    STATS_WRITE_FILES.increment ();
  }

  @Override
  public void write (final int b) throws IOException
  {
    super.write (b);
    STATS_WRITE_SIZE.addSize (1L);
    m_nBytesWritten++;
  }

  @Override
  public void write (final byte [] b) throws IOException
  {
    super.write (b);
    STATS_WRITE_SIZE.addSize (b.length);
    m_nBytesWritten += b.length;
  }

  @Override
  public void write (final byte [] b, final int nOffset, final int nLength) throws IOException
  {
    super.write (b, nOffset, nLength);
    STATS_WRITE_SIZE.addSize (nLength);
    m_nBytesWritten += nLength;
  }

  @Nonnegative
  public long getBytesWritten ()
  {
    return m_nBytesWritten;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("bytesWritten", m_nBytesWritten).getToString ();
  }
}

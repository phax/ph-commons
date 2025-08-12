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
package com.helger.commons.io.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.helger.commons.statistics.IMutableStatisticsHandlerCounter;
import com.helger.commons.statistics.IMutableStatisticsHandlerSize;
import com.helger.commons.statistics.StatisticsManager;

import jakarta.annotation.Nonnull;

/**
 * A special {@link FileInputStream} sub class that keeps track of all read
 * bytes for the statistics handler.
 *
 * @author Philip Helger
 */
public class CountingFileInputStream extends FileInputStream
{
  private static final IMutableStatisticsHandlerSize STATS_READ_SIZE = StatisticsManager.getSizeHandler (CountingFileInputStream.class.getName () +
                                                                                                        "$read.bytes");
  private static final IMutableStatisticsHandlerCounter STATS_READ_FILES = StatisticsManager.getCounterHandler (CountingFileInputStream.class.getName () +
                                                                                                               "$read.files");

  public CountingFileInputStream (@Nonnull final File aFile) throws FileNotFoundException
  {
    super (aFile);
    STATS_READ_FILES.increment ();
  }

  public CountingFileInputStream (@Nonnull final String sFilename) throws FileNotFoundException
  {
    super (sFilename);
    STATS_READ_FILES.increment ();
  }

  @Override
  public int read () throws IOException
  {
    final int ret = super.read ();
    if (ret != -1)
      STATS_READ_SIZE.addSize (1L);
    return ret;
  }

  @Override
  public int read (final byte [] b) throws IOException
  {
    final int ret = super.read (b);
    if (ret > -1)
      STATS_READ_SIZE.addSize (ret);
    return ret;
  }

  @Override
  public int read (final byte [] b, final int nOffset, final int nLength) throws IOException
  {
    final int ret = super.read (b, nOffset, nLength);
    if (ret > -1)
      STATS_READ_SIZE.addSize (ret);
    return ret;
  }
}

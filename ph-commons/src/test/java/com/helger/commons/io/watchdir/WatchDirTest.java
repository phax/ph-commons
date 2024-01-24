/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.commons.io.watchdir;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.concurrent.ThreadHelper;

/**
 * Test class for class {@link WatchDir}.
 *
 * @author Philip Helger
 */
public final class WatchDirTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (WatchDirTest.class);

  @Test
  // @Ignore ("Manually tested and considered to be working. Takes too long.")
  public void testRecursive () throws IOException
  {
    // register directory and process its events
    final Path aDir = Paths.get (".");
    final boolean bRecursive = true;

    final IWatchDirCallback aCB = (eAction, aPath) -> LOGGER.info ("CB: " + eAction + " - " + aPath);

    try (final WatchDir aWD = new WatchDir (aDir, bRecursive))
    {
      aWD.callbacks ().add (aCB);
      aWD.runAsync ();
      ThreadHelper.sleep (10, TimeUnit.SECONDS);
    }

    // Simplified
    try (final WatchDir d = WatchDir.createAsyncRunningWatchDir (aDir, bRecursive, aCB))
    {
      ThreadHelper.sleep (10, TimeUnit.SECONDS);
    }
  }

  @Test
  @Ignore ("Manually tested and considered to be working. Takes too long.")
  public void testNonRecursive () throws IOException
  {
    // Starting WatchDir
    final IWatchDirCallback aCB = (eAction, aPath) -> LOGGER.info ("CB: " + eAction + " - " + aPath);

    try (final WatchDir aWatch = WatchDir.createAsyncRunningWatchDir (Paths.get ("."), false, aCB))
    {
      ThreadHelper.sleep (10, TimeUnit.SECONDS);
    }
  }
}

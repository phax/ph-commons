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
package com.helger.io.watchdir;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.helger.io.file.PathOperations;
import com.helger.io.file.SimpleFileIO;

/**
 * Test class for the accessors and the event processing of {@link WatchDir}.
 *
 * @author Philip Helger
 */
public final class WatchDirAccessorsTest
{
  private static final Path BASE = Path.of ("target", "junittest-watchdir");

  @Before
  public void createBase ()
  {
    PathOperations.createDirRecursiveIfNotExisting (BASE);
  }

  @After
  public void deleteBase ()
  {
    PathOperations.deleteDirRecursiveIfExisting (BASE);
  }

  @Test
  public void testAccessors () throws IOException
  {
    try (final WatchDir aWD = new WatchDir (BASE, false))
    {
      assertEquals (BASE.toAbsolutePath (), aWD.getStartDirectory ());
      assertFalse (aWD.isRecursive ());
      assertFalse (aWD.isProcessing ());
      assertNotNull (aWD.callbacks ());
      assertTrue (aWD.callbacks ().isEmpty ());
      assertNotNull (aWD.toString ());
    }

    try (final WatchDir aWD = new WatchDir (BASE, true))
    {
      assertTrue (aWD.isRecursive ());
    }
  }

  @Test
  public void testProcessEventsWithoutCallback () throws IOException
  {
    try (final WatchDir aWD = new WatchDir (BASE, false))
    {
      // No callback registered
      aWD.processEvents ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }

  @Test
  public void testFileCreationIsReported () throws IOException, InterruptedException
  {
    final CountDownLatch aLatch = new CountDownLatch (1);
    final IWatchDirCallback aCB = (eAction, aPath) -> {
      assertNotNull (eAction);
      assertNotNull (aPath);
      aLatch.countDown ();
    };

    try (final WatchDir aWD = WatchDir.createAsyncRunningWatchDir (BASE, false, aCB))
    {
      assertEquals (1, aWD.callbacks ().size ());

      // Give the watcher a chance to start up
      for (int i = 0; i < 50 && !aWD.isProcessing (); ++i)
        Thread.sleep (100);
      assertTrue (aWD.isProcessing ());

      SimpleFileIO.writeFile (BASE.resolve ("created.txt").toFile (), "content", StandardCharsets.ISO_8859_1);

      // Wait at most 30 seconds for the event - the underlying WatchService is
      // polling based on some platforms
      assertTrue ("No directory change event was reported", aLatch.await (30, TimeUnit.SECONDS));

      aWD.stopProcessing ();
      assertFalse (aWD.isProcessing ());
    }
  }

  @SuppressWarnings ("resource")
  @Test
  public void testInvalidParams ()
  {
    try
    {
      new WatchDir (null, false);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException | IOException ex)
    {
      // expected
    }
  }

  @Test
  public void testRunAsyncAndReturn () throws IOException, InterruptedException
  {
    try (final WatchDir aWD = new WatchDir (BASE, false))
    {
      aWD.callbacks ().add ((eAction, aPath) -> { /* empty */ });
      final Thread aThread = aWD.runAsyncAndReturn ();
      assertNotNull (aThread);

      for (int i = 0; i < 50 && !aWD.isProcessing (); ++i)
        Thread.sleep (100);
      assertTrue (aWD.isProcessing ());

      aWD.stopProcessing ();
      aThread.join (10 * 1000);
      assertSame (Thread.State.TERMINATED, aThread.getState ());
    }
  }
}

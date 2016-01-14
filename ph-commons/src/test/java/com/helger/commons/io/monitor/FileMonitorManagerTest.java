/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.io.monitor;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.thread.ThreadHelper;

/**
 * Test class for class {@link FileMonitorManager}.
 *
 * @author Philip Helger
 */
public final class FileMonitorManagerTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (FileMonitorManagerTest.class);

  @Test
  public void testBasic ()
  {
    final IFileMonitorCallback aDeleteListener = new IFileMonitorCallback ()
    {
      @Override
      public void onFileDeleted (final FileChangeEvent event)
      {
        s_aLogger.info ("File deleted: " + event.getFile ().getAbsolutePath ());
      }
    };
    final IFileMonitorCallback aCreateListener = new IFileMonitorCallback ()
    {
      @Override
      public void onFileCreated (final FileChangeEvent event)
      {
        s_aLogger.info ("File created: " + event.getFile ().getAbsolutePath ());
      }
    };
    final IFileMonitorCallback aChangeListener = new IFileMonitorCallback ()
    {
      @Override
      public void onFileChanged (final FileChangeEvent event)
      {
        s_aLogger.info ("File changed: " + event.getFile ().getAbsolutePath ());
      }
    };
    final FileMonitorManager aMgr = new FileMonitorManager ();
    final boolean bRecursive = true;
    final File aMonitorFile = new File ("src/main");
    aMgr.createFileMonitor (aDeleteListener).setRecursive (bRecursive).addMonitoredFile (aMonitorFile);
    aMgr.createFileMonitor (aCreateListener).setRecursive (bRecursive).addMonitoredFile (aMonitorFile);
    aMgr.createFileMonitor (aChangeListener).setRecursive (bRecursive).addMonitoredFile (aMonitorFile);
    aMgr.start ();
    assertTrue (aMgr.isRunning ());
    ThreadHelper.sleepSeconds (5);
    aMgr.stop ();
  }
}

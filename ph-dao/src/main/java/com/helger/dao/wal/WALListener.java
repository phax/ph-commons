/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.dao.wal;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.UsedViaReflection;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.concurrent.BasicThreadFactory;
import com.helger.commons.concurrent.ExecutorServiceHelper;
import com.helger.commons.lang.TimeValue;
import com.helger.scope.IScope;
import com.helger.scope.singleton.AbstractGlobalSingleton;

/**
 * The global write ahead logging manager that schedules future writings of a
 * DAO.
 *
 * @author Philip Helger
 */
public final class WALListener extends AbstractGlobalSingleton
{
  /**
   * A single scheduled action consisting of the scheduled {@link Future} as
   * well as the original {@link Runnable} for rescheduling upon shutdown.
   *
   * @author Philip Helger
   */
  @Immutable
  private static final class WALItem
  {
    private final ScheduledFuture <?> m_aFuture;
    private final Runnable m_aRunnable;

    public WALItem (@Nonnull final ScheduledFuture <?> aFuture, @Nonnull final Runnable aRunnable)
    {
      m_aFuture = aFuture;
      m_aRunnable = aRunnable;
    }
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (WALListener.class);

  // custom ThreadFactory to give the baby a name
  private final ScheduledExecutorService m_aES = Executors.newSingleThreadScheduledExecutor (new BasicThreadFactory.Builder ().setNamingPattern ("WAL-Listener-%d")
                                                                                                                              .build ());
  @GuardedBy ("m_aRWLock")
  private final ICommonsSet <String> m_aWaitingDAOs = new CommonsHashSet <> ();
  @GuardedBy ("m_aRWLock")
  private final ICommonsMap <String, WALListener.WALItem> m_aScheduledItems = new CommonsHashMap <> ();

  @Deprecated
  @UsedViaReflection
  public WALListener ()
  {}

  @Nonnull
  public static WALListener getInstance ()
  {
    return getGlobalSingleton (WALListener.class);
  }

  @Override
  protected void onDestroy (@Nonnull final IScope aScopeInDestruction)
  {
    m_aRWLock.writeLocked ( () -> {
      // Reschedule all existing scheduled items to run now
      for (final Map.Entry <String, WALListener.WALItem> aEntry : m_aScheduledItems.entrySet ())
      {
        final WALListener.WALItem aItem = aEntry.getValue ();
        if (aItem.m_aFuture.cancel (false))
        {
          // reschedule to perform it now
          m_aES.submit (aItem.m_aRunnable);
          s_aLogger.info ("Rescheduled DAO writing for " + aEntry.getKey () + " to happen now");
        }
        else
          s_aLogger.info ("Cannot reschedule DAO writing for " + aEntry.getKey () + " because it is already running");
      }
      // ensure all are cleared
      m_aScheduledItems.clear ();
    });

    // Wait until all tasks finished
    ExecutorServiceHelper.shutdownAndWaitUntilAllTasksAreFinished (m_aES);
  }

  /**
   * This is the main method for registration of later writing.
   *
   * @param aDAO
   *        The DAO to be written
   * @param sWALFilename
   *        The filename of the WAL file for later deletion (in case the
   *        filename changes over time).
   * @param aWaitingWime
   *        The time to wait, until the file is physically written. May not be
   *        <code>null</code>.
   */
  public void registerForLaterWriting (@Nonnull final AbstractWALDAO <?> aDAO,
                                       @Nonnull final String sWALFilename,
                                       @Nonnull final TimeValue aWaitingWime)
  {
    // In case many DAOs of the same class exist, the filename is also added
    final String sKey = aDAO.getClass ().getName () + "::" + sWALFilename;

    // Check if the passed DAO is already scheduled for writing
    final boolean bDoScheduleForWriting = m_aRWLock.writeLocked ( () -> m_aWaitingDAOs.add (sKey));

    if (bDoScheduleForWriting)
    {
      // We need to schedule it now
      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug ("Now scheduling writing for DAO " + sKey);

      // What should be executed upon writing
      final Runnable r = () -> {
        // Use DAO lock!
        aDAO.internalWriteLocked ( () -> {
          // Main DAO writing
          aDAO._writeToFileAndResetPendingChanges ("ScheduledWriter.run");
          // Delete the WAL file
          aDAO._deleteWALFile (sWALFilename);

          if (s_aLogger.isDebugEnabled ())
            s_aLogger.debug ("Finished scheduled writing for DAO " + sKey);
        });

        // Remove from the internal set so that another job will be
        // scheduled for the same DAO
        // Do this after the writing to the file
        m_aRWLock.writeLocked ( () -> {
          // Remove from the overall set as well as from the scheduled items
          m_aWaitingDAOs.remove (sKey);
          m_aScheduledItems.remove (sKey);
        });
      };

      // Schedule exactly once in the specified waiting time
      final ScheduledFuture <?> aFuture = m_aES.schedule (r, aWaitingWime.getDuration (), aWaitingWime.getTimeUnit ());

      // Remember the scheduled item and the runnable so that the task can
      // be rescheduled upon shutdown.
      m_aRWLock.writeLocked ( () -> m_aScheduledItems.put (sKey, new WALItem (aFuture, r)));
    }
    // else the writing of the passed DAO is already scheduled and no further
    // action is necessary
  }
}

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
package com.helger.commons.deadlock;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Map;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.array.ArrayHelper;
import com.helger.base.callback.CallbackList;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

/**
 * This is the main dead lock detector, based on JMX {@link ThreadMXBean}
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class ThreadDeadlockDetector
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ThreadDeadlockDetector.class);

  private final ThreadMXBean m_aMBean = ManagementFactory.getThreadMXBean ();
  private final CallbackList <IThreadDeadlockCallback> m_aCallbacks = new CallbackList <> ();

  /**
   * This is the main method to be invoked to find deadlocked threads. In case a deadlock is found,
   * all registered callbacks are invoked.
   */
  public void findDeadlockedThreads ()
  {
    final long [] aThreadIDs = m_aMBean.isSynchronizerUsageSupported () ? m_aMBean.findDeadlockedThreads ()
                                                                        : m_aMBean.findMonitorDeadlockedThreads ();
    if (ArrayHelper.isNotEmpty (aThreadIDs))
    {
      // Get all stack traces
      final Map <Thread, StackTraceElement []> aAllStackTraces = Thread.getAllStackTraces ();

      // Sort by ID for a consistent result
      Arrays.sort (aThreadIDs);

      // Extract the relevant information
      final ICommonsList <ThreadDeadlockInfo> aThreadInfoList = new CommonsArrayList <> (aThreadIDs.length);
      for (final long nThreadID : aThreadIDs)
      {
        // ThreadInfo
        final ThreadInfo aThreadInfo = m_aMBean.getThreadInfo (nThreadID);
        if (aThreadInfo == null)
        {
          // The thread is no longer alive
          continue;
        }

        // Find matching thread and stack trace
        Thread aFoundThread = null;
        StackTraceElement [] aFoundStackTrace = null;
        // Sort ascending by thread ID for a consistent result
        for (final Map.Entry <Thread, StackTraceElement []> aEntry : aAllStackTraces.entrySet ())
          if (aEntry.getKey ().getId () == aThreadInfo.getThreadId ())
          {
            aFoundThread = aEntry.getKey ();
            aFoundStackTrace = aEntry.getValue ();
            break;
          }
        if (aFoundThread == null)
          throw new IllegalStateException ("Deadlocked Thread not found as defined by " + aThreadInfo.toString ());

        // Remember
        aThreadInfoList.add (new ThreadDeadlockInfo (aThreadInfo, aFoundThread, aFoundStackTrace));
      }
      if (aThreadInfoList.isEmpty ())
      {
        // All deadlocked threads are gone in the meantime
        return;
      }
      final ThreadDeadlockInfo [] aThreadInfos = aThreadInfoList.toArray (new ThreadDeadlockInfo [0]);

      // Invoke all callbacks
      if (m_aCallbacks.isEmpty ())
      {
        LOGGER.warn ("Found a deadlock of " + aThreadInfos.length + " threads but no callbacks are present!");
      }
      else
        m_aCallbacks.forEach (x -> x.onDeadlockDetected (aThreadInfos));
    }
  }

  /**
   * @return The mutable list of callbacks to be invoked when a deadlock is detected. Never
   *         <code>null</code>.
   */
  @NonNull
  @ReturnsMutableObject
  public CallbackList <IThreadDeadlockCallback> callbacks ()
  {
    return m_aCallbacks;
  }
}

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
package com.helger.commons.deadlock;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.callback.CallbackList;
import com.helger.commons.collection.ArrayHelper;

/**
 * This is the main dead lock detector, based on JMX {@link ThreadMXBean}
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class ThreadDeadlockDetector
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (ThreadDeadlockDetector.class);

  private final ThreadMXBean m_aMBean = ManagementFactory.getThreadMXBean ();
  private final CallbackList <IThreadDeadlockCallback> m_aCallbacks = new CallbackList <> ();

  /**
   * This is the main method to be invoked to find deadlocked threads. In case a
   * deadlock is found, all registered callbacks are invoked.
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
      final ThreadDeadlockInfo [] aThreadInfos = new ThreadDeadlockInfo [aThreadIDs.length];
      for (int i = 0; i < aThreadInfos.length; i++)
      {
        // ThreadInfo
        final ThreadInfo aThreadInfo = m_aMBean.getThreadInfo (aThreadIDs[i]);

        // Find matching thread and stack trace
        Thread aFoundThread = null;
        StackTraceElement [] aFoundStackTrace = null;
        // Sort ascending by thread ID for a consistent result
        for (final Map.Entry <Thread, StackTraceElement []> aEnrty : aAllStackTraces.entrySet ())
          if (aEnrty.getKey ().getId () == aThreadInfo.getThreadId ())
          {
            aFoundThread = aEnrty.getKey ();
            aFoundStackTrace = aEnrty.getValue ();
            break;
          }
        if (aFoundThread == null)
          throw new IllegalStateException ("Deadlocked Thread not found as defined by " + aThreadInfo.toString ());

        // Remember
        aThreadInfos[i] = new ThreadDeadlockInfo (aThreadInfo, aFoundThread, aFoundStackTrace);
      }

      // Invoke all callbacks
      if (m_aCallbacks.isEmpty ())
      {
        if (s_aLogger.isWarnEnabled ())
          s_aLogger.warn ("Found a deadlock of " + aThreadInfos.length + " threads but no callbacks are present!");
      }
      else
        m_aCallbacks.forEach (x -> x.onDeadlockDetected (aThreadInfos));
    }
  }

  @Nonnull
  @ReturnsMutableObject
  public CallbackList <IThreadDeadlockCallback> callbacks ()
  {
    return m_aCallbacks;
  }
}

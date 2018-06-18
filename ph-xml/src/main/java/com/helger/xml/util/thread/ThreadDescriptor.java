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
package com.helger.xml.util.thread;

import java.lang.Thread.State;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.lang.StackTraceHelper;
import com.helger.commons.string.StringHelper;
import com.helger.xml.microdom.IHasMicroNodeRepresentation;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;

/**
 * This class contains the information of a single thread at a certain point of
 * time.
 *
 * @author Philip Helger
 */
public class ThreadDescriptor implements IHasMicroNodeRepresentation
{
  public static final boolean DEFAULT_ENABLE_THREAD_INFO = false;

  private static final Logger s_aLogger = LoggerFactory.getLogger (ThreadDescriptor.class);
  private static final SimpleReadWriteLock s_aRWLock = new SimpleReadWriteLock ();
  private static final ThreadMXBean THREAD_MX = ManagementFactory.getThreadMXBean ();

  private static boolean s_bEnableThreadInfo = DEFAULT_ENABLE_THREAD_INFO;

  private final long m_nID;
  private final String m_sName;
  private final State m_eState;
  private final int m_nPriority;
  private final String m_sThreadGroup;
  private final String m_sStackTrace;
  private final ThreadInfo m_aThreadInfo;

  /**
   * Enable the retrieval of {@link ThreadInfo} objects. Warning: this takes a
   * lot of CPU, so enable this only when you are not running a performance
   * critical application! The default is {@value #DEFAULT_ENABLE_THREAD_INFO}.
   *
   * @param bEnableThreadInfo
   *        <code>true</code> to enabled, <code>false</code> to disable.
   */
  public static void setEnableThreadInfo (final boolean bEnableThreadInfo)
  {
    s_aRWLock.writeLocked ( () -> s_bEnableThreadInfo = bEnableThreadInfo);
  }

  public static boolean isEnableThreadInfo ()
  {
    return s_aRWLock.readLocked ( () -> s_bEnableThreadInfo);
  }

  public ThreadDescriptor (@Nonnull final Thread aThread, @Nullable final String sStackTrace)
  {
    ValueEnforcer.notNull (aThread, "Thread");

    m_nID = aThread.getId ();
    m_sName = aThread.getName ();
    m_eState = aThread.getState ();
    m_nPriority = aThread.getPriority ();
    final ThreadGroup aTG = aThread.getThreadGroup ();
    m_sThreadGroup = aTG != null ? aTG.getName () : "none - DIED!";
    m_sStackTrace = sStackTrace;
    ThreadInfo aThreadInfo = null;
    try
    {
      // This takes forever. Disabled as a performance improvement
      if (isEnableThreadInfo ())
        aThreadInfo = THREAD_MX.getThreadInfo (new long [] { m_nID }, true, true)[0];
    }
    catch (final Exception ex)
    {
      if (s_aLogger.isErrorEnabled ())
        s_aLogger.error ("Failed to get ThreadInfo for thread " + m_nID + ":", ex);
    }
    m_aThreadInfo = aThreadInfo;
  }

  public long getThreadID ()
  {
    return m_nID;
  }

  public State getThreadState ()
  {
    return m_eState;
  }

  @Nonnull
  @Nonempty
  public String getDescriptor ()
  {
    return "Thread[" + m_nID + "][" + m_sName + "][" + m_eState + "][" + m_nPriority + "][" + m_sThreadGroup + "]";
  }

  @Nullable
  public String getStackTrace ()
  {
    return m_sStackTrace;
  }

  @Nonnull
  @Nonempty
  public String getStackTraceNotNull ()
  {
    return StringHelper.hasText (m_sStackTrace) ? m_sStackTrace : "No stack trace available\n";
  }

  @Nonnull
  public String getLockInfo ()
  {
    final StringBuilder aSB = new StringBuilder ();
    if (m_aThreadInfo != null)
      try
      {
        final MonitorInfo [] aMonitorInfos = m_aThreadInfo.getLockedMonitors ();
        if (ArrayHelper.isNotEmpty (aMonitorInfos))
        {
          aSB.append ("Information on ").append (aMonitorInfos.length).append (" monitors:\n");
          for (final MonitorInfo aMonitorInfo : aMonitorInfos)
          {
            aSB.append ("  monitor: ")
               .append (aMonitorInfo.getClassName ())
               .append ('@')
               .append (Integer.toHexString (aMonitorInfo.getIdentityHashCode ()))
               .append (" at ")
               .append (aMonitorInfo.getLockedStackFrame ())
               .append (" [")
               .append ((aMonitorInfo.getLockedStackDepth ()))
               .append ("]\n");
          }
        }
        final LockInfo [] aSynchronizers = m_aThreadInfo.getLockedSynchronizers ();
        if (ArrayHelper.isNotEmpty (aSynchronizers))
        {
          aSB.append ("Information on ").append (aSynchronizers.length).append (" synchronizers:\n");
          for (final LockInfo aSynchronizer : aSynchronizers)
          {
            aSB.append ("  lock:")
               .append (aSynchronizer.getClassName ())
               .append ('@')
               .append (Integer.toHexString (aSynchronizer.getIdentityHashCode ()))
               .append ('\n');
          }
        }
      }
      catch (final Exception ex)
      {
        aSB.append ("Error retrieving infos: ").append (ex.toString ());
      }
    return aSB.toString ();
  }

  @Nonnull
  @Nonempty
  public String getAsString ()
  {
    final String sDescriptor = getDescriptor ();
    final String sStackTrace = getStackTraceNotNull ();
    final String sLockInfo = getLockInfo ();
    return sDescriptor + "\n" + sStackTrace + sLockInfo;
  }

  @Nonnull
  public IMicroElement getAsMicroNode ()
  {
    final IMicroElement eRet = new MicroElement ("thread");
    eRet.setAttribute ("id", m_nID);
    eRet.setAttribute ("name", m_sName);
    if (m_eState != null)
      eRet.setAttribute ("state", m_eState.toString ());
    eRet.setAttribute ("priority", m_nPriority);
    eRet.setAttribute ("threadgroup", m_sThreadGroup);
    eRet.appendElement ("stacktrace").appendText (getStackTraceNotNull ());
    if (m_aThreadInfo != null)
    {
      final IMicroElement eThreadInfo = eRet.appendElement ("threadinfo");
      try
      {
        final MonitorInfo [] aMonitorInfos = m_aThreadInfo.getLockedMonitors ();
        if (ArrayHelper.isNotEmpty (aMonitorInfos))
        {
          final IMicroElement eMonitorInfos = eThreadInfo.appendElement ("monitorinfos");
          eMonitorInfos.setAttribute ("count", aMonitorInfos.length);
          for (final MonitorInfo aMonitorInfo : aMonitorInfos)
          {
            final IMicroElement eMonitor = eMonitorInfos.appendElement ("monitor");
            eMonitor.setAttribute ("classname", aMonitorInfo.getClassName ());
            eMonitor.setAttribute ("identity", Integer.toHexString (aMonitorInfo.getIdentityHashCode ()));
            if (aMonitorInfo.getLockedStackFrame () != null)
              eMonitor.setAttribute ("stackframe", aMonitorInfo.getLockedStackFrame ().toString ());
            if (aMonitorInfo.getLockedStackDepth () >= 0)
              eMonitor.setAttribute ("stackdepth", aMonitorInfo.getLockedStackDepth ());
          }
        }

        final LockInfo [] aSynchronizers = m_aThreadInfo.getLockedSynchronizers ();
        if (ArrayHelper.isNotEmpty (aSynchronizers))
        {
          final IMicroElement eSynchronizers = eThreadInfo.appendElement ("synchronizers");
          eSynchronizers.setAttribute ("count", aSynchronizers.length);
          for (final LockInfo aSynchronizer : aSynchronizers)
          {
            final IMicroElement eSynchronizer = eSynchronizers.appendElement ("synchronizer");
            eSynchronizer.setAttribute ("classname", aSynchronizer.getClassName ());
            eSynchronizer.setAttribute ("identity", Integer.toHexString (aSynchronizer.getIdentityHashCode ()));
          }
        }
      }
      catch (final Exception ex)
      {
        eThreadInfo.setAttribute ("error", ex.getMessage ()).appendText (StackTraceHelper.getStackAsString (ex));
      }
    }
    return eRet;
  }

  @Nonnull
  public static ThreadDescriptor createForCurrentThread (@Nullable final Throwable t)
  {
    final String sThrowableStackTrace = StackTraceHelper.getStackAsString (t, false);
    return new ThreadDescriptor (Thread.currentThread (), sThrowableStackTrace);
  }
}

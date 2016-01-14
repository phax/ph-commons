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
package com.helger.commons.deadlock;

import java.lang.management.ThreadInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Represents information about a single deadlocked thread.
 *
 * @author Philip Helger
 */
@Immutable
public class ThreadDeadlockInfo
{
  private final ThreadInfo m_aThreadInfo;
  private final Thread m_aThread;
  private final StackTraceElement [] m_aStackTrace;

  public ThreadDeadlockInfo (@Nonnull final ThreadInfo aThreadInfo,
                             @Nonnull final Thread aThread,
                             @Nullable final StackTraceElement [] aStackTrace)
  {
    m_aThreadInfo = ValueEnforcer.notNull (aThreadInfo, "ThreadInfo");
    m_aThread = ValueEnforcer.notNull (aThread, "Thread");
    m_aStackTrace = ArrayHelper.getCopy (aStackTrace);
  }

  /**
   * @return The {@link ThreadInfo} as returned from JMX bean
   */
  @Nonnull
  public ThreadInfo getThreadInfo ()
  {
    return m_aThreadInfo;
  }

  /**
   * @return The underlying thread. Never <code>null</code>.
   */
  @Nonnull
  public Thread getThread ()
  {
    return m_aThread;
  }

  /**
   * @return <code>true</code> if a stack trace is present, <code>false</code>
   *         otherwise
   */
  public boolean hasStackTrace ()
  {
    return m_aStackTrace != null;
  }

  /**
   * @return The stack trace at the time the dead lock was found. May be
   *         <code>null</code> for certain system threads. Use
   *         <code>getThread ().getStackTrace ()</code> to retrieve the current
   *         stack trace.
   */
  @Nullable
  @ReturnsMutableCopy
  public StackTraceElement [] getStackTrace ()
  {
    return ArrayHelper.getCopy (m_aStackTrace);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("threadInfo", m_aThreadInfo)
                                       .append ("thread", m_aThread)
                                       .appendIfNotNull ("stackTrace", m_aStackTrace)
                                       .toString ();
  }
}

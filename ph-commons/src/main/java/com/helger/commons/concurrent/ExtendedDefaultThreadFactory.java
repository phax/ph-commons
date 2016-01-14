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
package com.helger.commons.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.lang.SecurityManagerHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Based on a DefaultThreadFactory somewhere in the Sun JDK but with the ability
 * to change the name of the thread slightly :)
 *
 * @author Philip Helger
 */
@ThreadSafe
public class ExtendedDefaultThreadFactory implements ThreadFactory
{
  private static final AtomicInteger s_aPoolNumber = new AtomicInteger (1);
  private final ThreadGroup m_aThreadGroup;
  private final AtomicInteger m_aThreadNumber = new AtomicInteger (1);
  private final String m_sNamePrefix;

  public ExtendedDefaultThreadFactory ()
  {
    this ("threadpool");
  }

  public ExtendedDefaultThreadFactory (@Nonnull @Nonempty final String sPoolPrefix)
  {
    m_aThreadGroup = SecurityManagerHelper.getThreadGroup ();
    m_sNamePrefix = sPoolPrefix + "[p" + s_aPoolNumber.getAndIncrement () + "-t";
  }

  @Nonnull
  public Thread newThread (@Nonnull final Runnable r)
  {
    final String sThreadName = m_sNamePrefix + m_aThreadNumber.getAndIncrement () + ']';
    final Thread aThread = new Thread (m_aThreadGroup, r, sThreadName, 0);
    if (aThread.isDaemon ())
      aThread.setDaemon (false);
    if (aThread.getPriority () != Thread.NORM_PRIORITY)
      aThread.setPriority (Thread.NORM_PRIORITY);
    return aThread;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("threadGroup", m_aThreadGroup)
                                       .append ("threadNumber", m_aThreadNumber)
                                       .append ("namePrefix", m_sNamePrefix)
                                       .toString ();
  }
}

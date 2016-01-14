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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Non reentrant lock. Copied from Netty 3.7 sources.
 *
 * @author Philip Helger
 * @since 5.6.1
 */
public final class NonReentrantLock extends AbstractQueuedSynchronizer implements Lock
{
  private Thread m_aOwner;

  public void lock ()
  {
    acquire (1);
  }

  public void lockInterruptibly () throws InterruptedException
  {
    acquireInterruptibly (1);
  }

  public boolean tryLock ()
  {
    return tryAcquire (1);
  }

  public boolean tryLock (final long time, final TimeUnit unit) throws InterruptedException
  {
    return tryAcquireNanos (1, unit.toNanos (time));
  }

  public void unlock ()
  {
    release (1);
  }

  public boolean isHeldByCurrentThread ()
  {
    return isHeldExclusively ();
  }

  public Condition newCondition ()
  {
    return new ConditionObject ();
  }

  @Override
  protected boolean tryAcquire (final int acquires)
  {
    if (compareAndSetState (0, 1))
    {
      m_aOwner = Thread.currentThread ();
      return true;
    }
    return false;
  }

  @Override
  protected boolean tryRelease (final int releases)
  {
    if (Thread.currentThread () != m_aOwner)
      throw new IllegalMonitorStateException ();
    m_aOwner = null;
    setState (0);
    return true;
  }

  @Override
  protected boolean isHeldExclusively ()
  {
    return getState () != 0 && m_aOwner == Thread.currentThread ();
  }
}

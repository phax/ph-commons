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
package com.helger.base.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.jspecify.annotations.NonNull;

/**
 * Non reentrant lock. Copied from Netty 3.7 sources.
 *
 * @author Philip Helger
 * @since 5.6.1
 */
public class NonReentrantLock extends AbstractQueuedSynchronizer implements Lock
{
  private transient Thread m_aOwner;

  /**
   * Default constructor.
   */
  public NonReentrantLock ()
  {}

  /** {@inheritDoc} */
  public void lock ()
  {
    acquire (1);
  }

  /** {@inheritDoc} */
  public void lockInterruptibly () throws InterruptedException
  {
    acquireInterruptibly (1);
  }

  /** {@inheritDoc} */
  public boolean tryLock ()
  {
    return tryAcquire (1);
  }

  /** {@inheritDoc} */
  public boolean tryLock (final long nTime, final TimeUnit eUnit) throws InterruptedException
  {
    return tryAcquireNanos (1, eUnit.toNanos (nTime));
  }

  /** {@inheritDoc} */
  public void unlock ()
  {
    release (1);
  }

  /**
   * @return <code>true</code> if the current thread holds this lock,
   *         <code>false</code> otherwise.
   */
  public boolean isHeldByCurrentThread ()
  {
    return isHeldExclusively ();
  }

  /** {@inheritDoc} */
  @NonNull
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

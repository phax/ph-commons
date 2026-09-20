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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/**
 * Test class for class {@link SimpleLock}, {@link SimpleReadWriteLock} and
 * {@link NonReentrantLock}.
 *
 * @author Philip Helger
 */
public final class SimpleLocksTest
{
  @Test
  public void testSimpleLockRunnableAndSupplier ()
  {
    final SimpleLock a = new SimpleLock ();
    final AtomicInteger aCount = new AtomicInteger (0);

    a.locked (aCount::incrementAndGet);
    assertEquals (1, aCount.get ());
    assertFalse (a.isLocked ());

    assertEquals ("v", a.lockedGet (() -> "v"));
    assertTrue (a.lockedBoolean (() -> true));
    assertEquals (1.5, a.lockedDouble (() -> 1.5), 0.0001);
    assertEquals (3, a.lockedInt (() -> 3));
    assertEquals (4L, a.lockedLong (() -> 4L));
    assertFalse (a.isLocked ());
  }

  @Test
  public void testSimpleLockThrowing () throws IOException
  {
    final SimpleLock a = new SimpleLock (true);
    assertTrue (a.isFair ());

    a.lockedThrowing (() -> { /* empty */ });
    assertEquals ("v", a.lockedGetThrowing (() -> "v"));

    // The lock is released even if the action throws
    try
    {
      a.lockedThrowing (() -> { throw new IOException ("mock"); });
      fail ();
    }
    catch (final IOException ex)
    {
      // expected
    }
    assertFalse (a.isLocked ());

    try
    {
      a.lockedGetThrowing (() -> { throw new IOException ("mock"); });
      fail ();
    }
    catch (final IOException ex)
    {
      // expected
    }
    assertFalse (a.isLocked ());
  }

  @Test
  public void testReadWriteLockRead () throws IOException
  {
    final SimpleReadWriteLock a = new SimpleReadWriteLock ();
    final AtomicInteger aCount = new AtomicInteger (0);

    a.readLocked (aCount::incrementAndGet);
    assertEquals (1, aCount.get ());

    assertEquals ("v", a.readLockedGet (() -> "v"));
    assertTrue (a.readLockedBoolean (() -> true));
    assertEquals (1.5, a.readLockedDouble (() -> 1.5), 0.0001);
    assertEquals (3, a.readLockedInt (() -> 3));
    assertEquals (4L, a.readLockedLong (() -> 4L));

    a.readLockedThrowing (() -> { /* empty */ });
    assertEquals ("v", a.readLockedGetThrowing (() -> "v"));

    try
    {
      a.readLockedThrowing (() -> { throw new IOException ("mock"); });
      fail ();
    }
    catch (final IOException ex)
    {
      // expected
    }
    try
    {
      a.readLockedGetThrowing (() -> { throw new IOException ("mock"); });
      fail ();
    }
    catch (final IOException ex)
    {
      // expected
    }
  }

  @Test
  public void testReadWriteLockWrite () throws IOException
  {
    final SimpleReadWriteLock a = new SimpleReadWriteLock (true);
    assertTrue (a.isFair ());

    final AtomicInteger aCount = new AtomicInteger (0);
    a.writeLocked (aCount::incrementAndGet);
    assertEquals (1, aCount.get ());

    assertEquals ("v", a.writeLockedGet (() -> "v"));
    assertTrue (a.writeLockedBoolean (() -> true));
    assertEquals (1.5, a.writeLockedDouble (() -> 1.5), 0.0001);
    assertEquals (3, a.writeLockedInt (() -> 3));
    assertEquals (4L, a.writeLockedLong (() -> 4L));

    a.writeLockedThrowing (() -> { /* empty */ });
    assertEquals ("v", a.writeLockedGetThrowing (() -> "v"));

    try
    {
      a.writeLockedThrowing (() -> { throw new IOException ("mock"); });
      fail ();
    }
    catch (final IOException ex)
    {
      // expected
    }
    assertFalse (a.isWriteLocked ());

    try
    {
      a.writeLockedGetThrowing (() -> { throw new IOException ("mock"); });
      fail ();
    }
    catch (final IOException ex)
    {
      // expected
    }
    assertFalse (a.isWriteLocked ());
  }

  @Test
  public void testNonReentrantLock () throws InterruptedException
  {
    final NonReentrantLock a = new NonReentrantLock ();
    assertFalse (a.isHeldByCurrentThread ());

    a.lock ();
    assertTrue (a.isHeldByCurrentThread ());
    a.unlock ();
    assertFalse (a.isHeldByCurrentThread ());

    assertTrue (a.tryLock ());
    assertTrue (a.isHeldByCurrentThread ());
    a.unlock ();

    assertTrue (a.tryLock (1, TimeUnit.SECONDS));
    a.unlock ();

    a.lockInterruptibly ();
    a.unlock ();

    assertNotNull (a.newCondition ());
  }

  @Test
  public void testNonReentrantLockIsNotReentrant () throws InterruptedException
  {
    final NonReentrantLock a = new NonReentrantLock ();
    a.lock ();
    try
    {
      // A second lock from the same thread must not succeed immediately
      assertFalse (a.tryLock ());
    }
    finally
    {
      a.unlock ();
    }
  }
}

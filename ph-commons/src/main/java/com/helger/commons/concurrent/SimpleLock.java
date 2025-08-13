/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import com.helger.commons.callback.IThrowingRunnable;
import com.helger.commons.functional.IThrowingSupplier;
import com.helger.commons.valueenforcer.ValueEnforcer;

import jakarta.annotation.Nonnull;

/**
 * This is an extension around {@link ReentrantLock} that allows for easy usage
 * with Java 8 :) See {@link #locked(Runnable)} and {@link #locked(Runnable)}
 * method. Also methods for suppliers are available.
 *
 * @author Philip Helger
 */
public class SimpleLock extends ReentrantLock
{
  /**
   * Default constructor creating a default {@link ReentrantLock}
   */
  public SimpleLock ()
  {}

  /**
   * Constructor creating a {@link ReentrantLock} with the provided fairness
   *
   * @param bFair
   *        <code>true</code> if this lock should use a fair ordering policy
   */
  public SimpleLock (final boolean bFair)
  {
    super (bFair);
  }

  /**
   * Execute the provided runnable in a read lock.
   *
   * @param aRunnable
   *        Runnable to be executed. May not be <code>null</code>.
   */
  public void locked (@Nonnull final Runnable aRunnable)
  {
    ValueEnforcer.notNull (aRunnable, "Runnable");

    lock ();
    try
    {
      aRunnable.run ();
    }
    finally
    {
      unlock ();
    }
  }

  /**
   * Execute the provided runnable in a read lock.
   *
   * @param aRunnable
   *        Runnable to be executed. May not be <code>null</code>.
   * @throws EXTYPE
   *         If the callable throws the exception
   * @param <EXTYPE>
   *        Exception type to be thrown
   */
  public <EXTYPE extends Exception> void lockedThrowing (@Nonnull final IThrowingRunnable <EXTYPE> aRunnable) throws EXTYPE
  {
    ValueEnforcer.notNull (aRunnable, "Runnable");

    lock ();
    try
    {
      aRunnable.run ();
    }
    finally
    {
      unlock ();
    }
  }

  /**
   * Execute the provided callable in a read lock. Note: no
   * nullable/non-nullable can be assumed.
   *
   * @param aSupplier
   *        Callable to be executed. May not be <code>null</code>.
   * @return The return value of the callable. May be <code>null</code>.
   * @param <T>
   *        Return type
   */
  public <T> T lockedGet (@Nonnull final Supplier <? extends T> aSupplier)
  {
    ValueEnforcer.notNull (aSupplier, "Supplier");

    lock ();
    try
    {
      return aSupplier.get ();
    }
    finally
    {
      unlock ();
    }
  }

  /**
   * Execute the provided callable in a read lock. Note: no
   * nullable/non-nullable can be assumed.
   *
   * @param aCallable
   *        Callable to be executed. May not be <code>null</code>.
   * @return The return value of the callable. May be <code>null</code>.
   * @throws EXTYPE
   *         If the callable throws the exception
   * @param <T>
   *        Return type
   * @param <EXTYPE>
   *        Exception type to be thrown
   */
  public <T, EXTYPE extends Exception> T lockedGetThrowing (@Nonnull final IThrowingSupplier <? extends T, EXTYPE> aCallable) throws EXTYPE
  {
    ValueEnforcer.notNull (aCallable, "Callable");

    lock ();
    try
    {
      return aCallable.get ();
    }
    finally
    {
      unlock ();
    }
  }

  /**
   * Execute the provided callable in a read lock.
   *
   * @param aSupplier
   *        Callable to be executed. May not be <code>null</code>.
   * @return The return value of the callable. May be <code>null</code>.
   */
  public boolean lockedBoolean (@Nonnull final BooleanSupplier aSupplier)
  {
    ValueEnforcer.notNull (aSupplier, "Supplier");

    lock ();
    try
    {
      return aSupplier.getAsBoolean ();
    }
    finally
    {
      unlock ();
    }
  }

  /**
   * Execute the provided callable in a read lock.
   *
   * @param aSupplier
   *        Callable to be executed. May not be <code>null</code>.
   * @return The return value of the callable. May be <code>null</code>.
   */
  public double lockedDouble (@Nonnull final DoubleSupplier aSupplier)
  {
    ValueEnforcer.notNull (aSupplier, "Supplier");

    lock ();
    try
    {
      return aSupplier.getAsDouble ();
    }
    finally
    {
      unlock ();
    }
  }

  /**
   * Execute the provided callable in a read lock.
   *
   * @param aSupplier
   *        Callable to be executed. May not be <code>null</code>.
   * @return The return value of the callable. May be <code>null</code>.
   */
  public int lockedInt (@Nonnull final IntSupplier aSupplier)
  {
    ValueEnforcer.notNull (aSupplier, "Supplier");

    lock ();
    try
    {
      return aSupplier.getAsInt ();
    }
    finally
    {
      unlock ();
    }
  }

  /**
   * Execute the provided callable in a read lock.
   *
   * @param aSupplier
   *        Callable to be executed. May not be <code>null</code>.
   * @return The return value of the callable. May be <code>null</code>.
   */
  public long lockedLong (@Nonnull final LongSupplier aSupplier)
  {
    ValueEnforcer.notNull (aSupplier, "Supplier");

    lock ();
    try
    {
      return aSupplier.getAsLong ();
    }
    finally
    {
      unlock ();
    }
  }
}

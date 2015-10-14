/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.callback.IThrowingCallable;
import com.helger.commons.callback.IThrowingRunnable;

/**
 * This is an extension around {@link ReentrantReadWriteLock} that allows for
 * easy usage with Java 8 :) See {@link #readLocked(Runnable)} and
 * {@link #writeLocked(Runnable)} method. For throwing versions see
 * {@link #readLockedThrowing(IThrowingRunnable)} and
 * {@link #writeLockedThrowing(IThrowingRunnable)}. Also methods for callables
 * are available.
 *
 * @author Philip Helger
 */
public class SimpleReadWriteLock implements ReadWriteLock, Serializable
{
  private final ReentrantReadWriteLock m_aRWLock;

  /**
   * Default constructor creating a default {@link ReentrantReadWriteLock}
   */
  public SimpleReadWriteLock ()
  {
    m_aRWLock = new ReentrantReadWriteLock ();
  }

  /**
   * Constructor creating a {@link ReentrantReadWriteLock} with the provided
   * fairness
   *
   * @param bFair
   *        <code>true</code> if this lock should use a fair ordering policy
   */
  public SimpleReadWriteLock (final boolean bFair)
  {
    m_aRWLock = new ReentrantReadWriteLock (bFair);
  }

  @Nonnull
  public Lock readLock ()
  {
    return m_aRWLock.readLock ();
  }

  @Nonnull
  public Lock writeLock ()
  {
    return m_aRWLock.writeLock ();
  }

  /**
   * Execute the provided runnable in a read lock.
   *
   * @param aRunnable
   *        Runnable to be executed. May not be <code>null</code>.
   */
  public void readLocked (@Nonnull final Runnable aRunnable)
  {
    ValueEnforcer.notNull (aRunnable, "Runnable");

    m_aRWLock.readLock ().lock ();
    try
    {
      aRunnable.run ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
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
  public <EXTYPE extends Exception> void readLockedThrowing (@Nonnull final IThrowingRunnable <EXTYPE> aRunnable) throws EXTYPE
  {
    ValueEnforcer.notNull (aRunnable, "Runnable");

    m_aRWLock.readLock ().lock ();
    try
    {
      aRunnable.run ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * Execute the provided callable in a read lock.
   *
   * @param aSupplier
   *        Callable to be executed. May not be <code>null</code>.
   * @return The return value of the callable. May be <code>null</code>.
   * @param <T>
   *        Return type
   */
  @Nullable
  public <T> T readLocked (@Nonnull final Supplier <T> aSupplier)
  {
    ValueEnforcer.notNull (aSupplier, "Supplier");

    m_aRWLock.readLock ().lock ();
    try
    {
      return aSupplier.get ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * Execute the provided callable in a read lock.
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
  @Nullable
  public <T, EXTYPE extends Exception> T readLockedThrowing (@Nonnull final IThrowingCallable <T, EXTYPE> aCallable) throws EXTYPE
  {
    ValueEnforcer.notNull (aCallable, "Callable");

    m_aRWLock.readLock ().lock ();
    try
    {
      return aCallable.call ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * Execute the provided callable in a read lock.
   *
   * @param aSupplier
   *        Callable to be executed. May not be <code>null</code>.
   * @return The return value of the callable. May be <code>null</code>.
   */
  public boolean readLocked (@Nonnull final BooleanSupplier aSupplier)
  {
    ValueEnforcer.notNull (aSupplier, "Supplier");

    m_aRWLock.readLock ().lock ();
    try
    {
      return aSupplier.getAsBoolean ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * Execute the provided callable in a read lock.
   *
   * @param aSupplier
   *        Callable to be executed. May not be <code>null</code>.
   * @return The return value of the callable. May be <code>null</code>.
   */
  public double readLocked (@Nonnull final DoubleSupplier aSupplier)
  {
    ValueEnforcer.notNull (aSupplier, "Supplier");

    m_aRWLock.readLock ().lock ();
    try
    {
      return aSupplier.getAsDouble ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * Execute the provided callable in a read lock.
   *
   * @param aSupplier
   *        Callable to be executed. May not be <code>null</code>.
   * @return The return value of the callable. May be <code>null</code>.
   */
  public int readLocked (@Nonnull final IntSupplier aSupplier)
  {
    ValueEnforcer.notNull (aSupplier, "Supplier");

    m_aRWLock.readLock ().lock ();
    try
    {
      return aSupplier.getAsInt ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * Execute the provided callable in a read lock.
   *
   * @param aSupplier
   *        Callable to be executed. May not be <code>null</code>.
   * @return The return value of the callable. May be <code>null</code>.
   */
  public long readLocked (@Nonnull final LongSupplier aSupplier)
  {
    ValueEnforcer.notNull (aSupplier, "Supplier");

    m_aRWLock.readLock ().lock ();
    try
    {
      return aSupplier.getAsLong ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * Execute the provided runnable in a write lock.
   *
   * @param aRunnable
   *        Runnable to be executed. May not be <code>null</code>.
   */
  public void writeLocked (@Nonnull final Runnable aRunnable)
  {
    ValueEnforcer.notNull (aRunnable, "Runnable");

    m_aRWLock.writeLock ().lock ();
    try
    {
      aRunnable.run ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * Execute the provided runnable in a write lock.
   *
   * @param aRunnable
   *        Runnable to be executed. May not be <code>null</code>.
   * @throws EXTYPE
   *         If the runnable throws the exception
   * @param <EXTYPE>
   *        Exception type to be thrown
   */
  public <EXTYPE extends Exception> void writeLockedThrowing (@Nonnull final IThrowingRunnable <EXTYPE> aRunnable) throws EXTYPE
  {
    ValueEnforcer.notNull (aRunnable, "Runnable");

    m_aRWLock.writeLock ().lock ();
    try
    {
      aRunnable.run ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * Execute the provided callable in a write lock.
   *
   * @param aSupplier
   *        Callable to be executed. May not be <code>null</code>.
   * @return The return value of the callable. May be <code>null</code>.
   * @param <T>
   *        Return type
   */
  @Nullable
  public <T> T writeLocked (@Nonnull final Supplier <T> aSupplier)
  {
    ValueEnforcer.notNull (aSupplier, "Supplier");

    m_aRWLock.writeLock ().lock ();
    try
    {
      return aSupplier.get ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * Execute the provided callable in a write lock.
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
  @Nullable
  public <T, EXTYPE extends Exception> T writeLockedThrowing (@Nonnull final IThrowingCallable <T, EXTYPE> aCallable) throws EXTYPE
  {
    ValueEnforcer.notNull (aCallable, "Callable");

    m_aRWLock.writeLock ().lock ();
    try
    {
      return aCallable.call ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * Execute the provided callable in a write lock.
   *
   * @param aSupplier
   *        Callable to be executed. May not be <code>null</code>.
   * @return The return value of the callable. May be <code>null</code>.
   */
  public boolean writeLocked (@Nonnull final BooleanSupplier aSupplier)
  {
    ValueEnforcer.notNull (aSupplier, "Supplier");

    m_aRWLock.writeLock ().lock ();
    try
    {
      return aSupplier.getAsBoolean ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * Execute the provided callable in a write lock.
   *
   * @param aSupplier
   *        Callable to be executed. May not be <code>null</code>.
   * @return The return value of the callable. May be <code>null</code>.
   */
  public double writeLocked (@Nonnull final DoubleSupplier aSupplier)
  {
    ValueEnforcer.notNull (aSupplier, "Supplier");

    m_aRWLock.writeLock ().lock ();
    try
    {
      return aSupplier.getAsDouble ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * Execute the provided callable in a write lock.
   *
   * @param aSupplier
   *        Callable to be executed. May not be <code>null</code>.
   * @return The return value of the callable. May be <code>null</code>.
   */
  public int writeLocked (@Nonnull final IntSupplier aSupplier)
  {
    ValueEnforcer.notNull (aSupplier, "Supplier");

    m_aRWLock.writeLock ().lock ();
    try
    {
      return aSupplier.getAsInt ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * Execute the provided callable in a write lock.
   *
   * @param aSupplier
   *        Callable to be executed. May not be <code>null</code>.
   * @return The return value of the callable. May be <code>null</code>.
   */
  public long writeLocked (@Nonnull final LongSupplier aSupplier)
  {
    ValueEnforcer.notNull (aSupplier, "Supplier");

    m_aRWLock.writeLock ().lock ();
    try
    {
      return aSupplier.getAsLong ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }
}

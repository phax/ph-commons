package com.helger.commons.concurrent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.callback.INonThrowingCallable;
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
public class SimpleReadWriteLock implements ReadWriteLock
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
   * @param aCallable
   *        Callable to be executed. May not be <code>null</code>.
   * @return The return value of the callable. May be <code>null</code>.
   * @param <T>
   *        Return type
   */
  @Nullable
  public <T> T readLocked (@Nonnull final INonThrowingCallable <T> aCallable)
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
   * @param aCallable
   *        Callable to be executed. May not be <code>null</code>.
   * @return The return value of the callable. May be <code>null</code>.
   * @param <T>
   *        Return type
   */
  @Nullable
  public <T> T writeLocked (@Nonnull final INonThrowingCallable <T> aCallable)
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
}

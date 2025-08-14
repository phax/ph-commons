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
package com.helger.commons.pool;

import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.base.concurrent.SimpleLock;
import com.helger.base.debug.GlobalDebug;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.log.ConditionalLogger;
import com.helger.base.log.IHasConditionalLogger;
import com.helger.base.reflection.GenericReflection;
import com.helger.base.state.ESuccess;
import com.helger.base.tostring.ToStringGenerator;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A simple generic object pool with a fixed size determined in the constructor.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type of the objects contained in the pool.
 */
@ThreadSafe
public final class ObjectPool <DATATYPE> implements IMutableObjectPool <DATATYPE>, IHasConditionalLogger
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ObjectPool.class);
  private static final ConditionalLogger CONDLOG = new ConditionalLogger (LOGGER, !GlobalDebug.DEFAULT_SILENT_MODE);

  // Lock for this object
  private final SimpleLock m_aLock = new SimpleLock ();

  // The factory for creating objects
  private final IObjectPoolFactory <DATATYPE> m_aFactory;

  // Semaphore for the number of items
  private final Semaphore m_aAvailable;

  // The items itself
  @GuardedBy ("m_aLock")
  private final Object [] m_aItems;

  // Holds the "used/unused" state, because null items may be stored
  @GuardedBy ("m_aLock")
  private final boolean [] m_aUsed;

  /**
   * @return <code>true</code> if logging is disabled, <code>false</code> if it is enabled.
   * @since 9.4.0
   */
  public static boolean isSilentMode ()
  {
    return CONDLOG.isDisabled ();
  }

  /**
   * Enable or disable certain regular log messages.
   *
   * @param bSilentMode
   *        <code>true</code> to disable logging, <code>false</code> to enable logging
   * @return The previous value of the silent mode.
   * @since 9.4.0
   */
  public static boolean setSilentMode (final boolean bSilentMode)
  {
    return !CONDLOG.setEnabled (!bSilentMode);
  }

  /**
   * Create a new object pool for a certain amount of items and a factory that creates the objects
   * on demand.
   *
   * @param nItemCount
   *        The number of items in the pool. Must be &ge; 1.
   * @param aFactory
   *        The factory to create object. May not be <code>null</code>. The factory may not create
   *        <code>null</code> objects, as this leads to an error!
   */
  public ObjectPool (@Nonnegative final int nItemCount, @Nonnull final Supplier <? extends DATATYPE> aFactory)
  {
    this (nItemCount, IObjectPoolFactory.wrap (aFactory));
  }

  /**
   * Create a new object pool for a certain amount of items and a factory that creates the objects
   * on demand.
   *
   * @param nItemCount
   *        The number of items in the pool. Must be &ge; 1.
   * @param aFactory
   *        The factory to create object. May not be <code>null</code>. The factory may not create
   *        <code>null</code> objects, as this leads to an error!
   */
  public ObjectPool (@Nonnegative final int nItemCount, @Nonnull final IObjectPoolFactory <DATATYPE> aFactory)
  {
    ValueEnforcer.isGT0 (nItemCount, "ItemCount");
    ValueEnforcer.notNull (aFactory, "Factory");

    m_aAvailable = new Semaphore (nItemCount);
    m_aItems = new Object [nItemCount];
    m_aUsed = new boolean [nItemCount];
    Arrays.fill (m_aUsed, 0, nItemCount, false);
    m_aFactory = aFactory;
  }

  /**
   * @return The maximum number of items in the pool. Always &gt; 0.
   * @see #getBorrowedObjectCount()
   * @since 11.0.6
   */
  @Nonnegative
  public int getPoolSize ()
  {
    return m_aItems.length;
  }

  /**
   * @return The number of objects currently borrowed from the pool. Something between 0 and
   *         {@link #getPoolSize()}
   * @see #getPoolSize()
   * @since 11.0.6
   */
  @Nonnegative
  public int getBorrowedObjectCount ()
  {
    return m_aItems.length - m_aAvailable.availablePermits ();
  }

  public void clearUnusedItems ()
  {
    // Reset all cached items
    m_aLock.lock ();
    try
    {
      for (int i = 0; i < m_aItems.length; ++i)
        if (!m_aUsed[i])
          m_aItems[i] = null;
    }
    finally
    {
      m_aLock.unlock ();
    }
  }

  @Nullable
  public DATATYPE borrowObject ()
  {
    // Wait for an object to be available
    try
    {
      m_aAvailable.acquire ();
    }
    catch (final InterruptedException ex)
    {
      // In case of acquisition interruption -> return null
      CONDLOG.error ("ObjectPool interrupted", ex);
      Thread.currentThread ().interrupt ();
      return null;
    }

    m_aLock.lock ();
    try
    {
      // Find first unused item
      for (int i = 0; i < m_aItems.length; ++i)
        if (!m_aUsed[i])
        {
          final int index = i;

          DATATYPE ret;
          if (m_aItems[i] == null)
          {
            // if the object is used for the first time, create a new object
            // via the factory
            CONDLOG.debug ( () -> "ObjectPool creates a new object for index " + index);

            m_aItems[i] = ret = m_aFactory.create ();
            if (ret == null)
              throw new IllegalStateException ("The factory returned a null object [1]!");
          }
          else
          {
            // An object is already existing and may be reused
            CONDLOG.debug ( () -> "ObjectPool reuses object for index " + index);

            ret = GenericReflection.uncheckedCast (m_aItems[i]);
            if (m_aFactory.activate (ret).isFailure ())
            {
              // Object cannot be reused - create a new one
              CONDLOG.info ( () -> "ObjectPool failed to activate object for index " + index);

              m_aItems[i] = ret = m_aFactory.create ();
              if (ret == null)
                throw new IllegalStateException ("The factory returned a null object [2]!");
            }
            else
            {
              CONDLOG.debug ( () -> "ObjectPool successfully activated object for index " + index);
            }
          }

          // As the last activity
          m_aUsed[i] = true;
          return ret;
        }

      throw new IllegalStateException ("Should never be reached - ObjectPool exceeds its limit. Looks like a programming error.");
    }
    finally
    {
      m_aLock.unlock ();
    }
  }

  @Nonnull
  public ESuccess returnObject (@Nonnull final DATATYPE aItem)
  {
    m_aLock.lock ();
    try
    {
      for (int i = 0; i < m_aItems.length; ++i)
        if (m_aUsed[i] && aItem == m_aItems[i])
        {
          final int index = i;

          CONDLOG.debug ( () -> "ObjectPool passivates object for index " + index);

          m_aFactory.passivate (aItem);
          m_aUsed[i] = false;

          // Okay, we have one more unused item
          m_aAvailable.release ();
          return ESuccess.SUCCESS;
        }

      CONDLOG.error ( () -> "Object " + aItem + " is not pooled!");
      return ESuccess.FAILURE;
    }
    finally
    {
      m_aLock.unlock ();
    }
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Factory", m_aFactory).getToString ();
  }
}

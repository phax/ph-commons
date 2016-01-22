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
package com.helger.commons.pool;

import java.util.Arrays;
import java.util.concurrent.Semaphore;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.concurrent.SimpleLock;
import com.helger.commons.factory.IFactory;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.state.ESuccess;

/**
 * A simple generic object pool with a fixed size determined in the constructor.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type of the objects contained in the pool.
 */
@ThreadSafe
public final class ObjectPool <DATATYPE> implements IMutableObjectPool <DATATYPE>
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (ObjectPool.class);

  // Lock for this object
  private final SimpleLock m_aLock = new SimpleLock ();

  // Semaphore for the number of items
  private final Semaphore m_aAvailable;

  // The items itself
  private final Object [] m_aItems;

  // Holds the "used/unused" state, because null items may be stored
  private final boolean [] m_aUsed;

  // The factory for creating objects
  private final IFactory <DATATYPE> m_aFactory;

  /**
   * Create a new object pool for a certain amount of items and a factory that
   * creates the objects on demand.
   *
   * @param nItemCount
   *        The number of items in the pool. Must be &ge; 1.
   * @param aFactory
   *        The factory to create object. May not be <code>null</code>. The
   *        factory may not create <code>null</code> objects, as this leads to
   *        an error!
   */
  public ObjectPool (@Nonnegative final int nItemCount, @Nonnull final IFactory <DATATYPE> aFactory)
  {
    ValueEnforcer.isGT0 (nItemCount, "ItemCount");
    ValueEnforcer.notNull (aFactory, "Factory");

    m_aAvailable = new Semaphore (nItemCount);
    m_aItems = new Object [nItemCount];
    m_aUsed = new boolean [nItemCount];
    Arrays.fill (m_aUsed, 0, nItemCount, false);
    m_aFactory = aFactory;
  }

  public void clearUnusedItems ()
  {
    // Reset all cached items
    m_aLock.locked ( () -> {
      for (int i = 0; i < m_aItems.length; ++i)
        if (!m_aUsed[i])
          m_aItems[i] = null;
    });
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
      s_aLogger.error ("ObjectPool interrupted", ex);
      return null;
    }

    return m_aLock.locked ( () -> {
      // Find first unused item
      for (int i = 0; i < m_aItems.length; ++i)
        if (!m_aUsed[i])
        {
          if (m_aItems[i] == null)
          {
            // if the object is used for the first time, create a new object
            // via the factory
            m_aItems[i] = m_aFactory.get ();
            if (m_aItems[i] == null)
              throw new IllegalStateException ("The factory returned a null object!");
          }
          m_aUsed[i] = true;
          return GenericReflection.<Object, DATATYPE> uncheckedCast (m_aItems[i]);
        }
      throw new IllegalStateException ("Should never be reached!");
    });
  }

  @Nonnull
  public ESuccess returnObject (@Nonnull final DATATYPE aItem)
  {
    return m_aLock.locked ( () -> {
      for (int i = 0; i < m_aItems.length; ++i)
        if (m_aUsed[i] && aItem == m_aItems[i])
        {
          m_aUsed[i] = false;

          // Okay, we have one more unused item
          m_aAvailable.release ();
          return ESuccess.SUCCESS;
        }
      s_aLogger.warn ("Object " + aItem + " is not pooled!");
      return ESuccess.FAILURE;
    });
  }
}

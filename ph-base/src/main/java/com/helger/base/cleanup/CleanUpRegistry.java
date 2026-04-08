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
package com.helger.base.cleanup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.misc.Singleton;
import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.spi.ServiceLoaderHelper;

/**
 * This class contains all the cleanup actions which resets library caches etc. to their original
 * state.
 *
 * @author Philip Helger
 */
@ThreadSafe
@Singleton
public final class CleanUpRegistry implements ICleanUpRegistry
{
  private static final class SingletonHolder
  {
    private static final CleanUpRegistry INSTANCE = new CleanUpRegistry ();
  }

  private static final class Item implements Comparable <Item>
  {
    private final int m_nPriority;
    private final Runnable m_aRunnable;

    private Item (final int nPriority, final Runnable aRunnable)
    {
      m_nPriority = nPriority;
      m_aRunnable = aRunnable;
    }

    /**
     * Compare this item with another item by priority.
     *
     * @param o
     *        The other item to compare to. May not be <code>null</code>.
     * @return A negative integer, zero, or a positive integer as this item has
     *         lower, equal, or higher priority than the specified item.
     */
    public int compareTo (@NonNull final Item o)
    {
      return Integer.compare (m_nPriority, o.m_nPriority);
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger (CleanUpRegistry.class);

  private static volatile boolean s_bDefaultInstantiated = false;

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

  // Use a weak hash map, because the key is a class
  @GuardedBy ("m_aRWLock")
  @CodingStyleguideUnaware
  private final List <Item> m_aActions = new ArrayList <> ();

  private CleanUpRegistry ()
  {
    _reinitialize ();
  }

  /**
   * @return <code>true</code> if the singleton instance has been created,
   *         <code>false</code> otherwise.
   */
  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  /**
   * @return The singleton instance of this class. Never <code>null</code>.
   */
  @NonNull
  public static CleanUpRegistry getInstance ()
  {
    final CleanUpRegistry ret = SingletonHolder.INSTANCE;
    s_bDefaultInstantiated = true;
    return ret;
  }

  private void _reinitialize ()
  {
    m_aRWLock.writeLocked ( () -> {
      // Delete existing
      m_aActions.clear ();

      for (final ICleanUpRegistrarSPI aSPI : ServiceLoaderHelper.getAllSPIImplementations (ICleanUpRegistrarSPI.class))
      {
        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Calling registerCleanUpAction on " + aSPI.getClass ().getName ());
        aSPI.registerCleanUpAction (this);
      }

      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Registered " + m_aActions.size () + " cleanup actions");
    });
  }

  /**
   * Reinitialize the clean up registry by re-reading all SPI implementations.
   */
  public void reinitialize ()
  {
    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Reinitializing " + getClass ().getName ());

    _reinitialize ();
  }

  /** {@inheritDoc} */
  public void registerCleanup (final int nPriority, @NonNull final Runnable aRunnable)
  {
    Objects.requireNonNull (aRunnable, "Runnable");

    m_aRWLock.writeLocked ( () -> {
      m_aActions.add (new Item (nPriority, aRunnable));
      Collections.sort (m_aActions);
    });
  }

  /**
   * Execute all registered cleanup actions in priority order.
   */
  public void performCleanUp ()
  {
    // Make a copy
    final List <Item> aActions = m_aRWLock.readLockedGet ( () -> new ArrayList <> (m_aActions));

    LOGGER.info ("Running " + aActions.size () + " cleanup actions");

    for (final Item aItem : aActions)
      aItem.m_aRunnable.run ();
  }
}

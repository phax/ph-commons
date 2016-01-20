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
package com.helger.commons.concurrent.collector;

import java.util.concurrent.BlockingQueue;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.lang.GenericReflection;

/**
 * Concurrent collector that performs action on each object separately
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type of the objects in the queue.
 */
public class ConcurrentCollectorSingle <DATATYPE> extends AbstractConcurrentCollector <DATATYPE>
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (ConcurrentCollectorSingle.class);

  private IConcurrentPerformer <DATATYPE> m_aPerformer;

  /**
   * Constructor that uses {@link #DEFAULT_MAX_QUEUE_SIZE} elements as the
   * maximum queue length.
   */
  public ConcurrentCollectorSingle ()
  {
    this (DEFAULT_MAX_QUEUE_SIZE);
  }

  /**
   * Constructor.
   *
   * @param nMaxQueueSize
   *        The maximum number of items that can be in the queue. Must be &gt;
   *        0.
   */
  public ConcurrentCollectorSingle (@Nonnegative final int nMaxQueueSize)
  {
    super (nMaxQueueSize);
  }

  /**
   * Constructor using an existing {@link BlockingQueue}.
   *
   * @param aQueue
   *        {@link BlockingQueue} to use. May not be <code>null</code>.
   */
  public ConcurrentCollectorSingle (@Nonnull final BlockingQueue <Object> aQueue)
  {
    super (aQueue);
  }

  /**
   * @return The current performer set. <code>null</code> if none was explicitly
   *         set.
   */
  @Nullable
  public final IConcurrentPerformer <DATATYPE> getPerformer ()
  {
    return m_aPerformer;
  }

  /**
   * Set the performer to be used. This method must be invoked before the
   * collector can be run. The passed implementation must be rock-solid as this
   * class will not make any retries. If the passed performer throws and
   * exception without handling the objects correct the objects will be lost!
   *
   * @param aPerformer
   *        The performer to be used. May not be <code>null</code>.
   * @return this for chaining
   * @throws IllegalStateException
   *         If another performer is already present!
   */
  @Nonnull
  public final ConcurrentCollectorSingle <DATATYPE> setPerformer (@Nonnull final IConcurrentPerformer <DATATYPE> aPerformer)
  {
    if (m_aPerformer != null)
      throw new IllegalStateException ("Another performer is already set!");
    m_aPerformer = ValueEnforcer.notNull (aPerformer, "Performer");
    return this;
  }

  private void _perform (final DATATYPE aObject)
  {
    try
    {
      // Perform the action on the objects, regardless of whether a
      // "stop queue message" was received or not
      m_aPerformer.runAsync (aObject);
    }
    catch (final Throwable t)
    {
      s_aLogger.error ("Failed to perform actions on object with performer " +
                       m_aPerformer +
                       " - object has been lost!",
                       t);
    }
  }

  /**
   * This method starts the collector by taking objects from the internal
   * {@link BlockingQueue}. So this method blocks and must be invoked from a
   * separate thread. This method runs until {@link #stopQueuingNewObjects()} is
   * new called and the queue is empty.
   *
   * @throws IllegalStateException
   *         if no performer is set - see
   *         {@link #setPerformer(IConcurrentPerformer)}
   */
  public final void run ()
  {
    if (m_aPerformer == null)
      throw new IllegalStateException ("No performer set!");

    try
    {
      // The temporary list that contains all objects to be delivered
      while (true)
      {
        // Block until the first object is in the queue
        final Object aCurrentObject = m_aQueue.take ();
        if (aCurrentObject == STOP_QUEUE_OBJECT)
          break;

        _perform (GenericReflection.<Object, DATATYPE> uncheckedCast (aCurrentObject));
      }
    }
    catch (final Throwable t)
    {
      s_aLogger.error ("Error taking elements from queue - queue has been interrupted!!!", t);
    }
  }
}

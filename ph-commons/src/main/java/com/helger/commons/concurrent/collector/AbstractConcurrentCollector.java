/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.state.ESuccess;

/**
 * Abstract concurrent collector based on {@link BlockingQueue}.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type of the objects in the queue.
 */
@ThreadSafe
public abstract class AbstractConcurrentCollector <DATATYPE> implements IMutableConcurrentCollector <DATATYPE>
{
  /** Default maximum queue size */
  public static final int DEFAULT_MAX_QUEUE_SIZE = 100;

  /**
   * The STOP object that is internally added to the queue to indicate the last
   * token. Should implement Comparable for the priority queue
   */
  public static final Object STOP_QUEUE_OBJECT = new Object ();

  private static final Logger LOGGER = LoggerFactory.getLogger (AbstractConcurrentCollector.class);

  // It's a list of Object because otherwise we could not use a static
  // STOP_OBJECT that works for every type. But it is ensured that the queue
  // contains only objects of type T
  // All Java BlockingQueues are thread safe
  protected final BlockingQueue <Object> m_aQueue;

  // Is the queue stopped?
  private final AtomicBoolean m_aStopTakingNewObjects = new AtomicBoolean (false);

  /**
   * Constructor creating an {@link ArrayBlockingQueue} internally.
   *
   * @param nMaxQueueSize
   *        The maximum number of items that can be in the queue. Must be &gt;
   *        0.
   */
  public AbstractConcurrentCollector (@Nonnegative final int nMaxQueueSize)
  {
    this (new ArrayBlockingQueue <> (ValueEnforcer.isGT0 (nMaxQueueSize, "MaxQueueSize")));
  }

  /**
   * Constructor using an arbitrary {@link BlockingQueue}.
   *
   * @param aQueue
   *        The {@link BlockingQueue} to be used. May not be <code>null</code>.
   */
  public AbstractConcurrentCollector (@Nonnull final BlockingQueue <Object> aQueue)
  {
    ValueEnforcer.notNull (aQueue, "Queue");
    m_aQueue = aQueue;
  }

  @Nonnull
  public final ESuccess queueObject (@Nonnull final DATATYPE aObject)
  {
    ValueEnforcer.notNull (aObject, "Object");
    if (isStopped ())
      throw new IllegalStateException ("The queue is already stopped and does not take any more elements");

    try
    {
      m_aQueue.put (aObject);
      return ESuccess.SUCCESS;
    }
    catch (final InterruptedException ex)
    {
      LOGGER.error ("Failed to submit object to queue", ex);
      Thread.currentThread ().interrupt ();
      return ESuccess.FAILURE;
    }
  }

  public boolean isQueueEmpty ()
  {
    return m_aQueue.isEmpty ();
  }

  @Nonnegative
  public final int getQueueLength ()
  {
    return m_aQueue.size ();
  }

  @Nonnull
  public final ESuccess stopQueuingNewObjects ()
  {
    if (m_aStopTakingNewObjects.getAndSet (true))
    {
      // Already stooped
      return ESuccess.FAILURE;
    }

    try
    {
      // put specific stop queue object
      m_aQueue.put (STOP_QUEUE_OBJECT);
      return ESuccess.SUCCESS;
    }
    catch (final InterruptedException ex)
    {
      LOGGER.error ("Error stopping queue", ex);
      Thread.currentThread ().interrupt ();
      // Set to false again
      m_aStopTakingNewObjects.set (false);
      return ESuccess.FAILURE;
    }
  }

  public final boolean isStopped ()
  {
    return m_aStopTakingNewObjects.get ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public final ICommonsList <DATATYPE> drainQueue ()
  {
    // Drain all objects to this queue
    final ICommonsList <Object> aDrainedToList = new CommonsArrayList <> ();
    m_aQueue.drainTo (aDrainedToList);

    // Change data type
    final ICommonsList <DATATYPE> ret = new CommonsArrayList <> ();
    for (final Object aObj : aDrainedToList)
      if (!EqualsHelper.identityEqual (aObj, STOP_QUEUE_OBJECT))
        ret.add (GenericReflection.uncheckedCast (aObj));
      else
      {
        // Re-add the stop object, because loops in derived classes rely on this
        // object
        m_aQueue.add (aObj);
      }
    return ret;
  }
}

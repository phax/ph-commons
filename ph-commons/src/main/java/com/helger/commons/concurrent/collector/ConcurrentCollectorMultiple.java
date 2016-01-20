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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.state.ESuccess;

/**
 * Concurrent collector that performs action on multiple objects at once
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type of the objects in the queue.
 */
public class ConcurrentCollectorMultiple <DATATYPE> extends AbstractConcurrentCollector <DATATYPE>
{
  /** The default number of objects to be put in the queue for execution. */
  public static final int DEFAULT_MAX_PERFORM_COUNT = DEFAULT_MAX_QUEUE_SIZE / 2;
  private static final Logger s_aLogger = LoggerFactory.getLogger (ConcurrentCollectorMultiple.class);

  @Nonnegative
  private final int m_nMaxPerformCount;

  private IConcurrentPerformer <List <DATATYPE>> m_aPerformer;

  /**
   * Constructor that uses {@link #DEFAULT_MAX_QUEUE_SIZE} elements as the
   * maximum queue length and {@link #DEFAULT_MAX_PERFORM_COUNT} as the max
   * perform count.
   */
  public ConcurrentCollectorMultiple ()
  {
    this (DEFAULT_MAX_QUEUE_SIZE, DEFAULT_MAX_PERFORM_COUNT);
  }

  /**
   * Constructor.
   *
   * @param nMaxQueueSize
   *        The maximum number of items that can be in the queue. Must be &gt;
   *        0.
   * @param nMaxPerformCount
   *        The maximum number of objects to be put in the queue for execution.
   *        Must be &gt; 0.
   */
  public ConcurrentCollectorMultiple (@Nonnegative final int nMaxQueueSize, @Nonnegative final int nMaxPerformCount)
  {
    super (nMaxQueueSize);
    ValueEnforcer.isGT0 (nMaxPerformCount, "MaxPerformCount");
    if (nMaxPerformCount > nMaxQueueSize)
      throw new IllegalArgumentException ("max perform size is illegal: " + nMaxPerformCount);
    m_nMaxPerformCount = nMaxPerformCount;
  }

  /**
   * Constructor using an existing {@link BlockingQueue} and the default max
   * perform count ({@value #DEFAULT_MAX_PERFORM_COUNT}).
   *
   * @param aQueue
   *        {@link BlockingQueue} to use. May not be <code>null</code>.
   */
  public ConcurrentCollectorMultiple (@Nonnull final BlockingQueue <Object> aQueue)
  {
    this (aQueue, DEFAULT_MAX_PERFORM_COUNT);
  }

  /**
   * Constructor using an existing {@link BlockingQueue}.
   *
   * @param aQueue
   *        {@link BlockingQueue} to use. May not be <code>null</code>.
   * @param nMaxPerformCount
   *        The maximum number of objects to be put in the list for execution.
   *        Must be &gt; 0.
   */
  public ConcurrentCollectorMultiple (@Nonnull final BlockingQueue <Object> aQueue,
                                      @Nonnegative final int nMaxPerformCount)
  {
    super (aQueue);
    m_nMaxPerformCount = ValueEnforcer.isGT0 (nMaxPerformCount, "MaxPerformCount");
  }

  /**
   * @return The maximum number of objects to be put in the list for execution.
   *         Always &ge; 0.
   */
  @Nonnegative
  public final int getMaxPerformCount ()
  {
    return m_nMaxPerformCount;
  }

  /**
   * @return The current performer set. <code>null</code> if none was explicitly
   *         set.
   */
  @Nullable
  public final IConcurrentPerformer <List <DATATYPE>> getPerformer ()
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
  public final ConcurrentCollectorMultiple <DATATYPE> setPerformer (@Nonnull final IConcurrentPerformer <List <DATATYPE>> aPerformer)
  {
    if (m_aPerformer != null)
      throw new IllegalStateException ("Another performer is already set!");
    m_aPerformer = ValueEnforcer.notNull (aPerformer, "Performer");
    return this;
  }

  /**
   * Internal method to invoke the performed for the passed list of objects.
   *
   * @param aObjectsToPerform
   *        List of objects to be passed to the performer. Never
   *        <code>null</code>. The length is at last
   *        {@link #getMaxPerformCount()}.
   * @return {@link ESuccess}
   */
  @Nonnull
  private ESuccess _perform (@Nonnull final List <DATATYPE> aObjectsToPerform)
  {
    if (!aObjectsToPerform.isEmpty ())
    {
      try
      {
        // Perform the action on the objects, regardless of whether a
        // "stop queue message" was received or not
        m_aPerformer.runAsync (aObjectsToPerform);
      }
      catch (final Throwable t)
      {
        s_aLogger.error ("Failed to perform actions on " +
                         aObjectsToPerform.size () +
                         " objects with performer " +
                         m_aPerformer +
                         " - objects are lost!",
                         t);
        return ESuccess.FAILURE;
      }

      // clear perform-list anyway
      aObjectsToPerform.clear ();
    }

    return ESuccess.SUCCESS;
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
      final List <DATATYPE> aObjectsToPerform = new ArrayList <DATATYPE> ();
      boolean bQueueIsStopped = false;

      while (true)
      {
        // Block until the first object is in the queue
        Object aCurrentObject = m_aQueue.take ();
        if (aCurrentObject == STOP_QUEUE_OBJECT)
          break;

        // add current object
        aObjectsToPerform.add (GenericReflection.<Object, DATATYPE> uncheckedCast (aCurrentObject));

        // take all messages that are in the queue and handle them at once.
        // Handle at last m_nMaxPerformSize objects
        while (aObjectsToPerform.size () < m_nMaxPerformCount && !m_aQueue.isEmpty ())
        {
          // Explicitly handle the "stop queue message" (using "=="!!!)
          if ((aCurrentObject = m_aQueue.take ()) == STOP_QUEUE_OBJECT)
          {
            bQueueIsStopped = true;
            break;
          }

          // add current object
          aObjectsToPerform.add (GenericReflection.<Object, DATATYPE> uncheckedCast (aCurrentObject));
        }

        _perform (aObjectsToPerform);

        // In case we received a stop message while getting the bulk messages
        // above -> break the loop manually
        // Note: do not include in while-loop above because the conditions may
        // not execute in the correct order since "take" is blocking!
        if (bQueueIsStopped)
          break;
      }

      // perform any remaining actions
      _perform (aObjectsToPerform);
    }
    catch (final Throwable t)
    {
      s_aLogger.error ("Error taking elements from queue - queue has been interrupted!!!", t);
    }
  }
}

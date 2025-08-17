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
package com.helger.xml.util.thread;

import java.lang.Thread.State;
import java.util.Comparator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.array.ArrayHelper;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.rt.StackTraceHelper;
import com.helger.base.string.StringHelper;
import com.helger.base.string.StringImplode;
import com.helger.base.timing.StopWatch;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsEnumMap;
import com.helger.collection.commons.CommonsTreeSet;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.commons.ICommonsNavigableSet;
import com.helger.collection.commons.ICommonsSet;
import com.helger.collection.helper.CollectionSort;
import com.helger.xml.microdom.IHasMicroNodeRepresentation;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * This class contains a list of {@link ThreadDescriptor} objects.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class ThreadDescriptorList implements IHasMicroNodeRepresentation
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ThreadDescriptorList.class);

  private final ICommonsList <ThreadDescriptor> m_aList = new CommonsArrayList <> ();
  private String m_sError;

  public ThreadDescriptorList ()
  {}

  @Nonnull
  public ThreadDescriptorList addDescriptor (@Nonnull final ThreadDescriptor aDescriptor)
  {
    ValueEnforcer.notNull (aDescriptor, "Descriptor");
    m_aList.add (aDescriptor);
    return this;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <ThreadDescriptor> getAllDescriptors ()
  {
    return m_aList.getClone ();
  }

  @Nonnull
  public ThreadDescriptorList setError (@Nullable final String sError)
  {
    m_sError = sError;
    return this;
  }

  @Nullable
  public String getError ()
  {
    return m_sError;
  }

  @Nonnull
  @ReturnsMutableCopy
  private ICommonsMap <State, ICommonsNavigableSet <Long>> _getStateMap ()
  {
    // Group threads by state
    final ICommonsMap <State, ICommonsNavigableSet <Long>> aStateMap = new CommonsEnumMap <> (State.class);
    for (final ThreadDescriptor aDescriptor : m_aList)
    {
      final State eState = aDescriptor.getThreadState ();
      final ICommonsNavigableSet <Long> aThreadIDs = aStateMap.computeIfAbsent (eState, k -> new CommonsTreeSet <> ());
      aThreadIDs.add (Long.valueOf (aDescriptor.getThreadID ()));
    }
    return aStateMap;
  }

  @Nonnull
  @Nonempty
  public String getAsString ()
  {
    final StringBuilder aSB = new StringBuilder ();
    final String sStr = m_sError;

    // Error always shown first!
    if (StringHelper.isNotEmpty (sStr))
      aSB.append ("ERROR retrieving all thread stack traces: ").append (m_sError).append ("\n\n");

    // Total thread count
    aSB.append ("Total thread count: ").append (m_aList.size ()).append ('\n');

    // Emit thread IDs grouped by state
    final ICommonsMap <State, ICommonsNavigableSet <Long>> aStateMap = _getStateMap ();
    for (final State eState : State.values ())
    {
      final ICommonsSet <Long> aThreadIDs = aStateMap.get (eState);
      final int nSize = aThreadIDs.size ();
      aSB.append ("Thread state ").append (eState).append (" [").append (nSize).append (']');
      if (nSize > 0)
        aSB.append (": ").append (aThreadIDs.toString ());
      aSB.append ('\n');
    }
    // Append all stack traces at the end
    for (final ThreadDescriptor aDescriptor : m_aList)
      aSB.append ('\n').append (aDescriptor.getAsString ());
    return aSB.toString ();
  }

  @Nonnull
  public IMicroElement getAsMicroNode ()
  {
    final IMicroElement eRet = new MicroElement ("threadlist");
    final String sStr = m_sError;
    if (StringHelper.isNotEmpty (sStr))
      eRet.addElement ("error").addText (m_sError);

    // Overall thread count
    eRet.setAttribute ("threadcount", m_aList.size ());

    // Emit thread IDs grouped by state
    final ICommonsMap <State, ICommonsNavigableSet <Long>> aStateMap = _getStateMap ();
    for (final State eState : State.values ())
    {
      final ICommonsSet <Long> aThreadIDs = aStateMap.get (eState);
      final int nSize = aThreadIDs.size ();

      final IMicroElement eThreadState = eRet.addElement ("threadstate");
      eThreadState.setAttribute ("id", eState.toString ());
      eThreadState.setAttribute ("threadcount", nSize);
      if (nSize > 0)
        eThreadState.addText (StringImplode.getImploded (',', aThreadIDs));
    }
    // Append all stack traces at the end
    for (final ThreadDescriptor aDescriptor : m_aList)
      eRet.addChild (aDescriptor.getAsMicroNode ());
    return eRet;
  }

  @Nonnull
  @Nonempty
  private static String _getAsString (@Nonnull final Throwable t)
  {
    return t.getMessage () + " -- " + t.getClass ().getName ();
  }

  @Nonnull
  public static ThreadDescriptorList createWithAllThreads ()
  {
    // add dump of all threads
    final StopWatch aSW = StopWatch.createdStarted ();
    final ThreadDescriptorList ret = new ThreadDescriptorList ();
    try
    {
      // Get all stack traces, sorted by thread ID
      for (final Map.Entry <Thread, StackTraceElement []> aEntry : CollectionSort.getSortedByKey (Thread.getAllStackTraces (),
                                                                                                  Comparator.comparing (Thread::getId))
                                                                                 .entrySet ())
      {
        final StackTraceElement [] aStackTrace = aEntry.getValue ();
        final String sStackTrace = ArrayHelper.isEmpty (aStackTrace) ? "No stack trace available!\n" : StackTraceHelper
                                                                                                                       .getStackAsString (aStackTrace,
                                                                                                                                          false);
        ret.addDescriptor (new ThreadDescriptor (aEntry.getKey (), sStackTrace));
      }
    }
    catch (final Exception ex)
    {
      LOGGER.error ("Error collecting all thread descriptors", ex);
      ret.setError ("Error collecting all thread descriptors: " + _getAsString (ex));
    }
    finally
    {
      final long nMillis = aSW.stopAndGetMillis ();
      if (nMillis > 1000)
        LOGGER.warn ("Took " + nMillis + " ms to get all thread descriptors!");
    }
    return ret;
  }
}

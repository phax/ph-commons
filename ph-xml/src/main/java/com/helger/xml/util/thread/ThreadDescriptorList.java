/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsEnumMap;
import com.helger.commons.collection.impl.CommonsTreeSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsNavigableSet;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.lang.StackTraceHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.timing.StopWatch;
import com.helger.xml.microdom.IHasMicroNodeRepresentation;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;

/**
 * This class contains a list of {@link ThreadDescriptor} objects.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class ThreadDescriptorList implements IHasMicroNodeRepresentation
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (ThreadDescriptorList.class);

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

    // Error always shown first!
    if (StringHelper.hasText (m_sError))
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
    if (StringHelper.hasText (m_sError))
      eRet.appendElement ("error").appendText (m_sError);

    // Overall thread count
    eRet.setAttribute ("threadcount", m_aList.size ());

    // Emit thread IDs grouped by state
    final ICommonsMap <State, ICommonsNavigableSet <Long>> aStateMap = _getStateMap ();
    for (final State eState : State.values ())
    {
      final ICommonsSet <Long> aThreadIDs = aStateMap.get (eState);
      final int nSize = aThreadIDs.size ();

      final IMicroElement eThreadState = eRet.appendElement ("threadstate");
      eThreadState.setAttribute ("id", eState.toString ());
      eThreadState.setAttribute ("threadcount", nSize);
      if (nSize > 0)
        eThreadState.appendText (StringHelper.getImploded (',', aThreadIDs));
    }

    // Append all stack traces at the end
    for (final ThreadDescriptor aDescriptor : m_aList)
      eRet.appendChild (aDescriptor.getAsMicroNode ());
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
      for (final Map.Entry <Thread, StackTraceElement []> aEntry : CollectionHelper.getSortedByKey (Thread.getAllStackTraces (),
                                                                                                    Comparator.comparing (Thread::getId))
                                                                                   .entrySet ())
      {
        final StackTraceElement [] aStackTrace = aEntry.getValue ();
        final String sStackTrace = ArrayHelper.isEmpty (aStackTrace) ? "No stack trace available!\n"
                                                                     : StackTraceHelper.getStackAsString (aStackTrace,
                                                                                                          false);
        ret.addDescriptor (new ThreadDescriptor (aEntry.getKey (), sStackTrace));
      }
    }
    catch (final Exception ex)
    {
      s_aLogger.error ("Error collecting all thread descriptors", ex);
      ret.setError ("Error collecting all thread descriptors: " + _getAsString (ex));
    }
    finally
    {
      final long nMillis = aSW.stopAndGetMillis ();
      if (nMillis > 1000)
        if (s_aLogger.isWarnEnabled ())
          s_aLogger.warn ("Took " + nMillis + " ms to get all thread descriptors!");
    }
    return ret;
  }
}

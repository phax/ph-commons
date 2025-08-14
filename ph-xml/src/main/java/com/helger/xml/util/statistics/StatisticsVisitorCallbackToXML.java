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
package com.helger.xml.util.statistics;

import com.helger.base.enforcer.ValueEnforcer;
import com.helger.base.statistics.IStatisticsHandlerCache;
import com.helger.base.statistics.IStatisticsHandlerCounter;
import com.helger.base.statistics.IStatisticsHandlerKeyedCounter;
import com.helger.base.statistics.IStatisticsHandlerKeyedSize;
import com.helger.base.statistics.IStatisticsHandlerKeyedTimer;
import com.helger.base.statistics.IStatisticsHandlerSize;
import com.helger.base.statistics.IStatisticsHandlerTimer;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.statistics.util.IStatisticsVisitorCallback;
import com.helger.xml.microdom.IMicroElement;

import jakarta.annotation.Nonnull;

/**
 * Special implementation of the {@link IStatisticsVisitorCallback} interface that fills a micro
 * element with all current values
 *
 * @author Philip Helger
 */
public class StatisticsVisitorCallbackToXML implements IStatisticsVisitorCallback
{
  private final IMicroElement m_eRoot;

  public StatisticsVisitorCallbackToXML (@Nonnull final IMicroElement eRoot)
  {
    m_eRoot = ValueEnforcer.notNull (eRoot, "Root");
  }

  /**
   * @return The root element passed in the constructor. Never <code>null</code> .
   */
  @Nonnull
  public IMicroElement getRoot ()
  {
    return m_eRoot;
  }

  @Override
  public void onCache (final String sName, final IStatisticsHandlerCache aHandler)
  {
    if (aHandler.getInvocationCount () > 0)
      m_eRoot.addElement (StatisticsExporter.ELEMENT_CACHE)
             .setAttribute (StatisticsExporter.ATTR_NAME, sName)
             .setAttribute (StatisticsExporter.ATTR_INVOCATIONCOUNT, aHandler.getInvocationCount ())
             .setAttribute (StatisticsExporter.ATTR_HITS, aHandler.getHits ())
             .setAttribute (StatisticsExporter.ATTR_MISSES, aHandler.getMisses ());
  }

  @Override
  public void onTimer (final String sName, final IStatisticsHandlerTimer aHandler)
  {
    if (aHandler.getInvocationCount () > 0)
      m_eRoot.addElement (StatisticsExporter.ELEMENT_TIMER)
             .setAttribute (StatisticsExporter.ATTR_NAME, sName)
             .setAttribute (StatisticsExporter.ATTR_INVOCATIONCOUNT, aHandler.getInvocationCount ())
             .setAttribute (StatisticsExporter.ATTR_MIN, aHandler.getMin ())
             .setAttribute (StatisticsExporter.ATTR_AVERAGE, aHandler.getAverage ())
             .setAttribute (StatisticsExporter.ATTR_MAX, aHandler.getMax ())
             .setAttributeWithConversion (StatisticsExporter.ATTR_SUM, aHandler.getSum ());
  }

  @Override
  public void onKeyedTimer (final String sName, final IStatisticsHandlerKeyedTimer aHandler)
  {
    if (aHandler.getInvocationCount () > 0)
    {
      final IMicroElement eKeyedTimer = m_eRoot.addElement (StatisticsExporter.ELEMENT_KEYEDTIMER)
                                               .setAttribute (StatisticsExporter.ATTR_NAME, sName)
                                               .setAttribute (StatisticsExporter.ATTR_INVOCATIONCOUNT,
                                                              aHandler.getInvocationCount ());
      for (final String sKey : CollectionHelper.getSorted (aHandler.getAllKeys ()))
      {
        eKeyedTimer.addElement (StatisticsExporter.ELEMENT_KEY)
                   .setAttribute (StatisticsExporter.ATTR_NAME, sKey)
                   .setAttribute (StatisticsExporter.ATTR_INVOCATIONCOUNT, aHandler.getInvocationCount (sKey))
                   .setAttribute (StatisticsExporter.ATTR_MIN, aHandler.getMin (sKey))
                   .setAttribute (StatisticsExporter.ATTR_AVERAGE, aHandler.getAverage (sKey))
                   .setAttribute (StatisticsExporter.ATTR_MAX, aHandler.getMax (sKey))
                   .setAttributeWithConversion (StatisticsExporter.ATTR_SUM, aHandler.getSum (sKey));
      }
    }
  }

  @Override
  public void onSize (final String sName, final IStatisticsHandlerSize aHandler)
  {
    if (aHandler.getInvocationCount () > 0)
      m_eRoot.addElement (StatisticsExporter.ELEMENT_SIZE)
             .setAttribute (StatisticsExporter.ATTR_NAME, sName)
             .setAttribute (StatisticsExporter.ATTR_INVOCATIONCOUNT, aHandler.getInvocationCount ())
             .setAttribute (StatisticsExporter.ATTR_MIN, aHandler.getMin ())
             .setAttribute (StatisticsExporter.ATTR_AVERAGE, aHandler.getAverage ())
             .setAttribute (StatisticsExporter.ATTR_MAX, aHandler.getMax ())
             .setAttributeWithConversion (StatisticsExporter.ATTR_SUM, aHandler.getSum ());
  }

  @Override
  public void onKeyedSize (final String sName, final IStatisticsHandlerKeyedSize aHandler)
  {
    if (aHandler.getInvocationCount () > 0)
    {
      final IMicroElement eKeyedSize = m_eRoot.addElement (StatisticsExporter.ELEMENT_KEYEDSIZE)
                                              .setAttribute (StatisticsExporter.ATTR_NAME, sName)
                                              .setAttribute (StatisticsExporter.ATTR_INVOCATIONCOUNT,
                                                             aHandler.getInvocationCount ());
      for (final String sKey : CollectionHelper.getSorted (aHandler.getAllKeys ()))
      {
        eKeyedSize.addElement (StatisticsExporter.ELEMENT_KEY)
                  .setAttribute (StatisticsExporter.ATTR_NAME, sKey)
                  .setAttribute (StatisticsExporter.ATTR_INVOCATIONCOUNT, aHandler.getInvocationCount (sKey))
                  .setAttribute (StatisticsExporter.ATTR_MIN, aHandler.getMin (sKey))
                  .setAttribute (StatisticsExporter.ATTR_AVERAGE, aHandler.getAverage (sKey))
                  .setAttribute (StatisticsExporter.ATTR_MAX, aHandler.getMax (sKey))
                  .setAttributeWithConversion (StatisticsExporter.ATTR_SUM, aHandler.getSum (sKey));
      }
    }
  }

  @Override
  public void onCounter (final String sName, final IStatisticsHandlerCounter aHandler)
  {
    if (aHandler.getInvocationCount () > 0)
      m_eRoot.addElement (StatisticsExporter.ELEMENT_COUNTER)
             .setAttribute (StatisticsExporter.ATTR_NAME, sName)
             .setAttribute (StatisticsExporter.ATTR_INVOCATIONCOUNT, aHandler.getInvocationCount ())
             .setAttribute (StatisticsExporter.ATTR_COUNT, aHandler.getCount ());
  }

  @Override
  public void onKeyedCounter (final String sName, final IStatisticsHandlerKeyedCounter aHandler)
  {
    if (aHandler.getInvocationCount () > 0)
    {
      final IMicroElement eKeyedCounter = m_eRoot.addElement (StatisticsExporter.ELEMENT_KEYEDCOUNTER)
                                                 .setAttribute (StatisticsExporter.ATTR_NAME, sName)
                                                 .setAttribute (StatisticsExporter.ATTR_INVOCATIONCOUNT,
                                                                aHandler.getInvocationCount ());
      for (final String sKey : CollectionHelper.getSorted (aHandler.getAllKeys ()))
      {
        eKeyedCounter.addElement (StatisticsExporter.ELEMENT_KEY)
                     .setAttribute (StatisticsExporter.ATTR_NAME, sKey)
                     .setAttribute (StatisticsExporter.ATTR_INVOCATIONCOUNT, aHandler.getInvocationCount (sKey))
                     .setAttribute (StatisticsExporter.ATTR_COUNT, aHandler.getCount (sKey));
      }
    }
  }
}

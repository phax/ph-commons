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
package com.helger.commons.statistics.util;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.MicroDocument;

/**
 * Class for exporting statistics.
 *
 * @author Philip Helger
 */
@Immutable
public final class StatisticsExporter
{
  /** Element name for XML export */
  public static final String ELEMENT_STATISTICS = "statistics";
  /** Element name for XML export */
  public static final String ELEMENT_CACHE = "cache";
  /** Attribute name for XML export */
  public static final String ATTR_HITS = "hits";
  /** Attribute name for XML export */
  public static final String ATTR_MISSES = "misses";
  /** Element name for XML export */
  public static final String ELEMENT_TIMER = "timer";
  /** Element name for XML export */
  public static final String ELEMENT_KEYEDTIMER = "keyedtimer";
  /** Element name for XML export */
  public static final String ELEMENT_SIZE = "size";
  /** Element name for XML export */
  public static final String ELEMENT_KEYEDSIZE = "keyedsize";
  /** Attribute name for XML export */
  public static final String ATTR_MIN = "min";
  /** Attribute name for XML export */
  public static final String ATTR_AVERAGE = "average";
  /** Attribute name for XML export */
  public static final String ATTR_MAX = "max";
  /** Attribute name for XML export */
  public static final String ATTR_SUM = "sum";
  /** Element name for XML export */
  public static final String ELEMENT_COUNTER = "counter";
  /** Element name for XML export */
  public static final String ELEMENT_KEYEDCOUNTER = "keyedcounter";
  /** Element name for XML export */
  public static final String ELEMENT_KEY = "key";
  /** Attribute name for XML export */
  public static final String ATTR_NAME = "name";
  /** Attribute name for XML export */
  public static final String ATTR_INVOCATIONCOUNT = "invocationcount";
  /** Attribute name for XML export */
  public static final String ATTR_COUNT = "count";

  @PresentForCodeCoverage
  private static final StatisticsExporter s_aInstance = new StatisticsExporter ();

  private StatisticsExporter ()
  {}

  @Nonnull
  public static IMicroDocument getAsXMLDocument ()
  {
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.appendElement (ELEMENT_STATISTICS);
    StatisticsVisitor.visitStatistics (new StatisticsVisitorCallbackToXML (eRoot));
    return aDoc;
  }
}

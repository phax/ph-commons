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
package com.helger.xml.util.statistics;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import com.helger.statistics.impl.StatisticsManager;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;

/**
 * Test class for class {@link StatisticsVisitorCallbackToXML}.
 *
 * @author Philip Helger
 */
public final class StatisticsVisitorCallbackToXMLTest
{
  private static final String PREFIX = StatisticsVisitorCallbackToXMLTest.class.getName () + "$";

  /**
   * Fill every kind of statistics handler, so that the export covers all callbacks.
   */
  @BeforeClass
  public static void fillStatistics ()
  {
    StatisticsManager.getCacheHandler (PREFIX + "cache").cacheHit ();
    StatisticsManager.getCacheHandler (PREFIX + "cache").cacheMiss ();
    StatisticsManager.getTimerHandler (PREFIX + "timer").addTime (100);
    StatisticsManager.getKeyedTimerHandler (PREFIX + "keyedtimer").addTime ("key1", 100);
    StatisticsManager.getSizeHandler (PREFIX + "size").addSize (1000);
    StatisticsManager.getKeyedSizeHandler (PREFIX + "keyedsize").addSize ("key1", 1000);
    StatisticsManager.getCounterHandler (PREFIX + "counter").increment ();
    StatisticsManager.getKeyedCounterHandler (PREFIX + "keyedcounter").increment ("key1");
  }

  private static void _assertExported (final IMicroElement eRoot, final String sElementName)
  {
    for (final IMicroElement eChild : eRoot.getAllChildElements (sElementName))
    {
      final String sName = eChild.getAttributeValue (StatisticsExporter.ATTR_NAME);
      if (sName != null && sName.startsWith (PREFIX))
      {
        assertNotNull (eChild.getAttributeValue (StatisticsExporter.ATTR_INVOCATIONCOUNT));
        return;
      }
    }
    fail ("No exported element '" + sElementName + "' of this test was found");
  }

  @Test
  public void testGetRoot ()
  {
    final IMicroElement eRoot = new MicroElement ("statistics");
    final StatisticsVisitorCallbackToXML aCB = new StatisticsVisitorCallbackToXML (eRoot);
    assertSame (eRoot, aCB.getRoot ());
  }

  @Test
  public void testExportAllHandlerTypes ()
  {
    final IMicroDocument aDoc = StatisticsExporter.getAsXMLDocument ();
    assertNotNull (aDoc);
    final IMicroElement eRoot = aDoc.getDocumentElement ();
    assertNotNull (eRoot);

    _assertExported (eRoot, StatisticsExporter.ELEMENT_CACHE);
    _assertExported (eRoot, StatisticsExporter.ELEMENT_TIMER);
    _assertExported (eRoot, StatisticsExporter.ELEMENT_KEYEDTIMER);
    _assertExported (eRoot, StatisticsExporter.ELEMENT_SIZE);
    _assertExported (eRoot, StatisticsExporter.ELEMENT_KEYEDSIZE);
    _assertExported (eRoot, StatisticsExporter.ELEMENT_COUNTER);
    _assertExported (eRoot, StatisticsExporter.ELEMENT_KEYEDCOUNTER);
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      new StatisticsVisitorCallbackToXML (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testKeyedElementsPresent ()
  {
    final IMicroElement eRoot = StatisticsExporter.getAsXMLDocument ().getDocumentElement ();
    for (final IMicroElement eKeyed : eRoot.getAllChildElements (StatisticsExporter.ELEMENT_KEYEDCOUNTER))
    {
      final String sName = eKeyed.getAttributeValue (StatisticsExporter.ATTR_NAME);
      if (sName != null && sName.startsWith (PREFIX))
      {
        assertTrue (eKeyed.getAllChildElements (StatisticsExporter.ELEMENT_KEY).isNotEmpty ());
        return;
      }
    }
    fail ("No keyed counter of this test was found");
  }
}

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
package com.helger.http.header;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.helger.base.numeric.mutable.MutableInt;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.datetime.helper.PDTFactory;

/**
 * Additional test class for class {@link HttpHeaderMap}.
 *
 * @author Philip Helger
 */
public final class HttpHeaderMapExtTest
{
  @Test
  public void testDateHeaders ()
  {
    final HttpHeaderMap aMap = new HttpHeaderMap ();
    final ZonedDateTime aZDT = PDTFactory.getCurrentZonedDateTime ();
    final LocalDateTime aLDT = aZDT.toLocalDateTime ();
    final LocalDate aLD = aZDT.toLocalDate ();

    aMap.setDateHeader ("d1", aZDT);
    assertEquals (HttpHeaderMap.getDateTimeAsString (aZDT), aMap.getFirstHeaderValue ("d1"));
    aMap.setDateHeader ("d2", aLDT);
    assertEquals (HttpHeaderMap.getDateTimeAsString (aLDT), aMap.getFirstHeaderValue ("d2"));
    aMap.setDateHeader ("d3", aLD);
    assertNotNull (aMap.getFirstHeaderValue ("d3"));
    aMap.setDateHeader ("d4", System.currentTimeMillis ());
    assertNotNull (aMap.getFirstHeaderValue ("d4"));

    aMap.addDateHeader ("a1", aZDT);
    aMap.addDateHeader ("a1", aLDT);
    assertEquals (2, aMap.getAllHeaderValues ("a1").size ());
    aMap.addDateHeader ("a2", aLD);
    aMap.addDateHeader ("a2", System.currentTimeMillis ());
    assertEquals (2, aMap.getAllHeaderValues ("a2").size ());
  }

  @Test
  public void testNumericHeaders ()
  {
    final HttpHeaderMap aMap = new HttpHeaderMap ();
    aMap.setIntHeader ("i", 17);
    assertEquals ("17", aMap.getFirstHeaderValue ("i"));
    aMap.setLongHeader ("l", 4711L);
    assertEquals ("4711", aMap.getFirstHeaderValue ("l"));

    aMap.addIntHeader ("i2", 17);
    aMap.addIntHeader ("i2", 18);
    assertEquals (new CommonsArrayList <> ("17", "18"), aMap.getAllHeaderValues ("i2"));
    aMap.addLongHeader ("l2", 17L);
    aMap.addLongHeader ("l2", 18L);
    assertEquals (new CommonsArrayList <> ("17", "18"), aMap.getAllHeaderValues ("l2"));

    aMap.setContentLength (1234);
    assertNotNull (aMap.getFirstHeaderValue ("Content-Length"));
    aMap.setContentType ("text/plain");
    assertEquals ("text/plain", aMap.getFirstHeaderValue ("Content-Type"));
  }

  @Test
  public void testRemove ()
  {
    final HttpHeaderMap aMap = new HttpHeaderMap ();
    aMap.addHeader ("a", "1");
    aMap.addHeader ("a", "2");
    aMap.addHeader ("b", "3");
    assertEquals (2, aMap.size ());
    assertFalse (aMap.isEmpty ());
    assertTrue (aMap.containsHeaders ("a"));
    assertFalse (aMap.containsHeaders ("c"));
    assertFalse (aMap.containsHeaders (null));

    // Remove a single value
    assertTrue (aMap.removeHeader ("a", "1").isChanged ());
    assertFalse (aMap.removeHeader ("a", "1").isChanged ());
    assertFalse (aMap.removeHeader (null, "1").isChanged ());
    assertFalse (aMap.removeHeader ("a", null).isChanged ());
    assertEquals (new CommonsArrayList <> ("2"), aMap.getAllHeaderValues ("a"));

    // Remove all values of one header
    assertTrue (aMap.removeHeaders ("a").isChanged ());
    assertFalse (aMap.removeHeaders ("a").isChanged ());
    assertFalse (aMap.removeHeaders (null).isChanged ());

    // Remove by filter
    assertTrue (aMap.removeHeadersIf (x -> x.equals ("b")).isChanged ());
    assertFalse (aMap.removeHeadersIf (x -> x.equals ("b")).isChanged ());
    assertTrue (aMap.isEmpty ());

    aMap.addHeader ("a", "1");
    assertTrue (aMap.removeAll ().isChanged ());
    assertFalse (aMap.removeAll ().isChanged ());
  }

  @Test
  public void testGetters ()
  {
    final HttpHeaderMap aMap = new HttpHeaderMap ();
    aMap.addHeader ("a", "1");
    aMap.addHeader ("a", "2");

    assertEquals (1, aMap.getAllHeaders ().size ());
    assertEquals (1, aMap.getAllHeaderNames ().size ());
    assertEquals ("1", aMap.getFirstHeaderValue ("a"));
    assertNull (aMap.getFirstHeaderValue ("none"));
    assertNull (aMap.getFirstHeaderValue (null));
    assertEquals ("1, 2", aMap.getHeaderCombined ("a", ", "));
    assertNull (aMap.getHeaderCombined ("none", ", "));
    assertTrue (aMap.getAllHeaderValues ("none").isEmpty ());

    final Map <String, List <String>> aAsMap = aMap.getAsMapStringToListString ();
    assertEquals (1, aAsMap.size ());
    assertEquals (2, aAsMap.get ("a").size ());

    assertNotNull (aMap.iterator ());
    assertNotNull (aMap.toString ());
  }

  @Test
  public void testForEach ()
  {
    final HttpHeaderMap aMap = new HttpHeaderMap ();
    aMap.addHeader ("a", "1");
    aMap.addHeader ("a", "2");
    aMap.addHeader ("b", "line1\tline2");

    final MutableInt aCount = new MutableInt (0);
    aMap.forEachSingleHeader ((n, v) -> aCount.inc (), false);
    assertEquals (3, aCount.intValue ());

    aCount.set (0);
    aMap.forEachSingleHeader ((n, v) -> aCount.inc (), true, true);
    assertEquals (3, aCount.intValue ());

    final ICommonsList <String> aLines = new CommonsArrayList <> ();
    aMap.forEachHeaderLine (aLines::add, false);
    assertEquals (3, aLines.size ());
    assertEquals ("a: 1", aLines.getAtIndex (0));

    aLines.clear ();
    aMap.forEachHeaderLine (aLines::add, true, true);
    assertEquals (3, aLines.size ());

    assertEquals (3, aMap.getAllHeaderLines (false).size ());
    assertEquals (3, aMap.getAllHeaderLines (true, true).size ());
    // The unified value contains no tab any more
    assertFalse (aMap.getAllHeaderLines (true).getLastOrNull ().contains ("\t"));
  }

  @Test
  public void testCloneAndEquals ()
  {
    final HttpHeaderMap aMap = new HttpHeaderMap ();
    aMap.addHeader ("a", "1");

    final HttpHeaderMap aClone = aMap.getClone ();
    assertNotSame (aMap, aClone);
    assertEquals (aMap, aClone);
    assertEquals (aMap.hashCode (), aClone.hashCode ());
    assertEquals (aMap, new HttpHeaderMap (aMap));

    assertNotEquals (aMap, null);
    assertNotEquals (aMap, "any other type");
    assertNotEquals (aMap, new HttpHeaderMap ());

    // Modifying the clone does not modify the original
    aClone.addHeader ("b", "2");
    assertEquals (1, aMap.size ());
    assertEquals (2, aClone.size ());
  }
}

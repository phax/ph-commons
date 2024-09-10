/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.commons.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link HttpHeaderMap}.
 *
 * @author Philip Helger
 */
public final class HttpHeaderMapTest
{
  @Test
  public void testEmpty ()
  {
    final HttpHeaderMap h = new HttpHeaderMap ();
    assertTrue (h.isEmpty ());
    assertFalse (h.isNotEmpty ());
    assertEquals (0, h.size ());
    assertNotNull (h.getAllHeaderLines (true));
    assertTrue (h.getAllHeaderLines (true).isEmpty ());
    assertNotNull (h.getAllHeaderLines (false));
    assertTrue (h.getAllHeaderLines (false).isEmpty ());
    assertNotNull (h.getAllHeaderNames ());
    assertTrue (h.getAllHeaderNames ().isEmpty ());
    assertNotNull (h.getAllHeaders ());
    assertTrue (h.getAllHeaders ().isEmpty ());
    assertNotNull (h.getAllHeaderValues ("key1"));
    assertTrue (h.getAllHeaderValues ("key1").isEmpty ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (h, new HttpHeaderMap ());
    CommonsTestHelper.testGetClone (h);
  }

  @Test
  public void testSimpleValue ()
  {
    final HttpHeaderMap h = new HttpHeaderMap ();
    h.addHeader ("key1", "val1");
    assertFalse (h.isEmpty ());
    assertTrue (h.isNotEmpty ());
    assertEquals (1, h.size ());
    assertNotNull (h.getAllHeaderLines (true));
    assertEquals (1, h.getAllHeaderLines (true).size ());
    assertEquals ("key1: val1", h.getAllHeaderLines (true).getFirstOrNull ());
    assertNotNull (h.getAllHeaderLines (false));
    assertEquals (1, h.getAllHeaderLines (false).size ());
    assertEquals ("key1: val1", h.getAllHeaderLines (false).getFirstOrNull ());
    assertNotNull (h.getAllHeaderNames ());
    assertEquals (1, h.getAllHeaderNames ().size ());
    assertEquals ("key1", h.getAllHeaderNames ().getFirst ());
    assertNotNull (h.getAllHeaders ());
    assertEquals (1, h.getAllHeaders ().size ());
    assertEquals ("key1", h.getAllHeaders ().getFirstKey ());
    assertNotNull (h.getAllHeaderValues ("key1"));
    assertEquals (1, h.getAllHeaderValues ("key1").size ());
    assertEquals ("val1", h.getAllHeaderValues ("key1").getFirstOrNull ());

    CommonsTestHelper.testGetClone (h);
  }

  @Test
  public void testMultipleSimpleValues ()
  {
    final HttpHeaderMap h = new HttpHeaderMap ();
    h.addHeader ("key1", "val1");
    h.addHeader ("key2", "val2");
    h.addHeader ("key3", "val3");
    assertFalse (h.isEmpty ());
    assertTrue (h.isNotEmpty ());
    assertEquals (3, h.size ());
    assertNotNull (h.getAllHeaderLines (true));
    assertEquals (3, h.getAllHeaderLines (true).size ());
    assertEquals ("key1: val1", h.getAllHeaderLines (true).getFirstOrNull ());
    assertEquals ("key2: val2", h.getAllHeaderLines (true).get (1));
    assertEquals ("key3: val3", h.getAllHeaderLines (true).get (2));
    assertNotNull (h.getAllHeaderLines (false));
    assertEquals (3, h.getAllHeaderLines (false).size ());
    assertEquals ("key1: val1", h.getAllHeaderLines (false).getFirstOrNull ());
    assertEquals ("key2: val2", h.getAllHeaderLines (false).get (1));
    assertEquals ("key3: val3", h.getAllHeaderLines (false).get (2));
    assertNotNull (h.getAllHeaderNames ());
    assertEquals (3, h.getAllHeaderNames ().size ());
    assertTrue (h.getAllHeaderNames ().contains ("key1"));
    assertTrue (h.getAllHeaderNames ().contains ("key2"));
    assertTrue (h.getAllHeaderNames ().contains ("key3"));
    assertNotNull (h.getAllHeaders ());
    assertEquals (3, h.getAllHeaders ().size ());
    assertTrue (h.getAllHeaders ().containsKey ("key1"));
    assertTrue (h.getAllHeaders ().containsKey ("key2"));
    assertTrue (h.getAllHeaders ().containsKey ("key3"));

    assertNotNull (h.getAllHeaderValues ("key1"));
    assertEquals (1, h.getAllHeaderValues ("key1").size ());
    assertEquals ("val1", h.getAllHeaderValues ("key1").getFirstOrNull ());
    assertNotNull (h.getAllHeaderValues ("key2"));
    assertEquals (1, h.getAllHeaderValues ("key2").size ());
    assertEquals ("val2", h.getAllHeaderValues ("key2").getFirstOrNull ());
    assertNotNull (h.getAllHeaderValues ("key3"));
    assertEquals (1, h.getAllHeaderValues ("key3").size ());
    assertEquals ("val3", h.getAllHeaderValues ("key3").getFirstOrNull ());

    assertNotNull (h.getAllHeaderValues ("KEY1"));
    assertEquals (1, h.getAllHeaderValues ("kEy1").size ());
    assertEquals ("val1", h.getAllHeaderValues ("keY1").getFirstOrNull ());

    CommonsTestHelper.testGetClone (h);
  }

  @Test
  public void testMultiValue ()
  {
    final HttpHeaderMap h = new HttpHeaderMap ();
    h.addHeader ("key1", "val1");
    h.addHeader ("key1", "val2");
    h.addHeader ("key1", "val3");
    assertFalse (h.isEmpty ());
    assertTrue (h.isNotEmpty ());
    assertEquals (1, h.size ());
    assertNotNull (h.getAllHeaderLines (true));
    assertEquals (3, h.getAllHeaderLines (true).size ());
    assertEquals ("key1: val1", h.getAllHeaderLines (true).getFirstOrNull ());
    assertEquals ("key1: val2", h.getAllHeaderLines (true).get (1));
    assertEquals ("key1: val3", h.getAllHeaderLines (true).get (2));
    assertNotNull (h.getAllHeaderLines (false));
    assertEquals (3, h.getAllHeaderLines (false).size ());
    assertEquals ("key1: val1", h.getAllHeaderLines (false).getFirstOrNull ());
    assertEquals ("key1: val2", h.getAllHeaderLines (false).get (1));
    assertEquals ("key1: val3", h.getAllHeaderLines (false).get (2));
    assertNotNull (h.getAllHeaderNames ());
    assertEquals (1, h.getAllHeaderNames ().size ());
    assertEquals ("key1", h.getAllHeaderNames ().getFirst ());
    assertNotNull (h.getAllHeaders ());
    assertEquals (1, h.getAllHeaders ().size ());
    assertEquals ("key1", h.getAllHeaders ().getFirstKey ());
    assertNotNull (h.getAllHeaderValues ("key1"));
    assertEquals (3, h.getAllHeaderValues ("key1").size ());
    assertEquals ("val1", h.getAllHeaderValues ("key1").getFirstOrNull ());
    assertEquals ("val2", h.getAllHeaderValues ("key1").get (1));
    assertEquals ("val3", h.getAllHeaderValues ("key1").get (2));

    CommonsTestHelper.testGetClone (h);
  }

  @Test
  public void testAddAllHeadersEmpty ()
  {
    final HttpHeaderMap h = new HttpHeaderMap ();
    h.addHeader ("key1", "val1");
    h.addHeader ("key1", "val2");
    h.addHeader ("key1", "val3");

    final HttpHeaderMap h2 = new HttpHeaderMap ();
    h2.addAllHeaders (h);
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (h2, h);

    CommonsTestHelper.testGetClone (h);
    CommonsTestHelper.testGetClone (h2);
  }

  @Test
  public void testAddAllHeadersPrefilled ()
  {
    final HttpHeaderMap h = new HttpHeaderMap ();
    h.addHeader ("key1", "val1");
    h.addHeader ("KEY1", "val2");
    h.addHeader ("KEY1", "val3");

    final HttpHeaderMap h2 = new HttpHeaderMap ();
    h2.addIntHeader ("key2", 42);
    h2.addAllHeaders (h);
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (h2, h);

    assertEquals (4, h2.getAllHeaderLines (true).size ());
    assertEquals ("key2: 42", h2.getAllHeaderLines (true).get (0));
    assertEquals ("key1: val1", h2.getAllHeaderLines (true).get (1));
    assertEquals ("key1: val2", h2.getAllHeaderLines (true).get (2));
    assertEquals ("key1: val3", h2.getAllHeaderLines (true).get (3));

    assertEquals (4, h2.getAllHeaderLines (false).size ());
    assertEquals ("key2: 42", h2.getAllHeaderLines (false).get (0));
    assertEquals ("key1: val1", h2.getAllHeaderLines (false).get (1));
    assertEquals ("key1: val2", h2.getAllHeaderLines (false).get (2));
    assertEquals ("key1: val3", h2.getAllHeaderLines (false).get (3));

    CommonsTestHelper.testGetClone (h);
    CommonsTestHelper.testGetClone (h2);
  }

  @Test
  public void testAddAllHeadersPrefilledDifferentCasing ()
  {
    final HttpHeaderMap h = new HttpHeaderMap ();
    // First header defines the casing!
    h.addHeader ("Key1", "val1");
    h.addHeader ("KEY1", "val2");
    h.addHeader ("KEY1", "val3");

    final HttpHeaderMap h2 = new HttpHeaderMap ();
    h2.addIntHeader ("key2", 42);
    h2.addAllHeaders (h);
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (h2, h);

    assertEquals (4, h2.getAllHeaderLines (true).size ());
    assertEquals ("key2: 42", h2.getAllHeaderLines (true).get (0));
    assertEquals ("Key1: val1", h2.getAllHeaderLines (true).get (1));
    assertEquals ("Key1: val2", h2.getAllHeaderLines (true).get (2));
    assertEquals ("Key1: val3", h2.getAllHeaderLines (true).get (3));

    assertEquals (4, h2.getAllHeaderLines (false).size ());
    assertEquals ("key2: 42", h2.getAllHeaderLines (false).get (0));
    assertEquals ("Key1: val1", h2.getAllHeaderLines (false).get (1));
    assertEquals ("Key1: val2", h2.getAllHeaderLines (false).get (2));
    assertEquals ("Key1: val3", h2.getAllHeaderLines (false).get (3));

    CommonsTestHelper.testGetClone (h);
    CommonsTestHelper.testGetClone (h2);
  }

  @Test
  public void testAddAllHeadersPrefilledSameName ()
  {
    final HttpHeaderMap h = new HttpHeaderMap ();
    h.addHeader ("key1", "val1");
    h.addHeader ("key1", "val2");
    h.addHeader ("key1", "val3");

    final HttpHeaderMap h2 = new HttpHeaderMap ();
    h2.addIntHeader ("key1", 42);
    h2.addAllHeaders (h);
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (h2, h);

    assertEquals (4, h2.getAllHeaderLines (true).size ());
    assertEquals ("key1: 42", h2.getAllHeaderLines (true).get (0));
    assertEquals ("key1: val1", h2.getAllHeaderLines (true).get (1));
    assertEquals ("key1: val2", h2.getAllHeaderLines (true).get (2));
    assertEquals ("key1: val3", h2.getAllHeaderLines (true).get (3));

    CommonsTestHelper.testGetClone (h);
    CommonsTestHelper.testGetClone (h2);
  }

  @Test
  public void testSetAllHeadersEmpty ()
  {
    final HttpHeaderMap h = new HttpHeaderMap ();
    h.addHeader ("key1", "val1");
    h.addHeader ("key1", "val2");
    h.addHeader ("key1", "val3");

    final HttpHeaderMap h2 = new HttpHeaderMap ();
    h2.setAllHeaders (h);
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (h2, h);

    CommonsTestHelper.testGetClone (h);
    CommonsTestHelper.testGetClone (h2);
  }

  @Test
  public void testSetAllHeadersPrefilled ()
  {
    final HttpHeaderMap h = new HttpHeaderMap ();
    h.addHeader ("key1", "val1");
    h.addHeader ("key1", "val2");
    h.addHeader ("KEY1", "val3");

    final HttpHeaderMap h2 = new HttpHeaderMap ();
    h2.addIntHeader ("key2", 42);
    h2.setAllHeaders (h);
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (h2, h);

    assertEquals (4, h2.getAllHeaderLines (true).size ());
    assertEquals ("key2: 42", h2.getAllHeaderLines (true).get (0));
    assertEquals ("key1: val1", h2.getAllHeaderLines (true).get (1));
    assertEquals ("key1: val2", h2.getAllHeaderLines (true).get (2));
    assertEquals ("key1: val3", h2.getAllHeaderLines (true).get (3));

    CommonsTestHelper.testGetClone (h);
    CommonsTestHelper.testGetClone (h2);
  }

  @Test
  public void testSetAllHeadersPrefilledSameName ()
  {
    final HttpHeaderMap h = new HttpHeaderMap ();
    h.addHeader ("key1", "val1");
    h.addHeader ("Key1", "val2");
    h.addHeader ("key1", "val3");

    final HttpHeaderMap h2 = new HttpHeaderMap ();
    h2.addIntHeader ("Key1", 42);
    h2.setAllHeaders (h);
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (h2, h);

    assertEquals (3, h2.getAllHeaderLines (true).size ());
    assertEquals ("key1: val1", h2.getAllHeaderLines (true).get (0));
    assertEquals ("key1: val2", h2.getAllHeaderLines (true).get (1));
    assertEquals ("key1: val3", h2.getAllHeaderLines (true).get (2));

    CommonsTestHelper.testGetClone (h);
    CommonsTestHelper.testGetClone (h2);
  }

  @Test
  public void testGetUnifiedValues ()
  {
    assertEquals ("", HttpHeaderMap.getUnifiedValue ("", false));
    assertEquals ("\"\"", HttpHeaderMap.getUnifiedValue ("", true));

    assertEquals ("abc", HttpHeaderMap.getUnifiedValue ("abc", false));
    assertEquals ("abc", HttpHeaderMap.getUnifiedValue ("abc", true));

    assertEquals (" ", HttpHeaderMap.getUnifiedValue (" ", false));
    assertEquals ("\" \"", HttpHeaderMap.getUnifiedValue (" ", true));

    assertEquals ("line1 line2", HttpHeaderMap.getUnifiedValue ("line1\tline2", false));
    assertEquals ("\"line1 line2\"", HttpHeaderMap.getUnifiedValue ("line1\tline2", true));

    assertEquals ("line1 line2", HttpHeaderMap.getUnifiedValue ("line1\rline2", false));
    assertEquals ("\"line1 line2\"", HttpHeaderMap.getUnifiedValue ("line1\rline2", true));

    assertEquals ("line1 line2", HttpHeaderMap.getUnifiedValue ("line1\nline2", false));
    assertEquals ("\"line1 line2\"", HttpHeaderMap.getUnifiedValue ("line1\nline2", true));

    assertEquals ("line1 line2", HttpHeaderMap.getUnifiedValue ("line1\r\nline2", false));
    assertEquals ("\"line1 line2\"", HttpHeaderMap.getUnifiedValue ("line1\r\nline2", true));

    assertEquals ("line1 line2", HttpHeaderMap.getUnifiedValue ("line1\r\n\r\r\n\n\n\t\t\tline2", false));
    assertEquals ("\"line1 line2\"", HttpHeaderMap.getUnifiedValue ("line1\r\n\r\r\n\n\n\t\t\tline2", true));

    assertEquals ("line1 line2", HttpHeaderMap.getUnifiedValue ("line1\r\n\r\r  \n\n\n  \t\t  \tline2", false));
    assertEquals ("\"line1 line2\"", HttpHeaderMap.getUnifiedValue ("line1\r\n \r\r\n\n \n\t \t\tline2", true));

    // Already quoted
    assertEquals ("\"ab cd\"", HttpHeaderMap.getUnifiedValue ("\"ab cd\"", true));
  }
}

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
package com.helger.commons.aggregate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.ext.CommonsArrayList;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link IAggregator}.
 *
 * @author Philip Helger
 */
public final class IAggregatorTest
{
  @Test
  public void testNull ()
  {
    final IAggregator <String, String> a1 = IAggregator.createNull ();
    assertNull (a1.apply (CollectionHelper.newList ("a", "b")));
    assertNull (a1.apply (new CommonsArrayList <String> ()));
  }

  @Test
  public void testConstant ()
  {
    final IAggregator <String, String> a1 = IAggregator.createConstant ("foo");
    assertEquals ("foo", a1.apply (CollectionHelper.newList ("a", "b")));
    assertEquals ("foo", a1.apply (new CommonsArrayList <String> ()));
  }

  @Test
  public void testUseAll ()
  {
    final IAggregator <String, Collection <String>> a1 = IAggregator.createUseAll ();
    final IAggregator <String, Collection <String>> a2 = IAggregator.createUseAll ();
    assertEquals (a1, a1);
    assertFalse (a1.equals (null));
    assertFalse (a1.equals ("any other"));
    assertEquals (a1.hashCode (), a1.hashCode ());
    assertFalse (a1.hashCode () == 0);
    assertFalse (a1.hashCode () == "any other".hashCode ());
    assertNotNull (a1.toString ());
    final List <String> l = CollectionHelper.newList ("a", null, "b", "", "c");
    assertEquals (l, a1.apply (l));
    assertEquals (l, a2.apply (l));
  }

  @Test
  public void testUseFirst ()
  {
    final IAggregator <String, String> a1 = IAggregator.createUseFirst ();
    assertEquals (a1, a1);
    assertFalse (a1.equals (null));
    assertFalse (a1.equals ("any other"));
    assertEquals (a1.hashCode (), a1.hashCode ());
    assertFalse (a1.hashCode () == 0);
    assertFalse (a1.hashCode () == "any other".hashCode ());
    assertNotNull (a1.toString ());
    final List <String> l = CollectionHelper.newList ("a", null, "b", "", "c");
    assertEquals ("a", a1.apply (l));
    assertNull (a1.apply (new ArrayList <String> ()));
    assertNull (a1.apply ((Collection <String>) null));
  }

  @Test
  public void testUseLast ()
  {
    final IAggregator <String, String> a1 = IAggregator.createUseLast ();
    assertEquals (a1, a1);
    assertFalse (a1.equals (null));
    assertFalse (a1.equals ("any other"));
    assertEquals (a1.hashCode (), a1.hashCode ());
    assertFalse (a1.hashCode () == 0);
    assertFalse (a1.hashCode () == "any other".hashCode ());
    assertNotNull (a1.toString ());
    final List <String> l = CollectionHelper.newList ("a", null, "b", "", "c");
    assertEquals ("c", a1.apply (l));
    assertNull (a1.apply (new ArrayList <String> ()));
    assertNull (a1.apply ((Collection <String>) null));
  }

  @Test
  public void testGetStringCombinator ()
  {
    final IAggregator <String, String> c = IAggregator.createStringAll ();
    assertEquals ("ab", c.apply ("a", "b"));
    assertEquals ("anull", c.apply ("a", null));
    assertEquals ("nullb", c.apply (null, "b"));
    assertEquals ("nullnull", c.apply (null, null));
    assertEquals ("", c.apply ("", ""));
  }

  @Test
  public void testGetStringCombinatorWithSeparatorChar ()
  {
    final IAggregator <String, String> c = IAggregator.createStringAll (',');
    assertEquals ("a,b", c.apply ("a", "b"));
    assertEquals ("a,null", c.apply ("a", null));
    assertEquals ("null,b", c.apply (null, "b"));
    assertEquals ("null,null", c.apply (null, null));
    assertEquals (",", c.apply ("", ""));
  }

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testGetStringCombinatorWithSeparatorString ()
  {
    final IAggregator <String, String> c = IAggregator.createStringAll (";");
    assertEquals ("a;b", c.apply ("a", "b"));
    assertEquals ("a;null", c.apply ("a", null));
    assertEquals ("null;b", c.apply (null, "b"));
    assertEquals ("null;null", c.apply (null, null));
    assertEquals (";", c.apply ("", ""));

    try
    {
      // null separator not allowed
      IAggregator.createStringAll (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetStringCombinatorIgnoreNull ()
  {
    final IAggregator <String, String> c = IAggregator.createStringIgnoreEmpty ();
    assertEquals ("ab", c.apply ("a", "b"));
    assertEquals ("a", c.apply ("a", null));
    assertEquals ("b", c.apply (null, "b"));
    assertEquals ("", c.apply (null, null));
    assertEquals ("", c.apply ("", ""));
    assertNotNull (c.toString ());
  }

  @Test
  public void testGetStringCombinatorWithSeparatorIgnoreEmptyChar ()
  {
    final IAggregator <String, String> c = IAggregator.createStringIgnoreEmpty (',');
    assertEquals ("a,b", c.apply ("a", "b"));
    assertEquals ("a", c.apply ("a", null));
    assertEquals ("b", c.apply (null, "b"));
    assertEquals ("", c.apply (null, null));
    assertEquals ("", c.apply ("", ""));
  }

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testGetStringCombinatorWithSeparatorIgnoreEmptyString ()
  {
    final IAggregator <String, String> c = IAggregator.createStringIgnoreEmpty (";");
    assertEquals ("a;b", c.apply ("a", "b"));
    assertEquals ("a", c.apply ("a", null));
    assertEquals ("b", c.apply (null, "b"));
    assertEquals ("", c.apply (null, null));
    assertEquals ("", c.apply ("", ""));

    try
    {
      // null separator not allowed
      IAggregator.createStringIgnoreEmpty (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}

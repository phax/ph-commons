/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Test;

import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link IAggregator}.
 *
 * @author Philip Helger
 */
public final class IAggregatorTest
{
  @Test
  public void testUseFirst ()
  {
    final IAggregator <String, String> a1 = IAggregator.createUseFirst ();
    assertEquals (a1, a1);
    assertNotEquals (a1, null);
    assertNotEquals (a1, "any other");
    assertEquals (a1.hashCode (), a1.hashCode ());
    assertNotEquals (a1.hashCode (), 0);
    assertNotEquals (a1.hashCode (), "any other".hashCode ());
    assertNotNull (a1.toString ());
    final ICommonsList <String> l = new CommonsArrayList<> ("a", null, "b", "", "c");
    assertEquals ("a", a1.apply (l));
    assertNull (a1.apply (new CommonsArrayList <String> ()));
    assertNull (a1.apply ((Collection <String>) null));
  }

  @Test
  public void testUseLast ()
  {
    final IAggregator <String, String> a1 = IAggregator.createUseLast ();
    assertEquals (a1, a1);
    assertNotEquals (a1, null);
    assertNotEquals (a1, "any other");
    assertEquals (a1.hashCode (), a1.hashCode ());
    assertNotEquals (a1.hashCode (), 0);
    assertNotEquals (a1.hashCode (), "any other".hashCode ());
    assertNotNull (a1.toString ());
    final ICommonsList <String> l = new CommonsArrayList<> ("a", null, "b", "", "c");
    assertEquals ("c", a1.apply (l));
    assertNull (a1.apply (new CommonsArrayList <String> ()));
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

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
package com.helger.commons.aggregate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collection;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.string.StringHelper;

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
    final IAggregator <String, String> a1 = CollectionHelper::getFirstElement;
    assertEquals (a1, a1);
    assertNotEquals (a1, null);
    assertNotEquals (a1, "any other");
    assertEquals (a1.hashCode (), a1.hashCode ());
    assertNotEquals (a1.hashCode (), 0);
    assertNotEquals (a1.hashCode (), "any other".hashCode ());
    assertNotNull (a1.toString ());
    final ICommonsList <String> l = new CommonsArrayList <> ("a", null, "b", "", "c");
    assertEquals ("a", a1.apply (l));
    assertNull (a1.apply (new CommonsArrayList <String> ()));
    assertNull (a1.apply ((Collection <String>) null));
  }

  @Test
  public void testUseLast ()
  {
    final IAggregator <String, String> a1 = CollectionHelper::getLastElement;
    assertEquals (a1, a1);
    assertNotEquals (a1, null);
    assertNotEquals (a1, "any other");
    assertEquals (a1.hashCode (), a1.hashCode ());
    assertNotEquals (a1.hashCode (), 0);
    assertNotEquals (a1.hashCode (), "any other".hashCode ());
    assertNotNull (a1.toString ());
    final ICommonsList <String> l = new CommonsArrayList <> ("a", null, "b", "", "c");
    assertEquals ("c", a1.apply (l));
    assertNull (a1.apply (new CommonsArrayList <String> ()));
    assertNull (a1.apply ((Collection <String>) null));
  }

  @Test
  public void testGetStringCombinator ()
  {
    final IAggregator <String, String> c = StringHelper::getImploded;
    assertEquals ("ab", c.apply ("a", "b"));
    assertEquals ("anull", c.apply ("a", null));
    assertEquals ("nullb", c.apply (null, "b"));
    assertEquals ("nullnull", c.apply (null, null));
    assertEquals ("", c.apply ("", ""));
  }

  @Test
  public void testGetStringCombinatorWithSeparatorChar ()
  {
    final IAggregator <String, String> c = x -> StringHelper.getImploded (',', x);
    assertEquals ("a,b", c.apply ("a", "b"));
    assertEquals ("a,null", c.apply ("a", null));
    assertEquals ("null,b", c.apply (null, "b"));
    assertEquals ("null,null", c.apply (null, null));
    assertEquals (",", c.apply ("", ""));
  }

  @Test
  public void testGetStringCombinatorWithSeparatorString ()
  {
    final IAggregator <String, String> c = x -> StringHelper.getImploded (";", x);
    assertEquals ("a;b", c.apply ("a", "b"));
    assertEquals ("a;null", c.apply ("a", null));
    assertEquals ("null;b", c.apply (null, "b"));
    assertEquals ("null;null", c.apply (null, null));
    assertEquals (";", c.apply ("", ""));
  }

  @Test
  public void testGetStringCombinatorIgnoreNull ()
  {
    final IAggregator <String, String> c = StringHelper::getImplodedNonEmpty;
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
    final IAggregator <String, String> c = x -> StringHelper.getImplodedNonEmpty (',', x);
    assertEquals ("a,b", c.apply ("a", "b"));
    assertEquals ("a", c.apply ("a", null));
    assertEquals ("b", c.apply (null, "b"));
    assertEquals ("", c.apply (null, null));
    assertEquals ("", c.apply ("", ""));
  }

  @Test
  public void testGetStringCombinatorWithSeparatorIgnoreEmptyString ()
  {
    final IAggregator <String, String> c = x -> StringHelper.getImplodedNonEmpty (";", x);
    assertEquals ("a;b", c.apply ("a", "b"));
    assertEquals ("a", c.apply ("a", null));
    assertEquals ("b", c.apply (null, "b"));
    assertEquals ("", c.apply (null, null));
    assertEquals ("", c.apply ("", ""));
  }
}

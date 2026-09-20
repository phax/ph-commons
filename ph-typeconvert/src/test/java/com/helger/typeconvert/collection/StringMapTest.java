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
package com.helger.typeconvert.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.helger.base.mock.CommonsAssert;
import com.helger.collection.commons.CommonsLinkedHashMap;

/**
 * Test class for class {@link StringMap}.
 *
 * @author Philip Helger
 */
public final class StringMapTest
{
  @Test
  public void testCtor ()
  {
    final StringMap aEmpty = new StringMap ();
    assertTrue (aEmpty.isEmpty ());

    final Map <String, String> aSrc = new CommonsLinkedHashMap <> ();
    aSrc.put ("a", "b");
    final StringMap aFromMap = new StringMap (aSrc);
    assertEquals (1, aFromMap.size ());
    assertEquals ("b", aFromMap.getValue ("a"));

    final StringMap aSingle = new StringMap ("a", "b");
    assertEquals (1, aSingle.size ());
    assertEquals ("b", aSingle.getValue ("a"));

    final StringMap aClone = aSingle.getClone ();
    assertNotSame (aSingle, aClone);
    assertEquals (aSingle, aClone);
  }

  @Test
  public void testAdd ()
  {
    final StringMap x = new StringMap ();
    assertTrue (x.add ("s", "a") == x);
    assertEquals ("a", x.getValue ("s"));

    // Object value - converted to String
    x.add ("o", Integer.valueOf (17));
    assertEquals ("17", x.getValue ("o"));

    x.add ("b", true);
    assertEquals ("true", x.getValue ("b"));
    x.add ("i", 5);
    assertEquals ("5", x.getValue ("i"));
    x.add ("l", 6L);
    assertEquals ("6", x.getValue ("l"));

    x.addWithoutValue ("e");
    assertEquals ("", x.getValue ("e"));
  }

  @Test
  public void testAddIfNotNull ()
  {
    final StringMap x = new StringMap ();
    x.addIfNotNull ("s", (String) null);
    assertFalse (x.containsKey ("s"));
    x.addIfNotNull ("s", "a");
    assertEquals ("a", x.getValue ("s"));

    x.addIfNotNull ("o", (Object) null);
    assertFalse (x.containsKey ("o"));
    x.addIfNotNull ("o", Integer.valueOf (17));
    assertEquals ("17", x.getValue ("o"));

    x.addIf ("f", "value", z -> false);
    assertFalse (x.containsKey ("f"));
    x.addIf ("t", "value", z -> true);
    assertEquals ("value", x.getValue ("t"));
  }

  @Test
  public void testPutInPrimitives ()
  {
    final IStringMap x = new StringMap ();
    assertTrue (x.putIn ("b", true).isChanged ());
    assertEquals ("true", x.getValue ("b"));
    assertFalse (x.putIn ("b", true).isChanged ());

    assertTrue (x.putIn ("i", 5).isChanged ());
    assertEquals ("5", x.getValue ("i"));
    assertTrue (x.putIn ("l", 6L).isChanged ());
    assertEquals ("6", x.getValue ("l"));
    assertTrue (x.putIn ("sh", (short) 7).isChanged ());
    assertEquals ("7", x.getValue ("sh"));
    assertTrue (x.putIn ("d", 1.5d).isChanged ());
    assertEquals ("1.5", x.getValue ("d"));
    assertTrue (x.putIn ("f", 2.5f).isChanged ());
    assertEquals ("2.5", x.getValue ("f"));
  }

  @Test
  public void testGetters ()
  {
    final StringMap x = new StringMap ();
    x.add ("i", 5).add ("l", 6L).add ("d", "1.5").add ("f", "2.5").add ("b", true);

    assertEquals (5, x.getAsInt ("i"));
    assertEquals (6, x.getAsLong ("l"));
    CommonsAssert.assertEquals (1.5, x.getAsDouble ("d"));
    CommonsAssert.assertEquals (2.5f, x.getAsFloat ("f"));
    assertTrue (x.getAsBoolean ("b"));
    assertFalse (x.getAsBoolean ("i"));
    assertNull (x.getValue ("none"));

    final IStringMap aClone = x.getClone ();
    assertEquals (x, aClone);
    assertEquals (x.hashCode (), aClone.hashCode ());
    assertEquals (x.toString (), aClone.toString ());
  }
}

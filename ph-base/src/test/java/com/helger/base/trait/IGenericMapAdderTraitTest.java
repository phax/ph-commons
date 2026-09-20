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
package com.helger.base.trait;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;

/**
 * Test class for the default methods of {@link IGenericMapAdderTrait}.
 *
 * @author Philip Helger
 */
public final class IGenericMapAdderTraitTest
{
  private static Map <String, String> _map (final String... aKeyValues)
  {
    final Map <String, String> ret = new LinkedHashMap <> ();
    for (int i = 0; i < aKeyValues.length; i += 2)
      ret.put (aKeyValues[i], aKeyValues[i + 1]);
    return ret;
  }

  @Test
  public void testAddPrimitives ()
  {
    final MockMapAdderTrait a = new MockMapAdderTrait ();
    assertSame (a, a.add ("b", true));
    a.add ("c", 'x');
    a.add ("d", 1.5d);
    a.add ("i", 3);
    a.add ("l", 4L);

    assertEquals (_map ("b", "true", "c", "x", "d", "1.5", "i", "3", "l", "4"), a.getAllAsString ());
  }

  @Test
  public void testAddObject ()
  {
    final MockMapAdderTrait a = new MockMapAdderTrait ();
    a.add ("o", (Object) "any");
    a.add ("n", (Object) null);
    assertEquals (_map ("o", "any", "n", null), a.getAllAsString ());
  }

  @Test
  public void testAddIf ()
  {
    final MockMapAdderTrait a = new MockMapAdderTrait ();
    a.addIf ("yes", new MockStringValue ("v1"), x -> true);
    a.addIf ("no", new MockStringValue ("v2"), x -> false);
    assertEquals (1, a.size ());
    assertTrue (a.containsKey ("yes"));

    // The Object based overloads
    a.addIf ("obj-yes", (Object) "v3", x -> true);
    a.addIf ("obj-no", (Object) "v4", x -> false);
    assertTrue (a.containsKey ("obj-yes"));
    assertFalse (a.containsKey ("obj-no"));

    // The generic typed overload
    a.addIf ("t-yes", Integer.valueOf (1), x -> true);
    a.addIf ("t-no", Integer.valueOf (2), x -> false);
    assertTrue (a.containsKey ("t-yes"));
    assertFalse (a.containsKey ("t-no"));
  }

  @Test
  public void testAddIfNotNullAndNotEmpty ()
  {
    final MockMapAdderTrait a = new MockMapAdderTrait ();
    a.addIfNotNull ("v", new MockStringValue ("value"));
    a.addIfNotNull ("n", (MockStringValue) null);
    a.addIfNotNull ("o", (Object) "obj");
    a.addIfNotNull ("on", (Object) null);
    assertEquals (_map ("v", "value", "o", "obj"), a.getAllAsString ());

    a.addIfNotEmpty ("ne", "text");
    a.addIfNotEmpty ("e", "");
    a.addIfNotEmpty ("nu", null);
    assertTrue (a.containsKey ("ne"));
    assertFalse (a.containsKey ("e"));
    assertFalse (a.containsKey ("nu"));
  }

  @Test
  public void testAddMapEntry ()
  {
    final MockMapAdderTrait a = new MockMapAdderTrait ();
    for (final Map.Entry <String, String> aEntry : _map ("k", "v").entrySet ())
      a.add (aEntry);
    assertEquals (_map ("k", "v"), a.getAllAsString ());
  }

  @Test
  public void testAddAll ()
  {
    final Map <String, MockStringValue> aSrc = new LinkedHashMap <> ();
    aSrc.put ("a", new MockStringValue ("1"));
    aSrc.put ("b", new MockStringValue ("2"));

    final MockMapAdderTrait a = new MockMapAdderTrait ();
    a.addAll (aSrc);
    a.addAll ((Map <String, MockStringValue>) null);
    assertEquals (_map ("a", "1", "b", "2"), a.getAllAsString ());

    final MockMapAdderTrait a2 = new MockMapAdderTrait ();
    a2.addAll (aSrc.entrySet ());
    a2.addAll ((Iterable <Map.Entry <String, MockStringValue>>) null);
    assertEquals (_map ("a", "1", "b", "2"), a2.getAllAsString ());
  }

  @Test
  public void testAddAllAny ()
  {
    final MockMapAdderTrait a = new MockMapAdderTrait ();
    a.addAllAny (_map ("a", "1", "b", "2"));
    a.addAllAny ((Map <String, Object>) null);
    assertEquals (_map ("a", "1", "b", "2"), a.getAllAsString ());

    final MockMapAdderTrait a2 = new MockMapAdderTrait ();
    final List <Map.Entry <String, ?>> aEntries = new ArrayList <> ();
    aEntries.add (new java.util.AbstractMap.SimpleEntry <> ("c", "3"));
    a2.addAllAny (aEntries);
    a2.addAllAny ((Iterable <Map.Entry <String, ?>>) null);
    assertEquals (_map ("c", "3"), a2.getAllAsString ());
  }

  @Test
  public void testAddAllMapped ()
  {
    final MockMapAdderTrait a = new MockMapAdderTrait ();
    a.addAllMapped (_map ("a", "x"), x -> new MockStringValue (x.toUpperCase (Locale.ROOT)));
    a.addAllMapped ((Map <String, String>) null, x -> new MockStringValue (x));
    assertEquals (_map ("a", "X"), a.getAllAsString ());

    final MockMapAdderTrait a2 = new MockMapAdderTrait ();
    a2.addAllMapped (_map ("b", "y"),
                     x -> x.toUpperCase (Locale.ROOT),
                     x -> new MockStringValue (x.toUpperCase (Locale.ROOT)));
    a2.addAllMapped ((Map <String, String>) null, x -> x, x -> new MockStringValue (x));
    assertEquals (_map ("B", "Y"), a2.getAllAsString ());
  }
}

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
package com.helger.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

import com.helger.base.state.EChange;
import com.helger.collection.commons.ICommonsList;

/**
 * Test class for class {@link JsonObject}.
 *
 * @author Philip Helger
 */
public final class JsonObjectTest
{
  @Test
  public void testContains ()
  {
    final JsonObject aObject = new JsonObject ();
    assertFalse (aObject.containsValue (5));
    assertFalse (aObject.containsValue (3.1234));
    assertFalse (aObject.containsValue ((IJson) null));
    assertNull (aObject.get ("key1"));
    assertNull (aObject.getAsValue ("key1"));
    assertNull (aObject.getAsObject ("key1"));
    assertNull (aObject.getAsArray ("key1"));

    aObject.add ("key1", 5);
    assertTrue (aObject.containsValue (5));
    assertFalse (aObject.containsValue (3.1234));
    assertFalse (aObject.containsValue ((IJson) null));
    assertTrue (aObject.containsValue (5L));
    assertTrue (aObject.containsValue ((byte) 5));
    assertTrue (aObject.containsValue ((short) 5));
    assertFalse (aObject.containsValue ((char) 5));
    assertEquals (5, aObject.getAsInt ("key1"));
    assertEquals (5, aObject.getAsValue ("key1").getAsInt ());

    aObject.add ("key2", 3.1234);
    assertTrue (aObject.containsValue (5));
    assertTrue (aObject.containsValue (3.1234));
    assertFalse (aObject.containsValue ((IJson) null));

    aObject.add ("key3", "This is a string");
    assertTrue (aObject.containsValue (5));
    assertTrue (aObject.containsValue (3.1234));
    assertTrue (aObject.containsValue ("This is a string"));
    assertFalse (aObject.containsValue ((IJson) null));

    aObject.add ("key4", new JsonArray ().add ("nested").add (0).add (BigDecimal.valueOf (12.34)));
    assertTrue (aObject.containsValue (5));
    assertTrue (aObject.containsValue (3.1234));
    assertTrue (aObject.containsValue ("This is a string"));
    assertTrue (aObject.containsValue (new JsonArray ().add ("nested").add (0).add (BigDecimal.valueOf (12.34))));
    assertFalse (aObject.containsValue ((IJson) null));
    assertNull (aObject.getAsValue ("key4"));
    assertNotNull (aObject.getAsArray ("key4"));
    assertNull (aObject.getAsObject ("key4"));

    aObject.add ("key5", new JsonObject ().add ("n1", "nested").add ("n2", 0).add ("n3", BigDecimal.valueOf (12.34)));
    assertTrue (aObject.containsValue (5));
    assertTrue (aObject.containsValue (3.1234));
    assertTrue (aObject.containsValue ("This is a string"));
    assertTrue (aObject.containsValue (new JsonArray ().add ("nested").add (0).add (BigDecimal.valueOf (12.34))));
    assertTrue (aObject.containsValue (new JsonObject ().add ("n1", "nested")
                                                        .add ("n2", 0)
                                                        .add ("n3", BigDecimal.valueOf (12.34))));
    assertFalse (aObject.containsValue ((IJson) null));
    assertNull (aObject.getAsValue ("key5"));
    assertNull (aObject.getAsArray ("key5"));
    assertNotNull (aObject.getAsObject ("key5"));

    aObject.add ("key6", 'c');
    assertTrue (aObject.containsValue ('c'));
    assertTrue (aObject.containsValue ("c"));

    aObject.add ("key7", 1.2f);
    assertTrue (aObject.containsValue (1.2f));

    assertFalse (aObject.containsKey ("key8"));
    aObject.addIfNotEmpty ("key8", null);
    assertFalse (aObject.containsKey ("key8"));
    aObject.addIfNotEmpty ("key8", "");
    assertFalse (aObject.containsKey ("key8"));
    aObject.addIfNotEmpty ("key8", "something");
    assertTrue (aObject.containsKey ("key8"));
  }

  @Test
  public void testGetAsObject ()
  {
    assertNull (new JsonArray ().getAsObject ());
    assertNotNull (new JsonObject ().getAsObject ());
    assertNull (JsonValue.create (true).getAsObject ());
  }

  @Test
  public void testReplaceKey ()
  {
    final JsonObject aObject = new JsonObject ();
    aObject.add ("k1", 1);
    aObject.add ("k2", new JsonObject ().add ("nested1", "v1").add ("nested2", 42));
    aObject.add ("k3", 3);

    // Sanity check on initial key order
    final ICommonsList <String> aExpectedOrder = aObject.keySet ().getCopyAsList ();
    assertEquals (3, aExpectedOrder.size ());
    assertEquals ("k1", aExpectedOrder.get (0));
    assertEquals ("k2", aExpectedOrder.get (1));
    assertEquals ("k3", aExpectedOrder.get (2));
    assertNotNull (aObject.getAsObject ("k2"));

    // Replace an existing object value with a plain value -> original key order is kept
    assertEquals (EChange.CHANGED, aObject.replaceKey ("k2", JsonValue.create ("plain")));
    assertEquals ("plain", aObject.getAsString ("k2"));
    assertNull (aObject.getAsObject ("k2"));

    final ICommonsList <String> aOrderAfter = aObject.keySet ().getCopyAsList ();
    assertEquals (3, aOrderAfter.size ());
    assertEquals ("k1", aOrderAfter.get (0));
    assertEquals ("k2", aOrderAfter.get (1));
    assertEquals ("k3", aOrderAfter.get (2));

    // Replace using the Object overload (uses the registered type converter)
    assertEquals (EChange.CHANGED, aObject.replaceKey ("k1", Integer.valueOf (99)));
    assertEquals (99, aObject.getAsInt ("k1"));
    assertEquals ("k1", aObject.keySet ().getCopyAsList ().get (0));

    // Replace a value with an array (object -> array swap)
    aObject.add ("k4", new JsonObject ().add ("x", 1));
    final JsonArray aArr = new JsonArray ();
    aArr.add ("a").add ("b");
    assertEquals (EChange.CHANGED, aObject.replaceKey ("k4", aArr));
    assertSame (aArr, aObject.get ("k4"));
    assertNotNull (aObject.getAsArray ("k4"));
    assertNull (aObject.getAsObject ("k4"));

    // Replace on a non-existing key must not insert anything and must report UNCHANGED
    final int nSizeBefore = aObject.size ();
    assertEquals (EChange.UNCHANGED, aObject.replaceKey ("doesNotExist", JsonValue.create ("x")));
    assertFalse (aObject.containsKey ("doesNotExist"));
    assertEquals (nSizeBefore, aObject.size ());

    // null key behaves like any other lookup -> no entry, no change
    assertEquals (EChange.UNCHANGED, aObject.replaceKey (null, JsonValue.create ("x")));
    assertFalse (aObject.containsKey (null));
    assertEquals (nSizeBefore, aObject.size ());

    // Object overload with null value goes through the type converter (results in a null JSON value)
    assertEquals (EChange.CHANGED, aObject.replaceKey ("k3", (Object) null));
    assertEquals ("k3", aObject.keySet ().getCopyAsList ().get (2));
  }

  @Test
  public void testReplaceKeyWithFunction ()
  {
    final JsonObject aObject = new JsonObject ();
    aObject.add ("counter", 1);
    aObject.add ("name", "Alice");
    aObject.add ("nested", new JsonObject ().add ("x", 10));

    // Modify an existing primitive value via the function (increment) - position is preserved
    assertEquals (EChange.CHANGED,
                  aObject.replaceKey ("counter", aOld -> JsonValue.create (aOld.getAsValue ().getAsInt () + 41)));
    assertEquals (42, aObject.getAsInt ("counter"));
    assertEquals ("counter", aObject.keySet ().getCopyAsList ().get (0));

    // Transform an existing object value into a plain value - position is preserved
    assertEquals (EChange.CHANGED, aObject.replaceKey ("nested", aOld -> JsonValue.create ("flattened")));
    assertEquals ("flattened", aObject.getAsString ("nested"));
    assertNull (aObject.getAsObject ("nested"));
    assertEquals ("nested", aObject.keySet ().getCopyAsList ().get (2));

    // Mapper returning null is a no-op (the function can opt out)
    assertEquals (EChange.UNCHANGED, aObject.replaceKey ("name", aOld -> null));
    assertEquals ("Alice", aObject.getAsString ("name"));

    // Key not present: function is invoked with null and may decide to insert
    assertFalse (aObject.containsKey ("inserted"));
    assertEquals (EChange.CHANGED, aObject.replaceKey ("inserted", aOld -> {
      assertNull (aOld);
      return JsonValue.create ("new");
    }));
    assertEquals ("new", aObject.getAsString ("inserted"));
    // Newly added key is appended at the end
    assertEquals ("inserted", aObject.keySet ().getCopyAsList ().get (aObject.size () - 1));

    // Key not present and function returns null -> no insert, no change
    assertEquals (EChange.UNCHANGED, aObject.replaceKey ("stillMissing", aOld -> {
      assertNull (aOld);
      return null;
    }));
    assertFalse (aObject.containsKey ("stillMissing"));
  }
}

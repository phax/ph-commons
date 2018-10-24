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
package com.helger.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.function.Function;

import org.junit.Test;

import com.helger.commons.collection.attr.StringMap;
import com.helger.commons.collection.map.MapEntry;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link JsonObject}.
 *
 * @author Philip Helger
 */
public final class JsonObjectTest
{
  @Test
  public void testSerialize ()
  {
    final JsonObject aObject = new JsonObject ();
    CommonsTestHelper.testDefaultSerialization (aObject);
    aObject.add ("key1", true);
    CommonsTestHelper.testDefaultSerialization (aObject);
    aObject.add ("key2", (byte) 3);
    CommonsTestHelper.testDefaultSerialization (aObject);
    aObject.add ("key3", 'x');
    CommonsTestHelper.testDefaultSerialization (aObject);
    aObject.add ("key4", 50);
    CommonsTestHelper.testDefaultSerialization (aObject);
    aObject.add ("key5", 51L);
    CommonsTestHelper.testDefaultSerialization (aObject);
    aObject.add ("key6", (short) 52);
    CommonsTestHelper.testDefaultSerialization (aObject);
    aObject.add ("key7", 3.1234);
    CommonsTestHelper.testDefaultSerialization (aObject);
    aObject.add ("key8", 3.1235f);
    CommonsTestHelper.testDefaultSerialization (aObject);
    aObject.add ("key9", "This is a string");
    CommonsTestHelper.testDefaultSerialization (aObject);
    aObject.add ("key10", new JsonArray ().add ("nested").add (0).add (Double.valueOf (12.34)));
    CommonsTestHelper.testDefaultSerialization (aObject);
    aObject.add ("key11", new JsonObject ().add ("n1", "nested").add ("n2", 0).add ("n3", Double.valueOf (12.34)));
    CommonsTestHelper.testDefaultSerialization (aObject);
    aObject.add (new MapEntry <> ("key12", "value12"));
    CommonsTestHelper.testDefaultSerialization (aObject);

    final JsonObject aObject2 = new JsonObject ();
    aObject2.addAll (aObject);
    CommonsTestHelper.testDefaultSerialization (aObject2);
    aObject2.addAll (new StringMap ().add ("a", "b").add ("c", "d"));
    CommonsTestHelper.testDefaultSerialization (aObject2);
    aObject2.addAllMapped (new StringMap ().add ("a", "b").add ("c", "d"), JsonValue::create);
    CommonsTestHelper.testDefaultSerialization (aObject2);
    aObject2.addAllMapped (new StringMap ().add ("e", "f").add ("g", "h"), Function.identity (), JsonValue::create);
    CommonsTestHelper.testDefaultSerialization (aObject2);
  }

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
    assertTrue (aObject.containsValue (5l));
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
  }

  @Test
  public void testGetAsObject ()
  {
    assertNull (new JsonArray ().getAsObject ());
    assertNotNull (new JsonObject ().getAsObject ());
    assertNull (JsonValue.create (true).getAsObject ());
  }
}

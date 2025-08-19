/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import org.junit.Test;

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
}

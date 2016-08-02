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
package com.helger.json;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

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
    aObject.add ("key1", 5);
    CommonsTestHelper.testDefaultSerialization (aObject);
    aObject.add ("key2", 3.1234);
    CommonsTestHelper.testDefaultSerialization (aObject);
    aObject.add ("key3", "This is a string");
    CommonsTestHelper.testDefaultSerialization (aObject);
    aObject.add ("key4", new JsonArray ().add ("nested").add (0).add (Double.valueOf (12.34)));
    CommonsTestHelper.testDefaultSerialization (aObject);
    aObject.add ("key5", new JsonObject ().add ("n1", "nested").add ("n2", 0).add ("n3", Double.valueOf (12.34)));
    CommonsTestHelper.testDefaultSerialization (aObject);
  }

  @Test
  public void testContains ()
  {
    final JsonObject aObject = new JsonObject ();
    assertFalse (aObject.containsValue (5));
    assertFalse (aObject.containsValue (3.1234));
    assertFalse (aObject.containsValue ((IJson) null));

    aObject.add ("key1", 5);
    assertTrue (aObject.containsValue (5));
    assertFalse (aObject.containsValue (3.1234));
    assertFalse (aObject.containsValue ((IJson) null));

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

    aObject.add ("key5", new JsonObject ().add ("n1", "nested").add ("n2", 0).add ("n3", BigDecimal.valueOf (12.34)));
    assertTrue (aObject.containsValue (5));
    assertTrue (aObject.containsValue (3.1234));
    assertTrue (aObject.containsValue ("This is a string"));
    assertTrue (aObject.containsValue (new JsonArray ().add ("nested").add (0).add (BigDecimal.valueOf (12.34))));
    assertTrue (aObject.containsValue (new JsonObject ().add ("n1", "nested")
                                                        .add ("n2", 0)
                                                        .add ("n3", BigDecimal.valueOf (12.34))));
    assertFalse (aObject.containsValue ((IJson) null));
  }

  @Test
  public void testGetAsObject ()
  {
    assertNull (new JsonArray ().getAsObject ());
    assertNotNull (new JsonObject ().getAsObject ());
    assertNull (JsonValue.create (true).getAsObject ());
  }
}

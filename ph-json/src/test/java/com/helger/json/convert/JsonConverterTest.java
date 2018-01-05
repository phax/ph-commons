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
package com.helger.json.convert;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.CommonsTreeMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsNavigableMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.json.JsonArray;
import com.helger.json.JsonObject;
import com.helger.json.JsonValue;

/**
 * Test class for class {@link JsonConverter}.
 *
 * @author Philip Helger
 */
public final class JsonConverterTest
{
  @Test
  public void testSimpleValues ()
  {
    assertTrue (JsonConverter.convertToJson (Boolean.TRUE) instanceof JsonValue);
    assertTrue (JsonConverter.convertToJson (Byte.valueOf ((byte) 0)) instanceof JsonValue);
    assertTrue (JsonConverter.convertToJson (Character.valueOf ('\0')) instanceof JsonValue);
    assertTrue (JsonConverter.convertToJson (Double.valueOf (3.1234D)) instanceof JsonValue);
    assertTrue (JsonConverter.convertToJson (Float.valueOf (3.1234F)) instanceof JsonValue);
    assertTrue (JsonConverter.convertToJson (Integer.valueOf (15)) instanceof JsonValue);
    assertTrue (JsonConverter.convertToJson (Long.valueOf (15L)) instanceof JsonValue);
    assertTrue (JsonConverter.convertToJson (Short.valueOf ((short) 15)) instanceof JsonValue);
    assertTrue (JsonConverter.convertToJson ("a string") instanceof JsonValue);
    assertTrue (JsonConverter.convertToJson (new StringBuilder ().append ("sb")) instanceof JsonValue);
    assertTrue (JsonConverter.convertToJson (new StringBuffer ().append ("sb")) instanceof JsonValue);
    assertTrue (JsonConverter.convertToJson (BigDecimal.ONE) instanceof JsonValue);
    assertTrue (JsonConverter.convertToJson (BigInteger.ONE) instanceof JsonValue);
    assertTrue (JsonConverter.convertToJson (new AtomicBoolean (true)) instanceof JsonValue);
    assertTrue (JsonConverter.convertToJson (new AtomicInteger (15)) instanceof JsonValue);
    assertTrue (JsonConverter.convertToJson (new AtomicLong (15L)) instanceof JsonValue);

    // Weird stuff
    assertTrue (JsonConverter.convertToJson (new JsonConverterTest ()) instanceof JsonValue);
  }

  @Test
  public void testArray ()
  {
    assertTrue (JsonConverter.convertToJson (new boolean [] { true, false, true }) instanceof JsonArray);
    assertTrue (JsonConverter.convertToJson (new byte [] { 0, 1, 2 }) instanceof JsonArray);
    assertTrue (JsonConverter.convertToJson (new char [] { 'a', 'b', 'c' }) instanceof JsonArray);
    assertTrue (JsonConverter.convertToJson (new double [] { 1, 2, 3 }) instanceof JsonArray);
    assertTrue (JsonConverter.convertToJson (new float [] { 1, 2, 3 }) instanceof JsonArray);
    assertTrue (JsonConverter.convertToJson (new int [] { 1, 2, 3 }) instanceof JsonArray);
    assertTrue (JsonConverter.convertToJson (new long [] { 1, 2, 3 }) instanceof JsonArray);
    assertTrue (JsonConverter.convertToJson (new short [] { 1, 2, 3 }) instanceof JsonArray);
    assertTrue (JsonConverter.convertToJson (new String [] { "foo", "bar", "bla" }) instanceof JsonArray);
  }

  @Test
  public void testMap ()
  {
    final ICommonsMap <String, Object> aMap = new CommonsHashMap <> ();
    aMap.put ("foo", "bar");
    aMap.put ("foo2", Integer.valueOf (5));
    assertTrue (JsonConverter.convertToJson (aMap) instanceof JsonObject);

    final ICommonsNavigableMap <String, Object> aTreeMap = new CommonsTreeMap <> ();
    aTreeMap.put ("foo", "bar");
    aTreeMap.put ("foo2", Integer.valueOf (5));
    assertTrue (JsonConverter.convertToJson (aTreeMap) instanceof JsonObject);

    final ICommonsOrderedMap <String, Object> aLinkedMap = new CommonsLinkedHashMap <> ();
    aLinkedMap.put ("foo", "bar");
    aLinkedMap.put ("foo2", Integer.valueOf (5));
    assertTrue (JsonConverter.convertToJson (aLinkedMap) instanceof JsonObject);
  }
}

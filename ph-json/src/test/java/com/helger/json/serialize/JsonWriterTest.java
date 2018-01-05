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
package com.helger.json.serialize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nullable;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.CommonsTreeMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsNavigableMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.mutable.MutableBoolean;
import com.helger.commons.mutable.MutableByte;
import com.helger.commons.mutable.MutableChar;
import com.helger.commons.mutable.MutableDouble;
import com.helger.commons.mutable.MutableFloat;
import com.helger.commons.mutable.MutableInt;
import com.helger.commons.mutable.MutableLong;
import com.helger.commons.mutable.MutableShort;
import com.helger.json.IJson;
import com.helger.json.JsonArray;
import com.helger.json.JsonObject;
import com.helger.json.convert.JsonConverter;

/**
 * Test class for class {@link JsonWriter}.
 *
 * @author Philip Helger
 */
public final class JsonWriterTest
{
  @Test
  public void testSimpleValues ()
  {
    assertEquals ("true", JsonConverter.convertToJson (Boolean.TRUE).getAsJsonString ());
    assertEquals ("0", JsonConverter.convertToJson (Byte.valueOf ((byte) 0)).getAsJsonString ());
    assertEquals ("\"\\u0000\"", JsonConverter.convertToJson (Character.toString ('\0')).getAsJsonString ());
    assertEquals ("3.1234", JsonConverter.convertToJson (Double.valueOf (3.1234D)).getAsJsonString ());
    assertEquals ("3.1234", JsonConverter.convertToJson (Float.valueOf (3.1234F)).getAsJsonString ());
    assertEquals ("15", JsonConverter.convertToJson (Integer.valueOf (15)).getAsJsonString ());
    assertEquals ("15", JsonConverter.convertToJson (Long.valueOf (15L)).getAsJsonString ());
    assertEquals ("15", JsonConverter.convertToJson (Short.valueOf ((short) 15)).getAsJsonString ());
    assertEquals ("\"a string\"", JsonConverter.convertToJson ("a string").getAsJsonString ());
    assertEquals ("\"sb\"", JsonConverter.convertToJson (new StringBuilder ().append ("sb")).getAsJsonString ());
    assertEquals ("\"sb\"", JsonConverter.convertToJson (new StringBuffer ().append ("sb")).getAsJsonString ());
    assertEquals ("1", JsonConverter.convertToJson (BigDecimal.ONE).getAsJsonString ());
    assertEquals ("1", JsonConverter.convertToJson (BigInteger.ONE).getAsJsonString ());
    assertEquals ("true", JsonConverter.convertToJson (new AtomicBoolean (true)).getAsJsonString ());
    assertEquals ("15", JsonConverter.convertToJson (new AtomicInteger (15)).getAsJsonString ());
    assertEquals ("15", JsonConverter.convertToJson (new AtomicLong (15L)).getAsJsonString ());
    assertEquals ("\"slashed/value\"", JsonConverter.convertToJson ("slashed/value").getAsJsonString ());
    // In this case the backslash is interpreted as such and there double
    // escaped
    assertEquals ("\"slashed\\\\/value\"", JsonConverter.convertToJson ("slashed\\/value").getAsJsonString ());
  }

  @Test
  public void testMutableValues ()
  {
    assertEquals ("true", JsonConverter.convertToJson (new MutableBoolean (true)).getAsJsonString ());
    assertEquals ("0", JsonConverter.convertToJson (new MutableByte (0)).getAsJsonString ());
    assertEquals ("\"\\u0000\"", JsonConverter.convertToJson (new MutableChar ('\0')).getAsJsonString ());
    assertEquals ("3.1234", JsonConverter.convertToJson (new MutableDouble (3.1234D)).getAsJsonString ());
    assertEquals ("3.1234", JsonConverter.convertToJson (new MutableFloat (3.1234F)).getAsJsonString ());
    assertEquals ("15", JsonConverter.convertToJson (new MutableInt (15)).getAsJsonString ());
    assertEquals ("15", JsonConverter.convertToJson (new MutableLong (15L)).getAsJsonString ());
    assertEquals ("15", JsonConverter.convertToJson (new MutableShort ((short) 15)).getAsJsonString ());
  }

  @Test
  public void testArray ()
  {
    assertEquals ("[true,false,true]",
                  JsonConverter.convertToJson (new boolean [] { true, false, true }).getAsJsonString ());
    assertEquals ("[0,1,2]", JsonConverter.convertToJson (new byte [] { 0, 1, 2 }).getAsJsonString ());
    assertEquals ("[\"a\",\"b\",\"c\"]",
                  JsonConverter.convertToJson (new char [] { 'a', 'b', 'c' }).getAsJsonString ());
    assertEquals ("[1.0,2.0,3.0]", JsonConverter.convertToJson (new double [] { 1, 2, 3 }).getAsJsonString ());
    assertEquals ("[1.0,2.0,3.0]", JsonConverter.convertToJson (new float [] { 1, 2, 3 }).getAsJsonString ());
    assertEquals ("[1,2,3]", JsonConverter.convertToJson (new int [] { 1, 2, 3 }).getAsJsonString ());
    assertEquals ("[1,2,3]", JsonConverter.convertToJson (new long [] { 1, 2, 3 }).getAsJsonString ());
    assertEquals ("[1,2,3]", JsonConverter.convertToJson (new short [] { 1, 2, 3 }).getAsJsonString ());
    assertEquals ("[\"foo\",\"bar\",\"bla\"]",
                  JsonConverter.convertToJson (new String [] { "foo", "bar", "bla" }).getAsJsonString ());
    assertEquals ("[1,0,10]",
                  JsonConverter.convertToJson (CollectionHelper.<Object> newList (BigDecimal.ONE,
                                                                                  BigInteger.ZERO,
                                                                                  BigDecimal.TEN))
                               .getAsJsonString ());
  }

  @Test
  public void testMap ()
  {
    final ICommonsMap <String, Object> aMap = new CommonsHashMap<> ();
    aMap.put ("foo", "bar");
    assertEquals ("{\"foo\":\"bar\"}", JsonConverter.convertToJson (aMap).getAsJsonString ());

    final ICommonsNavigableMap <String, Object> aTreeMap = new CommonsTreeMap<> ();
    aTreeMap.put ("foo", "bar");
    aTreeMap.put ("foo2", Integer.valueOf (5));
    assertEquals ("{\"foo\":\"bar\",\"foo2\":5}", JsonConverter.convertToJson (aTreeMap).getAsJsonString ());

    final ICommonsOrderedMap <String, Object> aLinkedMap = new CommonsLinkedHashMap<> ();
    aLinkedMap.put ("foo", "bar");
    aLinkedMap.put ("foo2", Integer.valueOf (5));
    assertEquals ("{\"foo\":\"bar\",\"foo2\":5}", JsonConverter.convertToJson (aLinkedMap).getAsJsonString ());
    assertEquals ("{foo:\"bar\",foo2:5}",
                  JsonConverter.convertToJson (aLinkedMap)
                               .getAsJsonString (new JsonWriterSettings ().setQuoteNames (false)));
  }

  @Test
  public void testComplex ()
  {
    final ICommonsList <JsonObject> aObjs = new CommonsArrayList<> ();
    for (final ICommonsMap <String, String> aRow : new CommonsArrayList<> (CollectionHelper.newMap ("key", "value")))
    {
      final JsonObject aObj = new JsonObject ();
      for (final Map.Entry <String, String> aEntry : aRow.entrySet ())
        aObj.add (aEntry.getKey (), aEntry.getValue ());
      aObjs.add (aObj);
    }
    assertEquals ("{\"aa\":[{\"key\":\"value\"}]}",
                  JsonConverter.convertToJson (new JsonObject ().add ("aa", aObjs)).getAsJsonString ());
  }

  private void _testWriteAndRead (@Nullable final Object aValue)
  {
    final IJson aJson = JsonConverter.convertToJson (aValue);
    assertNotNull ("Failed: " + aValue, aJson);
    final String sJson = aJson.getAsJsonString ();
    assertNotNull (sJson);
    final IJson aJsonRead = JsonReader.readFromString (sJson);
    assertNotNull ("Failed to read: " + sJson, aJsonRead);
    final String sJsonRead = aJsonRead.getAsJsonString ();
    assertNotNull (sJsonRead);
    assertEquals (sJson, sJsonRead);
  }

  @Test
  public void testWriteAndReadSimpleValues ()
  {
    _testWriteAndRead (null);
    _testWriteAndRead (Boolean.TRUE);
    _testWriteAndRead (Byte.valueOf ((byte) 0));
    _testWriteAndRead (Byte.valueOf (Byte.MIN_VALUE));
    _testWriteAndRead (Byte.valueOf (Byte.MAX_VALUE));
    _testWriteAndRead (Character.toString ('6'));
    _testWriteAndRead (Character.toString (Character.MIN_VALUE));
    _testWriteAndRead (Character.toString (Character.MAX_VALUE));
    _testWriteAndRead (BigDecimal.valueOf (3.1234D));
    _testWriteAndRead (BigDecimal.valueOf (Double.MIN_VALUE));
    _testWriteAndRead (BigDecimal.valueOf (Double.MAX_VALUE));
    _testWriteAndRead (BigDecimal.valueOf (3.1234F));
    _testWriteAndRead (BigDecimal.valueOf (Float.MIN_VALUE));
    _testWriteAndRead (BigDecimal.valueOf (Float.MAX_VALUE));
    _testWriteAndRead (Integer.valueOf (15));
    _testWriteAndRead (Integer.valueOf (Integer.MIN_VALUE));
    _testWriteAndRead (Integer.valueOf (Integer.MAX_VALUE));
    _testWriteAndRead (Long.valueOf (15L));
    _testWriteAndRead (Long.valueOf (Long.MIN_VALUE));
    _testWriteAndRead (Long.valueOf (Long.MAX_VALUE));
    _testWriteAndRead (Short.valueOf ((short) 15));
    _testWriteAndRead (Short.valueOf (Short.MIN_VALUE));
    _testWriteAndRead (Short.valueOf (Short.MAX_VALUE));
    _testWriteAndRead ("a string");
    _testWriteAndRead (new StringBuilder ().append ("string builder"));
    _testWriteAndRead (new StringBuffer ().append ("string buffer"));
    _testWriteAndRead (BigDecimal.ONE);
    _testWriteAndRead (BigInteger.ONE);
    _testWriteAndRead (new AtomicBoolean (true));
    _testWriteAndRead (new AtomicInteger (15));
    _testWriteAndRead (new AtomicLong (15L));
  }

  @Test
  public void testWriteAndReadArray ()
  {
    _testWriteAndRead (new boolean [] { true, false, true });
    _testWriteAndRead (new byte [] { 0, 1, 2 });
    _testWriteAndRead (new char [] { 'a', 'b', 'c' });
    _testWriteAndRead (new double [] { 1, 2, 3 });
    _testWriteAndRead (new float [] { 1, 2, 3 });
    _testWriteAndRead (new int [] { 1, 2, 3 });
    _testWriteAndRead (new long [] { 1, 2, 3 });
    _testWriteAndRead (new short [] { 1, 2, 3 });
    _testWriteAndRead (new String [] { "foo", "bar", "bla" });
    _testWriteAndRead (CollectionHelper.<Object> newList (BigDecimal.ONE, BigInteger.ZERO, BigDecimal.TEN));
  }

  @Test
  public void testWriteAndReadMap ()
  {
    final ICommonsMap <String, Object> aMap = new CommonsHashMap<> ();
    aMap.put ("foo", "bar");
    _testWriteAndRead (aMap);

    final ICommonsNavigableMap <String, Object> aTreeMap = new CommonsTreeMap<> ();
    aTreeMap.put ("foo", "bar");
    aTreeMap.put ("foo2", Integer.valueOf (5));
    _testWriteAndRead (aTreeMap);

    final ICommonsOrderedMap <String, Object> aLinkedMap = new CommonsLinkedHashMap<> ();
    aLinkedMap.put ("foo", "bar");
    aLinkedMap.put ("foo2", Integer.valueOf (5));
    _testWriteAndRead (aLinkedMap);
  }

  @Test
  public void testIndent ()
  {
    final String sCRLF = JsonWriterSettings.DEFAULT_NEWLINE_STRING;
    final JsonWriter aWriter = new JsonWriter (new JsonWriterSettings ().setIndentEnabled (true));
    assertEquals ("{}", aWriter.writeAsString (new JsonObject ()));
    assertEquals ("{" +
                  sCRLF +
                  "  \"foo\":\"bar\"" +
                  sCRLF +
                  "}",
                  aWriter.writeAsString (new JsonObject ().add ("foo", "bar")));
    assertEquals ("{" +
                  sCRLF +
                  "  \"foo\":[" +
                  sCRLF +
                  "    1," +
                  sCRLF +
                  "    2" +
                  sCRLF +
                  "  ]" +
                  sCRLF +
                  "}",
                  aWriter.writeAsString (new JsonObject ().add ("foo", new JsonArray ().add (1).add (2))));
    assertEquals ("{" +
                  sCRLF +
                  "  \"foo\":{" +
                  sCRLF +
                  "    \"bar\":\"baz\"" +
                  sCRLF +
                  "  }" +
                  sCRLF +
                  "}",
                  aWriter.writeAsString (new JsonObject ().add ("foo", new JsonObject ().add ("bar", "baz"))));
    assertEquals ("[]", aWriter.writeAsString (new JsonArray ()));
    assertEquals ("[" +
                  sCRLF +
                  "  {" +
                  sCRLF +
                  "    \"foo\":\"bar\"" +
                  sCRLF +
                  "  }" +
                  sCRLF +
                  "]",
                  aWriter.writeAsString (new JsonArray ().add (new JsonObject ().add ("foo", "bar"))));
  }

  @Test
  public void testWriteNewlineAtEnd ()
  {
    final String sCRLF = JsonWriterSettings.DEFAULT_NEWLINE_STRING;
    assertEquals ("{\"foo\":\"bar\"}", new JsonWriter ().writeAsString (new JsonObject ().add ("foo", "bar")));
    assertEquals ("{\"foo\":\"bar\"}",
                  new JsonWriter (new JsonWriterSettings ().setWriteNewlineAtEnd (false)).writeAsString (new JsonObject ().add ("foo",
                                                                                                                                "bar")));
    assertEquals ("{\"foo\":\"bar\"}" +
                  sCRLF,
                  new JsonWriter (new JsonWriterSettings ().setWriteNewlineAtEnd (true)).writeAsString (new JsonObject ().add ("foo",
                                                                                                                               "bar")));
  }
}

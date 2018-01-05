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

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link JsonArray}.
 *
 * @author Philip Helger
 */
public final class JsonArrayTest
{
  @Test
  public void testSerialize ()
  {
    final JsonArray aArray = new JsonArray ();
    CommonsTestHelper.testDefaultSerialization (aArray);
    aArray.add (5);
    CommonsTestHelper.testDefaultSerialization (aArray);
    aArray.add (3.1234);
    CommonsTestHelper.testDefaultSerialization (aArray);
    aArray.add ("This is a string");
    CommonsTestHelper.testDefaultSerialization (aArray);
    aArray.add (new JsonArray ().add ("nested").add (0).add (Double.valueOf (12.34)));
    CommonsTestHelper.testDefaultSerialization (aArray);
    aArray.add (new JsonObject ().add ("n1", "nested").add ("n2", 0).add ("n3", Double.valueOf (12.34)));
    CommonsTestHelper.testDefaultSerialization (aArray);
  }

  @Test
  public void testContains ()
  {
    final JsonArray aArray = new JsonArray ();
    assertFalse (aArray.contains (5));
    assertFalse (aArray.contains (5L));
    assertFalse (aArray.contains (3.1234));
    assertFalse (aArray.contains ((IJson) null));
    assertFalse (aArray.contains (false));
    assertFalse (aArray.contains ('x'));
    assertFalse (aArray.contains ((byte) 1));
    assertFalse (aArray.contains ((short) 1));
    assertFalse (aArray.contains (12.345f));

    aArray.add (5);
    assertTrue (aArray.contains (5));
    assertFalse (aArray.contains (3.1234));
    assertFalse (aArray.contains ((IJson) null));

    aArray.add (3.1234);
    assertTrue (aArray.contains (5));
    assertTrue (aArray.contains (3.1234));
    assertFalse (aArray.contains ((IJson) null));

    aArray.add ("This is a string");
    assertTrue (aArray.contains (5));
    assertTrue (aArray.contains (3.1234));
    assertTrue (aArray.contains ("This is a string"));
    assertFalse (aArray.contains ((IJson) null));

    aArray.add (new JsonArray ().add ("nested").add (0).add (BigDecimal.valueOf (12.34)));
    assertTrue (aArray.contains (5));
    assertTrue (aArray.contains (3.1234));
    assertTrue (aArray.contains ("This is a string"));
    assertTrue (aArray.contains (new JsonArray ().add ("nested").add (0).add (BigDecimal.valueOf (12.34))));
    assertFalse (aArray.contains ((IJson) null));

    aArray.add (new JsonObject ().add ("n1", "nested").add ("n2", 0).add ("n3", BigDecimal.valueOf (12.34)));
    assertTrue (aArray.contains (5));
    assertTrue (aArray.contains (3.1234));
    assertTrue (aArray.contains ("This is a string"));
    assertTrue (aArray.contains (new JsonArray ().add ("nested").add (0).add (BigDecimal.valueOf (12.34))));
    assertTrue (aArray.contains (new JsonObject ().add ("n1", "nested")
                                                  .add ("n2", 0)
                                                  .add ("n3", BigDecimal.valueOf (12.34))));
    assertFalse (aArray.contains ((IJson) null));
  }

  @Test
  public void testAddAll ()
  {
    final JsonArray aArray = new JsonArray ();
    assertEquals (0, aArray.size ());

    aArray.add ("Test");
    assertEquals (1, aArray.size ());

    aArray.addAll (new boolean [] { true, true, true });
    assertEquals (4, aArray.size ());
    aArray.addAll (new boolean [0]);
    assertEquals (4, aArray.size ());
    aArray.addAll ((boolean []) null);
    assertEquals (4, aArray.size ());

    aArray.addAll (new byte [] { 1, 2, 3 });
    assertEquals (7, aArray.size ());
    aArray.addAll (new byte [0]);
    assertEquals (7, aArray.size ());
    aArray.addAll ((byte []) null);
    assertEquals (7, aArray.size ());

    aArray.addAll (new char [] { 'x', 'y', 'z' });
    assertEquals (10, aArray.size ());
    aArray.addAll (new char [0]);
    assertEquals (10, aArray.size ());
    aArray.addAll ((char []) null);
    assertEquals (10, aArray.size ());

    aArray.addAll (new double [] { 1.2, 3.4, 5.6 });
    assertEquals (13, aArray.size ());
    aArray.addAll (new double [0]);
    assertEquals (13, aArray.size ());
    aArray.addAll ((double []) null);
    assertEquals (13, aArray.size ());

    aArray.addAll (new float [] { 1.2f, 3.4f, 5.6f });
    assertEquals (16, aArray.size ());
    aArray.addAll (new float [0]);
    assertEquals (16, aArray.size ());
    aArray.addAll ((float []) null);
    assertEquals (16, aArray.size ());

    aArray.addAll (new int [] { 1, 2, 3 });
    assertEquals (19, aArray.size ());
    aArray.addAll (new int [0]);
    assertEquals (19, aArray.size ());
    aArray.addAll ((int []) null);
    assertEquals (19, aArray.size ());

    aArray.addAll (new long [] { 1000, 1001, 1002 });
    assertEquals (22, aArray.size ());
    aArray.addAll (new long [0]);
    assertEquals (22, aArray.size ());
    aArray.addAll ((long []) null);
    assertEquals (22, aArray.size ());

    aArray.addAll (new short [] { 1, 2, 3 });
    assertEquals (25, aArray.size ());
    aArray.addAll (new short [0]);
    assertEquals (25, aArray.size ());
    aArray.addAll ((short []) null);
    assertEquals (25, aArray.size ());

    aArray.addAll (new Object [] { "xyz",
                                   Integer.valueOf (7),
                                   new BigDecimal ("11111111111111111111111111111111111122222.4") });
    assertEquals (28, aArray.size ());
    aArray.addAll (new Object [0]);
    assertEquals (28, aArray.size ());
    aArray.addAll ((Object []) null);
    assertEquals (28, aArray.size ());
    aArray.addAll ((Iterable <Object>) null);
    assertEquals (28, aArray.size ());

    aArray.addAll (new JsonArray ().add ("xyz")
                                   .add (7)
                                   .add (new BigDecimal ("11111111111111111111111111111111111122222.4")));
    assertEquals (31, aArray.size ());
    aArray.addAll (new JsonArray ());
    assertEquals (31, aArray.size ());
    aArray.addAll ((IJsonArray) null);
    assertEquals (31, aArray.size ());

    aArray.addAllMapped (new CommonsArrayList <Object> ("xyz",
                                                        Integer.valueOf (7),
                                                        new BigDecimal ("11111111111111111111111111111111111122222.4")),
                         JsonValue::create);
    assertEquals (34, aArray.size ());
    aArray.addAllMapped (new CommonsArrayList <> (0), JsonValue::create);
    assertEquals (34, aArray.size ());
    aArray.addAllMapped ((Iterable <Object>) null, JsonValue::create);
    assertEquals (34, aArray.size ());

    assertEquals (String.class, aArray.getValueAtIndex (0).getValueClass ());
    assertEquals (Boolean.class, aArray.getValueAtIndex (1).getValueClass ());
    assertEquals (Integer.class, aArray.getValueAtIndex (4).getValueClass ());
    assertEquals (String.class, aArray.getValueAtIndex (7).getValueClass ());
    assertEquals (Double.class, aArray.getValueAtIndex (10).getValueClass ());
    assertEquals (Double.class, aArray.getValueAtIndex (13).getValueClass ());
    assertEquals (Integer.class, aArray.getValueAtIndex (16).getValueClass ());
    assertEquals (Long.class, aArray.getValueAtIndex (19).getValueClass ());
    assertEquals (Integer.class, aArray.getValueAtIndex (22).getValueClass ());
    assertEquals (String.class, aArray.getValueAtIndex (25).getValueClass ());
    assertEquals (Integer.class, aArray.getValueAtIndex (26).getValueClass ());
    assertEquals (BigDecimal.class, aArray.getValueAtIndex (27).getValueClass ());
    assertEquals (String.class, aArray.getValueAtIndex (28).getValueClass ());
    assertEquals (Integer.class, aArray.getValueAtIndex (29).getValueClass ());
    assertEquals (BigDecimal.class, aArray.getValueAtIndex (30).getValueClass ());
    assertEquals (String.class, aArray.getValueAtIndex (31).getValueClass ());
    assertEquals (Integer.class, aArray.getValueAtIndex (32).getValueClass ());
    assertEquals (BigDecimal.class, aArray.getValueAtIndex (33).getValueClass ());

    assertEquals (34, aArray.getClonedValues ().size ());
    CommonsTestHelper.testGetClone (aArray);
  }

  @Test
  public void testAddAllAtIndex ()
  {
    final JsonArray aArray = new JsonArray ();
    assertEquals (0, aArray.size ());

    aArray.add ("Test");
    assertEquals (1, aArray.size ());

    aArray.addAllAt (0, new boolean [] { true, true, true });
    assertEquals (4, aArray.size ());
    aArray.addAllAt (0, new boolean [0]);
    assertEquals (4, aArray.size ());
    aArray.addAllAt (0, (boolean []) null);
    assertEquals (4, aArray.size ());

    aArray.addAllAt (0, new byte [] { 1, 2, 3 });
    assertEquals (7, aArray.size ());
    aArray.addAllAt (0, new byte [0]);
    assertEquals (7, aArray.size ());
    aArray.addAllAt (0, (byte []) null);
    assertEquals (7, aArray.size ());

    aArray.addAllAt (0, new char [] { 'x', 'y', 'z' });
    assertEquals (10, aArray.size ());
    aArray.addAllAt (0, new char [0]);
    assertEquals (10, aArray.size ());
    aArray.addAllAt (0, (char []) null);
    assertEquals (10, aArray.size ());

    aArray.addAllAt (0, new double [] { 1.2, 3.4, 5.6 });
    assertEquals (13, aArray.size ());
    aArray.addAllAt (0, new double [0]);
    assertEquals (13, aArray.size ());
    aArray.addAllAt (0, (double []) null);
    assertEquals (13, aArray.size ());

    aArray.addAllAt (0, new float [] { 1.2f, 3.4f, 5.6f });
    assertEquals (16, aArray.size ());
    aArray.addAllAt (0, new float [0]);
    assertEquals (16, aArray.size ());
    aArray.addAllAt (0, (float []) null);
    assertEquals (16, aArray.size ());

    aArray.addAllAt (0, new int [] { 1, 2, 3 });
    assertEquals (19, aArray.size ());
    aArray.addAllAt (0, new int [0]);
    assertEquals (19, aArray.size ());
    aArray.addAllAt (0, (int []) null);
    assertEquals (19, aArray.size ());

    aArray.addAllAt (0, new long [] { 1000, 1001, 1002 });
    assertEquals (22, aArray.size ());
    aArray.addAllAt (0, new long [0]);
    assertEquals (22, aArray.size ());
    aArray.addAllAt (0, (long []) null);
    assertEquals (22, aArray.size ());

    aArray.addAllAt (0, new short [] { 1, 2, 3 });
    assertEquals (25, aArray.size ());
    aArray.addAllAt (0, new short [0]);
    assertEquals (25, aArray.size ());
    aArray.addAllAt (0, (short []) null);
    assertEquals (25, aArray.size ());

    aArray.addAllAt (0,
                     new Object [] { "xyz",
                                     Integer.valueOf (7),
                                     new BigDecimal ("11111111111111111111111111111111111122222.4") });
    assertEquals (28, aArray.size ());
    aArray.addAllAt (0, new Object [0]);
    assertEquals (28, aArray.size ());
    aArray.addAllAt (0, (Object []) null);
    assertEquals (28, aArray.size ());
    aArray.addAllAt (0, (Iterable <Object>) null);
    assertEquals (28, aArray.size ());

    aArray.addAllAt (0,
                     new JsonArray ().add ("xyz")
                                     .add (7)
                                     .add (new BigDecimal ("11111111111111111111111111111111111122222.4")));
    assertEquals (31, aArray.size ());
    aArray.addAllAt (0, new JsonArray ());
    assertEquals (31, aArray.size ());
    aArray.addAllAt (0, (IJsonArray) null);
    assertEquals (31, aArray.size ());

    aArray.addAllMappedAt (0,
                           new CommonsArrayList <Object> ("xyz",
                                                          Integer.valueOf (7),
                                                          new BigDecimal ("11111111111111111111111111111111111122222.4")),
                           JsonValue::create);
    assertEquals (34, aArray.size ());
    aArray.addAllMappedAt (0, new CommonsArrayList <> (0), JsonValue::create);
    assertEquals (34, aArray.size ());
    aArray.addAllMappedAt (0, (Iterable <Object>) null, JsonValue::create);
    assertEquals (34, aArray.size ());

    assertEquals (String.class, aArray.getValueAtIndex (33).getValueClass ());
    assertEquals (Boolean.class, aArray.getValueAtIndex (30).getValueClass ());
    assertEquals (Integer.class, aArray.getValueAtIndex (27).getValueClass ());
    assertEquals (String.class, aArray.getValueAtIndex (24).getValueClass ());
    assertEquals (Double.class, aArray.getValueAtIndex (21).getValueClass ());
    assertEquals (Double.class, aArray.getValueAtIndex (18).getValueClass ());
    assertEquals (Integer.class, aArray.getValueAtIndex (15).getValueClass ());
    assertEquals (Long.class, aArray.getValueAtIndex (12).getValueClass ());
    assertEquals (Integer.class, aArray.getValueAtIndex (9).getValueClass ());
    assertEquals (String.class, aArray.getValueAtIndex (6).getValueClass ());
    assertEquals (Integer.class, aArray.getValueAtIndex (7).getValueClass ());
    assertEquals (BigDecimal.class, aArray.getValueAtIndex (8).getValueClass ());
    assertEquals (String.class, aArray.getValueAtIndex (3).getValueClass ());
    assertEquals (Integer.class, aArray.getValueAtIndex (4).getValueClass ());
    assertEquals (BigDecimal.class, aArray.getValueAtIndex (5).getValueClass ());
    assertEquals (String.class, aArray.getValueAtIndex (0).getValueClass ());
    assertEquals (Integer.class, aArray.getValueAtIndex (1).getValueClass ());
    assertEquals (BigDecimal.class, aArray.getValueAtIndex (2).getValueClass ());

    assertEquals (34, aArray.getClonedValues ().size ());
    CommonsTestHelper.testGetClone (aArray);
  }

  @Test
  public void testGetSubArray ()
  {
    final JsonArray aArray = new JsonArray ();
    for (int i = 1; i <= 10; ++i)
      aArray.add (i);
    assertEquals ("[1,2,3,4,5,6,7,8,9,10]", aArray.getAsJsonString ());

    final IJsonArray aSubArray = aArray.getSubArray (1, 5);
    assertEquals ("[2,3,4,5]", aSubArray.getAsJsonString ());

    assertEquals ("[10]", aArray.getSubArray (9, 10).getAsJsonString ());
    assertEquals ("[]", aArray.getSubArray (0, 0).getAsJsonString ());

    aArray.removeAtIndex (1);
    assertEquals ("[1,3,4,5,6,7,8,9,10]", aArray.getAsJsonString ());
    assertEquals ("[2,3,4,5]", aSubArray.getAsJsonString ());
  }

  @Test
  public void testGetAsArray ()
  {
    assertNotNull (new JsonArray ().getAsArray ());
    assertNull (new JsonObject ().getAsArray ());
    assertNull (JsonValue.create (true).getAsArray ());
  }
}

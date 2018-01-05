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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link JsonValue}.
 *
 * @author Philip Helger
 */
public final class JsonValueTest
{
  @Test
  public void testBasic ()
  {
    final JsonValue v = JsonValue.create (5);
    assertTrue (v.isValue ());
    assertFalse (v.isArray ());
    assertFalse (v.isObject ());
    assertEquals (Integer.valueOf (5), v.getValue ());
    assertFalse (v.hasNoValue ());
    assertFalse (v.isBooleanValue ());
    assertTrue (v.isIntValue ());
    assertFalse (v.isDecimalValue ());
    assertFalse (v.isStringValue ());
    assertNotNull (v.getAsBooleanObj ());
    assertNotNull (v.getAsIntObj ());
    assertNotNull (v.getAsLongObj ());
    assertNotNull (v.getAsBigInteger ());
    assertNotNull (v.getAsDoubleObj ());
    assertNotNull (v.getAsBigDecimal ());
    assertNotNull (v.getAsString ());
    assertNotNull (v.getCastedValue (Number.class));
    try
    {
      v.getCastedValue (String.class);
      fail ();
    }
    catch (final ClassCastException ex)
    {}
    try
    {
      v.getCastedValue (BigInteger.class);
      fail ();
    }
    catch (final ClassCastException ex)
    {}
    // Because it is immutable!
    assertSame (v, v.getClone ());
  }

  @Test
  public void testNull ()
  {
    final JsonValue v = JsonValue.create (null);
    assertTrue (v.isValue ());
    assertFalse (v.isArray ());
    assertFalse (v.isObject ());
    assertNull (v.getValue ());
    assertTrue (v.hasNoValue ());
    assertFalse (v.isBooleanValue ());
    assertFalse (v.isIntValue ());
    assertFalse (v.isDecimalValue ());
    assertFalse (v.isStringValue ());
    assertNull (v.getAsBooleanObj ());
    assertNull (v.getAsIntObj ());
    assertNull (v.getAsLongObj ());
    assertNull (v.getAsBigInteger ());
    assertNull (v.getAsDoubleObj ());
    assertNull (v.getAsBigDecimal ());
    assertNull (v.getAsString ());
    assertNull (v.getCastedValue (Number.class));
    assertNull (v.getCastedValue (String.class));
    assertNull (v.getCastedValue (BigInteger.class));
    // Because it is immutable!
    assertSame (v, v.getClone ());
  }

  @Test
  public void testCache ()
  {
    for (int i = Byte.MIN_VALUE; i <= Byte.MAX_VALUE; ++i)
      assertNotNull (JsonValue.create ((byte) i));
    for (int i = -200; i < 200; ++i)
      assertNotNull (JsonValue.create (i));
    for (long i = -200; i < 200; ++i)
      assertNotNull (JsonValue.create (i));
  }

  @Test
  public void testSerialize ()
  {
    CommonsTestHelper.testDefaultSerialization (JsonValue.create (true));
    CommonsTestHelper.testDefaultSerialization (JsonValue.create (false));
    CommonsTestHelper.testDefaultSerialization (JsonValue.create (Boolean.TRUE));
    CommonsTestHelper.testDefaultSerialization (JsonValue.create (Boolean.FALSE));
    CommonsTestHelper.testDefaultSerialization (JsonValue.create ((byte) 5));
    CommonsTestHelper.testDefaultSerialization (JsonValue.create (Byte.valueOf ((byte) -5)));
    CommonsTestHelper.testDefaultSerialization (JsonValue.create ('x'));
    CommonsTestHelper.testDefaultSerialization (JsonValue.create (3.14d));
    CommonsTestHelper.testDefaultSerialization (JsonValue.create (3.14f));
    CommonsTestHelper.testDefaultSerialization (JsonValue.create (Integer.MIN_VALUE));
    CommonsTestHelper.testDefaultSerialization (JsonValue.create (Integer.MAX_VALUE));
    CommonsTestHelper.testDefaultSerialization (JsonValue.create (Integer.valueOf (47)));
    CommonsTestHelper.testDefaultSerialization (JsonValue.create (Long.MIN_VALUE));
    CommonsTestHelper.testDefaultSerialization (JsonValue.create (Long.MAX_VALUE));
    CommonsTestHelper.testDefaultSerialization (JsonValue.create (Long.valueOf (Long.MAX_VALUE)));
    CommonsTestHelper.testDefaultSerialization (JsonValue.create ((short) 815));
    CommonsTestHelper.testDefaultSerialization (JsonValue.create (Short.valueOf ((short) 815)));
    CommonsTestHelper.testDefaultSerialization (JsonValue.create ("Test"));
    CommonsTestHelper.testDefaultSerialization (JsonValue.create ("bla\r\nfoo\r\nfasel"));
    CommonsTestHelper.testDefaultSerialization (JsonValue.create (new BigInteger ("123456789012345678901234567890")));
    CommonsTestHelper.testDefaultSerialization (JsonValue.create (BigInteger.valueOf (Long.MIN_VALUE)
                                                                            .subtract (BigInteger.ONE)));
    CommonsTestHelper.testDefaultSerialization (JsonValue.create (BigInteger.valueOf (Long.MAX_VALUE)
                                                                            .add (BigInteger.ONE)));
    CommonsTestHelper.testDefaultSerialization (JsonValue.create (new BigDecimal ("123456789012345678901234567890.8")));
  }

  @Test
  public void testGetAsValue ()
  {
    assertNull (new JsonArray ().getAsValue ());
    assertNull (new JsonObject ().getAsValue ());
    assertNotNull (JsonValue.create (true).getAsValue ());
  }
}

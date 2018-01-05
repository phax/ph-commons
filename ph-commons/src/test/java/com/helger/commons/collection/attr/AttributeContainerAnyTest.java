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
package com.helger.commons.collection.attr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import org.junit.Test;

import com.helger.commons.CGlobal;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.mock.CommonsAssert;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link AttributeContainerAny}.
 *
 * @author Philip Helger
 */
public final class AttributeContainerAnyTest
{
  @Test
  public void testInit ()
  {
    final AttributeContainerAny <String> x = new AttributeContainerAny <> ();
    assertNotNull (x.keySet ());
    assertTrue (x.keySet ().isEmpty ());
    assertTrue (x.isEmpty ());
    assertTrue (x.putIn ("key", "value").isChanged ());
    assertFalse (x.isEmpty ());
    assertEquals (1, x.size ());
    assertTrue (x.putIn ("key2", "value2").isChanged ());
    assertTrue (x.putIn ("key", "value3").isChanged ());
    assertFalse (x.putIn ("key", "value3").isChanged ());
    assertEquals ("value2", x.getValue ("key2"));
    assertEquals ("value2", x.getAsString ("key2"));
    assertEquals ("value2", x.<String> getCastedValue ("key2"));
    assertEquals ("value2", x.getCastedValue ("key2", String.class));
    try
    {
      x.getCastedValue ("key2", Integer.class);
      fail ();
    }
    catch (final ClassCastException ex)
    {}
    assertEquals ("value2", x.getSafeCastedValue ("key2", String.class));
    assertNull (x.getSafeCastedValue ("key2", Integer.class));
    assertEquals ("value2", x.getConvertedValue ("key2", String.class));
    assertEquals ("def", x.<String> getCastedValue ("key none", "def"));
    assertEquals (Integer.valueOf (5), x.getSafeCastedValue ("key2", Integer.valueOf (5), Integer.class));
    assertEquals ("def", x.getConvertedValue ("key none", "def", String.class));
    assertTrue (x.containsKey ("key2"));
    assertTrue (x.removeAll ().isChanged ());
    assertFalse (x.removeAll ().isChanged ());
    assertFalse (x.containsKey ("key2"));
    assertTrue (x.isEmpty ());
    assertTrue (x.keySet ().isEmpty ());
    assertFalse (x.removeObject ("key2").isChanged ());

    assertTrue (x.putIn ("key", Integer.valueOf (17)).isChanged ());
    assertTrue (x.getAsBoolean ("key"));
    assertEquals (17, x.getAsInt ("key"));
    assertEquals (17, x.getAsLong ("key"));
    assertEquals (CGlobal.ILLEGAL_UINT, x.getAsInt ("key2"));
    assertEquals (Integer.valueOf (17), x.getCastedValue ("key"));
    CommonsAssert.assertEquals (17, x.getAsDouble ("key"));
    CommonsAssert.assertEquals (CGlobal.ILLEGAL_DOUBLE, x.getAsDouble ("key2"));
    assertEquals (new BigInteger ("17"), x.getAsBigInteger ("key"));
    assertEquals (new BigDecimal ("17"), x.getAsBigDecimal ("key"));
    assertFalse (x.getAsBoolean ("key2"));
    assertTrue (x.removeObject ("key").isChanged ());
    assertFalse (x.removeObject ("key").isChanged ());

    // Check null values
    assertTrue (x.putIn ("null1", null).isUnchanged ());
    assertNull (x.getValue ("null1"));
    assertTrue (x.containsKey ("null1"));
    assertTrue (x.removeObject ("null1").isUnchanged ());
    assertNull (x.getValue ("null1"));
    assertFalse (x.containsKey ("null1"));
    assertTrue (x.removeObject ("null1").isUnchanged ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new AttributeContainerAny <String> (),
                                                                       new AttributeContainerAny <String> ());
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new AttributeContainerAny <> (CollectionHelper.newMap (new String [] { "key",
                                                                                                                                              "key2" },
                                                                                                                              new Object [] { "value",
                                                                                                                                              "value2" })),
                                                                       new AttributeContainerAny <> (CollectionHelper.newMap (new String [] { "key",
                                                                                                                                              "key2" },
                                                                                                                              new Object [] { "value",
                                                                                                                                              "value2" })));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new AttributeContainerAny <> (CollectionHelper.newMap (new String [] { "key",
                                                                                                                                                  "key2" },
                                                                                                                                  new Object [] { "value",
                                                                                                                                                  "value2" })),
                                                                           new AttributeContainerAny <> (CollectionHelper.newMap (new String [] { "key",
                                                                                                                                                  "key2" },
                                                                                                                                  new Object [] { "value",
                                                                                                                                                  "value" })));

    assertTrue (new AttributeContainerAny <> ((Map <String, Object>) null).isEmpty ());
  }

  @Test
  public void testGetAndSetAttributeFlag ()
  {
    final AttributeContainerAny <String> aCont = new AttributeContainerAny <> ();
    // Not yet present
    assertFalse (aCont.getAndSetFlag ("any"));
    // Now it is present
    assertTrue (aCont.getAndSetFlag ("any"));
    for (int i = 0; i < 20; ++i)
      assertTrue (aCont.getAndSetFlag ("any"));
  }

  @Test
  public void testWithStringArray ()
  {
    final AttributeContainerAny <String> aCont = new AttributeContainerAny <> ();
    aCont.putIn ("a", new String [] { "1", "20" });
    // Expected to use the first
    assertEquals ("1", aCont.getAsString ("a"));
    assertEquals (1, aCont.getAsInt ("a"));
    assertEquals (1, aCont.getAsLong ("a"));
    assertEquals (1, aCont.getAsShort ("a"));
    assertEquals (1, aCont.getAsByte ("a"));
    CommonsAssert.assertEquals (1, aCont.getAsDouble ("a"));
    CommonsAssert.assertEquals (1, aCont.getAsFloat ("a"));
    assertEquals (BigDecimal.ONE, aCont.getAsBigDecimal ("a"));
    assertEquals (BigInteger.ONE, aCont.getAsBigInteger ("a"));
    assertEquals (new CommonsArrayList <> ("1", "20"), aCont.getAsStringList ("a"));
    assertEquals (new CommonsLinkedHashSet <> ("1", "20"), aCont.getAsStringSet ("a"));
  }

  @Test
  public void testCastAndConvert ()
  {
    final AttributeContainerAny <String> aCont = new AttributeContainerAny <> ();
    aCont.putIn ("a", BigDecimal.TEN);

    assertSame (BigDecimal.TEN, aCont.getCastedValue ("a", BigDecimal.class));
    assertSame (BigDecimal.TEN, aCont.getCastedValue ("a", Number.class));
    assertSame (BigDecimal.TEN, aCont.getCastedValue ("a", Object.class));
    assertSame (BigDecimal.TEN, aCont.getCastedValue ("a", Serializable.class));

    assertSame (BigDecimal.TEN, aCont.getConvertedValue ("a", BigDecimal.class));
    assertSame (BigDecimal.TEN, aCont.getConvertedValue ("a", Number.class));
    assertSame (BigDecimal.TEN, aCont.getConvertedValue ("a", Object.class));
    assertSame (BigDecimal.TEN, aCont.getConvertedValue ("a", Serializable.class));

    // Source is String
    aCont.putIn ("a", "1381");
    assertEquals (1381, aCont.getAsInt ("a"));

    // Source is String
    aCont.putIn ("a", "1381.2");
    assertEquals (1381, aCont.getAsInt ("a"));

    // Source is double
    aCont.putIn ("a", 1381.2);
    assertEquals (1381, aCont.getAsInt ("a"));
  }
}

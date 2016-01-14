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
package com.helger.commons.collection.attr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import org.junit.Test;

import com.helger.commons.CGlobal;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.mock.CommonsAssert;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link MapBasedAttributeContainerAny}.
 *
 * @author Philip Helger
 */
public final class MapBasedAttributeContainerTest
{
  @Test
  public void testInit ()
  {
    final MapBasedAttributeContainerAny <String> x = new MapBasedAttributeContainerAny <String> ();
    assertNotNull (x.getAllAttributeNames ());
    assertTrue (x.getAllAttributeNames ().isEmpty ());
    assertTrue (x.isEmpty ());
    assertTrue (x.setAttribute ("key", "value").isChanged ());
    assertFalse (x.isEmpty ());
    assertEquals (1, x.getAttributeCount ());
    assertTrue (x.setAttribute ("key2", "value2").isChanged ());
    assertTrue (x.setAttribute ("key", "value3").isChanged ());
    assertFalse (x.setAttribute ("key", "value3").isChanged ());
    assertEquals ("value2", x.getAttributeObject ("key2"));
    assertEquals ("value2", x.getAttributeAsString ("key2"));
    assertEquals ("value2", x.<String> getCastedAttribute ("key2"));
    assertEquals ("value2", x.getTypedAttribute ("key2", String.class));
    assertEquals ("def", x.<String> getCastedAttribute ("key none", "def"));
    assertEquals ("def", x.getTypedAttribute ("key none", String.class, "def"));
    assertTrue (x.containsAttribute ("key2"));
    assertTrue (x.clear ().isChanged ());
    assertFalse (x.clear ().isChanged ());
    assertFalse (x.containsAttribute ("key2"));
    assertTrue (x.getAllAttributes ().isEmpty ());
    assertTrue (x.getAllAttributeNames ().isEmpty ());
    assertFalse (x.removeAttribute ("key2").isChanged ());

    assertTrue (x.setAttribute ("key", Integer.valueOf (17)).isChanged ());
    assertTrue (x.getAttributeAsBoolean ("key"));
    assertEquals (17, x.getAttributeAsInt ("key"));
    assertEquals (17, x.getAttributeAsLong ("key"));
    assertEquals (CGlobal.ILLEGAL_UINT, x.getAttributeAsInt ("key2"));
    assertEquals (Integer.valueOf (17), x.getCastedAttribute ("key"));
    CommonsAssert.assertEquals (17, x.getAttributeAsDouble ("key"));
    CommonsAssert.assertEquals (CGlobal.ILLEGAL_DOUBLE, x.getAttributeAsDouble ("key2"));
    assertEquals (new BigInteger ("17"), x.getAttributeAsBigInteger ("key"));
    assertEquals (new BigDecimal ("17.0"), x.getAttributeAsBigDecimal ("key"));
    assertFalse (x.getAttributeAsBoolean ("key2"));
    assertTrue (x.removeAttribute ("key").isChanged ());
    assertFalse (x.removeAttribute ("key").isChanged ());

    // Check null values
    assertTrue (x.setAttribute ("null1", null).isUnchanged ());
    assertNull (x.getAttributeObject ("null1"));
    assertFalse (x.containsAttribute ("null1"));
    assertTrue (x.removeAttribute ("null1").isUnchanged ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new MapBasedAttributeContainerAny <String> (),
                                                                       new MapBasedAttributeContainerAny <String> ());
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new MapBasedAttributeContainerAny <String> (CollectionHelper.newMap (new String [] { "key",
                                                                                                                                                            "key2" },
                                                                                                                                            new Object [] { "value",
                                                                                                                                                            "value2" })),
                                                                       new MapBasedAttributeContainerAny <String> (CollectionHelper.newMap (new String [] { "key",
                                                                                                                                                            "key2" },
                                                                                                                                            new Object [] { "value",
                                                                                                                                                            "value2" })));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new MapBasedAttributeContainerAny <String> (CollectionHelper.newMap (new String [] { "key",
                                                                                                                                                                "key2" },
                                                                                                                                                new Object [] { "value",
                                                                                                                                                                "value2" })),
                                                                           new MapBasedAttributeContainerAny <String> (CollectionHelper.newMap (new String [] { "key",
                                                                                                                                                                "key2" },
                                                                                                                                                new Object [] { "value",
                                                                                                                                                                "value" })));

    try
    {
      new MapBasedAttributeContainerAny <String> ((Map <String, Object>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetAndSetAttributeFlag ()
  {
    final MapBasedAttributeContainerAny <String> aCont = new MapBasedAttributeContainerAny <String> ();
    // Not yet present
    assertFalse (aCont.getAndSetAttributeFlag ("any"));
    // Now it is present
    assertTrue (aCont.getAndSetAttributeFlag ("any"));
    for (int i = 0; i < 20; ++i)
      assertTrue (aCont.getAndSetAttributeFlag ("any"));
  }
}

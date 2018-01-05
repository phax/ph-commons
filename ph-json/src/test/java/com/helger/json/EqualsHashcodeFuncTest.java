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

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.CommonsTreeMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.json.convert.JsonConverter;
import com.helger.json.serialize.JsonReader;

/**
 * Test class for equals and hashCode.
 *
 * @author Philip Helger
 */
public final class EqualsHashcodeFuncTest
{
  private void _testEqualsHashcode (@Nullable final Object aValue)
  {
    final IJson aJson = JsonConverter.convertToJson (aValue);
    assertNotNull ("Failed: " + aValue, aJson);
    final String sJson = aJson.getAsJsonString ();
    assertNotNull (sJson);
    final IJson aJsonRead = JsonReader.readFromString (sJson);
    assertNotNull ("Failed to read: " + sJson, aJsonRead);
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (aJson, aJsonRead);
  }

  @Test
  public void testSimpleValues ()
  {
    _testEqualsHashcode (Integer.valueOf (Integer.MIN_VALUE));
    _testEqualsHashcode (Integer.valueOf (Integer.MAX_VALUE));
    _testEqualsHashcode (Long.valueOf (Long.MIN_VALUE));
    _testEqualsHashcode (Long.valueOf (Long.MAX_VALUE));
    _testEqualsHashcode ("a string");
    _testEqualsHashcode ("a string/with/a/slash");
    _testEqualsHashcode (Double.valueOf (3.1234));
    _testEqualsHashcode (new BigDecimal ("3.12341234123412341234123412341234123412341234"));
  }

  @Test
  public void testArray ()
  {
    _testEqualsHashcode (new boolean [] { true, false, true });
    _testEqualsHashcode (new byte [] { 0, 1, 2 });
    _testEqualsHashcode (new char [] { 'a', 'b', 'c' });
    _testEqualsHashcode (new int [] { 1, 2, 3 });
    _testEqualsHashcode (new long [] { 1, 2, 3 });
    _testEqualsHashcode (new short [] { 1, 2, 3 });
    _testEqualsHashcode (new String [] { "foo", "bar", "bla" });
    _testEqualsHashcode (new CommonsArrayList <> (Double.valueOf (3.1234),
                                                  Double.valueOf (4.1415),
                                                  Double.valueOf (5.1415)));
  }

  @Test
  public void testMap ()
  {
    final ICommonsMap <String, Object> aMap = new CommonsHashMap <> ();
    aMap.put ("foo", "bar");
    _testEqualsHashcode (aMap);

    final ICommonsMap <String, Object> aTreeMap = new CommonsTreeMap <> ();
    aTreeMap.put ("foo", "bar");
    aTreeMap.put ("foo2", Integer.valueOf (5));
    _testEqualsHashcode (aTreeMap);

    final ICommonsMap <String, Object> aLinkedMap = new CommonsLinkedHashMap <> ();
    aLinkedMap.put ("foo", "bar");
    aLinkedMap.put ("foo2", Integer.valueOf (5));
    _testEqualsHashcode (aLinkedMap);
  }
}

/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.commons.jmx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Hashtable;
import java.util.Map;

import javax.management.ObjectName;

import org.junit.Test;

import com.helger.collection.commons.CommonsHashMap;

/**
 * Test class for class {@link ObjectNameHelper}.
 *
 * @author Philip Helger
 */
public final class ObjectNameHelperTest
{
  @Test
  public void testDefaultJMXDomain ()
  {
    assertEquals (CJMX.PH_JMX_DOMAIN, ObjectNameHelper.getDefaultJMXDomain ());
    ObjectNameHelper.setDefaultJMXDomain ("abc");
    assertEquals ("abc", ObjectNameHelper.getDefaultJMXDomain ());

    try
    {
      // Space not allowed
      ObjectNameHelper.setDefaultJMXDomain ("ab c");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      // Colon not allowed
      ObjectNameHelper.setDefaultJMXDomain ("ab:c");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
    try
    {
      // null not allowed
      ObjectNameHelper.setDefaultJMXDomain (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      // empty string not allowed
      ObjectNameHelper.setDefaultJMXDomain ("");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    assertEquals ("abc", ObjectNameHelper.getDefaultJMXDomain ());
  }

  @Test
  public void testGetCleanPropertyValue ()
  {
    assertEquals ("abc", ObjectNameHelper.getCleanPropertyValue ("abc"));
    // ":" and "," are replaced by "."
    assertEquals ("a.b.c", ObjectNameHelper.getCleanPropertyValue ("a:b,c"));
    // "//" is replaced by "__"
    assertEquals ("a__b", ObjectNameHelper.getCleanPropertyValue ("a//b"));
    // Values with a blank are quoted
    assertEquals ("\"a b\"", ObjectNameHelper.getCleanPropertyValue ("a b"));
  }

  @Test
  public void testCreate ()
  {
    final String sOldDomain = ObjectNameHelper.getDefaultJMXDomain ();
    try
    {
      ObjectNameHelper.setDefaultJMXDomain (CJMX.PH_JMX_DOMAIN);

      final Map <String, String> aParams = new CommonsHashMap <> ();
      aParams.put (CJMX.PROPERTY_TYPE, "MyType");
      final ObjectName aON = ObjectNameHelper.create (aParams);
      assertEquals (CJMX.PH_JMX_DOMAIN, aON.getDomain ());
      assertEquals ("MyType", aON.getKeyProperty (CJMX.PROPERTY_TYPE));

      // Same result with a Hashtable
      assertEquals (aON, ObjectNameHelper.create (new Hashtable <> (aParams)));

      // An empty map is not allowed
      try
      {
        ObjectNameHelper.create (new CommonsHashMap <> ());
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {
        // expected
      }

      // An invalid property value leads to an exception
      try
      {
        final Hashtable <String, String> aInvalid = new Hashtable <> ();
        aInvalid.put (CJMX.PROPERTY_TYPE, "a:b");
        ObjectNameHelper.create (aInvalid);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {
        // expected
      }
    }
    finally
    {
      ObjectNameHelper.setDefaultJMXDomain (sOldDomain);
    }
  }

  @Test
  public void testCreateWithDefaultProperties ()
  {
    final ObjectName aON = ObjectNameHelper.createWithDefaultProperties (this);
    assertEquals ("ObjectNameHelperTest", aON.getKeyProperty (CJMX.PROPERTY_TYPE));
    assertNull (aON.getKeyProperty (CJMX.PROPERTY_NAME));

    final ObjectName aON2 = ObjectNameHelper.createWithDefaultProperties (this, "any name");
    assertEquals ("ObjectNameHelperTest", aON2.getKeyProperty (CJMX.PROPERTY_TYPE));
    assertEquals ("\"any name\"", aON2.getKeyProperty (CJMX.PROPERTY_NAME));
  }
}

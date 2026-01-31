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
package com.helger.http.header.specific;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.unittest.support.TestHelper;

/**
 * Test class for {@link HttpForwardedHeaderHop}.
 *
 * @author Philip Helger
 */
public final class HttpForwardedHeaderHopTest
{
  @Test
  public void testBasicFunctionality ()
  {
    final HttpForwardedHeaderHop aHop = new HttpForwardedHeaderHop ();
    assertTrue (aHop.isEmpty ());
    assertFalse (aHop.isNotEmpty ());
    assertEquals (0, aHop.size ());
    assertEquals ("", aHop.getAsString ());

    // Add a basic pair
    aHop.setFor ("192.168.1.1");
    assertFalse (aHop.isEmpty ());
    assertTrue (aHop.isNotEmpty ());
    assertEquals (1, aHop.size ());
    assertTrue (aHop.containsFor ());
    assertEquals ("192.168.1.1", aHop.getFor ());
    assertEquals ("for=192.168.1.1", aHop.getAsString ());
  }

  @Test
  public void testStandardParameters ()
  {
    final HttpForwardedHeaderHop aHop = new HttpForwardedHeaderHop ();

    // Test "for" parameter
    aHop.setFor ("192.168.1.1");
    assertEquals ("192.168.1.1", aHop.getFor ());

    // Test "host" parameter
    aHop.setHost ("example.com");
    assertEquals ("example.com", aHop.getHost ());

    // Test "by" parameter
    aHop.setBy ("proxy.example.com");
    assertEquals ("proxy.example.com", aHop.getBy ());

    // Test "proto" parameter
    aHop.setProto ("https");
    assertEquals ("https", aHop.getProto ());

    // Check string representation
    final String sResult = aHop.getAsString ();
    assertTrue (sResult.contains ("for=192.168.1.1"));
    assertTrue (sResult.contains ("host=example.com"));
    assertTrue (sResult.contains ("by=proxy.example.com"));
    assertTrue (sResult.contains ("proto=https"));
  }

  @Test
  public void testQuotedValues ()
  {
    final HttpForwardedHeaderHop aHop = new HttpForwardedHeaderHop ();

    // Add a value that needs quoting (contains colon which is not a valid token char)
    aHop.setFor ("192.168.1.1:8080");
    aHop.setHost ("example.com with spaces");

    final String sResult = aHop.getAsString ();
    // Colon is not a valid token character, so the IP:port should be quoted
    assertTrue (sResult.contains ("for=\"192.168.1.1:8080\""));
    assertTrue (sResult.contains ("host=\"example.com with spaces\""));
  }

  @Test
  public void testIPv6Address ()
  {
    final HttpForwardedHeaderHop aHop = new HttpForwardedHeaderHop ();

    // IPv6 addresses in brackets should not be quoted
    aHop.setFor ("[2001:db8::1]");
    assertEquals ("for=\"[2001:db8::1]\"", aHop.getAsString ());
  }

  @Test
  public void testRemovePair ()
  {
    final HttpForwardedHeaderHop aHop = new HttpForwardedHeaderHop ();
    aHop.setFor ("192.168.1.1");
    aHop.setHost ("example.com");

    assertEquals (2, aHop.size ());
    assertTrue (aHop.removePair (HttpForwardedHeaderHop.PARAM_FOR).isChanged ());
    assertEquals (1, aHop.size ());
    assertFalse (aHop.containsFor ());
    assertNull (aHop.getFor ());

    // Remove non-existing key
    assertTrue (aHop.removePair ("nonexisting").isUnchanged ());
  }

  @Test
  public void testRemoveAll ()
  {
    final HttpForwardedHeaderHop aHop = new HttpForwardedHeaderHop ();
    aHop.setFor ("192.168.1.1");
    aHop.setHost ("example.com");

    assertEquals (2, aHop.size ());
    aHop.removeAll ();
    assertEquals (0, aHop.size ());
    assertTrue (aHop.isEmpty ());
  }

  @Test
  public void testGetAllTokensAndPairs ()
  {
    final HttpForwardedHeaderHop aHop = new HttpForwardedHeaderHop ();
    aHop.setFor ("192.168.1.1");
    aHop.setHost ("example.com");

    assertEquals (2, aHop.getAllTokens ().size ());
    assertTrue (aHop.getAllTokens ().contains (HttpForwardedHeaderHop.PARAM_FOR));
    assertTrue (aHop.getAllTokens ().contains (HttpForwardedHeaderHop.PARAM_HOST));

    assertEquals (2, aHop.getAllPairs ().size ());
    assertEquals ("192.168.1.1", aHop.getAllPairs ().get (HttpForwardedHeaderHop.PARAM_FOR));
    assertEquals ("example.com", aHop.getAllPairs ().get (HttpForwardedHeaderHop.PARAM_HOST));
  }

  @Test
  public void testToString ()
  {
    final HttpForwardedHeaderHop aHop = new HttpForwardedHeaderHop ();
    aHop.setProto ("https");
    aHop.setFor ("192.168.1.1");

    assertNotNull (aHop.toString ());
  }

  @Test (expected = IllegalArgumentException.class)
  public void testInvalidToken ()
  {
    final HttpForwardedHeaderHop aHop = new HttpForwardedHeaderHop ();
    // This should fail because "for=" contains an invalid character
    aHop.addPair ("for=", "value");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testEmptyToken ()
  {
    final HttpForwardedHeaderHop aHop = new HttpForwardedHeaderHop ();
    aHop.addPair ("", "value");
  }

  @Test (expected = NullPointerException.class)
  public void testNullValue ()
  {
    final HttpForwardedHeaderHop aHop = new HttpForwardedHeaderHop ();
    aHop.setFor (null);
  }

  @Test
  public void testEqualsAndHashCode ()
  {
    // Test with empty objects
    final HttpForwardedHeaderHop aList1 = new HttpForwardedHeaderHop ();
    final HttpForwardedHeaderHop aList2 = new HttpForwardedHeaderHop ();
    TestHelper.testDefaultImplementationWithEqualContentObject (aList1, aList2);

    // Test with identical content
    aList1.setFor ("192.168.1.1");
    aList1.setHost ("example.com");
    aList1.setProto ("https");

    aList2.setFor ("192.168.1.1");
    aList2.setHost ("example.com");
    aList2.setProto ("https");
    TestHelper.testDefaultImplementationWithEqualContentObject (aList1, aList2);

    // Test with different content
    final HttpForwardedHeaderHop aList3 = new HttpForwardedHeaderHop ();
    // Different IP
    aList3.setFor ("192.168.1.2");
    aList3.setHost ("example.com");
    aList3.setProto ("https");
    TestHelper.testDefaultImplementationWithDifferentContentObject (aList1, aList3);

    // Test with different number of pairs
    final HttpForwardedHeaderHop aList4 = new HttpForwardedHeaderHop ();
    aList4.setFor ("192.168.1.1");
    aList4.setHost ("example.com");
    // Missing proto parameter
    TestHelper.testDefaultImplementationWithDifferentContentObject (aList1, aList4);

    // Test with same values but different order (are considered equals)
    // Problem lays in the LinkedHashMap implementation of equals and hashCode
    final HttpForwardedHeaderHop aList5 = new HttpForwardedHeaderHop ();
    // Different order
    aList5.setProto ("https");
    aList5.setHost ("example.com");
    aList5.setFor ("192.168.1.1");
    TestHelper.testDefaultImplementationWithEqualContentObject (aList1, aList5);

    // Test with custom parameters
    final HttpForwardedHeaderHop aList6 = new HttpForwardedHeaderHop ();
    aList6.addPair ("custom", "value1");
    aList6.addPair ("another", "value2");

    final HttpForwardedHeaderHop aList7 = new HttpForwardedHeaderHop ();
    aList7.addPair ("custom", "value1");
    aList7.addPair ("another", "value2");
    TestHelper.testDefaultImplementationWithEqualContentObject (aList6, aList7);

    // Test with same keys but different values
    final HttpForwardedHeaderHop aList8 = new HttpForwardedHeaderHop ();
    aList8.addPair ("custom", "different_value");
    aList8.addPair ("another", "value2");
    TestHelper.testDefaultImplementationWithDifferentContentObject (aList6, aList8);
  }
}

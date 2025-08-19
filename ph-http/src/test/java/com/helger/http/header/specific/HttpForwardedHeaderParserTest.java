/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.collection.commons.ICommonsList;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for {@link HttpForwardedHeaderParser}.
 *
 * @author Philip Helger
 */
public final class HttpForwardedHeaderParserTest
{
  @Test
  public void testValidSinglePair ()
  {
    final HttpForwardedHeaderHop aResult = HttpForwardedHeaderParser.parseSingleHop ("for=192.168.1.1");
    assertNotNull (aResult);
    assertEquals (1, aResult.size ());
    assertEquals ("192.168.1.1", aResult.getFor ());
  }

  @Test
  public void testValidSinglePairCaseInsensitive ()
  {
    final HttpForwardedHeaderHop aResult = HttpForwardedHeaderParser.parseSingleHop ("FOR=192.168.1.1");
    assertNotNull (aResult);
    assertEquals (1, aResult.size ());
    assertEquals ("192.168.1.1", aResult.getFor ());
  }

  @Test
  public void testValidMultiplePairs ()
  {
    final HttpForwardedHeaderHop aResult = HttpForwardedHeaderParser.parseSingleHop ("for=192.168.1.1;host=example.com;proto=https");
    assertNotNull (aResult);
    assertEquals (3, aResult.size ());
    assertEquals ("192.168.1.1", aResult.getFor ());
    assertEquals ("example.com", aResult.getHost ());
    assertEquals ("https", aResult.getProto ());
  }

  @Test
  public void testQuotedValues ()
  {
    final HttpForwardedHeaderHop aResult = HttpForwardedHeaderParser.parseSingleHop ("for=\"192.168.1.1:8080\";host=\"example.com with spaces\"");
    assertNotNull (aResult);
    assertEquals (2, aResult.size ());
    assertEquals ("192.168.1.1:8080", aResult.getFor ());
    assertEquals ("example.com with spaces", aResult.getHost ());
  }

  @Test
  public void testEscapedCharacters ()
  {
    final HttpForwardedHeaderHop aResult = HttpForwardedHeaderParser.parseSingleHop ("for=\"test\\\"value\\\\\";host=\"simple\"");
    assertNotNull (aResult);
    assertEquals (2, aResult.size ());
    assertEquals ("test\"value\\", aResult.getFor ());
    assertEquals ("simple", aResult.getHost ());
  }

  @Test
  public void testWhitespaceHandling ()
  {
    // Basic case without whitespace
    HttpForwardedHeaderHop aResult = HttpForwardedHeaderParser.parseSingleHop ("for=192.168.1.1;host=example.com");
    assertNotNull (aResult);
    assertEquals (2, aResult.size ());
    assertEquals ("192.168.1.1", aResult.getFor ());
    assertEquals ("example.com", aResult.getHost ());

    // Test with whitespace around semicolons
    aResult = HttpForwardedHeaderParser.parseSingleHop ("for=192.168.1.1 ; host=example.com");
    assertNotNull (aResult);
    assertEquals (2, aResult.size ());
    assertEquals ("192.168.1.1", aResult.getFor ());
    assertEquals ("example.com", aResult.getHost ());

    // Test with leading/trailing whitespace
    aResult = HttpForwardedHeaderParser.parseSingleHop ("  for=192.168.1.1;host=example.com  ");
    assertNotNull (aResult);
    assertEquals (2, aResult.size ());
    assertEquals ("192.168.1.1", aResult.getFor ());
    assertEquals ("example.com", aResult.getHost ());
  }

  @Test
  public void testIPv6Support ()
  {
    final HttpForwardedHeaderHop aResult = HttpForwardedHeaderParser.parseSingleHop ("for=\"[2001:db8::1]\";proto=https");
    assertNotNull (aResult);
    assertEquals (2, aResult.size ());
    assertEquals ("[2001:db8::1]", aResult.getFor ());
    assertEquals ("https", aResult.getProto ());
  }

  @Test
  public void testCustomParameters ()
  {
    final HttpForwardedHeaderHop aResult = HttpForwardedHeaderParser.parseSingleHop ("for=192.168.1.1;custom=value123;another=\"quoted value\"");
    assertNotNull (aResult);
    assertEquals (3, aResult.size ());
    assertEquals ("192.168.1.1", aResult.getFor ());
    assertEquals ("value123", aResult.getFirstValue ("custom"));
    assertEquals ("quoted value", aResult.getFirstValue ("another"));
  }

  @Test
  public void testEmptyInput ()
  {
    HttpForwardedHeaderHop aResult = HttpForwardedHeaderParser.parseSingleHop ("");
    assertNotNull (aResult);
    assertTrue (aResult.isEmpty ());

    aResult = HttpForwardedHeaderParser.parseSingleHop ("   ");
    assertNotNull (aResult);
    assertTrue (aResult.isEmpty ());

    assertNotNull (HttpForwardedHeaderParser.parseSingleHop (null));
    assertTrue (HttpForwardedHeaderParser.parseSingleHop (null).isEmpty ());
  }

  @Test
  public void testIPv6Address ()
  {
    final HttpForwardedHeaderHop aResult = HttpForwardedHeaderParser.parseSingleHop ("for=\"[2001:db8::1]\";proto=https");
    assertNotNull (aResult);
    assertEquals (2, aResult.size ());
    assertEquals ("[2001:db8::1]", aResult.getFor ());
    assertEquals ("https", aResult.getProto ());
  }

  @Test
  public void testCustomParametersAlternate ()
  {
    final HttpForwardedHeaderHop aResult = HttpForwardedHeaderParser.parseSingleHop ("for=192.168.1.1;custom=value123;another=\"quoted value\"");
    assertNotNull (aResult);
    assertEquals (3, aResult.size ());
    assertEquals ("192.168.1.1", aResult.getFor ());
    assertEquals ("value123", aResult.getFirstValue ("custom"));
    assertEquals ("quoted value", aResult.getFirstValue ("another"));
  }

  @Test
  public void testEmptyString ()
  {
    HttpForwardedHeaderHop aResult = HttpForwardedHeaderParser.parseSingleHop ("");
    assertNotNull (aResult);
    assertTrue (aResult.isEmpty ());

    aResult = HttpForwardedHeaderParser.parseSingleHop ("   ");
    assertNotNull (aResult);
    assertTrue (aResult.isEmpty ());

    assertNotNull (HttpForwardedHeaderParser.parseSingleHop (null));
    assertTrue (HttpForwardedHeaderParser.parseSingleHop (null).isEmpty ());
  }

  @Test
  public void testEmptyPairs ()
  {
    // A standalone semicolon is actually invalid according to RFC 7239
    // The grammar is: forwarded-element = [ forwarded-pair ] *( ";" [ forwarded-pair ] )
    // So ";" alone would be invalid
    assertNull (HttpForwardedHeaderParser.parseMultipleHops (";"));
  }

  @Test
  public void testTrailingSemicolon ()
  {
    final HttpForwardedHeaderHop aResult = HttpForwardedHeaderParser.parseSingleHop ("for=192.168.1.1;");
    assertNotNull (aResult);
    assertEquals (1, aResult.size ());
    assertEquals ("192.168.1.1", aResult.getFor ());
  }

  @Test
  public void testMultipleSemicolons ()
  {
    // Multiple consecutive semicolons should be invalid
    // ";;" means there's an empty pair between semicolons
    final HttpForwardedHeaderHop aHop = HttpForwardedHeaderParser.parseSingleHop ("for=192.168.1.1;;host=example.com");
    assertNotNull (aHop);
    assertEquals (2, aHop.size ());
    assertEquals ("192.168.1.1", aHop.getFor ());
    assertEquals ("example.com", aHop.getHost ());
  }

  // Error cases - all should return null

  @Test
  public void testInvalidToken ()
  {
    assertNull (HttpForwardedHeaderParser.parseSingleHop ("for@invalid=192.168.1.1"));
    assertNull (HttpForwardedHeaderParser.parseSingleHop ("for=value;host@invalid=example.com"));
  }

  @Test
  public void testMissingEquals ()
  {
    assertNull (HttpForwardedHeaderParser.parseSingleHop ("for192.168.1.1"));
    assertNull (HttpForwardedHeaderParser.parseSingleHop ("for=192.168.1.1;hostexample.com"));
  }

  @Test
  public void testMissingValue ()
  {
    assertNull (HttpForwardedHeaderParser.parseSingleHop ("for="));
    assertNull (HttpForwardedHeaderParser.parseSingleHop ("for=192.168.1.1;host="));
  }

  @Test
  public void testUnterminatedQuotedString ()
  {
    assertNull (HttpForwardedHeaderParser.parseSingleHop ("for=\"192.168.1.1"));
    assertNull (HttpForwardedHeaderParser.parseSingleHop ("for=\"192.168.1.1;host=example.com"));
  }

  @Test
  public void testInvalidQuotedStringCharacters ()
  {
    // Control characters are not allowed in quoted strings
    assertNull (HttpForwardedHeaderParser.parseSingleHop ("for=\"test\u0001value\""));
    assertNull (HttpForwardedHeaderParser.parseSingleHop ("for=\"test\u007f\""));
  }

  @Test
  public void testIncompleteEscapeSequence ()
  {
    assertNull (HttpForwardedHeaderParser.parseSingleHop ("for=\"test\\"));
  }

  @Test
  public void testInvalidTokenCharacters ()
  {
    assertNull (HttpForwardedHeaderParser.parseSingleHop ("for with space=value"));
    assertNull (HttpForwardedHeaderParser.parseSingleHop ("for=value;host(invalid)=example.com"));
  }

  @Test
  public void testRealWorldExamples ()
  {
    // Example from RFC 7239
    HttpForwardedHeaderHop aResult = HttpForwardedHeaderParser.parseSingleHop ("for=192.0.2.60;proto=http;by=203.0.113.43");
    assertNotNull (aResult);
    assertEquals (3, aResult.size ());
    assertEquals ("192.0.2.60", aResult.getFor ());
    assertEquals ("http", aResult.getProto ());
    assertEquals ("203.0.113.43", aResult.getBy ());

    // Another RFC example
    aResult = HttpForwardedHeaderParser.parseSingleHop ("for=192.0.2.43");
    assertNotNull (aResult);
    assertEquals (1, aResult.size ());
    assertEquals ("192.0.2.43", aResult.getFor ());

    // Complex quoted example
    aResult = HttpForwardedHeaderParser.parseSingleHop ("for=\"[2001:db8:cafe::17]:4711\"");
    assertNotNull (aResult);
    assertEquals (1, aResult.size ());
    assertEquals ("[2001:db8:cafe::17]:4711", aResult.getFor ());
  }

  @Test
  public void testRoundTripConversion ()
  {
    // Test that parsing and then converting back to string works
    for (final String sOriginal : new String [] { "for=\"192.168.1.1:8080\"",
                                                  "for=\"192.168.1.1:8080\";host=\"example.com with spaces\"",
                                                  "for=\"192.168.1.1:8080\";host=example.com;proto=https",
                                                  "for=\"192.168.1.1:8080\";host=\"example.com with spaces\";proto=https",
                                                  "for=\"192.168.1.1:8080\";host=\"example.com with spaces\";proto=https;custom1=value1",
                                                  "for=\"_gazonk\"",
                                                  "For=\"[2001:db8:cafe::17]:4711\"",
                                                  "for=192.0.2.60;proto=http;by=203.0.113.43" })
    {
      // Parse
      final HttpForwardedHeaderHop aParsed = HttpForwardedHeaderParser.parseSingleHop (sOriginal);
      assertNotNull (aParsed);

      // Format
      final String sRecreated = aParsed.getAsString ();
      assertNotNull (sRecreated);

      // Parse the recreated string to verify it's still valid
      final HttpForwardedHeaderHop aReparsed = HttpForwardedHeaderParser.parseSingleHop (sRecreated);
      assertNotNull (aReparsed);

      // Check they are equal
      TestHelper.testDefaultImplementationWithEqualContentObject (aParsed, aReparsed);
    }
  }

  // Tests for multiple hops parsing functionality

  @Test
  public void testMultipleHopsBasic ()
  {
    final ICommonsList <HttpForwardedHeaderHop> aHops = HttpForwardedHeaderParser.parseMultipleHops ("for=192.168.1.1, for=192.168.1.2");
    assertNotNull (aHops);
    assertEquals (2, aHops.size ());

    assertEquals ("192.168.1.1", aHops.get (0).getFor ());
    assertEquals ("192.168.1.2", aHops.get (1).getFor ());
  }

  @Test
  public void testMultipleHopsComplex ()
  {
    final ICommonsList <HttpForwardedHeaderHop> aHops = HttpForwardedHeaderParser.parseMultipleHops ("for=192.168.1.1;proto=https, for=192.168.1.2;host=example.com, for=\"[2001:db8::1]\";proto=http");
    assertNotNull (aHops);
    assertEquals (3, aHops.size ());

    // First hop
    assertEquals ("192.168.1.1", aHops.get (0).getFor ());
    assertEquals ("https", aHops.get (0).getProto ());

    // Second hop
    assertEquals ("192.168.1.2", aHops.get (1).getFor ());
    assertEquals ("example.com", aHops.get (1).getHost ());

    // Third hop
    assertEquals ("[2001:db8::1]", aHops.get (2).getFor ());
    assertEquals ("http", aHops.get (2).getProto ());
  }

  @Test
  public void testMultipleHopsWithWhitespace ()
  {
    final ICommonsList <HttpForwardedHeaderHop> aHops = HttpForwardedHeaderParser.parseMultipleHops ("for=192.168.1.1 , for=192.168.1.2 ;host=example.com,  for=192.168.1.3");
    assertNotNull (aHops);
    assertEquals (3, aHops.size ());

    assertEquals ("192.168.1.1", aHops.get (0).getFor ());
    assertEquals ("192.168.1.2", aHops.get (1).getFor ());
    assertEquals ("example.com", aHops.get (1).getHost ());
    assertEquals ("192.168.1.3", aHops.get (2).getFor ());
  }

  @Test
  public void testMultipleHopsWithQuotedStringsContainingCommas ()
  {
    final ICommonsList <HttpForwardedHeaderHop> aHops = HttpForwardedHeaderParser.parseMultipleHops ("for=\"test, with comma\";proto=https, for=192.168.1.1");
    assertNotNull (aHops);
    assertEquals (2, aHops.size ());

    assertEquals ("test, with comma", aHops.get (0).getFor ());
    assertEquals ("https", aHops.get (0).getProto ());
    assertEquals ("192.168.1.1", aHops.get (1).getFor ());
  }

  @Test
  public void testMultipleHopsWithEscapedQuotes ()
  {
    final ICommonsList <HttpForwardedHeaderHop> aHops = HttpForwardedHeaderParser.parseMultipleHops ("for=\"test\\\"value\", for=\"another\\\"test\"");
    assertNotNull (aHops);
    assertEquals (2, aHops.size ());

    assertEquals ("test\"value", aHops.get (0).getFor ());
    assertEquals ("another\"test", aHops.get (1).getFor ());
  }

  @Test
  public void testMultipleHopsSingleHop ()
  {
    final ICommonsList <HttpForwardedHeaderHop> aHops = HttpForwardedHeaderParser.parseMultipleHops ("for=192.168.1.1;host=example.com;proto=https");
    assertNotNull (aHops);
    assertEquals (1, aHops.size ());

    assertEquals ("192.168.1.1", aHops.get (0).getFor ());
    assertEquals ("example.com", aHops.get (0).getHost ());
    assertEquals ("https", aHops.get (0).getProto ());
  }

  @Test
  public void testMultipleHopsEmptyInput ()
  {
    ICommonsList <HttpForwardedHeaderHop> aHops = HttpForwardedHeaderParser.parseMultipleHops ("");
    assertNotNull (aHops);
    assertTrue (aHops.isEmpty ());

    aHops = HttpForwardedHeaderParser.parseMultipleHops ("   ");
    assertNotNull (aHops);
    assertTrue (aHops.isEmpty ());

    aHops = HttpForwardedHeaderParser.parseMultipleHops (null);
    assertNotNull (aHops);
    assertTrue (aHops.isEmpty ());
  }

  @Test
  public void testMultipleHopsInvalidElement ()
  {
    // If one element is invalid, the entire parsing should fail
    assertNull (HttpForwardedHeaderParser.parseMultipleHops ("for=192.168.1.1, invalid@token=value"));
    assertNull (HttpForwardedHeaderParser.parseMultipleHops ("for=192.168.1.1, for=\"unterminated"));
    assertNotNull (HttpForwardedHeaderParser.parseMultipleHops ("for=192.168.1.1;;host=example.com, for=192.168.1.2"));
  }

  @Test
  public void testMultipleHopsRFC7239Examples ()
  {
    // Example from RFC 7239 Section 4 showing multiple hops
    final ICommonsList <HttpForwardedHeaderHop> aHops = HttpForwardedHeaderParser.parseMultipleHops ("for=192.0.2.60;proto=http;by=203.0.113.43, for=203.0.113.43");
    assertNotNull (aHops);
    assertEquals (2, aHops.size ());

    // First hop
    assertEquals ("192.0.2.60", aHops.get (0).getFor ());
    assertEquals ("http", aHops.get (0).getProto ());
    assertEquals ("203.0.113.43", aHops.get (0).getBy ());

    // Second hop
    assertEquals ("203.0.113.43", aHops.get (1).getFor ());
  }

  @Test
  public void testMultipleHopsTrailingComma ()
  {
    final ICommonsList <HttpForwardedHeaderHop> aHops = HttpForwardedHeaderParser.parseMultipleHops ("for=192.168.1.1, for=192.168.1.2,");
    assertNotNull (aHops);
    assertEquals (2, aHops.size ());

    assertEquals ("192.168.1.1", aHops.get (0).getFor ());
    assertEquals ("192.168.1.2", aHops.get (1).getFor ());
  }

  @Test
  public void testMultipleHopsLeadingComma ()
  {
    final ICommonsList <HttpForwardedHeaderHop> aHops = HttpForwardedHeaderParser.parseMultipleHops (", for=192.168.1.1, for=192.168.1.2");
    assertNotNull (aHops);
    assertEquals (2, aHops.size ());

    assertEquals ("192.168.1.1", aHops.get (0).getFor ());
    assertEquals ("192.168.1.2", aHops.get (1).getFor ());
  }

  @Test
  public void testMultipleHopsEmptyElements ()
  {
    final ICommonsList <HttpForwardedHeaderHop> aHops = HttpForwardedHeaderParser.parseMultipleHops ("for=192.168.1.1,, for=192.168.1.2");
    assertNotNull (aHops);
    assertEquals (2, aHops.size ());

    assertEquals ("192.168.1.1", aHops.get (0).getFor ());
    assertEquals ("192.168.1.2", aHops.get (1).getFor ());
  }
}

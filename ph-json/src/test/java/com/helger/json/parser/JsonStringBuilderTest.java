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
package com.helger.json.parser;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

/**
 * Test class for class {@link JsonStringBuilder}.
 *
 * @author Philip Helger
 */
public final class JsonStringBuilderTest
{
  @Test
  public void testAppendAndGet ()
  {
    final JsonStringBuilder aSB = new JsonStringBuilder ();
    assertEquals (0, aSB.getLength ());
    assertEquals ("", aSB.getAsString ());

    aSB.append ('a');
    aSB.append ('b');
    aSB.append ('c');
    assertEquals (3, aSB.getLength ());
    assertEquals ("abc", aSB.getAsString ());
    assertEquals ('b', aSB.charAt (1));
  }

  @Test
  public void testBackupInvalidatesCache ()
  {
    final JsonStringBuilder aSB = new JsonStringBuilder ();
    aSB.append ('a');
    aSB.append ('b');
    // Populate the internal String cache
    assertEquals ("ab", aSB.getAsString ());

    // Remove the last char - the previously cached value must not be returned
    aSB.backup (1);
    assertEquals (1, aSB.getLength ());
    assertEquals ("a", aSB.getAsString ());
  }

  @Test
  public void testResetInvalidatesCache ()
  {
    final JsonStringBuilder aSB = new JsonStringBuilder ();
    aSB.append ('x');
    assertEquals ("x", aSB.getAsString ());

    aSB.reset ();
    assertEquals (0, aSB.getLength ());
    assertEquals ("", aSB.getAsString ());
  }

  @Test
  public void testNumericConversions ()
  {
    final JsonStringBuilder aSB = new JsonStringBuilder ();
    for (final char c : "12345".toCharArray ())
      aSB.append (c);
    assertEquals (BigInteger.valueOf (12345), aSB.getAsBigInteger ());
    assertEquals (Double.valueOf (12345), aSB.getAsDouble ());
    assertEquals ("12345", aSB.getAsBigDecimal ().toPlainString ());
  }
}

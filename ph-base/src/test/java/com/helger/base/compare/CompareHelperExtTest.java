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
package com.helger.base.compare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;
import java.util.Locale;
import java.util.Random;

import org.junit.Test;

/**
 * Additional test class for class {@link CompareHelper}, covering the primitive and the null aware
 * overloads.
 *
 * @author Philip Helger
 */
public final class CompareHelperExtTest
{
  @Test
  public void testCompareBooleans ()
  {
    // "true" sorts before "false"
    assertTrue (CompareHelper.compareTrueBeforeFalse (true, false) < 0);
    assertTrue (CompareHelper.compareTrueBeforeFalse (false, true) > 0);
    assertEquals (0, CompareHelper.compareTrueBeforeFalse (true, true));
    assertEquals (0, CompareHelper.compareTrueBeforeFalse (false, false));

    // "false" sorts before "true"
    assertTrue (CompareHelper.compareFalseBeforeTrue (false, true) < 0);
    assertTrue (CompareHelper.compareFalseBeforeTrue (true, false) > 0);
    assertEquals (0, CompareHelper.compareFalseBeforeTrue (false, false));
    assertEquals (0, CompareHelper.compareFalseBeforeTrue (true, true));
  }

  @Test
  public void testComparePrimitives ()
  {
    assertTrue (CompareHelper.compare ((byte) 1, (byte) 2) < 0);
    assertEquals (0, CompareHelper.compare ((byte) 1, (byte) 1));

    assertTrue (CompareHelper.compare ('a', 'b') < 0);
    assertEquals (0, CompareHelper.compare ('a', 'a'));

    assertTrue (CompareHelper.compare (1d, 2d) < 0);
    assertEquals (0, CompareHelper.compare (1d, 1d));

    assertTrue (CompareHelper.compare (1f, 2f) < 0);
    assertEquals (0, CompareHelper.compare (1f, 1f));

    assertTrue (CompareHelper.compare (1, 2) < 0);
    assertEquals (0, CompareHelper.compare (1, 1));

    assertTrue (CompareHelper.compare (1L, 2L) < 0);
    assertEquals (0, CompareHelper.compare (1L, 1L));

    assertTrue (CompareHelper.compare ((short) 1, (short) 2) < 0);
    assertEquals (0, CompareHelper.compare ((short) 1, (short) 1));
  }

  @Test
  public void testCompareComparable ()
  {
    assertTrue (CompareHelper.compare ("a", "b") < 0);
    assertEquals (0, CompareHelper.compare ("a", "a"));

    // null handling - null comes first by default
    assertTrue (CompareHelper.compare ((String) null, "a") < 0);
    assertTrue (CompareHelper.compare ("a", (String) null) > 0);
    assertEquals (0, CompareHelper.compare ((String) null, (String) null));

    // null comes last
    assertTrue (CompareHelper.compare ((String) null, "a", false) > 0);
    assertTrue (CompareHelper.compare ("a", (String) null, false) < 0);
  }

  @Test
  public void testCompareWithComparator ()
  {
    final Comparator <String> aCmp = Comparator.naturalOrder ();

    assertTrue (CompareHelper.compare ("a", "b", aCmp) < 0);
    assertTrue (CompareHelper.compare (null, "a", aCmp) < 0);
    assertTrue (CompareHelper.compare ("a", null, aCmp) > 0);
    assertEquals (0, CompareHelper.compare (null, null, aCmp));

    assertTrue (CompareHelper.compare (null, "a", aCmp, false) > 0);
    assertTrue (CompareHelper.compare ("a", null, aCmp, false) < 0);
  }

  @Test
  public void testCompareStringWithCollator ()
  {
    final java.text.Collator aCollator = java.text.Collator.getInstance (Locale.GERMANY);

    assertTrue (CompareHelper.compare ("a", "b", aCollator) < 0);
    assertTrue (CompareHelper.compare (null, "a", aCollator) < 0);
    assertTrue (CompareHelper.compare ("a", null, aCollator) > 0);
    assertEquals (0, CompareHelper.compare (null, null, aCollator));

    assertTrue (CompareHelper.compare (null, "a", aCollator, false) > 0);
    assertTrue (CompareHelper.compare ("a", null, aCollator, false) < 0);
  }

  @Test
  public void testCompareIgnoreCase ()
  {
    assertEquals (0, CompareHelper.compareIgnoreCase ("ABC", "abc"));
    assertTrue (CompareHelper.compareIgnoreCase ("a", "B") < 0);

    assertTrue (CompareHelper.compareIgnoreCase (null, "a") < 0);
    assertTrue (CompareHelper.compareIgnoreCase ("a", null) > 0);
    assertEquals (0, CompareHelper.compareIgnoreCase (null, null));

    assertTrue (CompareHelper.compareIgnoreCase (null, "a", false) > 0);
    assertTrue (CompareHelper.compareIgnoreCase ("a", null, false) < 0);
  }
  private static int _compareCodePointsBruteForce (final String s1, final String s2)
  {
    final int [] a1 = s1.codePoints ().toArray ();
    final int [] a2 = s2.codePoints ().toArray ();
    final int nMin = Math.min (a1.length, a2.length);
    for (int i = 0; i < nMin; ++i)
      if (a1[i] != a2[i])
        return Integer.compare (a1[i], a2[i]);
    return Integer.compare (a1.length, a2.length);
  }

  private static void _assertSameSign (final String s1, final String s2)
  {
    final int nExpected = Integer.signum (_compareCodePointsBruteForce (s1, s2));
    final int nActual = Integer.signum (CompareHelper.compareByCodePoint (s1, s2));
    assertEquals ("Comparing '" + s1 + "' with '" + s2 + "'", nExpected, nActual);
  }

  @Test
  public void testCompareByCodePoint ()
  {
    // null handling
    assertEquals (0, CompareHelper.compareByCodePoint (null, null));
    assertTrue (CompareHelper.compareByCodePoint (null, "a") < 0);
    assertTrue (CompareHelper.compareByCodePoint ("a", null) > 0);
    assertTrue (CompareHelper.compareByCodePoint (null, "a", false) > 0);
    assertTrue (CompareHelper.compareByCodePoint ("a", null, false) < 0);

    // Simple cases
    assertEquals (0, CompareHelper.compareByCodePoint ("", ""));
    assertEquals (0, CompareHelper.compareByCodePoint ("abc", "abc"));
    assertTrue (CompareHelper.compareByCodePoint ("abc", "abd") < 0);
    assertTrue (CompareHelper.compareByCodePoint ("ab", "abc") < 0);
    assertTrue (CompareHelper.compareByCodePoint ("abc", "ab") > 0);

    // U+FFFF is smaller than U+10000 - String.compareTo says otherwise
    final String sFFFF = "\uFFFF";
    final String s10000 = new String (Character.toChars (0x10000));
    assertTrue (sFFFF.compareTo (s10000) > 0);
    assertTrue (CompareHelper.compareByCodePoint (sFFFF, s10000) < 0);
    assertTrue (CompareHelper.compareByCodePoint (s10000, sFFFF) > 0);
    assertTrue (CompareHelper.compareByCodePoint ("\uE000", s10000) < 0);

    final String [] aSamples = { "",
                                 "a",
                                 "b",
                                 "ab",
                                 "\uD7FF",
                                 "\uE000",
                                 "\uFFFF",
                                 s10000,
                                 new String (Character.toChars (0x10FFFF)),
                                 "\uD800",
                                 "\uDBFF",
                                 "\uDC00",
                                 "\uDFFF",
                                 "\uD800\uDC00",
                                 "\uD800\uDC01",
                                 "\uD800\uE000",
                                 "\uD800a",
                                 "\uD800\uD800",
                                 "\uD800\uD800\uDC00",
                                 "\uDC00\uD800",
                                 "a\uD800",
                                 "a\uDC00",
                                 "a\uD800\uDC00",
                                 "a\uFFFF",
                                 "\uFFFF\uD800",
                                 s10000 + "a",
                                 s10000 + "\uDC00" };
    for (final String s1 : aSamples)
      for (final String s2 : aSamples)
      {
        _assertSameSign (s1, s2);
        // Also with a common prefix
        _assertSameSign ("x" + s1, "x" + s2);
        _assertSameSign ("\uD800" + s1, "\uD800" + s2);
        _assertSameSign (s10000 + s1, s10000 + s2);
      }

    // Random strings from a small alphabet of tricky characters
    final char [] aAlphabet = { 'a', '\uD7FF', '\uD800', '\uDBFF', '\uDC00', '\uDFFF', '\uE000', '\uFFFF' };
    final Random aRandom = new Random (20261110);
    for (int n = 0; n < 50_000; ++n)
    {
      final StringBuilder aSB1 = new StringBuilder ();
      final StringBuilder aSB2 = new StringBuilder ();
      final int nCommon = aRandom.nextInt (3);
      for (int i = 0; i < nCommon; ++i)
      {
        final char c = aAlphabet[aRandom.nextInt (aAlphabet.length)];
        aSB1.append (c);
        aSB2.append (c);
      }
      final int nLen1 = aRandom.nextInt (4);
      for (int i = 0; i < nLen1; ++i)
        aSB1.append (aAlphabet[aRandom.nextInt (aAlphabet.length)]);
      final int nLen2 = aRandom.nextInt (4);
      for (int i = 0; i < nLen2; ++i)
        aSB2.append (aAlphabet[aRandom.nextInt (aAlphabet.length)]);
      _assertSameSign (aSB1.toString (), aSB2.toString ());
    }
  }
}

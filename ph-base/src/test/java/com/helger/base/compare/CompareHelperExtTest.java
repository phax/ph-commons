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
}

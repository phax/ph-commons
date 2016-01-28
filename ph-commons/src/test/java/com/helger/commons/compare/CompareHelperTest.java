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
package com.helger.commons.compare;

import static org.junit.Assert.assertEquals;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import org.junit.Test;

import com.helger.commons.collation.CollatorHelper;
import com.helger.commons.mock.AbstractCommonsTestCase;

/**
 * Test class for class {@link CompareHelper}.
 *
 * @author Philip Helger
 */
public final class CompareHelperTest extends AbstractCommonsTestCase
{
  @Test
  public void testNullSafeCompare ()
  {
    final String s1 = "A";
    final String s2 = "B";
    assertEquals (-1, CompareHelper.compare (s1, s2));
    assertEquals (+1, CompareHelper.compare (s2, s1));
    assertEquals (0, CompareHelper.compare (s1, s1));
    assertEquals (0, CompareHelper.compare (s2, s2));

    assertEquals (+1, CompareHelper.compare (s1, null));
    assertEquals (+1, CompareHelper.compare (s2, null));
    assertEquals (-1, CompareHelper.compare (null, s1));
    assertEquals (-1, CompareHelper.compare (null, s2));
    assertEquals (0, CompareHelper.compare ((String) null, null));

    // Using our collator
    assertEquals (-1, CompareHelper.compare ("1.1 a", "1.1.1 a", L_DE));
    assertEquals (-1, CompareHelper.compare ("1.1 a", "1.1.1 a", CollatorHelper.getCollatorSpaceBeforeDot (L_DE)));
    // Using the system collator
    assertEquals (+1, CompareHelper.compare ("1.1 a", "1.1.1 a", Collator.getInstance (L_DE)));

    assertEquals (-1, CompareHelper.compare (L_DE, L_EN, Comparator.comparing (Locale::toString)));
    assertEquals (+1, CompareHelper.compare (L_DE, null, Comparator.comparing (Locale::toString)));
    assertEquals (-1, CompareHelper.compare (null, L_EN, Comparator.comparing (Locale::toString)));
  }
}

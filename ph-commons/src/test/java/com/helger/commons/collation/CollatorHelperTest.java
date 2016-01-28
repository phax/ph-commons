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
package com.helger.commons.collation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.Collator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.junit.Test;

import com.helger.commons.collation.CollatorHelper;
import com.helger.commons.compare.CompareHelper;
import com.helger.commons.mock.AbstractCommonsTestCase;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link CollatorHelper}.
 *
 * @author Philip Helger
 */
public final class CollatorHelperTest extends AbstractCommonsTestCase
{
  @Test
  public void testGetCollatorSpaceBeforeDot ()
  {
    final Collator c = CollatorHelper.getCollatorSpaceBeforeDot (L_DE);
    assertNotNull (c);
    final Collator c2 = CollatorHelper.getCollatorSpaceBeforeDot (L_DE);
    assertNotNull (c2);
    assertTrue (c != c2);
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (c, c2);

    // Unknown locale
    assertNotNull (CollatorHelper.getCollatorSpaceBeforeDot (new Locale ("xy", "87")));

    final List <Collator> res = new Vector <Collator> ();
    final int nMax = 100;
    CommonsTestHelper.testInParallel (nMax, (Runnable) () -> res.add (CollatorHelper.getCollatorSpaceBeforeDot (L_EN)));

    assertEquals (nMax, res.size ());
    for (int i = 1; i < nMax; ++i)
      CommonsTestHelper.testDefaultImplementationWithEqualContentObject (res.get (0), res.get (i));
  }

  @Test
  public void testSort ()
  {
    final int nMax = 10000;
    CommonsTestHelper.testInParallel (nMax, (Runnable) () -> {
      Collator c = CollatorHelper.getCollatorSpaceBeforeDot (L_DE);
      assertEquals (-1, CompareHelper.compare ("1.1 a", "1.1.1 a", c));
      c = CollatorHelper.getCollatorSpaceBeforeDot (L_EN);
      assertEquals (-1, CompareHelper.compare ("1.1 a", "1.1.1 a", c));
    });
  }
}

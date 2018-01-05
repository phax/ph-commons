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
package com.helger.commons.compare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.text.Collator;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsVector;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link CollatorHelper}.
 *
 * @author Philip Helger
 */
public final class CollatorHelperTest
{
  public static final Locale L_DE = new Locale ("de");
  public static final Locale L_EN = new Locale ("en");

  @Test
  public void testGetCollatorSpaceBeforeDot ()
  {
    final Collator c = CollatorHelper.getCollatorSpaceBeforeDot (L_DE);
    assertNotNull (c);
    final Collator c2 = CollatorHelper.getCollatorSpaceBeforeDot (L_DE);
    assertNotNull (c2);
    assertNotSame (c, c2);
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (c, c2);

    // Unknown locale
    assertNotNull (CollatorHelper.getCollatorSpaceBeforeDot (new Locale ("xy", "87")));

    final List <Collator> res = new CommonsVector <> ();
    final int nMax = 100;
    CommonsTestHelper.testInParallel (nMax, () -> res.add (CollatorHelper.getCollatorSpaceBeforeDot (L_EN)));

    assertEquals (nMax, res.size ());
    for (int i = 1; i < nMax; ++i)
      CommonsTestHelper.testDefaultImplementationWithEqualContentObject (res.get (0), res.get (i));
  }

  @Test
  public void testSort ()
  {
    final int nMax = 10000;
    CommonsTestHelper.testInParallel (nMax, () -> {
      Collator c = CollatorHelper.getCollatorSpaceBeforeDot (L_DE);
      assertEquals (-1, CompareHelper.compare ("1.1 a", "1.1.1 a", c));
      c = CollatorHelper.getCollatorSpaceBeforeDot (L_EN);
      assertEquals (-1, CompareHelper.compare ("1.1 a", "1.1.1 a", c));
    });
  }
}

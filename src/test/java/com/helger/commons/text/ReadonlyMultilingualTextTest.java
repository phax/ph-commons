/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Locale;
import java.util.Map;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.mock.AbstractCommonsTestCase;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.text.IMultilingualText;
import com.helger.commons.text.ISimpleMultilingualText;
import com.helger.commons.text.MultilingualText;
import com.helger.commons.text.ReadonlyMultilingualText;

/**
 * Test class for class {@link ReadonlyMultilingualText}.
 *
 * @author Philip Helger
 */
public final class ReadonlyMultilingualTextTest extends AbstractCommonsTestCase
{
  @Test
  public void testCtor ()
  {
    IMultilingualText aMLT = new ReadonlyMultilingualText ();
    assertEquals (0, aMLT.getSize ());
    assertNotNull (aMLT.getAllLocales ());
    assertTrue (aMLT.getAllLocales ().isEmpty ());

    final Map <Locale, String> aMap = CollectionHelper.newMap (new Locale [] { L_DE, L_EN },
                                                               new String [] { "de", "en" });
    aMLT = new ReadonlyMultilingualText (aMap);
    assertEquals (2, aMLT.getSize ());
    assertEquals (2, aMLT.getAllLocales ().size ());
    assertTrue (aMLT.containsLocale (L_DE));
    assertFalse (aMLT.containsLocale (L_FR));
    assertTrue (aMLT.containsLocaleWithFallback (L_DE_AT));
    assertTrue (aMLT.containsLocaleWithFallback (L_DE));
    assertFalse (aMLT.containsLocaleWithFallback (L_FR_FR));
    assertFalse (aMLT.containsLocaleWithFallback (L_FR));
    assertEquals (2, aMLT.getAllTexts ().size ());

    final MultilingualText t = new MultilingualText ();
    for (final Map.Entry <Locale, String> aEntry : aMap.entrySet ())
      t.setText (aEntry.getKey (), aEntry.getValue ());
    aMLT = new ReadonlyMultilingualText (t);
    assertEquals (2, aMLT.getSize ());
    assertEquals (2, aMLT.getAllLocales ().size ());
    assertTrue (aMLT.containsLocale (L_DE));
    assertFalse (aMLT.containsLocale (L_FR));
    assertTrue (aMLT.containsLocaleWithFallback (L_DE_AT));
    assertTrue (aMLT.containsLocaleWithFallback (L_DE));
    assertFalse (aMLT.containsLocaleWithFallback (L_FR_FR));
    assertFalse (aMLT.containsLocaleWithFallback (L_FR));
    assertEquals (2, aMLT.getAllTexts ().size ());

    aMLT = new ReadonlyMultilingualText ((ISimpleMultilingualText) t);
    assertEquals (2, aMLT.getSize ());
    assertEquals (2, aMLT.getAllLocales ().size ());
    assertTrue (aMLT.containsLocale (L_DE));
    assertFalse (aMLT.containsLocale (L_FR));
    assertTrue (aMLT.containsLocaleWithFallback (L_DE_AT));
    assertTrue (aMLT.containsLocaleWithFallback (L_DE));
    assertFalse (aMLT.containsLocaleWithFallback (L_FR_FR));
    assertFalse (aMLT.containsLocaleWithFallback (L_FR));
    assertEquals (2, aMLT.getAllTexts ().size ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (aMLT,
                                                                 new ReadonlyMultilingualText (CollectionHelper.newMap (new Locale [] { L_DE,
                                                                                                                                       L_EN },
                                                                                                                        new String [] { "de",
                                                                                                                                       "en" })));
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new ReadonlyMultilingualText (),
                                                                 new ReadonlyMultilingualText ());
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (aMLT, new ReadonlyMultilingualText ());

    try
    {
      new ReadonlyMultilingualText ((Map <Locale, String>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new ReadonlyMultilingualText ((IMultilingualText) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new ReadonlyMultilingualText ((ISimpleMultilingualText) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}

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
package com.helger.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Locale;
import java.util.Map;

import org.junit.Test;

import com.helger.collection.helper.CollectionHelperExt;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link ReadOnlyMultilingualText}.
 *
 * @author Philip Helger
 */
public final class ReadOnlyMultilingualTextTest
{
  private static final Locale L_DE = new Locale ("de");
  private static final Locale L_EN = new Locale ("en");
  private static final Locale L_DE_AT = new Locale ("de", "AT");
  private static final Locale L_FR = new Locale ("fr");
  private static final Locale L_FR_FR = new Locale ("fr", "FR");

  @Test
  public void testCtor ()
  {
    IMultilingualText aMLT = new ReadOnlyMultilingualText ();
    assertEquals (0, aMLT.texts ().size ());
    assertNotNull (aMLT.texts ().keySet ());
    assertTrue (aMLT.texts ().isEmpty ());

    final Map <Locale, String> aMap = CollectionHelperExt.newMap (new Locale [] { L_DE, L_EN }, new String [] { "de", "en" });
    aMLT = new ReadOnlyMultilingualText (aMap);
    assertEquals (2, aMLT.texts ().size ());
    assertTrue (aMLT.texts ().containsKey (L_DE));
    assertFalse (aMLT.texts ().containsKey (L_FR));
    assertTrue (aMLT.containsLocaleWithFallback (L_DE_AT));
    assertTrue (aMLT.containsLocaleWithFallback (L_DE));
    assertFalse (aMLT.containsLocaleWithFallback (L_FR_FR));
    assertFalse (aMLT.containsLocaleWithFallback (L_FR));

    final MultilingualText t = new MultilingualText ();
    for (final Map.Entry <Locale, String> aEntry : aMap.entrySet ())
      t.setText (aEntry.getKey (), aEntry.getValue ());
    aMLT = new ReadOnlyMultilingualText (t);
    assertEquals (2, aMLT.texts ().size ());
    assertTrue (aMLT.texts ().containsKey (L_DE));
    assertFalse (aMLT.texts ().containsKey (L_FR));
    assertTrue (aMLT.containsLocaleWithFallback (L_DE_AT));
    assertTrue (aMLT.containsLocaleWithFallback (L_DE));
    assertFalse (aMLT.containsLocaleWithFallback (L_FR_FR));
    assertFalse (aMLT.containsLocaleWithFallback (L_FR));

    aMLT = new ReadOnlyMultilingualText (t);
    assertEquals (2, aMLT.texts ().size ());
    assertTrue (aMLT.texts ().containsKey (L_DE));
    assertFalse (aMLT.texts ().containsKey (L_FR));
    assertTrue (aMLT.containsLocaleWithFallback (L_DE_AT));
    assertTrue (aMLT.containsLocaleWithFallback (L_DE));
    assertFalse (aMLT.containsLocaleWithFallback (L_FR_FR));
    assertFalse (aMLT.containsLocaleWithFallback (L_FR));

    TestHelper.testDefaultImplementationWithEqualContentObject (aMLT,
                                                                       new ReadOnlyMultilingualText (CollectionHelperExt.newMap (new Locale [] { L_DE,
                                                                                                                                              L_EN },
                                                                                                                              new String [] { "de",
                                                                                                                                              "en" })));
    TestHelper.testDefaultImplementationWithEqualContentObject (new ReadOnlyMultilingualText (), new ReadOnlyMultilingualText ());
    TestHelper.testDefaultImplementationWithDifferentContentObject (aMLT, new ReadOnlyMultilingualText ());

    try
    {
      new ReadOnlyMultilingualText ((Map <Locale, String>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new ReadOnlyMultilingualText ((IMultilingualText) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}

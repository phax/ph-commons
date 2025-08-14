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
package com.helger.commons.text.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;
import com.helger.commons.text.IMultilingualText;
import com.helger.commons.text.MultilingualText;

/**
 * Test class for class {@link TextHelper}.
 *
 * @author Philip Helger
 */
public final class TextHelperTest
{
  private static final Locale L_DE = new Locale ("de");
  private static final Locale L_EN = new Locale ("en");

  @Test
  public void testGetCopyWithLocales ()
  {
    final MultilingualText aMLT = new MultilingualText ();
    assertTrue (aMLT.setText (Locale.ENGLISH, "Hi").isChanged ());
    assertTrue (aMLT.setText (Locale.GERMAN, "Moin").isChanged ());
    assertEquals (2, aMLT.texts ().size ());

    final ICommonsList <Locale> aLocaleList = new CommonsArrayList <> ();
    aLocaleList.add (Locale.ENGLISH);
    final MultilingualText aMLT2 = TextHelper.getCopyWithLocales (aMLT, aLocaleList);
    assertTrue (aMLT2.texts ().containsKey (Locale.ENGLISH));
    assertFalse (aMLT2.texts ().containsKey (Locale.GERMAN));

    assertTrue (aMLT.texts ().containsKey (Locale.ENGLISH));
    assertTrue (aMLT.texts ().containsKey (Locale.GERMAN));
  }

  @Test
  public void testCreateMultilingualTextFromMap ()
  {
    final ICommonsMap <String, String> aParamNames = new CommonsHashMap <> ();
    IMultilingualText aMLT = TextHelper.createMultilingualTextFromMap (aParamNames);
    assertEquals (0, aMLT.texts ().size ());

    aParamNames.put ("de", "x");
    aParamNames.put ("en", "y");
    aMLT = TextHelper.createMultilingualTextFromMap (aParamNames);
    assertEquals (2, aMLT.texts ().size ());
    assertEquals ("x", aMLT.getText (L_DE));
    assertEquals ("y", aMLT.getText (L_EN));
  }
}

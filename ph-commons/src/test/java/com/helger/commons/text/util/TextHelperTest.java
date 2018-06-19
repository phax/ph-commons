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
package com.helger.commons.text.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
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
  public void testGetFormattedText ()
  {
    assertEquals ("a", TextHelper.getFormattedText ("a", (Object []) null));
    assertEquals ("a", TextHelper.getFormattedText ("a", new Object [0]));
    assertEquals ("a{0}", TextHelper.getFormattedText ("a{0}", new Object [0]));
    assertEquals ("anull", TextHelper.getFormattedText ("a{0}", (Object) null));
    assertEquals ("ab", TextHelper.getFormattedText ("a{0}", "b"));
    assertEquals ("ab{1}", TextHelper.getFormattedText ("a{0}{1}", "b"));
    assertEquals ("anull{1}", TextHelper.getFormattedText ("a{0}{1}", (Object) null));
    assertNull (TextHelper.getFormattedText ((String) null, "b"));
  }

  @Test
  public void testGetCopyWithLocales ()
  {
    final MultilingualText aMLT = new MultilingualText ();
    assertTrue (aMLT.setText (Locale.ENGLISH, "Hi").isChanged ());
    assertTrue (aMLT.setText (Locale.GERMAN, "Moin").isChanged ());
    assertEquals (2, aMLT.size ());

    final ICommonsList <Locale> aLocaleList = new CommonsArrayList <> ();
    aLocaleList.add (Locale.ENGLISH);
    final MultilingualText aMLT2 = TextHelper.getCopyWithLocales (aMLT, aLocaleList);
    assertTrue (aMLT2.containsLocale (Locale.ENGLISH));
    assertFalse (aMLT2.containsLocale (Locale.GERMAN));

    assertTrue (aMLT.containsLocale (Locale.ENGLISH));
    assertTrue (aMLT.containsLocale (Locale.GERMAN));
  }

  @Test
  public void testCreateMultilingualTextFromMap ()
  {
    final ICommonsMap <String, String> aParamNames = new CommonsHashMap <> ();
    IMultilingualText aMLT = TextHelper.createMultilingualTextFromMap (aParamNames);
    assertEquals (0, aMLT.size ());

    aParamNames.put ("de", "x");
    aParamNames.put ("en", "y");
    aMLT = TextHelper.createMultilingualTextFromMap (aParamNames);
    assertEquals (2, aMLT.size ());
    assertEquals ("x", aMLT.getText (L_DE));
    assertEquals ("y", aMLT.getText (L_EN));
  }
}

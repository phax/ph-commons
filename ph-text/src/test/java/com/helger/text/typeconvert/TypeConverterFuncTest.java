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
package com.helger.text.typeconvert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Test;

import com.helger.text.MultilingualText;
import com.helger.text.locale.ELocaleName;
import com.helger.text.locale.LocaleCache;
import com.helger.text.locale.LocaleHelper;
import com.helger.typeconvert.TypeConverterException;
import com.helger.typeconvert.impl.TypeConverter;

/**
 * Test class for class {@link TypeConverter}.
 *
 * @author Philip Helger
 */
public final class TypeConverterFuncTest
{
  @Test
  public void testToString ()
  {
    // for super class
    final Object [] aUpCastObjs = { ELocaleName.ID_LANGUAGE_ALL };
    for (final Object aSrcValue : aUpCastObjs)
    {
      final String sValue = TypeConverter.convert (aSrcValue, String.class);
      final Object aObj2 = TypeConverter.convert (sValue, aSrcValue.getClass ());
      assertEquals (aSrcValue, aObj2);
    }
  }

  @Test
  public void testLocale ()
  {
    // Check if conversion works for all locales
    for (final Locale aLocale : LocaleCache.getInstance ().getAllLocales ())
    {
      final String sLocale = TypeConverter.convert (aLocale, String.class);
      assertNotNull (aLocale.toString (), sLocale);
      final Locale aLocale2 = TypeConverter.convert (sLocale, Locale.class);
      assertTrue (LocaleHelper.equalLocales (aLocale, aLocale2));
    }
  }

  @Test
  public void testNoConverterNoConversion ()
  {
    try
    {
      final MultilingualText m = TypeConverter.convert ("1234", MultilingualText.class);
      assertNotNull (m);
      fail ();
    }
    catch (final TypeConverterException ex)
    {}
  }
}

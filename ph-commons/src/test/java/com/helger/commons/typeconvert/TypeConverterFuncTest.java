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
package com.helger.commons.typeconvert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import com.helger.base.equals.EqualsHelper;
import com.helger.base.numeric.BigHelper;
import com.helger.base.state.EChange;
import com.helger.base.state.EContinue;
import com.helger.base.state.EEnabled;
import com.helger.base.state.EInterrupt;
import com.helger.base.state.ELeftRight;
import com.helger.base.state.EMandatory;
import com.helger.base.state.ESuccess;
import com.helger.base.state.ETopBottom;
import com.helger.base.state.EValidity;
import com.helger.commons.locale.ELocaleName;
import com.helger.commons.locale.LocaleCache;
import com.helger.commons.locale.LocaleHelper;
import com.helger.commons.state.ETriState;
import com.helger.commons.text.MultilingualText;
import com.helger.typeconvert.TypeConverterException;
import com.helger.typeconvert.impl.TypeConverter;
import com.helger.typeconvert.impl.TypeConverterProviderRuleBased;

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
    // Check conversion with explicit converters defined
    final Object [] aDefinedObjs = { BigDecimal.ONE,
                                     BigHelper.toBigDecimal (Double.MAX_VALUE),
                                     new BigDecimal ("123446712345678765456547865789762131.9999123446712345678765456547865789762131"),
                                     BigInteger.ZERO,
                                     new BigInteger ("123446712345678765456547865789762131"),
                                     Byte.valueOf ((byte) 5),
                                     Boolean.TRUE,
                                     Character.valueOf ('c'),
                                     Double.valueOf (1245.3433),
                                     Float.valueOf (31.451f),
                                     Integer.valueOf (17),
                                     Long.valueOf (Long.MAX_VALUE),
                                     Short.valueOf (Short.MIN_VALUE),
                                     EChange.CHANGED,
                                     EContinue.BREAK,
                                     EEnabled.DISABLED,
                                     EInterrupt.INTERRUPTED,
                                     ELeftRight.RIGHT,
                                     EMandatory.MANDATORY,
                                     ESuccess.FAILURE,
                                     ETopBottom.BOTTOM,
                                     ETriState.UNDEFINED,
                                     EValidity.VALID,
                                     "Jägalä".getBytes (StandardCharsets.ISO_8859_1) };
    for (final Object aSrcValue : aDefinedObjs)
    {
      final String sValue = TypeConverter.convert (aSrcValue, String.class);
      final Object aObj2 = TypeConverter.convert (sValue, aSrcValue.getClass ());
      assertTrue (EqualsHelper.equals (aSrcValue, aObj2));
    }

    // Test conversion if no explicit converter available for source class, but
    // for super class
    final Object [] aUpCastObjs = { ELocaleName.ID_LANGUAGE_ALL };
    for (final Object aSrcValue : aUpCastObjs)
    {
      final String sValue = TypeConverter.convert (aSrcValue, String.class);
      final Object aObj2 = TypeConverter.convert (sValue, aSrcValue.getClass ());
      assertEquals (aSrcValue, aObj2);
    }

    // Check if conversion works for special objects not overwriting
    // equals/hashcode
    for (final Object aSrcValue : new Object [] { new AtomicBoolean (true),
                                                  new AtomicInteger (177),
                                                  new AtomicLong (12374893127489L),
                                                  new StringBuilder (),
                                                  new StringBuilder ("Das StringBuilder kein equals implementiert ist doof"),
                                                  new StringBuffer (),
                                                  new StringBuffer ("Das gilt auch für StringBuffer") })
    {
      String sValue = TypeConverter.convert (aSrcValue, String.class);
      Object aObj2 = TypeConverter.convert (sValue, aSrcValue.getClass ());
      assertEquals (aSrcValue.toString (), aObj2.toString ());

      sValue = TypeConverter.convert (TypeConverterProviderRuleBased.getInstance (), aSrcValue, String.class);
      aObj2 = TypeConverter.convert (TypeConverterProviderRuleBased.getInstance (), sValue, aSrcValue.getClass ());
      assertEquals (aSrcValue.toString (), aObj2.toString ());
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

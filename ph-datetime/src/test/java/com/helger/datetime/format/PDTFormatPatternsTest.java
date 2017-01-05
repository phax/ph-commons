/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.datetime.format;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Test;

import com.helger.commons.locale.LocaleCache;

/**
 * Test class for class {@link PDTFormatPatterns}.
 *
 * @author Philip Helger
 */
public final class PDTFormatPatternsTest
{
  private void _checkPatternConsistency (final String sDefault,
                                         final String sShort,
                                         final String sMedium,
                                         final String sLong,
                                         final String sFull,
                                         final boolean bShortCanBeMedium,
                                         final boolean bShortCanBeLong,
                                         final boolean bMediumCanBeLong,
                                         final boolean bLongCanBeFull)
  {
    assertThat (sDefault, is (notNullValue ()));
    assertThat (sShort, is (notNullValue ()));
    assertThat (sMedium, is (notNullValue ()));
    assertThat (sLong, is (notNullValue ()));
    assertThat (sFull, is (notNullValue ()));

    assertThat (sDefault, is (sMedium));
    if (!bShortCanBeMedium)
      assertThat (sShort, is (not (sMedium)));
    if (!bShortCanBeLong)
      assertThat (sShort, is (not (sLong)));
    assertThat (sShort, is (not (sFull)));
    if (!bMediumCanBeLong)
      assertThat (sMedium, is (not (sLong)));
    if (!bMediumCanBeLong || !bLongCanBeFull)
      assertThat (sMedium, is (not (sFull)));
    if (!bLongCanBeFull)
      assertThat (sLong, is (not (sFull)));
  }

  @Test
  public void testGetPattern ()
  {
    String sDefault;
    String sShort;
    String sMedium;
    String sLong;
    String sFull;
    for (final Locale aLocale : LocaleCache.getInstance ().getAllLocales ())
    {
      // get pattern
      sDefault = PDTFormatPatterns.getDefaultPatternDate (aLocale);
      sShort = PDTFormatPatterns.getShortPatternDate (aLocale);
      sMedium = PDTFormatPatterns.getMediumPatternDate (aLocale);
      sLong = PDTFormatPatterns.getLongPatternDate (aLocale);
      sFull = PDTFormatPatterns.getFullPatternDate (aLocale);
      _checkPatternConsistency (sDefault, sShort, sMedium, sLong, sFull, true, true, true, true);

      sDefault = PDTFormatPatterns.getDefaultPatternTime (aLocale);
      sShort = PDTFormatPatterns.getShortPatternTime (aLocale);
      sMedium = PDTFormatPatterns.getMediumPatternTime (aLocale);
      sLong = PDTFormatPatterns.getLongPatternTime (aLocale);
      sFull = PDTFormatPatterns.getFullPatternTime (aLocale);
      _checkPatternConsistency (sDefault, sShort, sMedium, sLong, sFull, true, false, true, true);

      sDefault = PDTFormatPatterns.getDefaultPatternDateTime (aLocale);
      sShort = PDTFormatPatterns.getShortPatternDateTime (aLocale);
      sMedium = PDTFormatPatterns.getMediumPatternDateTime (aLocale);
      sLong = PDTFormatPatterns.getLongPatternDateTime (aLocale);
      sFull = PDTFormatPatterns.getFullPatternDateTime (aLocale);
      _checkPatternConsistency (sDefault, sShort, sMedium, sLong, sFull, false, false, false, true);
    }

    // Check null locales
    try
    {
      PDTFormatPatterns.getDefaultPatternDate (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      PDTFormatPatterns.getDefaultPatternTime (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      PDTFormatPatterns.getDefaultPatternDateTime (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}

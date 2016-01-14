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
package com.helger.commons.supplementary.test.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.junit.Test;

import com.helger.commons.CGlobal;
import com.helger.commons.locale.LocaleCache;

public final class JavaDecimalFormatFuncTest
{
  @Test
  public void testDecimalFormat ()
  {
    // for better comparison
    final DecimalFormatSymbols aDFS = DecimalFormatSymbols.getInstance (Locale.ENGLISH);

    // The 0 symbol shows a digit or 0 if no digit present
    NumberFormat formatter = new DecimalFormat ("000000", aDFS);
    String s = formatter.format (-1234.567);
    // -001235
    // notice that the number was rounded up

    // The # symbol shows a digit or nothing if no digit present
    formatter = new DecimalFormat ("##", aDFS);
    s = formatter.format (-1234.567);
    assertEquals ("-1235", s);

    s = formatter.format (0);
    assertEquals ("0", s);

    formatter = new DecimalFormat ("##00", aDFS);
    s = formatter.format (0);
    assertEquals ("00", s);

    // The . symbol indicates the decimal point
    formatter = new DecimalFormat (".00", aDFS);
    s = formatter.format (-.567);
    assertEquals ("-.57", s);

    formatter = new DecimalFormat ("0.00", aDFS);
    s = formatter.format (-.567);
    assertEquals ("-0.57", s);

    formatter = new DecimalFormat ("#.#", aDFS);
    s = formatter.format (-1234.567);
    assertEquals ("-1234.6", s);

    formatter = new DecimalFormat ("#.######", aDFS);
    s = formatter.format (-1234.567);
    assertEquals ("-1234.567", s);

    formatter = new DecimalFormat (".######", aDFS);
    s = formatter.format (-1234.567);
    assertEquals ("-1234.567", s);

    formatter = new DecimalFormat ("#.000000", aDFS);
    s = formatter.format (-1234.567);
    assertEquals ("-1234.567000", s);

    // The , symbol is used to group numbers
    formatter = new DecimalFormat ("#,###,###", aDFS);
    s = formatter.format (-1234.567);
    assertEquals ("-1,235", s);

    s = formatter.format (-1234567.890);
    assertEquals ("-1,234,568", s);

    // The ; symbol is used to specify an alternate pattern for negative values
    formatter = new DecimalFormat ("#;(#)", aDFS);
    s = formatter.format (-1234.567);
    assertEquals ("(1235)", s);

    // The ' symbol is used to quote literal symbols
    formatter = new DecimalFormat ("'#'#", aDFS);
    s = formatter.format (-1234.567);
    assertEquals ("-#1235", s);

    formatter = new DecimalFormat ("'abc'#", aDFS);
    s = formatter.format (-1234.567);
    assertEquals ("-abc1235", s);
  }

  @Test
  public void testCurrencyNoSymbol ()
  {
    /*
     * You could remove the currency symbol placeholder ('¤' or '\u00A4') from
     * the pattern and then create a number format with this pattern. Something
     * like the following:
     */
    NumberFormat curFormat = NumberFormat.getCurrencyInstance (LocaleCache.getInstance ().getLocale ("fr"));
    assertTrue (curFormat instanceof DecimalFormat);

    final String pattern = ((DecimalFormat) curFormat).toPattern ();
    assertEquals ("#,##0.00 \u00A4", pattern);

    final String patternWithoutCurSym = pattern.replaceAll ("\u00A4", "");
    assertEquals ("#,##0.00 ", patternWithoutCurSym);

    curFormat = new DecimalFormat (patternWithoutCurSym);
    ((DecimalFormat) curFormat).setDecimalFormatSymbols (DecimalFormatSymbols.getInstance (CGlobal.LOCALE_FIXED_NUMBER_FORMAT));
    assertEquals ("3.12 ", curFormat.format (3.1234));
  }

  @Test
  public void testNumberFormatVsToString ()
  {
    final NumberFormat aNF = NumberFormat.getNumberInstance (Locale.US);
    aNF.setMaximumFractionDigits (10);

    double d = 3.1234;
    assertEquals ("3.1234", aNF.format (d));
    assertEquals ("3.1234", Double.toString (d));

    d = 3.12345678901;
    assertEquals ("3.123456789", aNF.format (d));
    assertEquals ("3.12345678901", Double.toString (d));
  }
}

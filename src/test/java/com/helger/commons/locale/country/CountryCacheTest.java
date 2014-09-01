/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.locale.country;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;

import com.helger.commons.CGlobal;
import com.helger.commons.locale.LocaleCache;
import com.helger.commons.string.StringHelper;

/**
 * Test class for class {@link CountryCache}.
 * 
 * @author Philip Helger
 */
public final class CountryCacheTest
{
  private static final Locale LOCALE = new Locale ("de", "AT", "");

  @Test
  public void testGetCountryOfLocale ()
  {
    assertNull (CountryCache.getCountry ((Locale) null));
    assertEquals (CountryCache.getCountry (LOCALE.getCountry ()), CountryCache.getCountry (LOCALE));
  }

  @Test
  public void testGetCountry ()
  {
    assertNull (CountryCache.getCountry (""));
    assertNull (CountryCache.getCountry ((String) null));
    assertNull (CountryCache.getCountry ((Locale) null));
    assertNull (CountryCache.getCountry ("A"));
    assertNotNull (CountryCache.getCountry ("AT"));
    assertNotNull (CountryCache.getCountry ("at"));
    assertNull (CountryCache.getCountry ("AAA"));
    assertNull (CountryCache.getCountry ("1"));
    assertNull (CountryCache.getCountry ("12"));
    assertNotNull (CountryCache.getCountry ("123"));
    assertNull (CountryCache.getCountry ("1234"));

    assertNotNull (CountryCache.getCountry ("AT"));
    assertNotNull (CountryCache.getCountry ("at"));
    assertNotNull (CountryCache.getCountry ("pl"));
    // Returns a valid locale, but emits a warning:

    assertEquals (CountryCache.getCountry ("ch"), CountryCache.getCountry (new Locale ("de", "ch")));
    assertEquals (LocaleCache.getLocale ("", "AT", ""), CountryCache.getCountry ("_AT"));
    assertEquals (LocaleCache.getLocale ("", "AT", ""), CountryCache.getCountry ("de_AT"));
    assertEquals (CountryCache.getCountry ("AT"), CountryCache.getCountry (CountryCache.getCountry ("AT").toString ()));
    for (final String sLocale : CountryCache.getAllCountries ())
      assertTrue (CountryCache.containsCountry (sLocale));
    assertFalse (CountryCache.containsCountry ((String) null));
    assertFalse (CountryCache.containsCountry (CGlobal.LOCALE_ALL));
    assertFalse (CountryCache.containsCountry ((Locale) null));
  }

  @Test (expected = NullPointerException.class)
  public void testAddCountryNull ()
  {
    CountryCache.addCountry (null);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testAddCountryEmpty ()
  {
    CountryCache.addCountry ("");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testAddCountryIllegal ()
  {
    CountryCache.addCountry ("EN AAAA");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testAddCountryInvalidCasing ()
  {
    CountryCache.addCountry ("en");
  }

  @Test
  public void testAddCountry ()
  {
    assertTrue (CountryCache.addCountry ("YX").isChanged ());
  }

  @Test
  public void testContainsCountryByLocale ()
  {
    assertFalse (CountryCache.containsCountry ((Locale) null));
    assertTrue (CountryCache.containsCountry (LOCALE.getCountry ()) == CountryCache.containsCountry (LOCALE));
  }

  @Test
  public void testResetCache ()
  {
    // is always cleaned along with locale cache!
    LocaleCache.resetCache ();
    CountryCache.resetCache ();
    final int nCount = CountryCache.getAllCountries ().size ();
    CountryCache.addCountry ("123");
    assertTrue (CountryCache.containsCountry ("123"));
    assertEquals (nCount + 1, CountryCache.getAllCountries ().size ());
    // is always cleaned along with locale cache!
    LocaleCache.resetCache ();
    CountryCache.resetCache ();
    assertEquals (nCount, CountryCache.getAllCountries ().size ());
  }

  @Test
  public void testContainsCountryByString ()
  {
    assertFalse (CountryCache.containsCountry ((String) null));
    assertFalse (CountryCache.containsCountry (""));
    assertFalse (CountryCache.containsCountry ("a"));
    assertFalse (CountryCache.containsCountry ("A"));
    assertFalse (CountryCache.containsCountry ("aaa"));
    assertFalse (CountryCache.containsCountry ("AAA"));
    assertFalse (CountryCache.containsCountry ("1"));
    assertFalse (CountryCache.containsCountry ("12"));
    assertFalse (CountryCache.containsCountry ("1234"));
    assertTrue (CountryCache.containsCountry ("GB"));
    assertTrue (CountryCache.containsCountry ("gb"));
    assertFalse (CountryCache.containsCountry ("123"));
    CountryCache.addCountry ("123");
    assertTrue (CountryCache.containsCountry ("123"));
  }

  @Test
  public void testNoConcurrentModification ()
  {
    final Set <Locale> aCountries = new HashSet <Locale> ();
    for (final String sCountry : CountryCache.getAllCountries ())
      aCountries.add (CountryCache.getCountry (sCountry));

    for (final Locale aCountry : aCountries)
    {
      assertTrue (StringHelper.hasNoText (aCountry.getLanguage ()));
      assertTrue (StringHelper.hasText (aCountry.getCountry ()));
      assertTrue (StringHelper.hasNoText (aCountry.getVariant ()));
    }
  }

  @Test
  public void testNoConcurrentModification2 ()
  {
    final Set <Locale> aCountries = new HashSet <Locale> ();
    for (final Locale aCountry : CountryCache.getAllCountryLocales ())
      aCountries.add (CountryCache.getCountry (aCountry));

    for (final Locale aCountry : aCountries)
    {
      assertTrue (StringHelper.hasNoText (aCountry.getLanguage ()));
      assertTrue (StringHelper.hasText (aCountry.getCountry ()));
      assertTrue (StringHelper.hasNoText (aCountry.getVariant ()));
    }
  }
}

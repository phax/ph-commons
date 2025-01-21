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
package com.helger.commons.locale.country;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.locale.LocaleCache;
import com.helger.commons.locale.LocaleHelper;
import com.helger.commons.mock.CommonsAssert;
import com.helger.commons.string.StringHelper;

/**
 * Test class for class {@link CountryCache}.
 *
 * @author Philip Helger
 */
public final class CountryCacheTest
{
  private static final Locale LOCALE = new Locale ("de", "AT", "");

  @Before
  public void setup ()
  {
    // is always cleaned along with locale cache!
    LocaleCache.getInstance ().reinitialize ();
    CountryCache.getInstance ().reinitialize ();
  }

  @Test
  public void testGetCountryOfLocale ()
  {
    assertNull (CountryCache.getInstance ().getCountry ((Locale) null));
    assertEquals (CountryCache.getInstance ().getCountry (LOCALE.getCountry ()), CountryCache.getInstance ().getCountry (LOCALE));
  }

  @Test
  public void testGetCountry ()
  {
    assertNull (CountryCache.getInstance ().getCountry (""));
    assertNull (CountryCache.getInstance ().getCountry ((String) null));
    assertNull (CountryCache.getInstance ().getCountry ((Locale) null));
    assertNull (CountryCache.getInstance ().getCountry ("A"));
    assertNotNull (CountryCache.getInstance ().getCountry ("AT"));
    assertNotNull (CountryCache.getInstance ().getCountry ("at"));
    assertNotNull (CountryCache.getInstance ().getCountry ("AUT"));
    assertNotNull (CountryCache.getInstance ().getCountry ("aut"));
    assertNull (CountryCache.getInstance ().getCountry ("AAAA"));
    assertNull (CountryCache.getInstance ().getCountry ("1"));
    assertNull (CountryCache.getInstance ().getCountry ("12"));
    assertNull (CountryCache.getInstance ().getCountryExt ("123", (k, l, c, v) -> null));
    assertNotNull (CountryCache.getInstance ().getCountry ("123"));
    assertNull (CountryCache.getInstance ().getCountry ("1234"));

    assertNotNull (CountryCache.getInstance ().getCountry ("AT"));
    assertNotNull (CountryCache.getInstance ().getCountry ("at"));
    assertNotNull (CountryCache.getInstance ().getCountry ("pl"));
    assertNotNull (CountryCache.getInstance ().getCountry ("CS"));
    // Returns a valid locale, but emits a warning:

    assertEquals (CountryCache.getInstance ().getCountry ("ch"), CountryCache.getInstance ().getCountry (new Locale ("de", "ch")));
    assertEquals (LocaleCache.getInstance ().getLocale ("", "AT", ""), CountryCache.getInstance ().getCountry ("_AT"));
    assertEquals (LocaleCache.getInstance ().getLocale ("", "AT", ""), CountryCache.getInstance ().getCountry ("de_AT"));
    assertEquals (CountryCache.getInstance ().getCountry ("AT"),
                  CountryCache.getInstance ().getCountry (CountryCache.getInstance ().getCountry ("AT").toString ()));
    for (final String sLocale : CountryCache.getInstance ().getAllCountries ())
      assertTrue (CountryCache.getInstance ().containsCountry (sLocale));
    assertFalse (CountryCache.getInstance ().containsCountry ((String) null));
    assertFalse (CountryCache.getInstance ().containsCountry (LocaleHelper.LOCALE_ALL));
    assertFalse (CountryCache.getInstance ().containsCountry ((Locale) null));

    // Kosovo
    assertNotNull (CountryCache.getInstance ().getCountry ("XK"));
    // Proposed value for Northern Ireland
    assertNotNull (CountryCache.getInstance ().getCountry ("XI"));
  }

  @Test (expected = NullPointerException.class)
  public void testAddCountryNull ()
  {
    CountryCache.getInstance ().addCountry (null);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testAddCountryEmpty ()
  {
    CountryCache.getInstance ().addCountry ("");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testAddCountryIllegal ()
  {
    CountryCache.getInstance ().addCountry ("EN AAAA");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testAddCountryInvalidCasing ()
  {
    CountryCache.getInstance ().addCountry ("en");
  }

  @Test
  public void testAddCountry ()
  {
    assertTrue (CountryCache.getInstance ().addCountry ("YX").isChanged ());
    assertFalse (CountryCache.getInstance ().addCountry ("YX").isChanged ());
  }

  @Test
  public void testContainsCountryByLocale ()
  {
    assertFalse (CountryCache.getInstance ().containsCountry ((Locale) null));
    CommonsAssert.assertEquals (CountryCache.getInstance ().containsCountry (LOCALE.getCountry ()),
                                CountryCache.getInstance ().containsCountry (LOCALE));
  }

  @Test
  public void testResetCache ()
  {

    final int nCount = CountryCache.getInstance ().getAllCountries ().size ();
    CountryCache.getInstance ().addCountry ("123");
    assertTrue (CountryCache.getInstance ().containsCountry ("123"));
    assertEquals (nCount + 1, CountryCache.getInstance ().getAllCountries ().size ());
    // is always cleaned along with locale cache!
    LocaleCache.getInstance ().reinitialize ();
    CountryCache.getInstance ().reinitialize ();
    assertEquals (nCount, CountryCache.getInstance ().getAllCountries ().size ());
  }

  @Test
  public void testContainsCountryByString ()
  {
    assertFalse (CountryCache.getInstance ().containsCountry ((String) null));
    assertFalse (CountryCache.getInstance ().containsCountry (""));
    assertFalse (CountryCache.getInstance ().containsCountry ("a"));
    assertFalse (CountryCache.getInstance ().containsCountry ("A"));
    assertFalse (CountryCache.getInstance ().containsCountry ("aaa"));
    assertFalse (CountryCache.getInstance ().containsCountry ("AAA"));
    assertFalse (CountryCache.getInstance ().containsCountry ("1"));
    assertFalse (CountryCache.getInstance ().containsCountry ("12"));
    assertFalse (CountryCache.getInstance ().containsCountry ("1234"));
    assertTrue (CountryCache.getInstance ().containsCountry ("GB"));
    assertTrue (CountryCache.getInstance ().containsCountry ("gb"));
    assertTrue (CountryCache.getInstance ().containsCountry ("CS"));
    assertFalse (CountryCache.getInstance ().containsCountry ("123"));
    CountryCache.getInstance ().addCountry ("123");
    assertTrue (CountryCache.getInstance ().containsCountry ("123"));
  }

  @Test
  public void testGetAllCountriesString ()
  {
    for (final String sCountry : CountryCache.getInstance ().getAllCountries ())
    {
      final Locale aLocale = CountryCache.getInstance ().getCountry (sCountry);
      assertNotNull ("Failed to resolve '" + sCountry + "'", aLocale);
    }
  }

  @Test
  public void testGetAllCountriesLocale ()
  {
    for (final Locale aLocale : CountryCache.getInstance ().getAllCountryLocales ())
    {
      assertNotNull (aLocale);
    }
  }

  @Test
  public void testNoConcurrentModificationString ()
  {
    final ICommonsSet <Locale> aCountries = new CommonsHashSet <> ();
    for (final String sCountry : CountryCache.getInstance ().getAllCountries ())
      aCountries.add (CountryCache.getInstance ().getCountry (sCountry));

    for (final Locale aCountry : aCountries)
    {
      assertNotNull (aCountry);
      assertTrue (StringHelper.hasNoText (aCountry.getLanguage ()));
      assertTrue (StringHelper.hasText (aCountry.getCountry ()));
      assertTrue (StringHelper.hasNoText (aCountry.getVariant ()));
    }
  }

  @Test
  public void testNoConcurrentModificationLocale ()
  {
    final ICommonsSet <Locale> aCountries = new CommonsHashSet <> ();
    for (final Locale aCountry : CountryCache.getInstance ().getAllCountryLocales ())
      aCountries.add (CountryCache.getInstance ().getCountry (aCountry));

    for (final Locale aCountry : aCountries)
    {
      assertNotNull (aCountry);
      assertTrue (StringHelper.hasNoText (aCountry.getLanguage ()));
      assertTrue (StringHelper.hasText (aCountry.getCountry ()));
      assertTrue (StringHelper.hasNoText (aCountry.getVariant ()));
    }
  }

  @Test
  public void testResolve ()
  {
    final CountryCache aCC = CountryCache.getInstance ();

    assertTrue (aCC.containsCountry ("AT"));
    final Locale c1 = aCC.getCountry ("AT");
    assertEquals ("Austria", c1.getDisplayCountry (Locale.US));

    assertTrue (aCC.containsCountry ("AUT"));
    final Locale c2 = aCC.getCountry ("AUT");
    assertEquals ("Austria", c2.getDisplayCountry (Locale.US));
  }
}

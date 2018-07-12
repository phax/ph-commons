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
package com.helger.commons.locale.language;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.locale.LocaleCache;
import com.helger.commons.locale.LocaleHelper;
import com.helger.commons.string.StringHelper;

/**
 * Test class for class {@link LanguageCache}.
 *
 * @author Philip Helger
 */
public final class LanguageCacheTest
{
  private static final Locale LOCALE = new Locale ("de", "AT", "");

  @Test
  public void testGetLanguageOfLocale ()
  {
    assertNull (LanguageCache.getInstance ().getLanguage ((Locale) null));
    assertEquals (LanguageCache.getInstance ().getLanguage (LOCALE.getLanguage ()),
                  LanguageCache.getInstance ().getLanguage (LOCALE));
  }

  @Test
  public void testGetLanguage ()
  {
    assertNull (LanguageCache.getInstance ().getLanguage (""));
    assertNull (LanguageCache.getInstance ().getLanguage ((String) null));
    assertNull (LanguageCache.getInstance ().getLanguage ((Locale) null));
    assertNull (LanguageCache.getInstance ().getLanguage ("A"));
    assertNotNull (LanguageCache.getInstance ().getLanguage ("DE"));
    assertNotNull (LanguageCache.getInstance ().getLanguage ("de"));
    assertNull (LanguageCache.getInstance ().getLanguage ("ABCDEFGHI"));
    assertNull (LanguageCache.getInstance ().getLanguage ("1"));
    assertNull (LanguageCache.getInstance ().getLanguage ("12"));
    assertNotNull (LanguageCache.getInstance ().getLanguage ("zz"));
    assertNull (LanguageCache.getInstance ().getLanguage ("1234"));

    assertNotNull (LanguageCache.getInstance ().getLanguage ("AT"));
    assertNotNull (LanguageCache.getInstance ().getLanguage ("at"));
    assertNotNull (LanguageCache.getInstance ().getLanguage ("pl"));
    // Returns a valid locale, but emits a warning:

    assertEquals (LanguageCache.getInstance ().getLanguage ("de"),
                  LanguageCache.getInstance ().getLanguage (new Locale ("de", "ch")));
    assertEquals (LocaleCache.getInstance ().getLocale ("de", "", ""),
                  LanguageCache.getInstance ().getLanguage ("de_"));
    assertEquals (LocaleCache.getInstance ().getLocale ("de", "", ""),
                  LanguageCache.getInstance ().getLanguage ("de_AT"));
    assertEquals (LanguageCache.getInstance ().getLanguage ("de"),
                  LanguageCache.getInstance ()
                               .getLanguage (LanguageCache.getInstance ().getLanguage ("de").toString ()));
    for (final String sLocale : LanguageCache.getInstance ().getAllLanguages ())
      assertTrue (LanguageCache.getInstance ().containsLanguage (sLocale));
    assertFalse (LanguageCache.getInstance ().containsLanguage ((String) null));
    assertFalse (LanguageCache.getInstance ().containsLanguage (LocaleHelper.LOCALE_ALL));
    assertFalse (LanguageCache.getInstance ().containsLanguage ((Locale) null));
  }

  @Test (expected = NullPointerException.class)
  public void testAddLanguageNull ()
  {
    LanguageCache.getInstance ().addLanguage (null);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testAddLanguageEmpty ()
  {
    LanguageCache.getInstance ().addLanguage ("");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testAddLanguageIllegal ()
  {
    LanguageCache.getInstance ().addLanguage ("EN AAAA");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testAddLanguageInvalidCasing ()
  {
    LanguageCache.getInstance ().addLanguage ("YX");
  }

  @Test
  public void testAddLanguage ()
  {
    assertTrue (LanguageCache.getInstance ().addLanguage ("yx").isChanged ());
  }

  @Test
  public void testContainsLanguageByLocale ()
  {
    assertFalse (LanguageCache.getInstance ().containsLanguage ((Locale) null));
    assertTrue (LanguageCache.getInstance ()
                             .containsLanguage (LOCALE.getLanguage ()) == LanguageCache.getInstance ()
                                                                                       .containsLanguage (LOCALE));
  }

  @Test
  public void testResetCache ()
  {
    // is always cleaned along with locale cache!
    LocaleCache.getInstance ().reinitialize ();
    LanguageCache.getInstance ().reinitialize ();
    final int nCount = LanguageCache.getInstance ().getAllLanguages ().size ();
    LanguageCache.getInstance ().addLanguage ("zzz");
    assertTrue (LanguageCache.getInstance ().containsLanguage ("zzz"));
    assertEquals (nCount + 1, LanguageCache.getInstance ().getAllLanguages ().size ());
    // is always cleaned along with locale cache!
    LocaleCache.getInstance ().reinitialize ();
    LanguageCache.getInstance ().reinitialize ();
    assertEquals (nCount, LanguageCache.getInstance ().getAllLanguages ().size ());
  }

  @Test
  public void testContainsLanguageByString ()
  {
    assertFalse (LanguageCache.getInstance ().containsLanguage ((String) null));
    assertFalse (LanguageCache.getInstance ().containsLanguage (""));
    assertFalse (LanguageCache.getInstance ().containsLanguage ("a"));
    assertFalse (LanguageCache.getInstance ().containsLanguage ("A"));
    assertFalse (LanguageCache.getInstance ().containsLanguage ("aaa"));
    assertFalse (LanguageCache.getInstance ().containsLanguage ("AAA"));
    assertFalse (LanguageCache.getInstance ().containsLanguage ("1"));
    assertFalse (LanguageCache.getInstance ().containsLanguage ("12"));
    assertFalse (LanguageCache.getInstance ().containsLanguage ("1234"));
    assertTrue (LanguageCache.getInstance ().containsLanguage ("EN"));
    assertTrue (LanguageCache.getInstance ().containsLanguage ("en"));
    assertFalse (LanguageCache.getInstance ().containsLanguage ("zzz"));
    LanguageCache.getInstance ().addLanguage ("zzz");
    assertTrue (LanguageCache.getInstance ().containsLanguage ("zzz"));
  }

  @Test
  public void testNoConcurrentModification ()
  {
    final ICommonsSet <Locale> aLanguages = new CommonsHashSet <> ();
    for (final String sLanguage : LanguageCache.getInstance ().getAllLanguages ())
      aLanguages.add (LanguageCache.getInstance ().getLanguage (sLanguage));

    for (final Locale aLanguage : aLanguages)
    {
      assertNotNull (aLanguage);
      assertTrue (StringHelper.hasText (aLanguage.getLanguage ()));
      assertTrue (StringHelper.hasNoText (aLanguage.getCountry ()));
      assertTrue (StringHelper.hasNoText (aLanguage.getVariant ()));
    }
  }

  @Test
  public void testNoConcurrentModification2 ()
  {
    final ICommonsSet <Locale> aCountries = new CommonsHashSet <> ();
    for (final Locale aLanguage : LanguageCache.getInstance ().getAllLanguageLocales ())
      aCountries.add (LanguageCache.getInstance ().getLanguage (aLanguage));

    for (final Locale aLanguage : aCountries)
    {
      assertNotNull (aLanguage);
      assertTrue (StringHelper.hasText (aLanguage.getLanguage ()));
      assertTrue (StringHelper.hasNoText (aLanguage.getCountry ()));
      assertTrue (StringHelper.hasNoText (aLanguage.getVariant ()));
    }
  }
}

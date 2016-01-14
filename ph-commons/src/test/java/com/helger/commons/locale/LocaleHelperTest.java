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
package com.helger.commons.locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Locale;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.CGlobal;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.junit.DebugModeTestRule;
import com.helger.commons.mock.AbstractCommonsTestCase;
import com.helger.commons.system.SystemHelper;

/**
 * Test class for class {@link LocaleHelper}.
 *
 * @author Philip Helger
 */
public final class LocaleHelperTest extends AbstractCommonsTestCase
{
  @Rule
  public final TestRule m_aRule = new DebugModeTestRule ();

  @Test
  public void testGetLocaleDisplayName ()
  {
    assertNotNull (LocaleHelper.getLocaleDisplayName (null, L_DE));
    assertNotNull (LocaleHelper.getLocaleDisplayName (CGlobal.LOCALE_ALL, L_DE));
    assertNotNull (LocaleHelper.getLocaleDisplayName (CGlobal.LOCALE_INDEPENDENT, L_DE));
    assertNotNull (LocaleHelper.getLocaleDisplayName (L_DE, L_DE));
  }

  @Test
  public void testGetLocaleNativeDisplayName ()
  {
    assertNull (LocaleHelper.getLocaleNativeDisplayName (CGlobal.LOCALE_ALL));
    assertNull (LocaleHelper.getLocaleNativeDisplayName (CGlobal.LOCALE_INDEPENDENT));
    assertNotNull (LocaleHelper.getLocaleNativeDisplayName (L_DE));
    try
    {
      LocaleHelper.getLocaleNativeDisplayName (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetAllLocaleDisplayNames ()
  {
    assertNotNull (LocaleHelper.getAllLocaleDisplayNames (LocaleCache.getInstance ().getLocale ("de")));
    assertFalse (LocaleHelper.getAllLocaleDisplayNames (LocaleCache.getInstance ().getLocale ("de")).isEmpty ());
  }

  @Test
  public void testGetCalculatedLocaleListForResolving ()
  {
    try
    {
      LocaleHelper.getCalculatedLocaleListForResolving (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    // Neither language, country not variant
    List <Locale> aList = LocaleHelper.getCalculatedLocaleListForResolving (new Locale (""));
    assertNotNull (aList);
    assertEquals (0, aList.size ());

    // Only the language
    aList = LocaleHelper.getCalculatedLocaleListForResolving (LocaleCache.getInstance ().getLocale ("de"));
    assertNotNull (aList);
    assertEquals (1, aList.size ());
    assertEquals (LocaleCache.getInstance ().getLocale ("de"), aList.get (0));

    // Language + country
    aList = LocaleHelper.getCalculatedLocaleListForResolving (LocaleCache.getInstance ().getLocale ("de", "AT"));
    assertNotNull (aList);
    assertEquals (2, aList.size ());
    assertEquals (LocaleCache.getInstance ().getLocale ("de", "AT"), aList.get (0));
    assertEquals (LocaleCache.getInstance ().getLocale ("de"), aList.get (1));

    // Language + country + Variant
    aList = LocaleHelper.getCalculatedLocaleListForResolving (LocaleCache.getInstance ().getLocale ("de",
                                                                                                    "AT",
                                                                                                    "Wien"));
    assertNotNull (aList);
    assertEquals (3, aList.size ());
    assertEquals (LocaleCache.getInstance ().getLocale ("de", "AT", "Wien"), aList.get (0));
    assertEquals (LocaleCache.getInstance ().getLocale ("de", "AT"), aList.get (1));
    assertEquals (LocaleCache.getInstance ().getLocale ("de"), aList.get (2));

    // No language - only country
    aList = LocaleHelper.getCalculatedLocaleListForResolving (new Locale ("", "AT"));
    assertNotNull (aList);
    assertEquals (0, aList.size ());

    // No language - only country + variant
    aList = LocaleHelper.getCalculatedLocaleListForResolving (new Locale ("", "AT", "Wien"));
    assertNotNull (aList);
    assertEquals (0, aList.size ());
  }

  @Test
  public void testGetLocaleFromString ()
  {
    assertEquals (SystemHelper.getSystemLocale (), LocaleHelper.getLocaleFromString (""));
    assertEquals (LocaleCache.getInstance ().getLocale ("de"), LocaleHelper.getLocaleFromString ("de"));
    assertEquals (LocaleCache.getInstance ().getLocale ("de"), LocaleHelper.getLocaleFromString ("DE"));
    assertEquals (LocaleCache.getInstance ().getLocale ("de", "AT"), LocaleHelper.getLocaleFromString ("de_AT"));
    assertEquals (LocaleCache.getInstance ().getLocale ("de", "AT"), LocaleHelper.getLocaleFromString ("de_at"));
    assertEquals (LocaleCache.getInstance ().getLocale ("de", "AT"), LocaleHelper.getLocaleFromString ("de_at"));
    // only variant is not allowed!
    assertEquals (LocaleCache.getInstance ().getLocale ("", "", ""), LocaleHelper.getLocaleFromString ("__wien"));
    assertEquals (LocaleCache.getInstance ().getLocale ("de", "AT", "WIEN"),
                  LocaleHelper.getLocaleFromString ("de_at_wien"));
    assertEquals (LocaleCache.getInstance ().getLocale ("de", "", "WIEN"),
                  LocaleHelper.getLocaleFromString ("de__wien"));
    assertEquals (LocaleCache.getInstance ().getLocale ("", "AT", "WIEN"),
                  LocaleHelper.getLocaleFromString ("_at_wien"));
    assertEquals (LocaleCache.getInstance ().getLocale ("", "AT", "WIEN"),
                  LocaleHelper.getLocaleFromString ("dee_at_wien"));
    assertEquals (LocaleCache.getInstance ().getLocale ("de", "", "WIEN"),
                  LocaleHelper.getLocaleFromString ("de_att_wien"));
  }

  @Test
  public void testGetLocaleToUseOrFallback ()
  {
    final List <Locale> aLocales = CollectionHelper.newList (L_DE, L_EN, CGlobal.LOCALE_ALL);
    try
    {
      LocaleHelper.getLocaleToUseOrFallback (null, aLocales, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      LocaleHelper.getLocaleToUseOrFallback (L_DE, null, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    assertSame (L_DE, LocaleHelper.getLocaleToUseOrFallback (L_DE, aLocales, null));
    assertEquals (L_DE, LocaleHelper.getLocaleToUseOrFallback (L_DE_AT, aLocales, null));
    assertEquals (CGlobal.LOCALE_ALL, LocaleHelper.getLocaleToUseOrFallback (L_FR, aLocales, null));
    assertEquals (CGlobal.LOCALE_ALL,
                  LocaleHelper.getLocaleToUseOrFallback (CGlobal.LOCALE_INDEPENDENT, aLocales, null));
    assertEquals (CGlobal.LOCALE_INDEPENDENT,
                  LocaleHelper.getLocaleToUseOrFallback (L_FR,
                                                         CollectionHelper.newList (CGlobal.LOCALE_INDEPENDENT),
                                                         null));
    assertNull (LocaleHelper.getLocaleToUseOrFallback (L_FR, CollectionHelper.newList (L_DE, L_EN), null));
    assertEquals (L_FR_FR,
                  LocaleHelper.getLocaleToUseOrFallback (L_FR, CollectionHelper.newList (L_DE, L_EN), L_FR_FR));
    assertEquals (L_FR_FR, LocaleHelper.getLocaleToUseOrFallback (L_FR, CollectionHelper.newList (L_FR_FR), null));
  }

  @Test
  public void testIsSpecialLocale ()
  {
    assertTrue (LocaleHelper.isSpecialLocale (CGlobal.LOCALE_ALL));
    assertTrue (LocaleHelper.isSpecialLocale (CGlobal.LOCALE_INDEPENDENT));
    assertFalse (LocaleHelper.isSpecialLocale (null));
    assertFalse (LocaleHelper.isSpecialLocale (LocaleCache.getInstance ().getLocale ("de")));
  }

  @Test
  public void testIsSpecialLocaleCode ()
  {
    assertTrue (LocaleHelper.isSpecialLocaleCode (CGlobal.LOCALE_ALL.toString ()));
    assertTrue (LocaleHelper.isSpecialLocaleCode (CGlobal.LOCALE_INDEPENDENT.toString ()));
    assertFalse (LocaleHelper.isSpecialLocaleCode (null));
    assertFalse (LocaleHelper.isSpecialLocaleCode ("de"));
  }

  @Test
  public void testGetValidLanguageCode ()
  {
    assertNull (LocaleHelper.getValidLanguageCode (null));
    assertNull (LocaleHelper.getValidLanguageCode (""));
    assertNull (LocaleHelper.getValidLanguageCode ("1"));
    assertNull (LocaleHelper.getValidLanguageCode ("a"));
    assertNull (LocaleHelper.getValidLanguageCode (" "));
    assertNull (LocaleHelper.getValidLanguageCode ("_"));
    assertNull (LocaleHelper.getValidLanguageCode ("_de"));
    assertNull (LocaleHelper.getValidLanguageCode ("d.e"));
    assertNull (LocaleHelper.getValidLanguageCode ("d e"));
    assertNull (LocaleHelper.getValidLanguageCode ("abcdefghi"));
    assertEquals ("de", LocaleHelper.getValidLanguageCode ("De"));
    assertEquals ("abc", LocaleHelper.getValidLanguageCode ("ABC"));
    assertEquals ("en", LocaleHelper.getValidLanguageCode ("en"));
    assertEquals ("abcdefgh", LocaleHelper.getValidLanguageCode ("abcDefgh"));
  }

  @Test
  public void testGetValidCountryCode ()
  {
    assertNull (LocaleHelper.getValidCountryCode (null));
    assertNull (LocaleHelper.getValidCountryCode (""));
    assertNull (LocaleHelper.getValidCountryCode ("1"));
    assertNull (LocaleHelper.getValidCountryCode ("12"));
    assertNull (LocaleHelper.getValidCountryCode ("a"));
    assertNull (LocaleHelper.getValidCountryCode (" "));
    assertNull (LocaleHelper.getValidCountryCode ("_"));
    assertNull (LocaleHelper.getValidCountryCode ("_de"));
    assertNull (LocaleHelper.getValidCountryCode ("d.e"));
    assertNull (LocaleHelper.getValidCountryCode ("d e"));
    assertNull (LocaleHelper.getValidCountryCode ("abc"));
    assertNull (LocaleHelper.getValidCountryCode ("1234"));
    assertEquals ("DE", LocaleHelper.getValidCountryCode ("De"));
    assertEquals ("123", LocaleHelper.getValidCountryCode ("123"));
    assertEquals ("EN", LocaleHelper.getValidCountryCode ("en"));
  }

}

/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
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
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.mock.AbstractPHTestCase;
import com.helger.commons.mock.DebugModeTestRule;
import com.helger.commons.system.SystemHelper;

/**
 * Test class for class {@link LocaleUtils}.
 * 
 * @author Philip Helger
 */
public final class LocaleUtilsTest extends AbstractPHTestCase
{
  @Rule
  public final TestRule m_aRule = new DebugModeTestRule ();

  @Test
  public void testGetLocaleDisplayName ()
  {
    assertNotNull (LocaleUtils.getLocaleDisplayName (null, L_DE));
    assertNotNull (LocaleUtils.getLocaleDisplayName (CGlobal.LOCALE_ALL, L_DE));
    assertNotNull (LocaleUtils.getLocaleDisplayName (CGlobal.LOCALE_INDEPENDENT, L_DE));
    assertNotNull (LocaleUtils.getLocaleDisplayName (L_DE, L_DE));
  }

  @Test
  public void testGetLocaleNativeDisplayName ()
  {
    assertNotNull (LocaleUtils.getLocaleNativeDisplayName (CGlobal.LOCALE_ALL));
    assertNotNull (LocaleUtils.getLocaleNativeDisplayName (CGlobal.LOCALE_INDEPENDENT));
    assertNotNull (LocaleUtils.getLocaleNativeDisplayName (L_DE));
    try
    {
      LocaleUtils.getLocaleNativeDisplayName (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetAllLocaleDisplayNames ()
  {
    assertNotNull (LocaleUtils.getAllLocaleDisplayNames (LocaleCache.getLocale ("de")));
    assertFalse (LocaleUtils.getAllLocaleDisplayNames (LocaleCache.getLocale ("de")).isEmpty ());
  }

  @Test
  public void testGetCalculatedLocaleListForResolving ()
  {
    try
    {
      LocaleUtils.getCalculatedLocaleListForResolving (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    // Neither language, country not variant
    List <Locale> aList = LocaleUtils.getCalculatedLocaleListForResolving (new Locale (""));
    assertNotNull (aList);
    assertEquals (0, aList.size ());

    // Only the language
    aList = LocaleUtils.getCalculatedLocaleListForResolving (LocaleCache.getLocale ("de"));
    assertNotNull (aList);
    assertEquals (1, aList.size ());
    assertEquals (LocaleCache.getLocale ("de"), aList.get (0));

    // Language + country
    aList = LocaleUtils.getCalculatedLocaleListForResolving (LocaleCache.getLocale ("de", "AT"));
    assertNotNull (aList);
    assertEquals (2, aList.size ());
    assertEquals (LocaleCache.getLocale ("de", "AT"), aList.get (0));
    assertEquals (LocaleCache.getLocale ("de"), aList.get (1));

    // Language + country + Variant
    aList = LocaleUtils.getCalculatedLocaleListForResolving (LocaleCache.getLocale ("de", "AT", "Wien"));
    assertNotNull (aList);
    assertEquals (3, aList.size ());
    assertEquals (LocaleCache.getLocale ("de", "AT", "Wien"), aList.get (0));
    assertEquals (LocaleCache.getLocale ("de", "AT"), aList.get (1));
    assertEquals (LocaleCache.getLocale ("de"), aList.get (2));

    // No language - only country
    aList = LocaleUtils.getCalculatedLocaleListForResolving (new Locale ("", "AT"));
    assertNotNull (aList);
    assertEquals (0, aList.size ());

    // No language - only country + variant
    aList = LocaleUtils.getCalculatedLocaleListForResolving (new Locale ("", "AT", "Wien"));
    assertNotNull (aList);
    assertEquals (0, aList.size ());
  }

  @Test
  public void testGetLocaleFromString ()
  {
    assertEquals (SystemHelper.getSystemLocale (), LocaleUtils.getLocaleFromString (""));
    assertEquals (LocaleCache.getLocale ("de"), LocaleUtils.getLocaleFromString ("de"));
    assertEquals (LocaleCache.getLocale ("de"), LocaleUtils.getLocaleFromString ("DE"));
    assertEquals (LocaleCache.getLocale ("de", "AT"), LocaleUtils.getLocaleFromString ("de_AT"));
    assertEquals (LocaleCache.getLocale ("de", "AT"), LocaleUtils.getLocaleFromString ("de_at"));
    assertEquals (LocaleCache.getLocale ("de", "AT"), LocaleUtils.getLocaleFromString ("de_at"));
    // only variant is not allowed!
    assertEquals (LocaleCache.getLocale ("", "", ""), LocaleUtils.getLocaleFromString ("__wien"));
    assertEquals (LocaleCache.getLocale ("de", "AT", "WIEN"), LocaleUtils.getLocaleFromString ("de_at_wien"));
    assertEquals (LocaleCache.getLocale ("de", "", "WIEN"), LocaleUtils.getLocaleFromString ("de__wien"));
    assertEquals (LocaleCache.getLocale ("", "AT", "WIEN"), LocaleUtils.getLocaleFromString ("_at_wien"));
    assertEquals (LocaleCache.getLocale ("", "AT", "WIEN"), LocaleUtils.getLocaleFromString ("dee_at_wien"));
    assertEquals (LocaleCache.getLocale ("de", "", "WIEN"), LocaleUtils.getLocaleFromString ("de_att_wien"));
  }

  @Test
  public void testGetLocaleToUseOrFallback ()
  {
    final List <Locale> aLocales = ContainerHelper.newList (L_DE, L_EN, CGlobal.LOCALE_ALL);
    try
    {
      LocaleUtils.getLocaleToUseOrFallback (null, aLocales, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      LocaleUtils.getLocaleToUseOrFallback (L_DE, null, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    assertSame (L_DE, LocaleUtils.getLocaleToUseOrFallback (L_DE, aLocales, null));
    assertEquals (L_DE, LocaleUtils.getLocaleToUseOrFallback (L_DE_AT, aLocales, null));
    assertEquals (CGlobal.LOCALE_ALL, LocaleUtils.getLocaleToUseOrFallback (L_FR, aLocales, null));
    assertEquals (CGlobal.LOCALE_ALL, LocaleUtils.getLocaleToUseOrFallback (CGlobal.LOCALE_INDEPENDENT, aLocales, null));
    assertEquals (CGlobal.LOCALE_INDEPENDENT,
                  LocaleUtils.getLocaleToUseOrFallback (L_FR,
                                                        ContainerHelper.newList (CGlobal.LOCALE_INDEPENDENT),
                                                        null));
    assertNull (LocaleUtils.getLocaleToUseOrFallback (L_FR, ContainerHelper.newList (L_DE, L_EN), null));
    assertEquals (L_FR_FR, LocaleUtils.getLocaleToUseOrFallback (L_FR, ContainerHelper.newList (L_DE, L_EN), L_FR_FR));
    assertEquals (L_FR_FR, LocaleUtils.getLocaleToUseOrFallback (L_FR, ContainerHelper.newList (L_FR_FR), null));
  }

  @Test
  public void testIsSpecialLocale ()
  {
    assertTrue (LocaleUtils.isSpecialLocale (CGlobal.LOCALE_ALL));
    assertTrue (LocaleUtils.isSpecialLocale (CGlobal.LOCALE_INDEPENDENT));
    assertFalse (LocaleUtils.isSpecialLocale (null));
    assertFalse (LocaleUtils.isSpecialLocale (LocaleCache.getLocale ("de")));
  }

  @Test
  public void testIsSpecialLocaleCode ()
  {
    assertTrue (LocaleUtils.isSpecialLocaleCode (CGlobal.LOCALE_ALL.toString ()));
    assertTrue (LocaleUtils.isSpecialLocaleCode (CGlobal.LOCALE_INDEPENDENT.toString ()));
    assertFalse (LocaleUtils.isSpecialLocaleCode (null));
    assertFalse (LocaleUtils.isSpecialLocaleCode ("de"));
  }

  @Test
  public void testGetValidLanguageCode ()
  {
    assertNull (LocaleUtils.getValidLanguageCode (null));
    assertNull (LocaleUtils.getValidLanguageCode (""));
    assertNull (LocaleUtils.getValidLanguageCode ("1"));
    assertNull (LocaleUtils.getValidLanguageCode ("a"));
    assertNull (LocaleUtils.getValidLanguageCode (" "));
    assertNull (LocaleUtils.getValidLanguageCode ("_"));
    assertNull (LocaleUtils.getValidLanguageCode ("_de"));
    assertNull (LocaleUtils.getValidLanguageCode ("d.e"));
    assertNull (LocaleUtils.getValidLanguageCode ("d e"));
    assertNull (LocaleUtils.getValidLanguageCode ("abcdefghi"));
    assertEquals ("de", LocaleUtils.getValidLanguageCode ("De"));
    assertEquals ("abc", LocaleUtils.getValidLanguageCode ("ABC"));
    assertEquals ("en", LocaleUtils.getValidLanguageCode ("en"));
    assertEquals ("abcdefgh", LocaleUtils.getValidLanguageCode ("abcDefgh"));
  }

  @Test
  public void testGetValidCountryCode ()
  {
    assertNull (LocaleUtils.getValidCountryCode (null));
    assertNull (LocaleUtils.getValidCountryCode (""));
    assertNull (LocaleUtils.getValidCountryCode ("1"));
    assertNull (LocaleUtils.getValidCountryCode ("12"));
    assertNull (LocaleUtils.getValidCountryCode ("a"));
    assertNull (LocaleUtils.getValidCountryCode (" "));
    assertNull (LocaleUtils.getValidCountryCode ("_"));
    assertNull (LocaleUtils.getValidCountryCode ("_de"));
    assertNull (LocaleUtils.getValidCountryCode ("d.e"));
    assertNull (LocaleUtils.getValidCountryCode ("d e"));
    assertNull (LocaleUtils.getValidCountryCode ("abc"));
    assertNull (LocaleUtils.getValidCountryCode ("1234"));
    assertEquals ("DE", LocaleUtils.getValidCountryCode ("De"));
    assertEquals ("123", LocaleUtils.getValidCountryCode ("123"));
    assertEquals ("EN", LocaleUtils.getValidCountryCode ("en"));
  }

}

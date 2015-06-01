/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.Set;

import org.junit.Test;

import com.helger.commons.collections.CollectionHelper;
import com.helger.commons.mock.AbstractCommonsTestCase;
import com.helger.commons.string.StringHelper;

/**
 * Test class for class {@link LocaleCache}.
 *
 * @author Philip Helger
 */
public final class LocaleCacheTest extends AbstractCommonsTestCase
{
  @Test
  public void testGet ()
  {
    assertNotNull (LocaleCache.getLocale ("de"));
    assertNotNull (LocaleCache.getLocale ("de_at"));
    assertNotNull (LocaleCache.getLocale ("de_surely_not_known"));
    assertNull (LocaleCache.getLocale (null));
    assertNull (LocaleCache.getLocale (""));

    assertNotNull (LocaleCache.getLocale ("de", "AT"));
    assertEquals ("de_AT", LocaleCache.getLocale ("de", "AT").toString ());
    assertEquals ("de_AT", LocaleCache.getLocale ("de", "at").toString ());
    assertEquals ("de", LocaleCache.getLocale ("de", null).toString ());
    assertEquals ("_AT", LocaleCache.getLocale (null, "AT").toString ());
    assertNull (LocaleCache.getLocale (null, null));

    assertNotNull (LocaleCache.getLocale ("de", "AT", "Vienna"));
    assertEquals ("de__Vienna", new Locale ("de", "", "Vienna").toString ());
    assertEquals ("de__Vienna", LocaleCache.getLocale ("de", null, "Vienna").toString ());
    assertEquals ("de_AT", LocaleCache.getLocale ("de", "AT", null).toString ());
  }

  @Test
  public void testGetInvalid ()
  {
    assertNull (LocaleCache.getLocale ("gb result: chosen nickname \"stevenwhitecotton063\"; success;"));
    assertNull (LocaleCache.getLocale ("aa bb"));
  }

  @Test
  public void testGetAllLocales ()
  {
    assertNotNull (LocaleCache.getAllLocales ());
    for (final Locale aLocale : LocaleCache.getAllLocales ())
      assertNotNull (aLocale);
  }

  @Test
  public void testGetAllLanguages ()
  {
    for (final Locale aLocale : LocaleCache.getAllLanguages ())
    {
      assertNotNull (aLocale);
      assertTrue (StringHelper.hasText (aLocale.getLanguage ()));
      assertTrue (StringHelper.hasNoText (aLocale.getCountry ()));
      assertTrue (StringHelper.hasNoText (aLocale.getVariant ()));
    }
  }

  @Test
  public void testCompare ()
  {
    final Set <Locale> aLocales = LocaleCache.getAllLocales ();
    assertEquals (aLocales.size (),
                  CollectionHelper.getSorted (aLocales, new CollatingComparatorLocaleDisplayName (L_DE)).size ());
    assertEquals (aLocales.size (),
                  CollectionHelper.getSorted (aLocales, new CollatingComparatorLocaleDisplayNameNative (L_DE)).size ());
    assertEquals (aLocales.size (),
                  CollectionHelper.getSorted (aLocales, new CollatingComparatorLocaleDisplayNameInLocale (L_DE, L_EN))
                                  .size ());
  }

  @Test
  public void testContainsLocale ()
  {
    assertFalse (LocaleCache.containsLocale (null));
    assertFalse (LocaleCache.containsLocale (null));
    assertTrue (LocaleCache.containsLocale ("de"));
    assertTrue (LocaleCache.containsLocale ("de_at"));
    assertFalse (LocaleCache.containsLocale ("de_at_var"));
    assertFalse (LocaleCache.containsLocale ("de_xx"));
    assertFalse (LocaleCache.containsLocale ("deh"));

    assertTrue (LocaleCache.containsLocale ("de", "at"));
    assertFalse (LocaleCache.containsLocale ("de", "xx"));

    assertFalse (LocaleCache.containsLocale (null, null, null));
    assertTrue (LocaleCache.containsLocale ("de", null, null));
    assertTrue (LocaleCache.containsLocale ("de", "at", null));
    assertFalse (LocaleCache.containsLocale ("de", "xx", null));
    assertFalse (LocaleCache.containsLocale ("de", "at", "var"));
  }

  @Test
  public void testResetCache ()
  {
    LocaleCache.resetCache ();
    final int nCount = LocaleCache.getAllLanguages ().size ();
    LocaleCache.getLocale ("xy");
    assertEquals (nCount + 1, LocaleCache.getAllLanguages ().size ());
    LocaleCache.resetCache ();
    assertEquals (nCount, LocaleCache.getAllLanguages ().size ());
  }
}

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
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

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
    assertNotNull (LocaleCache.getInstance ().getLocale ("de"));
    assertNotNull (LocaleCache.getInstance ().getLocale ("de_at"));
    assertNotNull (LocaleCache.getInstance ().getLocale ("de_surely_not_known"));
    assertNull (LocaleCache.getInstance ().getLocale (null));
    assertNull (LocaleCache.getInstance ().getLocale (""));

    assertNotNull (LocaleCache.getInstance ().getLocale ("de", "AT"));
    assertEquals ("de_AT", LocaleCache.getInstance ().getLocale ("de", "AT").toString ());
    assertEquals ("de_AT", LocaleCache.getInstance ().getLocale ("de", "at").toString ());
    assertEquals ("de", LocaleCache.getInstance ().getLocale ("de", null).toString ());
    assertEquals ("_AT", LocaleCache.getInstance ().getLocale (null, "AT").toString ());
    assertNull (LocaleCache.getInstance ().getLocale (null, null));

    assertNotNull (LocaleCache.getInstance ().getLocale ("de", "AT", "Vienna"));
    assertEquals ("de__Vienna", new Locale ("de", "", "Vienna").toString ());
    assertEquals ("de__Vienna", LocaleCache.getInstance ().getLocale ("de", null, "Vienna").toString ());
    assertEquals ("de_AT", LocaleCache.getInstance ().getLocale ("de", "AT", null).toString ());
  }

  @Test
  public void testGetInvalid ()
  {
    assertNull (LocaleCache.getInstance ().getLocale ("gb result: chosen nickname \"stevenwhitecotton063\"; success;"));
    assertNull (LocaleCache.getInstance ().getLocale ("aa bb"));
  }

  @Test
  public void testGetAllLocales ()
  {
    assertNotNull (LocaleCache.getInstance ().getAllLocales ());
    for (final Locale aLocale : LocaleCache.getInstance ().getAllLocales ())
      assertNotNull (aLocale);
  }

  @Test
  public void testGetAllLanguages ()
  {
    for (final Locale aLocale : LocaleCache.getInstance ().getAllLanguages ())
    {
      assertNotNull (aLocale);
      assertTrue (StringHelper.hasText (aLocale.getLanguage ()));
      assertTrue (StringHelper.hasNoText (aLocale.getCountry ()));
      assertTrue (StringHelper.hasNoText (aLocale.getVariant ()));
    }
  }

  @Test
  public void testContainsLocale ()
  {
    assertFalse (LocaleCache.getInstance ().containsLocale (null));
    assertFalse (LocaleCache.getInstance ().containsLocale (null));
    assertTrue (LocaleCache.getInstance ().containsLocale ("de"));
    assertTrue (LocaleCache.getInstance ().containsLocale ("de_at"));
    assertFalse (LocaleCache.getInstance ().containsLocale ("de_at_var"));
    assertFalse (LocaleCache.getInstance ().containsLocale ("de_xx"));
    assertFalse (LocaleCache.getInstance ().containsLocale ("deh"));

    assertTrue (LocaleCache.getInstance ().containsLocale ("de", "at"));
    assertFalse (LocaleCache.getInstance ().containsLocale ("de", "xx"));

    assertFalse (LocaleCache.getInstance ().containsLocale (null, null, null));
    assertTrue (LocaleCache.getInstance ().containsLocale ("de", null, null));
    assertTrue (LocaleCache.getInstance ().containsLocale ("de", "at", null));
    assertFalse (LocaleCache.getInstance ().containsLocale ("de", "xx", null));
    assertFalse (LocaleCache.getInstance ().containsLocale ("de", "at", "var"));
  }

  @Test
  public void testResetCache ()
  {
    LocaleCache.getInstance ().reinitialize ();
    final int nCount = LocaleCache.getInstance ().getAllLanguages ().size ();
    LocaleCache.getInstance ().getLocale ("xy");
    assertEquals (nCount + 1, LocaleCache.getInstance ().getAllLanguages ().size ());
    LocaleCache.getInstance ().reinitialize ();
    assertEquals (nCount, LocaleCache.getInstance ().getAllLanguages ().size ());
  }
}

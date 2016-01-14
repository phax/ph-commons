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
package com.helger.commons.locale.country;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.helger.commons.mock.AbstractCommonsTestCase;

/**
 * Test class for class {@link ECountry}.
 *
 * @author Philip Helger
 */
public final class ECountryTest extends AbstractCommonsTestCase
{
  @Test
  public void testAll ()
  {
    for (final ECountry eCountry : ECountry.values ())
    {
      assertSame (eCountry, ECountry.valueOf (eCountry.name ()));
      assertSame (eCountry, ECountry.getFromIDOrNull (eCountry.getID ()));
      if (!eCountry.isCountrySub () && !eCountry.getISOCountryCode ().equalsIgnoreCase ("GB"))
        assertSame (eCountry, ECountry.getFromISOCodeOrNull (eCountry.getISOCountryCode ()));
      assertNotNull (eCountry.getDisplayText (L_DE));
    }
    assertNull (ECountry.getFromISOCodeOrNull (null));
    assertNull (ECountry.getFromISOCodeOrNull ("123"));
    assertNotNull (ECountry.getCountryListAsLocales ());
  }
}

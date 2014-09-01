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

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.locale.LocaleCache;
import com.helger.commons.mock.AbstractPHTestCase;

/**
 * Test class for class {@link ComparatorLocaleDisplayCountryInLocale}.
 * 
 * @author Philip Helger
 */
public final class ComparatorLocaleDisplayCountryInLocaleTest extends AbstractPHTestCase
{
  @Test
  public void testSort ()
  {
    assertNotNull (ContainerHelper.getSorted (LocaleCache.getAllLocales (),
                                              new ComparatorLocaleDisplayCountryInLocale (L_DE, L_EN)));
  }
}

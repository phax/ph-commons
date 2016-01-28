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

import java.util.Comparator;
import java.util.Locale;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.compare.ISerializableComparator;

public final class JavaListAllLocalesFuncTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (JavaListAllLocalesFuncTest.class);

  @Test
  public void testListAllCountries ()
  {
    for (final Locale aLocale : CollectionHelper.getSorted (Locale.getAvailableLocales (),
                                                            ISerializableComparator.getComparatorCollating (Locale::getCountry,
                                                                                                            Locale.US)))
      if (aLocale.getCountry ().length () > 0)
        s_aLogger.info (aLocale.getCountry () + " " + aLocale.getDisplayCountry () + " (" + aLocale.toString () + ")");
  }

  @Test
  public void testListAllSerbianCountries ()
  {
    for (final Locale aLocale : CollectionHelper.getSorted (Locale.getAvailableLocales (),
                                                            Comparator.comparing (Locale::toString)))
      if (aLocale.getLanguage ().equals ("sr") ||
          aLocale.getLanguage ().equals ("sh") ||
          aLocale.getLanguage ().equals ("bs"))
        s_aLogger.info (aLocale.toString () + ": " + aLocale.getDisplayName ());
  }
}

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
package com.helger.commons.supplementary.test.java;

import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.Locale;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.string.Strings;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.compare.IComparator;
import com.helger.commons.locale.LocaleCache;

public final class JavaCollatorFuncTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (JavaCollatorFuncTest.class);

  @Test
  public void testCollation () throws ParseException
  {
    final ICommonsSet <String> aSet = new CommonsHashSet <> ();
    aSet.add ("a");
    aSet.add ("A");
    aSet.add ("Ä");
    aSet.add ("ä");
    aSet.add (" ");
    aSet.add (".");
    aSet.add ("_");
    aSet.add ("#");
    aSet.add ("+");
    aSet.add ("0");
    aSet.add ("1");
    aSet.add ("2");
    aSet.add ("3");
    aSet.add ("4");
    aSet.add ("1.1 A");
    aSet.add ("1.1.1 A");
    aSet.add ("1.1.1.A");
    aSet.add ("A A A");
    aSet.add ("A A");
    aSet.add ("AA A");

    final Locale aLocale = LocaleCache.getInstance ().getLocale ("de_DE");

    for (final String s : aSet.getSorted (IComparator.getComparatorCollating (Locale.US)))
    {
      LOGGER.info (s);
    }
    LOGGER.info ("-------------------------1");

    final Collator aColl = Collator.getInstance (aLocale);
    aColl.setStrength (Collator.TERTIARY);
    aColl.setDecomposition (Collator.FULL_DECOMPOSITION);
    for (final String s : aSet.getSorted (IComparator.getComparatorCollating (aColl)))
    {
      LOGGER.info (s);
    }
    LOGGER.info ("-------------------------2");

    final RuleBasedCollator defaultCollator = (RuleBasedCollator) Collator.getInstance (aLocale);
    final String rules = defaultCollator.getRules ();
    // add rule for space before '_'
    final String sNewRules = Strings.replaceAll (rules, "<'.'<", "<' '<'.'<");
    final RuleBasedCollator collator2 = new RuleBasedCollator (sNewRules);
    collator2.setStrength (Collator.TERTIARY);
    collator2.setDecomposition (Collator.FULL_DECOMPOSITION);
    for (final String s : aSet.getSorted (IComparator.getComparatorCollating (collator2)))
    {
      LOGGER.info (s);
    }
    LOGGER.info ("-------------------------3");
  }
}

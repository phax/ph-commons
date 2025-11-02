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
package com.helger.text.compare;

import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.state.EChange;
import com.helger.base.string.StringReplace;
import com.helger.base.system.SystemHelper;
import com.helger.cache.impl.Cache;

/**
 * Helper class to easily create commonly used {@link Collator} objects.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class CollatorHelper
{
  private static final Logger LOGGER = LoggerFactory.getLogger (CollatorHelper.class);

  private static final Cache <Locale, Collator> COLLATOR_CACHE;
  static
  {
    COLLATOR_CACHE = Cache.<Locale, Collator> builder ().valueProvider (aLocale -> {
      if (aLocale == null)
      {
        LOGGER.error ("Very weird: no locale passed in. Falling back to system locale.");
        return Collator.getInstance (SystemHelper.getSystemLocale ());
      }

      /*
       * Collator.getInstance is synchronized and therefore extremely slow -> that's why we put a
       * cache around it!
       */
      final Collator aCollator = Collator.getInstance (aLocale);
      if (!(aCollator instanceof RuleBasedCollator))
      {
        LOGGER.warn ("Collator.getInstance did not return a RulleBasedCollator but a " +
                     aCollator.getClass ().getName ());
        return aCollator;
      }

      try
      {
        final String sRules = ((RuleBasedCollator) aCollator).getRules ();
        if (!sRules.contains ("<'.'<"))
        {
          /*
           * Nothing to replace - use collator as it is
           */
          LOGGER.warn ("Failed to identify the Collator rule part to be replaced. Locale used: " + aLocale);
          return aCollator;
        }

        final String sNewRules = StringReplace.replaceAll (sRules, "<'.'<", "<' '<'.'<");
        final RuleBasedCollator aNewCollator = new RuleBasedCollator (sNewRules);
        aNewCollator.setStrength (Collator.TERTIARY);
        aNewCollator.setDecomposition (Collator.FULL_DECOMPOSITION);
        return aNewCollator;
      }
      catch (final ParseException ex)
      {
        throw new IllegalStateException ("Failed to parse collator rule set for locale " + aLocale, ex);
      }
    }).maxSize (500).name (CollatorHelper.class.getName () + "$Cache").build ();
  }

  @PresentForCodeCoverage
  private static final CollatorHelper INSTANCE = new CollatorHelper ();

  private CollatorHelper ()
  {}

  /**
   * Create a collator that is based on the standard collator but sorts spaces before dots, because
   * spaces are more important word separators than dots. Another example is the correct sorting of
   * things like "1.1 a" vs. "1.1.1 b" . This is the default collator used for sorting by default!
   *
   * @param aLocale
   *        The locale for which the collator is to be retrieved. May be <code>null</code> to
   *        indicate the usage of the default locale.
   * @return The created {@link RuleBasedCollator} and never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static Collator getCollatorSpaceBeforeDot (@Nullable final Locale aLocale)
  {
    // Ensure to not pass null locale in
    final Locale aRealLocale = aLocale == null ? SystemHelper.getSystemLocale () : aLocale;

    // Always create a clone!
    return (Collator) COLLATOR_CACHE.getFromCache (aRealLocale).clone ();
  }

  /**
   * Clear all cached collators.
   *
   * @return {@link EChange}
   */
  @NonNull
  public static EChange clearCache ()
  {
    return COLLATOR_CACHE.clearCache ();
  }
}

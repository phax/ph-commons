/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.commons.compare;

import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.cache.Cache;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;
import com.helger.commons.system.SystemHelper;

/**
 * Helper class to easily create commonly used {@link Collator} objects.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class CollatorHelper
{
  /**
   * Local cache from Locale to Collator because Collator.getInstance is
   * synchronized!
   *
   * @author Philip Helger
   */
  @ThreadSafe
  private static final class CollatorCache extends Cache <Locale, Collator>
  {
    private static final Logger LOGGER = LoggerFactory.getLogger (CollatorCache.class);

    public CollatorCache ()
    {
      super (aLocale -> {
        if (aLocale == null)
        {
          LOGGER.error ("Very weird: no locale passed in. Falling back to system locale.");
          return Collator.getInstance (SystemHelper.getSystemLocale ());
        }

        // Collator.getInstance is synchronized and therefore extremely slow ->
        // that's why we put a cache around it!
        final Collator aCollator = Collator.getInstance (aLocale);
        if (!(aCollator instanceof RuleBasedCollator))
        {
          LOGGER.warn ("Collator.getInstance did not return a RulleBasedCollator but a " + aCollator.getClass ().getName ());
          return aCollator;
        }

        try
        {
          final String sRules = ((RuleBasedCollator) aCollator).getRules ();
          if (!sRules.contains ("<'.'<"))
          {
            // Nothing to replace - use collator as it is
            LOGGER.warn ("Failed to identify the Collator rule part to be replaced. Locale used: " + aLocale);
            return aCollator;
          }

          final String sNewRules = StringHelper.replaceAll (sRules, "<'.'<", "<' '<'.'<");
          final RuleBasedCollator aNewCollator = new RuleBasedCollator (sNewRules);
          aNewCollator.setStrength (Collator.TERTIARY);
          aNewCollator.setDecomposition (Collator.FULL_DECOMPOSITION);
          return aNewCollator;
        }
        catch (final ParseException ex)
        {
          throw new IllegalStateException ("Failed to parse collator rule set for locale " + aLocale, ex);
        }
      }, 500, CollatorHelper.class.getName ());
    }
  }

  private static final CollatorCache COLLATOR_CACHE = new CollatorCache ();

  @PresentForCodeCoverage
  private static final CollatorHelper INSTANCE = new CollatorHelper ();

  private CollatorHelper ()
  {}

  /**
   * Create a collator that is based on the standard collator but sorts spaces
   * before dots, because spaces are more important word separators than dots.
   * Another example is the correct sorting of things like "1.1 a" vs. "1.1.1 b"
   * . This is the default collator used for sorting by default!
   *
   * @param aLocale
   *        The locale for which the collator is to be retrieved. May be
   *        <code>null</code> to indicate the usage of the default locale.
   * @return The created {@link RuleBasedCollator} and never <code>null</code>.
   */
  @Nonnull
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
  @Nonnull
  public static EChange clearCache ()
  {
    return COLLATOR_CACHE.clearCache ();
  }
}

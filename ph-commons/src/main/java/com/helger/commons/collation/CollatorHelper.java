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
package com.helger.commons.collation;

import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.ELockType;
import com.helger.commons.annotation.IsLocked;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.cache.AbstractNotifyingCache;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;
import com.helger.commons.system.SystemHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

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
  private static final class CollatorCache extends AbstractNotifyingCache <Locale, Collator>
  {
    private static final Logger s_aLogger = LoggerFactory.getLogger (CollatorCache.class);

    public CollatorCache ()
    {
      super (-1, CollatorHelper.class.getName ());
    }

    @Override
    @Nonnull
    @IsLocked (ELockType.WRITE)
    @SuppressFBWarnings ("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
    protected Collator getValueToCache (@Nullable final Locale aLocale)
    {
      if (aLocale == null)
      {
        s_aLogger.error ("Very weird: no locale passed in. Falling back to system locale.");
        return Collator.getInstance (SystemHelper.getSystemLocale ());
      }

      // Collator.getInstance is synchronized and therefore extremely slow ->
      // that's why we put a cache around it!
      final Collator aCollator = Collator.getInstance (aLocale);
      if (aCollator == null)
      {
        final Locale aSystemLocale = SystemHelper.getSystemLocale ();
        s_aLogger.error ("Failed to get Collator for Locale " +
                         aLocale +
                         " - using Collator for syste locale " +
                         aSystemLocale +
                         "!");
        return Collator.getInstance (aSystemLocale);
      }
      if (!(aCollator instanceof RuleBasedCollator))
      {
        s_aLogger.warn ("Collator.getInstance did not return a RulleBasedCollator but a " +
                        aCollator.getClass ().getName ());
        return aCollator;
      }

      try
      {
        final String sRules = ((RuleBasedCollator) aCollator).getRules ();
        if (!sRules.contains ("<'.'<"))
        {
          // Nothing to replace - use collator as it is
          s_aLogger.warn ("Failed to identify the Collator rule part to be replaced. Locale used: " + aLocale);
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
    }
  }

  private static final CollatorCache s_aCache = new CollatorCache ();

  @PresentForCodeCoverage
  private static final CollatorHelper s_aInstance = new CollatorHelper ();

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
    return (Collator) s_aCache.getFromCache (aRealLocale).clone ();
  }

  /**
   * Clear all cached collators.
   *
   * @return {@link EChange}
   */
  @Nonnull
  public static EChange clearCache ()
  {
    return s_aCache.clearCache ();
  }
}

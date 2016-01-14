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
package com.helger.commons.text;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.locale.LocaleHelper;
import com.helger.commons.regex.RegExHelper;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

/**
 * A {@link Map} based implementation of {@link IMultilingualText} that does not
 * provide writing methods to the outside and is only to be used as a
 * non-abstract base class.
 *
 * @author Philip Helger
 */
public abstract class AbstractReadOnlyMapBasedMultilingualText extends AbstractHasText implements IMultilingualText
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractReadOnlyMapBasedMultilingualText.class);
  private static final AtomicBoolean s_aConsistencyChecksEnabled = new AtomicBoolean (GlobalDebug.isDebugMode ());

  private final Map <Locale, String> m_aTexts;

  public AbstractReadOnlyMapBasedMultilingualText ()
  {
    // Use a HashMap by default
    this (new HashMap <Locale, String> ());
  }

  /**
   * Protected constructor that specifies the underlying {@link Map} to use. Use
   * this constructor to e.g. provide a concurrent {@link HashMap} or similar.
   *
   * @param aMapToUse
   *        The map to use. Must not be <code>null</code> and must be writable.
   */
  protected AbstractReadOnlyMapBasedMultilingualText (@Nonnull final Map <Locale, String> aMapToUse)
  {
    m_aTexts = ValueEnforcer.notNull (aMapToUse, "MapToUse");
  }

  /**
   * Enable or disable the internal consistency checks.
   *
   * @param bPerformConsistencyChecks
   *        <code>true</code> to enable them, <code>false</code> to disable
   *        them.
   */
  public static void setPerformConsistencyChecks (final boolean bPerformConsistencyChecks)
  {
    s_aConsistencyChecksEnabled.set (bPerformConsistencyChecks);
  }

  /**
   * @return <code>true</code> if consistency checks are enabled,
   *         <code>false</code> if not. The default value is
   *         {@link GlobalDebug#isDebugMode()}.
   */
  public static boolean isPerformConsistencyChecks ()
  {
    return s_aConsistencyChecksEnabled.get ();
  }

  private static void _performConsistencyChecks (@Nonnull final String sValue)
  {
    // String contains masked newline? warning only!
    if (sValue.contains ("\\n"))
      s_aLogger.warn ("Passed string contains a masked newline - replace with an inline one:\n" + sValue);

    if (sValue.contains ("{0}"))
    {
      // When formatting is used, 2 single quotes are required!
      if (RegExHelper.stringMatchesPattern ("^'[^'].*", sValue))
        throw new IllegalArgumentException ("The passed string seems to start with unclosed single quotes: " + sValue);
      if (RegExHelper.stringMatchesPattern (".*[^']'[^'].*", sValue))
        throw new IllegalArgumentException ("The passed string seems to contain unclosed single quotes: " + sValue);
    }
    else
    {
      // When no formatting is used, single quotes are required!
      if (RegExHelper.stringMatchesPattern (".*''.*", sValue))
        throw new IllegalArgumentException ("The passed string seems to contain 2 single quotes: " + sValue);
    }
  }

  protected final void internalAddText (@Nonnull final Map.Entry <Locale, String> aEntry)
  {
    internalAddText (aEntry.getKey (), aEntry.getValue ());
  }

  protected final void internalAddText (@Nonnull final Locale aContentLocale, @Nullable final String sValue)
  {
    // Check here as well, because this method is invoked in constructors of
    // derived classes
    ValueEnforcer.notNull (aContentLocale, "ContentLocale");

    if (m_aTexts.containsKey (aContentLocale))
      throw new IllegalArgumentException ("Locale '" + aContentLocale + "' already contained: " + this.toString ());

    internalSetText (aContentLocale, sValue);
  }

  protected final void internalSetText (@Nonnull final Locale aContentLocale, @Nullable final String sValue)
  {
    if (sValue != null && s_aConsistencyChecksEnabled.get ())
      _performConsistencyChecks (sValue);

    m_aTexts.put (aContentLocale, sValue);
  }

  @Nonnull
  protected final EChange internalRemoveText (@Nullable final Locale aContentLocale)
  {
    return EChange.valueOf (m_aTexts.remove (aContentLocale) != null);
  }

  protected final void internalClear ()
  {
    m_aTexts.clear ();
  }

  @Nonnull
  @ReturnsMutableObject ("Internal use only")
  protected final Map <Locale, String> internalGetMap ()
  {
    return m_aTexts;
  }

  @Override
  @Nullable
  protected final Locale internalGetLocaleToUseWithFallback (@Nonnull final Locale aContentLocale)
  {
    // Always perform locale fallback resolution
    return LocaleHelper.getLocaleToUseOrNull (aContentLocale, m_aTexts.keySet ());
  }

  @Override
  @Nullable
  protected final String internalGetText (@Nonnull final Locale aContentLocale)
  {
    return m_aTexts.get (aContentLocale);
  }

  @Nonnull
  @ReturnsMutableObject ("Internal use only")
  protected final Set <Locale> internalGetAllLocales ()
  {
    return m_aTexts.keySet ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public final Set <Locale> getAllLocales ()
  {
    return CollectionHelper.newSet (m_aTexts.keySet ());
  }

  @Nonnegative
  public final int getLocaleCount ()
  {
    return m_aTexts.size ();
  }

  public final boolean containsLocale (@Nullable final Locale aLocale)
  {
    return m_aTexts.containsKey (aLocale);
  }

  public final boolean containsLocaleWithFallback (@Nullable final Locale aContentLocale)
  {
    if (aContentLocale != null)
      for (final Locale aCurrentLocale : LocaleHelper.getCalculatedLocaleListForResolving (aContentLocale))
        if (containsLocale (aCurrentLocale))
          return true;
    return false;
  }

  @Nonnull
  @ReturnsMutableCopy
  public final Map <Locale, String> getAllTexts ()
  {
    return CollectionHelper.newMap (m_aTexts);
  }

  @Nonnegative
  public final int getSize ()
  {
    return m_aTexts.size ();
  }

  public final boolean isEmpty ()
  {
    return m_aTexts.isEmpty ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final AbstractReadOnlyMapBasedMultilingualText rhs = (AbstractReadOnlyMapBasedMultilingualText) o;
    return m_aTexts.equals (rhs.m_aTexts);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aTexts).getHashCode ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public String toString ()
  {
    return new ToStringGenerator (this).append ("texts", m_aTexts).toString ();
  }
}

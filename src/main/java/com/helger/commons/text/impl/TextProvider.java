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
package com.helger.commons.text.impl;

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

import com.helger.commons.GlobalDebug;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.OverrideOnDemand;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.annotations.ReturnsMutableObject;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.locale.LocaleCache;
import com.helger.commons.locale.LocaleUtils;
import com.helger.commons.regex.RegExHelper;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.text.IReadonlyMultiLingualText;

/**
 * An in-memory implementation of the
 * {@link com.helger.commons.text.ITextProvider} interface.
 *
 * @author Philip Helger
 */
public class TextProvider extends AbstractTextProvider implements IReadonlyMultiLingualText
{
  /** German locale used */
  public static final Locale DE = LocaleCache.getLocale ("de");
  /** English locale used */
  public static final Locale EN = LocaleCache.getLocale ("en");

  private static final Logger s_aLogger = LoggerFactory.getLogger (TextProvider.class);
  private static final AtomicBoolean s_aConsistencyChecksEnabled = new AtomicBoolean (GlobalDebug.isDebugMode ());

  private final Map <Locale, String> m_aTexts = new HashMap <Locale, String> ();

  public TextProvider ()
  {}

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

  @Nonnull
  protected final TextProvider internalAddText (@Nonnull final Locale aContentLocale, @Nonnull final String sValue)
  {
    if (m_aTexts.containsKey (aContentLocale))
      throw new IllegalArgumentException ("Locale '" +
                                          aContentLocale +
                                          "' already contained in TextProvider: " +
                                          toString ());

    return internalSetText (aContentLocale, sValue);
  }

  @Nonnull
  protected final TextProvider internalSetText (@Nonnull final Locale aContentLocale, @Nullable final String sValue)
  {
    ValueEnforcer.notNull (aContentLocale, "ContentLocale");

    if (sValue != null && s_aConsistencyChecksEnabled.get ())
      _performConsistencyChecks (sValue);

    m_aTexts.put (aContentLocale, sValue);
    return this;
  }

  @Nonnull
  protected final EChange internalRemoveText (@Nullable final Locale aLocale)
  {
    return EChange.valueOf (m_aTexts.remove (aLocale) != null);
  }

  @Nonnull
  protected final EChange internalClear ()
  {
    if (m_aTexts.isEmpty ())
      return EChange.UNCHANGED;
    m_aTexts.clear ();
    return EChange.CHANGED;
  }

  @Nonnull
  public final TextProvider addTextDE (@Nonnull final String sDE)
  {
    return internalAddText (DE, sDE);
  }

  @Nonnull
  public final TextProvider addTextEN (@Nonnull final String sEN)
  {
    return internalAddText (EN, sEN);
  }

  @Nonnull
  @ReturnsMutableObject (reason = "Internal use only")
  protected final Map <Locale, String> internalGetMap ()
  {
    return m_aTexts;
  }

  @Override
  @Nullable
  protected final Locale internalGetLocaleToUseWithFallback (@Nonnull final Locale aContentLocale)
  {
    return LocaleUtils.getLocaleToUseOrNull (aContentLocale, m_aTexts.keySet ());
  }

  @Override
  @OverrideOnDemand
  @Nullable
  protected String internalGetText (@Nonnull final Locale aContentLocale)
  {
    return m_aTexts.get (aContentLocale);
  }

  @Nonnull
  @ReturnsMutableObject (reason = "Internal use only")
  public final Set <Locale> internalGetAllLocales ()
  {
    return m_aTexts.keySet ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public final Set <Locale> getAllLocales ()
  {
    return ContainerHelper.newSet (m_aTexts.keySet ());
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
      for (final Locale aCurrentLocale : LocaleUtils.getCalculatedLocaleListForResolving (aContentLocale))
        if (containsLocale (aCurrentLocale))
          return true;
    return false;
  }

  @Nonnull
  @ReturnsMutableCopy
  @Deprecated
  public final Map <Locale, String> getMap ()
  {
    return getAllTexts ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public final Map <Locale, String> getAllTexts ()
  {
    return ContainerHelper.newMap (m_aTexts);
  }

  @Nonnegative
  public final int size ()
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
    final TextProvider rhs = (TextProvider) o;
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

  @Nonnull
  public static TextProvider create_DE (@Nonnull final String sDE)
  {
    return new TextProvider ().addTextDE (sDE);
  }

  @Nonnull
  public static TextProvider create_EN (@Nonnull final String sEN)
  {
    return new TextProvider ().addTextEN (sEN);
  }

  @Nonnull
  public static TextProvider create_DE_EN (@Nonnull final String sDE, @Nonnull final String sEN)
  {
    return new TextProvider ().addTextDE (sDE).addTextEN (sEN);
  }
}

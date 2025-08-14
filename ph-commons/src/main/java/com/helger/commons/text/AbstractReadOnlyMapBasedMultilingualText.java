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
package com.helger.commons.text;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.OverridingMethodsMustInvokeSuper;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.debug.GlobalDebug;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.commons.debug.GlobalDebugExt;
import com.helger.commons.locale.LocaleHelper;
import com.helger.commons.regex.RegExHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A {@link Map} based implementation of {@link IMultilingualText} that does not provide writing
 * methods to the outside and is only to be used as a non-abstract base class.
 *
 * @author Philip Helger
 */
public abstract class AbstractReadOnlyMapBasedMultilingualText extends AbstractHasText implements
                                                               IMultilingualText,
                                                               Serializable
{
  private static final Logger LOGGER = LoggerFactory.getLogger (AbstractReadOnlyMapBasedMultilingualText.class);
  private static final AtomicBoolean CONSISTENCY_CHECK_ENABED = new AtomicBoolean (GlobalDebug.isDebugMode ());

  private final ICommonsOrderedMap <Locale, String> m_aTexts;

  protected AbstractReadOnlyMapBasedMultilingualText ()
  {
    // Use a CommonsLinkedHashMap by default
    this (new CommonsLinkedHashMap <> ());
  }

  /**
   * Protected constructor that specifies the underlying {@link Map} to use. Use this constructor to
   * e.g. provide a concurrent {@link HashMap} or similar.
   *
   * @param aMapToUse
   *        The map to use. Must not be <code>null</code> and must be writable.
   */
  protected AbstractReadOnlyMapBasedMultilingualText (@Nonnull final ICommonsOrderedMap <Locale, String> aMapToUse)
  {
    m_aTexts = ValueEnforcer.notNull (aMapToUse, "MapToUse");
  }

  /**
   * Enable or disable the internal consistency checks.
   *
   * @param bPerformConsistencyChecks
   *        <code>true</code> to enable them, <code>false</code> to disable them.
   */
  public static void setPerformConsistencyChecks (final boolean bPerformConsistencyChecks)
  {
    CONSISTENCY_CHECK_ENABED.set (bPerformConsistencyChecks);
  }

  /**
   * @return <code>true</code> if consistency checks are enabled, <code>false</code> if not. The
   *         default value is {@link GlobalDebugExt#isDebugMode()}.
   */
  public static boolean isPerformConsistencyChecks ()
  {
    return CONSISTENCY_CHECK_ENABED.get ();
  }

  private static void _performConsistencyChecks (@Nonnull final String sValue)
  {
    // String contains masked newline? warning only!
    if (sValue.contains ("\\n"))
      LOGGER.warn ("Passed string contains a masked newline - replace with an inline one:\n" + sValue);
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
    if (sValue != null && CONSISTENCY_CHECK_ENABED.get ())
      _performConsistencyChecks (sValue);

    m_aTexts.put (aContentLocale, sValue);
  }

  @Override
  @Nullable
  protected final String internalGetText (@Nonnull final Locale aContentLocale)
  {
    return m_aTexts.get (aContentLocale);
  }

  @Override
  @Nullable
  protected final Locale internalGetLocaleToUseWithFallback (@Nonnull final Locale aContentLocale)
  {
    // Always perform locale fallback resolution
    return LocaleHelper.getLocaleToUseOrNull (aContentLocale, m_aTexts.keySet ());
  }

  public final boolean containsLocaleWithFallback (@Nullable final Locale aContentLocale)
  {
    if (aContentLocale != null)
      for (final Locale aCurrentLocale : LocaleHelper.getCalculatedLocaleListForResolving (aContentLocale))
        if (m_aTexts.containsKey (aCurrentLocale))
          return true;
    return false;
  }

  @Nonnull
  @ReturnsMutableObject
  public final ICommonsOrderedMap <Locale, String> texts ()
  {
    return m_aTexts;
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
    // work around for a comparison issue in Java 24
    final HashCodeGenerator aHCG = new HashCodeGenerator (this);
    for (final var aEntry : m_aTexts.entrySet ())
      aHCG.append (aEntry.getKey ().toString ()).append (aEntry.getValue ());
    return aHCG.getHashCode ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Texts", m_aTexts).getToString ();
  }
}

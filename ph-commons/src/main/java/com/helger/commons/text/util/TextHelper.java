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
package com.helger.commons.text.util;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforcer.ValueEnforcer;
import com.helger.commons.locale.LocaleCache;
import com.helger.commons.text.IMultilingualText;
import com.helger.commons.text.MultilingualText;

import jakarta.annotation.Nonnull;

/**
 * Utility methods for formatting text using {@link MessageFormat}.
 *
 * @author Philip Helger
 */
@Immutable
public final class TextHelper
{
  /** German locale used */
  public static final Locale DE = LocaleCache.getInstance ().getLocale ("de");
  /** English locale used */
  public static final Locale EN = LocaleCache.getInstance ().getLocale ("en");

  @PresentForCodeCoverage
  private static final TextHelper INSTANCE = new TextHelper ();

  private TextHelper ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  public static MultilingualText create_DE (@Nonnull final String sDE)
  {
    final MultilingualText ret = new MultilingualText ();
    ret.addText (DE, sDE);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static MultilingualText create_EN (@Nonnull final String sEN)
  {
    final MultilingualText ret = new MultilingualText ();
    ret.addText (EN, sEN);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static MultilingualText create_DE_EN (@Nonnull final String sDE, @Nonnull final String sEN)
  {
    final MultilingualText ret = new MultilingualText ();
    ret.addText (DE, sDE);
    ret.addText (EN, sEN);
    return ret;
  }

  /**
   * Get a copy of this object with the specified locales. The default locale is copied.
   *
   * @param aMLT
   *        The initial multilingual text. May not be <code>null</code>.
   * @param aContentLocales
   *        The list of locales of which the strings are desired. May not be <code>null</code>.
   * @return The object containing only the texts of the given locales. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static MultilingualText getCopyWithLocales (@Nonnull final IMultilingualText aMLT,
                                                     @Nonnull final Collection <Locale> aContentLocales)
  {
    final MultilingualText ret = new MultilingualText ();
    for (final Locale aConrentLocale : aContentLocales)
      if (aMLT.texts ().containsKey (aConrentLocale))
        ret.setText (aConrentLocale, aMLT.getText (aConrentLocale));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static MultilingualText createMultilingualTextFromMap (@Nonnull final Map <String, String> aMap)
  {
    ValueEnforcer.notNull (aMap, "Map");

    final MultilingualText ret = new MultilingualText ();
    final LocaleCache aLC = LocaleCache.getInstance ();
    for (final Map.Entry <String, String> aEntry : aMap.entrySet ())
    {
      final String sText = aEntry.getValue ();
      if (sText != null)
        ret.setText (aLC.getLocale (aEntry.getKey ()), sText);
    }
    return ret;
  }
}

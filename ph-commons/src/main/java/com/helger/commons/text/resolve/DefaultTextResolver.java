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
package com.helger.commons.text.resolve;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.annotation.DevelopersNote;
import com.helger.commons.annotation.Singleton;
import com.helger.commons.text.IHasText;
import com.helger.commons.text.IHasTextWithArgs;

/**
 * Resolves texts either from a text provider or otherwise uses a fallback to a
 * file, based on the given enum constant.
 *
 * @author Philip Helger
 */
@ThreadSafe
@Singleton
public final class DefaultTextResolver extends EnumTextResolverWithPropertiesOverrideAndFallback
{
  private static final class SingletonHolder
  {
    private static final DefaultTextResolver INSTANCE = new DefaultTextResolver ();
  }

  private static boolean s_bDefaultInstantiated = false;

  private DefaultTextResolver ()
  {}

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  /**
   * @return The singleton instance. Never <code>null</code>.
   */
  @Nonnull
  public static DefaultTextResolver getInstance ()
  {
    final DefaultTextResolver ret = SingletonHolder.INSTANCE;
    s_bDefaultInstantiated = true;
    return ret;
  }

  /**
   * Get text
   *
   * @param aEnum
   *        Enumeration entry. May not be <code>null</code>.
   * @param aTP
   *        Text provider. May not be <code>null</code>.
   * @param aContentLocale
   *        Locale to use. May not be <code>null</code>.
   * @return <code>null</code> if the text is not available in the specific
   *         locale
   */
  @Nullable
  public static String getTextStatic (@Nonnull final Enum <?> aEnum,
                                      @Nonnull final IHasText aTP,
                                      @Nonnull final Locale aContentLocale)
  {
    return getInstance ().getText (aEnum, aTP, aContentLocale);
  }

  /**
   * Get text
   *
   * @param aEnum
   *        Enumeration entry. May not be <code>null</code>.
   * @param aTP
   *        Text provider. May not be <code>null</code>.
   * @param aContentLocale
   *        Locale to use. May not be <code>null</code>.
   * @return <code>null</code> if the text is not available in the specific
   *         locale
   * @deprecated Don't call this; Use the version without "WithArgs" because
   *             there are no args
   */
  @Nullable
  @Deprecated (forRemoval = false)
  @DevelopersNote ("Use getTextStatic instead when no argument is needed!")
  public static String getTextWithArgsStatic (@Nonnull final Enum <?> aEnum,
                                              @Nonnull final IHasTextWithArgs aTP,
                                              @Nonnull final Locale aContentLocale)
  {
    return getTextStatic (aEnum, aTP, aContentLocale);
  }

  /**
   * Get text
   *
   * @param aEnum
   *        Enumeration entry. May not be <code>null</code>.
   * @param aTP
   *        Text provider. May not be <code>null</code>.
   * @param aContentLocale
   *        Locale to use. May not be <code>null</code>.
   * @param aArgs
   *        The arguments to be added for the placeholders. May neither be
   *        <code>null</code> nor empty.
   * @return <code>null</code> if the text is not available in the specific
   *         locale
   */
  @Nullable
  public static String getTextWithArgsStatic (@Nonnull final Enum <?> aEnum,
                                              @Nonnull final IHasTextWithArgs aTP,
                                              @Nonnull final Locale aContentLocale,
                                              @Nullable final Object... aArgs)
  {
    return getInstance ().getTextWithArgs (aEnum, aTP, aContentLocale, aArgs);
  }
}

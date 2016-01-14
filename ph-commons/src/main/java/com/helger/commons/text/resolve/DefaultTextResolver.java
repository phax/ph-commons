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
    static final DefaultTextResolver s_aInstance = new DefaultTextResolver ();
  }

  private static boolean s_bDefaultInstantiated = false;

  private DefaultTextResolver ()
  {}

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  @Nonnull
  public static DefaultTextResolver getInstance ()
  {
    final DefaultTextResolver ret = SingletonHolder.s_aInstance;
    s_bDefaultInstantiated = true;
    return ret;
  }

  @Nullable
  public static String getTextStatic (@Nonnull final Enum <?> aEnum,
                                      @Nonnull final IHasText aTP,
                                      @Nonnull final Locale aContentLocale)
  {
    return getInstance ().getText (aEnum, aTP, aContentLocale);
  }

  @Nullable
  @Deprecated
  @DevelopersNote ("Use getTextStatic instead when no argument is needed!")
  public static String getTextWithArgsStatic (@Nonnull final Enum <?> aEnum,
                                              @Nonnull final IHasTextWithArgs aTP,
                                              @Nonnull final Locale aContentLocale)
  {
    return getInstance ().getTextWithArgs (aEnum, aTP, aContentLocale);
  }

  @Nullable
  public static String getTextWithArgsStatic (@Nonnull final Enum <?> aEnum,
                                              @Nonnull final IHasTextWithArgs aTP,
                                              @Nonnull final Locale aContentLocale,
                                              @Nullable final Object... aArgs)
  {
    return getInstance ().getTextWithArgs (aEnum, aTP, aContentLocale, aArgs);
  }
}

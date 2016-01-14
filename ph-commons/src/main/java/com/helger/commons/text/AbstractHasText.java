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

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;

/**
 * An abstract implementation of the {@link IHasText} that has default
 * implementations for the formatting things.
 *
 * @author Philip Helger
 */
public abstract class AbstractHasText implements IHasText
{
  protected AbstractHasText ()
  {}

  /**
   * Determine the locale to use.
   *
   * @param aContentLocale
   *        Requested locale. Never <code>null</code>.
   * @return The locale to use. May be <code>null</code>.
   */
  @Nullable
  protected abstract Locale internalGetLocaleToUseWithFallback (@Nonnull Locale aContentLocale);

  /**
   * Main text resolving. Get the text in the passed locale.
   *
   * @param aContentLocale
   *        Locale to use. This is the locale resolved internally. Never
   *        <code>null</code>.
   * @return <code>null</code> if no such text present in the passed locale
   */
  @Nullable
  protected abstract String internalGetText (@Nonnull Locale aContentLocale);

  @Nullable
  public final String getText (@Nonnull final Locale aContentLocale)
  {
    ValueEnforcer.notNull (aContentLocale, "ContentLocale");

    final Locale aLocaleToUse = internalGetLocaleToUseWithFallback (aContentLocale);
    return aLocaleToUse == null ? null : internalGetText (aLocaleToUse);
  }
}

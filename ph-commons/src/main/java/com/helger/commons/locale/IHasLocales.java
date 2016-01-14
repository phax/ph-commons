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
package com.helger.commons.locale;

import java.util.Collection;
import java.util.Locale;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;

/**
 * Base interface for objects having zero or more locales.
 *
 * @author Philip Helger
 */
public interface IHasLocales
{
  /**
   * @return The locales of this object. May not be <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  Collection <Locale> getAllLocales ();

  /**
   * @return The number of contained locales. Always &ge; 0.
   */
  @Nonnegative
  int getLocaleCount ();

  /**
   * Check if the passed locale is directly contained.
   *
   * @param aLocale
   *        The locale to check. May be <code>null</code>.
   * @return <code>true</code> if the locale is directly contained,
   *         <code>false</code> if not.
   */
  boolean containsLocale (@Nullable Locale aLocale);

  /**
   * Check if the passed locale is directly or by fallback contained. The
   * implementation class MUST add locale-generalisation when resolving the text
   * ("de_DE" =&gt; "de" =&gt; <i>default</i>).
   *
   * @param aLocale
   *        The locale to check. May be <code>null</code>.
   * @return <code>true</code> if the locale is contained, <code>false</code> if
   *         not.
   */
  boolean containsLocaleWithFallback (@Nullable Locale aLocale);
}

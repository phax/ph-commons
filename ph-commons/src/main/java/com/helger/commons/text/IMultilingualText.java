/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.ICommonsCollection;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.lang.IHasSize;

/**
 * Read-only interface for a multilingual text
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public interface IMultilingualText extends IHasTextWithArgs, IHasSize
{
  /**
   * @return The locales of this object. May not be <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsCollection <Locale> getAllLocales ();

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

  /**
   * @return A map over all contained locale/text pairs. Never <code>null</code>
   *         .
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsMap <Locale, String> getAllTexts ();
}

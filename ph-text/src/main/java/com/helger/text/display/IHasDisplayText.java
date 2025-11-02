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
package com.helger.text.display;

import java.util.Comparator;
import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.text.IHasText;
import com.helger.text.compare.ComparatorHelper;

/**
 * Base interface for objects that have a locale <b>dependent</b> display name.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IHasDisplayText
{
  /**
   * @param aContentLocale
   *        The locale to be used for resolving. May not be <code>null</code>.
   * @return The display text of the object in the given locale. May be <code>null</code> if the
   *         text could not be resolved in the passed locale.
   */
  @Nullable
  String getDisplayText (@NonNull Locale aContentLocale);

  /**
   * @return this as an instance of {@link IHasText}.
   * @since 8.5.2
   */
  @NonNull
  default IHasText getAsHasText ()
  {
    return this::getDisplayText;
  }

  @NonNull
  static Comparator <IHasDisplayText> getComparatorCollating (@NonNull final Locale aContentLocale,
                                                              @Nullable final Locale aSortLocale)
  {
    return ComparatorHelper.getComparatorCollating (x -> x.getDisplayText (aContentLocale), aSortLocale);
  }
}

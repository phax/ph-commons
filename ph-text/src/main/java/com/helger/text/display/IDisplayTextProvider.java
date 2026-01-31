/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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

import com.helger.text.compare.ComparatorHelper;

/**
 * Interface for a handler that provides the locale <b>dependent</b> name of an object.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The data type to retrieve the display text from
 */
@FunctionalInterface
public interface IDisplayTextProvider <DATATYPE>
{
  /**
   * Get the display text of the passed object in the given locale.
   *
   * @param aObject
   *        The object to be used. May be <code>null</code>.
   * @param aContentLocale
   *        The display locale to be used. May not be <code>null</code>.
   * @return The display text of the passed object in the given locale. May be <code>null</code>.
   */
  @Nullable
  String getDisplayText (@Nullable DATATYPE aObject, @NonNull Locale aContentLocale);

  @NonNull
  default Comparator <DATATYPE> getComparatorCollating (@NonNull final Locale aContentLocale,
                                                        @Nullable final Locale aSortLocale)
  {
    return ComparatorHelper.getComparatorCollating (x -> getDisplayText (x, aContentLocale), aSortLocale);
  }

  @NonNull
  static IDisplayTextProvider <IHasDisplayText> createHasDisplayText ()
  {
    return (x, aContentLocale) -> x == null ? null : x.getDisplayText (aContentLocale);
  }
}

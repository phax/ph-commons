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
package com.helger.commons.text.display;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.compare.ISerializableComparator;

/**
 * Interface for a handler that provides the locale <b>dependent</b> name of an
 * object.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The data type to retrieve the display text from
 */
@FunctionalInterface
public interface IDisplayTextProvider <DATATYPE> extends Serializable
{
  /**
   * Get the display text of the passed object in the given locale.
   *
   * @param aObject
   *        The object to be used. May be <code>null</code>.
   * @param aContentLocale
   *        The display locale to be used. May not be <code>null</code>.
   * @return The display text of the passed object in the given locale. May be
   *         <code>null</code>.
   */
  @Nullable
  String getDisplayText (@Nullable DATATYPE aObject, @Nonnull Locale aContentLocale);

  @Nonnull
  static IDisplayTextProvider <IHasDisplayText> createHasDisplayText ()
  {
    return (aObject, aContentLocale) -> aObject == null ? null : aObject.getDisplayText (aContentLocale);
  }

  @Nonnull
  default Comparator <DATATYPE> getComparatorCollating (@Nonnull final Locale aContentLocale,
                                                        @Nullable final Locale aSortLocale)
  {
    return ISerializableComparator.getComparatorCollating (aObject -> getDisplayText (aObject, aContentLocale),
                                                           aSortLocale);
  }
}

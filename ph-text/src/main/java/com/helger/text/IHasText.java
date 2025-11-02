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
package com.helger.text;

import java.util.Comparator;
import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.text.compare.ComparatorHelper;
import com.helger.text.display.IHasDisplayText;

/**
 * Basic interface for object providing multilingual texts without arguments. See also
 * {@link IHasTextWithArgs} for a derived interface with argument handling.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IHasText
{
  /**
   * Get the text specific for the passed locale. The implementation class MAY add
   * locale-generalisation when resolving the text ("de_DE" =&gt; "de" =&gt; <i>default</i>).
   *
   * @param aContentLocale
   *        The locale to use. May not be <code>null</code>.
   * @return <code>null</code> if no text for the given locale was found.
   */
  @Nullable
  String getText (@NonNull Locale aContentLocale);

  /**
   * @return this as an instance of {@link IHasDisplayText}.
   * @since 8.5.2
   */
  @NonNull
  default IHasDisplayText getAsHasDisplayText ()
  {
    return this::getText;
  }

  @NonNull
  static Comparator <IHasText> getComparatorCollating (@NonNull final Locale aContentLocale,
                                                       @Nullable final Locale aSortLocale)
  {
    return ComparatorHelper.getComparatorCollating (x -> x.getText (aContentLocale), aSortLocale);
  }
}

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

import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.compare.IComparator;
import com.helger.commons.text.display.IHasDisplayText;

/**
 * Basic interface for object providing multilingual texts without arguments.
 * See also {@link IHasTextWithArgs} for a derived interface with argument
 * handling.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IHasText extends Serializable
{
  /**
   * Get the text specific for the passed locale. The implementation class MAY
   * add locale-generalisation when resolving the text ("de_DE" =&gt; "de" =&gt;
   * <i>default</i>).
   *
   * @param aContentLocale
   *        The locale to use. May not be <code>null</code>.
   * @return <code>null</code> if no text for the given locale was found.
   */
  @Nullable
  String getText (@Nonnull Locale aContentLocale);

  /**
   * @return this as an instance of {@link IHasDisplayText}.
   * @since 8.5.2
   */
  @Nonnull
  default IHasDisplayText getAsHasDisplayText ()
  {
    return this::getText;
  }

  @Nonnull
  static Comparator <IHasText> getComparatorCollating (@Nonnull final Locale aContentLocale,
                                                       @Nullable final Locale aSortLocale)
  {
    return IComparator.getComparatorCollating (x -> x.getText (aContentLocale), aSortLocale);
  }
}

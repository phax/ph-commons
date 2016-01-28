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
package com.helger.commons.name;

import java.util.Comparator;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.compare.ISerializableComparator;

/**
 * Base interface for all objects that have a mandatory <b>INTERNAL</b> name
 * that is not multilingual but should be human interpretable. If you need to
 * have a name that is displayed on the screen, please use the
 * {@link IHasDisplayName} interface.
 *
 * @author Philip Helger
 */
@FunctionalInterface
public interface IHasName
{
  /**
   * @return The name of the object. May not be <code>null</code>.
   */
  @Nonnull
  String getName ();

  @Nonnull
  static Comparator <IHasName> getComparatorName ()
  {
    return Comparator.comparing (IHasName::getName);
  }

  @Nonnull
  static Comparator <IHasName> getComparatorCollating (@Nullable final Locale aSortLocale)
  {
    return ISerializableComparator.getComparatorCollating (IHasName::getName, aSortLocale);
  }
}

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

import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.compare.ISerializableComparator;

/**
 * Interface for a handler the provides the locale <b>independent</b> name of an
 * object.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The data type to retrieve the display name from
 */
@FunctionalInterface
public interface IDisplayNameProvider <DATATYPE> extends Serializable
{
  /**
   * Get the name of the passed object.
   *
   * @param aObject
   *        The object who's name is to be retrieved.
   * @return The name of the object. May be <code>null</code>.
   */
  @Nullable
  String getDisplayName (@Nullable DATATYPE aObject);

  @Nonnull
  static IDisplayNameProvider <IHasDisplayName> createHasDisplayName ()
  {
    return aObject -> aObject == null ? null : aObject.getDisplayName ();
  }

  @Nonnull
  default Comparator <DATATYPE> getComparatorCollating (@Nullable final Locale aSortLocale)
  {
    return ISerializableComparator.getComparatorCollating (aObject -> getDisplayName (aObject), aSortLocale);
  }
}

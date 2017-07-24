/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.settings;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.ICommonsMap;
import com.helger.commons.collection.ext.ICommonsSet;
import com.helger.commons.lang.IHasSize;
import com.helger.commons.name.IHasName;
import com.helger.commons.traits.IGetterByKeyTrait;

/**
 * Read-only settings base interface
 *
 * @author philip
 */
public interface ISettings extends IHasName, IHasSize, IGetterByKeyTrait <String>
{
  /**
   * @return A non-<code>null</code> set of all available field names in any
   *         order.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsSet <String> getAllFieldNames ();

  /**
   * @return A non-<code>null</code> map with all key-value-pairs.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsMap <String, Object> getAllEntries ();

  /**
   * Invoke the provided consumer on each entry (pair of key and value).
   *
   * @param aConsumer
   *        The consumer to be invoked. May not be <code>null</code>.
   * @see #forEach(BiPredicate,BiConsumer)
   */
  default void forEach (@Nonnull final BiConsumer <? super String, ? super Object> aConsumer)
  {
    getAllEntries ().forEach (aConsumer);
  }

  /**
   * Invoke the provided consumer on each entry (pair of key and value) that
   * matches the provided filter.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @param aConsumer
   *        The consumer to be invoked. May not be <code>null</code>.
   * @see #forEach(BiConsumer)
   */
  default void forEach (@Nullable final BiPredicate <? super String, ? super Object> aFilter,
                        @Nonnull final BiConsumer <? super String, ? super Object> aConsumer)
  {
    getAllEntries ().forEach (aFilter, aConsumer);
  }

  /**
   * Check if a value is present for the given field name.
   *
   * @param sFieldName
   *        The field name to check.
   * @return <code>true</code> if any value (even <code>null</code>) is set,
   *         <code>false</code> otherwise.
   */
  boolean containsField (@Nullable String sFieldName);

  /**
   * Get the value associated with the passed field name as an untyped object.
   *
   * @param sFieldName
   *        The field name to be queried. May be <code>null</code> resulting in
   *        a <code>null</code> return value
   * @return <code>null</code> if no such field exists
   */
  @Nullable
  Object getValue (@Nullable String sFieldName);

  /**
   * Get a nested settings value. This is like retrieving a map.
   *
   * @param sFieldName
   *        The field to retrieve. May be <code>null</code> resulting in a
   *        <code>null</code> return value.
   * @return <code>null</code> if no such field is available.
   */
  @Nullable
  ISettings getSettingsValue (@Nullable String sFieldName);
}

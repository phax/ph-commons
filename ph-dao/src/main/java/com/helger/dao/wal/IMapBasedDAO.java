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
package com.helger.dao.wal;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.IHasSize;

/**
 * Read-only base interface for the MAP based DAO
 *
 * @author Philip Helger
 * @param <INTERFACETYPE>
 *        The interface type of the elements handled by the DAO
 */
@ThreadSafe
public interface IMapBasedDAO <INTERFACETYPE extends IHasID <String>> extends IHasSize
{
  /**
   * @return A list of all contained items. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsList <? extends INTERFACETYPE> getAll ();

  /**
   * @param aFilter
   *        Filter to be applied. May be <code>null</code>.
   * @return A list of all contained items matching the filter. Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsList <? extends INTERFACETYPE> getAll (@Nullable Predicate <? super INTERFACETYPE> aFilter);

  /**
   * Find all items matching the filter and invoke the consumer on all matching
   * entries.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @param aConsumer
   *        The consumer to be invoked for all matches. May not be
   *        <code>null</code>.
   */
  void findAll (@Nullable Predicate <? super INTERFACETYPE> aFilter,
                @Nonnull Consumer <? super INTERFACETYPE> aConsumer);

  /**
   * Get all contained items matching the provided filter and map them to
   * something else.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @param aMapper
   *        The mapper to be invoked. May not be <code>null</code>.
   * @return The list all matching and mapped elements. Never <code>null</code>.
   * @param <RETTYPE>
   *        Return type to which is mapped
   */
  @Nonnull
  @ReturnsMutableCopy
  <RETTYPE> ICommonsList <RETTYPE> getAllMapped (@Nullable Predicate <? super INTERFACETYPE> aFilter,
                                                 @Nonnull Function <? super INTERFACETYPE, ? extends RETTYPE> aMapper);

  /**
   * Find all contained items that match the filter, map them to a different
   * type and invoke the consumer on all mapped items.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @param aMapper
   *        The mapper to be invoked. May not be <code>null</code>.
   * @param <RETTYPE>
   *        Return type to which is mapped
   */
  <RETTYPE> void findAllMapped (@Nullable Predicate <? super INTERFACETYPE> aFilter,
                                @Nonnull Function <? super INTERFACETYPE, ? extends RETTYPE> aMapper,
                                @Nonnull Consumer <? super RETTYPE> aConsumer);

  /**
   * Find the the first element that matches the filter.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return The first matching item or <code>null</code>.
   */
  @Nullable
  INTERFACETYPE findFirst (@Nullable Predicate <? super INTERFACETYPE> aFilter);

  @Nullable
  <RETTYPE> RETTYPE findFirstMapped (@Nullable Predicate <? super INTERFACETYPE> aFilter,
                                     @Nonnull Function <? super INTERFACETYPE, ? extends RETTYPE> aMapper);

  /**
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return <code>true</code> if at least one item matches the filter
   */
  boolean containsAny (@Nullable Predicate <? super INTERFACETYPE> aFilter);

  /**
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return <code>true</code> if no item matches the filter
   */
  boolean containsNone (@Nullable Predicate <? super INTERFACETYPE> aFilter);

  /**
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return <code>true</code> if all items match the filter
   */
  boolean containsOnly (@Nullable Predicate <? super INTERFACETYPE> aFilter);

  /**
   * @param sID
   *        The ID to be checked. May be <code>null</code>.
   * @return <code>true</code> if an item with the provided ID is contained.
   */
  boolean containsWithID (@Nullable String sID);

  /**
   * @return A set with the IDs of all contained items. Never <code>null</code>
   *         but maybe empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsSet <String> getAllIDs ();

  /**
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return The number of items matching the provided filter. Always &ge; 0.
   */
  @Nonnegative
  int getCount (@Nullable Predicate <? super INTERFACETYPE> aFilter);
}

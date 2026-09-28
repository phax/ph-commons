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
package com.helger.json;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.iface.IHasSize;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

/**
 * Base interface for all JSON arrays and objects. So the base interface for JSON objects having
 * other JSON child objects.
 *
 * @author Philip Helger
 */
public interface IJsonCollection extends IJson, IHasSize
{
  /**
   * Invoke the provided consumer on all contained JSON children. For an {@link IJsonArray} these
   * are all contained elements, for an {@link IJsonObject} these are all contained values. No
   * intermediate copy of the children is created.
   *
   * @param aConsumer
   *        The consumer to be invoked for each child. May not be <code>null</code>.
   * @since 12.5.1
   */
  void forEachJson (@NonNull Consumer <? super IJson> aConsumer);

  /**
   * Invoke the provided consumer on all contained JSON children, after mapping each of them with
   * the provided function.
   *
   * @param aMapper
   *        The mapping function to be executed for each child. May not be <code>null</code>.
   * @param aConsumer
   *        The consumer to be invoked for each mapped child. May not be <code>null</code>.
   * @param <DSTTYPE>
   *        The destination type to be mapped to
   * @see #forEachJson(Consumer)
   * @see #forEachMapped(Predicate, Function, Consumer)
   * @since 12.5.1
   */
  default <DSTTYPE> void forEachMapped (@NonNull final Function <? super IJson, DSTTYPE> aMapper,
                                        @NonNull final Consumer <? super DSTTYPE> aConsumer)
  {
    ValueEnforcer.notNull (aMapper, "Mapper");
    ValueEnforcer.notNull (aConsumer, "Consumer");

    forEachJson (aJson -> aConsumer.accept (aMapper.apply (aJson)));
  }

  /**
   * Invoke the provided consumer on all contained JSON children matching the provided filter, after
   * mapping each of them with the provided function.
   *
   * @param aFilter
   *        The filter to be applied on each child. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for each matching child. May not be
   *        <code>null</code>.
   * @param aConsumer
   *        The consumer to be invoked for each matching mapped child. May not be <code>null</code>.
   * @param <DSTTYPE>
   *        The destination type to be mapped to
   * @see #forEachJson(Consumer)
   * @see #forEachMapped(Function, Consumer)
   * @since 12.5.1
   */
  default <DSTTYPE> void forEachMapped (@Nullable final Predicate <? super IJson> aFilter,
                                        @NonNull final Function <? super IJson, DSTTYPE> aMapper,
                                        @NonNull final Consumer <? super DSTTYPE> aConsumer)
  {
    if (aFilter == null)
      forEachMapped (aMapper, aConsumer);
    else
    {
      ValueEnforcer.notNull (aMapper, "Mapper");
      ValueEnforcer.notNull (aConsumer, "Consumer");

      forEachJson (aJson -> {
        if (aFilter.test (aJson))
          aConsumer.accept (aMapper.apply (aJson));
      });
    }
  }

  /**
   * Create a new list where all contained JSON children are mapped with the provided function. For
   * an {@link IJsonArray} these are all contained elements, for an {@link IJsonObject} these are
   * all contained values. Compared to mapping the result of <code>IJsonArray.getAll ()</code> or
   * <code>IJsonObject.values ()</code> no intermediate copy of the children is created.
   *
   * @param aMapper
   *        The mapping function to be executed for each child. May not be <code>null</code>.
   * @return A new non-<code>null</code> list with all mapped children.
   * @param <DSTTYPE>
   *        The destination type to be mapped to
   * @see #forEachMapped(Function, Consumer)
   * @see #getAllMapped(Predicate, Function)
   * @since 12.5.1
   */
  @NonNull
  @ReturnsMutableCopy
  default <DSTTYPE> ICommonsList <DSTTYPE> getAllMapped (@NonNull final Function <? super IJson, DSTTYPE> aMapper)
  {
    final ICommonsList <DSTTYPE> ret = new CommonsArrayList <> (size ());
    forEachMapped (aMapper, ret::add);
    return ret;
  }

  /**
   * Create a new list where all contained JSON children matching the provided filter are mapped
   * with the provided function. For an {@link IJsonArray} these are all contained elements, for an
   * {@link IJsonObject} these are all contained values. Compared to mapping the result of
   * <code>IJsonArray.getAll ()</code> or <code>IJsonObject.values ()</code> no intermediate copy of
   * the children is created.
   *
   * @param aFilter
   *        The filter to be applied on each child. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for each matching child. May not be
   *        <code>null</code>.
   * @return A new non-<code>null</code> list with all mapped children. If no filter is provided the
   *         result is the same as of {@link #getAllMapped(Function)}.
   * @param <DSTTYPE>
   *        The destination type to be mapped to
   * @see #forEachMapped(Predicate, Function, Consumer)
   * @see #getAllMapped(Function)
   * @since 12.5.1
   */
  @NonNull
  @ReturnsMutableCopy
  default <DSTTYPE> ICommonsList <DSTTYPE> getAllMapped (@Nullable final Predicate <? super IJson> aFilter,
                                                         @NonNull final Function <? super IJson, DSTTYPE> aMapper)
  {
    final ICommonsList <DSTTYPE> ret = new CommonsArrayList <> (size ());
    forEachMapped (aFilter, aMapper, ret::add);
    return ret;
  }
}

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
package com.helger.commons.aggregate;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.functional.IFunction;
import com.helger.commons.string.StringHelper;

/**
 * Aggregate a list of input objects to a single output object (change n to 1).
 *
 * @author Philip Helger
 * @param <SRCTYPE>
 *        The input type.
 * @param <DSTTYPE>
 *        The output type.
 */
@FunctionalInterface
public interface IAggregator <SRCTYPE, DSTTYPE> extends IFunction <Collection <SRCTYPE>, DSTTYPE>
{
  /**
   * Aggregate a array of input objects to a single output object.
   *
   * @param aObjects
   *        Source object array. May not be <code>null</code>.
   * @return The aggregated object. May be <code>null</code>.
   */
  @Nullable
  @SuppressWarnings ("unchecked")
  // @SafeVarArgs does not work for default methods
  default DSTTYPE apply (@Nullable final SRCTYPE... aObjects)
  {
    return apply (new CommonsArrayList<> (aObjects));
  }

  /**
   * @return A new aggregator that uses the first element from the provided
   *         collection. Never <code>null</code>.
   */
  @Nonnull
  static <SRCTYPE> IAggregator <SRCTYPE, SRCTYPE> createUseFirst ()
  {
    return aCollection -> CollectionHelper.getFirstElement (aCollection);
  }

  /**
   * @return A new aggregator that uses the last element from the provided
   *         collection. Never <code>null</code>.
   */
  @Nonnull
  static <SRCTYPE> IAggregator <SRCTYPE, SRCTYPE> createUseLast ()
  {
    return aCollection -> CollectionHelper.getLastElement (aCollection);
  }

  /**
   * @return A new aggregator that combines all input strings into a single
   *         result string without any separator char. Never <code>null</code>.
   */
  @Nonnull
  static IAggregator <String, String> createStringAll ()
  {
    return aCollection -> StringHelper.getImploded (aCollection);
  }

  /**
   * @param cSep
   *        the separator char to be used
   * @return A new aggregator that combines all input strings into a single
   *         result string using the provided separator char. Never
   *         <code>null</code>.
   */
  @Nonnull
  static IAggregator <String, String> createStringAll (final char cSep)
  {
    return aCollection -> StringHelper.getImploded (cSep, aCollection);
  }

  /**
   * @param sSep
   *        the separator string to be used. May not be <code>null</code>.
   * @return A new aggregator that combines all input strings into a single
   *         result string using the provided separator string. Never
   *         <code>null</code>.
   */
  @Nonnull
  static IAggregator <String, String> createStringAll (@Nonnull final String sSep)
  {
    ValueEnforcer.notNull (sSep, "Separator");
    return aCollection -> StringHelper.getImploded (sSep, aCollection);
  }

  /**
   * @return A new aggregator that combines all non-empty input strings into a
   *         single result string without any separator char. Never
   *         <code>null</code>.
   */
  @Nonnull
  static IAggregator <String, String> createStringIgnoreEmpty ()
  {
    return aCollection -> StringHelper.getImplodedNonEmpty (aCollection);
  }

  /**
   * @param cSep
   *        the separator char to be used
   * @return A new aggregator that combines all non-empty input strings into a
   *         single result string using the provided separator char. Never
   *         <code>null</code>.
   */
  @Nonnull
  static IAggregator <String, String> createStringIgnoreEmpty (final char cSep)
  {
    return aCollection -> StringHelper.getImplodedNonEmpty (cSep, aCollection);
  }

  /**
   * @param sSep
   *        the separator string to be used. May not be <code>null</code>.
   * @return A new aggregator that combines all non-empty input strings into a
   *         single result string using the provided separator string. Never
   *         <code>null</code>.
   */
  @Nonnull
  static IAggregator <String, String> createStringIgnoreEmpty (@Nonnull final String sSep)
  {
    ValueEnforcer.notNull (sSep, "Separator");
    return aCollection -> StringHelper.getImplodedNonEmpty (sSep, aCollection);
  }
}

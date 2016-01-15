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
package com.helger.commons.aggregate;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collection.CollectionHelper;

/**
 * Aggregate a list of input objects to an output object.
 *
 * @author Philip Helger
 * @param <SRCTYPE>
 *        The input type.
 * @param <DSTTYPE>
 *        The output type.
 */
@FunctionalInterface
public interface IAggregator <SRCTYPE, DSTTYPE>
{
  /**
   * Aggregate a collection of input objects to a single output object.
   *
   * @param aObjects
   *        Source objects. May not be <code>null</code>.
   * @return The aggregated object. May be <code>null</code>.
   */
  @Nullable
  DSTTYPE aggregate (@Nullable Collection <SRCTYPE> aObjects);

  /**
   * Aggregate a collection of input objects to a single output object.
   *
   * @param aObjects
   *        Source objects. May not be <code>null</code>.
   * @return The aggregated object. May be <code>null</code>.
   */
  @SuppressWarnings ("unchecked")
  @Nullable
  default DSTTYPE aggregate (@Nonnull final SRCTYPE... aObjects)
  {
    return aggregate (CollectionHelper.newList (aObjects));
  }

  @Nonnull
  static <SRCTYPE, DSTTYPE> IAggregator <SRCTYPE, DSTTYPE> createNull ()
  {
    return x -> null;
  }

  @Nonnull
  static <SRCTYPE, DSTTYPE> IAggregator <SRCTYPE, DSTTYPE> createConstant (@Nullable final DSTTYPE aValue)
  {
    return x -> aValue;
  }

  @Nonnull
  static <SRCTYPE> IAggregator <SRCTYPE, Collection <SRCTYPE>> createUseAll ()
  {
    return x -> x;
  }

  @Nonnull
  static <SRCTYPE> IAggregator <SRCTYPE, SRCTYPE> createUseFirst ()
  {
    return x -> CollectionHelper.getFirstElement (x);
  }

  @Nonnull
  static <SRCTYPE> IAggregator <SRCTYPE, SRCTYPE> createUseLast ()
  {
    return x -> CollectionHelper.getLastElement (x);
  }
}

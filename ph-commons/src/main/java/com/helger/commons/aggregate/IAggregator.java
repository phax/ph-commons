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
 * Aggregate a list of input objects to an output object.
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
   * Aggregate a collection of input objects to a single output object.
   *
   * @param aObjects
   *        Source objects. May not be <code>null</code>.
   * @return The aggregated object. May be <code>null</code>.
   */
  @Nullable
  @SuppressWarnings ("unchecked")
  default DSTTYPE apply (@Nonnull final SRCTYPE... aObjects)
  {
    return apply (new CommonsArrayList <> (aObjects));
  }

  @Nonnull
  static <SRCTYPE, DSTTYPE> IAggregator <SRCTYPE, DSTTYPE> createNull ()
  {
    return aCollection -> null;
  }

  @Nonnull
  static <SRCTYPE, DSTTYPE> IAggregator <SRCTYPE, DSTTYPE> createConstant (@Nullable final DSTTYPE aValue)
  {
    return aCollection -> aValue;
  }

  @Nonnull
  static <SRCTYPE> IAggregator <SRCTYPE, Collection <SRCTYPE>> createUseAll ()
  {
    return aCollection -> aCollection;
  }

  @Nonnull
  static <SRCTYPE> IAggregator <SRCTYPE, SRCTYPE> createUseFirst ()
  {
    return aCollection -> CollectionHelper.getFirstElement (aCollection);
  }

  @Nonnull
  static <SRCTYPE> IAggregator <SRCTYPE, SRCTYPE> createUseLast ()
  {
    return aCollection -> CollectionHelper.getLastElement (aCollection);
  }

  @Nonnull
  static IAggregator <String, String> createStringAll ()
  {
    return aCollection -> StringHelper.getImploded (aCollection);
  }

  @Nonnull
  static IAggregator <String, String> createStringAll (final char cSep)
  {
    return aCollection -> StringHelper.getImploded (cSep, aCollection);
  }

  @Nonnull
  static IAggregator <String, String> createStringAll (@Nonnull final String sSep)
  {
    ValueEnforcer.notNull (sSep, "Separator");
    return aCollection -> StringHelper.getImploded (sSep, aCollection);
  }

  @Nonnull
  static IAggregator <String, String> createStringIgnoreEmpty ()
  {
    return aCollection -> StringHelper.getImplodedNonEmpty (aCollection);
  }

  @Nonnull
  static IAggregator <String, String> createStringIgnoreEmpty (final char cSep)
  {
    return aCollection -> StringHelper.getImplodedNonEmpty (cSep, aCollection);
  }

  @Nonnull
  static IAggregator <String, String> createStringIgnoreEmpty (@Nonnull final String sSep)
  {
    ValueEnforcer.notNull (sSep, "Separator");
    return aCollection -> StringHelper.getImplodedNonEmpty (sSep, aCollection);
  }
}

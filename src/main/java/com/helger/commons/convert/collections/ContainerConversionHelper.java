/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.convert.collections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotations.PresentForCodeCoverage;
import com.helger.commons.annotations.ReturnsImmutableObject;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.convert.IUnidirectionalConverter;
import com.helger.commons.filter.IFilter;

/**
 * This utility class helps applying conversions onto collections.
 *
 * @author Philip Helger
 * @deprecated Use the class from the 'com.helger.commons.collections.convert'
 *             package.
 */
@Immutable
@Deprecated
public final class ContainerConversionHelper
{
  @PresentForCodeCoverage
  private static final ContainerConversionHelper s_aInstance = new ContainerConversionHelper ();

  private ContainerConversionHelper ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newSet (@Nonnull final Iterator <? extends SRCTYPE> it,
                                                         @Nonnull final IUnidirectionalConverter <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return com.helger.commons.collections.convert.ContainerConversionHelper.newSet (it, aConverter);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newSet (@Nonnull final Iterable <? extends SRCTYPE> aCont,
                                                         @Nonnull final IUnidirectionalConverter <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return com.helger.commons.collections.convert.ContainerConversionHelper.newSet (aCont, aConverter);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newSet (@Nonnull final Iterable <? extends SRCTYPE> aCont,
                                                         @Nonnull final IFilter <? super SRCTYPE> aFilter,
                                                         @Nonnull final IUnidirectionalConverter <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return com.helger.commons.collections.convert.ContainerConversionHelper.newSet (aCont, aFilter, aConverter);
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newUnmodifiableSet (@Nonnull final Iterator <? extends SRCTYPE> it,
                                                                     @Nonnull final IUnidirectionalConverter <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return ContainerHelper.makeUnmodifiable (newSet (it, aConverter));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newUnmodifiableSet (@Nonnull final Iterable <? extends SRCTYPE> aCont,
                                                                     @Nonnull final IUnidirectionalConverter <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return ContainerHelper.makeUnmodifiable (newSet (aCont, aConverter));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newUnmodifiableSet (@Nonnull final Iterable <? extends SRCTYPE> aCont,
                                                                     @Nonnull final IFilter <? super SRCTYPE> aFilter,
                                                                     @Nonnull final IUnidirectionalConverter <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return ContainerHelper.makeUnmodifiable (ContainerConversionHelper.<SRCTYPE, DSTTYPE> newSet (aCont,
                                                                                                  aFilter,
                                                                                                  aConverter));
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newOrderedSet (@Nonnull final Iterator <? extends SRCTYPE> it,
                                                                @Nonnull final IUnidirectionalConverter <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    final Set <DSTTYPE> ret = new LinkedHashSet <DSTTYPE> ();
    while (it.hasNext ())
      ret.add (aConverter.convert (it.next ()));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newOrderedSet (@Nonnull final Iterable <? extends SRCTYPE> aCont,
                                                                @Nonnull final IUnidirectionalConverter <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return com.helger.commons.collections.convert.ContainerConversionHelper.newOrderedSet (aCont, aConverter);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newOrderedSet (@Nonnull final Iterable <? extends SRCTYPE> aCont,
                                                                @Nonnull final IFilter <? super SRCTYPE> aFilter,
                                                                @Nonnull final IUnidirectionalConverter <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return com.helger.commons.collections.convert.ContainerConversionHelper.newOrderedSet (aCont, aFilter, aConverter);
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newUnmodifiableOrderedSet (@Nonnull final Iterator <? extends SRCTYPE> it,
                                                                            @Nonnull final IUnidirectionalConverter <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return ContainerHelper.makeUnmodifiable (newOrderedSet (it, aConverter));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newUnmodifiableOrderedSet (@Nonnull final Iterable <? extends SRCTYPE> aCont,
                                                                            @Nonnull final IUnidirectionalConverter <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return ContainerHelper.makeUnmodifiable (newOrderedSet (aCont, aConverter));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newUnmodifiableOrderedSet (@Nonnull final Iterable <? extends SRCTYPE> aCont,
                                                                            @Nonnull final IFilter <? super SRCTYPE> aFilter,
                                                                            @Nonnull final IUnidirectionalConverter <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return ContainerHelper.makeUnmodifiable (ContainerConversionHelper.<SRCTYPE, DSTTYPE> newOrderedSet (aCont,
                                                                                                         aFilter,
                                                                                                         aConverter));
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> List <DSTTYPE> newList (@Nullable final Iterable <? extends SRCTYPE> aCont,
                                                           @Nonnull final IUnidirectionalConverter <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return com.helger.commons.collections.convert.ContainerConversionHelper.newList (aCont, aConverter);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> List <DSTTYPE> newList (@Nullable final SRCTYPE [] aCont,
                                                           @Nonnull final IUnidirectionalConverter <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return com.helger.commons.collections.convert.ContainerConversionHelper.newList (aCont, aConverter);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> List <DSTTYPE> newList (@Nullable final Iterable <? extends SRCTYPE> aCont,
                                                           @Nonnull final IFilter <? super SRCTYPE> aFilter,
                                                           @Nonnull final IUnidirectionalConverter <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return com.helger.commons.collections.convert.ContainerConversionHelper.newList (aCont, aFilter, aConverter);
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <SRCTYPE, DSTTYPE> List <DSTTYPE> newUnmodifiableList (@Nullable final Iterable <? extends SRCTYPE> aCont,
                                                                       @Nonnull final IUnidirectionalConverter <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return ContainerHelper.makeUnmodifiable (newList (aCont, aConverter));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <SRCTYPE, DSTTYPE> List <DSTTYPE> newUnmodifiableList (@Nullable final SRCTYPE [] aCont,
                                                                       @Nonnull final IUnidirectionalConverter <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return ContainerHelper.makeUnmodifiable (newList (aCont, aConverter));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <SRCTYPE, DSTTYPE> List <DSTTYPE> newUnmodifiableList (@Nullable final Iterable <? extends SRCTYPE> aCont,
                                                                       @Nonnull final IFilter <? super SRCTYPE> aFilter,
                                                                       @Nonnull final IUnidirectionalConverter <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return ContainerHelper.makeUnmodifiable (ContainerConversionHelper.<SRCTYPE, DSTTYPE> newList (aCont,
                                                                                                   aFilter,
                                                                                                   aConverter));
  }

  /**
   * Convert the given iterator to a sorted list.
   *
   * @param <SRCTYPE>
   *        The type of elements to iterate (source).
   * @param <DSTTYPE>
   *        The type of elements to return (destination).
   * @param it
   *        Input iterator. May not be <code>null</code>.
   * @param aConverter
   *        The converter to be used. May not be <code>null</code>.
   * @return a non-null {@link ArrayList} based on the results of
   *         {@link ContainerHelper#getSortedInline(List)}.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE extends Comparable <? super DSTTYPE>> List <DSTTYPE> getSorted (@Nonnull final Iterator <? extends SRCTYPE> it,
                                                                                                  @Nonnull final IUnidirectionalConverter <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return com.helger.commons.collections.convert.ContainerConversionHelper.getSorted (it, aConverter);
  }

  /**
   * Convert the given iterator to a sorted list.
   *
   * @param <SRCTYPE>
   *        The type of elements to iterate (source).
   * @param <DSTTYPE>
   *        The type of elements to return (destination).
   * @param it
   *        Input iterator. May not be <code>null</code>.
   * @param aConverter
   *        The converter to be used. May not be <code>null</code>.
   * @param aComparator
   *        The comparator to use. May not be <code>null</code>.
   * @return a non-null {@link ArrayList} based on the results of
   *         {@link ContainerHelper#getSortedInline(List, Comparator)}.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> List <DSTTYPE> getSorted (@Nonnull final Iterator <? extends SRCTYPE> it,
                                                             @Nonnull final IUnidirectionalConverter <? super SRCTYPE, ? extends DSTTYPE> aConverter,
                                                             @Nonnull final Comparator <? super DSTTYPE> aComparator)
  {
    return com.helger.commons.collections.convert.ContainerConversionHelper.getSorted (it, aConverter, aComparator);
  }

  /**
   * Convert the given iterator to a sorted list.
   *
   * @param <SRCTYPE>
   *        The type of elements to iterate (source).
   * @param <DSTTYPE>
   *        The type of elements to return (destination).
   * @param aCont
   *        Input container. May not be <code>null</code>.
   * @param aConverter
   *        The converter to be used. May not be <code>null</code>.
   * @return a non-null {@link ArrayList} based on the results of
   *         {@link ContainerHelper#getSortedInline(List)}.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE extends Comparable <? super DSTTYPE>> List <DSTTYPE> getSorted (@Nonnull final Iterable <? extends SRCTYPE> aCont,
                                                                                                  @Nonnull final IUnidirectionalConverter <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return com.helger.commons.collections.convert.ContainerConversionHelper.getSorted (aCont, aConverter);
  }

  /**
   * Convert the given iterator to a sorted list.
   *
   * @param <SRCTYPE>
   *        The type of elements to iterate (source).
   * @param <DSTTYPE>
   *        The type of elements to return (destination).
   * @param aCont
   *        Input iterator. May not be <code>null</code>.
   * @param aConverter
   *        The converter to be used. May not be <code>null</code>.
   * @param aComparator
   *        The comparator to use. May not be <code>null</code>.
   * @return a non-null {@link ArrayList} based on the results of
   *         {@link ContainerHelper#getSortedInline(List, Comparator)}.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> List <DSTTYPE> getSorted (@Nonnull final Iterable <? extends SRCTYPE> aCont,
                                                             @Nonnull final IUnidirectionalConverter <? super SRCTYPE, ? extends DSTTYPE> aConverter,
                                                             @Nonnull final Comparator <? super DSTTYPE> aComparator)
  {
    return com.helger.commons.collections.convert.ContainerConversionHelper.getSorted (aCont, aConverter, aComparator);
  }

  @Nonnull
  public static <SRCTYPE, DSTTYPE> Iterator <DSTTYPE> getIterator (@Nonnull final Iterable <SRCTYPE> aCont,
                                                                   @Nonnull final IUnidirectionalConverter <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return com.helger.commons.collections.convert.ContainerConversionHelper.getIterator (aCont, aConverter);
  }
}

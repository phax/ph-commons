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
package com.helger.commons.collection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsImmutableObject;
import com.helger.commons.annotation.ReturnsMutableCopy;

/**
 * This utility class helps applying conversions onto collections.
 *
 * @author Philip Helger
 */
@Immutable
public final class ContainerConversionHelper
{
  @PresentForCodeCoverage
  private static final ContainerConversionHelper s_aInstance = new ContainerConversionHelper ();

  private ContainerConversionHelper ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newSet (@Nonnull final Iterator <? extends SRCTYPE> it,
                                                         @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    final Set <DSTTYPE> ret = new HashSet <DSTTYPE> ();
    while (it.hasNext ())
      ret.add (aConverter.apply (it.next ()));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newSet (@Nonnull final Iterable <? extends SRCTYPE> aCont,
                                                         @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    final Set <DSTTYPE> ret = new HashSet <DSTTYPE> ();
    for (final SRCTYPE aValue : aCont)
      ret.add (aConverter.apply (aValue));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newSet (@Nonnull final Iterable <? extends SRCTYPE> aCont,
                                                         @Nonnull final Predicate <? super SRCTYPE> aFilter,
                                                         @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    final Set <DSTTYPE> ret = new HashSet <DSTTYPE> ();
    for (final SRCTYPE aIn : aCont)
      if (aFilter.test (aIn))
        ret.add (aConverter.apply (aIn));
    return ret;
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newUnmodifiableSet (@Nonnull final Iterator <? extends SRCTYPE> it,
                                                                     @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return CollectionHelper.makeUnmodifiable (newSet (it, aConverter));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newUnmodifiableSet (@Nonnull final Iterable <? extends SRCTYPE> aCont,
                                                                     @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return CollectionHelper.makeUnmodifiable (newSet (aCont, aConverter));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newUnmodifiableSet (@Nonnull final Iterable <? extends SRCTYPE> aCont,
                                                                     @Nonnull final Predicate <? super SRCTYPE> aFilter,
                                                                     @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return CollectionHelper.makeUnmodifiable (ContainerConversionHelper.<SRCTYPE, DSTTYPE> newSet (aCont,
                                                                                                   aFilter,
                                                                                                   aConverter));
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newOrderedSet (@Nonnull final Iterator <? extends SRCTYPE> it,
                                                                @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    final Set <DSTTYPE> ret = new LinkedHashSet <DSTTYPE> ();
    while (it.hasNext ())
      ret.add (aConverter.apply (it.next ()));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newOrderedSet (@Nonnull final Iterable <? extends SRCTYPE> aCont,
                                                                @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    final Set <DSTTYPE> ret = new LinkedHashSet <DSTTYPE> ();
    for (final SRCTYPE aValue : aCont)
      ret.add (aConverter.apply (aValue));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newOrderedSet (@Nonnull final Iterable <? extends SRCTYPE> aCont,
                                                                @Nonnull final Predicate <? super SRCTYPE> aFilter,
                                                                @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    final Set <DSTTYPE> ret = new LinkedHashSet <DSTTYPE> ();
    for (final SRCTYPE aIn : aCont)
      if (aFilter.test (aIn))
        ret.add (aConverter.apply (aIn));
    return ret;
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newUnmodifiableOrderedSet (@Nonnull final Iterator <? extends SRCTYPE> it,
                                                                            @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return CollectionHelper.makeUnmodifiable (newOrderedSet (it, aConverter));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newUnmodifiableOrderedSet (@Nonnull final Iterable <? extends SRCTYPE> aCont,
                                                                            @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return CollectionHelper.makeUnmodifiable (newOrderedSet (aCont, aConverter));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newUnmodifiableOrderedSet (@Nonnull final Iterable <? extends SRCTYPE> aCont,
                                                                            @Nonnull final Predicate <? super SRCTYPE> aFilter,
                                                                            @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return CollectionHelper.makeUnmodifiable (ContainerConversionHelper.<SRCTYPE, DSTTYPE> newOrderedSet (aCont,
                                                                                                          aFilter,
                                                                                                          aConverter));
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> List <DSTTYPE> newList (@Nullable final Iterable <? extends SRCTYPE> aCont,
                                                           @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    final List <DSTTYPE> ret = new ArrayList <DSTTYPE> ();
    if (aCont != null)
      for (final SRCTYPE aIn : aCont)
        ret.add (aConverter.apply (aIn));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> List <DSTTYPE> newList (@Nullable final SRCTYPE [] aCont,
                                                           @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    final List <DSTTYPE> ret = new ArrayList <DSTTYPE> (ArrayHelper.getSize (aCont));
    if (aCont != null)
      for (final SRCTYPE aIn : aCont)
        ret.add (aConverter.apply (aIn));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> List <DSTTYPE> newList (@Nullable final Iterable <? extends SRCTYPE> aCont,
                                                           @Nonnull final Predicate <? super SRCTYPE> aFilter,
                                                           @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    final List <DSTTYPE> ret = new ArrayList <DSTTYPE> ();
    if (aCont != null)
      for (final SRCTYPE aIn : aCont)
        if (aFilter.test (aIn))
          ret.add (aConverter.apply (aIn));
    return ret;
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <SRCTYPE, DSTTYPE> List <DSTTYPE> newUnmodifiableList (@Nullable final Iterable <? extends SRCTYPE> aCont,
                                                                       @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return CollectionHelper.makeUnmodifiable (newList (aCont, aConverter));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <SRCTYPE, DSTTYPE> List <DSTTYPE> newUnmodifiableList (@Nullable final SRCTYPE [] aCont,
                                                                       @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return CollectionHelper.makeUnmodifiable (newList (aCont, aConverter));
  }

  @Nonnull
  @ReturnsImmutableObject
  public static <SRCTYPE, DSTTYPE> List <DSTTYPE> newUnmodifiableList (@Nullable final Iterable <? extends SRCTYPE> aCont,
                                                                       @Nonnull final Predicate <? super SRCTYPE> aFilter,
                                                                       @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    return CollectionHelper.makeUnmodifiable (ContainerConversionHelper.<SRCTYPE, DSTTYPE> newList (aCont,
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
   *         {@link CollectionHelper#getSortedInline(List)}.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE extends Comparable <? super DSTTYPE>> List <DSTTYPE> getSorted (@Nonnull final Iterator <? extends SRCTYPE> it,
                                                                                                  @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    ValueEnforcer.notNull (it, "Iterator");
    ValueEnforcer.notNull (aConverter, "Converter");

    final List <DSTTYPE> ret = new ArrayList <DSTTYPE> ();
    while (it.hasNext ())
      ret.add (aConverter.apply (it.next ()));
    return CollectionHelper.getSortedInline (ret);
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
   *         {@link CollectionHelper#getSortedInline(List, Comparator)}.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> List <DSTTYPE> getSorted (@Nonnull final Iterator <? extends SRCTYPE> it,
                                                             @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter,
                                                             @Nonnull final Comparator <? super DSTTYPE> aComparator)
  {
    ValueEnforcer.notNull (it, "Iterator");
    ValueEnforcer.notNull (aConverter, "Converter");
    ValueEnforcer.notNull (aComparator, "Comparator");

    final List <DSTTYPE> ret = new ArrayList <DSTTYPE> ();
    while (it.hasNext ())
      ret.add (aConverter.apply (it.next ()));
    return CollectionHelper.getSortedInline (ret, aComparator);
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
   *         {@link CollectionHelper#getSortedInline(List)}.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE extends Comparable <? super DSTTYPE>> List <DSTTYPE> getSorted (@Nonnull final Iterable <? extends SRCTYPE> aCont,
                                                                                                  @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    ValueEnforcer.notNull (aCont, "Container");
    ValueEnforcer.notNull (aConverter, "Converter");

    final List <DSTTYPE> ret = new ArrayList <DSTTYPE> ();
    for (final SRCTYPE aSrc : aCont)
      ret.add (aConverter.apply (aSrc));
    return CollectionHelper.getSortedInline (ret);
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
   *         {@link CollectionHelper#getSortedInline(List, Comparator)}.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> List <DSTTYPE> getSorted (@Nonnull final Iterable <? extends SRCTYPE> aCont,
                                                             @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter,
                                                             @Nonnull final Comparator <? super DSTTYPE> aComparator)
  {
    ValueEnforcer.notNull (aCont, "Container");
    ValueEnforcer.notNull (aConverter, "Converter");
    ValueEnforcer.notNull (aComparator, "Comparator");

    final List <DSTTYPE> ret = new ArrayList <DSTTYPE> ();
    for (final SRCTYPE aSrc : aCont)
      ret.add (aConverter.apply (aSrc));
    return CollectionHelper.getSortedInline (ret, aComparator);
  }

  @Nonnull
  public static <SRCTYPE, DSTTYPE> Iterator <DSTTYPE> getIterator (@Nonnull final Iterable <SRCTYPE> aCont,
                                                                   @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    ValueEnforcer.notNull (aCont, "Container");
    ValueEnforcer.notNull (aConverter, "Converter");

    return new Iterator <DSTTYPE> ()
    {
      private final Iterator <SRCTYPE> m_aIT = aCont.iterator ();

      public boolean hasNext ()
      {
        return m_aIT.hasNext ();
      }

      public DSTTYPE next ()
      {
        return aConverter.apply (m_aIT.next ());
      }

      public void remove ()
      {
        m_aIT.remove ();
      }
    };
  }
}

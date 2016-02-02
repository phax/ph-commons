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

import com.helger.commons.annotation.PresentForCodeCoverage;
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
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newSetMapped (@Nonnull final Iterator <? extends SRCTYPE> it,
                                                               @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    final Set <DSTTYPE> ret = new HashSet <> ();
    while (it.hasNext ())
      ret.add (aConverter.apply (it.next ()));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newSetMapped (@Nonnull final Iterable <? extends SRCTYPE> aCont,
                                                               @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    final Set <DSTTYPE> ret = new HashSet <> ();
    for (final SRCTYPE aValue : aCont)
      ret.add (aConverter.apply (aValue));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newSetMapped (@Nonnull final Iterable <? extends SRCTYPE> aCont,
                                                               @Nonnull final Predicate <? super SRCTYPE> aFilter,
                                                               @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    final Set <DSTTYPE> ret = new HashSet <> ();
    for (final SRCTYPE aIn : aCont)
      if (aFilter.test (aIn))
        ret.add (aConverter.apply (aIn));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newOrderedSet (@Nonnull final Iterator <? extends SRCTYPE> it,
                                                                @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    final Set <DSTTYPE> ret = new LinkedHashSet <> ();
    while (it.hasNext ())
      ret.add (aConverter.apply (it.next ()));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> Set <DSTTYPE> newOrderedSet (@Nonnull final Iterable <? extends SRCTYPE> aCont,
                                                                @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    final Set <DSTTYPE> ret = new LinkedHashSet <> ();
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
    final Set <DSTTYPE> ret = new LinkedHashSet <> ();
    for (final SRCTYPE aIn : aCont)
      if (aFilter.test (aIn))
        ret.add (aConverter.apply (aIn));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> List <DSTTYPE> newListMapped (@Nullable final Iterable <? extends SRCTYPE> aCont,
                                                                 @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    final List <DSTTYPE> ret = new ArrayList <> ();
    if (aCont != null)
      for (final SRCTYPE aIn : aCont)
        ret.add (aConverter.apply (aIn));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> List <DSTTYPE> newListMapped (@Nullable final SRCTYPE [] aCont,
                                                                 @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    final List <DSTTYPE> ret = new ArrayList <> (ArrayHelper.getSize (aCont));
    if (aCont != null)
      for (final SRCTYPE aIn : aCont)
        ret.add (aConverter.apply (aIn));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <SRCTYPE, DSTTYPE> List <DSTTYPE> newListMapped (@Nullable final Iterable <? extends SRCTYPE> aCont,
                                                                 @Nonnull final Predicate <? super SRCTYPE> aFilter,
                                                                 @Nonnull final Function <? super SRCTYPE, ? extends DSTTYPE> aConverter)
  {
    final List <DSTTYPE> ret = new ArrayList <> ();
    if (aCont != null)
      for (final SRCTYPE aIn : aCont)
        if (aFilter.test (aIn))
          ret.add (aConverter.apply (aIn));
    return ret;
  }
}

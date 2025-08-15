/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.commons.compare;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.function.Function;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.compare.CompareHelper;
import com.helger.base.compare.IComparator;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Helper to create {@link Comparator} based objects
 *
 * @author Philip Helger
 */
@Immutable
public final class ComparatorHelper
{
  private ComparatorHelper ()
  {}

  @Nonnull
  public static Comparator <String> getComparatorCollating (@Nullable final Locale aSortLocale)
  {
    return getComparatorCollating (CollatorHelper.getCollatorSpaceBeforeDot (aSortLocale));
  }

  @Nonnull
  public static Comparator <String> getComparatorCollating (@Nonnull final Collator aCollator)
  {
    return Comparator.nullsFirst (aCollator::compare);
  }

  @Nonnull
  public static <T> Comparator <T> getComparatorCollating (@Nonnull final Function <? super T, String> aMapper,
                                                           @Nullable final Locale aSortLocale)
  {
    return Comparator.<T, String> comparing (aMapper, getComparatorCollating (aSortLocale));
  }

  @Nonnull
  public static <T> Comparator <T> getComparatorCollating (@Nonnull final Function <? super T, String> aMapper,
                                                           @Nonnull final Collator aCollator)
  {
    return Comparator.<T, String> comparing (aMapper, getComparatorCollating (aCollator));
  }

  @Nonnull
  public static IComparator <String> getComparatorStringLongestFirst ()
  {
    return getComparatorStringLongestFirst (CompareHelper.DEFAULT_NULL_VALUES_COME_FIRST);
  }

  @Nonnull
  public static IComparator <String> getComparatorStringLongestFirst (final boolean bNullValuesComeFirst)
  {
    return (c1, c2) -> CompareHelper.compare (c1, c2, (o1, o2) -> {
      final int ret = o2.length () - o1.length ();
      return ret != 0 ? ret : o1.compareTo (o2);
    }, bNullValuesComeFirst);
  }

  @Nonnull
  public static IComparator <String> getComparatorStringShortestFirst ()
  {
    return getComparatorStringShortestFirst (CompareHelper.DEFAULT_NULL_VALUES_COME_FIRST);
  }

  @Nonnull
  public static IComparator <String> getComparatorStringShortestFirst (final boolean bNullValuesComeFirst)
  {
    return (c1, c2) -> CompareHelper.compare (c1, c2, (o1, o2) -> {
      final int ret = o1.length () - o2.length ();
      return ret != 0 ? ret : o1.compareTo (o2);
    }, bNullValuesComeFirst);
  }

  @Nonnull
  public static IComparator <String> getComparatorStringIgnoreCase ()
  {
    return getComparatorStringIgnoreCase (CompareHelper.DEFAULT_NULL_VALUES_COME_FIRST);
  }

  @Nonnull
  public static IComparator <String> getComparatorStringIgnoreCase (final boolean bNullValuesComeFirst)
  {
    return (c1, c2) -> CompareHelper.compareIgnoreCase (c1, c2, bNullValuesComeFirst);
  }
}

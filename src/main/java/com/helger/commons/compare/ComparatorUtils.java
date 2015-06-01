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
package com.helger.commons.compare;

import java.util.Comparator;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotations.PresentForCodeCoverage;

/**
 * Helper class to easily create commonly used {@link Comparator} objects.
 *
 * @author Philip Helger
 */
@Immutable
public final class ComparatorUtils
{
  @PresentForCodeCoverage
  private static final ComparatorUtils s_aInstance = new ComparatorUtils ();

  private ComparatorUtils ()
  {}

  @Nonnull
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> Comparator <? super Map.Entry <KEYTYPE, VALUETYPE>> getComparatorMapEntryKey ()
  {
    return ComparatorUtils.<KEYTYPE, VALUETYPE> getComparatorMapEntryKey (ESortOrder.DEFAULT);
  }

  @Nonnull
  public static <KEYTYPE extends Comparable <? super KEYTYPE>, VALUETYPE> Comparator <? super Map.Entry <KEYTYPE, VALUETYPE>> getComparatorMapEntryKey (@Nonnull final ESortOrder eSortOrder)
  {
    return new AbstractComparator <Map.Entry <KEYTYPE, VALUETYPE>> ()
    {
      @Override
      protected int mainCompare (final Map.Entry <KEYTYPE, VALUETYPE> aEntry1,
                                 final Map.Entry <KEYTYPE, VALUETYPE> aEntry2)
      {
        return CompareUtils.nullSafeCompare (aEntry1.getKey (), aEntry2.getKey ());
      }
    }.setSortOrder (eSortOrder);
  }

  @Nonnull
  public static <KEYTYPE, VALUETYPE> Comparator <? super Map.Entry <KEYTYPE, VALUETYPE>> getComparatorMapEntryKey (final Comparator <? super KEYTYPE> aKeyComparator)
  {
    return new Comparator <Map.Entry <KEYTYPE, VALUETYPE>> ()
    {
      public int compare (final Map.Entry <KEYTYPE, VALUETYPE> aEntry1, final Map.Entry <KEYTYPE, VALUETYPE> aEntry2)
      {
        return aKeyComparator.compare (aEntry1.getKey (), aEntry2.getKey ());
      }
    };
  }

  @Nonnull
  public static <KEYTYPE, VALUETYPE extends Comparable <? super VALUETYPE>> Comparator <? super Map.Entry <KEYTYPE, VALUETYPE>> getComparatorMapEntryValue ()
  {
    return ComparatorUtils.<KEYTYPE, VALUETYPE> getComparatorMapEntryValue (ESortOrder.DEFAULT);
  }

  @Nonnull
  public static <KEYTYPE, VALUETYPE extends Comparable <? super VALUETYPE>> Comparator <? super Map.Entry <KEYTYPE, VALUETYPE>> getComparatorMapEntryValue (@Nonnull final ESortOrder eSortOrder)
  {
    return new AbstractComparator <Map.Entry <KEYTYPE, VALUETYPE>> ()
    {
      @Override
      protected int mainCompare (final Map.Entry <KEYTYPE, VALUETYPE> aEntry1,
                                 final Map.Entry <KEYTYPE, VALUETYPE> aEntry2)
      {
        return CompareUtils.nullSafeCompare (aEntry1.getValue (), aEntry2.getValue ());
      }
    }.setSortOrder (eSortOrder);
  }

  @Nonnull
  public static <KEYTYPE, VALUETYPE> Comparator <? super Map.Entry <KEYTYPE, VALUETYPE>> getComparatorMapEntryValue (@Nonnull final Comparator <? super VALUETYPE> aValueComparator)
  {
    return new Comparator <Map.Entry <KEYTYPE, VALUETYPE>> ()
    {
      public int compare (final Map.Entry <KEYTYPE, VALUETYPE> aEntry1, final Map.Entry <KEYTYPE, VALUETYPE> aEntry2)
      {
        return aValueComparator.compare (aEntry1.getValue (), aEntry2.getValue ());
      }
    };
  }
}

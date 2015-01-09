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

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.string.StringHelper;

/**
 * A simple collation aware comparator that compares objects by their "toString"
 * representation.
 * 
 * @author Philip Helger
 */
public class ComparatorAsString extends AbstractCollationComparator <Object>
{
  /**
   * Comparator with default locale {@link Collator} and default sort order.
   */
  public ComparatorAsString ()
  {
    super ();
  }

  /**
   * Comparator with default locale {@link Collator} and default sort order and
   * a nested comparator.
   * 
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   */
  public ComparatorAsString (@Nullable final Comparator <? super Object> aNestedComparator)
  {
    super (aNestedComparator);
  }

  /**
   * Comparator with default locale {@link Collator}.
   * 
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   */
  public ComparatorAsString (@Nonnull final ESortOrder eSortOrder)
  {
    super (eSortOrder);
  }

  /**
   * Comparator with default locale {@link Collator} and a nested comparator.
   * 
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   */
  public ComparatorAsString (@Nonnull final ESortOrder eSortOrder,
                             @Nullable final Comparator <? super Object> aNestedComparator)
  {
    super (eSortOrder, aNestedComparator);
  }

  /**
   * Comparator with default sort order and specified sort locale.
   * 
   * @param aSortLocale
   *        The locale to use. May be <code>null</code>.
   */
  public ComparatorAsString (@Nullable final Locale aSortLocale)
  {
    super (aSortLocale);
  }

  /**
   * Comparator with default sort order but special locale and a nested
   * comparator.
   * 
   * @param aSortLocale
   *        The locale to use. May be <code>null</code>.
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   */
  public ComparatorAsString (@Nullable final Locale aSortLocale,
                             @Nullable final Comparator <? super Object> aNestedComparator)
  {
    super (aSortLocale, aNestedComparator);
  }

  /**
   * Constructor with locale and sort order.
   * 
   * @param aSortLocale
   *        The locale to use. May be <code>null</code>.
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   */
  public ComparatorAsString (@Nullable final Locale aSortLocale, @Nonnull final ESortOrder eSortOrder)
  {
    super (aSortLocale, eSortOrder);
  }

  /**
   * Constructor with locale and sort order and a nested comparator.
   * 
   * @param aSortLocale
   *        The locale to use. May be <code>null</code>.
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   */
  public ComparatorAsString (@Nullable final Locale aSortLocale,
                             @Nonnull final ESortOrder eSortOrder,
                             @Nullable final Comparator <? super Object> aNestedComparator)
  {
    super (aSortLocale, eSortOrder, aNestedComparator);
  }

  /**
   * Constructor with {@link Collator} using the default sort order
   * 
   * @param aCollator
   *        The {@link Collator} to use. May not be <code>null</code>.
   */
  public ComparatorAsString (@Nonnull final Collator aCollator)
  {
    super (aCollator);
  }

  /**
   * Constructor with {@link Collator} using the default sort order and a nested
   * comparator.
   * 
   * @param aCollator
   *        The {@link Collator} to use. May not be <code>null</code>.
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   */
  public ComparatorAsString (@Nonnull final Collator aCollator,
                             @Nullable final Comparator <? super Object> aNestedComparator)
  {
    super (aCollator, aNestedComparator);
  }

  /**
   * Constructor with {@link Collator} and sort order.
   * 
   * @param aCollator
   *        The {@link Collator} to use. May not be <code>null</code>.
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   */
  public ComparatorAsString (@Nonnull final Collator aCollator, @Nonnull final ESortOrder eSortOrder)
  {
    super (aCollator, eSortOrder);
  }

  /**
   * Constructor with {@link Collator} and sort order and a nested comparator.
   * 
   * @param aCollator
   *        The {@link Collator} to use. May not be <code>null</code>.
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   */
  public ComparatorAsString (@Nonnull final Collator aCollator,
                             @Nonnull final ESortOrder eSortOrder,
                             @Nullable final Comparator <? super Object> aNestedComparator)
  {
    super (aCollator, eSortOrder, aNestedComparator);
  }

  @Override
  protected String asString (@Nullable final Object aValue)
  {
    return StringHelper.getToString (aValue, "");
  }
}

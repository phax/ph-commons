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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is another *lol* class: a {@link Comparator} for {@link Comparable}
 * objects. In comparison to {@link ComparatorComparable} this class can handle
 * <code>null</code> values.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        Data type to compare
 */
public class ComparatorComparableNullAware <DATATYPE extends Comparable <? super DATATYPE>> extends AbstractComparator <DATATYPE>
{
  /** Default value wether <code>null</code> values come first or last */
  public static final boolean DEFAULT_NULL_VALUES_COME_FIRST = CompareUtils.DEFAULT_NULL_VALUES_COME_FIRST;

  private boolean m_bNullValuesComeFirst = DEFAULT_NULL_VALUES_COME_FIRST;

  /**
   * Comparator with default sort order.
   */
  public ComparatorComparableNullAware ()
  {
    super ();
  }

  /**
   * Constructor with sort order.
   *
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   */
  public ComparatorComparableNullAware (@Nonnull final ESortOrder eSortOrder)
  {
    super (eSortOrder);
  }

  /**
   * Comparator with default sort order and a nested comparator.
   *
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   */
  public ComparatorComparableNullAware (@Nullable final Comparator <? super DATATYPE> aNestedComparator)
  {
    super (aNestedComparator);
  }

  /**
   * Comparator with sort order and a nested comparator.
   *
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   */
  public ComparatorComparableNullAware (@Nonnull final ESortOrder eSortOrder,
                                        @Nullable final Comparator <? super DATATYPE> aNestedComparator)
  {
    super (eSortOrder, aNestedComparator);
  }

  public final boolean isNullValuesComeFirst ()
  {
    return m_bNullValuesComeFirst;
  }

  @Nonnull
  public final ComparatorComparableNullAware <DATATYPE> setNullValuesComeFirst (final boolean bNullValuesComeFirst)
  {
    m_bNullValuesComeFirst = bNullValuesComeFirst;
    return this;
  }

  @Override
  protected final int mainCompare (@Nullable final DATATYPE aElement1, @Nullable final DATATYPE aElement2)
  {
    return CompareUtils.nullSafeCompare (aElement1, aElement2, m_bNullValuesComeFirst);
  }
}

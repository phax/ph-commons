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

import java.io.Serializable;
import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

/**
 * Abstract comparator class that supports a sort order and a nested comparator.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The data type to be compared
 */
@NotThreadSafe
public abstract class AbstractComparator <DATATYPE> implements Comparator <DATATYPE>, Serializable
{
  private ESortOrder m_eSortOrder;
  private Comparator <? super DATATYPE> m_aNestedComparator;

  /**
   * Comparator with default sort order and no nested comparator.
   */
  public AbstractComparator ()
  {
    this (ESortOrder.DEFAULT);
  }

  /**
   * Constructor with sort order.
   *
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   */
  public AbstractComparator (@Nonnull final ESortOrder eSortOrder)
  {
    m_eSortOrder = ValueEnforcer.notNull (eSortOrder, "SortOrder");
  }

  /**
   * @return The currently assigned sort order. Never <code>null</code>.
   */
  @Nonnull
  public final ESortOrder getSortOrder ()
  {
    return m_eSortOrder;
  }

  /**
   * Call this to enable sorting after the constructor was invoked.
   *
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final AbstractComparator <DATATYPE> setSortOrder (@Nonnull final ESortOrder eSortOrder)
  {
    m_eSortOrder = ValueEnforcer.notNull (eSortOrder, "SortOrder");
    return this;
  }

  /**
   * @return The nested comparator. May be <code>null</code>.
   */
  @Nonnull
  public final Comparator <? super DATATYPE> getNestedComparator ()
  {
    return m_aNestedComparator;
  }

  /**
   * Set a nested comparator to be invoked if the comparison result of this
   * comparator is 0.
   *
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final AbstractComparator <DATATYPE> setNestedComparator (@Nullable final Comparator <? super DATATYPE> aNestedComparator)
  {
    m_aNestedComparator = aNestedComparator;
    return this;
  }

  /**
   * @param aElement1
   *        First element to compare. No information on the <code>null</code>
   *        status.
   * @param aElement2
   *        Second element to compare. No information on the <code>null</code>
   *        status.
   * @return a negative integer, zero, or a positive integer as the first
   *         argument is less than, equal to, or greater than the second.
   */
  protected abstract int mainCompare (final DATATYPE aElement1, final DATATYPE aElement2);

  public final int compare (final DATATYPE aElement1, final DATATYPE aElement2)
  {
    // Main compare
    int nCompare = mainCompare (aElement1, aElement2);

    if (nCompare == 0 && m_aNestedComparator != null)
    {
      // Invoke the nested comparator for 2nd level comparison
      nCompare = m_aNestedComparator.compare (aElement1, aElement2);
    }

    // Apply sort order by switching the sign of the return value
    return m_eSortOrder.isAscending () ? nCompare : -nCompare;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("sortOrder", m_eSortOrder)
                                       .appendIfNotNull ("nestedComparator", m_aNestedComparator)
                                       .toString ();
  }
}

/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.changelog;

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.compare.AbstractComparator;
import com.helger.commons.compare.CompareUtils;
import com.helger.commons.compare.ESortOrder;

/**
 * Special comparator to sort change log entries by their date and in case of
 * equality by the parent change logs component name.
 * 
 * @author Philip Helger
 */
public final class ComparatorChangeLogEntryDate extends AbstractComparator <ChangeLogEntry>
{
  /**
   * Comparator with default sort order and no nested comparator.
   */
  public ComparatorChangeLogEntryDate ()
  {
    super ();
  }

  /**
   * Constructor with sort order.
   * 
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   */
  public ComparatorChangeLogEntryDate (@Nonnull final ESortOrder eSortOrder)
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
  public ComparatorChangeLogEntryDate (@Nullable final Comparator <? super ChangeLogEntry> aNestedComparator)
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
  public ComparatorChangeLogEntryDate (@Nonnull final ESortOrder eSortOrder,
                                       @Nullable final Comparator <? super ChangeLogEntry> aNestedComparator)
  {
    super (eSortOrder, aNestedComparator);
  }

  @Override
  protected int mainCompare (@Nonnull final ChangeLogEntry aEntry1, @Nonnull final ChangeLogEntry aEntry2)
  {
    final long n1 = aEntry1.getDate ().getTime ();
    final long n2 = aEntry2.getDate ().getTime ();
    int i = CompareUtils.compare (n1, n2);
    if (i == 0)
    {
      i = CompareUtils.nullSafeCompare (aEntry1.getChangeLog ().getComponent (), aEntry2.getChangeLog ()
                                                                                        .getComponent ());
    }
    return i;
  }
}

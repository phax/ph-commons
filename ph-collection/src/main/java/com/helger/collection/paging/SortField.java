/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.collection.paging;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.compare.ESortOrder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;

/**
 * This class represents a single field to sort by, consisting of a logical field name and the sort
 * order to be applied to it. The field name is a logical name only - it is up to the consumer to
 * map it onto a database column, a document field or a {@link java.util.Comparator}. This class
 * deliberately makes no assumption about the underlying data store.
 *
 * @author Philip Helger
 * @since 12.4.0
 */
@Immutable
public class SortField
{
  private final String m_sFieldName;
  private final ESortOrder m_eSortOrder;

  /**
   * Constructor.
   *
   * @param sFieldName
   *        The logical name of the field to sort by. May neither be <code>null</code> nor empty.
   * @param eSortOrder
   *        The sort order to be applied. May not be <code>null</code>.
   */
  public SortField (@NonNull @Nonempty final String sFieldName, @NonNull final ESortOrder eSortOrder)
  {
    m_sFieldName = ValueEnforcer.notEmpty (sFieldName, "FieldName");
    m_eSortOrder = ValueEnforcer.notNull (eSortOrder, "SortOrder");
  }

  /**
   * @return The logical name of the field to sort by. Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getFieldName ()
  {
    return m_sFieldName;
  }

  /**
   * Check if this sort field refers to the provided field name.
   *
   * @param sFieldName
   *        The field name to check. May be <code>null</code>.
   * @return <code>true</code> if the field name matches, <code>false</code> otherwise.
   */
  public boolean hasFieldName (@Nullable final String sFieldName)
  {
    return m_sFieldName.equals (sFieldName);
  }

  /**
   * @return The sort order to be applied. Never <code>null</code>.
   */
  @NonNull
  public ESortOrder getSortOrder ()
  {
    return m_eSortOrder;
  }

  /**
   * @return <code>true</code> if this field is to be sorted ascending, <code>false</code> if it is
   *         to be sorted descending.
   */
  public boolean isAscending ()
  {
    return m_eSortOrder.isAscending ();
  }

  /**
   * @return A new {@link SortField} with the same field name but the opposite sort order. This is
   *         mainly helpful for UIs that toggle the sort order of a column. Never <code>null</code>.
   */
  @NonNull
  public SortField getAlternate ()
  {
    return new SortField (m_sFieldName, m_eSortOrder.getAlternate ());
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final SortField rhs = (SortField) o;
    return m_sFieldName.equals (rhs.m_sFieldName) && m_eSortOrder.equals (rhs.m_eSortOrder);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sFieldName).append (m_eSortOrder).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("FieldName", m_sFieldName)
                                       .append ("SortOrder", m_eSortOrder)
                                       .getToString ();
  }

  /**
   * Create a new {@link SortField} that sorts ascending.
   *
   * @param sFieldName
   *        The logical name of the field to sort by. May neither be <code>null</code> nor empty.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static SortField ascending (@NonNull @Nonempty final String sFieldName)
  {
    return new SortField (sFieldName, ESortOrder.ASCENDING);
  }

  /**
   * Create a new {@link SortField} that sorts descending.
   *
   * @param sFieldName
   *        The logical name of the field to sort by. May neither be <code>null</code> nor empty.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static SortField descending (@NonNull @Nonempty final String sFieldName)
  {
    return new SortField (sFieldName, ESortOrder.DESCENDING);
  }
}

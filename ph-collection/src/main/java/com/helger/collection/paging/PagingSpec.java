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

import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.CGlobal;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

/**
 * The default immutable implementation of {@link IPagingSpec}.
 *
 * @author Philip Helger
 * @since 12.4.0
 */
@Immutable
public class PagingSpec implements IPagingSpec
{
  /** Return all elements, in the natural order of the underlying data store */
  public static final PagingSpec UNLIMITED = new PagingSpec (0, CGlobal.ILLEGAL_ULONG);

  private final long m_nStartIndex;
  private final long m_nMaxCount;
  private final ICommonsList <SortField> m_aSortFields;

  /**
   * Constructor without sort fields.
   *
   * @param nStartIndex
   *        The 0-based index of the first element to be returned. Must be &ge; 0.
   * @param nMaxCount
   *        The maximum number of elements to be returned. All values &lt; 0 mean "no limit".
   */
  public PagingSpec (@Nonnegative final long nStartIndex, final long nMaxCount)
  {
    this (nStartIndex, nMaxCount, (Iterable <? extends SortField>) null);
  }

  /**
   * Constructor with sort fields.
   *
   * @param nStartIndex
   *        The 0-based index of the first element to be returned. Must be &ge; 0.
   * @param nMaxCount
   *        The maximum number of elements to be returned. All values &lt; 0 mean "no limit".
   * @param aSortFields
   *        The fields to sort by, in the order of precedence. May be <code>null</code> or empty.
   */
  public PagingSpec (@Nonnegative final long nStartIndex,
                     final long nMaxCount,
                     @Nullable final SortField... aSortFields)
  {
    ValueEnforcer.isGE0 (nStartIndex, "StartIndex");
    m_nStartIndex = nStartIndex;
    // Normalize all negative values to the same one, so that equals works as expected
    m_nMaxCount = nMaxCount < 0 ? CGlobal.ILLEGAL_ULONG : nMaxCount;
    m_aSortFields = new CommonsArrayList <> (aSortFields);
    ValueEnforcer.notNullNoNullValue (m_aSortFields, "SortFields");
  }

  /**
   * Constructor with sort fields.
   *
   * @param nStartIndex
   *        The 0-based index of the first element to be returned. Must be &ge; 0.
   * @param nMaxCount
   *        The maximum number of elements to be returned. All values &lt; 0 mean "no limit".
   * @param aSortFields
   *        The fields to sort by, in the order of precedence. May be <code>null</code> or empty.
   */
  public PagingSpec (@Nonnegative final long nStartIndex,
                     final long nMaxCount,
                     @Nullable final Iterable <? extends SortField> aSortFields)
  {
    ValueEnforcer.isGE0 (nStartIndex, "StartIndex");
    m_nStartIndex = nStartIndex;
    // Normalize all negative values to the same one, so that equals works as expected
    m_nMaxCount = nMaxCount < 0 ? CGlobal.ILLEGAL_ULONG : nMaxCount;
    m_aSortFields = new CommonsArrayList <> (aSortFields);
    ValueEnforcer.notNullNoNullValue (m_aSortFields, "SortFields");
  }

  @Nonnegative
  public long getStartIndex ()
  {
    return m_nStartIndex;
  }

  public long getMaxCount ()
  {
    return m_nMaxCount;
  }

  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <SortField> getAllSortFields ()
  {
    return m_aSortFields.getClone ();
  }

  @Nonnegative
  public int getSortFieldCount ()
  {
    return m_aSortFields.size ();
  }

  public void forEachSortField (@NonNull final Consumer <? super SortField> aConsumer)
  {
    m_aSortFields.forEach (aConsumer);
  }

  /**
   * Create a copy of this object, using the provided sort fields instead of the contained ones.
   *
   * @param aSortFields
   *        The fields to sort by, in the order of precedence. May be <code>null</code> or empty.
   * @return A new object and never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public PagingSpec withSortFields (@Nullable final SortField... aSortFields)
  {
    return new PagingSpec (m_nStartIndex, m_nMaxCount, aSortFields);
  }

  /**
   * Create a copy of this object, using the provided sort fields instead of the contained ones.
   *
   * @param aSortFields
   *        The fields to sort by, in the order of precedence. May be <code>null</code> or empty.
   * @return A new object and never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public PagingSpec withSortFields (@Nullable final Iterable <? extends SortField> aSortFields)
  {
    return new PagingSpec (m_nStartIndex, m_nMaxCount, aSortFields);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final PagingSpec rhs = (PagingSpec) o;
    return m_nStartIndex == rhs.m_nStartIndex &&
           m_nMaxCount == rhs.m_nMaxCount &&
           m_aSortFields.equals (rhs.m_aSortFields);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_nStartIndex)
                                       .append (m_nMaxCount)
                                       .append (m_aSortFields)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("StartIndex", m_nStartIndex)
                                       .append ("MaxCount", m_nMaxCount)
                                       .append ("SortFields", m_aSortFields)
                                       .getToString ();
  }

  /**
   * Create a paging specification for a single page, using a 0-based page index.
   *
   * @param nPageIndex
   *        The 0-based index of the page to be returned. Must be &ge; 0.
   * @param nPageSize
   *        The number of elements per page. Must be &gt; 0.
   * @param aSortFields
   *        The fields to sort by, in the order of precedence. May be <code>null</code> or empty.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static PagingSpec createForPage (@Nonnegative final long nPageIndex,
                                          @Nonnegative final long nPageSize,
                                          @Nullable final SortField... aSortFields)
  {
    ValueEnforcer.isGE0 (nPageIndex, "PageIndex");
    ValueEnforcer.isGT0 (nPageSize, "PageSize");

    final long nStartIndex;
    try
    {
      nStartIndex = Math.multiplyExact (nPageIndex, nPageSize);
    }
    catch (final ArithmeticException ex)
    {
      throw new IllegalArgumentException ("The combination of page index " +
                                          nPageIndex +
                                          " and page size " +
                                          nPageSize +
                                          " leads to a start index that is too large");
    }
    return new PagingSpec (nStartIndex, nPageSize, aSortFields);
  }

  /**
   * Create a paging specification that returns all elements, sorted by the provided fields.
   *
   * @param aSortFields
   *        The fields to sort by, in the order of precedence. May be <code>null</code> or empty.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static PagingSpec createUnlimited (@Nullable final SortField... aSortFields)
  {
    return new PagingSpec (0, CGlobal.ILLEGAL_ULONG, aSortFields);
  }
}

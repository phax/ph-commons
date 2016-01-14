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
package com.helger.commons.hierarchy;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.CollectionHelper;

/**
 * An {@link IChildrenProvider} that returns the children in
 * {@link #getAllChildren(Object)} sorted.<br>
 * The implementation wraps an existing children provider and uses and external
 * comparator for sorting.
 *
 * @author Philip Helger
 * @param <CHILDTYPE>
 *        The data type of the child objects.
 */
@Immutable
public class ChildrenProviderSorting <CHILDTYPE> implements IChildrenProvider <CHILDTYPE>
{
  private final IChildrenProvider <CHILDTYPE> m_aChildrenProvider;
  private final Comparator <? super CHILDTYPE> m_aComparator;

  /**
   * Constructor.
   *
   * @param aCP
   *        The children provider to be wrapped
   * @param aComparator
   *        The comparator to be used for sorting children.
   */
  public ChildrenProviderSorting (@Nonnull final IChildrenProvider <CHILDTYPE> aCP,
                                  @Nonnull final Comparator <? super CHILDTYPE> aComparator)
  {
    m_aChildrenProvider = ValueEnforcer.notNull (aCP, "ChildrenProvider");
    m_aComparator = ValueEnforcer.notNull (aComparator, "Comparator");
  }

  @Nonnull
  public IChildrenProvider <CHILDTYPE> getChildrenProvider ()
  {
    return m_aChildrenProvider;
  }

  @Nonnull
  public Comparator <? super CHILDTYPE> getComparator ()
  {
    return m_aComparator;
  }

  public final boolean hasChildren (@Nullable final CHILDTYPE aCurrent)
  {
    // Just pass on to the original children resolver
    return m_aChildrenProvider.hasChildren (aCurrent);
  }

  public final int getChildCount (@Nullable final CHILDTYPE aCurrent)
  {
    // Just pass on to the original children resolver
    return m_aChildrenProvider.getChildCount (aCurrent);
  }

  @Nullable
  public List <? extends CHILDTYPE> getAllChildren (@Nullable final CHILDTYPE aCurrent)
  {
    // Get the unsorted collection of children
    final Collection <? extends CHILDTYPE> ret = m_aChildrenProvider.getAllChildren (aCurrent);

    // If there is anything to sort, do it now
    return ret == null ? null : CollectionHelper.getSorted (ret, m_aComparator);
  }
}

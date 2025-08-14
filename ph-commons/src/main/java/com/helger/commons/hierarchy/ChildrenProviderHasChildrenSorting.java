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
package com.helger.commons.hierarchy;

import java.util.Comparator;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforcer.ValueEnforcer;
import com.helger.commons.collection.impl.ICommonsCollection;
import com.helger.commons.collection.impl.ICommonsList;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * An implementation of the
 * {@link com.helger.commons.hierarchy.IChildrenProvider} interface that works
 * with all types that implement
 * {@link com.helger.commons.hierarchy.IHasChildren}. It automatically sorts the
 * returned children by the specified comparator.
 *
 * @author Philip Helger
 * @param <CHILDTYPE>
 *        The data type of the child objects.
 */
@Immutable
public class ChildrenProviderHasChildrenSorting <CHILDTYPE extends IHasChildren <CHILDTYPE>> extends ChildrenProviderHasChildren <CHILDTYPE>
{
  private final Comparator <? super CHILDTYPE> m_aComparator;

  public ChildrenProviderHasChildrenSorting (@Nonnull final Comparator <? super CHILDTYPE> aComparator)
  {
    m_aComparator = ValueEnforcer.notNull (aComparator, "Comparator");
  }

  @Nonnull
  public Comparator <? super CHILDTYPE> getComparator ()
  {
    return m_aComparator;
  }

  @Override
  @Nullable
  public ICommonsList <? extends CHILDTYPE> getAllChildren (@Nullable final CHILDTYPE aCurrent)
  {
    // Get all children of the passed element
    final ICommonsCollection <? extends CHILDTYPE> ret = aCurrent == null ? null : aCurrent.getAllChildren ();

    // If there is anything to sort do it now
    return ret == null ? null : ret.getSorted (m_aComparator);
  }
}

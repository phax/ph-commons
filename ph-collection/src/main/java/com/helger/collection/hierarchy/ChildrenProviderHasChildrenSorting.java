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
package com.helger.collection.hierarchy;

import java.util.Comparator;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.collection.commons.ICommonsCollection;
import com.helger.collection.commons.ICommonsList;

/**
 * An implementation of the
 * {@link com.helger.collection.hierarchy.IChildrenProvider} interface that works
 * with all types that implement
 * {@link com.helger.collection.hierarchy.IHasChildren}. It automatically sorts the
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

  public ChildrenProviderHasChildrenSorting (@NonNull final Comparator <? super CHILDTYPE> aComparator)
  {
    m_aComparator = ValueEnforcer.notNull (aComparator, "Comparator");
  }

  @NonNull
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

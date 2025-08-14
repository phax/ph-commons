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

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.collection.commons.ICommonsList;

import jakarta.annotation.Nullable;

/**
 * An {@link IChildrenProviderSorted} implementation for object implementing the
 * {@link IHasChildrenSorted} interface.
 *
 * @author Philip Helger
 * @param <CHILDTYPE>
 *        The data type of the child objects.
 */
@Immutable
public final class ChildrenProviderHasChildrenSorted <CHILDTYPE extends IHasChildrenSorted <CHILDTYPE>> extends
                                                     ChildrenProviderHasChildren <CHILDTYPE> implements
                                                     IChildrenProviderSorted <CHILDTYPE>
{
  @Override
  @Nullable
  public ICommonsList <? extends CHILDTYPE> getAllChildren (@Nullable final CHILDTYPE aCurrent)
  {
    return aCurrent == null ? null : aCurrent.getAllChildren ();
  }

  @Nullable
  public CHILDTYPE getChildAtIndex (@Nullable final CHILDTYPE aCurrent, @Nonnegative final int nIndex)
  {
    return aCurrent == null ? null : aCurrent.getChildAtIndex (nIndex);
  }
}

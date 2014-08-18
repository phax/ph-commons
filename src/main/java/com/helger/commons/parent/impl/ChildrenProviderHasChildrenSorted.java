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
package com.helger.commons.parent.impl;

import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.parent.IChildrenProviderSorted;
import com.helger.commons.parent.IHasChildrenSorted;

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
  public List <? extends CHILDTYPE> getChildren (@Nullable final CHILDTYPE aCurrent)
  {
    return aCurrent == null ? null : aCurrent.getChildren ();
  }

  @Nullable
  public CHILDTYPE getChildAtIndex (@Nullable final CHILDTYPE aCurrent, @Nonnegative final int nIndex)
  {
    return aCurrent == null ? null : aCurrent.getChildAtIndex (nIndex);
  }
}

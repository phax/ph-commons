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
package com.helger.commons.collection.ext;

import java.util.Collection;
import java.util.HashSet;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;

public class CommonsHashSet <ELEMENTTYPE> extends HashSet <ELEMENTTYPE> implements ICommonsSet <ELEMENTTYPE>
{
  public CommonsHashSet ()
  {}

  public CommonsHashSet (final int nInitialCapacity)
  {
    super (nInitialCapacity);
  }

  public CommonsHashSet (final int nInitialCapacity, final float fLoadFactor)
  {
    super (nInitialCapacity, fLoadFactor);
  }

  public CommonsHashSet (@Nonnull final Collection <? extends ELEMENTTYPE> aCont)
  {
    super (aCont);
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsHashSet <ELEMENTTYPE> getClone ()
  {
    return new CommonsHashSet <> (this);
  }
}

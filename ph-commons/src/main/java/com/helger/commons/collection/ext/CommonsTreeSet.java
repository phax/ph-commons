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
import java.util.Comparator;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;

public class CommonsTreeSet <ELEMENTTYPE> extends TreeSet <ELEMENTTYPE> implements ICommonsNavigableSet <ELEMENTTYPE>
{
  public CommonsTreeSet ()
  {}

  public CommonsTreeSet (@Nullable final Comparator <? super ELEMENTTYPE> aComparator)
  {
    super (aComparator);
  }

  public CommonsTreeSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    if (aCont != null)
      addAll (aCont);
  }

  public CommonsTreeSet (@Nullable final Iterable <? extends ELEMENTTYPE> aIterable)
  {
    addAll (aIterable);
  }

  public CommonsTreeSet (@Nullable final ELEMENTTYPE aValue)
  {
    add (aValue);
  }

  @SafeVarargs
  public CommonsTreeSet (@Nullable final ELEMENTTYPE... aValues)
  {
    addAll (aValues);
  }

  @Nonnull
  @ReturnsMutableCopy
  public <T> CommonsTreeSet <T> createInstance ()
  {
    return new CommonsTreeSet <> ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public CommonsTreeSet <ELEMENTTYPE> getClone ()
  {
    final CommonsTreeSet <ELEMENTTYPE> ret = new CommonsTreeSet <> (comparator ());
    ret.addAll (this);
    return ret;
  }
}

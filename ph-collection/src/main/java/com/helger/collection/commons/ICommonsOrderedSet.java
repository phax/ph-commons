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
package com.helger.collection.commons;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.collection.CollectionFind;

/**
 * A special ordered {@link Set} based interface with extended functionality based on
 * {@link ICommonsSet}.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        Set element type
 */
public interface ICommonsOrderedSet <ELEMENTTYPE> extends ICommonsSet <ELEMENTTYPE>
{
  @Override
  @NonNull
  @ReturnsMutableCopy
  default <T> ICommonsOrderedSet <T> createInstance ()
  {
    return new CommonsLinkedHashSet <> ();
  }

  @Override
  @NonNull
  @ReturnsMutableCopy
  default ICommonsOrderedSet <ELEMENTTYPE> getAll (@Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      return getClone ();

    final ICommonsOrderedSet <ELEMENTTYPE> ret = createInstance ();
    findAll (aFilter, ret::add);
    return ret;
  }

  @Override
  @NonNull
  @ReturnsMutableCopy
  default <DSTTYPE> ICommonsOrderedSet <DSTTYPE> getAllMapped (@NonNull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper)
  {
    final ICommonsOrderedSet <DSTTYPE> ret = createInstance ();
    findAllMapped (aMapper, ret::add);
    return ret;
  }

  @Override
  @NonNull
  @ReturnsMutableCopy
  default <DSTTYPE> ICommonsOrderedSet <DSTTYPE> getAllMapped (@Nullable final Predicate <? super ELEMENTTYPE> aFilter,
                                                               @NonNull final Function <? super ELEMENTTYPE, DSTTYPE> aMapper)
  {
    final ICommonsOrderedSet <DSTTYPE> ret = createInstance ();
    findAllMapped (aFilter, aMapper, ret::add);
    return ret;
  }

  @Override
  @NonNull
  @ReturnsMutableCopy
  default <DSTTYPE extends ELEMENTTYPE> ICommonsOrderedSet <DSTTYPE> getAllInstanceOf (@NonNull final Class <DSTTYPE> aDstClass)
  {
    final ICommonsOrderedSet <DSTTYPE> ret = createInstance ();
    findAllInstanceOf (aDstClass, ret::add);
    return ret;
  }

  @Nullable
  default ELEMENTTYPE getFirst ()
  {
    return getFirst (null);
  }

  @Nullable
  default ELEMENTTYPE getFirst (@Nullable final ELEMENTTYPE aDefault)
  {
    return isEmpty () ? aDefault : iterator ().next ();
  }

  @Nullable
  default ELEMENTTYPE getLast ()
  {
    return getLast (null);
  }

  @Nullable
  default ELEMENTTYPE getLast (@Nullable final ELEMENTTYPE aDefault)
  {
    return isEmpty () ? aDefault : CollectionFind.getLastElement (this);
  }

  @NonNull
  @ReturnsMutableCopy
  ICommonsOrderedSet <ELEMENTTYPE> getClone ();
}

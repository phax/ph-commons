/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.commons.collection.impl;

import java.util.Collections;
import java.util.SortedSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.annotation.ReturnsMutableCopy;

/**
 * A special {@link SortedSet} based interface with extended functionality based
 * on {@link ICommonsSet}.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        Set element type
 */
public interface ICommonsSortedSet <ELEMENTTYPE> extends SortedSet <ELEMENTTYPE>, ICommonsSet <ELEMENTTYPE>
{
  @Nullable
  default ELEMENTTYPE getFirst ()
  {
    return getFirst (null);
  }

  @Nullable
  default ELEMENTTYPE getFirst (@Nullable final ELEMENTTYPE aDefault)
  {
    return isEmpty () ? aDefault : first ();
  }

  @Nullable
  default ELEMENTTYPE getLast ()
  {
    return getLast (null);
  }

  @Nullable
  default ELEMENTTYPE getLast (@Nullable final ELEMENTTYPE aDefault)
  {
    return isEmpty () ? aDefault : last ();
  }

  @Override
  @Nonnull
  @CodingStyleguideUnaware
  default SortedSet <ELEMENTTYPE> getAsUnmodifiable ()
  {
    return Collections.unmodifiableSortedSet (this);
  }

  @Nonnull
  @ReturnsMutableCopy
  ICommonsSortedSet <ELEMENTTYPE> getClone ();
}

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
package com.helger.collection.commons;

import java.util.Collections;
import java.util.SortedSet;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.annotation.style.ReturnsMutableCopy;

/**
 * A special {@link SortedSet} based interface with extended functionality based on
 * {@link ICommonsSet}.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        Set element type
 */
public interface ICommonsSortedSet <ELEMENTTYPE> extends SortedSet <ELEMENTTYPE>, ICommonsSet <ELEMENTTYPE>
{
  /**
   * @return The first element of the set or <code>null</code> if the set is empty.
   * @deprecated Use {@link #getFirstOrNull()} instead
   */
  @Nullable
  @Deprecated (forRemoval = true, since = "12.5.0")
  default ELEMENTTYPE getFirst ()
  {
    return getFirst (null);
  }

  /**
   * @return The first element of the set or <code>null</code> if the set is empty.
   * @see #getFirst(Object)
   * @since 12.5.0
   */
  @Nullable
  default ELEMENTTYPE getFirstOrNull ()
  {
    return getFirst (null);
  }

  /**
   * @param aDefault
   *        The default value to be returned if this set is empty. May be <code>null</code>.
   * @return The first element of the set or the provided default value if the set is empty.
   * @see #getFirstOrNull()
   */
  @Nullable
  default ELEMENTTYPE getFirst (@Nullable final ELEMENTTYPE aDefault)
  {
    return isEmpty () ? aDefault : first ();
  }

  /**
   * @return The last element of the set or <code>null</code> if the set is empty.
   * @deprecated Use {@link #getLastOrNull()} instead
   */
  @Nullable
  @Deprecated (forRemoval = true, since = "12.5.0")
  default ELEMENTTYPE getLast ()
  {
    return getLast (null);
  }

  /**
   * @return The last element of the set or <code>null</code> if the set is empty.
   * @see #getLast(Object)
   * @since 12.5.0
   */
  @Nullable
  default ELEMENTTYPE getLastOrNull ()
  {
    return getLast (null);
  }

  /**
   * @param aDefault
   *        The default value to be returned if this set is empty. May be <code>null</code>.
   * @return The last element of the set or the provided default value if the set is empty.
   * @see #getLastOrNull()
   */
  @Nullable
  default ELEMENTTYPE getLast (@Nullable final ELEMENTTYPE aDefault)
  {
    return isEmpty () ? aDefault : last ();
  }

  @Override
  @NonNull
  @CodingStyleguideUnaware
  default SortedSet <ELEMENTTYPE> getAsUnmodifiable ()
  {
    return Collections.unmodifiableSortedSet (this);
  }

  @NonNull
  @ReturnsMutableCopy
  ICommonsSortedSet <ELEMENTTYPE> getClone ();
}

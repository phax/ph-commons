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
package com.helger.commons.collection.impl;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collection.iterate.EmptyIterator;

/**
 * Default implementation of an empty sorted set.
 *
 * @author Philip Helger
 * @param <T>
 *        Content type
 */
public final class EmptySortedSet <T> extends AbstractSet <T> implements SortedSet <T>
{
  public EmptySortedSet ()
  {}

  @Override
  @Nonnull
  public Iterator <T> iterator ()
  {
    return new EmptyIterator <T> ();
  }

  @Override
  @Nonnegative
  public int size ()
  {
    return 0;
  }

  @Override
  public boolean isEmpty ()
  {
    return true;
  }

  @Override
  public boolean contains (final Object obj)
  {
    return false;
  }

  @Nullable
  public Comparator <Object> comparator ()
  {
    return null;
  }

  @Nonnull
  public SortedSet <T> subSet (final Object fromElement, final Object toElement)
  {
    return this;
  }

  @Nonnull
  public SortedSet <T> headSet (final Object toElement)
  {
    return this;
  }

  @Nonnull
  public SortedSet <T> tailSet (final Object fromElement)
  {
    return this;
  }

  @Nullable
  public T first ()
  {
    return null;
  }

  @Nullable
  public T last ()
  {
    return null;
  }
}

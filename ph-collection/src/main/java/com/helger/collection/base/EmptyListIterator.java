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
package com.helger.collection.base;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.UnsupportedOperation;
import com.helger.base.tostring.ToStringGenerator;

/**
 * Utility implementation of the {@link ListIterator} for an empty list :)
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The type of the list iterator
 */
@Immutable
public class EmptyListIterator <ELEMENTTYPE> implements ListIterator <ELEMENTTYPE>
{
  /**
   * Unsupported operation.
   *
   * @param o
   *        The element to add. Ignored.
   * @throws UnsupportedOperationException
   *         Always thrown.
   */
  @UnsupportedOperation
  public void add (final ELEMENTTYPE o)
  {
    throw new UnsupportedOperationException ();
  }

  /** {@inheritDoc} */
  public boolean hasPrevious ()
  {
    return false;
  }

  /** {@inheritDoc} */
  public ELEMENTTYPE previous ()
  {
    throw new NoSuchElementException ();
  }

  /** {@inheritDoc} */
  public int previousIndex ()
  {
    return -1;
  }

  /** {@inheritDoc} */
  public boolean hasNext ()
  {
    return false;
  }

  /** {@inheritDoc} */
  public ELEMENTTYPE next ()
  {
    throw new NoSuchElementException ();
  }

  /** {@inheritDoc} */
  public int nextIndex ()
  {
    return 0;
  }

  /**
   * Unsupported operation.
   *
   * @throws UnsupportedOperationException
   *         Always thrown.
   */
  @UnsupportedOperation
  public void remove ()
  {
    throw new UnsupportedOperationException ();
  }

  /**
   * Unsupported operation.
   *
   * @param o
   *        The element to set. Ignored.
   * @throws UnsupportedOperationException
   *         Always thrown.
   */
  @UnsupportedOperation
  public void set (final ELEMENTTYPE o)
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public boolean equals (final Object o)
  {
    // Singleton object!
    return o == this;
  }

  @Override
  public int hashCode ()
  {
    // Singleton object!
    return System.identityHashCode (this);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).getToString ();
  }
}

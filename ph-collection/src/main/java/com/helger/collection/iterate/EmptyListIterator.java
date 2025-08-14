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
package com.helger.collection.iterate;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.UnsupportedOperation;
import com.helger.base.string.ToStringGenerator;

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
  @UnsupportedOperation
  public void add (final ELEMENTTYPE o)
  {
    throw new UnsupportedOperationException ();
  }

  public boolean hasPrevious ()
  {
    return false;
  }

  public ELEMENTTYPE previous ()
  {
    throw new NoSuchElementException ();
  }

  public int previousIndex ()
  {
    return -1;
  }

  public boolean hasNext ()
  {
    return false;
  }

  public ELEMENTTYPE next ()
  {
    throw new NoSuchElementException ();
  }

  public int nextIndex ()
  {
    return 0;
  }

  @UnsupportedOperation
  public void remove ()
  {
    throw new UnsupportedOperationException ();
  }

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

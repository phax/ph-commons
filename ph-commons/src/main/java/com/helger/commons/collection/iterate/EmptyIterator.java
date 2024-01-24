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
package com.helger.commons.collection.iterate;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.Immutable;

import com.helger.commons.string.ToStringGenerator;

/**
 * Implementation of an empty enumerator.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The pseudo element type to iterate
 */
@Immutable
public class EmptyIterator <ELEMENTTYPE> implements Iterator <ELEMENTTYPE>
{
  public boolean hasNext ()
  {
    return false;
  }

  public ELEMENTTYPE next ()
  {
    throw new NoSuchElementException ();
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

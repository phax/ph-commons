/**
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
package com.helger.commons.collections.iterate;

import java.util.Enumeration;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.lang.GenericReflection;
import com.helger.commons.string.ToStringGenerator;

/**
 * Represents a fixed {@link Enumeration} implementation that contains no
 * elements.
 * 
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The type to be contained in the operation.
 */
@Immutable
public final class EmptyEnumeration <ELEMENTTYPE> implements Enumeration <ELEMENTTYPE>
{
  /* The one and only instance of this enumeration */
  private static final EmptyEnumeration <Object> s_aInstance = new EmptyEnumeration <Object> ();

  private EmptyEnumeration ()
  {}

  public boolean hasMoreElements ()
  {
    return false;
  }

  public ELEMENTTYPE nextElement ()
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
    return new ToStringGenerator (this).toString ();
  }

  @Nonnull
  public static <ELEMENTTYPE> EmptyEnumeration <ELEMENTTYPE> getInstance ()
  {
    return GenericReflection.<EmptyEnumeration <Object>, EmptyEnumeration <ELEMENTTYPE>> uncheckedCast (s_aInstance);
  }
}

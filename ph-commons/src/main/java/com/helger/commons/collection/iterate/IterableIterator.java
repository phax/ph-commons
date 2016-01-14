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
package com.helger.commons.collection.iterate;

import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.string.ToStringGenerator;

/**
 * This class is used for simpler iteration over an Iterator via the new "for"
 * syntax.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The type of elements to iterate.
 */
public class IterableIterator <ELEMENTTYPE> implements IIterableIterator <ELEMENTTYPE>
{
  private static final IterableIterator <Object> s_aEmpty = new IterableIterator <Object> (new EmptyIterator <Object> ());

  private final Iterator <ELEMENTTYPE> m_aIter;

  protected IterableIterator (@Nonnull final ELEMENTTYPE [] aCont)
  {
    this (new ArrayIterator <> (aCont));
  }

  protected IterableIterator (@Nonnull final Iterable <ELEMENTTYPE> aCont)
  {
    this (aCont.iterator ());
  }

  protected IterableIterator (@Nonnull final Iterator <ELEMENTTYPE> aIter)
  {
    m_aIter = ValueEnforcer.notNull (aIter, "Iterator");
  }

  public final boolean hasNext ()
  {
    return m_aIter.hasNext ();
  }

  @Nullable
  public final ELEMENTTYPE next ()
  {
    return m_aIter.next ();
  }

  public final void remove ()
  {
    m_aIter.remove ();
  }

  @Nonnull
  public final Iterator <ELEMENTTYPE> iterator ()
  {
    return m_aIter;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("iter", m_aIter).toString ();
  }

  @Nonnull
  public static <ELEMENTTYPE> IIterableIterator <ELEMENTTYPE> createEmpty ()
  {
    return GenericReflection.<IterableIterator <Object>, IterableIterator <ELEMENTTYPE>> uncheckedCast (s_aEmpty);
  }

  @Nonnull
  public static <ELEMENTTYPE> IIterableIterator <ELEMENTTYPE> create (@Nonnull final ELEMENTTYPE [] aCont)
  {
    return new IterableIterator <ELEMENTTYPE> (aCont);
  }

  @Nonnull
  public static <ELEMENTTYPE> IIterableIterator <ELEMENTTYPE> create (@Nonnull final Iterable <ELEMENTTYPE> aCont)
  {
    return new IterableIterator <ELEMENTTYPE> (aCont);
  }

  @Nonnull
  public static <ELEMENTTYPE> IIterableIterator <ELEMENTTYPE> create (@Nonnull final Iterator <ELEMENTTYPE> aIter)
  {
    return new IterableIterator <ELEMENTTYPE> (aIter);
  }
}

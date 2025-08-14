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

import java.util.Arrays;
import java.util.Iterator;

import com.helger.base.equals.ValueEnforcer;
import com.helger.base.reflection.GenericReflection;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.base.EmptyIterator;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * This class is used for simpler iteration over an Iterator via the new "for" syntax.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The type of elements to iterate.
 */
public class CommonsIterableIterator <ELEMENTTYPE> implements ICommonsIterableIterator <ELEMENTTYPE>
{
  private static final CommonsIterableIterator <Object> EMPTY_ITERATOR = new CommonsIterableIterator <> (new EmptyIterator <> ());

  private final Iterator <ELEMENTTYPE> m_aIter;

  public CommonsIterableIterator (@Nonnull final ELEMENTTYPE [] aCont)
  {
    this (Arrays.asList (aCont));
  }

  public CommonsIterableIterator (@Nonnull final Iterable <ELEMENTTYPE> aCont)
  {
    this (aCont.iterator ());
  }

  public CommonsIterableIterator (@Nonnull final Iterator <ELEMENTTYPE> aIter)
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

  @Override
  public final void remove ()
  {
    m_aIter.remove ();
  }

  @Override
  @Nonnull
  public final Iterator <ELEMENTTYPE> iterator ()
  {
    return m_aIter;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Iter", m_aIter).getToString ();
  }

  @Nonnull
  public static <ELEMENTTYPE> ICommonsIterableIterator <ELEMENTTYPE> createEmpty ()
  {
    return GenericReflection.uncheckedCast (EMPTY_ITERATOR);
  }
}

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
package com.helger.collection.wrapped;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.equals.ValueEnforcer;
import com.helger.collection.commons.ICommonsList;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * This is a facade for a {@link List}. It may be used to wrap any kind of
 * {@link List} and overwrite single methods, e.g. for logging.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        Element type
 */
@NotThreadSafe
public class WrappedList <ELEMENTTYPE> implements ICommonsList <ELEMENTTYPE>
{
  @CodingStyleguideUnaware
  private final List <ELEMENTTYPE> m_aSrc;

  public WrappedList (@Nonnull final List <ELEMENTTYPE> aList)
  {
    m_aSrc = ValueEnforcer.notNull (aList, "List");
  }

  @Nonnull
  @ReturnsMutableCopy
  public WrappedList <ELEMENTTYPE> getClone ()
  {
    return new WrappedList <> (m_aSrc);
  }

  @Nonnull
  @ReturnsMutableObject ("design")
  @CodingStyleguideUnaware
  protected List <ELEMENTTYPE> directGetSource ()
  {
    return m_aSrc;
  }

  @Nullable
  public ELEMENTTYPE get (@Nonnegative final int nIndex)
  {
    return m_aSrc.get (nIndex);
  }

  public boolean add (@Nullable final ELEMENTTYPE aElement)
  {
    return m_aSrc.add (aElement);
  }

  public void add (@Nonnegative final int nIndex, @Nullable final ELEMENTTYPE aElement)
  {
    m_aSrc.add (nIndex, aElement);
  }

  public boolean addAll (@Nonnull final Collection <? extends ELEMENTTYPE> aElements)
  {
    return m_aSrc.addAll (aElements);
  }

  public boolean addAll (@Nonnegative final int nIndex, @Nonnull final Collection <? extends ELEMENTTYPE> aElements)
  {
    return m_aSrc.addAll (nIndex, aElements);
  }

  public void clear ()
  {
    m_aSrc.clear ();
  }

  public boolean contains (final Object aElement)
  {
    return m_aSrc.contains (aElement);
  }

  public boolean containsAll (final Collection <?> aElements)
  {
    return m_aSrc.containsAll (aElements);
  }

  public int indexOf (final Object aElement)
  {
    return m_aSrc.indexOf (aElement);
  }

  public boolean isEmpty ()
  {
    return m_aSrc.isEmpty ();
  }

  public Iterator <ELEMENTTYPE> iterator ()
  {
    return m_aSrc.iterator ();
  }

  public int lastIndexOf (final Object aElement)
  {
    return m_aSrc.lastIndexOf (aElement);
  }

  public ListIterator <ELEMENTTYPE> listIterator ()
  {
    return m_aSrc.listIterator ();
  }

  public ListIterator <ELEMENTTYPE> listIterator (final int nIndex)
  {
    return m_aSrc.listIterator (nIndex);
  }

  public boolean remove (final Object aElement)
  {
    return m_aSrc.remove (aElement);
  }

  public ELEMENTTYPE remove (final int nIndex)
  {
    return m_aSrc.remove (nIndex);
  }

  public boolean removeAll (final Collection <?> aElements)
  {
    return m_aSrc.removeAll (aElements);
  }

  public boolean retainAll (final Collection <?> aElements)
  {
    return m_aSrc.retainAll (aElements);
  }

  public ELEMENTTYPE set (@Nonnegative final int nIndex, final ELEMENTTYPE aElement)
  {
    return m_aSrc.set (nIndex, aElement);
  }

  @Nonnegative
  public int size ()
  {
    return m_aSrc.size ();
  }

  @ReturnsMutableObject ("as defined by List")
  public List <ELEMENTTYPE> subList (final int nFromIndex, final int nToIndex)
  {
    return m_aSrc.subList (nFromIndex, nToIndex);
  }

  public Object [] toArray ()
  {
    return m_aSrc.toArray ();
  }

  public <ARRAYELEMENTTYPE> ARRAYELEMENTTYPE [] toArray (final ARRAYELEMENTTYPE [] a)
  {
    return m_aSrc.toArray (a);
  }

  @Override
  public boolean equals (final Object o)
  {
    return m_aSrc.equals (o);
  }

  @Override
  public int hashCode ()
  {
    return m_aSrc.hashCode ();
  }

  @Override
  public String toString ()
  {
    return m_aSrc.toString ();
  }
}

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
package com.helger.collection.wrapped;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.collection.commons.ICommonsList;

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

  /**
   * Constructor.
   *
   * @param aList
   *        The list to wrap. May not be <code>null</code>.
   */
  public WrappedList (@NonNull final List <ELEMENTTYPE> aList)
  {
    m_aSrc = ValueEnforcer.notNull (aList, "List");
  }

  /** {@inheritDoc} */
  @NonNull
  @ReturnsMutableCopy
  public WrappedList <ELEMENTTYPE> getClone ()
  {
    return new WrappedList <> (m_aSrc);
  }

  @NonNull
  @ReturnsMutableObject ("design")
  @CodingStyleguideUnaware
  protected List <ELEMENTTYPE> directGetSource ()
  {
    return m_aSrc;
  }

  /** {@inheritDoc} */
  @Nullable
  public ELEMENTTYPE get (@Nonnegative final int nIndex)
  {
    return m_aSrc.get (nIndex);
  }

  /** {@inheritDoc} */
  public boolean add (@Nullable final ELEMENTTYPE aElement)
  {
    return m_aSrc.add (aElement);
  }

  /** {@inheritDoc} */
  public void add (@Nonnegative final int nIndex, @Nullable final ELEMENTTYPE aElement)
  {
    m_aSrc.add (nIndex, aElement);
  }

  /** {@inheritDoc} */
  public boolean addAll (@NonNull final Collection <? extends ELEMENTTYPE> aElements)
  {
    return m_aSrc.addAll (aElements);
  }

  /** {@inheritDoc} */
  public boolean addAll (@Nonnegative final int nIndex, @NonNull final Collection <? extends ELEMENTTYPE> aElements)
  {
    return m_aSrc.addAll (nIndex, aElements);
  }

  /** {@inheritDoc} */
  public void clear ()
  {
    m_aSrc.clear ();
  }

  /** {@inheritDoc} */
  public boolean contains (final Object aElement)
  {
    return m_aSrc.contains (aElement);
  }

  /** {@inheritDoc} */
  public boolean containsAll (final Collection <?> aElements)
  {
    return m_aSrc.containsAll (aElements);
  }

  /** {@inheritDoc} */
  public int indexOf (final Object aElement)
  {
    return m_aSrc.indexOf (aElement);
  }

  /** {@inheritDoc} */
  public boolean isEmpty ()
  {
    return m_aSrc.isEmpty ();
  }

  /** {@inheritDoc} */
  public Iterator <ELEMENTTYPE> iterator ()
  {
    return m_aSrc.iterator ();
  }

  /** {@inheritDoc} */
  public int lastIndexOf (final Object aElement)
  {
    return m_aSrc.lastIndexOf (aElement);
  }

  /** {@inheritDoc} */
  public ListIterator <ELEMENTTYPE> listIterator ()
  {
    return m_aSrc.listIterator ();
  }

  /** {@inheritDoc} */
  public ListIterator <ELEMENTTYPE> listIterator (final int nIndex)
  {
    return m_aSrc.listIterator (nIndex);
  }

  /** {@inheritDoc} */
  public boolean remove (final Object aElement)
  {
    return m_aSrc.remove (aElement);
  }

  /** {@inheritDoc} */
  public ELEMENTTYPE remove (final int nIndex)
  {
    return m_aSrc.remove (nIndex);
  }

  /** {@inheritDoc} */
  public boolean removeAll (final Collection <?> aElements)
  {
    return m_aSrc.removeAll (aElements);
  }

  /** {@inheritDoc} */
  public boolean retainAll (final Collection <?> aElements)
  {
    return m_aSrc.retainAll (aElements);
  }

  /** {@inheritDoc} */
  public ELEMENTTYPE set (@Nonnegative final int nIndex, final ELEMENTTYPE aElement)
  {
    return m_aSrc.set (nIndex, aElement);
  }

  /** {@inheritDoc} */
  @Nonnegative
  public int size ()
  {
    return m_aSrc.size ();
  }

  /** {@inheritDoc} */
  @ReturnsMutableObject ("as defined by List")
  public List <ELEMENTTYPE> subList (final int nFromIndex, final int nToIndex)
  {
    return m_aSrc.subList (nFromIndex, nToIndex);
  }

  /** {@inheritDoc} */
  public Object [] toArray ()
  {
    return m_aSrc.toArray ();
  }

  /** {@inheritDoc} */
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

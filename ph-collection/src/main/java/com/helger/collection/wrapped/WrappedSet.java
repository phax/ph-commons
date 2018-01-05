/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.impl.ICommonsSet;

/**
 * This is a facade for a {@link Set}. It may be used to wrap any kind of
 * {@link Set} and overwrite single methods, e.g. for logging.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        Element type
 */
@NotThreadSafe
public class WrappedSet <ELEMENTTYPE> implements ICommonsSet <ELEMENTTYPE>
{
  @CodingStyleguideUnaware
  private final Set <ELEMENTTYPE> m_aSrc;

  public WrappedSet (@Nonnull final Set <ELEMENTTYPE> aSet)
  {
    m_aSrc = ValueEnforcer.notNull (aSet, "Set");
  }

  @Nonnull
  @ReturnsMutableObject ("design")
  @CodingStyleguideUnaware
  protected Set <ELEMENTTYPE> directGetSource ()
  {
    return m_aSrc;
  }

  @Nonnull
  @ReturnsMutableCopy
  public WrappedSet <ELEMENTTYPE> getClone ()
  {
    return new WrappedSet <> (m_aSrc);
  }

  public boolean add (@Nullable final ELEMENTTYPE aElement)
  {
    return m_aSrc.add (aElement);
  }

  public boolean addAll (@Nonnull final Collection <? extends ELEMENTTYPE> aElements)
  {
    return m_aSrc.addAll (aElements);
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

  public boolean isEmpty ()
  {
    return m_aSrc.isEmpty ();
  }

  public Iterator <ELEMENTTYPE> iterator ()
  {
    return m_aSrc.iterator ();
  }

  public boolean remove (final Object aElement)
  {
    return m_aSrc.remove (aElement);
  }

  public boolean removeAll (final Collection <?> aElements)
  {
    return m_aSrc.removeAll (aElements);
  }

  public boolean retainAll (final Collection <?> aElements)
  {
    return m_aSrc.retainAll (aElements);
  }

  @Nonnegative
  public int size ()
  {
    return m_aSrc.size ();
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

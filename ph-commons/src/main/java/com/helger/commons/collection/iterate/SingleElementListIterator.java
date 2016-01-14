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

import java.util.ListIterator;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.UnsupportedOperation;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Specialized version of the {@link ListIterator} interface for a single
 * element.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The type of the element to be iterated.
 */
@Immutable
public class SingleElementListIterator <ELEMENTTYPE> implements ListIterator <ELEMENTTYPE>
{
  private boolean m_bHasNext;
  private final ELEMENTTYPE m_aElement;

  public SingleElementListIterator (@Nullable final ELEMENTTYPE aElement)
  {
    m_bHasNext = true;
    m_aElement = aElement;
  }

  @UnsupportedOperation
  public void add (final ELEMENTTYPE o)
  {
    throw new UnsupportedOperationException ();
  }

  public boolean hasNext ()
  {
    return m_bHasNext;
  }

  public boolean hasPrevious ()
  {
    return !m_bHasNext;
  }

  @Nullable
  public ELEMENTTYPE next ()
  {
    if (m_bHasNext)
    {
      m_bHasNext = false;
      return m_aElement;
    }
    throw new NoSuchElementException ();
  }

  public int nextIndex ()
  {
    return m_bHasNext ? 0 : 1;
  }

  public ELEMENTTYPE previous ()
  {
    if (!m_bHasNext)
    {
      m_bHasNext = true;
      return m_aElement;
    }
    throw new NoSuchElementException ();
  }

  public int previousIndex ()
  {
    return hasPrevious () ? 0 : -1;
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
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final SingleElementListIterator <?> rhs = (SingleElementListIterator <?>) o;
    return m_bHasNext == rhs.m_bHasNext && EqualsHelper.equals (m_aElement, rhs.m_aElement);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_bHasNext).append (m_aElement).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("hasNext", m_bHasNext).append ("element", m_aElement).toString ();
  }

  @Nonnull
  public static <T> ListIterator <T> create (@Nullable final T aElement)
  {
    return new SingleElementListIterator <T> (aElement);
  }
}

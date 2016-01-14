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
package com.helger.commons.collection.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.UnsupportedOperation;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.iterate.EmptyIterator;
import com.helger.commons.collection.iterate.EmptyListIterator;
import com.helger.commons.collection.iterate.SingleElementIterator;
import com.helger.commons.collection.iterate.SingleElementListIterator;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.string.ToStringGenerator;

/**
 * Implementation of the {@link List} interface handling exactly one element and
 * no more!
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The type of the element in the list
 */
@NotThreadSafe
public class SingleElementList <ELEMENTTYPE> implements List <ELEMENTTYPE>
{
  private boolean m_bHasElement;
  private ELEMENTTYPE m_aElement;

  public SingleElementList ()
  {
    m_bHasElement = false;
    m_aElement = null;
  }

  public SingleElementList (@Nullable final ELEMENTTYPE aElement)
  {
    m_bHasElement = true;
    m_aElement = aElement;
  }

  public boolean add (@Nullable final ELEMENTTYPE aElement)
  {
    if (m_bHasElement)
      throw new IllegalStateException ("List already contains an element");
    m_aElement = aElement;
    m_bHasElement = true;
    return true;
  }

  public void add (@Nonnegative final int nIndex, @Nullable final ELEMENTTYPE aElement)
  {
    if (nIndex != 0)
      throw new IllegalArgumentException ("Only 1 elements is allowed: " + nIndex);
    if (m_bHasElement)
      throw new IllegalStateException ("List already contains an element");
    m_aElement = aElement;
    m_bHasElement = true;
  }

  public boolean addAll (@Nonnull final Collection <? extends ELEMENTTYPE> aElements)
  {
    ValueEnforcer.notNull (aElements, "Elements");
    if (aElements.size () > 1)
      throw new IllegalArgumentException ("Cannot add lists with more than one element!");
    if (m_bHasElement)
      throw new IllegalStateException ("List already contains an element");
    final Iterator <? extends ELEMENTTYPE> i = aElements.iterator ();
    return i.hasNext () && add (i.next ());
  }

  public boolean addAll (@Nonnegative final int nIndex, @Nonnull final Collection <? extends ELEMENTTYPE> aElements)
  {
    if (nIndex != 0)
      throw new IndexOutOfBoundsException ("Only index 0 is valid!");
    return addAll (aElements);
  }

  public void clear ()
  {
    m_aElement = null;
    m_bHasElement = false;
  }

  public boolean contains (@Nullable final Object aElement)
  {
    return m_bHasElement && EqualsHelper.equals (m_aElement, aElement);
  }

  public boolean containsAll (@Nonnull final Collection <?> aElements)
  {
    for (final Object aElement : aElements)
      if (!contains (aElement))
        return false;
    return true;
  }

  public ELEMENTTYPE get (@Nonnegative final int nIndex)
  {
    if (!m_bHasElement || nIndex != 0)
      throw new IndexOutOfBoundsException ();
    return m_aElement;
  }

  public int indexOf (final Object aElement)
  {
    return contains (aElement) ? 0 : -1;
  }

  public boolean isEmpty ()
  {
    return !m_bHasElement;
  }

  @Nonnull
  public Iterator <ELEMENTTYPE> iterator ()
  {
    return m_bHasElement ? SingleElementIterator.create (m_aElement) : new EmptyIterator <ELEMENTTYPE> ();
  }

  public int lastIndexOf (@Nullable final Object aElement)
  {
    return contains (aElement) ? 0 : -1;
  }

  @Nonnull
  public ListIterator <ELEMENTTYPE> listIterator ()
  {
    return m_bHasElement ? SingleElementListIterator.create (m_aElement) : new EmptyListIterator <ELEMENTTYPE> ();
  }

  @Nonnull
  public ListIterator <ELEMENTTYPE> listIterator (final int nIndex)
  {
    if (!m_bHasElement || nIndex != 0)
      throw new IndexOutOfBoundsException ();
    return listIterator ();
  }

  @Nullable
  public ELEMENTTYPE remove (final int nIndex)
  {
    if (!m_bHasElement || nIndex != 0)
      throw new IndexOutOfBoundsException ();
    final ELEMENTTYPE aElement = m_aElement;
    m_aElement = null;
    m_bHasElement = false;
    return aElement;
  }

  public boolean remove (@Nullable final Object aElement)
  {
    if (!contains (aElement))
      return false;
    m_aElement = null;
    m_bHasElement = false;
    return true;
  }

  public boolean removeAll (@Nullable final Collection <?> aElements)
  {
    boolean bRemovedAll = true;
    if (aElements != null)
      for (final Object aElement : aElements)
        if (!remove (aElement))
          bRemovedAll = false;
    return bRemovedAll;
  }

  @UnsupportedOperation
  public boolean retainAll (@Nonnull final Collection <?> aElements)
  {
    ValueEnforcer.notNull (aElements, "Elements");
    if (m_bHasElement)
    {
      if (aElements.contains (m_aElement))
        return true;
      clear ();
    }
    return false;
  }

  @Nullable
  public ELEMENTTYPE set (@Nonnegative final int nIndex, @Nullable final ELEMENTTYPE aNewElement)
  {
    if (nIndex != 0)
      throw new IllegalArgumentException ("The passed index can onyl be 0!");
    final ELEMENTTYPE aOldElement = m_aElement;
    m_bHasElement = true;
    m_aElement = aNewElement;
    return aOldElement;
  }

  @Nonnegative
  public int size ()
  {
    return m_bHasElement ? 1 : 0;
  }

  @Nonnull
  public List <ELEMENTTYPE> subList (@Nonnegative final int nFromIndex, @Nonnegative final int nToIndex)
  {
    if (nFromIndex < 0 || nFromIndex > (m_bHasElement ? 1 : 0))
      throw new IndexOutOfBoundsException ("Invalid from index " + nFromIndex);
    if (nToIndex < 0 || nToIndex > (m_bHasElement ? 1 : 0))
      throw new IndexOutOfBoundsException ("Invalid to index " + nToIndex);

    // Empty
    if (!m_bHasElement || nFromIndex == nToIndex)
      return new ArrayList <ELEMENTTYPE> (0);

    return this;
  }

  @Nonnull
  public Object [] toArray ()
  {
    if (m_bHasElement)
    {
      final Object [] aObjects = new Object [1];
      aObjects[0] = m_aElement;
      return aObjects;
    }
    return ArrayHelper.EMPTY_OBJECT_ARRAY;
  }

  @Nonnull
  public <ARRAYELEMENTTYPE> ARRAYELEMENTTYPE [] toArray (@Nonnull final ARRAYELEMENTTYPE [] aDest)
  {
    ValueEnforcer.notNull (aDest, "Dest");

    if (!m_bHasElement)
      return aDest;
    if (m_aElement != null && !aDest.getClass ().getComponentType ().isAssignableFrom (m_aElement.getClass ()))
      throw new ArrayStoreException ("The array class " +
                                     aDest.getClass () +
                                     " cannot store the item of class " +
                                     m_aElement.getClass ());
    final ARRAYELEMENTTYPE [] ret = aDest.length < 1 ? ArrayHelper.newArraySameType (aDest, 1) : aDest;
    ret[0] = GenericReflection.<ELEMENTTYPE, ARRAYELEMENTTYPE> uncheckedCast (m_aElement);
    if (ret.length > 1)
      ret[1] = null;
    return ret;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final SingleElementList <?> rhs = (SingleElementList <?>) o;
    return m_bHasElement == rhs.m_bHasElement && EqualsHelper.equals (m_aElement, rhs.m_aElement);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_bHasElement).append (m_aElement).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("hasElement", m_bHasElement).append ("element", m_aElement).toString ();
  }

  @Nonnull
  public static <ELEMENTTYPE> SingleElementList <ELEMENTTYPE> create (@Nullable final ELEMENTTYPE aElement)
  {
    return new SingleElementList <ELEMENTTYPE> (aElement);
  }
}

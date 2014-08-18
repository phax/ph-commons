/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
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
package com.helger.commons.collections.list;

import java.util.Vector;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.IHasSize;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.MustImplementEqualsAndHashcode;
import com.helger.commons.factory.FactoryNull;
import com.helger.commons.factory.IFactory;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

// ESCA-JAVA0143:
/**
 * This is a specialized {@link Vector} that can handle read accesses on list
 * items that are not yet in the container. If {@link #get(int)} is called with
 * an index that would normally throw an {@link ArrayIndexOutOfBoundsException}
 * this class will fill all indices between the current {@link #size()} and the
 * desired index with values provided by an {@link IFactory}. If you don't pass
 * an {@link IFactory} in the constructor a default factory returning null
 * values is used.
 * 
 * @author Philip
 * @param <ELEMENTTYPE>
 *        The type of the elements in the list
 */
@ThreadSafe
public class SafeVector <ELEMENTTYPE> extends Vector <ELEMENTTYPE> implements IHasSize
{
  @MustImplementEqualsAndHashcode
  private final IFactory <ELEMENTTYPE> m_aFactory;

  public SafeVector ()
  {
    this (FactoryNull.<ELEMENTTYPE> getInstance ());
  }

  public SafeVector (@Nonnull final IFactory <ELEMENTTYPE> aFactory)
  {
    m_aFactory = ValueEnforcer.notNull (aFactory, "Factory");
  }

  private void _ensureSize (@Nonnegative final int nIndex)
  {
    // fill the gap
    while (size () <= nIndex)
      add (m_aFactory.create ());
  }

  @Override
  public synchronized ELEMENTTYPE get (@Nonnegative final int nIndex) // NOPMD
  {
    _ensureSize (nIndex);
    return super.get (nIndex);
  }

  @Override
  public synchronized ELEMENTTYPE set (@Nonnegative final int nIndex, @Nonnull final ELEMENTTYPE aElement) // NOPMD
  {
    _ensureSize (nIndex);
    return super.set (nIndex, aElement);
  }

  @Override
  public synchronized boolean equals (final Object o) // NOPMD
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    if (!super.equals (o))
      return false;
    final SafeVector <?> rhs = (SafeVector <?>) o;
    return m_aFactory.equals (rhs.m_aFactory);
  }

  @Override
  public synchronized int hashCode () // NOPMD
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_aFactory).getHashCode ();
  }

  @Override
  public synchronized String toString () // NOPMD
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("factory", m_aFactory).toString ();
  }
}

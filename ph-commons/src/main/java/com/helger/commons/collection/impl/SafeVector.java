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

import java.util.Vector;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.factory.IFactory;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

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
public class SafeVector <ELEMENTTYPE> extends Vector <ELEMENTTYPE>
{
  @MustImplementEqualsAndHashcode
  private final IFactory <ELEMENTTYPE> m_aFactory;

  /**
   * Constructor filling up the missing elements with <code>null</code> values.
   */
  public SafeVector ()
  {
    this ( () -> null);
  }

  /**
   * Constructor with a custom factory to fill the missing elements.
   *
   * @param aFactory
   *        The factory to use. May not be <code>null</code>.
   */
  public SafeVector (@Nonnull final IFactory <ELEMENTTYPE> aFactory)
  {
    m_aFactory = ValueEnforcer.notNull (aFactory, "Factory");
  }

  /**
   * @return The factory for filling missing values as provided in the
   *         constructor.
   */
  @Nonnull
  public IFactory <ELEMENTTYPE> getFactory ()
  {
    return m_aFactory;
  }

  private void _ensureSize (@Nonnegative final int nIndex)
  {
    // fill the gap
    while (size () <= nIndex)
      add (m_aFactory.get ());
  }

  @Override
  public synchronized ELEMENTTYPE get (@Nonnegative final int nIndex)
  {
    _ensureSize (nIndex);
    return super.get (nIndex);
  }

  @Override
  public synchronized ELEMENTTYPE set (@Nonnegative final int nIndex, @Nonnull final ELEMENTTYPE aElement)
  {
    _ensureSize (nIndex);
    return super.set (nIndex, aElement);
  }

  @Override
  public synchronized boolean equals (final Object o)
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
  public synchronized int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_aFactory).getHashCode ();
  }

  @Override
  public synchronized String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("factory", m_aFactory).toString ();
  }
}

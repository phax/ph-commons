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
package com.helger.collection.commons;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.array.ArrayHelper;
import com.helger.collection.CollectionHelper;

/**
 * A special {@link java.util.Set} implementation based on {@link ICommonsSet}, that compares the
 * elements by reference and not by {@link Object#equals(Object)}. It is backed by an
 * {@link IdentityHashMap} and behaves like
 * <code>Collections.newSetFromMap (new IdentityHashMap &lt;&gt; ())</code>, including
 * {@link #equals(Object)} and {@link #hashCode()}.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        Set element type
 * @since 12.6.0
 */
@NotThreadSafe
public class CommonsIdentityHashSet <ELEMENTTYPE> extends AbstractSet <ELEMENTTYPE> implements
                                    ICommonsSet <ELEMENTTYPE>,
                                    Serializable
{
  private final IdentityHashMap <ELEMENTTYPE, Boolean> m_aMap;

  /**
   * Create a new empty identity hash set with the default expected maximum size.
   */
  public CommonsIdentityHashSet ()
  {
    m_aMap = new IdentityHashMap <> ();
  }

  /**
   * Create a new empty identity hash set with the specified expected maximum size.
   *
   * @param nExpectedMaxSize
   *        The expected maximum number of elements. Must be &ge; 0.
   */
  public CommonsIdentityHashSet (@Nonnegative final int nExpectedMaxSize)
  {
    m_aMap = new IdentityHashMap <> (nExpectedMaxSize);
  }

  /**
   * Create a new identity hash set that contains the same elements as the provided collection.
   *
   * @param aCont
   *        The collection to copy the elements from. May be <code>null</code>.
   */
  public CommonsIdentityHashSet (@Nullable final Collection <? extends ELEMENTTYPE> aCont)
  {
    this (CollectionHelper.getSize (aCont));
    if (aCont != null)
      addAll (aCont);
  }

  /**
   * Create a new identity hash set with all elements of the provided iterable.
   *
   * @param aIterable
   *        The iterable from which the elements are copied from. May be <code>null</code>.
   */
  public CommonsIdentityHashSet (@Nullable final Iterable <? extends ELEMENTTYPE> aIterable)
  {
    this ();
    addAll (aIterable);
  }

  /**
   * Create a new identity hash set with all mapped elements of the provided iterable.
   *
   * @param aValues
   *        The iterable from which the elements are copied from. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May not be
   *        <code>null</code>.
   * @param <SRCTYPE>
   *        source data type
   */
  public <SRCTYPE> CommonsIdentityHashSet (@Nullable final Iterable <? extends SRCTYPE> aValues,
                                           @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    this ();
    addAllMapped (aValues, aMapper);
  }

  /**
   * Create a new identity hash set with exactly the provided value, even if it is
   * <code>null</code>.
   *
   * @param aValue
   *        The value to be added. May be <code>null</code>.
   */
  public CommonsIdentityHashSet (@Nullable final ELEMENTTYPE aValue)
  {
    this (1);
    add (aValue);
  }

  /**
   * Create a new identity hash set that contains the same elements as the provided array.
   *
   * @param aValues
   *        The array to copy the elements from. May be <code>null</code>.
   */
  @SafeVarargs
  public CommonsIdentityHashSet (@Nullable final ELEMENTTYPE... aValues)
  {
    this (ArrayHelper.getSize (aValues));
    addAll (aValues);
  }

  /**
   * Create a new identity hash set that contains mapped elements of the provided array.
   *
   * @param aValues
   *        The array to copy the elements from. May be <code>null</code>.
   * @param aMapper
   *        The mapping function to be executed for all provided elements. May not be
   *        <code>null</code>.
   * @param <SRCTYPE>
   *        source data type
   */
  public <SRCTYPE> CommonsIdentityHashSet (@Nullable final SRCTYPE [] aValues,
                                           @NonNull final Function <? super SRCTYPE, ? extends ELEMENTTYPE> aMapper)
  {
    this (ArrayHelper.getSize (aValues));
    addAllMapped (aValues, aMapper);
  }

  @Override
  @Nonnegative
  public int size ()
  {
    return m_aMap.size ();
  }

  @Override
  public boolean isEmpty ()
  {
    return m_aMap.isEmpty ();
  }

  @Override
  public boolean contains (@Nullable final Object aObj)
  {
    return m_aMap.containsKey (aObj);
  }

  @Override
  public boolean add (@Nullable final ELEMENTTYPE aElement)
  {
    return m_aMap.put (aElement, Boolean.TRUE) == null;
  }

  @Override
  public boolean remove (@Nullable final Object aObj)
  {
    return m_aMap.remove (aObj) != null;
  }

  @Override
  public void clear ()
  {
    m_aMap.clear ();
  }

  @Override
  @NonNull
  public Iterator <ELEMENTTYPE> iterator ()
  {
    return m_aMap.keySet ().iterator ();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NonNull
  @ReturnsMutableCopy
  public <T> CommonsIdentityHashSet <T> createInstance ()
  {
    return new CommonsIdentityHashSet <> ();
  }

  /**
   * @return A mutable copy of this set. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public CommonsIdentityHashSet <ELEMENTTYPE> getClone ()
  {
    return new CommonsIdentityHashSet <> (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    // Same as Collections.newSetFromMap
    return o == this || m_aMap.keySet ().equals (o);
  }

  @Override
  public int hashCode ()
  {
    // Uses the identity hash codes of the elements
    return m_aMap.keySet ().hashCode ();
  }
}

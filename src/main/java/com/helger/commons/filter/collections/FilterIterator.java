/**
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
package com.helger.commons.filter.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.UnsupportedOperation;
import com.helger.commons.collections.iterate.IIterableIterator;
import com.helger.commons.filter.IFilter;
import com.helger.commons.string.ToStringGenerator;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A simple filter iterator that takes a base iterator and an additional filter
 * and returns only the items that match the filter.
 * 
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The type to iterate
 */
public final class FilterIterator <ELEMENTTYPE> implements IIterableIterator <ELEMENTTYPE>
{
  // base iterator
  private final Iterator <? extends ELEMENTTYPE> m_aBaseIter;

  // current result value
  @SuppressFBWarnings ("UWF_NULL_FIELD")
  private ELEMENTTYPE m_aCurrent;

  // the filter to use
  private final IFilter <ELEMENTTYPE> m_aFilter;

  /**
   * Constructor.
   * 
   * @param aBaseIter
   *        The base iterable iterator to use. May not be <code>null</code>.
   * @param aFilter
   *        The filter to be applied. May not be <code>null</code>.
   */
  public FilterIterator (@Nonnull final IIterableIterator <? extends ELEMENTTYPE> aBaseIter,
                         @Nonnull final IFilter <ELEMENTTYPE> aFilter)
  {
    this (aBaseIter.iterator (), aFilter);
  }

  /**
   * Constructor.
   * 
   * @param aBaseIter
   *        The base iterator to use. May not be <code>null</code>.
   * @param aFilter
   *        The filter to be applied. May not be <code>null</code>.
   */
  public FilterIterator (@Nonnull final Iterator <? extends ELEMENTTYPE> aBaseIter,
                         @Nonnull final IFilter <ELEMENTTYPE> aFilter)
  {
    m_aBaseIter = ValueEnforcer.notNull (aBaseIter, "BaseIterator");
    m_aFilter = ValueEnforcer.notNull (aFilter, "Filter");
    _gotoNextCurrent ();
  }

  /**
   * Constructor.
   * 
   * @param aBaseCont
   *        The collection to iterate. May not be <code>null</code>.
   * @param aFilter
   *        The filter to be applied. May not be <code>null</code>.
   */
  public FilterIterator (@Nonnull final Iterable <? extends ELEMENTTYPE> aBaseCont,
                         @Nonnull final IFilter <ELEMENTTYPE> aFilter)
  {
    ValueEnforcer.notNull (aBaseCont, "BaseContainer");
    m_aBaseIter = aBaseCont.iterator ();
    m_aFilter = ValueEnforcer.notNull (aFilter, "Filter");
    _gotoNextCurrent ();
  }

  private void _gotoNextCurrent ()
  {
    m_aCurrent = null;
    while (m_aBaseIter.hasNext ())
    {
      final ELEMENTTYPE aTmp = m_aBaseIter.next ();
      if (m_aFilter.matchesFilter (aTmp))
      {
        m_aCurrent = aTmp;
        break;
      }
    }
  }

  public boolean hasNext ()
  {
    return m_aCurrent != null;
  }

  public ELEMENTTYPE next ()
  {
    if (!hasNext ())
      throw new NoSuchElementException ();
    final ELEMENTTYPE aRet = m_aCurrent;
    _gotoNextCurrent ();
    return aRet;
  }

  @UnsupportedOperation
  public void remove ()
  {
    throw new UnsupportedOperationException ();
  }

  @Nonnull
  public Iterator <ELEMENTTYPE> iterator ()
  {
    return this;
  }

  // equals and hashCode wont work, because standard Java iterators don't
  // implement this!

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("baseIter", m_aBaseIter).append ("filter", m_aFilter).toString ();
  }
}

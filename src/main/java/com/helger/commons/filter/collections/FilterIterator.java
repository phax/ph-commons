/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;

import com.helger.commons.collections.iterate.IIterableIterator;
import com.helger.commons.filter.IFilter;

/**
 * A simple filter iterator that takes a base iterator and an additional filter
 * and returns only the items that match the filter.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The type to iterate
 * @deprecated Use the class from the other package.
 */
@Deprecated
public final class FilterIterator <ELEMENTTYPE> extends
                                                com.helger.commons.collections.filter.FilterIterator <ELEMENTTYPE>
{
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
    super (aBaseIter, aFilter);
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
    super (aBaseIter, aFilter);
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
    super (aBaseCont, aFilter);
  }
}

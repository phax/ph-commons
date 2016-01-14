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
package com.helger.commons.filter;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * An implementation of {@link IFilter} that chains multiple instances of
 * {@link IFilter} with an <b>OR</b> operator.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type to be filtered.
 */
@Immutable
public class FilterListAny <DATATYPE> implements IFilter <DATATYPE>
{
  private final List <? extends IFilter <? super DATATYPE>> m_aFilters;

  @SafeVarargs
  public FilterListAny (@Nullable final IFilter <? super DATATYPE>... aFilters)
  {
    m_aFilters = CollectionHelper.newList (aFilters);
  }

  public FilterListAny (@Nullable final Iterable <? extends IFilter <? super DATATYPE>> aFilters)
  {
    m_aFilters = CollectionHelper.newList (aFilters);
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <? extends IFilter <? super DATATYPE>> getAllContainedFilters ()
  {
    return CollectionHelper.newList (m_aFilters);
  }

  public boolean test (@Nullable final DATATYPE aValue)
  {
    for (final IFilter <? super DATATYPE> aFilter : m_aFilters)
      if (aFilter.test (aValue))
        return true;
    return false;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final FilterListAny <?> rhs = (FilterListAny <?>) o;
    return m_aFilters.equals (rhs.m_aFilters);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aFilters).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("filters", m_aFilters).toString ();
  }

  @Nonnull
  @SafeVarargs
  public static <DATATYPE> FilterListAny <DATATYPE> create (@Nullable final IFilter <? super DATATYPE>... aFilters)
  {
    return new FilterListAny <DATATYPE> (aFilters);
  }

  @Nonnull
  public static <DATATYPE> FilterListAny <DATATYPE> create (@Nullable final Iterable <? extends IFilter <? super DATATYPE>> aFilters)
  {
    return new FilterListAny <DATATYPE> (aFilters);
  }
}

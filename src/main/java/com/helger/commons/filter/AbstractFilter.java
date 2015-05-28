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
package com.helger.commons.filter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.equals.EqualsUtils;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * An abstract implementation of {@link IFilter} that has an optional nested
 * filter.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type of object to filter.
 */
@NotThreadSafe
public abstract class AbstractFilter <DATATYPE> implements IFilter <DATATYPE>
{
  private EFilterMatchingStrategy m_eMatchingStrategy = EFilterMatchingStrategy.MATCH_ANY;
  private final IFilter <? super DATATYPE> m_aNestedFilter;

  /**
   * Constructor.
   *
   * @param aNestedFilter
   *        The nested filter to use. May be <code>null</code> to not have a
   *        nested filter-
   */
  public AbstractFilter (@Nullable final IFilter <? super DATATYPE> aNestedFilter)
  {
    m_aNestedFilter = aNestedFilter;
  }

  /**
   * @return The filter matching strategy as provided in the constructor. Never
   *         <code>null</code>. By default the "match any" strategy is used.
   */
  @Nonnull
  public final EFilterMatchingStrategy getMatchingStrategy ()
  {
    return m_eMatchingStrategy;
  }

  /**
   * Set the filter matching strategy.
   *
   * @param eMatchingStrategy
   *        The matching strategy to be used. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public final AbstractFilter <DATATYPE> setMatchingStrategy (@Nonnull final EFilterMatchingStrategy eMatchingStrategy)
  {
    m_eMatchingStrategy = ValueEnforcer.notNull (eMatchingStrategy, "MatchingStrategy");
    return this;
  }

  /**
   * @return The nested filter as specified in the constructor. May be
   *         <code>null</code>.
   */
  @Nullable
  public final IFilter <? super DATATYPE> getNestedFilter ()
  {
    return m_aNestedFilter;
  }

  /**
   * This is the method to be implemented to match this filter.
   *
   * @param aValue
   *        The value to be matched
   * @return <code>true</code> if the value matches the filter
   */
  public abstract boolean matchesThisFilter (final DATATYPE aValue);

  public final boolean matchesFilter (final DATATYPE aValue)
  {
    final boolean bIsMatchAny = m_eMatchingStrategy.equals (EFilterMatchingStrategy.MATCH_ANY);
    final boolean bIsMatchAll = !bIsMatchAny;

    // Match this filter
    final boolean bMatchesThisFilter = matchesThisFilter (aValue);
    if (bMatchesThisFilter && bIsMatchAny)
      return true;
    if (!bMatchesThisFilter && bIsMatchAll)
      return false;

    // Check nested filter (if present)
    if (m_aNestedFilter == null)
      return bMatchesThisFilter;

    final boolean bMatchesNestedFilter = m_aNestedFilter.matchesFilter (aValue);
    return bMatchesNestedFilter;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final AbstractFilter <?> rhs = (AbstractFilter <?>) o;
    return EqualsUtils.equals (m_aNestedFilter, rhs.m_aNestedFilter);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aNestedFilter).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).appendIfNotNull ("nestedFilter", m_aNestedFilter).toString ();
  }
}

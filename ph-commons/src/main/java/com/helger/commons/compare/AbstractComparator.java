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
package com.helger.commons.compare;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.string.ToStringGenerator;

/**
 * Abstract comparator class that supports a sort order and a nested comparator.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The data type to be compared
 */
@NotThreadSafe
public abstract class AbstractComparator <DATATYPE> implements ISerializableComparator <DATATYPE>
{
  private boolean m_bNullValuesComeFirst = CompareHelper.DEFAULT_NULL_VALUES_COME_FIRST;

  /**
   * Comparator with default sort order and no nested comparator.
   */
  public AbstractComparator ()
  {}

  /**
   * @return <code>true</code> if <code>null</code> values are to be ordered
   *         before non-<code>null</code> values, <code>false</code> if
   *         <code>null</code> are to be sorted after non-<code>null</code>
   *         values.
   */
  public final boolean isNullValuesComeFirst ()
  {
    return m_bNullValuesComeFirst;
  }

  /**
   * Change the sort position of <code>null</code> values.
   *
   * @param bNullValuesComeFirst
   *        <code>true</code> if <code>null</code> values should come first,
   *        <code>false</code> if <code>null</code> values should go last.
   * @return this
   */
  @Nonnull
  public final AbstractComparator <DATATYPE> setNullValuesComeFirst (final boolean bNullValuesComeFirst)
  {
    m_bNullValuesComeFirst = bNullValuesComeFirst;
    return this;
  }

  /**
   * @param aElement1
   *        First element to compare. Never <code>null</code>.
   * @param aElement2
   *        Second element to compare. Never <code>null</code>.
   * @return a negative integer, zero, or a positive integer as the first
   *         argument is less than, equal to, or greater than the second.
   */
  protected abstract int mainCompare (@Nonnull final DATATYPE aElement1, @Nonnull final DATATYPE aElement2);

  public final int compare (final DATATYPE aElement1, final DATATYPE aElement2)
  {
    int nCompare;
    if (aElement1 == aElement2)
    {
      // The same object
      nCompare = 0;
    }
    else
      if (aElement1 == null)
      {
        // First element is null
        nCompare = m_bNullValuesComeFirst ? -1 : +1;
      }
      else
        if (aElement2 == null)
        {
          // Second element is null
          nCompare = m_bNullValuesComeFirst ? +1 : -1;
        }
        else
        {
          // Both are not null - compare
          nCompare = mainCompare (aElement1, aElement2);
        }

    return nCompare;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("nullValuesComeFirst", m_bNullValuesComeFirst).toString ();
  }
}

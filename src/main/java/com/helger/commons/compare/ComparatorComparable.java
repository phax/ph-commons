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
package com.helger.commons.compare;

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.string.ToStringGenerator;

/**
 * This is another *lol* class: a {@link Comparator} for {@link Comparable}
 * objects.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        Data type to compare
 */
@NotThreadSafe
public class ComparatorComparable <DATATYPE extends Comparable <? super DATATYPE>> extends AbstractComparator <DATATYPE>
{
  private boolean m_bNullValuesComeFirst = CompareUtils.DEFAULT_NULL_VALUES_COME_FIRST;

  /**
   * Comparator with default sort order and no nested comparator.
   */
  public ComparatorComparable ()
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
  public final ComparatorComparable <DATATYPE> setNullValuesComeFirst (final boolean bNullValuesComeFirst)
  {
    m_bNullValuesComeFirst = bNullValuesComeFirst;
    return this;
  }

  @Override
  protected final int mainCompare (@Nullable final DATATYPE aElement1, @Nullable final DATATYPE aElement2)
  {
    return CompareUtils.nullSafeCompare (aElement1, aElement2, m_bNullValuesComeFirst);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("nullValuesComeFirst", m_bNullValuesComeFirst)
                            .toString ();
  }
}

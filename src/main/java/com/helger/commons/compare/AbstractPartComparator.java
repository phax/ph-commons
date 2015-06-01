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

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

/**
 * This class is an {@link AbstractComparator} that extracts a certain data
 * element from the main object to compare.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The data type to be compared
 * @param <PARTTYPE>
 *        The part type that is extracted from the data element and compared
 */
@NotThreadSafe
public abstract class AbstractPartComparator <DATATYPE, PARTTYPE> extends AbstractComparator <DATATYPE>
{
  private final Comparator <? super PARTTYPE> m_aPartComparator;
  private boolean m_bNullValuesComeFirst = CompareUtils.DEFAULT_NULL_VALUES_COME_FIRST;

  /**
   * Comparator with default sort order and a part comparator.
   *
   * @param aPartComparator
   *        The comparator for comparing the extracted parts. May not be
   *        <code>null</code>.
   */
  public AbstractPartComparator (@Nonnull final Comparator <? super PARTTYPE> aPartComparator)
  {
    m_aPartComparator = ValueEnforcer.notNull (aPartComparator, "PartComparator");
  }

  /**
   * @return The part comparator specified in the constructor. May be
   *         <code>null</code>.
   */
  @Nonnull
  public final Comparator <? super PARTTYPE> getPartComparator ()
  {
    return m_aPartComparator;
  }

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
  public final AbstractPartComparator <DATATYPE, PARTTYPE> setNullValuesComeFirst (final boolean bNullValuesComeFirst)
  {
    m_bNullValuesComeFirst = bNullValuesComeFirst;
    return this;
  }

  /**
   * Implement this method to extract the part to compare from the original
   * object.
   *
   * @param aObject
   *        The object to be compared. May be <code>null</code> depending on the
   *        implementation.
   * @return The part of the source object to be compared with the part
   *         comparator provided in the constructor. May be <code>null</code>.
   */
  @Nullable
  protected abstract PARTTYPE getPart (DATATYPE aObject);

  @Override
  protected final int mainCompare (final DATATYPE aElement1, final DATATYPE aElement2)
  {
    final PARTTYPE aPart1 = getPart (aElement1);
    final PARTTYPE aPart2 = getPart (aElement2);
    return CompareUtils.nullSafeCompare (aPart1, aPart2, m_aPartComparator, m_bNullValuesComeFirst);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("partComparator", m_aPartComparator)
                            .append ("nullValuesComeFirst", m_bNullValuesComeFirst)
                            .toString ();
  }
}

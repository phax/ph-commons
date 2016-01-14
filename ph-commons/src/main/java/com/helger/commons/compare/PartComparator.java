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

import java.util.Comparator;
import java.util.function.Function;

import javax.annotation.Nonnull;
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
public class PartComparator <DATATYPE, PARTTYPE> extends AbstractComparator <DATATYPE>
{
  private final Comparator <? super PARTTYPE> m_aPartComparator;
  private final Function <DATATYPE, PARTTYPE> m_aExtractor;

  /**
   * Comparator with default sort order and a part comparator.
   *
   * @param aPartComparator
   *        The comparator for comparing the extracted parts. May not be
   *        <code>null</code>.
   * @param aExtractor
   *        Part extractor function. May not be <code>null</code>.
   */
  public PartComparator (@Nonnull final Comparator <? super PARTTYPE> aPartComparator,
                         @Nonnull final Function <DATATYPE, PARTTYPE> aExtractor)
  {
    m_aPartComparator = ValueEnforcer.notNull (aPartComparator, "PartComparator");
    m_aExtractor = ValueEnforcer.notNull (aExtractor, "Extractor");
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
   * @return The extractor function specified in the constructor. May be
   *         <code>null</code>.
   */
  @Nonnull
  public final Function <DATATYPE, PARTTYPE> getExtractor ()
  {
    return m_aExtractor;
  }

  @Override
  protected final int mainCompare (@Nonnull final DATATYPE aElement1, @Nonnull final DATATYPE aElement2)
  {
    final PARTTYPE aPart1 = m_aExtractor.apply (aElement1);
    final PARTTYPE aPart2 = m_aExtractor.apply (aElement2);

    // The extracted parts may be null again so use check order of null values
    return CompareHelper.compare (aPart1, aPart2, m_aPartComparator, isNullValuesComeFirst ());
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("PartComparator", m_aPartComparator)
                            .append ("Extractor", m_aExtractor)
                            .toString ();
  }
}

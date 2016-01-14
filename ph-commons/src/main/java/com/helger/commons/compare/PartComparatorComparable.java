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

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;

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
public abstract class PartComparatorComparable <DATATYPE, PARTTYPE extends Comparable <? super PARTTYPE>>
                                               extends AbstractComparator <DATATYPE>
{
  private final Function <DATATYPE, PARTTYPE> m_aExtractor;

  /**
   * Comparator with default sort order.
   *
   * @param aExtractor
   *        Part extractor function. May not be <code>null</code>.
   */
  public PartComparatorComparable (@Nonnull final Function <DATATYPE, PARTTYPE> aExtractor)
  {
    m_aExtractor = ValueEnforcer.notNull (aExtractor, "Extractor");
  }

  @Override
  protected final int mainCompare (@Nonnull final DATATYPE aElement1, @Nonnull final DATATYPE aElement2)
  {
    final PARTTYPE aPart1 = m_aExtractor.apply (aElement1);
    final PARTTYPE aPart2 = m_aExtractor.apply (aElement2);

    // The extracted parts may be null again so use check order of null values
    return CompareHelper.compare (aPart1, aPart2, isNullValuesComeFirst ());
  }
}

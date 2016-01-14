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

import java.math.BigDecimal;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;

/**
 * Abstract comparator that handles values that can be represented as BigDecimal
 * values.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The data type to be compared. Must somehow have a value that can be
 *        compared as a BigDecimal value.
 */
public class BigDecimalComparator <DATATYPE> extends AbstractComparator <DATATYPE>
{
  private final Function <DATATYPE, BigDecimal> m_aExtractor;

  public BigDecimalComparator (@Nonnull final Function <DATATYPE, BigDecimal> aExtractor)
  {
    m_aExtractor = ValueEnforcer.notNull (aExtractor, "Extractor");
  }

  @Override
  protected final int mainCompare (@Nonnull final DATATYPE aElement1, @Nonnull final DATATYPE aElement2)
  {
    final BigDecimal aBD1 = m_aExtractor.apply (aElement1);
    final BigDecimal aBD2 = m_aExtractor.apply (aElement2);
    return CompareHelper.compare (aBD1, aBD2, isNullValuesComeFirst ());
  }
}

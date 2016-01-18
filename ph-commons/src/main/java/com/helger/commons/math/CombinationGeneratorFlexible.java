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
package com.helger.commons.math;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;

/**
 * Utility class for generating all possible combinations of elements for a
 * specified number of available slots. Duplicates in the passed elements will
 * not deliver duplicate result solutions. This implementation is flexible in
 * terms of handling the slots. This means it will also return result sets where
 * not all slots are filled.
 *
 * @author Boris Gregorcic
 * @author Philip Helger
 * @param <DATATYPE>
 *        Element type
 */
@Immutable
public final class CombinationGeneratorFlexible <DATATYPE>
{
  private final boolean m_bAllowEmpty;
  private final int m_nSlotCount;

  /**
   * Ctor
   *
   * @param nSlotCount
   *        the number of slots to use
   * @param bAllowEmpty
   *        whether or not to include an empty result set (no slot filled)
   */
  public CombinationGeneratorFlexible (@Nonnegative final int nSlotCount, final boolean bAllowEmpty)
  {
    ValueEnforcer.isGE0 (nSlotCount, "SlotCount");
    m_nSlotCount = nSlotCount;
    m_bAllowEmpty = bAllowEmpty;
  }

  /**
   * Iterate all combination, no matter they are unique or not.
   *
   * @param aElements
   *        List of elements.
   * @param aCallback
   *        Callback to invoke
   */
  public void iterateAllCombinations (@Nonnull final List <DATATYPE> aElements,
                                      @Nonnull final Consumer <List <DATATYPE>> aCallback)
  {
    ValueEnforcer.notNull (aElements, "Elements");
    ValueEnforcer.notNull (aCallback, "Callback");

    for (int nSlotCount = m_bAllowEmpty ? 0 : 1; nSlotCount <= m_nSlotCount; nSlotCount++)
    {
      if (aElements.isEmpty ())
      {
        aCallback.accept (new ArrayList <> ());
      }
      else
      {
        // Add all permutations for the current slot count
        for (final List <DATATYPE> aPermutation : new CombinationGenerator <> (aElements, nSlotCount))
          aCallback.accept (aPermutation);
      }
    }
  }

  /**
   * Generate all combinations without duplicates.
   *
   * @param aElements
   *        the elements to distribute to the specified slots (may be empty!)
   * @return a set of slot allocations representing all possible combinations
   */
  @Nonnull
  @ReturnsMutableCopy
  public Set <List <DATATYPE>> getCombinations (@Nonnull final List <DATATYPE> aElements)
  {
    ValueEnforcer.notNull (aElements, "Elements");

    final Set <List <DATATYPE>> aAllResults = new HashSet <List <DATATYPE>> ();
    iterateAllCombinations (aElements, aCurrentObject -> aAllResults.add (aCurrentObject));
    return aAllResults;
  }

  public static <DATATYPE> void iterateAllCombinations (@Nonnull final List <DATATYPE> aElements,
                                                        final boolean bAllowEmpty,
                                                        @Nonnull final Consumer <List <DATATYPE>> aCallback)
  {
    new CombinationGeneratorFlexible <DATATYPE> (aElements.size (), bAllowEmpty).iterateAllCombinations (aElements,
                                                                                                         aCallback);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static <DATATYPE> Set <List <DATATYPE>> getCombinations (@Nonnull final List <DATATYPE> aElements,
                                                                  final boolean bAllowEmpty)
  {
    return new CombinationGeneratorFlexible <DATATYPE> (aElements.size (), bAllowEmpty).getCombinations (aElements);
  }
}

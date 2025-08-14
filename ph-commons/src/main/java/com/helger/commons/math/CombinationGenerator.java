/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import java.math.BigInteger;
import java.util.Collection;
import java.util.NoSuchElementException;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.CGlobal;
import com.helger.base.enforcer.ValueEnforcer;
import com.helger.base.lang.GenericReflection;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.iterate.IIterableIterator;

import jakarta.annotation.Nonnull;

/**
 * Utility class for generating all possible combinations of elements for a
 * specified number of available slots. Duplicates in the passed elements will
 * be treated as different individuals and hence deliver duplicate result
 * solutions. This generator will only return complete result sets filling all
 * slots.
 *
 * @author Boris Gregorcic
 * @author Philip Helger
 * @param <DATATYPE>
 *        Element type to be combined
 */
public class CombinationGenerator <DATATYPE> implements IIterableIterator <ICommonsList <DATATYPE>>
{
  private final DATATYPE [] m_aElements;
  private final int [] m_aIndexResult;
  private final BigInteger m_aTotalCombinations;
  private BigInteger m_aCombinationsLeft;
  private final boolean m_bUseLong;
  private final long m_nTotalCombinations;
  private long m_nCombinationsLeft;

  /**
   * Ctor
   *
   * @param aElements
   *        the elements to fill into the slots for creating all combinations
   *        (must not be empty!)
   * @param nSlotCount
   *        the number of slots to use (must not be greater than the element
   *        count!)
   */
  public CombinationGenerator (@Nonnull @Nonempty final ICommonsList <DATATYPE> aElements, @Nonnegative final int nSlotCount)
  {
    ValueEnforcer.notEmpty (aElements, "Elements");
    ValueEnforcer.isBetweenInclusive (nSlotCount, "SlotCount", 0, aElements.size ());

    m_aElements = GenericReflection.uncheckedCast (aElements.toArray ());
    m_aIndexResult = new int [nSlotCount];
    final BigInteger aElementFactorial = FactorialHelper.getAnyFactorialLinear (m_aElements.length);
    final BigInteger aSlotFactorial = FactorialHelper.getAnyFactorialLinear (nSlotCount);
    final BigInteger aOverflowFactorial = FactorialHelper.getAnyFactorialLinear (m_aElements.length - nSlotCount);
    m_aTotalCombinations = aElementFactorial.divide (aSlotFactorial.multiply (aOverflowFactorial));
    // Can we use the fallback to long? Is much faster than using BigInteger
    m_bUseLong = m_aTotalCombinations.compareTo (CGlobal.BIGINT_MAX_LONG) < 0;
    m_nTotalCombinations = m_bUseLong ? m_aTotalCombinations.longValue () : CGlobal.ILLEGAL_ULONG;
    reset ();
  }

  /**
   * Reset the generator
   */
  public final void reset ()
  {
    for (int i = 0; i < m_aIndexResult.length; i++)
      m_aIndexResult[i] = i;
    m_aCombinationsLeft = m_aTotalCombinations;
    m_nCombinationsLeft = m_nTotalCombinations;
  }

  /**
   * @return number of combinations not yet generated
   */
  @Nonnull
  public BigInteger getCombinationsLeft ()
  {
    return m_bUseLong ? BigInteger.valueOf (m_nCombinationsLeft) : m_aCombinationsLeft;
  }

  /**
   * @return whether or not there are more combinations left
   */
  public boolean hasNext ()
  {
    return m_bUseLong ? m_nCombinationsLeft > 0 : m_aCombinationsLeft.compareTo (BigInteger.ZERO) > 0;
  }

  /**
   * @return total number of combinations
   */
  @Nonnull
  public BigInteger getTotalCombinations ()
  {
    return m_aTotalCombinations;
  }

  /**
   * Generate next combination (algorithm from Rosen p. 286)
   *
   * @return the next combination as list of the size specified for slots filled
   *         with elements from the original list
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <DATATYPE> next ()
  {
    if (!hasNext ())
      throw new NoSuchElementException ();

    // Not for the very first item, as the first item is the original order
    final boolean bFirstItem = m_bUseLong ? m_nCombinationsLeft == m_nTotalCombinations : m_aCombinationsLeft.equals (m_aTotalCombinations);
    if (!bFirstItem)
    {
      final int nElementCount = m_aElements.length;
      final int nSlotCount = m_aIndexResult.length;

      int i = nSlotCount - 1;
      while (m_aIndexResult[i] == (nElementCount - nSlotCount + i))
      {
        i--;
      }
      m_aIndexResult[i]++;
      final int nIndexResultI = m_aIndexResult[i];
      for (int j = i + 1; j < nSlotCount; j++)
      {
        m_aIndexResult[j] = nIndexResultI + j - i;
      }
    }

    // One combination less
    if (m_bUseLong)
      m_nCombinationsLeft--;
    else
      m_aCombinationsLeft = m_aCombinationsLeft.subtract (BigInteger.ONE);

    // Build result list
    final ICommonsList <DATATYPE> aResult = new CommonsArrayList <> (m_aIndexResult.length);
    for (final int nIndex : m_aIndexResult)
      aResult.add (m_aElements[nIndex]);
    return aResult;
  }

  /**
   * Get a list of all permutations of the input elements.
   *
   * @param <DATATYPE>
   *        Element type to be combined
   * @param aInput
   *        Input list.
   * @param nSlotCount
   *        Slot count.
   * @return The list of all permutations. Beware: the resulting list may be
   *         quite large and may contain duplicates if the input list contains
   *         duplicate elements!
   */
  @Nonnull
  public static <DATATYPE> ICommonsList <ICommonsList <DATATYPE>> getAllPermutations (@Nonnull @Nonempty final ICommonsList <DATATYPE> aInput,
                                                                                      @Nonnegative final int nSlotCount)
  {
    final ICommonsList <ICommonsList <DATATYPE>> aResultList = new CommonsArrayList <> ();
    addAllPermutations (aInput, nSlotCount, aResultList);
    return aResultList;
  }

  /**
   * Fill a list with all permutations of the input elements.
   *
   * @param <DATATYPE>
   *        Element type to be combined
   * @param aInput
   *        Input list.
   * @param nSlotCount
   *        Slot count.
   * @param aResultList
   *        The list to be filled with all permutations. Beware: this list may
   *        be quite large and may contain duplicates if the input list contains
   *        duplicate elements! Note: this list is not cleared before filling
   */
  public static <DATATYPE> void addAllPermutations (@Nonnull @Nonempty final ICommonsList <DATATYPE> aInput,
                                                    @Nonnegative final int nSlotCount,
                                                    @Nonnull final Collection <ICommonsList <DATATYPE>> aResultList)
  {
    for (final ICommonsList <DATATYPE> aPermutation : new CombinationGenerator <> (aInput, nSlotCount))
      aResultList.add (aPermutation);
  }
}

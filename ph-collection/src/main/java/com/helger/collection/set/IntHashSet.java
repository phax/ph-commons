/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.collection.set;

import java.util.Arrays;
import java.util.function.IntConsumer;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.clone.ICloneable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.iface.IHasSize;
import com.helger.base.tostring.ToStringGenerator;

/**
 * A hash set of primitive <code>int</code> values using open addressing with linear probing. No
 * value is ever boxed. The hashing and probing approach is the same as in
 * {@link com.helger.collection.map.IntIntMap}: the value <code>0</code> is used as the marker for
 * free slots and is therefore stored in a separate field. In contrast to {@link IntSet} no value
 * array is needed.
 *
 * @author Philip Helger
 * @since 12.6.0
 */
@NotThreadSafe
public class IntHashSet implements IHasSize, ICloneable <IntHashSet>
{
  private static final int FREE_KEY = 0;
  // taken from FastUtil
  private static final int INT_PHI = 0x9E3779B9;
  private static final float DEFAULT_FILL_FACTOR = 0.75f;

  /** Keys */
  private int [] m_aKeys;
  /** Do we have 'free' key in the set? */
  private boolean m_bHasFreeKey;
  /** Fill factor, must be between (0 and 1) */
  private final float m_fFillFactor;
  /** We will resize the set once it reaches this size */
  private int m_nThreshold;
  /** Current set size */
  private int m_nSize;
  /** Mask to calculate the original position */
  private int m_nMask;

  /**
   * Constructor with default initial capacity and fill factor.
   */
  public IntHashSet ()
  {
    this (16);
  }

  /**
   * Constructor with a custom expected number of elements.
   *
   * @param nSize
   *        The expected number of elements. Must be &gt; 0.
   */
  public IntHashSet (final int nSize)
  {
    this (nSize, DEFAULT_FILL_FACTOR);
  }

  /**
   * Constructor with a custom expected number of elements and fill factor.
   *
   * @param nSize
   *        The expected number of elements. Must be &gt; 0.
   * @param fFillFactor
   *        The fill factor. Must be between 0 and 1 (inclusive).
   */
  public IntHashSet (final int nSize, final float fFillFactor)
  {
    ValueEnforcer.isBetweenInclusive (fFillFactor, "FillFactor", 0f, 1f);
    ValueEnforcer.isGT0 (nSize, "Size");
    final int nCapacity = _getArraySize (nSize, fFillFactor);
    m_nMask = nCapacity - 1;
    m_fFillFactor = fFillFactor;

    m_aKeys = new int [nCapacity];
    m_nThreshold = (int) (nCapacity * fFillFactor);
  }

  /**
   * Copy constructor.
   *
   * @param aOther
   *        The set to copy from. May not be <code>null</code>.
   */
  private IntHashSet (@NonNull final IntHashSet aOther)
  {
    m_aKeys = aOther.m_aKeys.clone ();
    m_bHasFreeKey = aOther.m_bHasFreeKey;
    m_fFillFactor = aOther.m_fFillFactor;
    m_nThreshold = aOther.m_nThreshold;
    m_nSize = aOther.m_nSize;
    m_nMask = aOther.m_nMask;
  }

  /**
   * Same as <code>MapHelper.arraySize</code>: the least power of two &le; 2<sup>30</sup> and &ge;
   * <code>Math.ceil (nExpected / fFillFactor)</code>.
   */
  @Nonnegative
  private static int _getArraySize (final int nExpected, final float fFillFactor)
  {
    final long nNeeded = (long) Math.ceil (nExpected / fFillFactor);
    final long nSize = nNeeded <= 2 ? 2 : Long.highestOneBit (nNeeded - 1) << 1;
    if (nSize > (1 << 30))
      throw new IllegalArgumentException ("Too large (" +
                                          nExpected +
                                          " expected elements with fill factor " +
                                          fFillFactor +
                                          ")");
    return (int) nSize;
  }

  /**
   * Same as <code>MapHelper.phiMix</code>: mix the hash of the given integer using the golden
   * ratio.
   */
  private static int _phiMix (final int n)
  {
    final int h = n * INT_PHI;
    return h ^ (h >> 16);
  }

  private int _getNextIndex (final int nCurrentIndex)
  {
    return (nCurrentIndex + 1) & m_nMask;
  }

  /**
   * Find the position of a value.
   *
   * @param nValue
   *        Value to look for. Must not be the free key.
   * @return Position or -1 if not found
   */
  @CheckForSigned
  private int _getReadIndex (final int nValue)
  {
    int nIdx = _phiMix (nValue) & m_nMask;
    if (m_aKeys[nIdx] == nValue)
      return nIdx;
    if (m_aKeys[nIdx] == FREE_KEY)
    {
      // end of chain already
      return -1;
    }
    final int nStartIdx = nIdx;
    while ((nIdx = _getNextIndex (nIdx)) != nStartIdx)
    {
      if (m_aKeys[nIdx] == FREE_KEY)
        return -1;
      if (m_aKeys[nIdx] == nValue)
        return nIdx;
    }
    return -1;
  }

  /**
   * Find the first free slot for a value that is known not to be contained.
   *
   * @param nValue
   *        Value to look for. Must not be the free key.
   * @return Index of the free slot or -1 if the table is full.
   */
  @CheckForSigned
  private int _getFreeIndex (final int nValue)
  {
    final int nStartIdx = _phiMix (nValue) & m_nMask;
    int nIdx = nStartIdx;
    while (m_aKeys[nIdx] != FREE_KEY)
    {
      nIdx = _getNextIndex (nIdx);
      if (nIdx == nStartIdx)
        return -1;
    }
    return nIdx;
  }

  private void _rehash (final int nNewCapacity)
  {
    m_nThreshold = (int) (nNewCapacity * m_fFillFactor);
    m_nMask = nNewCapacity - 1;

    final int [] aOldKeys = m_aKeys;
    m_aKeys = new int [nNewCapacity];
    // The free key is not part of the array, so the size does not change
    for (final int nKey : aOldKeys)
      if (nKey != FREE_KEY)
        m_aKeys[_getFreeIndex (nKey)] = nKey;
  }

  private void _shiftKeys (final int nPos)
  {
    // Shift entries with the same hash.
    int nCurPos = nPos;
    final int [] aKeys = m_aKeys;
    while (true)
    {
      final int nLast = nCurPos;
      nCurPos = _getNextIndex (nCurPos);
      int k;
      while (true)
      {
        k = aKeys[nCurPos];
        if (k == FREE_KEY)
        {
          aKeys[nLast] = FREE_KEY;
          return;
        }
        // calculate the starting slot for the current key
        final int nSlot = _phiMix (k) & m_nMask;
        if (nLast <= nCurPos ? nLast >= nSlot || nSlot > nCurPos : nLast >= nSlot && nSlot > nCurPos)
          break;
        nCurPos = _getNextIndex (nCurPos);
      }
      aKeys[nLast] = k;
    }
  }

  /** {@inheritDoc} */
  @Nonnegative
  public int size ()
  {
    return m_nSize;
  }

  /** {@inheritDoc} */
  public boolean isEmpty ()
  {
    return m_nSize == 0;
  }

  /**
   * Check if the passed value is contained in this set.
   *
   * @param nValue
   *        The value to check.
   * @return <code>true</code> if the value is contained, <code>false</code> otherwise.
   */
  public boolean contains (final int nValue)
  {
    if (nValue == FREE_KEY)
      return m_bHasFreeKey;
    return _getReadIndex (nValue) != -1;
  }

  /**
   * Add a value to this set.
   *
   * @param nValue
   *        The value to add.
   * @return <code>true</code> if the value was added, <code>false</code> if it was already
   *         contained.
   */
  public boolean add (final int nValue)
  {
    if (nValue == FREE_KEY)
    {
      if (m_bHasFreeKey)
        return false;
      m_bHasFreeKey = true;
      ++m_nSize;
      return true;
    }

    if (_getReadIndex (nValue) != -1)
      return false;

    int nIdx = _getFreeIndex (nValue);
    if (nIdx < 0)
    {
      // no insertion point? Should not happen...
      _rehash (m_aKeys.length * 2);
      nIdx = _getFreeIndex (nValue);
    }
    m_aKeys[nIdx] = nValue;
    ++m_nSize;
    if (m_nSize >= m_nThreshold)
      _rehash (m_aKeys.length * 2);
    return true;
  }

  /**
   * Remove a value from this set.
   *
   * @param nValue
   *        The value to remove.
   * @return <code>true</code> if the value was removed, <code>false</code> if it was not
   *         contained.
   */
  public boolean remove (final int nValue)
  {
    if (nValue == FREE_KEY)
    {
      if (!m_bHasFreeKey)
        return false;
      m_bHasFreeKey = false;
      --m_nSize;
      return true;
    }

    final int nIdx = _getReadIndex (nValue);
    if (nIdx == -1)
      return false;

    _shiftKeys (nIdx);
    --m_nSize;
    return true;
  }

  /**
   * Remove all values from this set. The capacity is not reduced.
   */
  public void clear ()
  {
    if (m_nSize > 0)
    {
      Arrays.fill (m_aKeys, FREE_KEY);
      m_bHasFreeKey = false;
      m_nSize = 0;
    }
  }

  /**
   * Invoke the provided consumer for each value in this set. The order is undefined.
   *
   * @param aConsumer
   *        The consumer to invoke for each value. May not be <code>null</code>.
   */
  public void forEach (@NonNull final IntConsumer aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");
    if (m_bHasFreeKey)
      aConsumer.accept (FREE_KEY);
    for (final int nKey : m_aKeys)
      if (nKey != FREE_KEY)
        aConsumer.accept (nKey);
  }

  /**
   * @return A new array with all values of this set in no particular order. Never
   *         <code>null</code> but maybe empty.
   */
  @ReturnsMutableCopy
  public int @NonNull [] toArray ()
  {
    final int [] ret = new int [m_nSize];
    int nIndex = 0;
    if (m_bHasFreeKey)
      ret[nIndex++] = FREE_KEY;
    for (final int nKey : m_aKeys)
      if (nKey != FREE_KEY)
        ret[nIndex++] = nKey;
    return ret;
  }

  @NonNull
  @ReturnsMutableCopy
  public IntHashSet getClone ()
  {
    return new IntHashSet (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final IntHashSet rhs = (IntHashSet) o;
    if (m_nSize != rhs.m_nSize || m_bHasFreeKey != rhs.m_bHasFreeKey)
      return false;
    for (final int nKey : m_aKeys)
      if (nKey != FREE_KEY && !rhs.contains (nKey))
        return false;
    return true;
  }

  @Override
  public int hashCode ()
  {
    // Order independent: the sum of the mixed hashes of all values. The free key gets a separate
    // constant, as its mixed hash would be 0
    int ret = m_bHasFreeKey ? INT_PHI : 0;
    for (final int nKey : m_aKeys)
      if (nKey != FREE_KEY)
        ret += _phiMix (nKey);
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Values", toArray ()).getToString ();
  }
}

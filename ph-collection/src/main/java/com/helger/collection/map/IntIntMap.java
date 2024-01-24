/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.collection.map;

import java.util.Arrays;
import java.util.function.IntUnaryOperator;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.lang.IHasSize;

/**
 * Special int-int-primitive map. Source: https://github.com/mikvor/hashmapTest
 *
 * @author Mikhail Vorontsov
 * @author Philip Helger
 */
@NotThreadSafe
public class IntIntMap implements IHasSize
{
  private static final int FREE_KEY = 0;

  public static final int NO_VALUE = 0;

  /** Keys */
  private int [] m_aKeys;
  /** Values */
  private int [] m_aValues;

  /** Do we have 'free' key in the map? */
  private boolean m_bHasFreeKey;
  /** Value of 'free' key */
  private int m_nFreeValue = NO_VALUE;

  /** Fill factor, must be between (0 and 1) */
  private final float m_fFillFactor;
  /** We will resize a map once it reaches this size */
  private int m_nThreshold;
  /** Current map size */
  private int m_nSize;
  /** Mask to calculate the original position */
  private int m_nMask;

  public IntIntMap ()
  {
    this (16);
  }

  public IntIntMap (final int nSize)
  {
    this (nSize, 0.75f);
  }

  public IntIntMap (final int nSize, final float fFillFactor)
  {
    ValueEnforcer.isBetweenInclusive (fFillFactor, "FillFactor", 0f, 1f);
    ValueEnforcer.isGT0 (nSize, "Size");
    final int nCapacity = MapHelper.arraySize (nSize, fFillFactor);
    m_nMask = nCapacity - 1;
    m_fFillFactor = fFillFactor;

    m_aKeys = new int [nCapacity];
    m_aValues = _createValueArray (nCapacity);
    m_nThreshold = (int) (nCapacity * fFillFactor);
  }

  @Nonnull
  @ReturnsMutableCopy
  private static int [] _createValueArray (@Nonnegative final int nSize)
  {
    final int [] ret = new int [nSize];
    Arrays.fill (ret, NO_VALUE);
    return ret;
  }

  public int get (final int key)
  {
    return get (key, NO_VALUE);
  }

  public int get (final int key, final int nDefault)
  {
    if (key == FREE_KEY)
      return m_bHasFreeKey ? m_nFreeValue : nDefault;

    final int idx = _getReadIndex (key);
    return idx != -1 ? m_aValues[idx] : nDefault;
  }

  public int computeIfAbsent (final int key, @Nonnull final IntUnaryOperator aProvider)
  {
    int ret = get (key);
    if (ret == NO_VALUE)
    {
      ret = aProvider.applyAsInt (key);
      if (ret != NO_VALUE)
        put (key, ret);
    }
    return ret;
  }

  public int put (final int key, final int value)
  {
    if (key == FREE_KEY)
    {
      final int ret = m_nFreeValue;
      if (!m_bHasFreeKey)
      {
        ++m_nSize;
        m_bHasFreeKey = true;
      }
      m_nFreeValue = value;
      return ret;
    }

    int idx = _getPutIndex (key);
    if (idx < 0)
    {
      // no insertion point? Should not happen...
      _rehash (m_aKeys.length * 2);
      idx = _getPutIndex (key);
    }
    final int prev = m_aValues[idx];
    if (m_aKeys[idx] != key)
    {
      m_aKeys[idx] = key;
      m_aValues[idx] = value;
      ++m_nSize;
      if (m_nSize >= m_nThreshold)
        _rehash (m_aKeys.length * 2);
    }
    else
    {
      // it means used cell with our key
      if (m_aKeys[idx] != key)
        throw new IllegalStateException ();
      m_aValues[idx] = value;
    }
    return prev;
  }

  public int remove (final int key)
  {
    if (key == FREE_KEY)
    {
      if (!m_bHasFreeKey)
        return NO_VALUE;
      m_bHasFreeKey = false;
      final int ret = m_nFreeValue;
      m_nFreeValue = NO_VALUE;
      --m_nSize;
      return ret;
    }

    final int idx = _getReadIndex (key);
    if (idx == -1)
      return NO_VALUE;

    final int res = m_aValues[idx];
    m_aValues[idx] = NO_VALUE;
    _shiftKeys (idx);
    --m_nSize;
    return res;
  }

  @Nonnegative
  public int size ()
  {
    return m_nSize;
  }

  public boolean isEmpty ()
  {
    return m_nSize == 0;
  }

  private void _rehash (final int nNewCapacity)
  {
    m_nThreshold = (int) (nNewCapacity * m_fFillFactor);
    m_nMask = nNewCapacity - 1;

    final int nOldCapacity = m_aKeys.length;
    final int [] aOldKeys = m_aKeys;
    final int [] aOldValues = m_aValues;

    m_aKeys = new int [nNewCapacity];
    m_aValues = _createValueArray (nNewCapacity);
    m_nSize = m_bHasFreeKey ? 1 : 0;

    int i = nOldCapacity;
    while (i > 0)
    {
      i--;
      if (aOldKeys[i] != FREE_KEY)
        put (aOldKeys[i], aOldValues[i]);
    }
  }

  private int _shiftKeys (final int nPos)
  {
    // Shift entries with the same hash.
    int pos = nPos;
    final int [] keys = this.m_aKeys;
    while (true)
    {
      final int last = pos;
      pos = _getNextIndex (pos);
      int k;
      while (true)
      {
        k = keys[pos];
        if (k == FREE_KEY)
        {
          keys[last] = FREE_KEY;
          m_aValues[last] = NO_VALUE;
          return last;
        }
        // calculate the starting slot for the current key
        final int slot = MapHelper.phiMix (k) & m_nMask;
        if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos)
          break;
        pos = _getNextIndex (pos);
      }
      keys[last] = k;
      m_aValues[last] = m_aValues[pos];
    }
  }

  /**
   * Find key position in the map.
   *
   * @param key
   *        Key to look for
   * @return Key position or -1 if not found
   */
  @CheckForSigned
  private int _getReadIndex (final int key)
  {
    int idx = MapHelper.phiMix (key) & m_nMask;
    if (m_aKeys[idx] == key)
    {
      // we check FREE prior to this call
      return idx;
    }
    if (m_aKeys[idx] == FREE_KEY)
    {
      // end of chain already
      return -1;
    }
    final int startIdx = idx;
    while ((idx = _getNextIndex (idx)) != startIdx)
    {
      if (m_aKeys[idx] == FREE_KEY)
        return -1;
      if (m_aKeys[idx] == key)
        return idx;
    }
    return -1;
  }

  /**
   * Find an index of a cell which should be updated by 'put' operation. It can
   * be: 1) a cell with a given key 2) first free cell in the chain
   *
   * @param key
   *        Key to look for
   * @return Index of a cell to be updated by a 'put' operation
   */
  @CheckForSigned
  private int _getPutIndex (final int key)
  {
    final int readIdx = _getReadIndex (key);
    if (readIdx >= 0)
      return readIdx;
    // key not found, find insertion point
    final int startIdx = MapHelper.phiMix (key) & m_nMask;
    if (m_aKeys[startIdx] == FREE_KEY)
      return startIdx;
    int idx = startIdx;
    while (m_aKeys[idx] != FREE_KEY)
    {
      idx = _getNextIndex (idx);
      if (idx == startIdx)
        return -1;
    }
    return idx;
  }

  private int _getNextIndex (final int currentIndex)
  {
    return (currentIndex + 1) & m_nMask;
  }

  @FunctionalInterface
  public interface IConsumer
  {
    void accept (int nKey, int nValue);
  }

  public void forEach (@Nonnull final IConsumer aConsumer)
  {
    if (m_bHasFreeKey)
      aConsumer.accept (FREE_KEY, m_nFreeValue);
    final int nLen = m_aKeys.length;
    for (int i = 0; i < nLen; ++i)
    {
      final int nKey = m_aKeys[i];
      if (nKey != FREE_KEY)
      {
        final int nValue = m_aValues[i];
        if (nValue != NO_VALUE)
          aConsumer.accept (nKey, nValue);
      }
    }
  }
}

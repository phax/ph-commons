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
package com.helger.collection.list;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.clone.ICloneable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.iface.IHasSize;
import com.helger.base.state.EContinue;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.helper.CollectionSort;

/**
 * A growable list of primitive <code>int</code> values, backed by an <code>int</code> array. In
 * contrast to a <code>List&lt;Integer&gt;</code> no value is ever boxed.
 *
 * @author Philip Helger
 * @since 12.6.0
 */
@NotThreadSafe
public class IntArrayList implements IHasSize, ICloneable <IntArrayList>
{
  /** The default initial capacity */
  public static final int DEFAULT_INITIAL_CAPACITY = 10;

  private int [] m_aData;
  private int m_nSize;

  /**
   * Create a new empty list with the default initial capacity.
   */
  public IntArrayList ()
  {
    this (DEFAULT_INITIAL_CAPACITY);
  }

  /**
   * Create a new empty list with the provided initial capacity.
   *
   * @param nInitialCapacity
   *        The initial capacity for which memory is reserved. Must be &ge; 0.
   */
  public IntArrayList (@Nonnegative final int nInitialCapacity)
  {
    ValueEnforcer.isGE0 (nInitialCapacity, "InitialCapacity");
    m_aData = new int [nInitialCapacity];
  }

  /**
   * Copy constructor.
   *
   * @param aOther
   *        The list to copy from. May not be <code>null</code>.
   */
  private IntArrayList (@NonNull final IntArrayList aOther)
  {
    m_aData = Arrays.copyOf (aOther.m_aData, aOther.m_nSize);
    m_nSize = aOther.m_nSize;
  }

  private void _ensureCapacity (@Nonnegative final int nMinCapacity)
  {
    final int nOldCapacity = m_aData.length;
    if (nMinCapacity > nOldCapacity)
    {
      // Grow by 50%, but at least to the minimum capacity
      final long nGrown = nOldCapacity + (nOldCapacity >> 1) + 1L;
      final int nNewCapacity = (int) Math.min (Math.max (nGrown, nMinCapacity), Integer.MAX_VALUE - 8);
      if (nNewCapacity < nMinCapacity)
        throw new OutOfMemoryError ("Required array size too large");
      m_aData = Arrays.copyOf (m_aData, nNewCapacity);
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

  /** {@inheritDoc} */
  public boolean isNotEmpty ()
  {
    return m_nSize > 0;
  }

  /**
   * Append a value at the end of the list.
   *
   * @param nValue
   *        The value to add.
   */
  public void add (final int nValue)
  {
    if (m_nSize == m_aData.length)
      _ensureCapacity (m_nSize + 1);
    m_aData[m_nSize++] = nValue;
  }

  /**
   * Append all provided values at the end of the list.
   *
   * @param aValues
   *        The values to add. May be <code>null</code>.
   */
  public void addAll (final int @Nullable... aValues)
  {
    if (aValues != null && aValues.length > 0)
    {
      _ensureCapacity (m_nSize + aValues.length);
      System.arraycopy (aValues, 0, m_aData, m_nSize, aValues.length);
      m_nSize += aValues.length;
    }
  }

  /**
   * Get the value at the provided index.
   *
   * @param nIndex
   *        The index to retrieve. Must be &ge; 0 and &lt; {@link #size()}.
   * @return The value at the index.
   * @throws IndexOutOfBoundsException
   *         if the index is invalid
   */
  public int get (@Nonnegative final int nIndex)
  {
    Objects.checkIndex (nIndex, m_nSize);
    return m_aData[nIndex];
  }

  /**
   * Set the value at the provided index.
   *
   * @param nIndex
   *        The index to set. Must be &ge; 0 and &lt; {@link #size()}.
   * @param nValue
   *        The new value.
   * @return The previous value at the index.
   * @throws IndexOutOfBoundsException
   *         if the index is invalid
   */
  public int set (@Nonnegative final int nIndex, final int nValue)
  {
    Objects.checkIndex (nIndex, m_nSize);
    final int ret = m_aData[nIndex];
    m_aData[nIndex] = nValue;
    return ret;
  }

  /**
   * Get the last value of the list.
   *
   * @return The last value.
   * @throws NoSuchElementException
   *         if the list is empty
   */
  public int getLast ()
  {
    if (m_nSize == 0)
      throw new NoSuchElementException ("The list is empty");
    return m_aData[m_nSize - 1];
  }

  /**
   * Remove the last value of the list.
   *
   * @return The removed value.
   * @throws NoSuchElementException
   *         if the list is empty
   */
  public int removeLast ()
  {
    if (m_nSize == 0)
      throw new NoSuchElementException ("The list is empty");
    return m_aData[--m_nSize];
  }

  /**
   * Remove all values. The capacity is not reduced.
   */
  public void clear ()
  {
    m_nSize = 0;
  }

  /**
   * Get the index of the first occurrence of the provided value.
   *
   * @param nValue
   *        The value to search.
   * @return The index of the first occurrence or -1 if the value is not contained.
   */
  @CheckForSigned
  public int indexOf (final int nValue)
  {
    for (int i = 0; i < m_nSize; ++i)
      if (m_aData[i] == nValue)
        return i;
    return -1;
  }

  /**
   * Check if the provided value is contained.
   *
   * @param nValue
   *        The value to search.
   * @return <code>true</code> if the value is contained, <code>false</code> otherwise.
   */
  public boolean contains (final int nValue)
  {
    return indexOf (nValue) >= 0;
  }

  /**
   * Invoke the provided consumer for each value in list order.
   *
   * @param aConsumer
   *        The consumer to invoke. May not be <code>null</code>.
   */
  public void forEach (@NonNull final IntConsumer aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");
    for (int i = 0; i < m_nSize; ++i)
      aConsumer.accept (m_aData[i]);
  }

  /**
   * Invoke the provided consumer for each value in list order that matches the provided filter.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code> in which case all values are passed to
   *        the consumer.
   * @param aConsumer
   *        The consumer to invoke. May not be <code>null</code>.
   * @see #forEach(IntConsumer)
   */
  public void forEach (@Nullable final IntPredicate aFilter, @NonNull final IntConsumer aConsumer)
  {
    if (aFilter == null)
      forEach (aConsumer);
    else
    {
      ValueEnforcer.notNull (aConsumer, "Consumer");
      for (int i = 0; i < m_nSize; ++i)
      {
        final int nValue = m_aData[i];
        if (aFilter.test (nValue))
          aConsumer.accept (nValue);
      }
    }
  }

  /**
   * A consumer for a value together with its index.
   *
   * @author Philip Helger
   */
  @FunctionalInterface
  public interface IIndexedConsumer
  {
    void accept (int nValue, @Nonnegative int nIndex);
  }

  /**
   * Invoke the provided consumer for each value in list order, providing the value AND the index.
   *
   * @param aConsumer
   *        The consumer to invoke. May not be <code>null</code>.
   */
  public void forEachByIndex (@NonNull final IIndexedConsumer aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");
    for (int i = 0; i < m_nSize; ++i)
      aConsumer.accept (m_aData[i], i);
  }

  /**
   * A special version of {@link #forEach(IntConsumer)} that can break iteration if a certain
   * requirement is fulfilled.
   *
   * @param aConsumer
   *        The consumer to invoke. May not be <code>null</code>.
   * @return {@link EContinue#BREAK} if iteration was stopped, {@link EContinue#CONTINUE} otherwise.
   */
  @NonNull
  public EContinue forEachBreakable (@NonNull final IntFunction <EContinue> aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");
    for (int i = 0; i < m_nSize; ++i)
      if (aConsumer.apply (m_aData[i]).isBreak ())
        return EContinue.BREAK;
    return EContinue.CONTINUE;
  }

  /**
   * Sort all values in ascending numerical order.
   */
  public void sort ()
  {
    Arrays.sort (m_aData, 0, m_nSize);
  }

  /**
   * Sort all values with the provided comparator. The sort is stable, so values that compare equal
   * keep their relative order.
   *
   * @param aCmp
   *        The comparator to use. May not be <code>null</code>.
   * @see CollectionSort#sortInlineStable(int[], int, int, IntBinaryOperator)
   */
  public void sortStable (@NonNull final IntBinaryOperator aCmp)
  {
    CollectionSort.sortInlineStable (m_aData, 0, m_nSize, aCmp);
  }

  /**
   * @return A new array with all values in list order. Never <code>null</code> but maybe empty.
   */
  @ReturnsMutableCopy
  public int @NonNull [] toArray ()
  {
    return Arrays.copyOf (m_aData, m_nSize);
  }

  @NonNull
  @ReturnsMutableCopy
  public IntArrayList getClone ()
  {
    return new IntArrayList (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final IntArrayList rhs = (IntArrayList) o;
    return Arrays.equals (m_aData, 0, m_nSize, rhs.m_aData, 0, rhs.m_nSize);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aData, 0, m_nSize).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Values", toArray ()).getToString ();
  }
}

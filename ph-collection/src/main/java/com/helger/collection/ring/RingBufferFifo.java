/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.collection.ring;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

/**
 * A FIFO (first in first out) ring buffer.
 *
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        The elements contained in the ring buffer.
 */
@NotThreadSafe
public class RingBufferFifo <ELEMENTTYPE>
{
  private final int m_nCapacity;
  private final Object [] m_aElements;
  private final boolean m_bAllowOverwrite;
  private int m_nWritePos = 0;
  private int m_nAvailable = 0;

  /**
   * Constructor
   *
   * @param nCapacity
   *        The number of elements in the ring buffer. Must be &gt; 0.
   * @param bAllowOverwrite
   *        <code>true</code> if the oldest element gets overwritten when the
   *        next element is put.
   */
  public RingBufferFifo (@Nonnegative final int nCapacity, final boolean bAllowOverwrite)
  {
    ValueEnforcer.isGT0 (nCapacity, "Capacity");
    m_nCapacity = nCapacity;
    m_aElements = new Object [nCapacity];
    m_bAllowOverwrite = bAllowOverwrite;
  }

  /**
   * Reset the buffer so that it is empty again.
   */
  public void reset ()
  {
    m_nWritePos = 0;
    m_nAvailable = 0;
  }

  /**
   * @return The capacity as stated in the constructor. Always &gt; 0.
   */
  @Nonnegative
  public int getCapacity ()
  {
    return m_nCapacity;
  }

  /**
   * @return The number of available elements in the ring buffer. Always &ge; 0.
   */
  @Nonnegative
  public int getAvailable ()
  {
    return m_nAvailable;
  }

  /**
   * @return The number of free elements in the ring buffer. Always &ge; 0.
   */
  @Nonnegative
  public int getRemainingCapacity ()
  {
    return m_nCapacity - m_nAvailable;
  }

  /**
   * @return <code>true</code> if overwriting the oldest element is allowed (as
   *         specified in the constructor).
   */
  public boolean isOverwriteAllowed ()
  {
    return m_bAllowOverwrite;
  }

  /**
   * Add a new element into the ring buffer
   *
   * @param aElement
   *        The element to be added. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if the element was successfully added or if
   *         allow overwrite is active and the element was overwritten.
   */
  @Nonnull
  public EChange put (@Nullable final ELEMENTTYPE aElement)
  {
    if (m_nAvailable < m_nCapacity)
    {
      if (m_nWritePos >= m_nCapacity)
        m_nWritePos = 0;
      m_aElements[m_nWritePos] = aElement;
      m_nWritePos++;
      m_nAvailable++;
      return EChange.CHANGED;
    }
    else
      if (m_bAllowOverwrite)
      {
        if (m_nWritePos >= m_nCapacity)
          m_nWritePos = 0;
        m_aElements[m_nWritePos] = aElement;
        m_nWritePos++;
        return EChange.CHANGED;
      }

    return EChange.UNCHANGED;
  }

  /**
   * Take an element from the ring buffer.
   *
   * @return <code>null</code> if no more element is available or if the current
   *         element is <code>null</code>.
   */
  @Nullable
  public ELEMENTTYPE take ()
  {
    final int nAvailable = m_nAvailable;
    if (nAvailable == 0)
      return null;

    int nIndex = m_nWritePos - nAvailable;
    if (nIndex < 0)
      nIndex += m_nCapacity;

    final Object ret = m_aElements[nIndex];
    m_nAvailable--;
    return GenericReflection.uncheckedCast (ret);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Capacity", m_nCapacity)
                                       .append ("Elements", m_aElements)
                                       .append ("AllowOverwrite", m_bAllowOverwrite)
                                       .append ("WritePos", m_nWritePos)
                                       .append ("Available", m_nAvailable)
                                       .getToString ();
  }
}

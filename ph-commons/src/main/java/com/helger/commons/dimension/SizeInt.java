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
package com.helger.commons.dimension;

import java.io.Serializable;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.math.MathHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * This class represents an object having width and height.
 *
 * @author Philip Helger
 */
@Immutable
public class SizeInt implements IHasDimensionInt, Serializable
{
  private final int m_nWidth;
  private final int m_nHeight;

  public SizeInt (@Nonnull final IHasDimensionInt aObj)
  {
    this (aObj.getWidth (), aObj.getHeight ());
  }

  public SizeInt (@Nonnegative final int nWidth, @Nonnegative final int nHeight)
  {
    m_nWidth = ValueEnforcer.isGE0 (nWidth, "Width");
    m_nHeight = ValueEnforcer.isGE0 (nHeight, "Height");
  }

  @Nonnegative
  public int getWidth ()
  {
    return m_nWidth;
  }

  @Nonnegative
  public int getHeight ()
  {
    return m_nHeight;
  }

  /**
   * Return the scaled width and height relative to a maximum size.
   *
   * @param nMaxWidth
   *        Maximum width. Must be &gt; 0.
   * @param nMaxHeight
   *        Maximum height. Must be &gt; 0.
   * @return An array with 2 elements, where the first element is the width, and
   *         the second is the height.
   */
  @Nonnull
  @CheckReturnValue
  public SizeInt getBestMatchingSize (@Nonnegative final int nMaxWidth, @Nonnegative final int nMaxHeight)
  {
    ValueEnforcer.isGT0 (nMaxWidth, "MaxWidth");
    ValueEnforcer.isGT0 (nMaxHeight, "MaxHeight");

    final double dRelWidth = MathHelper.getDividedDouble (m_nWidth, nMaxWidth);
    final double dRelHeight = MathHelper.getDividedDouble (m_nHeight, nMaxHeight);
    if (dRelWidth > dRelHeight)
    {
      if (m_nWidth > nMaxWidth)
        return new SizeInt (nMaxWidth, (int) (m_nHeight / dRelWidth));
    }
    else
    {
      if (m_nHeight > nMaxHeight)
        return new SizeInt ((int) (m_nWidth / dRelHeight), nMaxHeight);
    }
    return this;
  }

  @Nonnull
  @CheckReturnValue
  public SizeInt getScaledToWidth (@Nonnegative final int nNewWidth)
  {
    ValueEnforcer.isGT0 (nNewWidth, "NewWidth");

    if (m_nWidth == nNewWidth)
      return this;
    final double dMultFactory = MathHelper.getDividedDouble (nNewWidth, m_nWidth);
    return new SizeInt (nNewWidth, (int) (m_nHeight * dMultFactory));
  }

  @Nonnull
  @CheckReturnValue
  public SizeInt getScaledToHeight (@Nonnegative final int nNewHeight)
  {
    ValueEnforcer.isGT0 (nNewHeight, "NewHeight");

    if (m_nHeight == nNewHeight)
      return this;
    final double dMultFactory = MathHelper.getDividedDouble (nNewHeight, m_nHeight);
    return new SizeInt ((int) (m_nWidth * dMultFactory), nNewHeight);
  }

  @Nonnull
  @CheckReturnValue
  public SizeInt getAdded (@Nonnull final IHasDimensionInt aToAdd)
  {
    ValueEnforcer.notNull (aToAdd, "ToAdd");

    return new SizeInt (m_nWidth + aToAdd.getWidth (), m_nHeight + aToAdd.getHeight ());
  }

  @Nonnull
  @CheckReturnValue
  public SizeInt getSubtracted (@Nonnull final IHasDimensionInt aToSubtract)
  {
    ValueEnforcer.notNull (aToSubtract, "ToSubtract");

    return new SizeInt (m_nWidth - aToSubtract.getWidth (), m_nHeight - aToSubtract.getHeight ());
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final SizeInt rhs = (SizeInt) o;
    return m_nWidth == rhs.m_nWidth && m_nHeight == rhs.m_nHeight;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_nWidth).append (m_nHeight).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("width", m_nWidth).append ("height", m_nHeight).toString ();
  }
}

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
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * This class represents an object having width and height.
 *
 * @author Philip Helger
 */
@Immutable
public class SizeFloat implements IHasDimensionFloat, Serializable
{
  private final float m_dWidth;
  private final float m_dHeight;

  public SizeFloat (@Nonnull final IHasDimensionInt aObj)
  {
    this (aObj.getWidth (), aObj.getHeight ());
  }

  public SizeFloat (@Nonnull final IHasDimensionFloat aObj)
  {
    this (aObj.getWidth (), aObj.getHeight ());
  }

  public SizeFloat (@Nonnegative final float dWidth, @Nonnegative final float dHeight)
  {
    m_dWidth = ValueEnforcer.isGE0 (dWidth, "Width");
    m_dHeight = ValueEnforcer.isGE0 (dHeight, "Height");
  }

  @Nonnegative
  public float getWidth ()
  {
    return m_dWidth;
  }

  @Nonnegative
  public float getHeight ()
  {
    return m_dHeight;
  }

  /**
   * Return the scaled width and height relative to a maximum size.
   *
   * @param dMaxWidth
   *        Maximum width. Must be &gt; 0.
   * @param dMaxHeight
   *        Maximum height. Must be &gt; 0.
   * @return An array with 2 elements, where the first element is the width, and
   *         the second is the height.
   */
  @Nonnull
  @CheckReturnValue
  public SizeFloat getBestMatchingSize (@Nonnegative final float dMaxWidth, @Nonnegative final float dMaxHeight)
  {
    ValueEnforcer.isGT0 (dMaxWidth, "MaxWidth");
    ValueEnforcer.isGT0 (dMaxHeight, "MaxHeight");

    final float dRelWidth = m_dWidth / dMaxWidth;
    final float dRelHeight = m_dHeight / dMaxHeight;
    if (dRelWidth > dRelHeight)
    {
      if (m_dWidth > dMaxWidth)
        return new SizeFloat (dMaxWidth, m_dHeight / dRelWidth);
    }
    else
    {
      if (m_dHeight > dMaxHeight)
        return new SizeFloat (m_dWidth / dRelHeight, dMaxHeight);
    }
    return this;
  }

  @Nonnull
  @CheckReturnValue
  public SizeFloat getScaledToWidth (@Nonnegative final float dNewWidth)
  {
    ValueEnforcer.isGT0 (dNewWidth, "NewWidth");

    if (m_dWidth == dNewWidth)
      return this;
    final float dMultFactory = dNewWidth / m_dWidth;
    return new SizeFloat (dNewWidth, m_dHeight * dMultFactory);
  }

  @Nonnull
  @CheckReturnValue
  public SizeFloat getScaledToHeight (@Nonnegative final float dNewHeight)
  {
    ValueEnforcer.isGT0 (dNewHeight, "NewHeight");

    if (m_dHeight == dNewHeight)
      return this;
    final float dMultFactory = dNewHeight / m_dHeight;
    return new SizeFloat (m_dWidth * dMultFactory, dNewHeight);
  }

  @Nonnull
  @CheckReturnValue
  public SizeFloat getAdded (@Nonnull final IHasDimensionInt aToAdd)
  {
    ValueEnforcer.notNull (aToAdd, "ToAdd");

    return new SizeFloat (m_dWidth + aToAdd.getWidth (), m_dHeight + aToAdd.getHeight ());
  }

  @Nonnull
  @CheckReturnValue
  public SizeFloat getAdded (@Nonnull final IHasDimensionFloat aToAdd)
  {
    ValueEnforcer.notNull (aToAdd, "ToAdd");

    return new SizeFloat (m_dWidth + aToAdd.getWidth (), m_dHeight + aToAdd.getHeight ());
  }

  @Nonnull
  @CheckReturnValue
  public SizeFloat getSubtracted (@Nonnull final IHasDimensionInt aToSubtract)
  {
    ValueEnforcer.notNull (aToSubtract, "ToSubtract");

    return new SizeFloat (m_dWidth - aToSubtract.getWidth (), m_dHeight - aToSubtract.getHeight ());
  }

  @Nonnull
  @CheckReturnValue
  public SizeFloat getSubtracted (@Nonnull final IHasDimensionFloat aToSubtract)
  {
    ValueEnforcer.notNull (aToSubtract, "ToSubtract");

    return new SizeFloat (m_dWidth - aToSubtract.getWidth (), m_dHeight - aToSubtract.getHeight ());
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final SizeFloat rhs = (SizeFloat) o;
    return EqualsHelper.equals (m_dWidth, rhs.m_dWidth) && EqualsHelper.equals (m_dHeight, rhs.m_dHeight);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_dWidth).append (m_dHeight).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("width", m_dWidth).append ("height", m_dHeight).toString ();
  }
}

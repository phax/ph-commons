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
package com.helger.base.dimension;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.CheckReturnValue;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;

/**
 * This class represents an object having width and height.
 *
 * @author Philip Helger
 */
@Immutable
public class SizeDouble implements IHasDimensionDouble
{
  private final double m_dWidth;
  private final double m_dHeight;

  /**
   * Constructor copying from an existing int dimension object.
   *
   * @param aObj
   *        The dimension object to copy from. May not be <code>null</code>.
   */
  public SizeDouble (@NonNull final IHasDimensionInt aObj)
  {
    this (aObj.getWidth (), aObj.getHeight ());
  }

  /**
   * Constructor copying from an existing long dimension object.
   *
   * @param aObj
   *        The dimension object to copy from. May not be <code>null</code>.
   */
  public SizeDouble (@NonNull final IHasDimensionLong aObj)
  {
    this (aObj.getWidth (), aObj.getHeight ());
  }

  /**
   * Constructor copying from an existing float dimension object.
   *
   * @param aObj
   *        The dimension object to copy from. May not be <code>null</code>.
   */
  public SizeDouble (@NonNull final IHasDimensionFloat aObj)
  {
    this (aObj.getWidth (), aObj.getHeight ());
  }

  /**
   * Constructor copying from an existing double dimension object.
   *
   * @param aObj
   *        The dimension object to copy from. May not be <code>null</code>.
   */
  public SizeDouble (@NonNull final IHasDimensionDouble aObj)
  {
    this (aObj.getWidth (), aObj.getHeight ());
  }

  /**
   * Constructor with width and height.
   *
   * @param dWidth
   *        The width. Must be &ge; 0.
   * @param dHeight
   *        The height. Must be &ge; 0.
   */
  public SizeDouble (@Nonnegative final double dWidth, @Nonnegative final double dHeight)
  {
    m_dWidth = ValueEnforcer.isGE0 (dWidth, "Width");
    m_dHeight = ValueEnforcer.isGE0 (dHeight, "Height");
  }

  /** {@inheritDoc} */
  @Nonnegative
  public double getWidth ()
  {
    return m_dWidth;
  }

  /** {@inheritDoc} */
  @Nonnegative
  public double getHeight ()
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
   * @return An array with 2 elements, where the first element is the width, and the second is the
   *         height.
   */
  @NonNull
  @CheckReturnValue
  public SizeDouble getBestMatchingSize (@Nonnegative final double dMaxWidth, @Nonnegative final double dMaxHeight)
  {
    ValueEnforcer.isGT0 (dMaxWidth, "MaxWidth");
    ValueEnforcer.isGT0 (dMaxHeight, "MaxHeight");

    final double dRelWidth = m_dWidth / dMaxWidth;
    final double dRelHeight = m_dHeight / dMaxHeight;
    if (dRelWidth > dRelHeight)
    {
      if (m_dWidth > dMaxWidth)
        return new SizeDouble (dMaxWidth, m_dHeight / dRelWidth);
    }
    else
    {
      if (m_dHeight > dMaxHeight)
        return new SizeDouble (m_dWidth / dRelHeight, dMaxHeight);
    }
    return this;
  }

  /**
   * Get a new size that is scaled to the specified width, keeping the aspect ratio.
   *
   * @param dNewWidth
   *        The new width. Must be &gt; 0.
   * @return A new {@link SizeDouble} with the scaled dimensions. Never <code>null</code>.
   */
  @NonNull
  @CheckReturnValue
  public SizeDouble getScaledToWidth (@Nonnegative final double dNewWidth)
  {
    ValueEnforcer.isGT0 (dNewWidth, "NewWidth");

    if (m_dWidth == dNewWidth)
      return this;
    final double dMultFactory = dNewWidth / m_dWidth;
    return new SizeDouble (dNewWidth, m_dHeight * dMultFactory);
  }

  /**
   * Get a new size that is scaled to the specified height, keeping the aspect ratio.
   *
   * @param dNewHeight
   *        The new height. Must be &gt; 0.
   * @return A new {@link SizeDouble} with the scaled dimensions. Never <code>null</code>.
   */
  @NonNull
  @CheckReturnValue
  public SizeDouble getScaledToHeight (@Nonnegative final double dNewHeight)
  {
    ValueEnforcer.isGT0 (dNewHeight, "NewHeight");

    if (m_dHeight == dNewHeight)
      return this;
    final double dMultFactory = dNewHeight / m_dHeight;
    return new SizeDouble (m_dWidth * dMultFactory, dNewHeight);
  }

  /**
   * Get a new size with the dimensions of the provided int dimension added.
   *
   * @param aToAdd
   *        The dimension to add. May not be <code>null</code>.
   * @return A new {@link SizeDouble}. Never <code>null</code>.
   */
  @NonNull
  @CheckReturnValue
  public SizeDouble getAdded (@NonNull final IHasDimensionInt aToAdd)
  {
    ValueEnforcer.notNull (aToAdd, "ToAdd");

    return new SizeDouble (m_dWidth + aToAdd.getWidth (), m_dHeight + aToAdd.getHeight ());
  }

  /**
   * Get a new size with the dimensions of the provided float dimension added.
   *
   * @param aToAdd
   *        The dimension to add. May not be <code>null</code>.
   * @return A new {@link SizeDouble}. Never <code>null</code>.
   */
  @NonNull
  @CheckReturnValue
  public SizeDouble getAdded (@NonNull final IHasDimensionFloat aToAdd)
  {
    ValueEnforcer.notNull (aToAdd, "ToAdd");

    return new SizeDouble (m_dWidth + aToAdd.getWidth (), m_dHeight + aToAdd.getHeight ());
  }

  /**
   * Get a new size with the dimensions of the provided double dimension added.
   *
   * @param aToAdd
   *        The dimension to add. May not be <code>null</code>.
   * @return A new {@link SizeDouble}. Never <code>null</code>.
   */
  @NonNull
  @CheckReturnValue
  public SizeDouble getAdded (@NonNull final IHasDimensionDouble aToAdd)
  {
    ValueEnforcer.notNull (aToAdd, "ToAdd");

    return new SizeDouble (m_dWidth + aToAdd.getWidth (), m_dHeight + aToAdd.getHeight ());
  }

  /**
   * Get a new size with the dimensions of the provided int dimension subtracted.
   *
   * @param aToSubtract
   *        The dimension to subtract. May not be <code>null</code>.
   * @return A new {@link SizeDouble}. Never <code>null</code>.
   */
  @NonNull
  @CheckReturnValue
  public SizeDouble getSubtracted (@NonNull final IHasDimensionInt aToSubtract)
  {
    ValueEnforcer.notNull (aToSubtract, "ToSubtract");

    return new SizeDouble (m_dWidth - aToSubtract.getWidth (), m_dHeight - aToSubtract.getHeight ());
  }

  /**
   * Get a new size with the dimensions of the provided float dimension subtracted.
   *
   * @param aToSubtract
   *        The dimension to subtract. May not be <code>null</code>.
   * @return A new {@link SizeDouble}. Never <code>null</code>.
   */
  @NonNull
  @CheckReturnValue
  public SizeDouble getSubtracted (@NonNull final IHasDimensionFloat aToSubtract)
  {
    ValueEnforcer.notNull (aToSubtract, "ToSubtract");

    return new SizeDouble (m_dWidth - aToSubtract.getWidth (), m_dHeight - aToSubtract.getHeight ());
  }

  /**
   * Get a new size with the dimensions of the provided double dimension subtracted.
   *
   * @param aToSubtract
   *        The dimension to subtract. May not be <code>null</code>.
   * @return A new {@link SizeDouble}. Never <code>null</code>.
   */
  @NonNull
  @CheckReturnValue
  public SizeDouble getSubtracted (@NonNull final IHasDimensionDouble aToSubtract)
  {
    ValueEnforcer.notNull (aToSubtract, "ToSubtract");

    return new SizeDouble (m_dWidth - aToSubtract.getWidth (), m_dHeight - aToSubtract.getHeight ());
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final SizeDouble rhs = (SizeDouble) o;
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
    return new ToStringGenerator (this).append ("width", m_dWidth).append ("height", m_dHeight).getToString ();
  }
}

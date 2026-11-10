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
public class SizeFloat implements IHasDimensionFloat
{
  private final float m_fWidth;
  private final float m_fHeight;

  /**
   * Constructor copying from an existing int dimension object.
   *
   * @param aObj
   *        The dimension object to copy from. May not be <code>null</code>.
   */
  public SizeFloat (@NonNull final IHasDimensionInt aObj)
  {
    this (aObj.getWidth (), aObj.getHeight ());
  }

  /**
   * Constructor copying from an existing long dimension object.
   *
   * @param aObj
   *        The dimension object to copy from. May not be <code>null</code>.
   */
  public SizeFloat (@NonNull final IHasDimensionLong aObj)
  {
    this (aObj.getWidth (), aObj.getHeight ());
  }

  /**
   * Constructor copying from an existing float dimension object.
   *
   * @param aObj
   *        The dimension object to copy from. May not be <code>null</code>.
   */
  public SizeFloat (@NonNull final IHasDimensionFloat aObj)
  {
    this (aObj.getWidth (), aObj.getHeight ());
  }

  /**
   * Constructor with width and height.
   *
   * @param fWidth
   *        The width. Must be &ge; 0.
   * @param fHeight
   *        The height. Must be &ge; 0.
   */
  public SizeFloat (@Nonnegative final float fWidth, @Nonnegative final float fHeight)
  {
    m_fWidth = ValueEnforcer.isGE0 (fWidth, "Width");
    m_fHeight = ValueEnforcer.isGE0 (fHeight, "Height");
  }

  /** {@inheritDoc} */
  @Nonnegative
  public float getWidth ()
  {
    return m_fWidth;
  }

  /** {@inheritDoc} */
  @Nonnegative
  public float getHeight ()
  {
    return m_fHeight;
  }

  /**
   * Return the scaled width and height relative to a maximum size.
   *
   * @param fMaxWidth
   *        Maximum width. Must be &gt; 0.
   * @param fMaxHeight
   *        Maximum height. Must be &gt; 0.
   * @return An array with 2 elements, where the first element is the width, and the second is the
   *         height.
   */
  @NonNull
  @CheckReturnValue
  public SizeFloat getBestMatchingSize (@Nonnegative final float fMaxWidth, @Nonnegative final float fMaxHeight)
  {
    ValueEnforcer.isGT0 (fMaxWidth, "MaxWidth");
    ValueEnforcer.isGT0 (fMaxHeight, "MaxHeight");

    final float fRelWidth = m_fWidth / fMaxWidth;
    final float fRelHeight = m_fHeight / fMaxHeight;
    if (fRelWidth > fRelHeight)
    {
      if (m_fWidth > fMaxWidth)
        return new SizeFloat (fMaxWidth, m_fHeight / fRelWidth);
    }
    else
    {
      if (m_fHeight > fMaxHeight)
        return new SizeFloat (m_fWidth / fRelHeight, fMaxHeight);
    }
    return this;
  }

  /**
   * Get a new size that is scaled to the specified width, keeping the aspect ratio.
   *
   * @param fNewWidth
   *        The new width. Must be &gt; 0.
   * @return A new {@link SizeFloat} with the scaled dimensions. Never <code>null</code>.
   * @throws IllegalStateException
   *         if the current width is 0
   */
  @NonNull
  @CheckReturnValue
  public SizeFloat getScaledToWidth (@Nonnegative final float fNewWidth)
  {
    ValueEnforcer.isGT0 (fNewWidth, "NewWidth");
    if (m_fWidth == 0)
      throw new IllegalStateException ("The width is 0, so the aspect ratio cannot be kept");

    if (m_fWidth == fNewWidth)
      return this;
    final float fMultFactory = fNewWidth / m_fWidth;
    return new SizeFloat (fNewWidth, m_fHeight * fMultFactory);
  }

  /**
   * Get a new size that is scaled to the specified height, keeping the aspect ratio.
   *
   * @param fNewHeight
   *        The new height. Must be &gt; 0.
   * @return A new {@link SizeFloat} with the scaled dimensions. Never <code>null</code>.
   * @throws IllegalStateException
   *         if the current height is 0
   */
  @NonNull
  @CheckReturnValue
  public SizeFloat getScaledToHeight (@Nonnegative final float fNewHeight)
  {
    ValueEnforcer.isGT0 (fNewHeight, "NewHeight");
    if (m_fHeight == 0)
      throw new IllegalStateException ("The height is 0, so the aspect ratio cannot be kept");

    if (m_fHeight == fNewHeight)
      return this;
    final float fMultFactory = fNewHeight / m_fHeight;
    return new SizeFloat (m_fWidth * fMultFactory, fNewHeight);
  }

  /**
   * Get a new size with the dimensions of the provided int dimension added.
   *
   * @param aToAdd
   *        The dimension to add. May not be <code>null</code>.
   * @return A new {@link SizeFloat}. Never <code>null</code>.
   */
  @NonNull
  @CheckReturnValue
  public SizeFloat getAdded (@NonNull final IHasDimensionInt aToAdd)
  {
    ValueEnforcer.notNull (aToAdd, "ToAdd");

    return new SizeFloat (m_fWidth + aToAdd.getWidth (), m_fHeight + aToAdd.getHeight ());
  }

  /**
   * Get a new size with the dimensions of the provided float dimension added.
   *
   * @param aToAdd
   *        The dimension to add. May not be <code>null</code>.
   * @return A new {@link SizeFloat}. Never <code>null</code>.
   */
  @NonNull
  @CheckReturnValue
  public SizeFloat getAdded (@NonNull final IHasDimensionFloat aToAdd)
  {
    ValueEnforcer.notNull (aToAdd, "ToAdd");

    return new SizeFloat (m_fWidth + aToAdd.getWidth (), m_fHeight + aToAdd.getHeight ());
  }

  /**
   * Get a new size with the dimensions of the provided int dimension subtracted.
   *
   * @param aToSubtract
   *        The dimension to subtract. May not be <code>null</code>.
   * @return A new {@link SizeFloat}. Never <code>null</code>.
   */
  @NonNull
  @CheckReturnValue
  public SizeFloat getSubtracted (@NonNull final IHasDimensionInt aToSubtract)
  {
    ValueEnforcer.notNull (aToSubtract, "ToSubtract");

    return new SizeFloat (m_fWidth - aToSubtract.getWidth (), m_fHeight - aToSubtract.getHeight ());
  }

  /**
   * Get a new size with the dimensions of the provided float dimension subtracted.
   *
   * @param aToSubtract
   *        The dimension to subtract. May not be <code>null</code>.
   * @return A new {@link SizeFloat}. Never <code>null</code>.
   */
  @NonNull
  @CheckReturnValue
  public SizeFloat getSubtracted (@NonNull final IHasDimensionFloat aToSubtract)
  {
    ValueEnforcer.notNull (aToSubtract, "ToSubtract");

    return new SizeFloat (m_fWidth - aToSubtract.getWidth (), m_fHeight - aToSubtract.getHeight ());
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final SizeFloat rhs = (SizeFloat) o;
    return EqualsHelper.equals (m_fWidth, rhs.m_fWidth) && EqualsHelper.equals (m_fHeight, rhs.m_fHeight);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_fWidth).append (m_fHeight).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Width", m_fWidth).append ("Height", m_fHeight).getToString ();
  }
}

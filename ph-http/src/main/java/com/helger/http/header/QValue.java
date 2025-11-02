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
package com.helger.http.header;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;

/**
 * Represents the quality value of an HTTP Accept* header.
 *
 * @author Philip Helger
 */
@Immutable
public class QValue implements Comparable <QValue>
{
  /** Minimum quality value: 0 */
  public static final double MIN_QUALITY = 0;
  /** Maximum quality value: 1 */
  public static final double MAX_QUALITY = 1;
  /** 50% quantile quality value: 0.5 */
  public static final double HALF_QUALITY = 0.5;

  private static final Logger LOGGER = LoggerFactory.getLogger (QValue.class);

  /** Minimum quality: 0 */
  public static final QValue MIN_QVALUE = new QValue (MIN_QUALITY);
  /** Maximum quality: 1 */
  public static final QValue MAX_QVALUE = new QValue (MAX_QUALITY);

  private final double m_dQuality;

  public QValue (@Nonnegative final double dQuality)
  {
    if (dQuality < MIN_QUALITY)
      LOGGER.warn ("QValue is too small: " + dQuality);
    else
      if (dQuality > MAX_QUALITY)
        LOGGER.warn ("QValue is too large: " + dQuality);
    m_dQuality = getValueInRange (dQuality);
  }

  /**
   * Get the passed value in the range {@link #MIN_QUALITY} and
   * {@link #MAX_QUALITY}.
   *
   * @param dQuality
   *        Source value
   * @return Aligned value &ge; {@link #MIN_QUALITY} and &le;
   *         {@link #MAX_QUALITY}.
   */
  public static double getValueInRange (final double dQuality)
  {
    if (dQuality < MIN_QUALITY)
      return MIN_QUALITY;
    if (dQuality > MAX_QUALITY)
      return MAX_QUALITY;
    return dQuality;
  }

  @Nonnegative
  public double getQuality ()
  {
    return m_dQuality;
  }

  /**
   * @return <code>true</code> if the quality is {@link #MIN_QUALITY}
   */
  public boolean isMinimumQuality ()
  {
    return EqualsHelper.equals (m_dQuality, MIN_QUALITY);
  }

  /**
   * @return <code>true</code> if the quality is &gt; {@link #MIN_QUALITY}
   */
  public boolean isAboveMinimumQuality ()
  {
    return m_dQuality > MIN_QUALITY;
  }

  /**
   * @return <code>true</code> if the quality is &le; {@link #HALF_QUALITY}
   * @see #isHighValue()
   */
  public boolean isLowValue ()
  {
    return m_dQuality <= HALF_QUALITY;
  }

  /**
   * @return <code>true</code> if the quality is &gt; {@link #HALF_QUALITY}
   * @see #isLowValue()
   */
  public boolean isHighValue ()
  {
    return m_dQuality > HALF_QUALITY;
  }

  /**
   * @return <code>true</code> if the quality is &lt; {@link #MAX_QUALITY}
   */
  public boolean isBelowMaximumQuality ()
  {
    return m_dQuality < MAX_QUALITY;
  }

  /**
   * @return <code>true</code> if the quality is {@link #MAX_QUALITY}
   */
  public boolean isMaximumQuality ()
  {
    return EqualsHelper.equals (m_dQuality, MAX_QUALITY);
  }

  /**
   * @return <code>true</code> if the quality is &gt; {@link #MIN_QUALITY} and
   *         &lt; {@link #MAX_QUALITY}.
   * @see #isAboveMinimumQuality()
   * @see #isBelowMaximumQuality()
   */
  public boolean isBetweenMinimumAndMaximum ()
  {
    return isAboveMinimumQuality () && isBelowMaximumQuality ();
  }

  public int compareTo (@NonNull final QValue rhs)
  {
    return Double.compare (m_dQuality, rhs.m_dQuality);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final QValue rhs = (QValue) o;
    return EqualsHelper.equals (m_dQuality, rhs.m_dQuality);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_dQuality).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Quality", m_dQuality).getToString ();
  }
}

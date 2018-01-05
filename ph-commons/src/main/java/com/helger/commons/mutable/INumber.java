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
package com.helger.commons.mutable;

import java.io.Serializable;

/**
 * Base interface comparable to {@link Number} - but an interface and not an
 * abstract class.
 * 
 * @author Philip Helger
 */
public interface INumber extends Serializable
{
  /**
   * Returns the value of the specified number as an {@code int}, which may
   * involve rounding or truncation.
   *
   * @return the numeric value represented by this object after conversion to
   *         type {@code int}.
   */
  int intValue ();

  /**
   * Returns the value of the specified number as a {@code long}, which may
   * involve rounding or truncation.
   *
   * @return the numeric value represented by this object after conversion to
   *         type {@code long}.
   */
  long longValue ();

  /**
   * Returns the value of the specified number as a {@code float}, which may
   * involve rounding.
   *
   * @return the numeric value represented by this object after conversion to
   *         type {@code float}.
   */
  float floatValue ();

  /**
   * Returns the value of the specified number as a {@code double}, which may
   * involve rounding.
   *
   * @return the numeric value represented by this object after conversion to
   *         type {@code double}.
   */
  double doubleValue ();

  /**
   * Returns the value of the specified number as a {@code byte}, which may
   * involve rounding or truncation.
   * <p>
   * This implementation returns the result of {@link #intValue} cast to a
   * {@code byte}.
   *
   * @return the numeric value represented by this object after conversion to
   *         type {@code byte}.
   */
  default byte byteValue ()
  {
    return (byte) intValue ();
  }

  /**
   * Returns the value of the specified number as a {@code short}, which may
   * involve rounding or truncation.
   * <p>
   * This implementation returns the result of {@link #intValue} cast to a
   * {@code short}.
   *
   * @return the numeric value represented by this object after conversion to
   *         type {@code short}.
   */
  default short shortValue ()
  {
    return (short) intValue ();
  }
}

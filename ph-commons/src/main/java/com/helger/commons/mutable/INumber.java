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

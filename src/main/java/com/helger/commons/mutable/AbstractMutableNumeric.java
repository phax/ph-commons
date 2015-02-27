package com.helger.commons.mutable;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.annotation.Nonnull;

/**
 * Base implementation class for {@link IMutableNumeric} extending
 * {@link Number} class.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Real implementation type
 */
public abstract class AbstractMutableNumeric <IMPLTYPE extends AbstractMutableNumeric <IMPLTYPE>> extends Number implements IMutableNumeric <IMPLTYPE>
{
  @Nonnull
  public final Byte getAsByte ()
  {
    return Byte.valueOf (byteValue ());
  }

  @Nonnull
  public final Character getAsCharacter ()
  {
    return Character.valueOf ((char) intValue ());
  }

  @Nonnull
  public final Double getAsDouble ()
  {
    return Double.valueOf (doubleValue ());
  }

  @Nonnull
  public final Float getAsFloat ()
  {
    return Float.valueOf (floatValue ());
  }

  @Nonnull
  public final Integer getAsInteger ()
  {
    return Integer.valueOf (intValue ());
  }

  @Nonnull
  public final Long getAsLong ()
  {
    return Long.valueOf (longValue ());
  }

  @Nonnull
  public final Short getAsShort ()
  {
    return Short.valueOf (shortValue ());
  }

  @Nonnull
  public BigInteger getAsBigInteger ()
  {
    return BigInteger.valueOf (longValue ());
  }

  @Nonnull
  public BigDecimal getAsBigDecimal ()
  {
    return BigDecimal.valueOf (doubleValue ());
  }
}

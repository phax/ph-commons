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
package com.helger.commons.mutable;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.OverrideOnDemand;

/**
 * Base implementation class for {@link IMutableNumeric} extending
 * {@link Number} class.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Real implementation type
 */
public abstract class AbstractMutableNumeric <IMPLTYPE extends AbstractMutableNumeric <IMPLTYPE>> extends Number
                                             implements IMutableNumeric <IMPLTYPE>
{
  /**
   * This method is invoked after a value changed. This method is also called if
   * e.g. an increment of zero happens so no change effectively was performed.
   */
  @OverrideOnDemand
  protected void onAfterChange ()
  {}

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

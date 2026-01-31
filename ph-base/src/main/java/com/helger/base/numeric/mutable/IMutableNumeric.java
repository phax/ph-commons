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
package com.helger.base.numeric.mutable;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.jspecify.annotations.NonNull;

import com.helger.base.numeric.INumber;

/**
 * Base interface for mutable numeric values
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IMutableNumeric <IMPLTYPE extends IMutableNumeric <IMPLTYPE>> extends
                                 IMutableObject <IMPLTYPE>,
                                 INumber
{
  /**
   * @return <code>true</code> if the value is 0
   */
  boolean is0 ();

  /**
   * @return <code>true</code> if the value is not 0
   */
  default boolean isNot0 ()
  {
    return !is0 ();
  }

  /**
   * @return <code>true</code> if the value is &lt; 0
   */
  boolean isLT0 ();

  /**
   * @return <code>true</code> if the value is &le; 0
   */
  boolean isLE0 ();

  /**
   * @return <code>true</code> if the value is &gt; 0
   */
  boolean isGT0 ();

  /**
   * @return <code>true</code> if the value is &ge; 0
   */
  boolean isGE0 ();

  @NonNull
  default Byte getAsByte ()
  {
    return Byte.valueOf (byteValue ());
  }

  @NonNull
  default Character getAsCharacter ()
  {
    return Character.valueOf ((char) intValue ());
  }

  @NonNull
  default Double getAsDouble ()
  {
    return Double.valueOf (doubleValue ());
  }

  @NonNull
  default Float getAsFloat ()
  {
    return Float.valueOf (floatValue ());
  }

  @NonNull
  default Integer getAsInteger ()
  {
    return Integer.valueOf (intValue ());
  }

  @NonNull
  default Long getAsLong ()
  {
    return Long.valueOf (longValue ());
  }

  @NonNull
  default Short getAsShort ()
  {
    return Short.valueOf (shortValue ());
  }

  @NonNull
  default BigInteger getAsBigInteger ()
  {
    return BigInteger.valueOf (longValue ());
  }

  @NonNull
  default BigDecimal getAsBigDecimal ()
  {
    return BigDecimal.valueOf (doubleValue ());
  }
}

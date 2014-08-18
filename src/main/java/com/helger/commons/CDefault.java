/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons;

import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotations.PresentForCodeCoverage;

/**
 * A class containing default values for all primitive and primitive wrapper
 * types.
 * 
 * @author Philip Helger
 */
@Immutable
public final class CDefault
{
  // Default values for primitive values
  /** Default boolean value */
  public static final boolean DEFAULT_BOOLEAN = false;
  /** Default byte value */
  public static final byte DEFAULT_BYTE = (byte) 0;
  /** Default char value */
  public static final char DEFAULT_CHAR = '\0';
  /** Default double value */
  public static final double DEFAULT_DOUBLE = 0D;
  /** Default float value */
  public static final float DEFAULT_FLOAT = 0F;
  /** Default int value */
  public static final int DEFAULT_INT = 0;
  /** Default long value */
  public static final long DEFAULT_LONG = 0L;
  /** Default short value */
  public static final short DEFAULT_SHORT = (short) 0;

  // Default values for primitive wrapper values
  /** Boolean value of {@link #DEFAULT_BOOLEAN} */
  public static final Boolean DEFAULT_BOOLEAN_OBJ = Boolean.valueOf (DEFAULT_BOOLEAN);
  /** Byte value of {@link #DEFAULT_BYTE} */
  public static final Byte DEFAULT_BYTE_OBJ = Byte.valueOf (DEFAULT_BYTE);
  /** Character value of {@link #DEFAULT_CHAR} */
  public static final Character DEFAULT_CHAR_OBJ = Character.valueOf (DEFAULT_CHAR);
  /** Double value of {@link #DEFAULT_DOUBLE} */
  public static final Double DEFAULT_DOUBLE_OBJ = Double.valueOf (DEFAULT_DOUBLE);
  /** Float value of {@link #DEFAULT_FLOAT} */
  public static final Float DEFAULT_FLOAT_OBJ = Float.valueOf (DEFAULT_FLOAT);
  /** Integer value of {@link #DEFAULT_INT} */
  public static final Integer DEFAULT_INT_OBJ = Integer.valueOf (DEFAULT_INT);
  /** Long value of {@link #DEFAULT_LONG} */
  public static final Long DEFAULT_LONG_OBJ = Long.valueOf (DEFAULT_LONG);
  /** Short value of {@link #DEFAULT_SHORT} */
  public static final Short DEFAULT_SHORT_OBJ = Short.valueOf (DEFAULT_SHORT);

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final CDefault s_aInstance = new CDefault ();

  private CDefault ()
  {}
}

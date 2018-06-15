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
package com.helger.commons.typeconvert;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.commons.mutable.MutableBigDecimal;
import com.helger.commons.mutable.MutableBigInteger;
import com.helger.commons.mutable.MutableBoolean;
import com.helger.commons.mutable.MutableByte;
import com.helger.commons.mutable.MutableChar;
import com.helger.commons.mutable.MutableDouble;
import com.helger.commons.mutable.MutableFloat;
import com.helger.commons.mutable.MutableInt;
import com.helger.commons.mutable.MutableLong;
import com.helger.commons.mutable.MutableShort;

/**
 * Register the mutable* specific type converter
 *
 * @author Philip Helger
 */
@Immutable
@IsSPIImplementation
public final class MutableTypeConverterRegistrar implements ITypeConverterRegistrarSPI
{
  public void registerTypeConverter (@Nonnull final ITypeConverterRegistry aRegistry)
  {
    // MutableBigDecimal
    aRegistry.registerTypeConverter (MutableBigDecimal.class, BigDecimal.class, MutableBigDecimal::getAsBigDecimal);
    aRegistry.registerTypeConverter (BigDecimal.class, MutableBigDecimal.class, MutableBigDecimal::new);
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (MutableBigDecimal.class,
                                                                  MutableBigDecimal::getAsBigDecimal);
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (MutableBigDecimal.class,
                                                                  aSource -> new MutableBigDecimal (TypeConverter.convert (aSource,
                                                                                                                           BigDecimal.class)));

    // MutableBigInteger
    aRegistry.registerTypeConverter (MutableBigInteger.class, BigInteger.class, MutableBigInteger::getAsBigInteger);
    aRegistry.registerTypeConverter (BigInteger.class, MutableBigInteger.class, MutableBigInteger::new);
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (MutableBigInteger.class,
                                                                  MutableBigInteger::getAsBigInteger);
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (MutableBigInteger.class,
                                                                  aSource -> new MutableBigInteger (TypeConverter.convert (aSource,
                                                                                                                           BigInteger.class)));

    // MutableBoolean
    aRegistry.registerTypeConverter (MutableBoolean.class, Boolean.class, MutableBoolean::getAsBoolean);
    aRegistry.registerTypeConverter (Boolean.class, MutableBoolean.class, MutableBoolean::new);
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (MutableBoolean.class, MutableBoolean::getAsBoolean);
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (MutableBoolean.class,
                                                                  aSource -> new MutableBoolean (TypeConverter.convert (aSource,
                                                                                                                        Boolean.class)));

    // MutableByte
    aRegistry.registerTypeConverter (MutableByte.class, Byte.class, MutableByte::getAsByte);
    aRegistry.registerTypeConverter (Byte.class, MutableByte.class, MutableByte::new);
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (MutableByte.class, MutableByte::getAsByte);
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (MutableByte.class,
                                                                  aSource -> new MutableByte (TypeConverter.convert (aSource,
                                                                                                                     Byte.class)));

    // MutableChar
    aRegistry.registerTypeConverter (MutableChar.class, Character.class, MutableChar::getAsCharacter);
    aRegistry.registerTypeConverter (Character.class, MutableChar.class, MutableChar::new);
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (MutableChar.class, MutableChar::getAsCharacter);
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (MutableChar.class,
                                                                  aSource -> new MutableChar (TypeConverter.convert (aSource,
                                                                                                                     Character.class)));

    // MutableDouble
    aRegistry.registerTypeConverter (MutableDouble.class, Double.class, MutableDouble::getAsDouble);
    aRegistry.registerTypeConverter (Double.class, MutableDouble.class, MutableDouble::new);
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (MutableDouble.class, MutableDouble::getAsDouble);
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (MutableDouble.class,
                                                                  aSource -> new MutableDouble (TypeConverter.convert (aSource,
                                                                                                                       Double.class)));

    // MutableFloat
    aRegistry.registerTypeConverter (MutableFloat.class, Float.class, MutableFloat::getAsFloat);
    aRegistry.registerTypeConverter (Float.class, MutableFloat.class, MutableFloat::new);
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (MutableFloat.class, MutableFloat::getAsFloat);
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (MutableFloat.class,
                                                                  aSource -> new MutableFloat (TypeConverter.convert (aSource,
                                                                                                                      Float.class)));

    // MutableInt
    aRegistry.registerTypeConverter (MutableInt.class, Integer.class, MutableInt::getAsInteger);
    aRegistry.registerTypeConverter (Integer.class, MutableInt.class, MutableInt::new);
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (MutableInt.class, MutableInt::getAsInteger);
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (MutableInt.class,
                                                                  aSource -> new MutableInt (TypeConverter.convert (aSource,
                                                                                                                    Integer.class)));

    // MutableLong
    aRegistry.registerTypeConverter (MutableLong.class, Long.class, MutableLong::getAsLong);
    aRegistry.registerTypeConverter (Long.class, MutableLong.class, MutableLong::new);
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (MutableLong.class, MutableLong::getAsLong);
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (MutableLong.class,
                                                                  aSource -> new MutableLong (TypeConverter.convert (aSource,
                                                                                                                     Long.class)));

    // MutableShort
    aRegistry.registerTypeConverter (MutableShort.class, Short.class, MutableShort::getAsShort);
    aRegistry.registerTypeConverter (Short.class, MutableShort.class, MutableShort::new);
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (MutableShort.class, MutableShort::getAsShort);
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (MutableShort.class,
                                                                  aSource -> new MutableShort (TypeConverter.convert (aSource,
                                                                                                                      Short.class)));
  }
}

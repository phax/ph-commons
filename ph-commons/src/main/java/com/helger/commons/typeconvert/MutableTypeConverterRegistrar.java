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
    aRegistry.registerTypeConverter (MutableBigDecimal.class, BigDecimal.class, aSource -> aSource.getAsBigDecimal ());
    aRegistry.registerTypeConverter (BigDecimal.class,
                                     MutableBigDecimal.class,
                                     aSource -> new MutableBigDecimal (aSource));
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (MutableBigDecimal.class,
                                                                  aSource -> aSource.getAsBigDecimal ());
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (MutableBigDecimal.class,
                                                                  aSource -> new MutableBigDecimal (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                      BigDecimal.class)));

    // MutableBigInteger
    aRegistry.registerTypeConverter (MutableBigInteger.class, BigInteger.class, aSource -> aSource.getAsBigInteger ());
    aRegistry.registerTypeConverter (BigInteger.class,
                                     MutableBigInteger.class,
                                     aSource -> new MutableBigInteger (aSource));
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (MutableBigInteger.class,
                                                                  aSource -> aSource.getAsBigInteger ());
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (MutableBigInteger.class,
                                                                  aSource -> new MutableBigInteger (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                      BigInteger.class)));

    // MutableBoolean
    aRegistry.registerTypeConverter (MutableBoolean.class, Boolean.class, aSource -> aSource.getAsBoolean ());
    aRegistry.registerTypeConverter (Boolean.class, MutableBoolean.class, aSource -> new MutableBoolean (aSource));
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (MutableBoolean.class,
                                                                  aSource -> aSource.getAsBoolean ());
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (MutableBoolean.class,
                                                                  aSource -> new MutableBoolean (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                   Boolean.class)));

    // MutableByte
    aRegistry.registerTypeConverter (MutableByte.class, Byte.class, aSource -> aSource.getAsByte ());
    aRegistry.registerTypeConverter (Byte.class, MutableByte.class, aSource -> new MutableByte (aSource));
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (MutableByte.class, aSource -> aSource.getAsByte ());
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (MutableByte.class,
                                                                  aSource -> new MutableByte (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                Byte.class)));

    // MutableChar
    aRegistry.registerTypeConverter (MutableChar.class, Character.class, aSource -> aSource.getAsCharacter ());
    aRegistry.registerTypeConverter (Character.class, MutableChar.class, aSource -> new MutableChar (aSource));
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (MutableChar.class,
                                                                  aSource -> aSource.getAsCharacter ());
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (MutableChar.class,
                                                                  aSource -> new MutableChar (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                Character.class)));

    // MutableDouble
    aRegistry.registerTypeConverter (MutableDouble.class, Double.class, aSource -> aSource.getAsDouble ());
    aRegistry.registerTypeConverter (Double.class, MutableDouble.class, aSource -> new MutableDouble (aSource));
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (MutableDouble.class,
                                                                  aSource -> aSource.getAsDouble ());
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (MutableDouble.class,
                                                                  aSource -> new MutableDouble (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                  Double.class)));

    // MutableFloat
    aRegistry.registerTypeConverter (MutableFloat.class, Float.class, aSource -> aSource.getAsFloat ());
    aRegistry.registerTypeConverter (Float.class, MutableFloat.class, aSource -> new MutableFloat (aSource));
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (MutableFloat.class, aSource -> aSource.getAsFloat ());
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (MutableFloat.class,
                                                                  aSource -> new MutableFloat (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                 Float.class)));

    // MutableInt
    aRegistry.registerTypeConverter (MutableInt.class, Integer.class, aSource -> aSource.getAsInteger ());
    aRegistry.registerTypeConverter (Integer.class, MutableInt.class, aSource -> new MutableInt (aSource));
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (MutableInt.class, aSource -> aSource.getAsInteger ());
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (MutableInt.class,
                                                                  aSource -> new MutableInt (TypeConverter.convertIfNecessary (aSource,
                                                                                                                               Integer.class)));

    // MutableLong
    aRegistry.registerTypeConverter (MutableLong.class, Long.class, aSource -> aSource.getAsLong ());
    aRegistry.registerTypeConverter (Long.class, MutableLong.class, aSource -> new MutableLong (aSource));
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (MutableLong.class, aSource -> aSource.getAsLong ());
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (MutableLong.class,
                                                                  aSource -> new MutableLong (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                Long.class)));

    // MutableShort
    aRegistry.registerTypeConverter (MutableShort.class, Short.class, aSource -> aSource.getAsShort ());
    aRegistry.registerTypeConverter (Short.class, MutableShort.class, aSource -> new MutableShort (aSource));
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (MutableShort.class, aSource -> aSource.getAsShort ());
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (MutableShort.class,
                                                                  aSource -> new MutableShort (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                 Short.class)));
  }
}

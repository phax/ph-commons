/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
import com.helger.commons.typeconvert.rule.TypeConverterRuleAnySourceFixedDestination;
import com.helger.commons.typeconvert.rule.TypeConverterRuleFixedSourceAnyDestination;

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
    aRegistry.registerTypeConverterRule (TypeConverterRuleFixedSourceAnyDestination.create (MutableBigDecimal.class,
                                                                                            aSource -> aSource.getAsBigDecimal ()));
    aRegistry.registerTypeConverterRule (TypeConverterRuleAnySourceFixedDestination.create (MutableBigDecimal.class,
                                                                                            aSource -> new MutableBigDecimal (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                                                BigDecimal.class))));

    // MutableBigInteger
    aRegistry.registerTypeConverter (MutableBigInteger.class, BigInteger.class, aSource -> aSource.getAsBigInteger ());
    aRegistry.registerTypeConverter (BigInteger.class,
                                     MutableBigInteger.class,
                                     aSource -> new MutableBigInteger (aSource));
    aRegistry.registerTypeConverterRule (TypeConverterRuleFixedSourceAnyDestination.create (MutableBigInteger.class,
                                                                                            aSource -> aSource.getAsBigInteger ()));
    aRegistry.registerTypeConverterRule (TypeConverterRuleAnySourceFixedDestination.create (MutableBigInteger.class,
                                                                                            aSource -> new MutableBigInteger (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                                                BigInteger.class))));

    // MutableBoolean
    aRegistry.registerTypeConverter (MutableBoolean.class, Boolean.class, aSource -> aSource.getAsBoolean ());
    aRegistry.registerTypeConverter (Boolean.class, MutableBoolean.class, aSource -> new MutableBoolean (aSource));
    aRegistry.registerTypeConverterRule (TypeConverterRuleFixedSourceAnyDestination.create (MutableBoolean.class,
                                                                                            aSource -> aSource.getAsBoolean ()));
    aRegistry.registerTypeConverterRule (TypeConverterRuleAnySourceFixedDestination.create (MutableBoolean.class,
                                                                                            aSource -> new MutableBoolean (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                                             Boolean.class))));

    // MutableByte
    aRegistry.registerTypeConverter (MutableByte.class, Byte.class, aSource -> aSource.getAsByte ());
    aRegistry.registerTypeConverter (Byte.class, MutableByte.class, aSource -> new MutableByte (aSource));
    aRegistry.registerTypeConverterRule (TypeConverterRuleFixedSourceAnyDestination.create (MutableByte.class,
                                                                                            aSource -> aSource.getAsByte ()));
    aRegistry.registerTypeConverterRule (TypeConverterRuleAnySourceFixedDestination.create (MutableByte.class,
                                                                                            aSource -> new MutableByte (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                                          Byte.class))));

    // MutableChar
    aRegistry.registerTypeConverter (MutableChar.class, Character.class, aSource -> aSource.getAsCharacter ());
    aRegistry.registerTypeConverter (Character.class, MutableChar.class, aSource -> new MutableChar (aSource));
    aRegistry.registerTypeConverterRule (TypeConverterRuleFixedSourceAnyDestination.create (MutableChar.class,
                                                                                            aSource -> aSource.getAsCharacter ()));
    aRegistry.registerTypeConverterRule (TypeConverterRuleAnySourceFixedDestination.create (MutableChar.class,
                                                                                            aSource -> new MutableChar (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                                          Character.class))));

    // MutableDouble
    aRegistry.registerTypeConverter (MutableDouble.class, Double.class, aSource -> aSource.getAsDouble ());
    aRegistry.registerTypeConverter (Double.class, MutableDouble.class, aSource -> new MutableDouble (aSource));
    aRegistry.registerTypeConverterRule (TypeConverterRuleFixedSourceAnyDestination.create (MutableDouble.class,
                                                                                            aSource -> aSource.getAsDouble ()));
    aRegistry.registerTypeConverterRule (TypeConverterRuleAnySourceFixedDestination.create (MutableDouble.class,
                                                                                            aSource -> new MutableDouble (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                                            Double.class))));

    // MutableFloat
    aRegistry.registerTypeConverter (MutableFloat.class, Float.class, aSource -> aSource.getAsFloat ());
    aRegistry.registerTypeConverter (Float.class, MutableFloat.class, aSource -> new MutableFloat (aSource));
    aRegistry.registerTypeConverterRule (TypeConverterRuleFixedSourceAnyDestination.create (MutableFloat.class,
                                                                                            aSource -> aSource.getAsFloat ()));
    aRegistry.registerTypeConverterRule (TypeConverterRuleAnySourceFixedDestination.create (MutableFloat.class,
                                                                                            aSource -> new MutableFloat (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                                           Float.class))));

    // MutableInt
    aRegistry.registerTypeConverter (MutableInt.class, Integer.class, aSource -> aSource.getAsInteger ());
    aRegistry.registerTypeConverter (Integer.class, MutableInt.class, aSource -> new MutableInt (aSource));
    aRegistry.registerTypeConverterRule (TypeConverterRuleFixedSourceAnyDestination.create (MutableInt.class,
                                                                                            aSource -> aSource.getAsInteger ()));
    aRegistry.registerTypeConverterRule (TypeConverterRuleAnySourceFixedDestination.create (MutableInt.class,
                                                                                            aSource -> new MutableInt (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                                         Integer.class))));

    // MutableLong
    aRegistry.registerTypeConverter (MutableLong.class, Long.class, aSource -> aSource.getAsLong ());
    aRegistry.registerTypeConverter (Long.class, MutableLong.class, aSource -> new MutableLong (aSource));
    aRegistry.registerTypeConverterRule (TypeConverterRuleFixedSourceAnyDestination.create (MutableLong.class,
                                                                                            aSource -> aSource.getAsLong ()));
    aRegistry.registerTypeConverterRule (TypeConverterRuleAnySourceFixedDestination.create (MutableLong.class,
                                                                                            aSource -> new MutableLong (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                                          Long.class))));

    // MutableShort
    aRegistry.registerTypeConverter (MutableShort.class, Short.class, aSource -> aSource.getAsShort ());
    aRegistry.registerTypeConverter (Short.class, MutableShort.class, aSource -> new MutableShort (aSource));
    aRegistry.registerTypeConverterRule (TypeConverterRuleFixedSourceAnyDestination.create (MutableShort.class,
                                                                                            aSource -> aSource.getAsShort ()));
    aRegistry.registerTypeConverterRule (TypeConverterRuleAnySourceFixedDestination.create (MutableShort.class,
                                                                                            aSource -> new MutableShort (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                                           Short.class))));
  }
}

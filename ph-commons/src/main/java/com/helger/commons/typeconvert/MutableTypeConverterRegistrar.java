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
    aRegistry.registerTypeConverter (MutableBigDecimal.class,
                                     BigDecimal.class,
                                     (ITypeConverter) aSource -> ((MutableBigDecimal) aSource).getAsBigDecimal ());
    aRegistry.registerTypeConverter (BigDecimal.class,
                                     MutableBigDecimal.class,
                                     (ITypeConverter) aSource -> new MutableBigDecimal ((BigDecimal) aSource));
    aRegistry.registerTypeConverterRule (new TypeConverterRuleFixedSourceAnyDestination (MutableBigDecimal.class,
                                                                                         aSource -> ((MutableBigDecimal) aSource).getAsBigDecimal ()));
    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (MutableBigDecimal.class,
                                                                                         aSource -> new MutableBigDecimal (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                                             BigDecimal.class))));

    // MutableBigInteger
    aRegistry.registerTypeConverter (MutableBigInteger.class,
                                     BigInteger.class,
                                     (ITypeConverter) aSource -> ((MutableBigInteger) aSource).getAsBigInteger ());
    aRegistry.registerTypeConverter (BigInteger.class,
                                     MutableBigInteger.class,
                                     (ITypeConverter) aSource -> new MutableBigInteger ((BigInteger) aSource));
    aRegistry.registerTypeConverterRule (new TypeConverterRuleFixedSourceAnyDestination (MutableBigInteger.class,
                                                                                         aSource -> ((MutableBigInteger) aSource).getAsBigInteger ()));
    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (MutableBigInteger.class,
                                                                                         aSource -> new MutableBigInteger (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                                             BigInteger.class))));

    // MutableBoolean
    aRegistry.registerTypeConverter (MutableBoolean.class,
                                     Boolean.class,
                                     (ITypeConverter) aSource -> ((MutableBoolean) aSource).getAsBoolean ());
    aRegistry.registerTypeConverter (Boolean.class,
                                     MutableBoolean.class,
                                     (ITypeConverter) aSource -> new MutableBoolean ((Boolean) aSource));
    aRegistry.registerTypeConverterRule (new TypeConverterRuleFixedSourceAnyDestination (MutableBoolean.class,
                                                                                         aSource -> ((MutableBoolean) aSource).getAsBoolean ()));
    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (MutableBoolean.class,
                                                                                         aSource -> new MutableBoolean (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                                          Boolean.class))));

    // MutableByte
    aRegistry.registerTypeConverter (MutableByte.class,
                                     Byte.class,
                                     (ITypeConverter) aSource -> ((MutableByte) aSource).getAsByte ());
    aRegistry.registerTypeConverter (Byte.class,
                                     MutableByte.class,
                                     (ITypeConverter) aSource -> new MutableByte ((Byte) aSource));
    aRegistry.registerTypeConverterRule (new TypeConverterRuleFixedSourceAnyDestination (MutableByte.class,
                                                                                         aSource -> ((MutableByte) aSource).getAsByte ()));
    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (MutableByte.class,
                                                                                         aSource -> new MutableByte (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                                       Byte.class))));

    // MutableChar
    aRegistry.registerTypeConverter (MutableChar.class,
                                     Character.class,
                                     (ITypeConverter) aSource -> ((MutableChar) aSource).getAsCharacter ());
    aRegistry.registerTypeConverter (Character.class,
                                     MutableChar.class,
                                     (ITypeConverter) aSource -> new MutableChar ((Character) aSource));
    aRegistry.registerTypeConverterRule (new TypeConverterRuleFixedSourceAnyDestination (MutableChar.class,
                                                                                         aSource -> ((MutableChar) aSource).getAsCharacter ()));
    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (MutableChar.class,
                                                                                         aSource -> new MutableChar (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                                       Character.class))));

    // MutableDouble
    aRegistry.registerTypeConverter (MutableDouble.class,
                                     Double.class,
                                     (ITypeConverter) aSource -> ((MutableDouble) aSource).getAsDouble ());
    aRegistry.registerTypeConverter (Double.class,
                                     MutableDouble.class,
                                     (ITypeConverter) aSource -> new MutableDouble ((Double) aSource));
    aRegistry.registerTypeConverterRule (new TypeConverterRuleFixedSourceAnyDestination (MutableDouble.class,
                                                                                         aSource -> ((MutableDouble) aSource).getAsDouble ()));
    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (MutableDouble.class,
                                                                                         aSource -> new MutableDouble (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                                         Double.class))));

    // MutableFloat
    aRegistry.registerTypeConverter (MutableFloat.class,
                                     Float.class,
                                     (ITypeConverter) aSource -> ((MutableFloat) aSource).getAsFloat ());
    aRegistry.registerTypeConverter (Float.class,
                                     MutableFloat.class,
                                     (ITypeConverter) aSource -> new MutableFloat ((Float) aSource));
    aRegistry.registerTypeConverterRule (new TypeConverterRuleFixedSourceAnyDestination (MutableFloat.class,
                                                                                         aSource -> ((MutableFloat) aSource).getAsFloat ()));
    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (MutableFloat.class,
                                                                                         aSource -> new MutableFloat (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                                        Float.class))));

    // MutableInt
    aRegistry.registerTypeConverter (MutableInt.class,
                                     Integer.class,
                                     (ITypeConverter) aSource -> ((MutableInt) aSource).getAsInteger ());
    aRegistry.registerTypeConverter (Integer.class,
                                     MutableInt.class,
                                     (ITypeConverter) aSource -> new MutableInt ((Integer) aSource));
    aRegistry.registerTypeConverterRule (new TypeConverterRuleFixedSourceAnyDestination (MutableInt.class,
                                                                                         aSource -> ((MutableInt) aSource).getAsInteger ()));
    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (MutableInt.class,
                                                                                         aSource -> new MutableInt (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                                      Integer.class))));

    // MutableLong
    aRegistry.registerTypeConverter (MutableLong.class,
                                     Long.class,
                                     (ITypeConverter) aSource -> ((MutableLong) aSource).getAsLong ());
    aRegistry.registerTypeConverter (Long.class,
                                     MutableLong.class,
                                     (ITypeConverter) aSource -> new MutableLong ((Long) aSource));
    aRegistry.registerTypeConverterRule (new TypeConverterRuleFixedSourceAnyDestination (MutableLong.class,
                                                                                         aSource -> ((MutableLong) aSource).getAsLong ()));
    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (MutableLong.class,
                                                                                         aSource -> new MutableLong (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                                       Long.class))));

    // MutableShort
    aRegistry.registerTypeConverter (MutableShort.class,
                                     Short.class,
                                     (ITypeConverter) aSource -> ((MutableShort) aSource).getAsShort ());
    aRegistry.registerTypeConverter (Short.class,
                                     MutableShort.class,
                                     (ITypeConverter) aSource -> new MutableShort ((Short) aSource));
    aRegistry.registerTypeConverterRule (new TypeConverterRuleFixedSourceAnyDestination (MutableShort.class,
                                                                                         aSource -> ((MutableShort) aSource).getAsShort ()));
    aRegistry.registerTypeConverterRule (new TypeConverterRuleAnySourceFixedDestination (MutableShort.class,
                                                                                         aSource -> new MutableShort (TypeConverter.convertIfNecessary (aSource,
                                                                                                                                                        Short.class))));
  }
}

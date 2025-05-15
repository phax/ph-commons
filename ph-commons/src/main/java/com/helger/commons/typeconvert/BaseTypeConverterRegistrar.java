/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonnull;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.IsSPIImplementation;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;

/**
 * Register the base type converter
 *
 * @author Philip Helger
 */
@Immutable
@IsSPIImplementation
public final class BaseTypeConverterRegistrar implements ITypeConverterRegistrarSPI
{
  private static final Logger LOGGER = LoggerFactory.getLogger (BaseTypeConverterRegistrar.class);

  /**
   * Register all type converters for the 15 base types:<br>
   * <ul>
   * <li>Boolean</li>
   * <li>Byte</li>
   * <li>Character</li>
   * <li>Double</li>
   * <li>Float</li>
   * <li>Integer</li>
   * <li>Long</li>
   * <li>Short</li>
   * <li>String</li>
   * <li>BigDecimal</li>
   * <li>BigInteger</li>
   * <li>AtomicBoolean</li>
   * <li>AtomicInteger</li>
   * <li>AtomicLong</li>
   * <li>StringBuffer</li>
   * <li>StringBuilder</li>
   * </ul>
   */
  public void registerTypeConverter (@Nonnull final ITypeConverterRegistry aRegistry)
  {
    // to Boolean
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                         Boolean.class,
                                                                         aSource -> Boolean.valueOf (aSource.intValue () !=
                                                                                                     0));
    aRegistry.registerTypeConverter (Character.class,
                                     Boolean.class,
                                     aSource -> Boolean.valueOf (aSource.charValue () != 0));
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (Boolean.class,
                                                                  aSource -> StringParser.parseBoolObj (aSource,
                                                                                                        (Boolean) null));

    // to Byte
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                         Byte.class,
                                                                         aSource -> Byte.valueOf (aSource.byteValue ()));
    aRegistry.registerTypeConverter (Boolean.class,
                                     Byte.class,
                                     aSource -> Byte.valueOf (aSource.booleanValue () ? (byte) 1 : (byte) 0));
    aRegistry.registerTypeConverter (Character.class,
                                     Byte.class,
                                     aSource -> Byte.valueOf ((byte) aSource.charValue ()));
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (Byte.class,
                                                                  aSource -> StringParser.parseByteObj (aSource,
                                                                                                        (Byte) null));

    // to Character
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                         Character.class,
                                                                         aSource -> Character.valueOf ((char) aSource.intValue ()));
    aRegistry.registerTypeConverter (Boolean.class,
                                     Character.class,
                                     aSource -> Character.valueOf (aSource.booleanValue () ? (char) 1 : (char) 0));
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (Character.class, aSource -> {
      final String sSource = aSource.toString ();
      return sSource.length () == 1 ? Character.valueOf (sSource.charAt (0)) : null;
    });

    // to Double
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                         Double.class,
                                                                         aSource -> Double.valueOf (aSource.doubleValue ()));
    aRegistry.registerTypeConverter (Boolean.class,
                                     Double.class,
                                     aSource -> Double.valueOf (aSource.booleanValue () ? 1d : 0d));
    aRegistry.registerTypeConverter (Character.class, Double.class, aSource -> Double.valueOf (aSource.charValue ()));
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (Double.class,
                                                                  aSource -> StringParser.parseDoubleObj (aSource,
                                                                                                          (Double) null));

    // to Float
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                         Float.class,
                                                                         aSource -> Float.valueOf (aSource.floatValue ()));
    aRegistry.registerTypeConverter (Boolean.class,
                                     Float.class,
                                     aSource -> Float.valueOf (aSource.booleanValue () ? 1f : 0f));
    aRegistry.registerTypeConverter (Character.class, Float.class, aSource -> Float.valueOf (aSource.charValue ()));
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (Float.class,
                                                                  aSource -> StringParser.parseFloatObj (aSource,
                                                                                                         (Float) null));

    // to Integer
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                         Integer.class,
                                                                         aSource -> Integer.valueOf (aSource.intValue ()));
    aRegistry.registerTypeConverter (Boolean.class,
                                     Integer.class,
                                     aSource -> Integer.valueOf (aSource.booleanValue () ? 1 : 0));
    aRegistry.registerTypeConverter (Character.class, Integer.class, aSource -> Integer.valueOf (aSource.charValue ()));
    aRegistry.registerTypeConverter (String.class, Integer.class, aSource -> {
      final BigDecimal aBD = StringParser.parseBigDecimal (aSource, (BigDecimal) null);
      return aBD == null ? null : Integer.valueOf (aBD.intValue ());
    });
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (Integer.class,
                                                                  aSource -> StringParser.parseIntObj (aSource,
                                                                                                       (Integer) null));

    // to Long
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                         Long.class,
                                                                         aSource -> Long.valueOf (aSource.longValue ()));
    aRegistry.registerTypeConverter (Boolean.class,
                                     Long.class,
                                     aSource -> Long.valueOf (aSource.booleanValue () ? 1L : 0L));
    aRegistry.registerTypeConverter (Character.class, Long.class, aSource -> Long.valueOf (aSource.charValue ()));
    aRegistry.registerTypeConverter (String.class, Long.class, aSource -> {
      final BigDecimal aBD = StringParser.parseBigDecimal (aSource, (BigDecimal) null);
      return aBD == null ? null : Long.valueOf (aBD.longValue ());
    });
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (Long.class,
                                                                  aSource -> StringParser.parseLongObj (aSource,
                                                                                                        (Long) null));

    // to Short
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                         Short.class,
                                                                         aSource -> Short.valueOf (aSource.shortValue ()));
    aRegistry.registerTypeConverter (Boolean.class,
                                     Short.class,
                                     aSource -> Short.valueOf (aSource.booleanValue () ? (short) 1 : (short) 0));
    aRegistry.registerTypeConverter (Character.class,
                                     Short.class,
                                     aSource -> Short.valueOf ((short) aSource.charValue ()));
    aRegistry.registerTypeConverter (String.class, Short.class, aSource -> {
      final BigDecimal aBD = StringParser.parseBigDecimal (aSource, (BigDecimal) null);
      return aBD == null ? null : Short.valueOf (aBD.shortValue ());
    });
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (Short.class,
                                                                  aSource -> StringParser.parseShortObj (aSource,
                                                                                                         (Short) null));

    // to String
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (String.class, Object::toString);

    // to BigDecimal
    aRegistry.registerTypeConverter (BigInteger.class, BigDecimal.class, BigDecimal::new);
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                         BigDecimal.class,
                                                                         aSource -> BigDecimal.valueOf (aSource.doubleValue ()));
    aRegistry.registerTypeConverter (Boolean.class,
                                     BigDecimal.class,
                                     aSource -> aSource.booleanValue () ? BigDecimal.ONE : BigDecimal.ZERO);
    aRegistry.registerTypeConverter (Character.class,
                                     BigDecimal.class,
                                     aSource -> BigDecimal.valueOf (aSource.charValue ()));
    aRegistry.registerTypeConverter (Byte.class, BigDecimal.class, aSource -> BigDecimal.valueOf (aSource.intValue ()));
    aRegistry.registerTypeConverter (Integer.class,
                                     BigDecimal.class,
                                     aSource -> BigDecimal.valueOf (aSource.intValue ()));
    aRegistry.registerTypeConverter (Long.class,
                                     BigDecimal.class,
                                     aSource -> BigDecimal.valueOf (aSource.longValue ()));
    aRegistry.registerTypeConverter (Short.class,
                                     BigDecimal.class,
                                     aSource -> BigDecimal.valueOf (aSource.shortValue ()));
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (BigDecimal.class,
                                                                  aSource -> StringParser.parseBigDecimal (aSource.toString (),
                                                                                                           (BigDecimal) null));

    // to BigInteger
    aRegistry.registerTypeConverter (BigDecimal.class, BigInteger.class, BigDecimal::toBigInteger);
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                         BigInteger.class,
                                                                         aSource -> BigInteger.valueOf (aSource.longValue ()));
    aRegistry.registerTypeConverter (Boolean.class,
                                     BigInteger.class,
                                     aSource -> aSource.booleanValue () ? BigInteger.ONE : BigInteger.ZERO);
    aRegistry.registerTypeConverter (Character.class,
                                     BigInteger.class,
                                     aSource -> BigInteger.valueOf (aSource.charValue ()));
    aRegistry.registerTypeConverter (String.class, BigInteger.class, aSource -> {
      final BigDecimal aBD = StringParser.parseBigDecimal (aSource, (BigDecimal) null);
      return aBD == null ? null : aBD.toBigInteger ();
    });
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (BigInteger.class,
                                                                  aSource -> StringParser.parseBigInteger (aSource.toString (),
                                                                                                           (BigInteger) null));

    // AtomicBoolean
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (AtomicBoolean.class,
                                                                  aSource -> Boolean.valueOf (aSource.get ()));
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (AtomicBoolean.class,
                                                                  aSource -> new AtomicBoolean (TypeConverter.convert (aSource,
                                                                                                                       Boolean.class)
                                                                                                             .booleanValue ()));
    // AtomicInteger
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (AtomicInteger.class,
                                                                  aSource -> Integer.valueOf (aSource.get ()));
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (AtomicInteger.class,
                                                                  aSource -> new AtomicInteger (TypeConverter.convert (aSource,
                                                                                                                       Integer.class)
                                                                                                             .intValue ()));

    // AtomicLong
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (AtomicLong.class,
                                                                  aSource -> Long.valueOf (aSource.get ()));
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (AtomicLong.class,
                                                                  aSource -> new AtomicLong (TypeConverter.convert (aSource,
                                                                                                                    Long.class)
                                                                                                          .longValue ()));

    // to StringBuilder
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (StringBuilder.class, aSource -> {
      if (aSource instanceof final CharSequence aCS)
        return new StringBuilder (aCS);
      return new StringBuilder (TypeConverter.convert (aSource, String.class));
    });

    // to StringBuffer
    aRegistry.registerTypeConverterRuleAnySourceFixedDestination (StringBuffer.class, aSource -> {
      if (aSource instanceof final CharSequence aCS)
        return new StringBuffer (aCS);
      return new StringBuffer (TypeConverter.convert (aSource, String.class));
    });

    // Enum
    /*
     * We need to append the Enum class name, otherwise we cannot resolve it! Use the colon as it is
     * not allowed in class names.
     */
    aRegistry.registerTypeConverterRuleAssignableSourceFixedDestination (Enum.class,
                                                                         String.class,
                                                                         aSource -> aSource.getClass ().getName () +
                                                                                    ':' +
                                                                                    aSource.name ());

    aRegistry.registerTypeConverterRuleFixedSourceAssignableDestination (String.class, Enum.class, x -> {
      /*
       * Split class name and enum value name
       */
      final ICommonsList <String> aParts = StringHelper.getExploded (':', x, 2);
      try
      {
        /*
         * Resolve any enum class. Note: The explicit EChange is just here, because an explicit enum
         * type is needed. It must of course not only be EChange :)
         */
        final Class <EChange> aClass = GenericReflection.getClassFromName (aParts.get (0));
        /*
         * And look up the element by name
         */
        return Enum.valueOf (aClass, aParts.get (1));
      }
      catch (final ClassNotFoundException ex)
      {
        return null;
      }
    });

    // String[] to any
    aRegistry.registerTypeConverterRuleFixedSourceAnyDestination (String [].class, x -> {
      if (x.length == 0)
        return null;
      if (x.length > 1)
        LOGGER.warn ("An array with " + x.length + " items is present; using the first value: " + Arrays.toString (x));
      return x[0];
    });
  }
}

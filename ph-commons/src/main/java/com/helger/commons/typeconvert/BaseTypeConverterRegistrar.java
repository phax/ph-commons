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
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;
import com.helger.commons.typeconvert.rule.AbstractTypeConverterRuleAnySourceFixedDestination;
import com.helger.commons.typeconvert.rule.AbstractTypeConverterRuleAssignableSourceFixedDestination;
import com.helger.commons.typeconvert.rule.AbstractTypeConverterRuleFixedSourceAnyDestination;
import com.helger.commons.typeconvert.rule.AbstractTypeConverterRuleFixedSourceAssignableDestination;

/**
 * Register the base type converter
 *
 * @author Philip Helger
 */
@Immutable
@IsSPIImplementation
public final class BaseTypeConverterRegistrar implements ITypeConverterRegistrarSPI
{
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
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                                                        Boolean.class)
    {
      public Boolean convert (@Nonnull final Object aSource)
      {
        return Boolean.valueOf (((Number) aSource).intValue () != 0);
      }
    });
    aRegistry.registerTypeConverter (Character.class,
                                     Boolean.class,
                                     (ITypeConverter) aSource -> Boolean.valueOf (((Character) aSource).charValue () != 0));
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (Boolean.class)
    {
      public Boolean convert (@Nonnull final Object aSource)
      {
        return StringParser.parseBoolObj (aSource, (Boolean) null);
      }
    });

    // to Byte
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                                                        Byte.class)
    {
      public Byte convert (@Nonnull final Object aSource)
      {
        return Byte.valueOf (((Number) aSource).byteValue ());
      }
    });
    aRegistry.registerTypeConverter (Boolean.class,
                                     Byte.class,
                                     (ITypeConverter) aSource -> Byte.valueOf (((Boolean) aSource).booleanValue () ? (byte) 1
                                                                                                                   : (byte) 0));
    aRegistry.registerTypeConverter (Character.class,
                                     Byte.class,
                                     (ITypeConverter) aSource -> Byte.valueOf ((byte) ((Character) aSource).charValue ()));
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (Byte.class)
    {
      public Byte convert (@Nonnull final Object aSource)
      {
        return StringParser.parseByteObj (aSource, (Byte) null);
      }
    });

    // to Character
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                                                        Character.class)
    {
      public Character convert (@Nonnull final Object aSource)
      {
        return Character.valueOf ((char) ((Number) aSource).intValue ());
      }
    });
    aRegistry.registerTypeConverter (Boolean.class,
                                     Character.class,
                                     (ITypeConverter) aSource -> Character.valueOf (((Boolean) aSource).booleanValue () ? (char) 1
                                                                                                                        : (char) 0));
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (Character.class)
    {
      public Character convert (@Nonnull final Object aSource)
      {
        final String sSource = aSource.toString ();
        return sSource.length () == 1 ? Character.valueOf (sSource.charAt (0)) : null;
      }
    });

    // to Double
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                                                        Double.class)
    {
      public Double convert (@Nonnull final Object aSource)
      {
        return Double.valueOf (((Number) aSource).doubleValue ());
      }
    });
    aRegistry.registerTypeConverter (Boolean.class,
                                     Double.class,
                                     (ITypeConverter) aSource -> Double.valueOf (((Boolean) aSource).booleanValue () ? 1d
                                                                                                                     : 0d));
    aRegistry.registerTypeConverter (Character.class,
                                     Double.class,
                                     (ITypeConverter) aSource -> Double.valueOf (((Character) aSource).charValue ()));
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (Double.class)
    {
      public Double convert (@Nonnull final Object aSource)
      {
        return StringParser.parseDoubleObj (aSource, (Double) null);
      }
    });

    // to Float
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                                                        Float.class)
    {
      public Float convert (@Nonnull final Object aSource)
      {
        return Float.valueOf (((Number) aSource).floatValue ());
      }
    });
    aRegistry.registerTypeConverter (Boolean.class,
                                     Float.class,
                                     (ITypeConverter) aSource -> Float.valueOf (((Boolean) aSource).booleanValue () ? 1f
                                                                                                                    : 0f));
    aRegistry.registerTypeConverter (Character.class,
                                     Float.class,
                                     (ITypeConverter) aSource -> Float.valueOf (((Character) aSource).charValue ()));
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (Float.class)
    {
      public Float convert (@Nonnull final Object aSource)
      {
        return StringParser.parseFloatObj (aSource, (Float) null);
      }
    });

    // to Integer
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                                                        Integer.class)
    {
      public Integer convert (@Nonnull final Object aSource)
      {
        return Integer.valueOf (((Number) aSource).intValue ());
      }
    });
    aRegistry.registerTypeConverter (Boolean.class,
                                     Integer.class,
                                     (ITypeConverter) aSource -> Integer.valueOf (((Boolean) aSource).booleanValue () ? 1
                                                                                                                      : 0));
    aRegistry.registerTypeConverter (Character.class,
                                     Integer.class,
                                     (ITypeConverter) aSource -> Integer.valueOf (((Character) aSource).charValue ()));
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (Integer.class)
    {
      public Integer convert (@Nonnull final Object aSource)
      {
        return StringParser.parseIntObj (aSource, (Integer) null);
      }
    });

    // to Long
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                                                        Long.class)
    {
      public Long convert (@Nonnull final Object aSource)
      {
        return Long.valueOf (((Number) aSource).longValue ());
      }
    });
    aRegistry.registerTypeConverter (Boolean.class,
                                     Long.class,
                                     (ITypeConverter) aSource -> Long.valueOf (((Boolean) aSource).booleanValue () ? 1L
                                                                                                                   : 0L));
    aRegistry.registerTypeConverter (Character.class,
                                     Long.class,
                                     (ITypeConverter) aSource -> Long.valueOf (((Character) aSource).charValue ()));
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (Long.class)
    {
      public Long convert (@Nonnull final Object aSource)
      {
        return StringParser.parseLongObj (aSource, (Long) null);
      }
    });

    // to Short
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                                                        Short.class)
    {
      public Short convert (@Nonnull final Object aSource)
      {
        return Short.valueOf (((Number) aSource).shortValue ());
      }
    });
    aRegistry.registerTypeConverter (Boolean.class,
                                     Short.class,
                                     (ITypeConverter) aSource -> Short.valueOf (((Boolean) aSource).booleanValue () ? (short) 1
                                                                                                                    : (short) 0));
    aRegistry.registerTypeConverter (Character.class,
                                     Short.class,
                                     (ITypeConverter) aSource -> Short.valueOf ((short) ((Character) aSource).charValue ()));
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (Short.class)
    {
      public Short convert (@Nonnull final Object aSource)
      {
        return StringParser.parseShortObj (aSource, (Short) null);
      }
    });

    // to String
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (String.class)
    {
      public String convert (@Nonnull final Object aSource)
      {
        return aSource.toString ();
      }
    });

    // to BigDecimal
    aRegistry.registerTypeConverter (BigInteger.class,
                                     BigDecimal.class,
                                     (ITypeConverter) aSource -> new BigDecimal ((BigInteger) aSource));
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                                                        BigDecimal.class)
    {
      public BigDecimal convert (@Nonnull final Object aSource)
      {
        return BigDecimal.valueOf (((Number) aSource).doubleValue ());
      }
    });
    aRegistry.registerTypeConverter (Boolean.class,
                                     BigDecimal.class,
                                     (ITypeConverter) aSource -> ((Boolean) aSource).booleanValue () ? BigDecimal.ONE
                                                                                                     : BigDecimal.ZERO);
    aRegistry.registerTypeConverter (Character.class,
                                     BigDecimal.class,
                                     (ITypeConverter) aSource -> BigDecimal.valueOf (((Character) aSource).charValue ()));
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (BigDecimal.class)
    {
      public BigDecimal convert (@Nonnull final Object aSource)
      {
        return StringParser.parseBigDecimal (aSource.toString (), (BigDecimal) null);
      }
    });

    // to BigInteger
    aRegistry.registerTypeConverter (BigDecimal.class,
                                     BigInteger.class,
                                     (ITypeConverter) aSource -> ((BigDecimal) aSource).toBigInteger ());
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAssignableSourceFixedDestination (Number.class,
                                                                                                        BigInteger.class)
    {
      public BigInteger convert (@Nonnull final Object aSource)
      {
        return BigInteger.valueOf (((Number) aSource).longValue ());
      }
    });
    aRegistry.registerTypeConverter (Boolean.class,
                                     BigInteger.class,
                                     (ITypeConverter) aSource -> ((Boolean) aSource).booleanValue () ? BigInteger.ONE
                                                                                                     : BigInteger.ZERO);
    aRegistry.registerTypeConverter (Character.class,
                                     BigInteger.class,
                                     (ITypeConverter) aSource -> BigInteger.valueOf (((Character) aSource).charValue ()));
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (BigInteger.class)
    {
      public BigInteger convert (@Nonnull final Object aSource)
      {
        return StringParser.parseBigInteger (aSource.toString (), (BigInteger) null);
      }
    });

    // AtomicBoolean
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleFixedSourceAnyDestination (AtomicBoolean.class)
    {
      @Override
      protected Object getInBetweenValue (@Nonnull final Object aSource)
      {
        return Boolean.valueOf (((AtomicBoolean) aSource).get ());
      }
    });
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (AtomicBoolean.class)
    {
      public AtomicBoolean convert (@Nonnull final Object aSource)
      {
        return new AtomicBoolean (TypeConverter.convertIfNecessary (aSource, Boolean.class).booleanValue ());
      }
    });

    // AtomicInteger
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleFixedSourceAnyDestination (AtomicInteger.class)
    {
      @Override
      protected Object getInBetweenValue (@Nonnull final Object aSource)
      {
        return Integer.valueOf (((AtomicInteger) aSource).get ());
      }
    });
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (AtomicInteger.class)
    {
      public AtomicInteger convert (@Nonnull final Object aSource)
      {
        return new AtomicInteger (TypeConverter.convertIfNecessary (aSource, Integer.class).intValue ());
      }
    });

    // AtomicLong
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleFixedSourceAnyDestination (AtomicLong.class)
    {
      @Override
      protected Object getInBetweenValue (@Nonnull final Object aSource)
      {
        return Long.valueOf (((AtomicLong) aSource).get ());
      }
    });
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (AtomicLong.class)
    {
      public AtomicLong convert (@Nonnull final Object aSource)
      {
        return new AtomicLong (TypeConverter.convertIfNecessary (aSource, Long.class).longValue ());
      }
    });

    // to StringBuilder
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (StringBuilder.class)
    {
      public StringBuilder convert (@Nonnull final Object aSource)
      {
        if (aSource instanceof CharSequence)
          return new StringBuilder ((CharSequence) aSource);
        return new StringBuilder (TypeConverter.convertIfNecessary (aSource, String.class));
      }
    });

    // to StringBuffer
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (StringBuffer.class)
    {
      public StringBuffer convert (@Nonnull final Object aSource)
      {
        if (aSource instanceof CharSequence)
          return new StringBuffer ((CharSequence) aSource);
        return new StringBuffer (TypeConverter.convertIfNecessary (aSource, String.class));
      }
    });

    // Enum
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAssignableSourceFixedDestination (Enum.class,
                                                                                                        String.class)
    {
      @Nonnull
      public String convert (@Nonnull final Object aSource)
      {
        // We need to append the Enum class name, otherwise we cannot resolve
        // it! Use the colon as it is not allowed in class names.
        return aSource.getClass ().getName () + ':' + ((Enum <?>) aSource).name ();
      }
    });
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleFixedSourceAssignableDestination (String.class,
                                                                                                        Enum.class)
    {
      public Enum <?> convert (@Nonnull final Object aSource)
      {
        // Split class name and enum value name
        final List <String> aParts = StringHelper.getExploded (':', (String) aSource, 2);
        try
        {
          // Resolve any enum class
          // Note: The explicit EChange is just here, because an explicit enum
          // type is needed. It must of course not only be EChange :)
          final Class <EChange> aClass = GenericReflection.getClassFromName (aParts.get (0));

          // And look up the element by name
          return Enum.valueOf (aClass, aParts.get (1));
        }
        catch (final ClassNotFoundException ex)
        {
          return null;
        }
      }
    });
  }
}

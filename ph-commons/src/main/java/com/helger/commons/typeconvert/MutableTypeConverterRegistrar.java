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
import javax.annotation.Nullable;
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
import com.helger.commons.typeconvert.rule.AbstractTypeConverterRuleAnySourceFixedDestination;
import com.helger.commons.typeconvert.rule.AbstractTypeConverterRuleFixedSourceAnyDestination;

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
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleFixedSourceAnyDestination (MutableBigDecimal.class)
    {
      @Override
      @Nonnull
      protected Object getInBetweenValue (@Nonnull final Object aSource)
      {
        return ((MutableBigDecimal) aSource).getAsBigDecimal ();
      }
    });
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (MutableBigDecimal.class)
    {
      @Nullable
      public MutableBigDecimal apply (@Nonnull final Object aSource)
      {
        return new MutableBigDecimal (TypeConverter.convertIfNecessary (aSource, BigDecimal.class));
      }
    });

    // MutableBigInteger
    aRegistry.registerTypeConverter (MutableBigInteger.class,
                                     BigInteger.class,
                                     (ITypeConverter) aSource -> ((MutableBigInteger) aSource).getAsBigInteger ());
    aRegistry.registerTypeConverter (BigInteger.class,
                                     MutableBigInteger.class,
                                     (ITypeConverter) aSource -> new MutableBigInteger ((BigInteger) aSource));
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleFixedSourceAnyDestination (MutableBigInteger.class)
    {
      @Override
      @Nonnull
      protected Object getInBetweenValue (@Nonnull final Object aSource)
      {
        return ((MutableBigInteger) aSource).getAsBigInteger ();
      }
    });
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (MutableBigInteger.class)
    {
      @Nullable
      public MutableBigInteger apply (@Nonnull final Object aSource)
      {
        return new MutableBigInteger (TypeConverter.convertIfNecessary (aSource, BigInteger.class));
      }
    });

    // MutableBoolean
    aRegistry.registerTypeConverter (MutableBoolean.class,
                                     Boolean.class,
                                     (ITypeConverter) aSource -> ((MutableBoolean) aSource).getAsBoolean ());
    aRegistry.registerTypeConverter (Boolean.class,
                                     MutableBoolean.class,
                                     (ITypeConverter) aSource -> new MutableBoolean ((Boolean) aSource));
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleFixedSourceAnyDestination (MutableBoolean.class)
    {
      @Override
      @Nonnull
      protected Object getInBetweenValue (@Nonnull final Object aSource)
      {
        return ((MutableBoolean) aSource).getAsBoolean ();
      }
    });
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (MutableBoolean.class)
    {
      @Nullable
      public MutableBoolean apply (@Nonnull final Object aSource)
      {
        return new MutableBoolean (TypeConverter.convertIfNecessary (aSource, Boolean.class));
      }
    });

    // MutableByte
    aRegistry.registerTypeConverter (MutableByte.class,
                                     Byte.class,
                                     (ITypeConverter) aSource -> ((MutableByte) aSource).getAsByte ());
    aRegistry.registerTypeConverter (Byte.class,
                                     MutableByte.class,
                                     (ITypeConverter) aSource -> new MutableByte ((Byte) aSource));
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleFixedSourceAnyDestination (MutableByte.class)
    {
      @Override
      @Nonnull
      protected Object getInBetweenValue (@Nonnull final Object aSource)
      {
        return ((MutableByte) aSource).getAsByte ();
      }
    });
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (MutableByte.class)
    {
      @Nullable
      public MutableByte apply (@Nonnull final Object aSource)
      {
        return new MutableByte (TypeConverter.convertIfNecessary (aSource, Byte.class));
      }
    });

    // MutableChar
    aRegistry.registerTypeConverter (MutableChar.class,
                                     Character.class,
                                     (ITypeConverter) aSource -> ((MutableChar) aSource).getAsCharacter ());
    aRegistry.registerTypeConverter (Character.class,
                                     MutableChar.class,
                                     (ITypeConverter) aSource -> new MutableChar ((Character) aSource));
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleFixedSourceAnyDestination (MutableChar.class)
    {
      @Override
      @Nonnull
      protected Object getInBetweenValue (@Nonnull final Object aSource)
      {
        return ((MutableChar) aSource).getAsCharacter ();
      }
    });
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (MutableChar.class)
    {
      @Nullable
      public MutableChar apply (@Nonnull final Object aSource)
      {
        return new MutableChar (TypeConverter.convertIfNecessary (aSource, Character.class));
      }
    });

    // MutableDouble
    aRegistry.registerTypeConverter (MutableDouble.class,
                                     Double.class,
                                     (ITypeConverter) aSource -> ((MutableDouble) aSource).getAsDouble ());
    aRegistry.registerTypeConverter (Double.class,
                                     MutableDouble.class,
                                     (ITypeConverter) aSource -> new MutableDouble ((Double) aSource));
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleFixedSourceAnyDestination (MutableDouble.class)
    {
      @Override
      @Nonnull
      protected Object getInBetweenValue (@Nonnull final Object aSource)
      {
        return ((MutableDouble) aSource).getAsDouble ();
      }
    });
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (MutableDouble.class)
    {
      @Nullable
      public MutableDouble apply (@Nonnull final Object aSource)
      {
        return new MutableDouble (TypeConverter.convertIfNecessary (aSource, Double.class));
      }
    });

    // MutableFloat
    aRegistry.registerTypeConverter (MutableFloat.class,
                                     Float.class,
                                     (ITypeConverter) aSource -> ((MutableFloat) aSource).getAsFloat ());
    aRegistry.registerTypeConverter (Float.class,
                                     MutableFloat.class,
                                     (ITypeConverter) aSource -> new MutableFloat ((Float) aSource));
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleFixedSourceAnyDestination (MutableFloat.class)
    {
      @Override
      @Nonnull
      protected Object getInBetweenValue (@Nonnull final Object aSource)
      {
        return ((MutableFloat) aSource).getAsFloat ();
      }
    });
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (MutableFloat.class)
    {
      @Nullable
      public MutableFloat apply (@Nonnull final Object aSource)
      {
        return new MutableFloat (TypeConverter.convertIfNecessary (aSource, Float.class));
      }
    });

    // MutableInt
    aRegistry.registerTypeConverter (MutableInt.class,
                                     Integer.class,
                                     (ITypeConverter) aSource -> ((MutableInt) aSource).getAsInteger ());
    aRegistry.registerTypeConverter (Integer.class,
                                     MutableInt.class,
                                     (ITypeConverter) aSource -> new MutableInt ((Integer) aSource));
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleFixedSourceAnyDestination (MutableInt.class)
    {
      @Override
      @Nonnull
      protected Object getInBetweenValue (@Nonnull final Object aSource)
      {
        return ((MutableInt) aSource).getAsInteger ();
      }
    });
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (MutableInt.class)
    {
      @Nullable
      public MutableInt apply (@Nonnull final Object aSource)
      {
        return new MutableInt (TypeConverter.convertIfNecessary (aSource, Integer.class));
      }
    });

    // MutableLong
    aRegistry.registerTypeConverter (MutableLong.class,
                                     Long.class,
                                     (ITypeConverter) aSource -> ((MutableLong) aSource).getAsLong ());
    aRegistry.registerTypeConverter (Long.class,
                                     MutableLong.class,
                                     (ITypeConverter) aSource -> new MutableLong ((Long) aSource));
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleFixedSourceAnyDestination (MutableLong.class)
    {
      @Override
      @Nonnull
      protected Object getInBetweenValue (@Nonnull final Object aSource)
      {
        return ((MutableLong) aSource).getAsLong ();
      }
    });
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (MutableLong.class)
    {
      @Nullable
      public MutableLong apply (@Nonnull final Object aSource)
      {
        return new MutableLong (TypeConverter.convertIfNecessary (aSource, Long.class));
      }
    });

    // MutableShort
    aRegistry.registerTypeConverter (MutableShort.class,
                                     Short.class,
                                     (ITypeConverter) aSource -> ((MutableShort) aSource).getAsShort ());
    aRegistry.registerTypeConverter (Short.class,
                                     MutableShort.class,
                                     (ITypeConverter) aSource -> new MutableShort ((Short) aSource));
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleFixedSourceAnyDestination (MutableShort.class)
    {
      @Override
      @Nonnull
      protected Object getInBetweenValue (@Nonnull final Object aSource)
      {
        return ((MutableShort) aSource).getAsShort ();
      }
    });
    aRegistry.registerTypeConverterRule (new AbstractTypeConverterRuleAnySourceFixedDestination (MutableShort.class)
    {
      @Nullable
      public MutableShort apply (@Nonnull final Object aSource)
      {
        return new MutableShort (TypeConverter.convertIfNecessary (aSource, Short.class));
      }
    });
  }
}

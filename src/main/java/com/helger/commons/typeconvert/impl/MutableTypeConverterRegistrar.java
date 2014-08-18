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
package com.helger.commons.typeconvert.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotations.IsSPIImplementation;
import com.helger.commons.mutable.MutableBoolean;
import com.helger.commons.mutable.MutableByte;
import com.helger.commons.mutable.MutableChar;
import com.helger.commons.mutable.MutableDouble;
import com.helger.commons.mutable.MutableFloat;
import com.helger.commons.mutable.MutableInt;
import com.helger.commons.mutable.MutableLong;
import com.helger.commons.mutable.MutableShort;
import com.helger.commons.typeconvert.ITypeConverter;
import com.helger.commons.typeconvert.ITypeConverterRegistrarSPI;
import com.helger.commons.typeconvert.ITypeConverterRegistry;
import com.helger.commons.typeconvert.TypeConverter;
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
    // MutableBoolean
    aRegistry.registerTypeConverter (MutableBoolean.class, Boolean.class, new ITypeConverter ()
    {
      public Boolean convert (@Nonnull final Object aSource)
      {
        return ((MutableBoolean) aSource).getAsBoolean ();
      }
    });
    aRegistry.registerTypeConverter (Boolean.class, MutableBoolean.class, new ITypeConverter ()
    {
      public MutableBoolean convert (@Nonnull final Object aSource)
      {
        return new MutableBoolean ((Boolean) aSource);
      }
    });
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
      public MutableBoolean convert (@Nonnull final Object aSource)
      {
        return new MutableBoolean (TypeConverter.convertIfNecessary (aSource, Boolean.class));
      }
    });

    // MutableByte
    aRegistry.registerTypeConverter (MutableByte.class, Byte.class, new ITypeConverter ()
    {
      public Byte convert (@Nonnull final Object aSource)
      {
        return ((MutableByte) aSource).getAsByte ();
      }
    });
    aRegistry.registerTypeConverter (Byte.class, MutableByte.class, new ITypeConverter ()
    {
      public MutableByte convert (@Nonnull final Object aSource)
      {
        return new MutableByte ((Byte) aSource);
      }
    });
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
      public MutableByte convert (@Nonnull final Object aSource)
      {
        return new MutableByte (TypeConverter.convertIfNecessary (aSource, Byte.class));
      }
    });

    // MutableChar
    aRegistry.registerTypeConverter (MutableChar.class, Character.class, new ITypeConverter ()
    {
      public Character convert (@Nonnull final Object aSource)
      {
        return ((MutableChar) aSource).getAsCharacter ();
      }
    });
    aRegistry.registerTypeConverter (Character.class, MutableChar.class, new ITypeConverter ()
    {
      public MutableChar convert (@Nonnull final Object aSource)
      {
        return new MutableChar ((Character) aSource);
      }
    });
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
      public MutableChar convert (@Nonnull final Object aSource)
      {
        return new MutableChar (TypeConverter.convertIfNecessary (aSource, Character.class));
      }
    });

    // MutableDouble
    aRegistry.registerTypeConverter (MutableDouble.class, Double.class, new ITypeConverter ()
    {
      public Double convert (@Nonnull final Object aSource)
      {
        return ((MutableDouble) aSource).getAsDouble ();
      }
    });
    aRegistry.registerTypeConverter (Double.class, MutableDouble.class, new ITypeConverter ()
    {
      public MutableDouble convert (@Nonnull final Object aSource)
      {
        return new MutableDouble ((Double) aSource);
      }
    });
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
      public MutableDouble convert (@Nonnull final Object aSource)
      {
        return new MutableDouble (TypeConverter.convertIfNecessary (aSource, Double.class));
      }
    });

    // MutableFloat
    aRegistry.registerTypeConverter (MutableFloat.class, Float.class, new ITypeConverter ()
    {
      public Float convert (@Nonnull final Object aSource)
      {
        return ((MutableFloat) aSource).getAsFloat ();
      }
    });
    aRegistry.registerTypeConverter (Float.class, MutableFloat.class, new ITypeConverter ()
    {
      public MutableFloat convert (@Nonnull final Object aSource)
      {
        return new MutableFloat ((Float) aSource);
      }
    });
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
      public MutableFloat convert (@Nonnull final Object aSource)
      {
        return new MutableFloat (TypeConverter.convertIfNecessary (aSource, Float.class));
      }
    });

    // MutableInt
    aRegistry.registerTypeConverter (MutableInt.class, Integer.class, new ITypeConverter ()
    {
      public Integer convert (@Nonnull final Object aSource)
      {
        return ((MutableInt) aSource).getAsInteger ();
      }
    });
    aRegistry.registerTypeConverter (Integer.class, MutableInt.class, new ITypeConverter ()
    {
      public MutableInt convert (@Nonnull final Object aSource)
      {
        return new MutableInt ((Integer) aSource);
      }
    });
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
      public MutableInt convert (@Nonnull final Object aSource)
      {
        return new MutableInt (TypeConverter.convertIfNecessary (aSource, Integer.class));
      }
    });

    // MutableLong
    aRegistry.registerTypeConverter (MutableLong.class, Long.class, new ITypeConverter ()
    {
      public Long convert (@Nonnull final Object aSource)
      {
        return ((MutableLong) aSource).getAsLong ();
      }
    });
    aRegistry.registerTypeConverter (Long.class, MutableLong.class, new ITypeConverter ()
    {
      public MutableLong convert (@Nonnull final Object aSource)
      {
        return new MutableLong ((Long) aSource);
      }
    });
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
      public MutableLong convert (@Nonnull final Object aSource)
      {
        return new MutableLong (TypeConverter.convertIfNecessary (aSource, Long.class));
      }
    });

    // MutableShort
    aRegistry.registerTypeConverter (MutableShort.class, Short.class, new ITypeConverter ()
    {
      public Short convert (@Nonnull final Object aSource)
      {
        return ((MutableShort) aSource).getAsShort ();
      }
    });
    aRegistry.registerTypeConverter (Short.class, MutableShort.class, new ITypeConverter ()
    {
      public MutableShort convert (@Nonnull final Object aSource)
      {
        return new MutableShort ((Short) aSource);
      }
    });
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
      public MutableShort convert (@Nonnull final Object aSource)
      {
        return new MutableShort (TypeConverter.convertIfNecessary (aSource, Short.class));
      }
    });
  }
}

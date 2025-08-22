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
package com.helger.typeconvert.impl;

import com.helger.base.traits.ITypeConverterTo;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * An instance of {@link ITypeConverterTo} for the usage with arbitrary target classes. They must be
 * registered in the TypeConverter.
 *
 * @author Philip Helger
 * @param <DST>
 *        Target type
 * @since 12.0.0 RC2
 */
public class TypeConverterToAny <DST> implements ITypeConverterTo <DST>
{
  private final Class <DST> m_aDestClass;

  public TypeConverterToAny (@Nonnull final Class <DST> aClass)
  {
    m_aDestClass = aClass;
  }

  @Nonnull
  public DST convert (final boolean value)
  {
    return TypeConverter.convert (value, m_aDestClass);
  }

  @Nonnull
  public DST convert (final byte value)
  {
    return TypeConverter.convert (value, m_aDestClass);
  }

  @Nonnull
  public DST convert (final char value)
  {
    return TypeConverter.convert (value, m_aDestClass);
  }

  @Nonnull
  public DST convert (final double value)
  {
    return TypeConverter.convert (value, m_aDestClass);
  }

  @Nonnull
  public DST convert (final float value)
  {
    return TypeConverter.convert (value, m_aDestClass);
  }

  @Nonnull
  public DST convert (final int value)
  {
    return TypeConverter.convert (value, m_aDestClass);
  }

  @Nonnull
  public DST convert (final long value)
  {
    return TypeConverter.convert (value, m_aDestClass);
  }

  @Nonnull
  public DST convert (final short value)
  {
    return TypeConverter.convert (value, m_aDestClass);
  }

  @Nonnull
  public DST convert (@Nullable final Object value)
  {
    return TypeConverter.convert (value, m_aDestClass);
  }

  @Nonnull
  public static <T> TypeConverterToAny <T> of (@Nonnull final Class <T> aClass)
  {
    return new TypeConverterToAny <> (aClass);
  }
}

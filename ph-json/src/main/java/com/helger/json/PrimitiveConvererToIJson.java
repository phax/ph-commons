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
package com.helger.json;

import com.helger.base.traits.IPrimitiveConverterTo;
import com.helger.json.convert.JsonConverter;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * An instance of {@link IPrimitiveConverterTo} for the usage with JSON.
 *
 * @author Philip Helger
 */
public class PrimitiveConvererToIJson implements IPrimitiveConverterTo <IJson>
{
  public static final PrimitiveConvererToIJson INSTANCE = new PrimitiveConvererToIJson ();

  protected PrimitiveConvererToIJson ()
  {}

  @Nonnull
  public IJson convert (final boolean value)
  {
    return JsonValue.create (value);
  }

  @Nonnull
  public IJson convert (final byte value)
  {
    return convert ((int) value);
  }

  @Nonnull
  public IJson convert (final char value)
  {
    return JsonValue.create (value);
  }

  @Nonnull
  public IJson convert (final double value)
  {
    return JsonValue.create (value);
  }

  @Nonnull
  public IJson convert (final float value)
  {
    return convert ((double) value);
  }

  @Nonnull
  public IJson convert (final int value)
  {
    return JsonValue.create (value);
  }

  @Nonnull
  public IJson convert (final long value)
  {
    return JsonValue.create (value);
  }

  @Nonnull
  public IJson convert (final short value)
  {
    return convert ((int) value);
  }

  @Nonnull
  public IJson convert (@Nullable final Object value)
  {
    return JsonConverter.convertToJson (value);
  }
}

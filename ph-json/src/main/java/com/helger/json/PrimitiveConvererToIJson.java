package com.helger.json;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.traits.IPrimitiveConverterTo;
import com.helger.json.convert.JsonConverter;

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
    return JsonValue.create (value);
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
    return JsonValue.create (value);
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
    return JsonValue.create (value);
  }

  @Nonnull
  public IJson convert (@Nullable final Object value)
  {
    return JsonConverter.convertToJson (value);
  }
}

/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.base.trait;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A minimal value type used by {@link MockAdderTrait} and {@link MockMapAdderTrait}.
 *
 * @author Philip Helger
 */
public final class MockStringValue implements IAddableByTrait
{
  /** Converts everything to its String representation */
  public static final ITypeConverterTo <MockStringValue> CONVERTER = new ITypeConverterTo <> ()
  {
    public MockStringValue convert (final boolean value)
    {
      return new MockStringValue (Boolean.toString (value));
    }

    public MockStringValue convert (final byte value)
    {
      return new MockStringValue (Byte.toString (value));
    }

    public MockStringValue convert (final char value)
    {
      return new MockStringValue (Character.toString (value));
    }

    public MockStringValue convert (final double value)
    {
      return new MockStringValue (Double.toString (value));
    }

    public MockStringValue convert (final float value)
    {
      return new MockStringValue (Float.toString (value));
    }

    public MockStringValue convert (final int value)
    {
      return new MockStringValue (Integer.toString (value));
    }

    public MockStringValue convert (final long value)
    {
      return new MockStringValue (Long.toString (value));
    }

    public MockStringValue convert (final short value)
    {
      return new MockStringValue (Short.toString (value));
    }

    public MockStringValue convert (@Nullable final Object value)
    {
      return value == null ? null : new MockStringValue (String.valueOf (value));
    }
  };

  private final String m_sValue;

  public MockStringValue (@NonNull final String sValue)
  {
    m_sValue = sValue;
  }

  @NonNull
  public String getValue ()
  {
    return m_sValue;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    return m_sValue.equals (((MockStringValue) o).m_sValue);
  }

  @Override
  public int hashCode ()
  {
    return m_sValue.hashCode ();
  }

  @Override
  public String toString ()
  {
    return m_sValue;
  }
}

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
package com.helger.json.valueserializer;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.typeconvert.TypeConverter;

/**
 * A {@link IJsonValueSerializer} implementation that uses {@link TypeConverter}
 * to convert the value to a {@link String}.
 *
 * @author Philip Helger
 */
public final class JsonValueSerializerTypeConverterToString implements IJsonValueSerializer
{
  private static final JsonValueSerializerTypeConverterToString INSTANCE = new JsonValueSerializerTypeConverterToString ();

  private JsonValueSerializerTypeConverterToString ()
  {}

  @Nonnull
  public static JsonValueSerializerTypeConverterToString getInstance ()
  {
    return INSTANCE;
  }

  public void appendAsJsonString (@Nullable final Object aValue, @Nonnull @WillNotClose final Writer aWriter) throws IOException
  {
    ValueEnforcer.notNull (aWriter, "Writer");

    final String sValue = TypeConverter.convert (aValue, String.class);
    aWriter.write (sValue);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).getToString ();
  }
}

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
package com.helger.json.valueserializer;

import java.io.IOException;
import java.io.Writer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.WillNotClose;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.json.convert.JsonEscapeHelper;

/**
 * A special {@link IJsonValueSerializer} that writes an escaped string.
 *
 * @author Philip Helger
 */
public final class JsonValueSerializerEscaped implements IJsonValueSerializer
{
  private static final JsonValueSerializerEscaped INSTANCE = new JsonValueSerializerEscaped ();

  private JsonValueSerializerEscaped ()
  {}

  @NonNull
  public static JsonValueSerializerEscaped getInstance ()
  {
    return INSTANCE;
  }

  public static void appendEscapedJsonString (@NonNull final String sValue, @NonNull @WillNotClose final Writer aWriter) throws IOException
  {
    aWriter.write ('"');
    JsonEscapeHelper.jsonEscapeToWriter (sValue, aWriter);
    aWriter.write ('"');
  }

  public void appendAsJsonString (@Nullable final Object aValue, @NonNull @WillNotClose final Writer aWriter) throws IOException
  {
    appendEscapedJsonString (String.valueOf (aValue), aWriter);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).getToString ();
  }
}

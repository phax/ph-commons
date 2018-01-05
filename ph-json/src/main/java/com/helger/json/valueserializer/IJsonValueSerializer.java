/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import com.helger.json.IJsonValue;

/**
 * Interface for handling the serialization of simple {@link IJsonValue}
 * objects.
 *
 * @author Philip Helger
 */
public interface IJsonValueSerializer
{
  /**
   * Append the textual representation of the passed value to the passed
   * {@link Writer}.
   *
   * @param aValue
   *        The native value to use. May be <code>null</code>. Note: this is not
   *        the {@link IJsonValue} but the inner object.
   * @param aWriter
   *        the {@link Writer} to append the string representation to. Never
   *        <code>null</code>.
   * @throws IOException
   *         in case of a write error
   */
  void appendAsJsonString (@Nullable Object aValue, @Nonnull @WillNotClose Writer aWriter) throws IOException;
}

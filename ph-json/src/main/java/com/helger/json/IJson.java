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
package com.helger.json;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.lang.ICloneable;
import com.helger.json.serialize.IJsonWriterSettings;
import com.helger.json.serialize.JsonWriter;
import com.helger.json.serialize.JsonWriterSettings;

/**
 * Base interface for all JSON object types: list, object and key-value-pair
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public interface IJson extends ICloneable <IJson>, Serializable
{
  /**
   * @return <code>true</code> if it is an array and can be case to
   *         {@link IJsonArray}, <code>false</code> otherwise.
   */
  boolean isArray ();

  /**
   * @return this as an {@link IJsonArray} or <code>null</code> if
   *         {@link #isArray()} returned <code>false</code>
   */
  @Nullable
  default IJsonArray getAsArray ()
  {
    return isArray () ? (IJsonArray) this : null;
  }

  /**
   * @return <code>true</code> if it is an array and can be case to
   *         {@link IJsonObject}, <code>false</code> otherwise.
   */
  boolean isObject ();

  /**
   * @return this as an {@link IJsonObject} or <code>null</code> if
   *         {@link #isObject()} returned <code>false</code>
   */
  @Nullable
  default IJsonObject getAsObject ()
  {
    return isObject () ? (IJsonObject) this : null;
  }

  /**
   * @return <code>true</code> if it is an array and can be case to
   *         {@link IJsonValue}, <code>false</code> otherwise.
   */
  boolean isValue ();

  /**
   * @return this as an {@link IJsonValue} or <code>null</code> if
   *         {@link #isValue()} returned <code>false</code>
   */
  @Nullable
  default IJsonValue getAsValue ()
  {
    return isValue () ? (IJsonValue) this : null;
  }

  /**
   * Convert this JSON element to a string.
   *
   * @return The non-<code>null</code> String (serialization) representation of
   *         this JSON object using the default settings.
   * @see #getAsJsonString(IJsonWriterSettings)
   * @see JsonWriterSettings#DEFAULT_SETTINGS
   * @see JsonWriter
   */
  @Nonnull
  default String getAsJsonString ()
  {
    return getAsJsonString (JsonWriterSettings.DEFAULT_SETTINGS);
  }

  /**
   * Convert this JSON element to a string using the provided settings.
   *
   * @param aWriterSettings
   *        The Json writer settings to be used.
   * @return The non-<code>null</code> string representation of this object.
   * @see #getAsJsonString()
   * @see JsonWriter
   */
  @Nonnull
  default String getAsJsonString (@Nonnull final IJsonWriterSettings aWriterSettings)
  {
    return new JsonWriter (aWriterSettings).writeAsString (this);
  }
}

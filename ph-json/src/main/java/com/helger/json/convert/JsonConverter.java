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
package com.helger.json.convert;

import java.util.Collection;
import java.util.Map;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.lang.ClassHelper;
import com.helger.commons.typeconvert.TypeConverter;
import com.helger.json.IHasJson;
import com.helger.json.IJson;
import com.helger.json.IJsonArray;
import com.helger.json.IJsonObject;
import com.helger.json.JsonArray;
import com.helger.json.JsonObject;
import com.helger.json.JsonValue;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A utility class for converting objects from and to {@link IJson}.
 *
 * @author Philip Helger
 */
@Immutable
public final class JsonConverter
{
  @PresentForCodeCoverage
  private static final JsonConverter INSTANCE = new JsonConverter ();

  private JsonConverter ()
  {}

  /**
   * Convert any Object to an {@link IJson} representation. Supported classes are:
   * <ul>
   * <li><code>null</code></li>
   * <li>com.helger.json.IJson</li>
   * <li>com.helger.json.IJsonProvider</li>
   * <li>boolean []</li>
   * <li>byte []</li>
   * <li>char []</li>
   * <li>double []</li>
   * <li>float []</li>
   * <li>int []</li>
   * <li>long []</li>
   * <li>short []</li>
   * <li>Object [] - recursive invocation</li>
   * <li>Collection&lt;?&gt; - recursive invocation</li>
   * <li>Map&lt;?,?&gt; - recursive invocation, key must be a String!</li>
   * </ul>
   * All other objects are put "as is" into {@link JsonValue} objects.
   *
   * @param aObject
   *        Source Object to convert May be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static IJson convertToJson (@Nullable final Object aObject)
  {
    if (aObject == null)
      return JsonValue.NULL;

    if (aObject instanceof final IJson aJson)
      return aJson;

    if (aObject instanceof final IHasJson aHasJson)
      return aHasJson.getAsJson ();

    if (ClassHelper.isArray (aObject))
    {
      if (aObject instanceof final boolean [] aArray)
        return new JsonArray (aArray.length).addAll (aArray);

      if (aObject instanceof final byte [] aArray)
        return new JsonArray (aArray.length).addAll (aArray);

      if (aObject instanceof final char [] aArray)
        return new JsonArray (aArray.length).addAll (aArray);

      if (aObject instanceof final double [] aArray)
        return new JsonArray (aArray.length).addAll (aArray);

      if (aObject instanceof final float [] aArray)
        return new JsonArray (aArray.length).addAll (aArray);

      if (aObject instanceof final int [] aArray)
        return new JsonArray (aArray.length).addAll (aArray);

      if (aObject instanceof final long [] aArray)
        return new JsonArray (aArray.length).addAll (aArray);

      if (aObject instanceof final short [] aArray)
        return new JsonArray (aArray.length).addAll (aArray);

      if (aObject instanceof final Object [] aArray)
      {
        final IJsonArray aJsonArray = new JsonArray (aArray.length);
        for (final Object aValue : aArray)
        {
          // Recursive conversion
          aJsonArray.add (convertToJson (aValue));
        }
        return aJsonArray;
      }
      throw new IllegalStateException ("Expected an array but got none. Object=" + aObject);
    }

    if (aObject instanceof final Collection <?> aCollection)
    {
      final IJsonArray aJsonArray = new JsonArray (aCollection.size ());
      for (final Object aValue : aCollection)
      {
        // Recursive conversion
        aJsonArray.add (convertToJson (aValue));
      }
      return aJsonArray;
    }

    if (aObject instanceof final Map <?, ?> aMap)
    {
      final IJsonObject aJsonObject = new JsonObject (aMap.size ());
      for (final Map.Entry <?, ?> aEntry : aMap.entrySet ())
      {
        final String sKey = TypeConverter.convert (aEntry.getKey (), String.class);
        if (sKey == null)
          throw new IllegalArgumentException ("Map key '" + aEntry.getKey () + "' could not be converted to a String!");
        // Recursive conversion
        final IJson aValue = convertToJson (aEntry.getValue ());
        aJsonObject.addJson (sKey, aValue);
      }
      return aJsonObject;
    }

    // Assume it is a simple value
    return JsonValue.create (aObject);
  }
}

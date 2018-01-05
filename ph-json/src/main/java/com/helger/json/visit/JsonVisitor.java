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
package com.helger.json.visit;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.json.IJson;
import com.helger.json.IJsonArray;
import com.helger.json.IJsonObject;
import com.helger.json.IJsonValue;

/**
 * Recursively visit all Json nodes and invoke the provided callback.
 *
 * @author Philip Helger
 */
@Immutable
public final class JsonVisitor
{
  @PresentForCodeCoverage
  private static final JsonVisitor s_aInstance = new JsonVisitor ();

  private JsonVisitor ()
  {}

  private static void _recursiveVisit (@Nonnull final IJson aJson, @Nonnull final IJsonVisitorCallback aCB)
  {
    if (aJson.isValue ())
    {
      // Simple value
      aCB.onJsonValue ((IJsonValue) aJson);
    }
    else
      // Complex (array or object)
      if (aJson.isArray ())
      {
        final IJsonArray aArray = (IJsonArray) aJson;
        aCB.onJsonArrayStart (aArray);

        for (final IJson aChild : aArray)
          _recursiveVisit (aChild, aCB);

        aCB.onJsonArrayEnd (aArray);
      }
      else
      {
        // Must be an object
        final IJsonObject aObject = (IJsonObject) aJson;
        aCB.onJsonObjectStart (aObject);

        for (final Map.Entry <String, IJson> aEntry : aObject)
        {
          aCB.onJsonObjectElementName (aEntry.getKey ());
          // Object value
          _recursiveVisit (aEntry.getValue (), aCB);
        }

        aCB.onJsonObjectEnd (aObject);
      }
  }

  /**
   * Visit the whole JSON.
   *
   * @param aJson
   *        The JSON to visit. May not be <code>null</code>.
   * @param aCB
   *        The callback to be invoked. May not be <code>null</code>.
   */
  public static void visit (@Nonnull final IJson aJson, @Nonnull final IJsonVisitorCallback aCB)
  {
    ValueEnforcer.notNull (aJson, "JSON");
    ValueEnforcer.notNull (aCB, "Callback");
    _recursiveVisit (aJson, aCB);
  }
}

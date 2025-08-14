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
package com.helger.json.mapping;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.string.Strings;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.lang.StackTraceHelper;
import com.helger.commons.location.ILocation;
import com.helger.commons.location.SimpleLocation;
import com.helger.commons.state.ETriState;
import com.helger.commons.string.StringHelper;
import com.helger.json.IJsonObject;
import com.helger.json.JsonObject;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Contains some JSON mappings to commonly used data types.
 *
 * @author Philip Helger
 * @since 11.0.5
 */
@Immutable
public final class JsonMapper
{
  public static final String JSON_TRISTATE_TRUE = "TRUE";
  public static final String JSON_TRISTATE_FALSE = "FALSE";
  public static final String JSON_TRISTATE_UNDEFINED = "UNDEFINED";

  public static final String JSON_CLASS = "class";
  public static final String JSON_MESSAGE = "message";
  public static final String JSON_STACK_TRACE = "stackTrace";

  public static final String JSON_RESOURCE_ID = "resource";
  public static final String JSON_LINE_NUM = "line";
  public static final String JSON_COLUMN_NUM = "col";

  private JsonMapper ()
  {}

  /**
   * Get the tristate representation of the provided value. Either {@link #JSON_TRISTATE_TRUE} or
   * {@link #JSON_TRISTATE_FALSE}.
   *
   * @param b
   *        boolean value to get converted.
   * @return A non-<code>null</code> JSON value string.
   * @see #getJsonTriState(ETriState)
   */
  @Nonnull
  @Nonempty
  public static String getJsonTriState (final boolean b)
  {
    return b ? JSON_TRISTATE_TRUE : JSON_TRISTATE_FALSE;
  }

  /**
   * Get the tristate representation of the provided value. Either {@link #JSON_TRISTATE_TRUE},
   * {@link #JSON_TRISTATE_FALSE} or {@link #JSON_TRISTATE_UNDEFINED}.
   *
   * @param eTriState
   *        Tristate value to get converted. May be <code>null</code>.
   * @return A non-<code>null</code> JSON value string.
   * @see #getJsonTriState(boolean)
   */
  @Nullable
  public static String getJsonTriState (@Nullable final ETriState eTriState)
  {
    if (eTriState == null)
      return null;
    if (eTriState.isUndefined ())
      return JSON_TRISTATE_UNDEFINED;
    return getJsonTriState (eTriState.isTrue ());
  }

  /**
   * Convert the provided value into a tristate value. Must be one of {@link #JSON_TRISTATE_TRUE},
   * {@link #JSON_TRISTATE_FALSE} or {@link #JSON_TRISTATE_UNDEFINED}.
   *
   * @param sTriState
   *        Source value. May be <code>null</code>.
   * @return <code>null</code> if the provided value is unknown.
   */
  @Nullable
  public static ETriState getAsTriState (@Nullable final String sTriState)
  {
    if (JSON_TRISTATE_TRUE.equals (sTriState))
      return ETriState.TRUE;
    if (JSON_TRISTATE_FALSE.equals (sTriState))
      return ETriState.FALSE;
    if (JSON_TRISTATE_UNDEFINED.equals (sTriState))
      return ETriState.UNDEFINED;
    return null;
  }

  /**
   * Get the JSON representation of a stack trace.<br>
   *
   * <pre>
   * {
   *   "class" : string,
   *   "message" : string?,
   *   "stackTrace" : string
   * }
   * </pre>
   *
   * @param t
   *        The exception to convert to a JSON object. May be <code>null</code>.
   * @return <code>null</code> if the parameter is <code>null</code>, the JSON object otherwise.
   * @see JsonUnmappedException for a representation after reading
   */
  @Nullable
  public static IJsonObject getJsonException (@Nullable final Throwable t)
  {
    if (t == null)
      return null;
    if (t instanceof final JsonUnmappedException aUnmappedEx)
      return aUnmappedEx.getAsJson ();
    return new JsonObject ().add (JSON_CLASS, t.getClass ().getName ())
                            .addIfNotNull (JSON_MESSAGE, t.getMessage ())
                            .add (JSON_STACK_TRACE, StackTraceHelper.getStackAsString (t));
  }

  @Nullable
  public static JsonUnmappedException getAsUnmappedException (@Nullable final IJsonObject aObj)
  {
    if (aObj == null)
      return null;

    final String sClassName = aObj.getAsString (JsonMapper.JSON_CLASS);
    final String sMessage = aObj.getAsString (JsonMapper.JSON_MESSAGE);
    final ICommonsList <String> aStackTraceLines = StringHelper.getExploded (StackTraceHelper.DEFAULT_LINE_SEPARATOR,
                                                                             aObj.getAsString (JsonMapper.JSON_STACK_TRACE));
    if (sClassName == null)
      return null;

    return new JsonUnmappedException (sClassName, sMessage, aStackTraceLines);
  }

  @Nullable
  public static IJsonObject getJsonSimpleLocation (@Nullable final ILocation aLocation)
  {
    if (aLocation == null || !aLocation.isAnyInformationPresent ())
      return null;
    final IJsonObject ret = new JsonObject ();
    if (aLocation.hasResourceID ())
      ret.add (JSON_RESOURCE_ID, aLocation.getResourceID ());
    if (aLocation.hasLineNumber ())
      ret.add (JSON_LINE_NUM, aLocation.getLineNumber ());
    if (aLocation.hasColumnNumber ())
      ret.add (JSON_COLUMN_NUM, aLocation.getColumnNumber ());
    return ret;
  }

  @Nullable
  public static ILocation getAsSimpleLocation (@Nullable final IJsonObject aObj)
  {
    if (aObj == null)
      return null;

    final String sResourceID = aObj.getAsString (JSON_RESOURCE_ID);
    final int nLineNumber = aObj.getAsInt (JSON_LINE_NUM, -1);
    final int nColumnNumber = aObj.getAsInt (JSON_COLUMN_NUM, -1);
    if (Strings.isEmpty (sResourceID) && nLineNumber < 0 && nColumnNumber < 0)
      return null;

    return new SimpleLocation (sResourceID, nLineNumber, nColumnNumber);
  }
}

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

import javax.annotation.Nonnull;

import com.helger.commons.callback.ICallback;
import com.helger.json.IJsonArray;
import com.helger.json.IJsonObject;
import com.helger.json.IJsonValue;

/**
 * Callback interface for visiting the JSON hierarchy.
 *
 * @author Philip Helger
 */
public interface IJsonVisitorCallback extends ICallback
{
  /**
   * Invoke for each simple value.
   *
   * @param aValue
   *        The current value. Never <code>null</code>.
   */
  default void onJsonValue (@Nonnull final IJsonValue aValue)
  {}

  /**
   * Invoked before iterating the children of an array.
   *
   * @param aValue
   *        The current array. Never <code>null</code>.
   */
  default void onJsonArrayStart (@Nonnull final IJsonArray aValue)
  {}

  /**
   * Invoked after iterating the children of an array.
   *
   * @param aValue
   *        The current array. Never <code>null</code>.
   */
  default void onJsonArrayEnd (@Nonnull final IJsonArray aValue)
  {}

  /**
   * Invoked before iterating the children of an object.
   *
   * @param aValue
   *        The current object. Never <code>null</code>.
   */
  default void onJsonObjectStart (@Nonnull final IJsonObject aValue)
  {}

  /**
   * Invoked for each Json object element. The value is handled with the
   * existing methods like {@link #onJsonValue(IJsonValue)} etc.
   *
   * @param sName
   *        The current object's element name. Never <code>null</code>.
   */
  default void onJsonObjectElementName (@Nonnull final String sName)
  {}

  /**
   * Invoked after iterating the children of an object.
   *
   * @param aValue
   *        The current object. Never <code>null</code>.
   */
  default void onJsonObjectEnd (@Nonnull final IJsonObject aValue)
  {}
}

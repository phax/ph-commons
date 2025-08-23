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
package com.helger.url.param;

import com.helger.annotation.style.MustImplementEqualsAndHashcode;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.collection.commons.ICommonsIterable;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.collection.commons.ICommonsOrderedSet;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Read-only interface for objects handling URL parameters
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public interface IURLParameterList extends ICommonsIterable <URLParameter>
{
  /**
   * @param sName
   *        Parameter name. May be <code>null</code>.
   * @return <code>true</code> if it contained, <code>false</code> if not.
   */
  boolean contains (@Nullable String sName);

  /**
   * @param sName
   *        Parameter name. May be <code>null</code>.
   * @param sValue
   *        The expected parameter value. May be <code>null</code>
   * @return <code>true</code> if it contained, <code>false</code> if not.
   */
  boolean contains (@Nullable String sName, @Nullable String sValue);

  @Nonnull
  @ReturnsMutableCopy
  ICommonsOrderedSet <String> getAllParamNames ();

  @Nonnull
  @ReturnsMutableCopy
  ICommonsList <String> getAllParamValues (@Nullable String sName);

  /**
   * Get the value of the first parameter with the provided name
   *
   * @param sName
   *        The parameter name to search
   * @return <code>null</code> if no such parameter is present.
   */
  @Nullable
  String getFirstParamValue (@Nullable String sName);

  /**
   * @return A new multi map (map from String to List of String) with all values. Order is
   *         maintained lost. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsOrderedMap <String, ICommonsList <String>> getAsMultiMap ();
}

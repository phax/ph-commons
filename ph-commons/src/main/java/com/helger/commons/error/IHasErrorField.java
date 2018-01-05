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
package com.helger.commons.error;

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.string.StringHelper;

/**
 * Base interface for an object that has an error field.
 *
 * @author Philip Helger
 */
public interface IHasErrorField
{
  /**
   * @return The field for which the error occurred. May be <code>null</code>.
   * @since 8.5.0
   */
  @Nullable
  String getErrorFieldName ();

  /**
   * @return <code>true</code> if a field name is present, <code>false</code>
   *         otherwise
   * @since 8.5.0
   */
  default boolean hasErrorFieldName ()
  {
    return StringHelper.hasText (getErrorFieldName ());
  }

  /**
   * @return <code>true</code> if no field name is present, <code>false</code>
   *         otherwise
   * @since 8.5.0
   */
  default boolean hasNoErrorFieldName ()
  {
    return StringHelper.hasNoText (getErrorFieldName ());
  }

  /**
   * Check if this error has the passed error field name.
   *
   * @param sErrorFieldName
   *        The error field name to check. May be null.
   * @return <code>true</code> if a field name is equal, <code>false</code>
   *         otherwise
   * @since 8.5.0
   */
  default boolean hasErrorFieldName (@Nullable final String sErrorFieldName)
  {
    return EqualsHelper.equals (getErrorFieldName (), sErrorFieldName);
  }

  @Nonnull
  static Comparator <IHasErrorField> getComparatorErrorFieldName ()
  {
    return Comparator.nullsFirst (Comparator.comparing (IHasErrorField::getErrorFieldName));
  }
}

/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.errorlist;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.error.IHasErrorID;
import com.helger.commons.error.IHasSeverity;
import com.helger.commons.error.IResourceLocation;
import com.helger.commons.error.ResourceLocation;
import com.helger.commons.string.StringHelper;

/**
 * Base interface for single errors and resource errors.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 */
public interface IErrorBase <IMPLTYPE extends IErrorBase <IMPLTYPE>> extends IHasSeverity <IMPLTYPE>, IHasErrorID
{
  /**
   * {@inheritDoc}
   *
   * @since 8.4.1
   */
  default String getErrorID ()
  {
    return null;
  }

  /**
   * @return The field for which the error occurred. May be <code>null</code>.
   * @since 8.4.1
   */
  @Nullable
  default String getErrorFieldName ()
  {
    return null;
  }

  /**
   * @return <code>true</code> if a field name is present, <code>false</code>
   *         otherwise
   * @since 8.4.1
   */
  default boolean hasErrorFieldName ()
  {
    return StringHelper.hasText (getErrorFieldName ());
  }

  /**
   * @return <code>true</code> if no field name is present, <code>false</code>
   *         otherwise
   * @since 8.4.1
   */
  default boolean hasNoErrorFieldName ()
  {
    return StringHelper.hasNoText (getErrorFieldName ());
  }

  /**
   * Check if this error has the passed error field name,
   *
   * @param sErrorFieldName
   *        The error field name to check. May be null.
   * @return <code>true</code> if a field name is equal, <code>false</code>
   *         otherwise
   * @since 8.4.1
   */
  default boolean hasErrorFieldName (@Nullable final String sErrorFieldName)
  {
    return EqualsHelper.equals (getErrorFieldName (), sErrorFieldName);
  }

  /**
   * @return The non-<code>null</code> location of the error.
   * @since 8.4.1
   */
  @Nonnull
  default IResourceLocation getLocation ()
  {
    return ResourceLocation.NO_LOCATION;
  }

  /**
   * Check if a reasonable error location is present.
   * 
   * @return <code>true</code> if location information is present,
   *         <code>false</code> otherwise.
   */
  default boolean hasLocation ()
  {
    return getLocation ().isAnyInformationPresent ();
  }

  /**
   * @return The linked exception or <code>null</code> if no such exception is
   *         available.
   * @since 8.4.1
   */
  @Nullable
  default Throwable getLinkedException ()
  {
    return null;
  }

  /**
   * @return <code>true</code> if a linked exception is present,
   *         <code>false</code> if not.
   * @since 8.4.1
   */
  default boolean hasLinkedException ()
  {
    return getLinkedException () != null;
  }
}

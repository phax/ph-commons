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
package com.helger.commons.error;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.error.field.IHasErrorField;
import com.helger.commons.error.id.IHasErrorID;
import com.helger.commons.error.level.IHasErrorLevelComparable;
import com.helger.commons.error.location.ErrorLocation;
import com.helger.commons.error.location.IErrorLocation;
import com.helger.commons.string.StringHelper;

/**
 * Common interface for single errors and resource errors.
 *
 * @author Philip Helger
 * @since 8.4.1
 */
public interface IError extends IHasErrorLevelComparable <IError>, IHasErrorID, IHasErrorField
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
   * @return The non-<code>null</code> location of the error. Use
   *         {@link ErrorLocation#NO_LOCATION} to indicate no location is
   *         available.
   * @since 8.4.1
   */
  @Nonnull
  default IErrorLocation getLocation ()
  {
    return ErrorLocation.NO_LOCATION;
  }

  /**
   * Check if a reasonable error location is present.
   *
   * @return <code>true</code> if location information is present,
   *         <code>false</code> otherwise.
   * @since 8.4.1
   */
  default boolean hasLocation ()
  {
    return getLocation ().isAnyInformationPresent ();
  }

  /**
   * Get the error message of this error.
   *
   * @param aContentLocale
   *        The locale to be used in case the error text is available in
   *        multiple languages.
   * @return The message of this form error. May be <code>null</code> in case no
   *         error text is available or if the passed Locale is not supported.
   * @since 8.4.1
   */
  @Nullable
  default String getErrorText (@Nonnull final Locale aContentLocale)
  {
    return null;
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

  /**
   * Get the error as a string representation, including error ID, error
   * location, error text and the linked exception.
   *
   * @param aDisplayLocale
   *        Locale to resolve the error text
   * @return The default string representation
   * @since 8.4.1
   */
  @Nonnull
  @Nonempty
  default String getAsString (@Nonnull final Locale aDisplayLocale)
  {
    // Error level
    String ret = "[" + getErrorLevel ().getID () + "]";

    // Error ID
    final String sErrorID = getErrorID ();
    if (StringHelper.hasText (sErrorID))
      ret += "[" + sErrorID + "]";

    // Location (including field)
    final IErrorLocation aLocation = getLocation ();
    if (aLocation.isAnyInformationPresent ())
      ret += " " + aLocation.getAsString () + ":";

    // Message
    final String sErrorText = getErrorText (aDisplayLocale);
    if (StringHelper.hasText (sErrorText))
      ret += " " + sErrorText;

    // Linked exception
    final Throwable aLinkedEx = getLinkedException ();
    if (aLinkedEx != null)
      ret += " (" + aLinkedEx.getClass ().getName () + ": " + aLinkedEx.getMessage () + ")";
    return ret;
  }
}

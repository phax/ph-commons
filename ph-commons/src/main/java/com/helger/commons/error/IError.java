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

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
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
 * @since 8.5.0
 */
@MustImplementEqualsAndHashcode
public interface IError extends IHasErrorLevelComparable <IError>, IHasErrorID, IHasErrorField
{
  /**
   * {@inheritDoc}
   */
  default String getErrorID ()
  {
    return null;
  }

  /**
   * @return The field for which the error occurred. May be <code>null</code>.
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
   */
  @Nullable
  default String getErrorText (@Nonnull final Locale aContentLocale)
  {
    return null;
  }

  /**
   * @return The linked exception or <code>null</code> if no such exception is
   *         available.
   */
  @Nullable
  default Throwable getLinkedException ()
  {
    return null;
  }

  /**
   * @return <code>true</code> if a linked exception is present,
   *         <code>false</code> if not.
   */
  default boolean hasLinkedException ()
  {
    return getLinkedException () != null;
  }

  /**
   * @return The message of the linked exception or <code>null</code> if no such
   *         exception is available.
   */
  @Nullable
  default String getLinkedExceptionMessage ()
  {
    final Throwable t = getLinkedException ();
    return t == null ? null : t.getMessage ();
  }

  /**
   * @return The stack trace of the linked exception or <code>null</code> if no
   *         such exception is available.
   */
  @Nullable
  default StackTraceElement [] getLinkedExceptionStackTrace ()
  {
    final Throwable t = getLinkedException ();
    return t == null ? null : t.getStackTrace ();
  }

  /**
   * @return The cause of the linked exception or <code>null</code> if no such
   *         exception is available.
   */
  @Nullable
  default Throwable getLinkedExceptionCause ()
  {
    final Throwable t = getLinkedException ();
    return t == null ? null : t.getCause ();
  }

  /**
   * Get the error as a string representation, including error ID, error
   * location, error text and the linked exception.
   *
   * @param aContentLocale
   *        Locale to resolve the error text
   * @return The default string representation
   */
  @Nonnull
  @Nonempty
  default String getAsString (@Nonnull final Locale aContentLocale)
  {
    // Error level
    String ret = "[" + getErrorLevel ().getID () + "]";

    // Error ID
    final String sErrorID = getErrorID ();
    if (StringHelper.hasText (sErrorID))
      ret += "[" + sErrorID + "]";

    // Error ID
    final String sErrorFieldName = getErrorFieldName ();
    if (StringHelper.hasText (sErrorFieldName))
    {
      if (ret.length () > 0)
        ret += " ";
      ret += "in " + sErrorFieldName;
    }

    // Location
    final IErrorLocation aLocation = getLocation ();
    if (aLocation.isAnyInformationPresent ())
    {
      if (ret.length () > 0)
        ret += " ";
      ret += "@ " + aLocation.getAsString () + ":";
    }

    // Message
    final String sErrorText = getErrorText (aContentLocale);
    if (StringHelper.hasText (sErrorText))
    {
      if (ret.length () > 0)
        ret += " ";
      ret += sErrorText;
    }

    // Linked exception
    final Throwable aLinkedEx = getLinkedException ();
    if (aLinkedEx != null)
    {
      if (ret.length () > 0)
        ret += " ";
      ret += "(" + aLinkedEx.getClass ().getName () + ": " + aLinkedEx.getMessage () + ")";
    }
    return ret;
  }
}

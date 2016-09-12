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

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.error.location.IErrorLocation;

/**
 * Base interface for a single error, that has an error ID, and error level, and
 * error field name and an error text.
 *
 * @author Philip Helger
 * @deprecated Use {@link com.helger.commons.error.IError} instead
 */
@Deprecated
public interface IError extends com.helger.commons.error.IError
{
  /**
   * @return The error field name of this object as an {@link IErrorLocation} .
   *         Never <code>null</code>.
   * @deprecated Use {@link #getErrorLocation()} instead
   */
  @Nonnull
  @Deprecated
  default IErrorLocation getResourceLocation ()
  {
    return getErrorLocation ();
  }

  /**
   * @return The message of this form error. The error text is always locale
   *         specific because this error is meant to be for a single form
   *         instance represented in a fixed locale. The result may neither be
   *         <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  String getErrorText ();
}

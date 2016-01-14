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
package com.helger.commons.email;

import java.util.Locale;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.regex.RegExCache;

/**
 * Perform simple email address validation based on a regular expression.
 *
 * @author Philip Helger
 */
@Immutable
public final class EmailAddressHelper
{
  /** This is the email RegEx :) */
  public static final String EMAIL_ADDRESS_PATTERN = "[a-z0-9!#\\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\\$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";

  /** Compile this little pattern only once */
  private static final Pattern s_aPattern = RegExCache.getPattern (EMAIL_ADDRESS_PATTERN);

  private EmailAddressHelper ()
  {}

  /**
   * Get the unified version of an email address. It trims leading and trailing
   * spaces and lower-cases the email address.
   *
   * @param sEmailAddress
   *        The email address to unify. May be <code>null</code>.
   * @return The unified email address or <code>null</code> if the input address
   *         is <code>null</code>.
   */
  @Nullable
  public static String getUnifiedEmailAddress (@Nullable final String sEmailAddress)
  {
    return sEmailAddress == null ? null : sEmailAddress.trim ().toLowerCase (Locale.US);
  }

  /**
   * Checks if a value is a valid e-mail address according to a certain regular
   * expression.
   *
   * @param sEmailAddress
   *        The value validation is being performed on. A <code>null</code>
   *        value is considered invalid.
   * @return <code>true</code> if the email address is valid, <code>false</code>
   *         otherwise.
   */
  public static boolean isValid (@Nullable final String sEmailAddress)
  {
    if (sEmailAddress == null)
      return false;

    // Unify (lowercase)
    final String sUnifiedEmail = getUnifiedEmailAddress (sEmailAddress);

    // Pattern matching
    return s_aPattern.matcher (sUnifiedEmail).matches ();
  }
}

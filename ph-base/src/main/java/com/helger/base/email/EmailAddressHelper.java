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
package com.helger.base.email;

import java.util.Locale;
import java.util.regex.Pattern;

import com.helger.annotation.concurrent.Immutable;

import jakarta.annotation.Nullable;

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

  /**
   * This is the simple email RegEx that deals with Unicode email addresses. Based on
   * https://github.com/itplr-kosit/validator-configuration-xrechnung/issues/109
   */
  public static final String EMAIL_ADDRESS_PATTERN_SIMPLE = "[^@\\s]+@([^@.\\s]+\\.)+[^@.\\s]+";

  /** Compile this pattern only once */
  private static final Pattern PATTERN = Pattern.compile (EMAIL_ADDRESS_PATTERN);

  /** Compile this pattern only once */
  private static final Pattern PATTERN_SIMPLE = Pattern.compile (EMAIL_ADDRESS_PATTERN_SIMPLE);

  private EmailAddressHelper ()
  {}

  /**
   * Get the unified version of an email address. It trims leading and trailing spaces and
   * lower-cases the email address.
   *
   * @param sEmailAddress
   *        The email address to unify. May be <code>null</code>.
   * @return The unified email address or <code>null</code> if the input address is
   *         <code>null</code>.
   */
  @Nullable
  public static String getUnifiedEmailAddress (@Nullable final String sEmailAddress)
  {
    return sEmailAddress == null ? null : sEmailAddress.trim ().toLowerCase (Locale.US);
  }

  /**
   * Checks if a value is a valid e-mail address according to the original, complex regular
   * expression (see {@link #EMAIL_ADDRESS_PATTERN}).
   *
   * @param sEmailAddress
   *        The value validation is being performed on. A <code>null</code> value is considered
   *        invalid.
   * @return <code>true</code> if the email address is valid, <code>false</code> otherwise.
   */
  public static boolean isValid (@Nullable final String sEmailAddress)
  {
    if (sEmailAddress == null)
      return false;

    // Unify (lowercase)
    final String sUnifiedEmail = getUnifiedEmailAddress (sEmailAddress);

    // Pattern matching
    return PATTERN.matcher (sUnifiedEmail).matches ();
  }

  /**
   * Checks if a value is a valid e-mail address according to the simple regular expression (see
   * {@link #EMAIL_ADDRESS_PATTERN_SIMPLE}). The idea is, that all email addresses valid with
   * {@link #isValid(String)} are still valid with this one.
   *
   * @param sEmailAddress
   *        The value validation is being performed on. A <code>null</code> value is considered
   *        invalid.
   * @return <code>true</code> if the email address is valid, <code>false</code> otherwise.
   */
  public static boolean isValidForSimplePattern (@Nullable final String sEmailAddress)
  {
    if (sEmailAddress == null)
      return false;

    // Unify (lowercase)
    final String sUnifiedEmail = getUnifiedEmailAddress (sEmailAddress);

    // Pattern matching
    return PATTERN_SIMPLE.matcher (sUnifiedEmail).matches ();
  }
}

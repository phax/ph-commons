/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.http.permissionspolicy;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.name.IHasName;
import com.helger.base.string.StringHelper;

/**
 * A single Permissions Policy directive. It consists of a directive name, an allow list value and
 * an optional reporting endpoint.
 *
 * @author Philip Helger
 * @since 12.3.6
 */
public interface IPermissionsPolicyDirective extends IHasName
{
  /** The name of the optional per-directive reporting endpoint parameter */
  String PARAM_REPORT_TO = "report-to";

  /**
   * @return The name of this directive. Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  String getName ();

  /**
   * @return The allow list value of this directive, in the form <code>*</code> or
   *         <code>(...)</code>. May be <code>null</code> or empty. Note that the empty allow list
   *         <code>()</code> is a valid value that disables the feature everywhere.
   */
  @Nullable
  String getValue ();

  /**
   * @return The name of the reporting endpoint to which policy violations of this directive should
   *         be reported. May be <code>null</code> or empty.
   */
  @Nullable
  String getReportTo ();

  /**
   * @return <code>true</code> if this directive has a non-empty value, <code>false</code>
   *         otherwise.
   */
  default boolean hasValue ()
  {
    return StringHelper.isNotEmpty (getValue ());
  }

  /**
   * @return <code>true</code> if this directive has a non-empty reporting endpoint,
   *         <code>false</code> otherwise.
   */
  default boolean hasReportTo ()
  {
    return StringHelper.isNotEmpty (getReportTo ());
  }

  /**
   * @return The directive as a string in the format <code>name=value</code> respectively
   *         <code>name=value;report-to=endpoint</code>. If no value is present, only the name is
   *         returned. Never <code>null</code>.
   */
  @NonNull
  @Nonempty
  default String getAsString ()
  {
    final StringBuilder aSB = new StringBuilder (getName ());
    if (hasValue ())
      aSB.append ('=').append (getValue ());
    if (hasReportTo ())
      aSB.append (';').append (PARAM_REPORT_TO).append ('=').append (getReportTo ());
    return aSB.toString ();
  }

  /**
   * @return The same as {@link #getAsString()} if a value is present, or <code>null</code> if no
   *         value is set.
   */
  @Nullable
  default String getAsStringIfHasValue ()
  {
    return hasValue () ? getAsString () : null;
  }
}

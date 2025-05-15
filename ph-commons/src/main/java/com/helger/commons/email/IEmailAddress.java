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
package com.helger.commons.email;

import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;

import com.helger.commons.name.IHasDisplayName;
import com.helger.commons.string.StringHelper;

/**
 * Aggregation of a personal name and an email address.
 *
 * @author Philip Helger
 */
public interface IEmailAddress extends IHasDisplayName
{
  /**
   * @return The main email address. May not be <code>null</code>.
   */
  @Nonnull
  String getAddress ();

  /**
   * @return The personal name. May be <code>null</code>.
   */
  @Nullable
  String getPersonal ();

  /**
   * @return <code>true</code> if a personal name is present, <code>false</code>
   *         if not.
   */
  default boolean hasPersonal ()
  {
    return StringHelper.hasText (getPersonal ());
  }

  @Nonnull
  default String getDisplayName ()
  {
    if (hasPersonal ())
      return getPersonal () + " <" + getAddress () + ">";
    return getAddress ();
  }
}

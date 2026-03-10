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
package com.helger.datetime.domain;

import java.text.DateFormatSymbols;
import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.array.ArrayHelper;
import com.helger.base.id.IHasIntID;
import com.helger.base.lang.EnumHelper;

/**
 * Represents the AM/PM values
 *
 * @author Philip Helger
 */
public enum EAmPm implements IHasIntID
{
  AM (0),
  PM (1);

  private final int m_nID;

  EAmPm (final int nID)
  {
    m_nID = nID;
  }

  /**
   * @return The numeric ID of this AM/PM value (0 for AM, 1 for PM).
   */
  public int getID ()
  {
    return m_nID;
  }

  /**
   * Get the locale-specific display name of this AM/PM value.
   *
   * @param aLocale
   *        The locale to use. May not be <code>null</code>.
   * @return The display name or <code>null</code> if the locale does not
   *         provide AM/PM strings.
   */
  @Nullable
  public String getDisplayName (@NonNull final Locale aLocale)
  {
    return ArrayHelper.getSafeElement (DateFormatSymbols.getInstance (aLocale).getAmPmStrings (), m_nID);
  }

  /**
   * Get the {@link EAmPm} value matching the given ID.
   *
   * @param nID
   *        The ID to search for.
   * @return <code>null</code> if no matching value was found.
   */
  @Nullable
  public static EAmPm getFromIDOrNull (final int nID)
  {
    return EnumHelper.getFromIDOrNull (EAmPm.class, nID);
  }
}

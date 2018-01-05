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
package com.helger.datetime.domain;

import java.text.DateFormatSymbols;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.id.IHasIntID;
import com.helger.commons.lang.EnumHelper;

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

  private EAmPm (final int nID)
  {
    m_nID = nID;
  }

  public int getID ()
  {
    return m_nID;
  }

  @Nullable
  public String getDisplayName (@Nonnull final Locale aLocale)
  {
    return ArrayHelper.getSafeElement (DateFormatSymbols.getInstance (aLocale).getAmPmStrings (), m_nID);
  }

  @Nullable
  public static EAmPm getFromIDOrNull (final int nID)
  {
    return EnumHelper.getFromIDOrNull (EAmPm.class, nID);
  }
}

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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.lang.EnumHelper;

/**
 * Represents a generic error level.
 *
 * @author Philip Helger
 */
public enum EErrorLevel implements IErrorLevel
{
  /** Success */
  SUCCESS ("success", 0),
  /** Information level */
  INFO ("info", 100),
  /** Warning level. */
  WARN ("warn", 200),
  /** Error level */
  ERROR ("error", 300),
  /** Fatal error */
  FATAL_ERROR ("fatal_error", 400);

  /** Lowest error level within this enum */
  public static final EErrorLevel LOWEST = SUCCESS;
  /** Highest error level within this enum */
  public static final EErrorLevel HIGHEST = FATAL_ERROR;

  private final String m_sID;
  private final int m_nNumericLevel;

  private EErrorLevel (@Nonnull @Nonempty final String sID, @Nonnegative final int nNumericLevel)
  {
    m_sID = sID;
    m_nNumericLevel = nNumericLevel;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nonnegative
  public int getNumericLevel ()
  {
    return m_nNumericLevel;
  }

  @Nullable
  public static EErrorLevel getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EErrorLevel.class, sID);
  }

  @Nonnull
  public static EErrorLevel getFromIDOrThrow (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrThrow (EErrorLevel.class, sID);
  }

  @Nullable
  public static EErrorLevel getFromIDOrDefault (@Nullable final String sID, @Nullable final EErrorLevel eDefault)
  {
    return EnumHelper.getFromIDOrDefault (EErrorLevel.class, sID, eDefault);
  }

  @Nullable
  public static EErrorLevel getFromIDCaseInsensitiveOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDCaseInsensitiveOrNull (EErrorLevel.class, sID);
  }

  @Nonnull
  public static EErrorLevel getFromIDCaseInsensitiveOrThrow (@Nullable final String sID)
  {
    return EnumHelper.getFromIDCaseInsensitiveOrThrow (EErrorLevel.class, sID);
  }

  @Nullable
  public static EErrorLevel getFromIDCaseInsensitiveOrDefault (@Nullable final String sID,
                                                               @Nullable final EErrorLevel eDefault)
  {
    return EnumHelper.getFromIDCaseInsensitiveOrDefault (EErrorLevel.class, sID, eDefault);
  }
}

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
package com.helger.commons.changelog;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;

/**
 * Represents the category of a single change log entry.
 *
 * @author Philip Helger
 */
public enum EChangeLogCategory implements IHasID <String>
{
  API ("api"),
  TECH ("tech"),
  USER_INTERFACE ("ui"),
  FEATURE ("feature"),
  BUSINESS_LOGIC ("businesslogic"),
  SECURITY ("security"),
  TEST ("test"),
  DATA ("data"),
  PERFORMANCE ("performance");

  private final String m_sID;

  private EChangeLogCategory (@Nonnull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nullable
  public static EChangeLogCategory getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EChangeLogCategory.class, sID);
  }

  @Nullable
  public static EChangeLogCategory getFromIDOrDefault (@Nullable final String sID,
                                                       @Nullable final EChangeLogCategory eDefault)
  {
    return EnumHelper.getFromIDOrDefault (EChangeLogCategory.class, sID, eDefault);
  }
}

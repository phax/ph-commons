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
package com.helger.config.source;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;

/**
 * Defines the type of configuration sources and the respective default
 * priority.
 *
 * @author Philip Helger
 */
public enum EConfigSourceType implements IHasID <String>
{
  /**
   * A configuration values from a system property.
   */
  SYSTEM_PROPERTY ("sysprop", 400),
  /**
   * A configuration value from an environment variable.
   */
  ENVIRONMENT_VARIABLE ("envvar", 300),
  /**
   * A configuration value from a file.
   */
  RESOURCE ("resource", 200),
  /**
   * A configuration value from any other source inside the application.
   */
  APPLICATION ("appl", 100);

  private final String m_sID;
  private final int m_nDefaultPriority;

  EConfigSourceType (@Nonnull @Nonempty final String sID, @Nonnegative final int nDefaultPriority)
  {
    m_sID = sID;
    m_nDefaultPriority = nDefaultPriority;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * @return The default priority for a configuration source of this type.
   *         Always &gt; 0.
   */
  @Nonnegative
  public int getDefaultPriority ()
  {
    return m_nDefaultPriority;
  }

  @Nullable
  public static EConfigSourceType getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EConfigSourceType.class, sID);
  }
}

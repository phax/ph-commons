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
package com.helger.commons.filter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;

/**
 * Filter matching strategy for {@link IFilter} implementations based on
 * {@link AbstractFilter}.
 *
 * @author Philip Helger
 */
public enum EFilterMatchingStrategy implements IHasID <String>
{
  /**
   * Match either this filter or an eventually present nested filter. If no
   * nested filter is present, only this filter must match.
   */
  MATCH_ANY ("matchany"),
  /**
   * Math both this filter and an eventually present nested filter. If no nested
   * filter is present, only this filter must match.
   */
  MATCH_ALL ("matchall");

  private final String m_sID;

  private EFilterMatchingStrategy (@Nonnull @Nonempty final String sID)
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
  public static EFilterMatchingStrategy getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EFilterMatchingStrategy.class, sID);
  }
}

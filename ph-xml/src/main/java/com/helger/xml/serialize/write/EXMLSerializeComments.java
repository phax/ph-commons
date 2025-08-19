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
package com.helger.xml.serialize.write;

import com.helger.annotation.Nonempty;
import com.helger.base.id.IHasID;
import com.helger.base.lang.EnumHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Determines whether comments should be serialized or not.
 *
 * @author Philip Helger
 */
public enum EXMLSerializeComments implements IHasID <String>
{
  EMIT ("emit"),
  IGNORE ("ignore");

  private final String m_sID;

  EXMLSerializeComments (@Nonnull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * @return <code>true</code> it emit is enabled
   */
  public boolean isEmit ()
  {
    return this == EMIT;
  }

  @Nullable
  public static EXMLSerializeComments getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EXMLSerializeComments.class, sID);
  }
}

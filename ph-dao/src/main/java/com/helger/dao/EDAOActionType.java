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
package com.helger.dao;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.id.IHasID;
import com.helger.base.lang.EnumHelper;

/**
 * Enumeration with the different actions that can occur in a DAO. This is used
 * in the WAL DAO.
 *
 * @author Philip Helger
 */
public enum EDAOActionType implements IHasID <String>
{
  CREATE ("create"),
  UPDATE ("update"),
  DELETE ("delete");

  private final String m_sID;

  EDAOActionType (@NonNull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  /** {@inheritDoc} */
  @NonNull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * Get the enum value matching the given ID.
   *
   * @param sID
   *        The ID to search. May be <code>null</code>.
   * @return <code>null</code> if no such action type exists.
   */
  @Nullable
  public static EDAOActionType getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EDAOActionType.class, sID);
  }

  /**
   * Get the enum value matching the given ID or throw an exception.
   *
   * @param sID
   *        The ID to search. May be <code>null</code>.
   * @return Never <code>null</code>.
   * @throws IllegalArgumentException
   *         If no matching action type is found.
   */
  @Nullable
  public static EDAOActionType getFromIDOrThrow (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrThrow (EDAOActionType.class, sID);
  }
}

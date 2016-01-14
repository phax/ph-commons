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
package com.helger.commons.state;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;

/**
 * Represents an object that has one of 3 different states compared to a boolean
 * that has only 2 different states.
 *
 * @author Philip Helger
 */
public enum ETriState implements IHasID <String>,ITriState
{
  TRUE ("true", Boolean.TRUE),
  FALSE ("false", Boolean.FALSE),
  UNDEFINED ("undefined", null);

  private final String m_sID;
  private final Boolean m_aBoolean;

  private ETriState (@Nonnull @Nonempty final String sID, @Nullable final Boolean aBoolean)
  {
    m_sID = sID;
    m_aBoolean = aBoolean;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  public boolean isTrue ()
  {
    return this == TRUE;
  }

  public boolean isFalse ()
  {
    return this == FALSE;
  }

  public boolean isDefined ()
  {
    return this != UNDEFINED;
  }

  public boolean getAsBooleanValue ()
  {
    if (this == UNDEFINED)
      throw new IllegalStateException ("The TriState is undefined!");
    return m_aBoolean.booleanValue ();
  }

  public boolean getAsBooleanValue (final boolean bUndefinedValue)
  {
    return this == UNDEFINED ? bUndefinedValue : m_aBoolean.booleanValue ();
  }

  @Nullable
  public Boolean getAsBooleanObj ()
  {
    return m_aBoolean;
  }

  @Nullable
  public Boolean getAsBooleanObj (@Nullable final Boolean aUndefinedValue)
  {
    return this == UNDEFINED ? aUndefinedValue : m_aBoolean;
  }

  @Nonnull
  public static ETriState valueOf (final boolean bValue)
  {
    return bValue ? TRUE : FALSE;
  }

  @Nonnull
  public static ETriState valueOf (@Nullable final Boolean aValue)
  {
    return aValue == null ? UNDEFINED : valueOf (aValue.booleanValue ());
  }

  @Nonnull
  public static ETriState valueOf (@Nonnull final ITriState aTriState)
  {
    return valueOf (aTriState.getAsBooleanObj (null));
  }

  @Nullable
  public static ETriState getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (ETriState.class, sID);
  }

  @Nonnull
  public static ETriState getFromIDOrUndefined (@Nullable final String sID)
  {
    return getFromIDOrDefault (sID, UNDEFINED);
  }

  @Nullable
  public static ETriState getFromIDOrDefault (@Nullable final String sID, @Nullable final ETriState eDefault)
  {
    return EnumHelper.getFromIDOrDefault (ETriState.class, sID, eDefault);
  }
}

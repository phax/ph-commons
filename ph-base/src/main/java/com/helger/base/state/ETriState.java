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
package com.helger.base.state;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.id.IHasID;
import com.helger.base.lang.EnumHelper;

/**
 * Represents an object that has one of 3 different states compared to a boolean
 * that has only 2 different states.
 *
 * @author Philip Helger
 */
public enum ETriState implements IHasID <String>, ITriState
{
  TRUE ("true", Boolean.TRUE),
  FALSE ("false", Boolean.FALSE),
  UNDEFINED ("undefined", null);

  private final String m_sID;
  private final Boolean m_aBoolean;

  ETriState (@NonNull @Nonempty final String sID, @Nullable final Boolean aBoolean)
  {
    m_sID = sID;
    m_aBoolean = aBoolean;
  }

  /** {@inheritDoc} */
  @NonNull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /** {@inheritDoc} */
  public boolean isTrue ()
  {
    return this == TRUE;
  }

  /** {@inheritDoc} */
  public boolean isFalse ()
  {
    return this == FALSE;
  }

  /** {@inheritDoc} */
  public boolean isDefined ()
  {
    return this != UNDEFINED;
  }

  /** {@inheritDoc} */
  public boolean getAsBooleanValue ()
  {
    if (this == UNDEFINED)
      throw new IllegalStateException ("The TriState is undefined!");
    return m_aBoolean.booleanValue ();
  }

  /** {@inheritDoc} */
  public boolean getAsBooleanValue (final boolean bUndefinedValue)
  {
    return this == UNDEFINED ? bUndefinedValue : m_aBoolean.booleanValue ();
  }

  /** {@inheritDoc} */
  @Nullable
  public Boolean getAsBooleanObj ()
  {
    return m_aBoolean;
  }

  /** {@inheritDoc} */
  @Nullable
  public Boolean getAsBooleanObj (@Nullable final Boolean aUndefinedValue)
  {
    return this == UNDEFINED ? aUndefinedValue : m_aBoolean;
  }

  /**
   * Convert a boolean value to the corresponding {@link ETriState} enum value.
   *
   * @param bValue
   *        <code>true</code> for {@link #TRUE}, <code>false</code> for
   *        {@link #FALSE}.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static ETriState valueOf (final boolean bValue)
  {
    return bValue ? TRUE : FALSE;
  }

  /**
   * Convert a {@link Boolean} value to the corresponding {@link ETriState} enum
   * value.
   *
   * @param aValue
   *        The Boolean value. May be <code>null</code> for {@link #UNDEFINED}.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static ETriState valueOf (@Nullable final Boolean aValue)
  {
    return aValue == null ? UNDEFINED : valueOf (aValue.booleanValue ());
  }

  /**
   * Convert an {@link ITriState} to the corresponding {@link ETriState} enum
   * value.
   *
   * @param aTriState
   *        The tri state to convert. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static ETriState valueOf (@NonNull final ITriState aTriState)
  {
    return valueOf (aTriState.getAsBooleanObj (null));
  }

  /**
   * Get the {@link ETriState} enum value matching the provided ID.
   *
   * @param sID
   *        The ID to search. May be <code>null</code>.
   * @return <code>null</code> if no matching enum value was found.
   */
  @Nullable
  public static ETriState getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (ETriState.class, sID);
  }

  /**
   * Get the {@link ETriState} enum value matching the provided ID, defaulting
   * to {@link #UNDEFINED}.
   *
   * @param sID
   *        The ID to search. May be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @NonNull
  public static ETriState getFromIDOrUndefined (@Nullable final String sID)
  {
    return getFromIDOrDefault (sID, UNDEFINED);
  }

  /**
   * Get the {@link ETriState} enum value matching the provided ID, with a
   * custom default.
   *
   * @param sID
   *        The ID to search. May be <code>null</code>.
   * @param eDefault
   *        The default value to return if no match is found. May be
   *        <code>null</code>.
   * @return The matching enum value or the provided default.
   */
  @Nullable
  public static ETriState getFromIDOrDefault (@Nullable final String sID, @Nullable final ETriState eDefault)
  {
    return EnumHelper.getFromIDOrDefault (ETriState.class, sID, eDefault);
  }
}

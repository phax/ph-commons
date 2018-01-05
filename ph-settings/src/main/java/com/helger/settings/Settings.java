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
package com.helger.settings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.attr.AttributeContainerAny;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

/**
 * The default implementation of the {@link ISettings} object.
 *
 * @author philip
 */
public class Settings extends AttributeContainerAny <String> implements ISettings
{
  private final String m_sName;

  /**
   * Constructor for new settings.
   *
   * @param sName
   *        Name of the settings. May neither be <code>null</code> nor empty.
   */
  public Settings (@Nonnull @Nonempty final String sName)
  {
    m_sName = ValueEnforcer.notEmpty (sName, "Name");
  }

  /**
   * Copy like constructor
   *
   * @param aOther
   *        Object to copy from. May not be <code>null</code>.
   */
  public Settings (@Nonnull final ISettings aOther)
  {
    this (aOther.getName ());
    putAllIn (aOther);
  }

  @Nonnull
  public EChange putIn (@Nonnull @Nonempty final String sName, @Nullable final Object aNewValue)
  {
    // Additional check, that name may not be empty
    ValueEnforcer.notEmpty (sName, "Name");

    return super.putIn (sName, aNewValue);
  }

  @Nonnull
  @Nonempty
  public final String getName ()
  {
    return m_sName;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final Settings rhs = (Settings) o;
    return m_sName.equals (rhs.m_sName);
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_sName).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("Name", m_sName).getToString ();
  }
}

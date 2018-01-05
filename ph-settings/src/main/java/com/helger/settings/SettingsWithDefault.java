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
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation of {@link ISettingsWithDefault} based on
 * {@link Settings}.
 *
 * @author Philip Helger
 */
public class SettingsWithDefault extends Settings implements ISettingsWithDefault
{
  private final ISettings m_aDefaultSettings;

  public SettingsWithDefault (@Nonnull final ISettings aDefaultSettings)
  {
    this (aDefaultSettings.getName (), aDefaultSettings);
  }

  public SettingsWithDefault (@Nonnull @Nonempty final String sName, @Nonnull final ISettings aDefaultSettings)
  {
    super (sName);
    m_aDefaultSettings = ValueEnforcer.notNull (aDefaultSettings, "DefaultSettings");
  }

  public boolean containsKeyDirect (@Nullable final String sFieldName)
  {
    return super.containsKey (sFieldName);
  }

  @Override
  public boolean containsKey (@Nullable final Object sFieldName)
  {
    if (containsKeyDirect ((String) sFieldName))
      return true;
    return m_aDefaultSettings.containsKey (sFieldName);
  }

  @Nullable
  public Object getValueDirect (@Nullable final String sFieldName)
  {
    return super.getValue (sFieldName);
  }

  @Override
  @Nullable
  public Object getValue (@Nullable final String sFieldName)
  {
    Object aValue = getValueDirect (sFieldName);
    if (aValue == null)
    {
      // Value not found - query default
      aValue = m_aDefaultSettings.getValue (sFieldName);
    }
    return aValue;
  }

  @Nonnull
  public ISettings getDefaultSettings ()
  {
    return m_aDefaultSettings;
  }

  @Nonnull
  public EChange setToDefault (@Nullable final String sFieldName)
  {
    final Object aDefaultValue = m_aDefaultSettings.getValue (sFieldName);
    if (aDefaultValue == null)
      return EChange.UNCHANGED;

    // set if field is present in the configuration
    return putIn (sFieldName, aDefaultValue);
  }

  @Nonnull
  public EChange setAllToDefault ()
  {
    EChange eChange = EChange.UNCHANGED;
    for (final String sFieldName : m_aDefaultSettings.keySet ())
      eChange = eChange.or (setToDefault (sFieldName));
    return eChange;
  }

  public boolean isSetToDefault (@Nullable final String sFieldName)
  {
    return containsKeyDirect (sFieldName) &&
           EqualsHelper.equals (getValueDirect (sFieldName), m_aDefaultSettings.getValue (sFieldName));
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final SettingsWithDefault rhs = (SettingsWithDefault) o;
    return m_aDefaultSettings.equals (rhs.m_aDefaultSettings);
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_aDefaultSettings).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("DefaultSettings", m_aDefaultSettings)
                            .getToString ();
  }
}

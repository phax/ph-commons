/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.bmx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ICloneable;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Settings for Binary Micro XML (BMX) handling.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public final class BMXSettings implements ICloneable <BMXSettings>
{
  private final Set <EBMXSetting> m_aSettings = EnumSet.noneOf (EBMXSetting.class);

  public BMXSettings ()
  {}

  public BMXSettings (@Nonnull final Collection <? extends EBMXSetting> aSettings)
  {
    if (aSettings == null)
      throw new NullPointerException ("settings");
    m_aSettings.addAll (EBMXSetting.getAllEnabledByDefault ());
  }

  public BMXSettings (@Nonnull final BMXSettings aOther)
  {
    if (aOther == null)
      throw new NullPointerException ("other");
    m_aSettings.addAll (aOther.m_aSettings);
  }

  @Nonnull
  public BMXSettings set (@Nonnull final EBMXSetting eSetting)
  {
    if (eSetting == null)
      throw new NullPointerException ("setting");
    m_aSettings.add (eSetting);
    return this;
  }

  @Nonnull
  public BMXSettings unset (@Nullable final EBMXSetting eSetting)
  {
    m_aSettings.remove (eSetting);
    return this;
  }

  public boolean isSet (@Nullable final EBMXSetting eSetting)
  {
    return m_aSettings.contains (eSetting);
  }

  @Nonnull
  public int getStorageValue ()
  {
    int ret = 0;
    for (final EBMXSetting eSetting : m_aSettings)
      ret |= eSetting.getValue ();
    return ret;
  }

  @Nonnull
  public BMXSettings getClone ()
  {
    return new BMXSettings (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof BMXSettings))
      return false;
    final BMXSettings rhs = (BMXSettings) o;
    return m_aSettings.equals (rhs.m_aSettings);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aSettings).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("settings", m_aSettings).toString ();
  }

  @Nonnull
  public static BMXSettings createDefault ()
  {
    return new BMXSettings (EBMXSetting.getAllEnabledByDefault ());
  }

  @Nonnull
  public static BMXSettings createFromStorageValue (final int nValue)
  {
    final List <EBMXSetting> aSettings = new ArrayList <EBMXSetting> ();
    for (final EBMXSetting eSetting : EBMXSetting.values ())
      if ((nValue & eSetting.getValue ()) != 0)
        aSettings.add (eSetting);
    return new BMXSettings (aSettings);
  }
}

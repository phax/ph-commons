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
package com.helger.commons.thirdparty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.version.Version;

/**
 * Represents a custom license.
 *
 * @author Philip Helger
 */
@Immutable
public class CustomLicense implements ILicense
{
  private final String m_sID;
  private final String m_sName;
  private final Version m_aVersion;
  private final String m_sWebSiteURL;

  /**
   * Create a custom license.
   *
   * @param sID
   *        The ID of the license.
   * @param sName
   *        The name of the license.
   * @param aVersion
   *        The version of the license.
   * @param sURL
   *        The URL of the license.
   */
  public CustomLicense (@Nonnull @Nonempty final String sID,
                        @Nonnull @Nonempty final String sName,
                        @Nullable final Version aVersion,
                        @Nullable final String sURL)
  {
    m_sID = ValueEnforcer.notEmpty (sID, "ID");
    m_sName = ValueEnforcer.notEmpty (sName, "Name");
    m_aVersion = aVersion;
    m_sWebSiteURL = sURL;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nonnull
  @Nonempty
  public String getDisplayName ()
  {
    return m_sName;
  }

  @Nullable
  public Version getVersion ()
  {
    return m_aVersion;
  }

  @Nullable
  public String getURL ()
  {
    return m_sWebSiteURL;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final CustomLicense rhs = (CustomLicense) o;
    return m_sID.equals (rhs.m_sID) &&
           m_sName.equals (rhs.m_sName) &&
           EqualsHelper.equals (m_aVersion, rhs.m_aVersion) &&
           EqualsHelper.equals (m_sWebSiteURL, rhs.m_sWebSiteURL);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sID)
                                       .append (m_sName)
                                       .append (m_aVersion)
                                       .append (m_sWebSiteURL)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ID", m_sID)
                                       .append ("name", m_sName)
                                       .appendIfNotNull ("version", m_aVersion)
                                       .appendIfNotNull ("website", m_sWebSiteURL)
                                       .toString ();
  }
}

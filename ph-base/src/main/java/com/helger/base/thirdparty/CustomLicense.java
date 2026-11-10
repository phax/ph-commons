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
package com.helger.base.thirdparty;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.base.version.Version;

/**
 * Represents a custom licence.
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
   * Create a custom licence.
   *
   * @param sID
   *        The ID of the licence.
   * @param sName
   *        The name of the licence.
   * @param aVersion
   *        The version of the licence.
   * @param sURL
   *        The URL of the licence.
   */
  public CustomLicense (@NonNull @Nonempty final String sID,
                        @NonNull @Nonempty final String sName,
                        @Nullable final Version aVersion,
                        @Nullable final String sURL)
  {
    m_sID = ValueEnforcer.notEmpty (sID, "ID");
    m_sName = ValueEnforcer.notEmpty (sName, "Name");
    m_aVersion = aVersion;
    m_sWebSiteURL = sURL;
  }

  /**
   * @return The unique ID of this licence. Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * @return The display name of this licence. Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getDisplayName ()
  {
    return m_sName;
  }

  /**
   * @return The version of this licence. May be <code>null</code>.
   */
  @Nullable
  public Version getVersion ()
  {
    return m_aVersion;
  }

  /**
   * @return The URL of this licence. May be <code>null</code>.
   */
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
                                       .append ("Name", m_sName)
                                       .appendIfNotNull ("Version", m_aVersion)
                                       .appendIfNotNull ("Website", m_sWebSiteURL)
                                       .getToString ();
  }
}

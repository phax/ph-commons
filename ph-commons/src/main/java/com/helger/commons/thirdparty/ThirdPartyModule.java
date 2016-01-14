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
 * The default implementation of the {@link IThirdPartyModule} interface.
 *
 * @author Philip Helger
 */
@Immutable
public final class ThirdPartyModule implements IThirdPartyModule
{
  /** By default a third-party module is not optional */
  public static final boolean DEFAULT_OPTIONAL = false;

  private final String m_sDisplayName;
  private final String m_sCopyrightOwner;
  private final ILicense m_aLicense;
  private final Version m_aVersion;
  private final String m_sWebSiteURL;
  private final boolean m_bOptional;

  public ThirdPartyModule (@Nonnull final IThirdPartyModule aOther, final boolean bOptional)
  {
    this (aOther.getDisplayName (),
          aOther.getCopyrightOwner (),
          aOther.getLicense (),
          aOther.getVersion (),
          aOther.getWebSiteURL (),
          bOptional);
  }

  public ThirdPartyModule (@Nonnull @Nonempty final String sDisplayName,
                           @Nonnull @Nonempty final String sCopyrightOwner,
                           @Nonnull final ILicense aLicense)
  {
    this (sDisplayName, sCopyrightOwner, aLicense, DEFAULT_OPTIONAL);
  }

  public ThirdPartyModule (@Nonnull @Nonempty final String sDisplayName,
                           @Nonnull @Nonempty final String sCopyrightOwner,
                           @Nonnull final ILicense aLicense,
                           final boolean bOptional)
  {
    this (sDisplayName, sCopyrightOwner, aLicense, (Version) null, (String) null, bOptional);
  }

  public ThirdPartyModule (@Nonnull @Nonempty final String sDisplayName,
                           @Nonnull @Nonempty final String sCopyrightOwner,
                           @Nonnull final ILicense aLicense,
                           @Nullable final Version aVersion,
                           @Nullable final String sWebsiteURL)
  {
    this (sDisplayName, sCopyrightOwner, aLicense, aVersion, sWebsiteURL, DEFAULT_OPTIONAL);
  }

  public ThirdPartyModule (@Nonnull @Nonempty final String sDisplayName,
                           @Nonnull @Nonempty final String sCopyrightOwner,
                           @Nonnull final ILicense aLicense,
                           @Nullable final Version aVersion,
                           @Nullable final String sWebsiteURL,
                           final boolean bOptional)
  {
    m_sDisplayName = ValueEnforcer.notEmpty (sDisplayName, "DisplayName");
    m_sCopyrightOwner = ValueEnforcer.notEmpty (sCopyrightOwner, "CopyrightOwner");
    m_aLicense = ValueEnforcer.notNull (aLicense, "License");
    m_aVersion = aVersion;
    m_sWebSiteURL = sWebsiteURL;
    m_bOptional = bOptional;
  }

  @Nonnull
  @Nonempty
  public String getDisplayName ()
  {
    return m_sDisplayName;
  }

  @Nonnull
  @Nonempty
  public String getCopyrightOwner ()
  {
    return m_sCopyrightOwner;
  }

  @Nonnull
  public ILicense getLicense ()
  {
    return m_aLicense;
  }

  @Nullable
  public Version getVersion ()
  {
    return m_aVersion;
  }

  @Nullable
  public String getWebSiteURL ()
  {
    return m_sWebSiteURL;
  }

  public boolean isOptional ()
  {
    return m_bOptional;
  }

  @Nonnull
  public ThirdPartyModule getAsOptionalCopy ()
  {
    if (m_bOptional)
      return this;
    return new ThirdPartyModule (this, true);
  }

  @Nonnull
  public ThirdPartyModule getAsNonOptionalCopy ()
  {
    if (!m_bOptional)
      return this;
    return new ThirdPartyModule (this, false);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ThirdPartyModule rhs = (ThirdPartyModule) o;
    return m_sDisplayName.equals (rhs.m_sDisplayName) &&
           m_sCopyrightOwner.equals (rhs.m_sCopyrightOwner) &&
           m_aLicense.equals (rhs.m_aLicense) &&
           EqualsHelper.equals (m_aVersion, rhs.m_aVersion) &&
           EqualsHelper.equals (m_sWebSiteURL, rhs.m_sWebSiteURL) &&
           m_bOptional == rhs.m_bOptional;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sDisplayName)
                                       .append (m_sCopyrightOwner)
                                       .append (m_aLicense)
                                       .append (m_aVersion)
                                       .append (m_sWebSiteURL)
                                       .append (m_bOptional)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("displayName", m_sDisplayName)
                                       .append ("copyrightOwner", m_sCopyrightOwner)
                                       .append ("license", m_aLicense)
                                       .appendIfNotNull ("version", m_aVersion)
                                       .appendIfNotNull ("website", m_sWebSiteURL)
                                       .append ("optional", m_bOptional)
                                       .toString ();
  }
}

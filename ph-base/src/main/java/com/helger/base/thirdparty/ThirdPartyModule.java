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
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.string.StringHelper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.base.version.Version;

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

  /**
   * Constructor copying from another third party module with a different optional flag.
   *
   * @param aOther
   *        The third party module to copy from. May not be <code>null</code>.
   * @param bOptional
   *        <code>true</code> if the module is optional, <code>false</code> otherwise.
   * @deprecated Use {@link #builder(IThirdPartyModule)} instead
   */
  @Deprecated (forRemoval = true, since = "12.6.0")
  public ThirdPartyModule (@NonNull final IThirdPartyModule aOther, final boolean bOptional)
  {
    this (aOther.getDisplayName (),
          aOther.getCopyrightOwner (),
          aOther.getLicense (),
          aOther.getVersion (),
          aOther.getWebSiteURL (),
          bOptional);
  }

  /**
   * Constructor with required fields and default optional flag.
   *
   * @param sDisplayName
   *        The display name. Neither <code>null</code> nor empty.
   * @param sCopyrightOwner
   *        The copyright owner. Neither <code>null</code> nor empty.
   * @param aLicense
   *        The licence. May not be <code>null</code>.
   * @deprecated Use {@link #builder()} instead
   */
  @Deprecated (forRemoval = true, since = "12.6.0")
  public ThirdPartyModule (@NonNull @Nonempty final String sDisplayName,
                           @NonNull @Nonempty final String sCopyrightOwner,
                           @NonNull final ILicense aLicense)
  {
    this (sDisplayName, sCopyrightOwner, aLicense, (Version) null, (String) null, DEFAULT_OPTIONAL);
  }

  /**
   * Constructor with required fields and optional flag.
   *
   * @param sDisplayName
   *        The display name. Neither <code>null</code> nor empty.
   * @param sCopyrightOwner
   *        The copyright owner. Neither <code>null</code> nor empty.
   * @param aLicense
   *        The licence. May not be <code>null</code>.
   * @param bOptional
   *        <code>true</code> if the module is optional, <code>false</code> otherwise.
   * @deprecated Use {@link #builder()} instead
   */
  @Deprecated (forRemoval = true, since = "12.6.0")
  public ThirdPartyModule (@NonNull @Nonempty final String sDisplayName,
                           @NonNull @Nonempty final String sCopyrightOwner,
                           @NonNull final ILicense aLicense,
                           final boolean bOptional)
  {
    this (sDisplayName, sCopyrightOwner, aLicense, (Version) null, (String) null, bOptional);
  }

  /**
   * Constructor with all fields except optional flag.
   *
   * @param sDisplayName
   *        The display name. Neither <code>null</code> nor empty.
   * @param sCopyrightOwner
   *        The copyright owner. Neither <code>null</code> nor empty.
   * @param aLicense
   *        The licence. May not be <code>null</code>.
   * @param aVersion
   *        The version. May be <code>null</code>.
   * @param sWebsiteURL
   *        The website URL. May be <code>null</code>.
   * @deprecated Use {@link #builder()} instead
   */
  @Deprecated (forRemoval = true, since = "12.6.0")
  public ThirdPartyModule (@NonNull @Nonempty final String sDisplayName,
                           @NonNull @Nonempty final String sCopyrightOwner,
                           @NonNull final ILicense aLicense,
                           @Nullable final Version aVersion,
                           @Nullable final String sWebsiteURL)
  {
    this (sDisplayName, sCopyrightOwner, aLicense, aVersion, sWebsiteURL, DEFAULT_OPTIONAL);
  }

  /**
   * Full constructor with all fields.
   *
   * @param sDisplayName
   *        The display name. Neither <code>null</code> nor empty.
   * @param sCopyrightOwner
   *        The copyright owner. Neither <code>null</code> nor empty.
   * @param aLicense
   *        The licence. May not be <code>null</code>.
   * @param aVersion
   *        The version. May be <code>null</code>.
   * @param sWebsiteURL
   *        The website URL. May be <code>null</code>.
   * @param bOptional
   *        <code>true</code> if the module is optional, <code>false</code> otherwise.
   */
  public ThirdPartyModule (@NonNull @Nonempty final String sDisplayName,
                           @NonNull @Nonempty final String sCopyrightOwner,
                           @NonNull final ILicense aLicense,
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

  /**
   * @return The display name of this module. Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getDisplayName ()
  {
    return m_sDisplayName;
  }

  /**
   * @return The copyright owner of this module. Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getCopyrightOwner ()
  {
    return m_sCopyrightOwner;
  }

  /**
   * @return The licence of this module. Never <code>null</code>.
   */
  @NonNull
  public ILicense getLicense ()
  {
    return m_aLicense;
  }

  /**
   * @return The version of this module. May be <code>null</code>.
   */
  @Nullable
  public Version getVersion ()
  {
    return m_aVersion;
  }

  /**
   * @return The website URL of this module. May be <code>null</code>.
   */
  @Nullable
  public String getWebSiteURL ()
  {
    return m_sWebSiteURL;
  }

  /**
   * @return <code>true</code> if this module is optional, <code>false</code> otherwise.
   */
  public boolean isOptional ()
  {
    return m_bOptional;
  }

  /**
   * @return A copy of this module with the optional flag set to <code>true</code>. If already
   *         optional, <code>this</code> is returned. Never <code>null</code>.
   */
  @NonNull
  public ThirdPartyModule getAsOptionalCopy ()
  {
    if (m_bOptional)
      return this;
    return builder (this).optional (true).build ();
  }

  /**
   * @return A copy of this module with the optional flag set to <code>false</code>. If already
   *         non-optional, <code>this</code> is returned. Never <code>null</code>.
   */
  @NonNull
  public ThirdPartyModule getAsNonOptionalCopy ()
  {
    if (!m_bOptional)
      return this;
    return builder (this).optional (false).build ();
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
    return new ToStringGenerator (this).append ("DisplayName", m_sDisplayName)
                                       .append ("CopyrightOwner", m_sCopyrightOwner)
                                       .append ("License", m_aLicense)
                                       .appendIfNotNull ("Version", m_aVersion)
                                       .appendIfNotNull ("Website", m_sWebSiteURL)
                                       .append ("Optional", m_bOptional)
                                       .getToString ();
  }

  /**
   * Builder class for class {@link ThirdPartyModule}.
   *
   * @author Philip Helger
   * @since 12.6.0
   */
  @NotThreadSafe
  public static class ThirdPartyModuleBuilder implements IBuilder <ThirdPartyModule>
  {
    private String m_sDisplayName;
    private String m_sCopyrightOwner;
    private ILicense m_aLicense;
    private Version m_aVersion;
    private String m_sWebSiteUrl;
    private boolean m_bOptional = DEFAULT_OPTIONAL;

    /**
     * Default constructor.
     */
    public ThirdPartyModuleBuilder ()
    {}

    /**
     * Copy constructor from an existing third party module.
     *
     * @param aSrc
     *        The source module to copy from. May not be <code>null</code>.
     */
    public ThirdPartyModuleBuilder (@NonNull final IThirdPartyModule aSrc)
    {
      ValueEnforcer.notNull (aSrc, "Source");
      displayName (aSrc.getDisplayName ()).copyrightOwner (aSrc.getCopyrightOwner ())
                                          .license (aSrc.getLicense ())
                                          .version (aSrc.getVersion ())
                                          .webSiteUrl (aSrc.getWebSiteURL ())
                                          .optional (aSrc.isOptional ());
    }

    /**
     * Set the display name.
     *
     * @param s
     *        The display name. May be <code>null</code> but is required for building.
     * @return this for chaining
     */
    @NonNull
    public final ThirdPartyModuleBuilder displayName (@Nullable final String s)
    {
      m_sDisplayName = s;
      return this;
    }

    /**
     * Set the copyright owner.
     *
     * @param s
     *        The copyright owner. May be <code>null</code> but is required for building.
     * @return this for chaining
     */
    @NonNull
    public final ThirdPartyModuleBuilder copyrightOwner (@Nullable final String s)
    {
      m_sCopyrightOwner = s;
      return this;
    }

    /**
     * Set the licence.
     *
     * @param a
     *        The licence. May be <code>null</code> but is required for building.
     * @return this for chaining
     */
    @NonNull
    public final ThirdPartyModuleBuilder license (@Nullable final ILicense a)
    {
      m_aLicense = a;
      return this;
    }

    /**
     * Set the version.
     *
     * @param a
     *        The version. May be <code>null</code>.
     * @return this for chaining
     */
    @NonNull
    public final ThirdPartyModuleBuilder version (@Nullable final Version a)
    {
      m_aVersion = a;
      return this;
    }

    /**
     * Set the version.
     *
     * @param s
     *        The version string. May be <code>null</code>.
     * @return this for chaining
     */
    @NonNull
    public final ThirdPartyModuleBuilder version (@Nullable final String s)
    {
      return version (s == null ? null : Version.parse (s));
    }

    /**
     * Set the website URL.
     *
     * @param s
     *        The website URL. May be <code>null</code>.
     * @return this for chaining
     */
    @NonNull
    public final ThirdPartyModuleBuilder webSiteUrl (@Nullable final String s)
    {
      m_sWebSiteUrl = s;
      return this;
    }

    /**
     * Set whether the module is optional. Default is {@link ThirdPartyModule#DEFAULT_OPTIONAL}.
     *
     * @param b
     *        <code>true</code> if the module is optional, <code>false</code> otherwise.
     * @return this for chaining
     */
    @NonNull
    public final ThirdPartyModuleBuilder optional (final boolean b)
    {
      m_bOptional = b;
      return this;
    }

    /**
     * Build the {@link ThirdPartyModule} from the provided parameters.
     *
     * @return A new {@link ThirdPartyModule} instance. Never <code>null</code>.
     * @throws IllegalStateException
     *         if any required parameter is missing.
     */
    @NonNull
    public ThirdPartyModule build () throws IllegalStateException
    {
      if (StringHelper.isEmpty (m_sDisplayName))
        throw new IllegalStateException ("DisplayName is missing");
      if (StringHelper.isEmpty (m_sCopyrightOwner))
        throw new IllegalStateException ("CopyrightOwner is missing");
      if (m_aLicense == null)
        throw new IllegalStateException ("Licence is missing");

      return new ThirdPartyModule (m_sDisplayName,
                                   m_sCopyrightOwner,
                                   m_aLicense,
                                   m_aVersion,
                                   m_sWebSiteUrl,
                                   m_bOptional);
    }
  }

  /**
   * @return A new builder for {@link ThirdPartyModule} objects. Never <code>null</code>.
   * @since 12.6.0
   */
  @NonNull
  public static ThirdPartyModuleBuilder builder ()
  {
    return new ThirdPartyModuleBuilder ();
  }

  /**
   * Create a new builder that is pre-filled with the data of the provided module.
   *
   * @param aSrc
   *        The source module to copy from. May not be <code>null</code>.
   * @return A new builder for {@link ThirdPartyModule} objects. Never <code>null</code>.
   * @since 12.6.0
   */
  @NonNull
  public static ThirdPartyModuleBuilder builder (@NonNull final IThirdPartyModule aSrc)
  {
    return new ThirdPartyModuleBuilder (aSrc);
  }
}

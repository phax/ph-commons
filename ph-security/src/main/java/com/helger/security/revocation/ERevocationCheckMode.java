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
package com.helger.security.revocation;

import java.security.cert.PKIXRevocationChecker;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.base.id.IHasID;
import com.helger.base.lang.EnumHelper;

/**
 * An enum that defines the revocation checking modes.
 *
 * @author Philip Helger
 * @since 11.2.0
 */
public enum ERevocationCheckMode implements IHasID <String>
{
  /**
   * Try OCSP before CRL.
   */
  OCSP_BEFORE_CRL ("ocsp-before-crl", true, true, new HashSet <> ()),
  /**
   * Try OCSP but don't try CRL.
   */
  OCSP ("ocsp", true, false, EnumSet.of (PKIXRevocationChecker.Option.NO_FALLBACK)),
  /**
   * Try CRL before OCSP.
   */
  CRL_BEFORE_OCSP ("crl-before-ocsp", true, true, EnumSet.of (PKIXRevocationChecker.Option.PREFER_CRLS)),
  /**
   * Try CRL but don't try OCSP.
   */
  CRL ("crl",
       false,
       true,
       EnumSet.of (PKIXRevocationChecker.Option.PREFER_CRLS, PKIXRevocationChecker.Option.NO_FALLBACK)),
  /**
   * Don't do remote checking.
   */
  NONE ("none", false, false, new HashSet <> ());

  private final String m_sID;
  private final boolean m_bOCSP;
  private final boolean m_bCRL;
  @CodingStyleguideUnaware
  private final Set <PKIXRevocationChecker.Option> m_aOptions;

  ERevocationCheckMode (@NonNull @Nonempty final String sID,
                        final boolean bOCSP,
                        final boolean bCRL,
                        @NonNull final Set <PKIXRevocationChecker.Option> aOptions)
  {
    m_sID = sID;
    m_bOCSP = bOCSP;
    m_bCRL = bCRL;
    m_aOptions = aOptions;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * @return <code>true</code> if this mode uses OCSP checking, <code>false</code> otherwise.
   */
  public boolean isOCSP ()
  {
    return m_bOCSP;
  }

  /**
   * @return <code>true</code> if this mode uses CRL checking, <code>false</code> otherwise.
   */
  public boolean isCRL ()
  {
    return m_bCRL;
  }

  /**
   * @return <code>true</code> if this mode uses exactly one of OCSP or CRL but not both,
   *         <code>false</code> otherwise.
   */
  public boolean isOnlyOne ()
  {
    return (m_bOCSP && !m_bCRL) || (m_bCRL && !m_bOCSP);
  }

  /**
   * @return <code>true</code> if this mode uses neither OCSP nor CRL, <code>false</code> otherwise.
   */
  public boolean isNone ()
  {
    return !m_bOCSP && !m_bCRL;
  }

  /**
   * Add all PKIXRevocationChecker#Option values of this mode to the provided target collection.
   *
   * @param aTarget
   *        The target collection to add the options to. May not be <code>null</code>.
   */
  public void addAllOptionsTo (@NonNull final Collection <? super PKIXRevocationChecker.Option> aTarget)
  {
    aTarget.addAll (m_aOptions);
  }

  /**
   * Find the revocation check mode with the passed ID.
   *
   * @param sID
   *        The ID to be searched. May be <code>null</code>.
   * @return <code>null</code> if no such revocation check mode was found.
   * @since 12.2.1
   */
  @Nullable
  public static ERevocationCheckMode getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (ERevocationCheckMode.class, sID);
  }

  /**
   * Find the revocation check mode with the passed ID.
   *
   * @param sID
   *        The ID to be searched. May be <code>null</code>.
   * @param eDefault
   *        The default value to be returned if no such ID is contained. May be <code>null</code>.
   * @return <code>eDefault</code> if no such revocation check mode was found.
   * @since 12.2.1
   */
  @Nullable
  public static ERevocationCheckMode getFromIDOrDefault (@Nullable final String sID,
                                                         @Nullable final ERevocationCheckMode eDefault)
  {
    return EnumHelper.getFromIDOrDefault (ERevocationCheckMode.class, sID, eDefault);
  }
}

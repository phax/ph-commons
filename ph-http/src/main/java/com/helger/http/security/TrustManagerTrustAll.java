/*
 * Copyright (C) 2015-2025 Philip Helger (www.helger.com)
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
package com.helger.http.security;

import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.debug.GlobalDebug;
import com.helger.base.tostring.ToStringGenerator;

import jakarta.annotation.Nullable;

/**
 * A trust manager that accepts all certificates.
 *
 * @author Philip Helger
 */
public class TrustManagerTrustAll implements X509TrustManager
{
  private static final Logger LOGGER = LoggerFactory.getLogger (TrustManagerTrustAll.class);

  private final boolean m_bDebug;

  public TrustManagerTrustAll ()
  {
    this (GlobalDebug.isDebugMode ());
  }

  public TrustManagerTrustAll (final boolean bDebug)
  {
    m_bDebug = bDebug;
  }

  /**
   * @return The debug flag as passed in the constructor.
   */
  public boolean isDebug ()
  {
    return m_bDebug;
  }

  @Nullable
  public X509Certificate [] getAcceptedIssuers ()
  {
    return null;
  }

  public void checkServerTrusted (final X509Certificate [] aChain, final String sAuthType)
  {
    if (m_bDebug)
      LOGGER.info ("checkServerTrusted (" + Arrays.toString (aChain) + ", " + sAuthType + ")");
  }

  public void checkClientTrusted (final X509Certificate [] aChain, final String sAuthType)
  {
    if (m_bDebug)
      LOGGER.info ("checkClientTrusted (" + Arrays.toString (aChain) + ", " + sAuthType + ")");
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Debug", m_bDebug).getToString ();
  }
}

/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.http.tls;

import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.exception.InitializationException;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsEnumMap;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.commons.ICommonsSet;

/**
 * Standalone implementation of {@link ITLSConfigurationMode}.
 *
 * @author Philip Helger
 * @since 9.0.5
 */
public class TLSConfigurationMode implements ITLSConfigurationMode
{

  private static final Logger LOGGER = LoggerFactory.getLogger (TLSConfigurationMode.class);
  private static final ICommonsMap <ETLSVersion, SSLContext> TLS_CONTEXT_MAP = new CommonsEnumMap <> (ETLSVersion.class);

  static
  {
    for (final ETLSVersion eTLSVersion : ETLSVersion.values ())
    {
      try
      {
        final SSLContext aSSLCtx = SSLContext.getInstance (eTLSVersion.getID ());
        aSSLCtx.init (null, null, null);
        TLS_CONTEXT_MAP.put (eTLSVersion, aSSLCtx);
      }
      catch (final NoSuchAlgorithmException ex)
      {
        // E.g. TLS 1.3 on Java 8
        // -> ignore
      }
      catch (final Exception ex)
      {
        throw new InitializationException ("Error creating SSLContext for " + eTLSVersion, ex);
      }
    }
    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Initialized TLS ContextMap with " + TLS_CONTEXT_MAP.keySet () + " keys");
  }

  public static boolean isSupportedCipherSuiteInSSLContext (@NonNull final ETLSVersion [] aTLSVersions,
                                                            @NonNull @Nonempty final String sCipherSuite)
  {
    // Check if the cipher suite is available for any TLS version
    for (final ETLSVersion eTLSVersion : aTLSVersions)
    {
      final SSLContext aSSLCtx = TLS_CONTEXT_MAP.get (eTLSVersion);
      if (aSSLCtx != null)
      {
        final SSLParameters aParams = aSSLCtx.getSupportedSSLParameters ();
        final ICommonsSet <String> aCipherSuites = new CommonsHashSet <> (aParams.getCipherSuites ());
        if (aCipherSuites.contains (sCipherSuite))
        {
          if (LOGGER.isDebugEnabled ())
            LOGGER.debug ("Cipher suite '" + sCipherSuite + "' is supported in TLS version " + eTLSVersion);
          return true;
        }
      }
    }
    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Cipher suite '" + sCipherSuite + "' is NOT supported by any TLS version");
    return false;
  }

  // Order is important
  private final ICommonsList <ETLSVersion> m_aTLSVersions;
  private final ICommonsList <String> m_aCipherSuites;

  /**
   * Constructor. The constructor uses only the cipher suites that are supported
   * by the underlying operating system and TLS version.
   * {@link #getAllCipherSuites()} returns the filtered list.
   *
   * @param aTLSVersions
   *        The supported TLS versions. Order is important and maintained. MAy
   *        neither be <code>null</code> nor empty.
   * @param aCipherSuites
   *        The cipher suites to be used. May not be <code>null</code> and may
   *        not contain <code>null</code> values.
   */
  public TLSConfigurationMode (@NonNull @Nonempty final ETLSVersion [] aTLSVersions,
                               @NonNull final String [] aCipherSuites)
  {
    ValueEnforcer.notEmptyNoNullValue (aTLSVersions, "TLSVersions");
    ValueEnforcer.notNullNoNullValue (aCipherSuites, "CipherSuites");
    m_aTLSVersions = new CommonsArrayList <> (aTLSVersions);
    // Use only the cipher suites that are supported
    m_aCipherSuites = CommonsArrayList.createFiltered (aCipherSuites,
                                                       x -> isSupportedCipherSuiteInSSLContext (aTLSVersions, x));
  }

  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <ETLSVersion> getAllTLSVersions ()
  {
    return m_aTLSVersions.getClone ();
  }

  @NonNull
  @ReturnsMutableCopy
  @Override
  public ICommonsList <String> getAllTLSVersionIDs ()
  {
    return m_aTLSVersions.getAllMapped (ETLSVersion::getID);
  }

  @Nullable
  @ReturnsMutableCopy
  @Override
  public String [] getAllTLSVersionIDsAsArray ()
  {
    if (m_aTLSVersions.isEmpty ())
      return null;
    return getAllTLSVersionIDs ().toArray (new String [m_aTLSVersions.size ()]);
  }

  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllCipherSuites ()
  {
    return m_aCipherSuites.getClone ();
  }

  @Nullable
  @ReturnsMutableCopy
  @Override
  public String [] getAllCipherSuitesAsArray ()
  {
    if (m_aCipherSuites.isEmpty ())
      return null;
    return m_aCipherSuites.toArray (new String [m_aCipherSuites.size ()]);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final TLSConfigurationMode rhs = (TLSConfigurationMode) o;
    return m_aTLSVersions.equals (rhs.m_aTLSVersions) && m_aCipherSuites.equals (rhs.m_aCipherSuites);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aTLSVersions).append (m_aCipherSuites).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("TLSVersions", m_aTLSVersions)
                                       .append ("CipherSuites", m_aCipherSuites)
                                       .getToString ();
  }
}

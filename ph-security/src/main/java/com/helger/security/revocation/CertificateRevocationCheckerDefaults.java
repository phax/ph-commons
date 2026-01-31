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

import java.security.GeneralSecurityException;
import java.security.cert.CertPathValidatorException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.security.crl.CRLCache;

/**
 * A helper class with certificate revocation check default settings.
 *
 * @author Philip Helger
 * @since 11.2.0
 */
@ThreadSafe
public final class CertificateRevocationCheckerDefaults
{
  // By default only CRL is used because
  // a) some responders have issues with OCSP
  // https://github.com/phax/phase4/issues/124
  // b) OCSP is not ideal from a privacy perspective
  // https://letsencrypt.org/2024/07/23/replacing-ocsp-with-crls/
  // 2026-01-27 Changed from CRL to CRL_BEFORE_OCSP to be more resilient
  public static final ERevocationCheckMode DEFAULT_REVOCATION_CHECK_MODE = ERevocationCheckMode.CRL_BEFORE_OCSP;
  public static final boolean DEFAULT_ALLOW_SOFT_FAIL = false;
  public static final boolean DEFAULT_ALLOW_EXEC_SYNC = true;
  public static final boolean DEFAULT_CACHE_REVOCATION_CHECK_RESULTS = true;
  public static final Duration DEFAULT_REVOCATION_CHECK_CACHING_DURATION = Duration.ofHours (6);

  private static final Logger LOGGER = LoggerFactory.getLogger (CertificateRevocationCheckerDefaults.class);

  private static final SimpleReadWriteLock RW_LOCK = new SimpleReadWriteLock ();
  @GuardedBy ("RW_LOCK")
  private static ERevocationCheckMode s_eRevocationCheckMode = DEFAULT_REVOCATION_CHECK_MODE;
  @GuardedBy ("RW_LOCK")
  private static Consumer <? super GeneralSecurityException> s_aExceptionHdl = ex -> LOGGER.warn ("Certificate is revoked",
                                                                                                  ex);
  private static final AtomicBoolean ALLOW_SOFT_FAIL = new AtomicBoolean (DEFAULT_ALLOW_SOFT_FAIL);
  @GuardedBy ("RW_LOCK")
  private static Consumer <? super List <? extends CertPathValidatorException>> s_aSoftFailExceptionHdl = exs -> LOGGER.warn ("Certificate revocation check succeeded but has messages: " +
                                                                                                                              exs);
  private static final AtomicBoolean ALLOW_EXEC_SYNC = new AtomicBoolean (DEFAULT_ALLOW_EXEC_SYNC);
  @GuardedBy ("RW_LOCK")
  private static CRLCache s_aDefaultCRLCache = CRLCache.createDefault ();
  // Revocation checking stuff
  private static final AtomicBoolean CACHE_REVOCATION_RESULTS = new AtomicBoolean (DEFAULT_CACHE_REVOCATION_CHECK_RESULTS);

  private CertificateRevocationCheckerDefaults ()
  {}

  /**
   * @return The global revocation check mode. Never <code>null</code>. The default is
   *         {@link ERevocationCheckMode#OCSP}.
   */
  @NonNull
  public static ERevocationCheckMode getRevocationCheckMode ()
  {
    return RW_LOCK.readLockedGet ( () -> s_eRevocationCheckMode);
  }

  /**
   * Set the global revocation check mode to use, if no specific mode was provided.
   *
   * @param eRevocationCheckMode
   *        The global revocation check mode to use. May not be <code>null</code>.
   */
  public static void setRevocationCheckMode (@NonNull final ERevocationCheckMode eRevocationCheckMode)
  {
    ValueEnforcer.notNull (eRevocationCheckMode, "RevocationCheckMode");
    RW_LOCK.writeLocked ( () -> s_eRevocationCheckMode = eRevocationCheckMode);
    LOGGER.info ("Global CertificateRevocationChecker revocation mode was set to: " + eRevocationCheckMode);
  }

  /**
   * @return The exception handler to be invoked in case of an exception in certificate checking.
   */
  @NonNull
  public static Consumer <? super GeneralSecurityException> getExceptionHdl ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aExceptionHdl);
  }

  /**
   * Set the exception handler to be invoked, if certificate checking throws an exception.
   *
   * @param aExceptionHdl
   *        The exception handler to be used. May not be <code>null</code>.
   */
  public static void setExceptionHdl (@NonNull final Consumer <? super GeneralSecurityException> aExceptionHdl)
  {
    ValueEnforcer.notNull (aExceptionHdl, "ExceptionHdl");
    RW_LOCK.writeLocked ( () -> s_aExceptionHdl = aExceptionHdl);
  }

  /**
   * Allow revocation check to succeed if the revocation status cannot be determined for one of the
   * following reasons:
   * <ul>
   * <li>The CRL or OCSP response cannot be obtained because of a network error.</li>
   * <li>The OCSP responder returns one of the following errors specified in section 2.3 of RFC
   * 2560: internalError or tryLater.</li>
   * </ul>
   * Note that these conditions apply to both OCSP and CRLs, and unless the NO_FALLBACK option is
   * set, the revocation check is allowed to succeed only if both mechanisms fail under one of the
   * conditions as stated above.Exceptions that cause the network errors are ignored but can be
   * later retrieved by calling the getSoftFailExceptions method.
   *
   * @return <code>true</code> if soft fail is enabled, <code>false</code> if not. Default is
   *         defined by {@link #DEFAULT_ALLOW_SOFT_FAIL}.
   */
  public static boolean isAllowSoftFail ()
  {
    return ALLOW_SOFT_FAIL.get ();
  }

  /**
   * Set enable "soft fail" mode.
   *
   * @param bAllow
   *        <code>true</code> to allow it, <code>false</code> to disallow it.
   */
  public static void setAllowSoftFail (final boolean bAllow)
  {
    ALLOW_SOFT_FAIL.set (bAllow);
    LOGGER.info ("Global CertificateRevocationChecker allows for soft fail: " + bAllow);
  }

  /**
   * @return The handler to be invoked in case of failures in certificate checking.
   * @see #isAllowSoftFail()
   */
  @NonNull
  public static Consumer <? super List <CertPathValidatorException>> getSoftFailExceptionHdl ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aSoftFailExceptionHdl);
  }

  /**
   * Set the handler to be invoked, if certificate checking has soft failures.
   *
   * @param aSoftFailExceptionHdl
   *        The handler to be used. May not be <code>null</code>.
   * @see #isAllowSoftFail()
   */
  public static void setSoftFailExceptionHdl (@NonNull final Consumer <? super List <? extends CertPathValidatorException>> aSoftFailExceptionHdl)
  {
    ValueEnforcer.notNull (aSoftFailExceptionHdl, "SoftFailExceptionHdl");
    RW_LOCK.writeLocked ( () -> s_aSoftFailExceptionHdl = aSoftFailExceptionHdl);
  }

  /**
   * @return <code>true</code> if the revocation check should be performed in a
   *         <code>synchronized</code> block, <code>false</code> if not. Default is defined by
   *         {@link #DEFAULT_ALLOW_EXEC_SYNC}.
   */
  public static boolean isExecuteInSynchronizedBlock ()
  {
    return ALLOW_EXEC_SYNC.get ();
  }

  /**
   * Enable or disable the execution of the revocation check in a <code>synchronized</code> block.
   *
   * @param bExecSync
   *        <code>true</code> to use the synchronized block, <code>false</code> to run it
   *        unsynchronized.
   */
  public static void setExecuteInSynchronizedBlock (final boolean bExecSync)
  {
    ALLOW_EXEC_SYNC.set (bExecSync);
    LOGGER.info ("Global CertificateRevocationChecker is executed synchronously: " + bExecSync);
  }

  /**
   * @return The default CRL cache to be used. Never <code>null</code>.
   */
  @NonNull
  public static CRLCache getDefaultCRLCache ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aDefaultCRLCache);
  }

  /**
   * Set the default CRL cache to be used.
   *
   * @param aCRLCache
   *        The cache to be used. Never <code>null</code>.
   */
  public static void setDefaultCRLCache (@NonNull final CRLCache aCRLCache)
  {
    ValueEnforcer.notNull (aCRLCache, "CRLCache");
    RW_LOCK.writeLocked ( () -> s_aDefaultCRLCache = aCRLCache);
    LOGGER.info ("Global default CRL Cache is set to: " + aCRLCache);
  }

  /**
   * @return <code>true</code> if OSCP results may be cached, <code>false</code> if not. The default
   *         is {@value #DEFAULT_CACHE_REVOCATION_CHECK_RESULTS}.
   */
  public static boolean isCacheRevocationCheckResults ()
  {
    return CACHE_REVOCATION_RESULTS.get ();
  }

  /**
   * Enable or disable caching of OSCP results.
   *
   * @param bCache
   *        <code>true</code> to enable caching, <code>false</code> to disable it.
   */
  public static void setCacheRevocationCheckResults (final boolean bCache)
  {
    CACHE_REVOCATION_RESULTS.set (bCache);
    LOGGER.info ("Global cache revocation check results enabled: " + bCache);
  }
}

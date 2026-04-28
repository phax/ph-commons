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
package com.helger.security.crl;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHelper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

/**
 * An allow list for CRL distribution point URLs. URLs extracted from the "CRL Distribution Points"
 * extension of an X.509 certificate are attacker controlled. Without an allow list, the default
 * {@link CRLDownloader} will issue an HTTP(S) request to whatever host the certificate names. That
 * can lead to:
 * <ul>
 * <li>Server-Side Request Forgery (SSRF) against internal infrastructure.</li>
 * <li>Unbounded download size, since the response body is read until EOF.</li>
 * <li>Long-running connections to slow or unresponsive hosts.</li>
 * </ul>
 * Use this class to restrict CRL downloads to a known set of trusted URL prefixes, e.g.
 * <code>https://crl.example.org/</code>.
 * <p>
 * Matching is case-insensitive (URL schemes and host names are case-insensitive per RFC 3986; the
 * path is technically case-sensitive but for an allow list a generous match is the safer default).
 * An allow list with no entries treats every URL as allowed (this matches the historical behavior).
 * As soon as one or more prefixes are added, only URLs starting with one of those prefixes are
 * accepted.
 * </p>
 *
 * @author Philip Helger
 * @since 12.2.4
 */
@ThreadSafe
public class CRLAllowList
{
  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("m_aRWLock")
  private final ICommonsList <String> m_aAllowedPrefixes = new CommonsArrayList <> ();

  /**
   * Default constructor. The created allow list contains no prefixes and therefore allows every
   * URL.
   */
  public CRLAllowList ()
  {}

  /**
   * @return <code>true</code> if no prefixes are configured (i.e. every URL is allowed),
   *         <code>false</code> if at least one prefix is configured.
   */
  public boolean isEmpty ()
  {
    return m_aRWLock.readLockedBoolean (m_aAllowedPrefixes::isEmpty);
  }

  /**
   * @return The number of configured allow list prefixes. Always &ge; 0.
   */
  @Nonnegative
  public int getPrefixCount ()
  {
    return m_aRWLock.readLockedInt (m_aAllowedPrefixes::size);
  }

  /**
   * @return A copy of the configured allow list prefixes. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllAllowedPrefixes ()
  {
    return m_aRWLock.readLockedGet (m_aAllowedPrefixes::getClone);
  }

  /**
   * Add an allowed URL prefix. URLs starting with this prefix will be allowed.
   *
   * @param sPrefix
   *        The prefix to add. May neither be <code>null</code> nor empty.
   * @return this for chaining
   */
  @NonNull
  public CRLAllowList addAllowedPrefix (@NonNull @Nonempty final String sPrefix)
  {
    ValueEnforcer.notEmpty (sPrefix, "Prefix");
    m_aRWLock.writeLocked ( () -> m_aAllowedPrefixes.add (sPrefix));
    return this;
  }

  /**
   * Remove all configured allow list prefixes. After calling this method, the allow list is empty
   * and therefore allows every URL.
   *
   * @return this for chaining
   */
  @NonNull
  public CRLAllowList removeAllPrefixes ()
  {
    m_aRWLock.writeLocked (m_aAllowedPrefixes::clear);
    return this;
  }

  /**
   * Check if the provided URL is allowed by this allow list.
   *
   * @param sURL
   *        The URL to check. May be <code>null</code>.
   * @return <code>true</code> if the URL is allowed (either because the allow list is empty or
   *         because the URL starts with one of the configured prefixes), <code>false</code>
   *         otherwise. A <code>null</code> or empty URL is never allowed when the allow list
   *         contains at least one prefix.
   */
  public boolean isAllowed (@Nullable final String sURL)
  {
    if (StringHelper.isEmpty (sURL))
      return false;

    return m_aRWLock.readLockedBoolean ( () -> {
      if (m_aAllowedPrefixes.isEmpty ())
        return true;

      for (final String sPrefix : m_aAllowedPrefixes)
        if (sURL.regionMatches (true, 0, sPrefix, 0, sPrefix.length ()))
          return true;
      return false;
    });
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("AllowedPrefixes", m_aAllowedPrefixes).getToString ();
  }
}

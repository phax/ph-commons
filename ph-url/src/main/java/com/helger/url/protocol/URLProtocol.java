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
package com.helger.url.protocol;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;

/**
 * A simple implementation of the {@link IURLProtocol} interface.
 *
 * @author Philip Helger
 */
public class URLProtocol implements IURLProtocol
{
  private final String m_sProtocol;
  private final boolean m_bAllowsForQueryParameters;

  /**
   * Constructor.
   *
   * @param sProtocol
   *        The protocol string (e.g. "http://"). May neither be <code>null</code> nor empty.
   * @param bAllowsForQueryParameters
   *        <code>true</code> if this protocol supports query parameters,
   *        <code>false</code> otherwise.
   */
  public URLProtocol (@NonNull @Nonempty final String sProtocol, final boolean bAllowsForQueryParameters)
  {
    m_sProtocol = ValueEnforcer.notEmpty (sProtocol, "Protocol");
    m_bAllowsForQueryParameters = bAllowsForQueryParameters;
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @Nonempty
  public String getProtocol ()
  {
    return m_sProtocol;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isUsedInURL (@Nullable final String sURL)
  {
    return sURL != null && sURL.startsWith (m_sProtocol);
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  public String getWithProtocol (@Nullable final String sURL)
  {
    if (sURL == null)
      return null;
    return m_sProtocol + sURL;
  }

  /**
   * Get the URL with this protocol prepended, but only if no known protocol is
   * already present.
   *
   * @param sURL
   *        The URL to check. May be <code>null</code>.
   * @return <code>null</code> if the input URL is <code>null</code>, the
   *         original URL if it already has a known protocol, or the URL
   *         prefixed with this protocol.
   */
  @Nullable
  public String getWithProtocolIfNone (@Nullable final String sURL)
  {
    if (sURL == null || URLProtocolRegistry.getInstance ().hasKnownProtocol (sURL))
      return sURL;
    return m_sProtocol + sURL;
  }

  /**
   * {@inheritDoc}
   */
  public boolean allowsForQueryParameters ()
  {
    return m_bAllowsForQueryParameters;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final URLProtocol rhs = (URLProtocol) o;
    return m_sProtocol.equals (rhs.m_sProtocol) && m_bAllowsForQueryParameters == rhs.m_bAllowsForQueryParameters;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sProtocol).append (m_bAllowsForQueryParameters).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("protocol", m_sProtocol).append ("queryParams", m_bAllowsForQueryParameters).getToString ();
  }
}

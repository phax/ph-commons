/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.url;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.lang.ServiceLoaderUtils;

/**
 * A central registry for supported URL protocols. By default, the registry will
 * include all protocols contained in {@link EURLProtocol}, but it may be
 * extended by custom protocols
 *
 * @author Boris Gregorcic
 * @author Philip Helger
 */
@Immutable
public final class URLProtocolRegistry
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (URLProtocolRegistry.class);

  private static final class SingletonHolder
  {
    static final URLProtocolRegistry s_aInstance = new URLProtocolRegistry ();
  }

  private static boolean s_bDefaultInstantiated = false;

  private final ReentrantReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  @GuardedBy ("m_aRWLock")
  private final Map <String, IURLProtocol> m_aProtocols = new HashMap <String, IURLProtocol> ();

  private URLProtocolRegistry ()
  {
    reinitialize ();
  }

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  @Nonnull
  public static URLProtocolRegistry getInstance ()
  {
    s_bDefaultInstantiated = true;
    return SingletonHolder.s_aInstance;
  }

  /**
   * Reinitialize all protocols. Adds all {@link EURLProtocol} values and
   * invokes all SPI implementations.
   */
  public void reinitialize ()
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      m_aProtocols.clear ();

      // Add all default protocols
      for (final EURLProtocol aProtocol : EURLProtocol.values ())
        m_aProtocols.put (aProtocol.getProtocol (), aProtocol);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }

    // Load all SPI implementations
    for (final IURLProtocolRegistrarSPI aRegistrar : ServiceLoaderUtils.getAllSPIImplementations (IURLProtocolRegistrarSPI.class))
    {
      final Set <? extends IURLProtocol> aURLProtocols = aRegistrar.getProtocols ();
      if (aURLProtocols != null)
        for (final IURLProtocol aSPIProtocol : aURLProtocols)
          registerProtocol (aSPIProtocol);
    }
    s_aLogger.info (getRegisteredProtocolCount () + " URL protocols registered");
  }

  /**
   * Registers a new protocol
   *
   * @param aProtocol
   *        The protocol to be registered. May not be <code>null</code>.
   * @throws IllegalArgumentException
   *         If another handler for this protocol is already installed.
   */
  public void registerProtocol (@Nonnull final IURLProtocol aProtocol)
  {
    ValueEnforcer.notNull (aProtocol, "Protocol");

    m_aRWLock.writeLock ().lock ();
    try
    {
      final String sProtocol = aProtocol.getProtocol ();
      if (m_aProtocols.containsKey (sProtocol))
        throw new IllegalArgumentException ("Another handler for protocol '" + sProtocol + "' is already registered!");
      m_aProtocols.put (sProtocol, aProtocol);
      s_aLogger.info ("Registered new custom URL protocol: " + aProtocol);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * @return All registered protocols
   */
  @Nonnull
  @ReturnsMutableCopy
  public Collection <IURLProtocol> getAllProtocols ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return ContainerHelper.newList (m_aProtocols.values ());
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnegative
  public int getRegisteredProtocolCount ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aProtocols.size ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * Try to evaluate the matching URL protocol from the passed URL
   *
   * @param sURL
   *        The URL to get the protocol from
   * @return The corresponding URL protocol or <code>null</code> if unresolved
   */
  @Nullable
  public IURLProtocol getProtocol (@Nullable final String sURL)
  {
    if (sURL != null)
    {
      m_aRWLock.readLock ().lock ();
      try
      {
        for (final IURLProtocol aProtocol : m_aProtocols.values ())
          if (aProtocol.isUsedInURL (sURL))
            return aProtocol;
      }
      finally
      {
        m_aRWLock.readLock ().unlock ();
      }
    }
    return null;
  }

  /**
   * Try to evaluate the matching URL protocol from the passed URL
   *
   * @param aURL
   *        The URL data
   * @return The corresponding URL protocol or <code>null</code> if unresolved
   */
  @Nullable
  public IURLProtocol getProtocol (@Nullable final IURLData aURL)
  {
    return aURL == null ? null : getProtocol (aURL.getPath ());
  }

  /**
   * Check if the passed URL has any known protocol
   *
   * @param sURL
   *        The URL to analyze
   * @return <code>true</code> if the protocol is known, <code>false</code>
   *         otherwise
   */
  public boolean hasKnownProtocol (@Nullable final String sURL)
  {
    return getProtocol (sURL) != null;
  }

  /**
   * Check if the passed URL has any known protocol
   *
   * @param aURL
   *        The URL to analyze
   * @return <code>true</code> if the protocol is known, <code>false</code>
   *         otherwise
   */
  public boolean hasKnownProtocol (@Nullable final IURLData aURL)
  {
    return getProtocol (aURL) != null;
  }

  /**
   * Return the passed URL where the protocol has been stripped (if known)
   *
   * @param sURL
   *        The URL to strip the protocol from. May be <code>null</code>.
   * @return The passed URL where any known protocol has been stripped
   */
  @Nullable
  public String getWithoutProtocol (@Nullable final String sURL)
  {
    final IURLProtocol aProtocol = getProtocol (sURL);
    return aProtocol == null ? sURL : sURL.substring (aProtocol.getProtocol ().length ());
  }
}

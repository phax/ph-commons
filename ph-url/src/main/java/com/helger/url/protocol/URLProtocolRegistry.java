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
package com.helger.url.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.misc.Singleton;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.spi.ServiceLoaderHelper;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsCollection;
import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.commons.ICommonsSet;
import com.helger.url.IURLData;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A central registry for supported URL protocols. By default, the registry will include all
 * protocols contained in {@link EURLProtocol}, but it may be extended by custom protocols
 *
 * @author Boris Gregorcic
 * @author Philip Helger
 */
@ThreadSafe
@Singleton
public final class URLProtocolRegistry
{
  private static final class SingletonHolder
  {
    private static final URLProtocolRegistry INSTANCE = new URLProtocolRegistry ();
  }

  private static final Logger LOGGER = LoggerFactory.getLogger (URLProtocolRegistry.class);

  private static boolean s_bDefaultInstantiated = false;

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("m_aRWLock")
  private final ICommonsMap <String, IURLProtocol> m_aProtocols = new CommonsHashMap <> ();

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
    final URLProtocolRegistry ret = SingletonHolder.INSTANCE;
    s_bDefaultInstantiated = true;
    return ret;
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

    final String sProtocol = aProtocol.getProtocol ();
    m_aRWLock.writeLocked ( () -> {
      if (m_aProtocols.containsKey (sProtocol))
        throw new IllegalArgumentException ("Another handler for protocol '" + sProtocol + "' is already registered!");
      m_aProtocols.put (sProtocol, aProtocol);
    });

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Registered new custom URL protocol: " + aProtocol);
  }

  /**
   * @return All registered protocols
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsCollection <IURLProtocol> getAllProtocols ()
  {
    return m_aRWLock.readLockedGet (m_aProtocols::copyOfValues);
  }

  @Nonnegative
  public int getRegisteredProtocolCount ()
  {
    return m_aRWLock.readLockedInt (m_aProtocols::size);
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
    if (sURL == null)
      return null;

    // Is called often - so no Lambdas
    m_aRWLock.readLock ().lock ();
    try
    {
      for (final IURLProtocol aProtocol : m_aProtocols.values ())
        if (aProtocol.isUsedInURL (sURL))
          return aProtocol;
      return null;
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
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
   * @return <code>true</code> if the protocol is known, <code>false</code> otherwise
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
   * @return <code>true</code> if the protocol is known, <code>false</code> otherwise
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

  @Nullable
  public String getWithProtocolIfNone (@Nonnull final IURLProtocol aProtocol, @Nullable final String sURL)
  {
    ValueEnforcer.notNull (aProtocol, "Protocol");
    if (sURL == null || hasKnownProtocol (sURL))
      return sURL;
    return aProtocol.getWithProtocol (sURL);
  }

  /**
   * Reinitialize all protocols. Adds all {@link EURLProtocol} values and invokes all SPI
   * implementations.
   */
  public void reinitialize ()
  {
    m_aRWLock.writeLocked ( () -> {
      m_aProtocols.clear ();

      // Add all default protocols
      for (final EURLProtocol aProtocol : EURLProtocol.values ())
        m_aProtocols.put (aProtocol.getProtocol (), aProtocol);
    });

    // Load all SPI implementations
    for (final IURLProtocolRegistrarSPI aRegistrar : ServiceLoaderHelper.getAllSPIImplementations (IURLProtocolRegistrarSPI.class))
    {
      final ICommonsSet <? extends IURLProtocol> aURLProtocols = aRegistrar.getAllProtocols ();
      if (aURLProtocols != null)
        for (final IURLProtocol aSPIProtocol : aURLProtocols)
          registerProtocol (aSPIProtocol);
    }

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug (getRegisteredProtocolCount () + " URL protocols registered");
  }
}

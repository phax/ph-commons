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
package com.helger.commons.url;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.Singleton;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.lang.ServiceLoaderHelper;

/**
 * A central registry for supported URL protocols. By default, the registry will
 * include all protocols contained in {@link EURLProtocol}, but it may be
 * extended by custom protocols
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
    static final URLProtocolRegistry s_aInstance = new URLProtocolRegistry ();
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (URLProtocolRegistry.class);

  private static boolean s_bDefaultInstantiated = false;

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
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
    final URLProtocolRegistry ret = SingletonHolder.s_aInstance;
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

    m_aRWLock.writeLocked ( () -> {
      final String sProtocol = aProtocol.getProtocol ();
      if (m_aProtocols.containsKey (sProtocol))
        throw new IllegalArgumentException ("Another handler for protocol '" + sProtocol + "' is already registered!");
      m_aProtocols.put (sProtocol, aProtocol);
    });

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Registered new custom URL protocol: " + aProtocol);
  }

  /**
   * @return All registered protocols
   */
  @Nonnull
  @ReturnsMutableCopy
  public Collection <IURLProtocol> getAllProtocols ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.newList (m_aProtocols.values ()));
  }

  @Nonnegative
  public int getRegisteredProtocolCount ()
  {
    return m_aRWLock.readLocked ( () -> m_aProtocols.size ());
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

    return m_aRWLock.readLocked ( () -> {
      for (final IURLProtocol aProtocol : m_aProtocols.values ())
        if (aProtocol.isUsedInURL (sURL))
          return aProtocol;
      return null;
    });
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

  /**
   * Reinitialize all protocols. Adds all {@link EURLProtocol} values and
   * invokes all SPI implementations.
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
      final Set <? extends IURLProtocol> aURLProtocols = aRegistrar.getAllProtocols ();
      if (aURLProtocols != null)
        for (final IURLProtocol aSPIProtocol : aURLProtocols)
          registerProtocol (aSPIProtocol);
    }

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug (getRegisteredProtocolCount () + " URL protocols registered");
  }
}

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
import com.helger.commons.annotations.PresentForCodeCoverage;
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

  private static final ReentrantReadWriteLock s_aRWLock = new ReentrantReadWriteLock ();
  @GuardedBy ("s_aRWLock")
  private static final Map <String, IURLProtocol> s_aProtocols = new HashMap <String, IURLProtocol> ();

  static
  {
    reinitialize ();
  }

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final URLProtocolRegistry s_aInstance = new URLProtocolRegistry ();

  private URLProtocolRegistry ()
  {}

  /**
   * Reinitialize all protocols. Adds all {@link EURLProtocol} values and
   * invokes all SPI implementations.
   */
  public static void reinitialize ()
  {
    s_aRWLock.writeLock ().lock ();
    try
    {
      s_aProtocols.clear ();

      // Add all default protocols
      for (final EURLProtocol aProtocol : EURLProtocol.values ())
        s_aProtocols.put (aProtocol.getProtocol (), aProtocol);
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
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
  public static void registerProtocol (@Nonnull final IURLProtocol aProtocol)
  {
    ValueEnforcer.notNull (aProtocol, "Protocol");

    s_aRWLock.writeLock ().lock ();
    try
    {
      final String sProtocol = aProtocol.getProtocol ();
      if (s_aProtocols.containsKey (sProtocol))
        throw new IllegalArgumentException ("Another handler for protocol '" + sProtocol + "' is already registered!");
      s_aProtocols.put (sProtocol, aProtocol);
      s_aLogger.info ("Registered new custom URL protocol: " + aProtocol);
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * @return All registered protocols
   */
  @Nonnull
  @ReturnsMutableCopy
  public static Collection <IURLProtocol> getAllProtocols ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return ContainerHelper.newList (s_aProtocols.values ());
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnegative
  public static int getRegisteredProtocolCount ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return s_aProtocols.size ();
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
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
  public static IURLProtocol getProtocol (@Nullable final String sURL)
  {
    if (sURL != null)
    {
      s_aRWLock.readLock ().lock ();
      try
      {
        for (final IURLProtocol aProtocol : s_aProtocols.values ())
          if (aProtocol.isUsedInURL (sURL))
            return aProtocol;
      }
      finally
      {
        s_aRWLock.readLock ().unlock ();
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
  public static IURLProtocol getProtocol (@Nullable final IURLData aURL)
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
  public static boolean hasKnownProtocol (@Nullable final String sURL)
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
  public static boolean hasKnownProtocol (@Nullable final IURLData aURL)
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
  public static String getWithoutProtocol (@Nullable final String sURL)
  {
    final IURLProtocol aProtocol = getProtocol (sURL);
    return aProtocol == null ? sURL : sURL.substring (aProtocol.getProtocol ().length ());
  }
}

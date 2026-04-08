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
package com.helger.scope.spi;

import java.util.List;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.misc.Singleton;
import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.mock.exception.IMockException;
import com.helger.base.spi.ServiceLoaderHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.scope.IGlobalScope;
import com.helger.scope.IRequestScope;
import com.helger.scope.ISessionScope;

/**
 * This is an internal class, that triggers the SPI implementations registered for scope lifecycle
 * SPI implementations. <b>Never</b> call this class from outside of this project!
 *
 * @author Philip Helger
 */
@ThreadSafe
@Singleton
public final class ScopeSPIManager
{
  private static final class SingletonHolder
  {
    private static final ScopeSPIManager INSTANCE = new ScopeSPIManager ();
  }

  private static final Logger LOGGER = LoggerFactory.getLogger (ScopeSPIManager.class);

  private static volatile boolean s_bDefaultInstantiated = false;

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("m_aRWLock")
  @CodingStyleguideUnaware
  private List <IGlobalScopeSPI> m_aGlobalSPIs;
  @GuardedBy ("m_aRWLock")
  @CodingStyleguideUnaware
  private List <ISessionScopeSPI> m_aSessionSPIs;
  @GuardedBy ("m_aRWLock")
  @CodingStyleguideUnaware
  private List <IRequestScopeSPI> m_aRequestSPIs;

  private ScopeSPIManager ()
  {
    reinitialize ();
  }

  /**
   * @return <code>true</code> if the singleton instance has been created,
   *         <code>false</code> otherwise.
   */
  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  /**
   * @return The singleton instance of this class. Never <code>null</code>.
   */
  @NonNull
  public static ScopeSPIManager getInstance ()
  {
    final ScopeSPIManager ret = SingletonHolder.INSTANCE;
    s_bDefaultInstantiated = true;
    return ret;
  }

  /**
   * Reinitialize all SPI listeners by reloading them from the service loader.
   */
  public void reinitialize ()
  {
    // Register all listeners
    m_aRWLock.writeLocked ( () -> {
      m_aGlobalSPIs = ServiceLoaderHelper.getAllSPIImplementations (IGlobalScopeSPI.class);
      m_aSessionSPIs = ServiceLoaderHelper.getAllSPIImplementations (ISessionScopeSPI.class);
      m_aRequestSPIs = ServiceLoaderHelper.getAllSPIImplementations (IRequestScopeSPI.class);
    });

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Reinitialized " + ScopeSPIManager.class.getName ());
  }

  /**
   * @return All registered global scope SPI listeners. Never <code>null</code> but maybe empty.
   */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <IGlobalScopeSPI> getAllGlobalScopeSPIs ()
  {
    return m_aRWLock.readLockedGet ( () -> new CommonsArrayList <> (m_aGlobalSPIs));
  }

  /**
   * @return All registered session scope SPI listeners. Never <code>null</code> but maybe empty.
   */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <ISessionScopeSPI> getAllSessionScopeSPIs ()
  {
    return m_aRWLock.readLockedGet ( () -> new CommonsArrayList <> (m_aSessionSPIs));
  }

  /**
   * @return All registered request scope SPI listeners. Never <code>null</code> but maybe empty.
   */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <IRequestScopeSPI> getAllRequestScopeSPIs ()
  {
    // Is called very often!
    m_aRWLock.readLock ().lock ();
    try
    {
      return new CommonsArrayList <> (m_aRequestSPIs);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nullable
  private static Exception _propagate (@NonNull final Exception ex)
  {
    return ex instanceof IMockException ? null : ex;
  }

  /**
   * Invoke all registered global scope SPI listeners for scope begin.
   *
   * @param aGlobalScope
   *        The global scope that just began. May not be <code>null</code>.
   */
  public void onGlobalScopeBegin (@NonNull final IGlobalScope aGlobalScope)
  {
    for (final IGlobalScopeSPI aSPI : getAllGlobalScopeSPIs ())
      try
      {
        aSPI.onGlobalScopeBegin (aGlobalScope);
      }
      catch (final Exception ex)
      {
        LOGGER.error ("Failed to invoke SPI method onGlobalScopeBegin on " + aSPI + " with scope " + aGlobalScope,
                      _propagate (ex));
      }
  }

  /**
   * Invoke all registered global scope SPI listeners for scope end.
   *
   * @param aGlobalScope
   *        The global scope that is about to end. May not be
   *        <code>null</code>.
   */
  public void onGlobalScopeEnd (@NonNull final IGlobalScope aGlobalScope)
  {
    for (final IGlobalScopeSPI aSPI : getAllGlobalScopeSPIs ())
      try
      {
        aSPI.onGlobalScopeEnd (aGlobalScope);
      }
      catch (final Exception ex)
      {
        LOGGER.error ("Failed to invoke SPI method onGlobalScopeEnd on " + aSPI + " with scope " + aGlobalScope,
                      _propagate (ex));
      }
  }

  /**
   * Invoke all registered session scope SPI listeners for scope begin.
   *
   * @param aSessionScope
   *        The session scope that just began. May not be <code>null</code>.
   */
  public void onSessionScopeBegin (@NonNull final ISessionScope aSessionScope)
  {
    for (final ISessionScopeSPI aSPI : getAllSessionScopeSPIs ())
      try
      {
        aSPI.onSessionScopeBegin (aSessionScope);
      }
      catch (final Exception ex)
      {
        LOGGER.error ("Failed to invoke SPI method onSessionScopeBegin on " + aSPI + " with scope " + aSessionScope,
                      _propagate (ex));
      }
  }

  /**
   * Invoke all registered session scope SPI listeners for scope end.
   *
   * @param aSessionScope
   *        The session scope that is about to end. May not be
   *        <code>null</code>.
   */
  public void onSessionScopeEnd (@NonNull final ISessionScope aSessionScope)
  {
    for (final ISessionScopeSPI aSPI : getAllSessionScopeSPIs ())
      try
      {
        aSPI.onSessionScopeEnd (aSessionScope);
      }
      catch (final Exception ex)
      {
        LOGGER.error ("Failed to invoke SPI method onSessionScopeEnd on " + aSPI + " with scope " + aSessionScope,
                      _propagate (ex));
      }
  }

  /**
   * Invoke all registered request scope SPI listeners for scope begin.
   *
   * @param aRequestScope
   *        The request scope that just began. May not be <code>null</code>.
   */
  public void onRequestScopeBegin (@NonNull final IRequestScope aRequestScope)
  {
    for (final IRequestScopeSPI aSPI : getAllRequestScopeSPIs ())
      try
      {
        aSPI.onRequestScopeBegin (aRequestScope);
      }
      catch (final Exception ex)
      {
        LOGGER.error ("Failed to invoke SPI method onRequestScopeBegin on " + aSPI + " with scope " + aRequestScope,
                      _propagate (ex));
      }
  }

  /**
   * Invoke all registered request scope SPI listeners for scope end.
   *
   * @param aRequestScope
   *        The request scope that is about to end. May not be
   *        <code>null</code>.
   */
  public void onRequestScopeEnd (@NonNull final IRequestScope aRequestScope)
  {
    for (final IRequestScopeSPI aSPI : getAllRequestScopeSPIs ())
      try
      {
        aSPI.onRequestScopeEnd (aRequestScope);
      }
      catch (final Exception ex)
      {
        LOGGER.error ("Failed to invoke SPI method onRequestScopeEnd on " + aSPI + " with scope " + aRequestScope,
                      _propagate (ex));
      }
  }
}

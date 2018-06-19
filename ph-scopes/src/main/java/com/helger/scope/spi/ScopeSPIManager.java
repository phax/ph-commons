/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.Singleton;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.exception.mock.IMockException;
import com.helger.commons.lang.ServiceLoaderHelper;
import com.helger.scope.IGlobalScope;
import com.helger.scope.IRequestScope;
import com.helger.scope.ISessionScope;

/**
 * This is an internal class, that triggers the SPI implementations registered
 * for scope lifecycle SPI implementations. <b>Never</b> call this class from
 * outside of this project!
 *
 * @author Philip Helger
 */
@ThreadSafe
@Singleton
public final class ScopeSPIManager
{
  private static final class SingletonHolder
  {
    private static final ScopeSPIManager s_aInstance = new ScopeSPIManager ();
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (ScopeSPIManager.class);

  private static boolean s_bDefaultInstantiated = false;

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("m_aRWLock")
  private ICommonsList <IGlobalScopeSPI> m_aGlobalSPIs;
  @GuardedBy ("m_aRWLock")
  private ICommonsList <ISessionScopeSPI> m_aSessionSPIs;
  @GuardedBy ("m_aRWLock")
  private ICommonsList <IRequestScopeSPI> m_aRequestSPIs;

  private ScopeSPIManager ()
  {
    reinitialize ();
  }

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  @Nonnull
  public static ScopeSPIManager getInstance ()
  {
    final ScopeSPIManager ret = SingletonHolder.s_aInstance;
    s_bDefaultInstantiated = true;
    return ret;
  }

  public void reinitialize ()
  {
    // Register all listeners
    m_aRWLock.writeLocked ( () -> {
      m_aGlobalSPIs = ServiceLoaderHelper.getAllSPIImplementations (IGlobalScopeSPI.class);
      m_aSessionSPIs = ServiceLoaderHelper.getAllSPIImplementations (ISessionScopeSPI.class);
      m_aRequestSPIs = ServiceLoaderHelper.getAllSPIImplementations (IRequestScopeSPI.class);
    });

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Reinitialized " + ScopeSPIManager.class.getName ());
  }

  /**
   * @return All registered global scope SPI listeners. Never <code>null</code>
   *         but maybe empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <IGlobalScopeSPI> getAllGlobalScopeSPIs ()
  {
    return m_aRWLock.readLocked ( () -> m_aGlobalSPIs.getClone ());
  }

  /**
   * @return All registered session scope SPI listeners. Never <code>null</code>
   *         but maybe empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <ISessionScopeSPI> getAllSessionScopeSPIs ()
  {
    return m_aRWLock.readLocked ( () -> m_aSessionSPIs.getClone ());
  }

  /**
   * @return All registered request scope SPI listeners. Never <code>null</code>
   *         but maybe empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <IRequestScopeSPI> getAllRequestScopeSPIs ()
  {
    return m_aRWLock.readLocked ( () -> m_aRequestSPIs.getClone ());
  }

  public void onGlobalScopeBegin (@Nonnull final IGlobalScope aGlobalScope)
  {
    for (final IGlobalScopeSPI aSPI : getAllGlobalScopeSPIs ())
      try
      {
        aSPI.onGlobalScopeBegin (aGlobalScope);
      }
      catch (final Exception ex)
      {
        if (s_aLogger.isErrorEnabled ())
          s_aLogger.error ("Failed to invoke SPI method onGlobalScopeBegin on " + aSPI + " with scope " + aGlobalScope,
                           ex instanceof IMockException ? null : ex);
      }
  }

  public void onGlobalScopeEnd (@Nonnull final IGlobalScope aGlobalScope)
  {
    for (final IGlobalScopeSPI aSPI : getAllGlobalScopeSPIs ())
      try
      {
        aSPI.onGlobalScopeEnd (aGlobalScope);
      }
      catch (final Exception ex)
      {
        if (s_aLogger.isErrorEnabled ())
          s_aLogger.error ("Failed to invoke SPI method onGlobalScopeEnd on " + aSPI + " with scope " + aGlobalScope,
                           ex instanceof IMockException ? null : ex);
      }
  }

  public void onSessionScopeBegin (@Nonnull final ISessionScope aSessionScope)
  {
    for (final ISessionScopeSPI aSPI : getAllSessionScopeSPIs ())
      try
      {
        aSPI.onSessionScopeBegin (aSessionScope);
      }
      catch (final Exception ex)
      {
        if (s_aLogger.isErrorEnabled ())
          s_aLogger.error ("Failed to invoke SPI method onSessionScopeBegin on " +
                           aSPI +
                           " with scope " +
                           aSessionScope,
                           ex instanceof IMockException ? null : ex);
      }
  }

  public void onSessionScopeEnd (@Nonnull final ISessionScope aSessionScope)
  {
    for (final ISessionScopeSPI aSPI : getAllSessionScopeSPIs ())
      try
      {
        aSPI.onSessionScopeEnd (aSessionScope);
      }
      catch (final Exception ex)
      {
        if (s_aLogger.isErrorEnabled ())
          s_aLogger.error ("Failed to invoke SPI method onSessionScopeEnd on " + aSPI + " with scope " + aSessionScope,
                           ex instanceof IMockException ? null : ex);
      }
  }

  public void onRequestScopeBegin (@Nonnull final IRequestScope aRequestScope)
  {
    for (final IRequestScopeSPI aSPI : getAllRequestScopeSPIs ())
      try
      {
        aSPI.onRequestScopeBegin (aRequestScope);
      }
      catch (final Exception ex)
      {
        if (s_aLogger.isErrorEnabled ())
          s_aLogger.error ("Failed to invoke SPI method onRequestScopeBegin on " +
                           aSPI +
                           " with scope " +
                           aRequestScope,
                           ex instanceof IMockException ? null : ex);
      }
  }

  public void onRequestScopeEnd (@Nonnull final IRequestScope aRequestScope)
  {
    for (final IRequestScopeSPI aSPI : getAllRequestScopeSPIs ())
      try
      {
        aSPI.onRequestScopeEnd (aRequestScope);
      }
      catch (final Exception ex)
      {
        if (s_aLogger.isErrorEnabled ())
          s_aLogger.error ("Failed to invoke SPI method onRequestScopeEnd on " + aSPI + " with scope " + aRequestScope,
                           ex instanceof IMockException ? null : ex);
      }
  }
}

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
package com.helger.commons.scope.spi;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.Singleton;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.exception.mock.IMockException;
import com.helger.commons.lang.ServiceLoaderHelper;
import com.helger.commons.scope.IApplicationScope;
import com.helger.commons.scope.IGlobalScope;
import com.helger.commons.scope.IRequestScope;
import com.helger.commons.scope.ISessionApplicationScope;
import com.helger.commons.scope.ISessionScope;

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
    static final ScopeSPIManager s_aInstance = new ScopeSPIManager ();
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (ScopeSPIManager.class);

  private static boolean s_bDefaultInstantiated = false;

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("m_aRWLock")
  private List <IGlobalScopeSPI> m_aGlobalSPIs;
  @GuardedBy ("m_aRWLock")
  private List <IApplicationScopeSPI> m_aApplicationSPIs;
  @GuardedBy ("m_aRWLock")
  private List <ISessionScopeSPI> m_aSessionSPIs;
  @GuardedBy ("m_aRWLock")
  private List <ISessionApplicationScopeSPI> m_aSessionApplicationSPIs;
  @GuardedBy ("m_aRWLock")
  private List <IRequestScopeSPI> m_aRequestSPIs;

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
      m_aApplicationSPIs = ServiceLoaderHelper.getAllSPIImplementations (IApplicationScopeSPI.class);
      m_aSessionSPIs = ServiceLoaderHelper.getAllSPIImplementations (ISessionScopeSPI.class);
      m_aSessionApplicationSPIs = ServiceLoaderHelper.getAllSPIImplementations (ISessionApplicationScopeSPI.class);
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
  public List <IGlobalScopeSPI> getAllGlobalScopeSPIs ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.newList (m_aGlobalSPIs));
  }

  /**
   * @return All registered application scope SPI listeners. Never
   *         <code>null</code> but maybe empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  public List <IApplicationScopeSPI> getAllApplicationScopeSPIs ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.newList (m_aApplicationSPIs));
  }

  /**
   * @return All registered session scope SPI listeners. Never <code>null</code>
   *         but maybe empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  public List <ISessionScopeSPI> getAllSessionScopeSPIs ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.newList (m_aSessionSPIs));
  }

  /**
   * @return All registered session application scope SPI listeners. Never
   *         <code>null</code> but maybe empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  public List <ISessionApplicationScopeSPI> getAllSessionApplicationScopeSPIs ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.newList (m_aSessionApplicationSPIs));
  }

  /**
   * @return All registered request scope SPI listeners. Never <code>null</code>
   *         but maybe empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  public List <IRequestScopeSPI> getAllRequestScopeSPIs ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.newList (m_aRequestSPIs));
  }

  public void onGlobalScopeBegin (@Nonnull final IGlobalScope aGlobalScope)
  {
    for (final IGlobalScopeSPI aSPI : getAllGlobalScopeSPIs ())
      try
      {
        aSPI.onGlobalScopeBegin (aGlobalScope);
      }
      catch (final Throwable t)
      {
        s_aLogger.error ("Failed to invoke SPI method onGlobalScopeBegin on " +
                         aSPI +
                         " with scope " +
                         aGlobalScope,
                         t instanceof IMockException ? null : t);
      }
  }

  public void onGlobalScopeEnd (@Nonnull final IGlobalScope aGlobalScope)
  {
    for (final IGlobalScopeSPI aSPI : getAllGlobalScopeSPIs ())
      try
      {
        aSPI.onGlobalScopeEnd (aGlobalScope);
      }
      catch (final Throwable t)
      {
        s_aLogger.error ("Failed to invoke SPI method onGlobalScopeEnd on " +
                         aSPI +
                         " with scope " +
                         aGlobalScope,
                         t instanceof IMockException ? null : t);
      }
  }

  public void onApplicationScopeBegin (@Nonnull final IApplicationScope aApplicationScope)
  {
    for (final IApplicationScopeSPI aSPI : getAllApplicationScopeSPIs ())
      try
      {
        aSPI.onApplicationScopeBegin (aApplicationScope);
      }
      catch (final Throwable t)
      {
        s_aLogger.error ("Failed to invoke SPI method onApplicationScopeBegin on " +
                         aSPI +
                         " with scope " +
                         aApplicationScope,
                         t instanceof IMockException ? null : t);
      }
  }

  public void onApplicationScopeEnd (@Nonnull final IApplicationScope aApplicationScope)
  {
    for (final IApplicationScopeSPI aSPI : getAllApplicationScopeSPIs ())
      try
      {
        aSPI.onApplicationScopeEnd (aApplicationScope);
      }
      catch (final Throwable t)
      {
        s_aLogger.error ("Failed to invoke SPI method onApplicationScopeEnd on " +
                         aSPI +
                         " with scope " +
                         aApplicationScope,
                         t instanceof IMockException ? null : t);
      }
  }

  public void onSessionScopeBegin (@Nonnull final ISessionScope aSessionScope)
  {
    for (final ISessionScopeSPI aSPI : getAllSessionScopeSPIs ())
      try
      {
        aSPI.onSessionScopeBegin (aSessionScope);
      }
      catch (final Throwable t)
      {
        s_aLogger.error ("Failed to invoke SPI method onSessionScopeBegin on " +
                         aSPI +
                         " with scope " +
                         aSessionScope,
                         t instanceof IMockException ? null : t);
      }
  }

  public void onSessionScopeEnd (@Nonnull final ISessionScope aSessionScope)
  {
    for (final ISessionScopeSPI aSPI : getAllSessionScopeSPIs ())
      try
      {
        aSPI.onSessionScopeEnd (aSessionScope);
      }
      catch (final Throwable t)
      {
        s_aLogger.error ("Failed to invoke SPI method onSessionScopeEnd on " +
                         aSPI +
                         " with scope " +
                         aSessionScope,
                         t instanceof IMockException ? null : t);
      }
  }

  public void onSessionApplicationScopeBegin (@Nonnull final ISessionApplicationScope aSessionApplicationScope)
  {
    for (final ISessionApplicationScopeSPI aSPI : getAllSessionApplicationScopeSPIs ())
      try
      {
        aSPI.onSessionApplicationScopeBegin (aSessionApplicationScope);
      }
      catch (final Throwable t)
      {
        s_aLogger.error ("Failed to invoke SPI method onSessionApplicationScopeBegin on " +
                         aSPI +
                         " with scope " +
                         aSessionApplicationScope,
                         t instanceof IMockException ? null : t);
      }
  }

  public void onSessionApplicationScopeEnd (@Nonnull final ISessionApplicationScope aSessionApplicationScope)
  {
    for (final ISessionApplicationScopeSPI aSPI : getAllSessionApplicationScopeSPIs ())
      try
      {
        aSPI.onSessionApplicationScopeEnd (aSessionApplicationScope);
      }
      catch (final Throwable t)
      {
        s_aLogger.error ("Failed to invoke SPI method onSessionApplicationScopeEnd on " +
                         aSPI +
                         " with scope " +
                         aSessionApplicationScope,
                         t instanceof IMockException ? null : t);
      }
  }

  public void onRequestScopeBegin (@Nonnull final IRequestScope aRequestScope)
  {
    for (final IRequestScopeSPI aSPI : getAllRequestScopeSPIs ())
      try
      {
        aSPI.onRequestScopeBegin (aRequestScope);
      }
      catch (final Throwable t)
      {
        s_aLogger.error ("Failed to invoke SPI method onRequestScopeBegin on " +
                         aSPI +
                         " with scope " +
                         aRequestScope,
                         t instanceof IMockException ? null : t);
      }
  }

  public void onRequestScopeEnd (@Nonnull final IRequestScope aRequestScope)
  {
    for (final IRequestScopeSPI aSPI : getAllRequestScopeSPIs ())
      try
      {
        aSPI.onRequestScopeEnd (aRequestScope);
      }
      catch (final Throwable t)
      {
        s_aLogger.error ("Failed to invoke SPI method onRequestScopeEnd on " +
                         aSPI +
                         " with scope " +
                         aRequestScope,
                         t instanceof IMockException ? null : t);
      }
  }
}

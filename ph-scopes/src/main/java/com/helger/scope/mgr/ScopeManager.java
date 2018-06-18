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
package com.helger.scope.mgr;

import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.concurrent.SimpleLock;
import com.helger.commons.string.StringHelper;
import com.helger.scope.GlobalScope;
import com.helger.scope.IGlobalScope;
import com.helger.scope.IRequestScope;
import com.helger.scope.ISessionScope;
import com.helger.scope.RequestScope;
import com.helger.scope.ScopeHelper;
import com.helger.scope.SessionScope;
import com.helger.scope.spi.ScopeSPIManager;

/**
 * This is the manager class for non-web scope handling. The following scopes
 * are supported:
 * <ul>
 * <li>global</li>
 * <li>application</li>
 * <li>request</li>
 * <li>session</li>
 * <li>session application</li>
 * </ul>
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class ScopeManager
{
  /**
   * The prefix to be used for attribute names in any scope to indicate system
   * internal attributes
   */
  public static final String SCOPE_ATTRIBUTE_PREFIX_INTERNAL = "$ph-";

  public static final boolean DEFAULT_CREATE_SCOPE = true;

  private static final Logger s_aLogger = LoggerFactory.getLogger (ScopeManager.class);

  private static final SimpleLock s_aGlobalLock = new SimpleLock ();

  /** Global scope */
  @GuardedBy ("s_aGlobalLock")
  private static volatile IGlobalScope s_aGlobalScope;

  /** Request scope */
  private static final ThreadLocal <IRequestScope> s_aRequestScopeTL = new ThreadLocal <> ();

  @PresentForCodeCoverage
  private static final ScopeManager s_aInstance = new ScopeManager ();

  private ScopeManager ()
  {}

  // --- global scope ---

  /**
   * This method is only to be called by this class and the web scope manager!
   *
   * @param aGlobalScope
   *        The scope to be set. May not be <code>null</code>.
   */
  public static void setGlobalScope (@Nonnull final IGlobalScope aGlobalScope)
  {
    ValueEnforcer.notNull (aGlobalScope, "GlobalScope");

    s_aGlobalLock.locked ( () -> {
      if (s_aGlobalScope != null)
        throw new IllegalStateException ("Another global scope with ID '" +
                                         s_aGlobalScope.getID () +
                                         "' is already present. New global scope with ID '" +
                                         aGlobalScope.getID () +
                                         "' is not set!");

      s_aGlobalScope = aGlobalScope;

      aGlobalScope.initScope ();
      if (ScopeHelper.debugGlobalScopeLifeCycle (s_aLogger))
        s_aLogger.info ("Global scope '" + aGlobalScope.getID () + "' initialized!", ScopeHelper.getDebugStackTrace ());

      // Invoke SPIs
      ScopeSPIManager.getInstance ().onGlobalScopeBegin (aGlobalScope);
    });
  }

  /**
   * This method is used to set the initial global scope.
   *
   * @param sScopeID
   *        The scope ID to use
   * @return The created global scope object. Never <code>null</code>.
   */
  @Nonnull
  public static IGlobalScope onGlobalBegin (@Nonnull @Nonempty final String sScopeID)
  {
    return onGlobalBegin (sScopeID, GlobalScope::new);
  }

  @Nonnull
  public static <T extends IGlobalScope> T onGlobalBegin (@Nonnull @Nonempty final String sScopeID,
                                                          @Nonnull final Function <? super String, T> aFactory)
  {
    final T aGlobalScope = aFactory.apply (sScopeID);
    setGlobalScope (aGlobalScope);
    return aGlobalScope;
  }

  @Nullable
  public static IGlobalScope getGlobalScopeOrNull ()
  {
    final IGlobalScope ret = s_aGlobalScope;
    if (ret != null && ret.isValid ())
      return ret;
    // Return null if it is not set, in destruction or already destroyed
    return null;
  }

  public static boolean isGlobalScopePresent ()
  {
    return getGlobalScopeOrNull () != null;
  }

  @Nonnull
  public static IGlobalScope getGlobalScope ()
  {
    final IGlobalScope aGlobalScope = getGlobalScopeOrNull ();
    if (aGlobalScope == null)
      throw new IllegalStateException ("No global scope object has been set!");
    return aGlobalScope;
  }

  /**
   * To be called when the singleton global context is to be destroyed.
   */
  public static void onGlobalEnd ()
  {
    s_aGlobalLock.locked ( () -> {
      /**
       * This code removes all attributes set for the global context. This is
       * necessary, since the attributes would survive a Tomcat servlet context
       * reload if we don't kill them manually.<br>
       * Global scope variable may be null if onGlobalBegin() was never called!
       */
      if (s_aGlobalScope != null)
      {
        // Invoke SPI
        ScopeSPIManager.getInstance ().onGlobalScopeEnd (s_aGlobalScope);

        // Destroy and invalidate scope
        final String sDestroyedScopeID = s_aGlobalScope.getID ();
        s_aGlobalScope.destroyScope ();
        s_aGlobalScope = null;

        // done
        if (ScopeHelper.debugGlobalScopeLifeCycle (s_aLogger))
          s_aLogger.info ("Global scope '" + sDestroyedScopeID + "' shut down!", ScopeHelper.getDebugStackTrace ());
      }
      else
        s_aLogger.warn ("No global scope present that could be shut down!");
    });
  }

  // --- session scope ---

  /**
   * Get the current session scope, based on the current request scope.
   *
   * @return Never <code>null</code>.
   * @throws IllegalStateException
   *         If no request scope is present or if the underlying request scope
   *         does not have a session ID.
   */
  @Nonnull
  public static ISessionScope getSessionScope ()
  {
    return getSessionScope (ScopeManager.DEFAULT_CREATE_SCOPE);
  }

  /**
   * Get the current session scope, based on the current request scope.
   *
   * @param bCreateIfNotExisting
   *        <code>true</code> to create a new scope, if none is present yet,
   *        <code>false</code> to return <code>null</code> if either no request
   *        scope or no session scope is present.
   * @return <code>null</code> if bCreateIfNotExisting is <code>false</code> and
   *         either no request scope or no session scope is present, the
   *         {@link ISessionScope} otherwise.
   * @throws IllegalStateException
   *         if bCreateIfNotExisting is <code>true</code> but no request scope
   *         is present. This exception is also thrown if the underlying request
   *         scope does not have a session ID.
   */
  @Nullable
  public static ISessionScope getSessionScope (final boolean bCreateIfNotExisting)
  {
    return getSessionScope (bCreateIfNotExisting, SessionScope::new);
  }

  @Nullable
  public static ISessionScope getSessionScope (final boolean bCreateIfNotExisting,
                                               @Nonnull final Function <? super String, ? extends ISessionScope> aFactory)
  {
    final IRequestScope aRequestScope = getRequestScopeOrNull ();
    if (aRequestScope != null)
    {
      final ScopeSessionManager aSSM = ScopeSessionManager.getInstance ();

      // Get the session ID from the underlying request
      final String sSessionID = aRequestScope.getSessionID (bCreateIfNotExisting);

      // Check if a matching session scope is present
      ISessionScope aSessionScope = aSSM.getSessionScopeOfID (sSessionID);
      if (aSessionScope == null && bCreateIfNotExisting)
      {
        if (sSessionID == null)
          throw new IllegalStateException ("Cannot create a SessionScope without a known session ID!");

        // Create a new session scope
        aSessionScope = aFactory.apply (sSessionID);

        // And register in the Session Manager
        aSSM.onScopeBegin (aSessionScope);
      }

      // We're done - maybe null
      return aSessionScope;
    }

    // If we want a session scope, we expect the return value to be non-null!
    if (bCreateIfNotExisting)
      throw new IllegalStateException ("No request scope is present, so no session scope can be created!");

    // No request scope present and no need to create a session
    return null;
  }

  /**
   * Manually destroy the passed session scope.
   *
   * @param aSessionScope
   *        The session scope to be destroyed. May not be <code>null</code>.
   */
  public static void destroySessionScope (@Nonnull final ISessionScope aSessionScope)
  {
    ValueEnforcer.notNull (aSessionScope, "SessionScope");

    ScopeSessionManager.getInstance ().onScopeEnd (aSessionScope);
  }

  // --- request scope ---

  /**
   * This method is only to be called by this class and the web scope manager!
   *
   * @param aRequestScope
   *        The request scope to use. May not be <code>null</code>.
   */
  public static void internalSetAndInitRequestScope (@Nonnull final IRequestScope aRequestScope)
  {
    ValueEnforcer.notNull (aRequestScope, "RequestScope");
    ValueEnforcer.isTrue (ScopeManager::isGlobalScopePresent,
                          "No global context present! May be the global context listener is not installed?");

    // Happens if an internal redirect happens in a web-application (e.g. for
    // 404 page)
    final IRequestScope aExistingRequestScope = s_aRequestScopeTL.get ();
    if (aExistingRequestScope != null)
    {
      if (s_aLogger.isWarnEnabled ())
        s_aLogger.warn ("A request scope is already present - will overwrite it: " + aExistingRequestScope.toString ());
      if (aExistingRequestScope.isValid ())
      {
        // The scope shall be destroyed here, as this is most probably a
        // programming error!
        s_aLogger.warn ("Destroying the old request scope before the new one gets initialized!");
        _destroyRequestScope (aExistingRequestScope);
      }
    }

    // set request context
    s_aRequestScopeTL.set (aRequestScope);

    // Now init the scope
    aRequestScope.initScope ();

    // call SPIs
    ScopeSPIManager.getInstance ().onRequestScopeBegin (aRequestScope);
  }

  @Nonnull
  public static IRequestScope onRequestBegin (@Nonnull @Nonempty final String sScopeID,
                                              @Nonnull @Nonempty final String sSessionID)
  {
    return onRequestBegin (sScopeID, sSessionID, RequestScope::new);
  }

  @Nonnull
  public static <T extends IRequestScope> T onRequestBegin (@Nonnull @Nonempty final String sScopeID,
                                                            @Nonnull @Nonempty final String sSessionID,
                                                            @Nonnull final BiFunction <? super String, ? super String, T> aFactory)
  {
    final T aRequestScope = aFactory.apply (sScopeID, sSessionID);
    internalSetAndInitRequestScope (aRequestScope);
    return aRequestScope;
  }

  /**
   * @return The current request scope or <code>null</code> if no request scope
   *         is present.
   */
  @Nullable
  public static IRequestScope getRequestScopeOrNull ()
  {
    return s_aRequestScopeTL.get ();
  }

  /**
   * @return <code>true</code> if a request scope is present, <code>false</code>
   *         otherwise
   */
  public static boolean isRequestScopePresent ()
  {
    return getRequestScopeOrNull () != null;
  }

  /**
   * @return The current request scope and never <code>null</code>.
   * @throws IllegalStateException
   *         If no request scope is present
   */
  @Nonnull
  public static IRequestScope getRequestScope ()
  {
    final IRequestScope aScope = getRequestScopeOrNull ();
    if (aScope == null)
      throw new IllegalStateException ("No request scope is available.");
    return aScope;
  }

  private static void _destroyRequestScope (@Nonnull final IRequestScope aRequestScope)
  {
    // call SPIs
    ScopeSPIManager.getInstance ().onRequestScopeEnd (aRequestScope);

    // Destroy scope
    aRequestScope.destroyScope ();
  }

  /**
   * Internal method to clear request scope thread local.
   *
   * @since 9.0.0
   */
  public static void internalClearRequestScope ()
  {
    // Remove from ThreadLocal
    s_aRequestScopeTL.remove ();
  }

  /**
   * To be called after a request finished.
   */
  public static void onRequestEnd ()
  {
    final IRequestScope aRequestScope = getRequestScopeOrNull ();
    try
    {
      // Do we have something to destroy?
      if (aRequestScope != null)
      {
        _destroyRequestScope (aRequestScope);
      }
      else
      {
        // Happens after an internal redirect happened in a web-application
        // (e.g. for 404 page) for the original scope
        s_aLogger.warn ("No request scope present that could be ended!");
      }
    }
    finally
    {
      // Remove from ThreadLocal
      internalClearRequestScope ();
    }
  }

  /**
   * Check if the passed attribute name is an internal attribute.
   *
   * @param sAttributeName
   *        The name of the attribute to check. May be <code>null</code>.
   * @return <code>true</code> if the passed attribute name is not
   *         <code>null</code> and starts with the
   *         {@link #SCOPE_ATTRIBUTE_PREFIX_INTERNAL} prefix.
   */
  public static boolean isInternalAttribute (@Nullable final String sAttributeName)
  {
    return StringHelper.startsWith (sAttributeName, SCOPE_ATTRIBUTE_PREFIX_INTERNAL);
  }
}

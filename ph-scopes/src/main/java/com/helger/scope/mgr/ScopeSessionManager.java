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
package com.helger.scope.mgr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;
import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.misc.ReturnsMutableCopy;
import com.helger.annotation.misc.Singleton;
import com.helger.annotation.misc.UsedViaReflection;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.state.EChange;
import com.helger.commons.statistics.IMutableStatisticsHandlerCounter;
import com.helger.commons.statistics.StatisticsManager;
import com.helger.commons.string.StringHelper;
import com.helger.scope.IScope;
import com.helger.scope.ISessionScope;
import com.helger.scope.singleton.AbstractGlobalSingleton;
import com.helger.scope.spi.ScopeSPIManager;

/**
 * Internal manager class for session scopes.<br>
 * This class is only non-final so that the WebScopeSessionManager can be used for web scopes!
 *
 * @author Philip Helger
 */
@ThreadSafe
@Singleton
public class ScopeSessionManager extends AbstractGlobalSingleton
{
  public static final boolean DEFAULT_DESTROY_ALL_SESSIONS_ON_SCOPE_END = true;
  public static final boolean DEFAULT_END_ALL_SESSIONS_ON_SCOPE_END = true;
  private static final Logger LOGGER = LoggerFactory.getLogger (ScopeSessionManager.class);
  private static final IMutableStatisticsHandlerCounter STATS_UNIQUE_SESSIONS = StatisticsManager.getCounterHandler (ScopeSessionManager.class.getName () +
                                                                                                                     "$UNIQUE_SESSIONS");

  private static ScopeSessionManager s_aInstance;

  /** All contained session scopes. */
  @GuardedBy ("m_aRWLock")
  private final ICommonsMap <String, ISessionScope> m_aSessionScopes = new CommonsHashMap <> ();
  @GuardedBy ("m_aRWLock")
  private final ICommonsSet <String> m_aSessionsInDestruction = new CommonsHashSet <> ();
  @GuardedBy ("m_aRWLock")
  private boolean m_bDestroyAllSessionsOnScopeEnd = DEFAULT_DESTROY_ALL_SESSIONS_ON_SCOPE_END;
  @GuardedBy ("m_aRWLock")
  private boolean m_bEndAllSessionsOnScopeEnd = DEFAULT_END_ALL_SESSIONS_ON_SCOPE_END;

  /**
   * Invoked internally.
   *
   * @deprecated Do not call explicitly
   */
  @Deprecated
  @UsedViaReflection
  public ScopeSessionManager ()
  {}

  @Nonnull
  public static ScopeSessionManager getInstance ()
  {
    // This special handling is needed, because this global singleton is
    // required upon shutdown of the GlobalWebScope!
    ScopeSessionManager ret = s_aInstance;
    if (ret == null)
      ret = s_aInstance = getGlobalSingleton (ScopeSessionManager.class);
    return ret;
  }

  /**
   * Get the session scope with the specified ID. If no such scope exists, no further actions are
   * taken.
   *
   * @param sScopeID
   *        The ID to be resolved. May be <code>null</code>.
   * @return <code>null</code> if no such scope exists.
   */
  @Nullable
  public ISessionScope getSessionScopeOfID (@Nullable final String sScopeID)
  {
    if (StringHelper.hasNoText (sScopeID))
      return null;

    return m_aRWLock.readLockedGet ( () -> m_aSessionScopes.get (sScopeID));
  }

  /**
   * Register the passed session scope in the internal map, call the {@link ISessionScope
   * #initScope()} method and finally invoke the SPIs for the new scope.
   *
   * @param aSessionScope
   *        The session scope that was just created. May not be <code>null</code>.
   */
  public void onScopeBegin (@Nonnull final ISessionScope aSessionScope)
  {
    ValueEnforcer.notNull (aSessionScope, "SessionScope");

    final String sSessionID = aSessionScope.getID ();
    m_aRWLock.writeLocked ( () -> {
      if (m_aSessionScopes.put (sSessionID, aSessionScope) != null)
        LOGGER.error ("Overwriting session scope with ID '" + sSessionID + "'");
    });

    // Init the scope after it was registered
    aSessionScope.initScope ();

    // Invoke SPIs
    ScopeSPIManager.getInstance ().onSessionScopeBegin (aSessionScope);

    // Increment statistics counter
    STATS_UNIQUE_SESSIONS.increment ();
  }

  /**
   * Close the passed session scope gracefully. Each managed scope is guaranteed to be destroyed
   * only once. First the SPI manager is invoked, and afterwards the scope is destroyed.
   *
   * @param aSessionScope
   *        The session scope to be ended. May not be <code>null</code>.
   */
  public void onScopeEnd (@Nonnull final ISessionScope aSessionScope)
  {
    ValueEnforcer.notNull (aSessionScope, "SessionScope");

    // Only handle scopes that are not yet destructed
    if (aSessionScope.isValid ())
    {
      final String sSessionID = aSessionScope.getID ();

      final boolean bCanDestroyScope = m_aRWLock.writeLockedBoolean ( () -> {
        boolean bWLCanDestroyScope = false;
        // Only if we're not just in destruction of exactly this session
        if (m_aSessionsInDestruction.add (sSessionID))
        {
          // Remove from map
          final ISessionScope aRemovedScope = m_aSessionScopes.remove (sSessionID);
          if (!EqualsHelper.identityEqual (aRemovedScope, aSessionScope))
          {
            LOGGER.error ("Ending an unknown session with ID '" + sSessionID + "'");
            LOGGER.error ("  Scope to be removed: " + aSessionScope);
            LOGGER.error ("  Removed scope:       " + aRemovedScope);
          }
          bWLCanDestroyScope = true;
        }
        else
          LOGGER.info ("Already destructing session '" + sSessionID + "'");
        return bWLCanDestroyScope;
      });

      if (bCanDestroyScope)
      {
        // Destroy scope outside of write lock
        try
        {
          // Invoke SPIs
          ScopeSPIManager.getInstance ().onSessionScopeEnd (aSessionScope);

          // Destroy the scope
          aSessionScope.destroyScope ();
        }
        finally
        {
          // Remove from "in destruction" list
          m_aRWLock.writeLockedBoolean ( () -> m_aSessionsInDestruction.remove (sSessionID));
        }
      }
    }
  }

  /**
   * @return <code>true</code> if at least one session is present, <code>false</code> otherwise
   */
  public boolean containsAnySession ()
  {
    return m_aRWLock.readLockedBoolean (m_aSessionScopes::isNotEmpty);
  }

  /**
   * @return The number of managed session scopes. Always &ge; 0.
   */
  @Nonnegative
  public int getSessionCount ()
  {
    return m_aRWLock.readLockedInt (m_aSessionScopes::size);
  }

  /**
   * @return A non-<code>null</code>, mutable copy of all managed session scopes.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <ISessionScope> getAllSessionScopes ()
  {
    return m_aRWLock.readLockedGet (m_aSessionScopes::copyOfValues);
  }

  private void _checkIfAnySessionsExist ()
  {
    if (containsAnySession ())
    {
      m_aRWLock.writeLocked ( () -> {
        LOGGER.error ("The following " +
                      m_aSessionScopes.size () +
                      " session scopes are left over: " +
                      m_aSessionScopes.toString ());
        m_aSessionScopes.clear ();
      });
    }
  }

  /**
   * Destroy all known session scopes. After this method it is ensured that the internal session map
   * is empty.
   */
  public void destroyAllSessions ()
  {
    // destroy all session scopes (use a copy, because we're invalidating
    // the sessions internally!)
    for (final ISessionScope aSessionScope : getAllSessionScopes ())
    {
      // Unfortunately we need a special handling here
      if (aSessionScope.selfDestruct ().isContinue ())
      {
        // Remove from map
        onScopeEnd (aSessionScope);
      }
      // Else the destruction was already started!
    }

    // Sanity check in case something went wrong
    _checkIfAnySessionsExist ();
  }

  /**
   * Remove all existing session scopes, and invoke the destruction methods on the contained
   * objects.
   */
  private void _endAllSessionScopes ()
  {
    // end all session scopes without destroying the underlying sessions (make a
    // copy, because we're invalidating the sessions!)
    for (final ISessionScope aSessionScope : getAllSessionScopes ())
    {
      // Remove from map
      onScopeEnd (aSessionScope);
    }

    // Sanity check in case something went wrong
    _checkIfAnySessionsExist ();
  }

  public final boolean isDestroyAllSessionsOnScopeEnd ()
  {
    return m_aRWLock.readLockedBoolean ( () -> m_bDestroyAllSessionsOnScopeEnd);
  }

  @Nonnull
  public final EChange setDestroyAllSessionsOnScopeEnd (final boolean bDestroyAllSessionsOnScopeEnd)
  {
    return m_aRWLock.writeLockedGet ( () -> {
      if (m_bDestroyAllSessionsOnScopeEnd == bDestroyAllSessionsOnScopeEnd)
        return EChange.UNCHANGED;
      m_bDestroyAllSessionsOnScopeEnd = bDestroyAllSessionsOnScopeEnd;
      return EChange.CHANGED;
    });
  }

  public final boolean isEndAllSessionsOnScopeEnd ()
  {
    return m_aRWLock.readLockedBoolean ( () -> m_bEndAllSessionsOnScopeEnd);
  }

  @Nonnull
  public final EChange setEndAllSessionsOnScopeEnd (final boolean bEndAllSessionsOnScopeEnd)
  {
    return m_aRWLock.writeLockedGet ( () -> {
      if (m_bEndAllSessionsOnScopeEnd == bEndAllSessionsOnScopeEnd)
        return EChange.UNCHANGED;
      m_bEndAllSessionsOnScopeEnd = bEndAllSessionsOnScopeEnd;
      return EChange.CHANGED;
    });
  }

  @Override
  protected void onDestroy (@Nonnull final IScope aScopeInDestruction)
  {
    if (isDestroyAllSessionsOnScopeEnd ())
      destroyAllSessions ();
    else
      if (isEndAllSessionsOnScopeEnd ())
        _endAllSessionScopes ();
    s_aInstance = null;
  }
}

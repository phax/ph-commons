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
package com.helger.commons.scope.mgr;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import com.helger.commons.annotation.UsedViaReflection;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.scope.IScope;
import com.helger.commons.scope.ISessionScope;
import com.helger.commons.scope.singleton.AbstractGlobalSingleton;
import com.helger.commons.scope.spi.ScopeSPIManager;
import com.helger.commons.state.EChange;
import com.helger.commons.statistics.IMutableStatisticsHandlerCounter;
import com.helger.commons.statistics.StatisticsManager;
import com.helger.commons.string.StringHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Internal manager class for session scopes.<br>
 * This class is only non-final so that the WebScopeSessionManager can be used
 * for web scopes!
 *
 * @author Philip Helger
 */
@ThreadSafe
@Singleton
public class ScopeSessionManager extends AbstractGlobalSingleton
{
  public static final boolean DEFAULT_DESTROY_ALL_SESSIONS_ON_SCOPE_END = true;
  public static final boolean DEFAULT_END_ALL_SESSIONS_ON_SCOPE_END = true;
  private static final Logger s_aLogger = LoggerFactory.getLogger (ScopeSessionManager.class);
  private static final IMutableStatisticsHandlerCounter s_aUniqueSessionCounter = StatisticsManager.getCounterHandler (ScopeSessionManager.class.getName () +
                                                                                                                       "$UNIQUE_SESSIONS");

  private static volatile ScopeSessionManager s_aInstance = null;

  /** All contained session scopes. */
  @GuardedBy ("m_aRWLock")
  private final Map <String, ISessionScope> m_aSessionScopes = new HashMap <> ();
  @GuardedBy ("m_aRWLock")
  private final Set <String> m_aSessionsInDestruction = new HashSet <> ();
  @GuardedBy ("m_aRWLock")
  private boolean m_bDestroyAllSessionsOnScopeEnd = DEFAULT_DESTROY_ALL_SESSIONS_ON_SCOPE_END;
  @GuardedBy ("m_aRWLock")
  private boolean m_bEndAllSessionsOnScopeEnd = DEFAULT_END_ALL_SESSIONS_ON_SCOPE_END;

  @Deprecated
  @UsedViaReflection
  public ScopeSessionManager ()
  {}

  @Nonnull
  public static ScopeSessionManager getInstance ()
  {
    // This special handling is needed, because this global singleton is
    // required upon shutdown of the GlobalWebScope!
    if (s_aInstance == null)
      s_aInstance = getGlobalSingleton (ScopeSessionManager.class);
    return s_aInstance;
  }

  /**
   * Get the session scope with the specified ID. If no such scope exists, no
   * further actions are taken.
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

    return m_aRWLock.readLocked ( () -> m_aSessionScopes.get (sScopeID));
  }

  /**
   * Register the passed session scope in the internal map, call the
   * {@link ISessionScope #initScope()} method and finally invoke the SPIs for
   * the new scope.
   *
   * @param aSessionScope
   *        The session scope that was just created. May not be
   *        <code>null</code>.
   */
  public void onScopeBegin (@Nonnull final ISessionScope aSessionScope)
  {
    ValueEnforcer.notNull (aSessionScope, "SessionScope");

    final String sSessionID = aSessionScope.getID ();
    m_aRWLock.writeLocked ( () -> {
      if (m_aSessionScopes.put (sSessionID, aSessionScope) != null)
        s_aLogger.error ("Overwriting session scope with ID '" + sSessionID + "'");
    });

    // Init the scope after it was registered
    aSessionScope.initScope ();

    // Invoke SPIs
    ScopeSPIManager.getInstance ().onSessionScopeBegin (aSessionScope);

    // Increment statistics counter
    s_aUniqueSessionCounter.increment ();
  }

  /**
   * Close the passed session scope gracefully. Each managed scope is guaranteed
   * to be destroyed only once. First the SPI manager is invoked, and afterwards
   * the scope is destroyed.
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

      final boolean bCanDestroyScope = m_aRWLock.writeLocked ( () -> {
        boolean bWLCanDestroyScope = false;
        // Only if we're not just in destruction of exactly this session
        if (m_aSessionsInDestruction.add (sSessionID))
        {
          // Remove from map
          final ISessionScope aRemovedScope = m_aSessionScopes.remove (sSessionID);
          if (aRemovedScope != aSessionScope)
          {
            s_aLogger.error ("Ending an unknown session with ID '" + sSessionID + "'");
            s_aLogger.error ("  Scope to be removed: " + aSessionScope);
            s_aLogger.error ("  Removed scope:       " + aRemovedScope);
          }
          bWLCanDestroyScope = true;
        }
        else
          s_aLogger.info ("Already destructing session '" + sSessionID + "'");
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
          m_aRWLock.writeLocked ( () -> {
            m_aSessionsInDestruction.remove (sSessionID);
          });
        }
      }
    }
  }

  /**
   * @return <code>true</code> if at least one session is present,
   *         <code>false</code> otherwise
   */
  public boolean containsAnySession ()
  {
    return m_aRWLock.readLocked ( () -> !m_aSessionScopes.isEmpty ());
  }

  /**
   * @return The number of managed session scopes. Always &ge; 0.
   */
  @Nonnegative
  public int getSessionCount ()
  {
    return m_aRWLock.readLocked ( () -> m_aSessionScopes.size ());
  }

  /**
   * @return A non-<code>null</code>, mutable copy of all managed session
   *         scopes.
   */
  @Nonnull
  @ReturnsMutableCopy
  public Collection <? extends ISessionScope> getAllSessionScopes ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.newList (m_aSessionScopes.values ()));
  }

  private void _checkIfAnySessionsExist ()
  {
    if (containsAnySession ())
    {
      m_aRWLock.writeLocked ( () -> {
        s_aLogger.error ("The following " +
                         m_aSessionScopes.size () +
                         " session scopes are left over: " +
                         m_aSessionScopes.toString ());
        m_aSessionScopes.clear ();
      });
    }
  }

  /**
   * Destroy all known session scopes. After this method it is ensured that the
   * internal session map is empty.
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
   * Remove all existing session scopes, and invoke the destruction methods on
   * the contained objects.
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

  public boolean isDestroyAllSessionsOnScopeEnd ()
  {
    return m_aRWLock.readLocked ( () -> m_bDestroyAllSessionsOnScopeEnd);
  }

  @Nonnull
  public EChange setDestroyAllSessionsOnScopeEnd (final boolean bDestroyAllSessionsOnScopeEnd)
  {
    return m_aRWLock.writeLocked ( () -> {
      if (m_bDestroyAllSessionsOnScopeEnd == bDestroyAllSessionsOnScopeEnd)
        return EChange.UNCHANGED;
      m_bDestroyAllSessionsOnScopeEnd = bDestroyAllSessionsOnScopeEnd;
      return EChange.CHANGED;
    });
  }

  public boolean isEndAllSessionsOnScopeEnd ()
  {
    return m_aRWLock.readLocked ( () -> m_bEndAllSessionsOnScopeEnd);
  }

  @Nonnull
  public EChange setEndAllSessionsOnScopeEnd (final boolean bEndAllSessionsOnScopeEnd)
  {
    return m_aRWLock.writeLocked ( () -> {
      if (m_bEndAllSessionsOnScopeEnd == bEndAllSessionsOnScopeEnd)
        return EChange.UNCHANGED;
      m_bEndAllSessionsOnScopeEnd = bEndAllSessionsOnScopeEnd;
      return EChange.CHANGED;
    });
  }

  @SuppressFBWarnings ("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
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

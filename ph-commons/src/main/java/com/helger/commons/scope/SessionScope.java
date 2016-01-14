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
package com.helger.commons.scope;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.scope.mgr.MetaScopeFactory;
import com.helger.commons.scope.spi.ScopeSPIManager;
import com.helger.commons.state.EContinue;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation of the {@link ISessionScope} interface
 *
 * @author Philip Helger
 */
@ThreadSafe
public class SessionScope extends AbstractMapBasedScope implements ISessionScope
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SessionScope.class);

  /** The contained session application scopes */
  private final Map <String, ISessionApplicationScope> m_aSessionAppScopes = new HashMap <> ();

  public SessionScope (@Nonnull @Nonempty final String sScopeID)
  {
    super (sScopeID);

    // Sessions are always displayed to see what's happening
    if (ScopeHelper.debugSessionScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Created session scope '" +
                      getID () +
                      "' of class " +
                      ClassHelper.getClassLocalName (this),
                      ScopeHelper.getDebugStackTrace ());
  }

  public void initScope ()
  {}

  @Override
  protected final void destroyOwnedScopes ()
  {
    m_aRWLock.writeLocked ( () -> {
      for (final ISessionApplicationScope aSessionAppScope : m_aSessionAppScopes.values ())
      {
        // Invoke SPIs
        ScopeSPIManager.getInstance ().onSessionApplicationScopeEnd (aSessionAppScope);

        // destroy the scope
        aSessionAppScope.destroyScope ();
      }
      m_aSessionAppScopes.clear ();
    });
  }

  @Override
  protected void preDestroy ()
  {
    if (ScopeHelper.debugSessionScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Destroying session scope '" +
                      getID () +
                      "' of class " +
                      ClassHelper.getClassLocalName (this),
                      ScopeHelper.getDebugStackTrace ());
  }

  @Override
  protected void postDestroy ()
  {
    if (ScopeHelper.debugSessionScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Destroyed session scope '" +
                      getID () +
                      "' of class " +
                      ClassHelper.getClassLocalName (this),
                      ScopeHelper.getDebugStackTrace ());
  }

  @Nonnull
  public EContinue selfDestruct ()
  {
    // Nothing to do here!

    // Note: don't call ScopeSessionManager.onScopeEnd here. This must be done
    // manually if this method returns "CONTINUE".
    return EContinue.CONTINUE;
  }

  @Nonnull
  @Nonempty
  private String _getApplicationScopeIDPrefix ()
  {
    return getID () + '.';
  }

  @Nonnull
  @Nonempty
  public String createApplicationScopeID (@Nonnull @Nonempty final String sApplicationID)
  {
    ValueEnforcer.notEmpty (sApplicationID, "ApplicationID");

    // To make the ID unique, prepend the application ID with this scope ID
    return _getApplicationScopeIDPrefix () + sApplicationID;
  }

  @Nullable
  public String getApplicationIDFromApplicationScopeID (@Nullable final String sApplicationScopeID)
  {
    if (StringHelper.hasNoText (sApplicationScopeID))
      return null;

    final String sPrefix = _getApplicationScopeIDPrefix ();
    if (sApplicationScopeID.startsWith (sPrefix))
      return sApplicationScopeID.substring (sPrefix.length ());

    // Not a valid application scope ID
    return null;
  }

  @Nonnull
  protected ISessionApplicationScope createSessionApplicationScope (@Nonnull @Nonempty final String sApplicationID)
  {
    return MetaScopeFactory.getScopeFactory ().createSessionApplicationScope (sApplicationID);
  }

  @Nullable
  public ISessionApplicationScope getSessionApplicationScope (@Nonnull @Nonempty final String sApplicationID,
                                                              final boolean bCreateIfNotExisting)
  {
    ValueEnforcer.notEmpty (sApplicationID, "ApplicationID");

    final String sAppScopeID = createApplicationScopeID (sApplicationID);

    // Try with read-lock only
    ISessionApplicationScope aSessionAppScope = m_aRWLock.readLocked ( () -> m_aSessionAppScopes.get (sAppScopeID));

    if (aSessionAppScope == null && bCreateIfNotExisting)
    {
      aSessionAppScope = m_aRWLock.writeLocked ( () -> {
        // Check again - now in write lock
        ISessionApplicationScope aWLSessionAppScope = m_aSessionAppScopes.get (sAppScopeID);
        if (aWLSessionAppScope == null)
        {
          // Definitively not present
          aWLSessionAppScope = createSessionApplicationScope (sAppScopeID);
          m_aSessionAppScopes.put (sAppScopeID, aWLSessionAppScope);
          aWLSessionAppScope.initScope ();

          // Invoke SPIs
          ScopeSPIManager.getInstance ().onSessionApplicationScopeBegin (aWLSessionAppScope);
        }
        return aWLSessionAppScope;
      });
    }
    return aSessionAppScope;
  }

  public void restoreSessionApplicationScope (@Nonnull @Nonempty final String sScopeID,
                                              @Nonnull final ISessionApplicationScope aScope)
  {
    ValueEnforcer.notEmpty (sScopeID, "ScopeID");
    ValueEnforcer.notNull (aScope, "Scope");

    m_aRWLock.writeLocked ( () -> {
      if (m_aSessionAppScopes.containsKey (sScopeID))
        throw new IllegalArgumentException ("A session application scope with the ID '" +
                                            sScopeID +
                                            "' is already contained!");
      m_aSessionAppScopes.put (sScopeID, aScope);
    });
  }

  @Nonnull
  @ReturnsMutableCopy
  public Map <String, ISessionApplicationScope> getAllSessionApplicationScopes ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.newMap (m_aSessionAppScopes));
  }

  @Nonnegative
  public int getSessionApplicationScopeCount ()
  {
    return m_aRWLock.readLocked ( () -> m_aSessionAppScopes.size ());
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("sessionAppScopes", m_aSessionAppScopes)
                            .toString ();
  }
}

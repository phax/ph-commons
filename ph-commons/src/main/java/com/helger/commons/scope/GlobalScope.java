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
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.scope.mgr.MetaScopeFactory;
import com.helger.commons.scope.spi.ScopeSPIManager;
import com.helger.commons.string.ToStringGenerator;

/**
 * Base implementation of the {@link IGlobalScope} interface.<br>
 * Note: for synchronization issues, this class stores the attributes in a
 * separate map.
 *
 * @author Philip Helger
 */
@ThreadSafe
public class GlobalScope extends AbstractMapBasedScope implements IGlobalScope
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (GlobalScope.class);

  /** Contained application scopes */
  private final Map <String, IApplicationScope> m_aAppScopes = new HashMap <> ();

  public GlobalScope (@Nonnull @Nonempty final String sScopeID)
  {
    super (sScopeID);

    if (ScopeHelper.debugGlobalScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Created global scope '" +
                      getID () +
                      "' of class " +
                      ClassHelper.getClassLocalName (this),
                      ScopeHelper.getDebugStackTrace ());
  }

  public void initScope ()
  {}

  @Override
  protected void destroyOwnedScopes ()
  {
    m_aRWLock.writeLocked ( () -> {
      for (final IApplicationScope aAppScope : m_aAppScopes.values ())
      {
        // Invoke SPIs
        ScopeSPIManager.getInstance ().onApplicationScopeEnd (aAppScope);

        // Destroy the scope
        aAppScope.destroyScope ();
      }
      m_aAppScopes.clear ();
    });
  }

  @Override
  protected void preDestroy ()
  {
    if (ScopeHelper.debugGlobalScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Destroying global scope '" +
                      getID () +
                      "' of class " +
                      ClassHelper.getClassLocalName (this),
                      ScopeHelper.getDebugStackTrace ());
  }

  @Override
  protected void postDestroy ()
  {
    if (ScopeHelper.debugGlobalScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Destroyed global scope '" +
                      getID () +
                      "' of class " +
                      ClassHelper.getClassLocalName (this),
                      ScopeHelper.getDebugStackTrace ());
  }

  /**
   * This method creates a new application scope. Override in WebScopeManager to
   * create an IApplicationWebScope!
   *
   * @param sApplicationID
   *        The application ID to use
   * @return Never <code>null</code>.
   */
  @Nonnull
  @OverrideOnDemand
  protected IApplicationScope createApplicationScope (@Nonnull @Nonempty final String sApplicationID)
  {
    return MetaScopeFactory.getScopeFactory ().createApplicationScope (sApplicationID);
  }

  @Nullable
  public IApplicationScope getApplicationScope (@Nonnull @Nonempty final String sApplicationID,
                                                final boolean bCreateIfNotExisting)
  {
    ValueEnforcer.notEmpty (sApplicationID, "ApplicationID");

    // Read-lock only
    IApplicationScope aAppScope = m_aRWLock.readLocked ( () -> m_aAppScopes.get (sApplicationID));
    if (aAppScope == null && bCreateIfNotExisting)
    {
      // now write lock
      aAppScope = m_aRWLock.writeLocked ( () -> {
        // Make sure it was not added in the mean time
        IApplicationScope aWLAppScope = m_aAppScopes.get (sApplicationID);
        if (aWLAppScope == null)
        {
          aWLAppScope = createApplicationScope (sApplicationID);
          m_aAppScopes.put (sApplicationID, aWLAppScope);
          aWLAppScope.initScope ();

          // Invoke SPIs
          ScopeSPIManager.getInstance ().onApplicationScopeBegin (aWLAppScope);
        }
        return aWLAppScope;
      });
    }
    return aAppScope;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Map <String, IApplicationScope> getAllApplicationScopes ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.newMap (m_aAppScopes));
  }

  @Nonnegative
  public int getApplicationScopeCount ()
  {
    return m_aRWLock.readLocked ( () -> m_aAppScopes.size ());
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final GlobalScope rhs = (GlobalScope) o;
    return m_aAppScopes.equals (rhs.m_aAppScopes);
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_aAppScopes).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("appScopes", m_aAppScopes).toString ();
  }
}

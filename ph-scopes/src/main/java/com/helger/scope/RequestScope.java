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
package com.helger.scope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.string.ToStringGenerator;

import jakarta.annotation.Nonnull;

/**
 * Default implementation for non-web request scopes.
 *
 * @author Philip Helger
 */
public class RequestScope extends AbstractScope implements IRequestScope
{
  private static final Logger LOGGER = LoggerFactory.getLogger (RequestScope.class);

  private final String m_sSessionID;

  public RequestScope (@Nonnull @Nonempty final String sScopeID, @Nonnull @Nonempty final String sSessionID)
  {
    super (sScopeID);
    m_sSessionID = ValueEnforcer.notEmpty (sSessionID, "SessionID");

    // done initialization
    if (ScopeHelper.isDebugRequestScopeLifeCycle ())
      LOGGER.info ("Created request scope '" + sScopeID + "'", ScopeHelper.getDebugException ());
  }

  @Nonnull
  @Nonempty
  public final String getSessionID (final boolean bCreateIfNotExisting)
  {
    return m_sSessionID;
  }

  public void initScope ()
  {}

  @Override
  protected void preDestroy ()
  {
    if (ScopeHelper.isDebugRequestScopeLifeCycle ())
      LOGGER.info ("Destroying request scope '" + getID () + "' of class " + ClassHelper.getClassLocalName (this),
                   ScopeHelper.getDebugException ());
  }

  @Override
  protected void postDestroy ()
  {
    if (ScopeHelper.isDebugRequestScopeLifeCycle ())
      LOGGER.info ("Destroyed request scope '" + getID () + "' of class " + ClassHelper.getClassLocalName (this),
                   ScopeHelper.getDebugException ());
  }

  @Override
  public boolean equals (final Object o)
  {
    return super.equals (o);
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("sessionID", m_sSessionID).getToString ();
  }
}

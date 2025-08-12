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
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.state.EContinue;

import jakarta.annotation.Nonnull;

/**
 * Default implementation of the {@link ISessionScope} interface
 *
 * @author Philip Helger
 */
@ThreadSafe
public class SessionScope extends AbstractScope implements ISessionScope
{
  private static final Logger LOGGER = LoggerFactory.getLogger (SessionScope.class);

  public SessionScope (@Nonnull @Nonempty final String sScopeID)
  {
    super (sScopeID);

    // Sessions are always displayed to see what's happening
    if (ScopeHelper.isDebugSessionScopeLifeCycle ())
      LOGGER.info ("Created session scope '" + sScopeID + "'", ScopeHelper.getDebugException ());
  }

  public void initScope ()
  {}

  @Override
  protected void preDestroy ()
  {
    if (ScopeHelper.isDebugSessionScopeLifeCycle ())
      LOGGER.info ("Destroying session scope '" + getID () + "' of class " + ClassHelper.getClassLocalName (this),
                   ScopeHelper.getDebugException ());
  }

  @Override
  protected void postDestroy ()
  {
    if (ScopeHelper.isDebugSessionScopeLifeCycle ())
      LOGGER.info ("Destroyed session scope '" + getID () + "' of class " + ClassHelper.getClassLocalName (this),
                   ScopeHelper.getDebugException ());
  }

  @Nonnull
  public EContinue selfDestruct ()
  {
    // Nothing to do here!

    // Note: don't call ScopeSessionManager.onScopeEnd here. This must be done
    // manually if this method returns "CONTINUE".
    return EContinue.CONTINUE;
  }
}

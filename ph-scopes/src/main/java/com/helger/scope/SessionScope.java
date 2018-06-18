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
package com.helger.scope;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.state.EContinue;

/**
 * Default implementation of the {@link ISessionScope} interface
 *
 * @author Philip Helger
 */
@ThreadSafe
public class SessionScope extends AbstractScope implements ISessionScope
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SessionScope.class);

  public SessionScope (@Nonnull @Nonempty final String sScopeID)
  {
    super (sScopeID);

    // Sessions are always displayed to see what's happening
    if (ScopeHelper.debugSessionScopeLifeCycle (s_aLogger))
      if (s_aLogger.isInfoEnabled ())
        s_aLogger.info ("Created session scope '" + sScopeID + "'", ScopeHelper.getDebugStackTrace ());
  }

  public void initScope ()
  {}

  @Override
  protected void preDestroy ()
  {
    if (ScopeHelper.debugSessionScopeLifeCycle (s_aLogger))
      if (s_aLogger.isInfoEnabled ())
        s_aLogger.info ("Destroying session scope '" + getID () + "' of class " + ClassHelper.getClassLocalName (this),
                        ScopeHelper.getDebugStackTrace ());
  }

  @Override
  protected void postDestroy ()
  {
    if (ScopeHelper.debugSessionScopeLifeCycle (s_aLogger))
      if (s_aLogger.isInfoEnabled ())
        s_aLogger.info ("Destroyed session scope '" + getID () + "' of class " + ClassHelper.getClassLocalName (this),
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
}

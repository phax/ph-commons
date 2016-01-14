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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.lang.ClassHelper;

/**
 * Represents a single "session application scope". This is a scope that is
 * specific to the selected application within the global scope and to the
 * current user session.
 *
 * @author Philip Helger
 */
@ThreadSafe
public class SessionApplicationScope extends AbstractMapBasedScope implements ISessionApplicationScope
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SessionApplicationScope.class);

  public SessionApplicationScope (@Nonnull @Nonempty final String sScopeID)
  {
    super (sScopeID);

    if (ScopeHelper.debugSessionApplicationScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Created session application scope '" +
                      sScopeID +
                      "' of class " +
                      ClassHelper.getClassLocalName (this),
                      ScopeHelper.getDebugStackTrace ());
  }

  public void initScope ()
  {}

  @Override
  protected void preDestroy ()
  {
    if (ScopeHelper.debugSessionApplicationScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Destroying session application scope '" +
                      getID () +
                      "' of class " +
                      ClassHelper.getClassLocalName (this),
                      ScopeHelper.getDebugStackTrace ());
  }

  @Override
  protected void postDestroy ()
  {
    if (ScopeHelper.debugSessionApplicationScopeLifeCycle (s_aLogger))
      s_aLogger.info ("Destroyed session application scope '" +
                      getID () +
                      "' of class " +
                      ClassHelper.getClassLocalName (this),
                      ScopeHelper.getDebugStackTrace ());
  }
}

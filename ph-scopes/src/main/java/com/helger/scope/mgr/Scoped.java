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

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.scope.IRequestScope;

/**
 * Auto closable wrapper around
 * {@link ScopeManager#onRequestBegin( String, String)} and
 * {@link ScopeManager#onRequestEnd()}
 *
 * @author Philip Helger
 * @since 9.0.0
 */
public class Scoped implements AutoCloseable
{
  private IRequestScope m_aRequestScope;

  public Scoped ()
  {
    this ("scope-id", "session-id");
  }

  public Scoped (@Nonnull @Nonempty final String sScopeID, @Nonnull @Nonempty final String sSessionID)
  {
    m_aRequestScope = ScopeManager.onRequestBegin (sScopeID, sSessionID);
  }

  @Nonnull
  public IRequestScope getRequestScope ()
  {
    if (m_aRequestScope == null)
      throw new IllegalStateException ("No request scope present!");
    return m_aRequestScope;
  }

  public void close ()
  {
    m_aRequestScope = null;
    ScopeManager.onRequestEnd ();
  }
}

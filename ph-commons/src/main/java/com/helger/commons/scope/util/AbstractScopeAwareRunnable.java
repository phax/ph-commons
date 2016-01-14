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
package com.helger.commons.scope.util;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.callback.INonThrowingRunnable;
import com.helger.commons.scope.mgr.ScopeManager;
import com.helger.commons.scope.mock.ScopeAwareTestSetup;
import com.helger.commons.string.ToStringGenerator;

/**
 * Abstract implementation of {@link Runnable} that handles WebScopes correctly.
 *
 * @author Philip Helger
 */
public abstract class AbstractScopeAwareRunnable implements INonThrowingRunnable
{
  private final String m_sApplicationID;
  private final String m_sRequestID;
  private final String m_sSessionID;

  public AbstractScopeAwareRunnable ()
  {
    this (ScopeManager.getApplicationScope ().getID ());
  }

  public AbstractScopeAwareRunnable (@Nonnull @Nonempty final String sApplicationID)
  {
    this (sApplicationID, ScopeAwareTestSetup.MOCK_REQUEST_SCOPE_ID, ScopeAwareTestSetup.MOCK_SESSION_SCOPE_ID);
  }

  public AbstractScopeAwareRunnable (@Nonnull @Nonempty final String sApplicationID,
                                     @Nonnull @Nonempty final String sRequestID,
                                     @Nonnull @Nonempty final String sSessionID)
  {
    m_sApplicationID = ValueEnforcer.notEmpty (sApplicationID, "ApplicationID");
    m_sRequestID = ValueEnforcer.notEmpty (sRequestID, "RequestID");
    m_sSessionID = ValueEnforcer.notEmpty (sSessionID, "SessionID");
  }

  @Nonnull
  @Nonempty
  public String getApplicationID ()
  {
    return m_sApplicationID;
  }

  @Nonnull
  @Nonempty
  public String getRequestID ()
  {
    return m_sRequestID;
  }

  @Nonnull
  @Nonempty
  public String getSessionID ()
  {
    return m_sSessionID;
  }

  /**
   * Implement your code in here.
   */
  protected abstract void scopedRun ();

  public final void run ()
  {
    ScopeManager.onRequestBegin (m_sApplicationID, m_sRequestID, m_sSessionID);
    try
    {
      scopedRun ();
    }
    finally
    {
      ScopeManager.onRequestEnd ();
    }
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("applicationID", m_sApplicationID)
                                       .append ("requestID", m_sRequestID)
                                       .append ("sessionID", m_sSessionID)
                                       .toString ();
  }
}

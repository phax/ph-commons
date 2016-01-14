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

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.callback.INonThrowingCallable;
import com.helger.commons.scope.mgr.ScopeManager;
import com.helger.commons.scope.mock.ScopeAwareTestSetup;
import com.helger.commons.string.ToStringGenerator;

/**
 * Abstract implementation of {@link Callable} that handles WebScopes correctly.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        The return type of the function.
 */
public abstract class AbstractScopeAwareCallable <DATATYPE> implements INonThrowingCallable <DATATYPE>
{
  private final String m_sApplicationID;
  private final String m_sRequestID;
  private final String m_sSessionID;

  public AbstractScopeAwareCallable ()
  {
    this (ScopeManager.getApplicationScope ().getID ());
  }

  public AbstractScopeAwareCallable (@Nonnull @Nonempty final String sApplicationID)
  {
    this (sApplicationID, ScopeAwareTestSetup.MOCK_REQUEST_SCOPE_ID, ScopeAwareTestSetup.MOCK_SESSION_SCOPE_ID);
  }

  public AbstractScopeAwareCallable (@Nonnull @Nonempty final String sApplicationID,
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
   * Implement your code in here
   *
   * @return The return value of the {@link #call()} method.
   */
  @Nullable
  protected abstract DATATYPE scopedRun ();

  @Nullable
  public final DATATYPE call ()
  {
    ScopeManager.onRequestBegin (m_sApplicationID, m_sRequestID, m_sSessionID);
    try
    {
      final DATATYPE ret = scopedRun ();
      return ret;
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

/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.string.ToStringGenerator;

/**
 * Abstract implementation for scope aware functional implementations
 *
 * @author Philip Helger
 */
@Immutable
public abstract class AbstractScopeAwareAction implements Serializable
{
  protected final String m_sApplicationID;
  protected final String m_sRequestID;
  protected final String m_sSessionID;

  public AbstractScopeAwareAction (@Nonnull @Nonempty final String sApplicationID,
                                   @Nonnull @Nonempty final String sRequestID,
                                   @Nonnull @Nonempty final String sSessionID)
  {
    m_sApplicationID = ValueEnforcer.notEmpty (sApplicationID, "ApplicationID");
    m_sRequestID = ValueEnforcer.notEmpty (sRequestID, "RequestID");
    m_sSessionID = ValueEnforcer.notEmpty (sSessionID, "SessionID");
  }

  @Nonnull
  @Nonempty
  public final String getApplicationID ()
  {
    return m_sApplicationID;
  }

  @Nonnull
  @Nonempty
  public final String getRequestID ()
  {
    return m_sRequestID;
  }

  @Nonnull
  @Nonempty
  public final String getSessionID ()
  {
    return m_sSessionID;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ApplicationID", m_sApplicationID)
                                       .append ("RequestID", m_sRequestID)
                                       .append ("SessionID", m_sSessionID)
                                       .getToString ();
  }
}

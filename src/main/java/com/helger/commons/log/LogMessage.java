/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.log;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.error.EErrorLevel;
import com.helger.commons.error.IHasErrorLevel;
import com.helger.commons.error.ISeverityComparable;
import com.helger.commons.state.IErrorIndicator;
import com.helger.commons.state.ISuccessIndicator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Represents a single log message.
 * 
 * @author Philip Helger
 */
@Immutable
public class LogMessage implements IHasErrorLevel, ISuccessIndicator, IErrorIndicator, ISeverityComparable <LogMessage>, Serializable
{
  private final Date m_aIssueDT;
  private final EErrorLevel m_eErrorLevel;
  private final Serializable m_aMsg;
  private final Throwable m_aThrowable;

  public LogMessage (@Nonnull final EErrorLevel eLevel,
                     @Nonnull final Serializable aMsg,
                     @Nullable final Throwable aThrowable)
  {
    m_aIssueDT = new Date ();
    m_eErrorLevel = ValueEnforcer.notNull (eLevel, "ErrorLevel");
    m_aMsg = ValueEnforcer.notNull (aMsg, "Message");
    m_aThrowable = aThrowable;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Date getIssueDateTime ()
  {
    // Return a copy
    return (Date) m_aIssueDT.clone ();
  }

  @Nonnull
  public EErrorLevel getErrorLevel ()
  {
    return m_eErrorLevel;
  }

  @Nonnull
  public Serializable getMessage ()
  {
    return m_aMsg;
  }

  @Nullable
  public Throwable getThrowable ()
  {
    return m_aThrowable;
  }

  public boolean isSuccess ()
  {
    return m_eErrorLevel.isSuccess ();
  }

  public boolean isFailure ()
  {
    return m_eErrorLevel.isFailure ();
  }

  public boolean isError ()
  {
    return m_eErrorLevel.isError ();
  }

  public boolean isNoError ()
  {
    return m_eErrorLevel.isNoError ();
  }

  public boolean isEqualSevereThan (@Nonnull final LogMessage aOther)
  {
    return m_eErrorLevel.isEqualSevereThan (aOther.m_eErrorLevel);
  }

  public boolean isLessSevereThan (@Nonnull final LogMessage aOther)
  {
    return m_eErrorLevel.isLessSevereThan (aOther.m_eErrorLevel);
  }

  public boolean isLessOrEqualSevereThan (@Nonnull final LogMessage aOther)
  {
    return m_eErrorLevel.isLessOrEqualSevereThan (aOther.m_eErrorLevel);
  }

  public boolean isMoreSevereThan (@Nonnull final LogMessage aOther)
  {
    return m_eErrorLevel.isMoreSevereThan (aOther.m_eErrorLevel);
  }

  public boolean isMoreOrEqualSevereThan (@Nonnull final LogMessage aOther)
  {
    return m_eErrorLevel.isMoreOrEqualSevereThan (aOther.m_eErrorLevel);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("issueDT", m_aIssueDT)
                                       .append ("errorLevel", m_eErrorLevel)
                                       .append ("msg", m_aMsg)
                                       .appendIfNotNull ("throwable", m_aThrowable)
                                       .toString ();
  }
}

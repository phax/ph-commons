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
package com.helger.commons.log;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.error.level.IErrorLevel;
import com.helger.commons.error.level.IHasErrorLevelComparable;
import com.helger.commons.string.ToStringGenerator;

/**
 * Represents a single log message.
 *
 * @author Philip Helger
 */
@Immutable
public class LogMessage implements IHasErrorLevelComparable <LogMessage>
{
  private final LocalDateTime m_aIssueDT;
  private final IErrorLevel m_aErrorLevel;
  private final Serializable m_aMsg;
  private final Throwable m_aThrowable;

  public LogMessage (@Nonnull final IErrorLevel aErrorLevel,
                     @Nonnull final Serializable aMsg,
                     @Nullable final Throwable aThrowable)
  {
    this (PDTFactory.getCurrentLocalDateTime (), aErrorLevel, aMsg, aThrowable);
  }

  public LogMessage (@Nonnull final LocalDateTime aIssueDT,
                     @Nonnull final IErrorLevel aErrorLevel,
                     @Nonnull final Serializable aMsg,
                     @Nullable final Throwable aThrowable)
  {
    m_aIssueDT = ValueEnforcer.notNull (aIssueDT, "IssueDT");
    m_aErrorLevel = ValueEnforcer.notNull (aErrorLevel, "ErrorLevel");
    m_aMsg = ValueEnforcer.notNull (aMsg, "Message");
    m_aThrowable = aThrowable;
  }

  @Nonnull
  public LocalDateTime getIssueDateTime ()
  {
    return m_aIssueDT;
  }

  @Nonnull
  public IErrorLevel getErrorLevel ()
  {
    return m_aErrorLevel;
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

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("issueDT", m_aIssueDT)
                                       .append ("errorLevel", m_aErrorLevel)
                                       .append ("msg", m_aMsg)
                                       .appendIfNotNull ("throwable", m_aThrowable)
                                       .getToString ();
  }
}

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
package com.helger.commons.callback.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.base.enforcer.ValueEnforcer;
import com.helger.base.mock.exception.IMockException;
import com.helger.base.string.ToStringGenerator;
import com.helger.commons.error.level.EErrorLevel;
import com.helger.commons.error.level.IErrorLevel;
import com.helger.commons.error.level.IHasErrorLevel;
import com.helger.commons.log.LogHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A specific implementation of the {@link IExceptionCallback} interface, that
 * logs all exceptions to a standard logger.
 *
 * @author Philip Helger
 */
@ThreadSafe
public class LoggingExceptionCallback implements IExceptionCallback <Throwable>, IHasErrorLevel
{
  private static final Logger LOGGER = LoggerFactory.getLogger (LoggingExceptionCallback.class);

  private IErrorLevel m_aErrorLevel = EErrorLevel.ERROR;

  public LoggingExceptionCallback ()
  {}

  /**
   * @return The configured error level. Never <code>null</code>.
   */
  @Nonnull
  public IErrorLevel getErrorLevel ()
  {
    return m_aErrorLevel;
  }

  /**
   * Set the error level to be used.
   *
   * @param aErrorLevel
   *        Error level to use. May not be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  public final LoggingExceptionCallback setErrorLevel (@Nonnull final IErrorLevel aErrorLevel)
  {
    m_aErrorLevel = ValueEnforcer.notNull (aErrorLevel, "ErrorLevel");
    return this;
  }

  /**
   * Get the text to be logged for a certain exception
   *
   * @param t
   *        The exception to be logged. May theoretically be <code>null</code>.
   * @return The text to be logged. May neither be <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  @OverrideOnDemand
  protected String getLogMessage (@Nullable final Throwable t)
  {
    if (t == null)
      return "An error occurred";
    return "An exception was thrown";
  }

  /**
   * Check if the passed exception should be part of the log entry.
   *
   * @param t
   *        The exception to check. May theoretically be <code>null</code>.
   * @return <code>true</code> to log the exception, <code>false</code> to not
   *         log it
   */
  @OverrideOnDemand
  protected boolean isLogException (@Nullable final Throwable t)
  {
    return !(t instanceof IMockException);
  }

  public void onException (@Nullable final Throwable t)
  {
    final String sLogMessage = getLogMessage (t);
    final boolean bLogException = isLogException (t);
    LogHelper.log (LOGGER, m_aErrorLevel, sLogMessage, bLogException ? t : null);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).getToString ();
  }
}

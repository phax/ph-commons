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
package com.helger.commons.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a wrapper for a exception that can log the passed exception when it
 * is thrown.
 *
 * @author Philip Helger
 */
public class LoggedException extends Exception implements ILoggedException
{
  /**
   * The default setting, whether runtime exceptions are logged, if no parameter
   * is specified.
   */
  public static final boolean DEFAULT_DO_LOG = true;

  public static final String MSG_TEXT = "Exception created.";

  private static final Logger s_aLogger = LoggerFactory.getLogger (Logger.ROOT_LOGGER_NAME);

  public LoggedException ()
  {
    this (DEFAULT_DO_LOG);
  }

  public LoggedException (final boolean bLog)
  {
    super ();
    if (bLog)
      s_aLogger.error (MSG_TEXT, this);
  }

  public LoggedException (@Nullable final String sMsg)
  {
    this (DEFAULT_DO_LOG, sMsg);
  }

  public LoggedException (final boolean bLog, @Nullable final String sMsg)
  {
    super (sMsg);
    if (bLog)
      s_aLogger.error (MSG_TEXT, this);
  }

  public LoggedException (@Nullable final Throwable t)
  {
    this (DEFAULT_DO_LOG, t);
  }

  public LoggedException (final boolean bLog, @Nullable final Throwable t)
  {
    super (t);
    if (bLog)
      s_aLogger.error (MSG_TEXT, this);
  }

  public LoggedException (@Nullable final String sMsg, @Nullable final Throwable t)
  {
    this (DEFAULT_DO_LOG, sMsg, t);
  }

  public LoggedException (final boolean bLog, @Nullable final String sMsg, @Nullable final Throwable t)
  {
    super (sMsg, t);
    if (bLog)
      s_aLogger.error (MSG_TEXT, this);
  }

  @Nonnull
  public static Exception newException (@Nullable final Throwable t)
  {
    if (t instanceof LoggedException)
      return (LoggedException) t;
    if (t instanceof ILoggedException)
      return new Exception (t);
    return new LoggedException (t);
  }

  @Nonnull
  public static Exception newException (@Nullable final String sMsg, @Nullable final Throwable t)
  {
    if (t instanceof LoggedException)
      return (LoggedException) t;
    if (t instanceof ILoggedException)
      return new Exception (sMsg, t);
    return new LoggedException (sMsg, t);
  }
}

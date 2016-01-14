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
package com.helger.commons.log;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.error.EErrorLevel;
import com.helger.commons.error.IErrorLevel;
import com.helger.commons.error.IHasErrorLevel;

/**
 * Some utility functions to help integrating the {@link IErrorLevel} enum in
 * this package with SLF4J logger.
 *
 * @author Philip Helger
 */
@Immutable
public final class LogHelper
{
  @PresentForCodeCoverage
  private static final LogHelper s_aInstance = new LogHelper ();

  private LogHelper ()
  {}

  /**
   * Check if logging is enabled for the passed class based on the error level
   * provider by the passed object
   *
   * @param aLoggingClass
   *        The class to determine the logger from. May not be <code>null</code>
   *        .
   * @param aErrorLevelProvider
   *        The error level provider. May not be <code>null</code>.
   * @return <code>true</code> if the respective log level is allowed,
   *         <code>false</code> if not
   */
  public static boolean isEnabled (@Nonnull final Class <?> aLoggingClass,
                                   @Nonnull final IHasErrorLevel aErrorLevelProvider)
  {
    return isEnabled (LoggerFactory.getLogger (aLoggingClass), aErrorLevelProvider.getErrorLevel ());
  }

  /**
   * Check if logging is enabled for the passed logger based on the error level
   * provider by the passed object
   *
   * @param aLogger
   *        The logger. May not be <code>null</code>.
   * @param aErrorLevelProvider
   *        The error level provider. May not be <code>null</code>.
   * @return <code>true</code> if the respective log level is allowed,
   *         <code>false</code> if not
   */
  public static boolean isEnabled (@Nonnull final Logger aLogger, @Nonnull final IHasErrorLevel aErrorLevelProvider)
  {
    return isEnabled (aLogger, aErrorLevelProvider.getErrorLevel ());
  }

  /**
   * Check if logging is enabled for the passed class based on the error level
   * provided
   *
   * @param aLoggingClass
   *        The class to determine the logger from. May not be <code>null</code>
   *        .
   * @param eErrorLevel
   *        The error level. May not be <code>null</code>.
   * @return <code>true</code> if the respective log level is allowed,
   *         <code>false</code> if not
   */
  public static boolean isEnabled (@Nonnull final Class <?> aLoggingClass, @Nonnull final IErrorLevel eErrorLevel)
  {
    return isEnabled (LoggerFactory.getLogger (aLoggingClass), eErrorLevel);
  }

  /**
   * Check if logging is enabled for the passed logger based on the error level
   * provided
   *
   * @param aLogger
   *        The logger. May not be <code>null</code>.
   * @param eErrorLevel
   *        The error level. May not be <code>null</code>.
   * @return <code>true</code> if the respective log level is allowed,
   *         <code>false</code> if not
   */
  public static boolean isEnabled (@Nonnull final Logger aLogger, @Nonnull final IErrorLevel eErrorLevel)
  {
    if (eErrorLevel.isMoreOrEqualSevereThan (EErrorLevel.ERROR))
      return aLogger.isErrorEnabled ();
    if (eErrorLevel.isMoreOrEqualSevereThan (EErrorLevel.WARN))
      return aLogger.isWarnEnabled ();
    if (eErrorLevel.isMoreOrEqualSevereThan (EErrorLevel.INFO))
      return aLogger.isInfoEnabled ();
    return aLogger.isDebugEnabled ();
  }

  public static void log (@Nonnull final Class <?> aLoggingClass,
                          @Nonnull final IHasErrorLevel aErrorLevelProvider,
                          @Nonnull final String sMsg)
  {
    log (aLoggingClass, aErrorLevelProvider.getErrorLevel (), sMsg, null);
  }

  public static void log (@Nonnull final Class <?> aLoggingClass,
                          @Nonnull final IHasErrorLevel aErrorLevelProvider,
                          @Nonnull final String sMsg,
                          @Nullable final Throwable t)
  {
    log (LoggerFactory.getLogger (aLoggingClass), aErrorLevelProvider.getErrorLevel (), sMsg, t);
  }

  public static void log (@Nonnull final Logger aLogger,
                          @Nonnull final IHasErrorLevel aErrorLevelProvider,
                          @Nonnull final String sMsg)
  {
    log (aLogger, aErrorLevelProvider.getErrorLevel (), sMsg, null);
  }

  public static void log (@Nonnull final Logger aLogger,
                          @Nonnull final IHasErrorLevel aErrorLevelProvider,
                          @Nonnull final String sMsg,
                          @Nullable final Throwable t)
  {
    log (aLogger, aErrorLevelProvider.getErrorLevel (), sMsg, t);
  }

  public static void log (@Nonnull final Class <?> aLoggingClass,
                          @Nonnull final IErrorLevel aErrorLevel,
                          @Nonnull final String sMsg)
  {
    log (aLoggingClass, aErrorLevel, sMsg, null);
  }

  public static void log (@Nonnull final Class <?> aLoggingClass,
                          @Nonnull final IErrorLevel eErrorLevel,
                          @Nonnull final String sMsg,
                          @Nullable final Throwable t)
  {
    log (LoggerFactory.getLogger (aLoggingClass), eErrorLevel, sMsg, t);
  }

  public static void log (@Nonnull final Logger aLogger,
                          @Nonnull final IErrorLevel eErrorLevel,
                          @Nonnull final String sMsg)
  {
    log (aLogger, eErrorLevel, sMsg, null);
  }

  public static void log (@Nonnull final Logger aLogger,
                          @Nonnull final IErrorLevel eErrorLevel,
                          @Nonnull final String sMsg,
                          @Nullable final Throwable t)
  {
    ValueEnforcer.notNull (aLogger, "Logger");
    ValueEnforcer.notNull (eErrorLevel, "ErrorLevel");
    ValueEnforcer.notNull (sMsg, "Message");

    if (eErrorLevel.isMoreOrEqualSevereThan (EErrorLevel.ERROR))
      aLogger.error (sMsg, t);
    else
      if (eErrorLevel.isMoreOrEqualSevereThan (EErrorLevel.WARN))
        aLogger.warn (sMsg, t);
      else
        if (eErrorLevel.isMoreOrEqualSevereThan (EErrorLevel.INFO))
          aLogger.info (sMsg, t);
        else
          if (aLogger.isDebugEnabled ())
            aLogger.debug (sMsg, t);
  }
}

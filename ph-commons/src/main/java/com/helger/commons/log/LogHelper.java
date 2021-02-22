/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.error.level.EErrorLevel;
import com.helger.commons.error.level.IErrorLevel;
import com.helger.commons.error.level.IHasErrorLevel;

/**
 * Some utility functions to help integrating the {@link IErrorLevel} enum in
 * this package with SLF4J logger.
 *
 * @author Philip Helger
 */
@Immutable
public final class LogHelper
{
  /**
   * Abstraction interface for <code>logger.is...Enabled ()</code> depending on
   * the error level.
   *
   * @author Philip Helger
   * @since 9.1.3
   */
  @FunctionalInterface
  public static interface IFuncIsLoggingEnabled
  {
    boolean isEnabled ();
  }

  /**
   * Abstraction interface for <code>logger.debug</code> or
   * <code>logger.info</code> depending on the error level.
   *
   * @author Philip Helger
   * @since 9.1.3
   */
  @FunctionalInterface
  public static interface IFuncLogger
  {
    void log (@Nonnull String sMsg, @Nullable Throwable t);
  }

  @PresentForCodeCoverage
  private static final LogHelper INSTANCE = new LogHelper ();

  private LogHelper ()
  {}

  @Nonnull
  public static IFuncIsLoggingEnabled getFuncIsEnabled (@Nonnull final Logger aLogger, @Nonnull final IErrorLevel aErrorLevel)
  {
    ValueEnforcer.notNull (aLogger, "Logger");
    ValueEnforcer.notNull (aErrorLevel, "ErrorLevel");

    if (aErrorLevel.isGE (EErrorLevel.ERROR))
      return aLogger::isErrorEnabled;
    if (aErrorLevel.isGE (EErrorLevel.WARN))
      return aLogger::isWarnEnabled;
    if (aErrorLevel.isGE (EErrorLevel.INFO))
      return aLogger::isInfoEnabled;
    return aLogger::isDebugEnabled;
  }

  @Nonnull
  public static IFuncLogger getFuncLogger (@Nonnull final Logger aLogger, @Nonnull final IErrorLevel aErrorLevel)
  {
    ValueEnforcer.notNull (aLogger, "Logger");
    ValueEnforcer.notNull (aErrorLevel, "ErrorLevel");

    if (aErrorLevel.isGE (EErrorLevel.ERROR))
      return aLogger::error;
    if (aErrorLevel.isGE (EErrorLevel.WARN))
      return aLogger::warn;
    if (aErrorLevel.isGE (EErrorLevel.INFO))
      return aLogger::info;
    return aLogger::debug;
  }

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
  public static boolean isEnabled (@Nonnull final Class <?> aLoggingClass, @Nonnull final IHasErrorLevel aErrorLevelProvider)
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
   * @param aErrorLevel
   *        The error level. May not be <code>null</code>.
   * @return <code>true</code> if the respective log level is allowed,
   *         <code>false</code> if not
   */
  public static boolean isEnabled (@Nonnull final Class <?> aLoggingClass, @Nonnull final IErrorLevel aErrorLevel)
  {
    return isEnabled (LoggerFactory.getLogger (aLoggingClass), aErrorLevel);
  }

  /**
   * Check if logging is enabled for the passed logger based on the error level
   * provided
   *
   * @param aLogger
   *        The logger. May not be <code>null</code>.
   * @param aErrorLevel
   *        The error level. May not be <code>null</code>.
   * @return <code>true</code> if the respective log level is allowed,
   *         <code>false</code> if not
   */
  public static boolean isEnabled (@Nonnull final Logger aLogger, @Nonnull final IErrorLevel aErrorLevel)
  {
    return getFuncIsEnabled (aLogger, aErrorLevel).isEnabled ();
  }

  /**
   * Generically log something
   *
   * @param aLoggingClass
   *        Logging class to use. May not be <code>null</code>.
   * @param aErrorLevelProvider
   *        Error level provided to use. May not be <code>null</code>.
   * @param sMsg
   *        The message to log. May not be <code>null</code>.
   */
  public static void log (@Nonnull final Class <?> aLoggingClass,
                          @Nonnull final IHasErrorLevel aErrorLevelProvider,
                          @Nonnull final String sMsg)
  {
    log (aLoggingClass, aErrorLevelProvider.getErrorLevel (), sMsg, null);
  }

  /**
   * Generically log something
   *
   * @param aLoggingClass
   *        Logging class to use. May not be <code>null</code>.
   * @param aErrorLevelProvider
   *        Error level provided to use. May not be <code>null</code>.
   * @param sMsg
   *        The message to log. May not be <code>null</code>.
   * @param t
   *        Optional exception that occurred. May be <code>null</code>.
   */
  public static void log (@Nonnull final Class <?> aLoggingClass,
                          @Nonnull final IHasErrorLevel aErrorLevelProvider,
                          @Nonnull final String sMsg,
                          @Nullable final Throwable t)
  {
    log (LoggerFactory.getLogger (aLoggingClass), aErrorLevelProvider.getErrorLevel (), sMsg, t);
  }

  /**
   * Generically log something
   *
   * @param aLogger
   *        Logger to use. May not be <code>null</code>.
   * @param aErrorLevelProvider
   *        Error level provider to use. May not be <code>null</code>.
   * @param sMsg
   *        The message to log. May not be <code>null</code>.
   */
  public static void log (@Nonnull final Logger aLogger, @Nonnull final IHasErrorLevel aErrorLevelProvider, @Nonnull final String sMsg)
  {
    log (aLogger, aErrorLevelProvider.getErrorLevel (), sMsg, null);
  }

  /**
   * Generically log something
   *
   * @param aLogger
   *        Logger to use. May not be <code>null</code>.
   * @param aErrorLevelProvider
   *        Error level provider to use. May not be <code>null</code>.
   * @param sMsg
   *        The message to log. May not be <code>null</code>.
   * @param t
   *        Optional exception that occurred. May be <code>null</code>.
   */
  public static void log (@Nonnull final Logger aLogger,
                          @Nonnull final IHasErrorLevel aErrorLevelProvider,
                          @Nonnull final String sMsg,
                          @Nullable final Throwable t)
  {
    log (aLogger, aErrorLevelProvider.getErrorLevel (), sMsg, t);
  }

  /**
   * Generically log something
   *
   * @param aLoggingClass
   *        Logging class to use. May not be <code>null</code>.
   * @param aErrorLevel
   *        Error level to use. May not be <code>null</code>.
   * @param sMsg
   *        The message to log. May not be <code>null</code>.
   */
  public static void log (@Nonnull final Class <?> aLoggingClass, @Nonnull final IErrorLevel aErrorLevel, @Nonnull final String sMsg)
  {
    log (aLoggingClass, aErrorLevel, sMsg, null);
  }

  /**
   * Generically log something
   *
   * @param aLoggingClass
   *        Logging class to use. May not be <code>null</code>.
   * @param aErrorLevel
   *        Error level to use. May not be <code>null</code>.
   * @param sMsg
   *        The message to log. May not be <code>null</code>.
   * @param t
   *        Optional exception that occurred. May be <code>null</code>.
   */
  public static void log (@Nonnull final Class <?> aLoggingClass,
                          @Nonnull final IErrorLevel aErrorLevel,
                          @Nonnull final String sMsg,
                          @Nullable final Throwable t)
  {
    log (LoggerFactory.getLogger (aLoggingClass), aErrorLevel, sMsg, t);
  }

  /**
   * Generically log something
   *
   * @param aLogger
   *        Logger to use. May not be <code>null</code>.
   * @param aErrorLevel
   *        Error level to use. May not be <code>null</code>.
   * @param sMsg
   *        The message to log. May not be <code>null</code>.
   */
  public static void log (@Nonnull final Logger aLogger, @Nonnull final IErrorLevel aErrorLevel, @Nonnull final String sMsg)
  {
    log (aLogger, aErrorLevel, sMsg, null);
  }

  /**
   * Generically log something
   *
   * @param aLogger
   *        Logger to use. May not be <code>null</code>.
   * @param aErrorLevel
   *        Error level to use. May not be <code>null</code>.
   * @param sMsg
   *        The message to log. May not be <code>null</code>.
   * @param t
   *        Optional exception that occurred. May be <code>null</code>.
   */
  public static void log (@Nonnull final Logger aLogger,
                          @Nonnull final IErrorLevel aErrorLevel,
                          @Nonnull final String sMsg,
                          @Nullable final Throwable t)
  {
    ValueEnforcer.notNull (aLogger, "Logger");
    ValueEnforcer.notNull (aErrorLevel, "ErrorLevel");
    ValueEnforcer.notNull (sMsg, "Message");

    if (isEnabled (aLogger, aErrorLevel))
      getFuncLogger (aLogger, aErrorLevel).log (sMsg, t);
  }

  /**
   * Generically log something
   *
   * @param aLoggingClass
   *        Logging class to use. May not be <code>null</code>.
   * @param aErrorLevelProvider
   *        Error level provided to use. May not be <code>null</code>.
   * @param aMsgSupplier
   *        Message supplier to use. The supplier is only invoked if the log
   *        level is enabled on the provided logger. May not be
   *        <code>null</code>.
   * @since 9.1.3
   */
  public static void log (@Nonnull final Class <?> aLoggingClass,
                          @Nonnull final IHasErrorLevel aErrorLevelProvider,
                          @Nonnull final Supplier <String> aMsgSupplier)
  {
    log (aLoggingClass, aErrorLevelProvider.getErrorLevel (), aMsgSupplier, null);
  }

  /**
   * Generically log something
   *
   * @param aLoggingClass
   *        Logging class to use. May not be <code>null</code>.
   * @param aErrorLevelProvider
   *        Error level provided to use. May not be <code>null</code>.
   * @param aMsgSupplier
   *        Message supplier to use. The supplier is only invoked if the log
   *        level is enabled on the provided logger. May not be
   *        <code>null</code>.
   * @param t
   *        Optional exception that occurred. May be <code>null</code>.
   * @since 9.1.3
   */
  public static void log (@Nonnull final Class <?> aLoggingClass,
                          @Nonnull final IHasErrorLevel aErrorLevelProvider,
                          @Nonnull final Supplier <String> aMsgSupplier,
                          @Nullable final Throwable t)
  {
    log (LoggerFactory.getLogger (aLoggingClass), aErrorLevelProvider.getErrorLevel (), aMsgSupplier, t);
  }

  /**
   * Generically log something
   *
   * @param aLogger
   *        Logger to use. May not be <code>null</code>.
   * @param aErrorLevelProvider
   *        Error level provider to use. May not be <code>null</code>.
   * @param aMsgSupplier
   *        Message supplier to use. The supplier is only invoked if the log
   *        level is enabled on the provided logger. May not be
   *        <code>null</code>.
   * @since 9.1.3
   */
  public static void log (@Nonnull final Logger aLogger,
                          @Nonnull final IHasErrorLevel aErrorLevelProvider,
                          @Nonnull final Supplier <String> aMsgSupplier)
  {
    log (aLogger, aErrorLevelProvider.getErrorLevel (), aMsgSupplier, null);
  }

  /**
   * Generically log something
   *
   * @param aLogger
   *        Logger to use. May not be <code>null</code>.
   * @param aErrorLevelProvider
   *        Error level provider to use. May not be <code>null</code>.
   * @param aMsgSupplier
   *        Message supplier to use. The supplier is only invoked if the log
   *        level is enabled on the provided logger. May not be
   *        <code>null</code>.
   * @param t
   *        Optional exception that occurred. May be <code>null</code>.
   * @since 9.1.3
   */
  public static void log (@Nonnull final Logger aLogger,
                          @Nonnull final IHasErrorLevel aErrorLevelProvider,
                          @Nonnull final Supplier <String> aMsgSupplier,
                          @Nullable final Throwable t)
  {
    log (aLogger, aErrorLevelProvider.getErrorLevel (), aMsgSupplier, t);
  }

  /**
   * Generically log something
   *
   * @param aLoggingClass
   *        Logging class to use. May not be <code>null</code>.
   * @param aErrorLevel
   *        Error level to use. May not be <code>null</code>.
   * @param aMsgSupplier
   *        Message supplier to use. The supplier is only invoked if the log
   *        level is enabled on the provided logger. May not be
   *        <code>null</code>.
   * @since 9.1.3
   */
  public static void log (@Nonnull final Class <?> aLoggingClass,
                          @Nonnull final IErrorLevel aErrorLevel,
                          @Nonnull final Supplier <String> aMsgSupplier)
  {
    log (aLoggingClass, aErrorLevel, aMsgSupplier, null);
  }

  /**
   * Generically log something
   *
   * @param aLoggingClass
   *        Logging class to use. May not be <code>null</code>.
   * @param aErrorLevel
   *        Error level to use. May not be <code>null</code>.
   * @param aMsgSupplier
   *        Message supplier to use. The supplier is only invoked if the log
   *        level is enabled on the provided logger. May not be
   *        <code>null</code>.
   * @param t
   *        Optional exception that occurred. May be <code>null</code>.
   * @since 9.1.3
   */
  public static void log (@Nonnull final Class <?> aLoggingClass,
                          @Nonnull final IErrorLevel aErrorLevel,
                          @Nonnull final Supplier <String> aMsgSupplier,
                          @Nullable final Throwable t)
  {
    log (LoggerFactory.getLogger (aLoggingClass), aErrorLevel, aMsgSupplier, t);
  }

  /**
   * Generically log something
   *
   * @param aLogger
   *        Logger to use. May not be <code>null</code>.
   * @param aErrorLevel
   *        Error level to use. May not be <code>null</code>.
   * @param aMsgSupplier
   *        Message supplier to use. The supplier is only invoked if the log
   *        level is enabled on the provided logger. May not be
   *        <code>null</code>.
   * @since 9.1.3
   */
  public static void log (@Nonnull final Logger aLogger,
                          @Nonnull final IErrorLevel aErrorLevel,
                          @Nonnull final Supplier <String> aMsgSupplier)
  {
    log (aLogger, aErrorLevel, aMsgSupplier, null);
  }

  /**
   * Generically log something
   *
   * @param aLogger
   *        Logger to use. May not be <code>null</code>.
   * @param aErrorLevel
   *        Error level to use. May not be <code>null</code>.
   * @param aMsgSupplier
   *        Message supplier to use. The supplier is only invoked if the log
   *        level is enabled on the provided logger. May not be
   *        <code>null</code>.
   * @param t
   *        Optional exception that occurred. May be <code>null</code>.
   * @since 9.1.3
   */
  public static void log (@Nonnull final Logger aLogger,
                          @Nonnull final IErrorLevel aErrorLevel,
                          @Nonnull final Supplier <String> aMsgSupplier,
                          @Nullable final Throwable t)
  {
    ValueEnforcer.notNull (aLogger, "Logger");
    ValueEnforcer.notNull (aErrorLevel, "ErrorLevel");
    ValueEnforcer.notNull (aMsgSupplier, "MessageSupplier");

    if (isEnabled (aLogger, aErrorLevel))
      getFuncLogger (aLogger, aErrorLevel).log (aMsgSupplier.get (), t);
  }
}

/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.base.log;

import java.util.function.Supplier;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;

/**
 * An {@link ILogHandler} that logs to an SLF4J {@link Logger}.
 *
 * @author Philip Helger
 * @since 12.5.2
 */
@Immutable
public final class LoggerLogHandler implements ILogHandler
{
  private final Logger m_aLogger;

  /**
   * Constructor
   *
   * @param aLogger
   *        The SLF4J logger to log to. May not be <code>null</code>.
   */
  public LoggerLogHandler (@NonNull final Logger aLogger)
  {
    ValueEnforcer.notNull (aLogger, "Logger");
    m_aLogger = aLogger;
  }

  /**
   * @return The logger provided in the constructor. Never <code>null</code>.
   */
  @NonNull
  public Logger getLogger ()
  {
    return m_aLogger;
  }

  public void trace (@NonNull final String sMsg, @Nullable final Throwable aThrowable)
  {
    if (m_aLogger.isTraceEnabled ())
      m_aLogger.trace (sMsg, aThrowable);
  }

  @Override
  public void trace (@NonNull final Supplier <String> aMsgSupplier)
  {
    if (m_aLogger.isTraceEnabled ())
      m_aLogger.trace (aMsgSupplier.get ());
  }

  @Override
  public void trace (@NonNull final Supplier <String> aMsgSupplier, @Nullable final Throwable aThrowable)
  {
    if (m_aLogger.isTraceEnabled ())
      m_aLogger.trace (aMsgSupplier.get (), aThrowable);
  }

  public void debug (@NonNull final String sMsg, @Nullable final Throwable aThrowable)
  {
    if (m_aLogger.isDebugEnabled ())
      m_aLogger.debug (sMsg, aThrowable);
  }

  @Override
  public void debug (@NonNull final Supplier <String> aMsgSupplier)
  {
    if (m_aLogger.isDebugEnabled ())
      m_aLogger.debug (aMsgSupplier.get ());
  }

  @Override
  public void debug (@NonNull final Supplier <String> aMsgSupplier, @Nullable final Throwable aThrowable)
  {
    if (m_aLogger.isDebugEnabled ())
      m_aLogger.debug (aMsgSupplier.get (), aThrowable);
  }

  public void info (@NonNull final String sMsg, @Nullable final Throwable aThrowable)
  {
    if (m_aLogger.isInfoEnabled ())
      m_aLogger.info (sMsg, aThrowable);
  }

  @Override
  public void info (@NonNull final Supplier <String> aMsgSupplier)
  {
    if (m_aLogger.isInfoEnabled ())
      m_aLogger.info (aMsgSupplier.get ());
  }

  @Override
  public void info (@NonNull final Supplier <String> aMsgSupplier, @Nullable final Throwable aThrowable)
  {
    if (m_aLogger.isInfoEnabled ())
      m_aLogger.info (aMsgSupplier.get (), aThrowable);
  }

  public void warn (@NonNull final String sMsg, @Nullable final Throwable aThrowable)
  {
    if (m_aLogger.isWarnEnabled ())
      m_aLogger.warn (sMsg, aThrowable);
  }

  @Override
  public void warn (@NonNull final Supplier <String> aMsgSupplier)
  {
    if (m_aLogger.isWarnEnabled ())
      m_aLogger.warn (aMsgSupplier.get ());
  }

  @Override
  public void warn (@NonNull final Supplier <String> aMsgSupplier, @Nullable final Throwable aThrowable)
  {
    if (m_aLogger.isWarnEnabled ())
      m_aLogger.warn (aMsgSupplier.get (), aThrowable);
  }

  public void error (@NonNull final String sMsg, @Nullable final Throwable aThrowable)
  {
    if (m_aLogger.isErrorEnabled ())
      m_aLogger.error (sMsg, aThrowable);
  }

  @Override
  public void error (@NonNull final Supplier <String> aMsgSupplier)
  {
    if (m_aLogger.isErrorEnabled ())
      m_aLogger.error (aMsgSupplier.get ());
  }

  @Override
  public void error (@NonNull final Supplier <String> aMsgSupplier, @Nullable final Throwable aThrowable)
  {
    if (m_aLogger.isErrorEnabled ())
      m_aLogger.error (aMsgSupplier.get (), aThrowable);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("Logger", m_aLogger.getName ()).getToString ();
  }
}

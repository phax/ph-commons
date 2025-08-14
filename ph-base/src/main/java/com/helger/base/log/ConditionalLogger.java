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
package com.helger.base.log;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import org.slf4j.Logger;

import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.base.debug.GlobalDebug;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.state.IEnabledIndicator;
import com.helger.base.tostring.ToStringGenerator;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Conditional logger
 *
 * @author Philip Helger
 * @since 11.0.4
 */
@ThreadSafe
public final class ConditionalLogger implements IConditionalLogger, IEnabledIndicator
{
  private final Logger m_aLogger;
  private final AtomicBoolean m_aEnabled;

  public ConditionalLogger (@Nonnull final Logger aLogger)
  {
    this (aLogger, GlobalDebug.isDebugMode ());
  }

  public ConditionalLogger (@Nonnull final Logger aLogger, final boolean bEnabled)
  {
    ValueEnforcer.notNull (aLogger, "Logger");
    m_aLogger = aLogger;
    m_aEnabled = new AtomicBoolean (bEnabled);
  }

  public boolean isEnabled ()
  {
    return m_aEnabled.get ();
  }

  public boolean setEnabled (final boolean bEnabled)
  {
    // Return the old value
    return m_aEnabled.getAndSet (bEnabled);
  }

  public void trace (@Nonnull final Supplier <String> aMsgSupplier)
  {
    if (isEnabled () && m_aLogger.isTraceEnabled ())
      m_aLogger.trace (aMsgSupplier.get ());
  }

  public void trace (@Nonnull final Supplier <String> aMsgSupplier, @Nullable final Exception ex)
  {
    if (isEnabled () && m_aLogger.isTraceEnabled ())
      m_aLogger.trace (aMsgSupplier.get (), ex);
  }

  public void debug (@Nonnull final Supplier <String> aMsgSupplier)
  {
    if (isEnabled () && m_aLogger.isDebugEnabled ())
      m_aLogger.debug (aMsgSupplier.get ());
  }

  public void debug (@Nonnull final Supplier <String> aMsgSupplier, @Nullable final Exception ex)
  {
    if (isEnabled () && m_aLogger.isDebugEnabled ())
      m_aLogger.debug (aMsgSupplier.get (), ex);
  }

  public void info (@Nonnull final String sMsg)
  {
    if (isEnabled ())
      m_aLogger.info (sMsg);
  }

  public void info (@Nonnull final Supplier <String> aMsgSupplier)
  {
    if (isEnabled ())
      m_aLogger.info (aMsgSupplier.get ());
  }

  public void info (@Nonnull final String sMsg, @Nullable final Exception ex)
  {
    if (isEnabled ())
      m_aLogger.info (sMsg, ex);
  }

  public void info (@Nonnull final Supplier <String> aMsgSupplier, @Nullable final Exception ex)
  {
    if (isEnabled ())
      m_aLogger.info (aMsgSupplier.get (), ex);
  }

  public void warn (@Nonnull final String sMsg)
  {
    if (isEnabled ())
      m_aLogger.warn (sMsg);
  }

  public void warn (@Nonnull final Supplier <String> aMsgSupplier)
  {
    if (isEnabled ())
      m_aLogger.warn (aMsgSupplier.get ());
  }

  public void warn (@Nonnull final String sMsg, @Nullable final Exception ex)
  {
    if (isEnabled ())
      m_aLogger.warn (sMsg, ex);
  }

  public void warn (@Nonnull final Supplier <String> aMsgSupplier, @Nullable final Exception ex)
  {
    if (isEnabled ())
      m_aLogger.warn (aMsgSupplier.get (), ex);
  }

  public void error (@Nonnull final String sMsg)
  {
    if (isEnabled ())
      m_aLogger.error (sMsg);
  }

  public void error (@Nonnull final Supplier <String> aMsgSupplier)
  {
    if (isEnabled ())
      m_aLogger.error (aMsgSupplier.get ());
  }

  public void error (@Nonnull final String sMsg, @Nullable final Exception ex)
  {
    if (isEnabled ())
      m_aLogger.error (sMsg, ex);
  }

  public void error (@Nonnull final Supplier <String> aMsgSupplier, @Nullable final Exception ex)
  {
    if (isEnabled ())
      m_aLogger.error (aMsgSupplier.get (), ex);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Enabled", isEnabled ()).append ("Logger", m_aLogger).getToString ();
  }
}

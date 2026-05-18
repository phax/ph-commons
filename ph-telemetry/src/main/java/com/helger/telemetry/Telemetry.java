/*
 * Copyright (C) 2026 Philip Helger (www.helger.com)
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
package com.helger.telemetry;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.base.concurrent.SimpleReadWriteLock;

/**
 * Static facade for the telemetry tracing abstraction. Resolves the registered
 * {@link ITelemetryTracerSPI} on first use (lazily via {@link ServiceLoader}); falls back to an
 * internal no-op tracer if no implementation is on the classpath.
 *
 * @author Philip Helger
 * @since 12.2.6
 */
@ThreadSafe
public final class Telemetry
{
  private static final Logger LOGGER = LoggerFactory.getLogger (Telemetry.class);
  private static final SimpleReadWriteLock RW_LOCK = new SimpleReadWriteLock ();

  private static volatile ITelemetryTracerSPI s_aTracer;

  private Telemetry ()
  {}

  /**
   * Install a custom tracer. Intended primarily for tests. Pass <code>null</code> to revert to the
   * SPI-discovered tracer.
   *
   * @param aTracer
   *        The tracer to install, or <code>null</code> to revert.
   */
  public static void install (@Nullable final ITelemetryTracerSPI aTracer)
  {
    RW_LOCK.writeLocked ( () -> s_aTracer = aTracer);
    if (aTracer != null)
      LOGGER.info ("Installed custom telemetry tracer: " + aTracer.getClass ().getName ());
  }

  @NonNull
  private static ITelemetryTracerSPI _resolveTracer ()
  {
    final ITelemetryTracerSPI aFast = s_aTracer;
    if (aFast != null)
      return aFast;

    return RW_LOCK.writeLockedGet ( () -> {
      ITelemetryTracerSPI aRet = s_aTracer;
      if (aRet == null)
      {
        final Iterator <ITelemetryTracerSPI> aIt = ServiceLoader.load (ITelemetryTracerSPI.class).iterator ();
        if (aIt.hasNext ())
        {
          aRet = aIt.next ();
          LOGGER.info ("Resolved telemetry tracer SPI: " + aRet.getClass ().getName ());
        }
        else
        {
          aRet = NoOpTelemetryTracer.INSTANCE;
          LOGGER.debug ("No ITelemetryTracerSPI registered - using no-op tracer");
        }
        s_aTracer = aRet;
      }
      return aRet;
    });
  }

  /**
   * Start a new span. See {@link ITelemetryTracerSPI#startSpan(String, ETelemetrySpanKind)}.
   *
   * @param sName
   *        The span name. Never <code>null</code>.
   * @param eKind
   *        The span kind. Never <code>null</code>.
   * @return A new active span. Never <code>null</code>.
   */
  @NonNull
  public static ITelemetrySpan startSpan (@NonNull final String sName, @NonNull final ETelemetrySpanKind eKind)
  {
    return _resolveTracer ().startSpan (sName, eKind);
  }

  /**
   * Run {@code aBody} inside a fresh span. The span is started, the body is executed with the span
   * passed in (so it can set attributes / status), exceptions are recorded on the span (with status
   * ERROR) and re-thrown, and the span is ended in a {@code finally} block.
   *
   * @param sName
   *        The span name. Never <code>null</code>.
   * @param eKind
   *        The span kind. Never <code>null</code>.
   * @param aBody
   *        The body. Never <code>null</code>. Receives the active span.
   * @param <T>
   *        Body return type.
   * @return The value returned by the body.
   */
  public static <T> T withSpan (@NonNull final String sName,
                                @NonNull final ETelemetrySpanKind eKind,
                                @NonNull final Function <ITelemetrySpan, T> aBody)
  {
    try (final ITelemetrySpan aSpan = startSpan (sName, eKind))
    {
      try
      {
        return aBody.apply (aSpan);
      }
      catch (final RuntimeException ex)
      {
        aSpan.recordException (ex).setStatusError (ex.getMessage ());
        throw ex;
      }
    }
  }

  /**
   * Run {@code aBody} inside a fresh span. The span is started, the body is executed with the span
   * passed in (so it can set attributes / status), exceptions are recorded on the span (with status
   * ERROR) and re-thrown, and the span is ended in a {@code finally} block.
   *
   * @param sName
   *        The span name. Never <code>null</code>.
   * @param eKind
   *        The span kind. Never <code>null</code>.
   * @param aBody
   *        The body. Never <code>null</code>. Receives the active span.
   * @param <T>
   *        Body return type.
   */
  public static void withSpanVoid (@NonNull final String sName,
                                   @NonNull final ETelemetrySpanKind eKind,
                                   @NonNull final Consumer <ITelemetrySpan> aBody)
  {
    try (final ITelemetrySpan aSpan = startSpan (sName, eKind))
    {
      try
      {
        aBody.accept (aSpan);
      }
      catch (final RuntimeException ex)
      {
        aSpan.recordException (ex).setStatusError (ex.getMessage ());
        throw ex;
      }
    }
  }

  // === No-op fallback ===

  public static final class NoOpTelemetryTracer implements ITelemetryTracerSPI
  {
    public static final NoOpTelemetryTracer INSTANCE = new NoOpTelemetryTracer ();

    private NoOpTelemetryTracer ()
    {}

    @NonNull
    public ITelemetrySpan startSpan (@NonNull final String sName, @NonNull final ETelemetrySpanKind eKind)
    {
      return NoOpTelemetrySpan.INSTANCE;
    }
  }

  public static final class NoOpTelemetrySpan implements ITelemetrySpan
  {
    public static final NoOpTelemetrySpan INSTANCE = new NoOpTelemetrySpan ();

    private NoOpTelemetrySpan ()
    {}

    @NonNull
    public ITelemetrySpan setAttribute (@NonNull final String sKey, @Nullable final String sValue)
    {
      return this;
    }

    @NonNull
    public ITelemetrySpan setAttribute (@NonNull final String sKey, final boolean bValue)
    {
      return this;
    }

    @NonNull
    public ITelemetrySpan setAttribute (@NonNull final String sKey, final long nValue)
    {
      return this;
    }

    @NonNull
    public ITelemetrySpan setAttribute (@NonNull final String sKey, final double dValue)
    {
      return this;
    }

    @NonNull
    public ITelemetrySpan recordException (@NonNull final Throwable aException)
    {
      return this;
    }

    @NonNull
    public ITelemetrySpan setStatusOk ()
    {
      return this;
    }

    @NonNull
    public ITelemetrySpan setStatusError (@Nullable final String sMessage)
    {
      return this;
    }

    public void close ()
    {}
  }
}

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
import java.util.function.LongSupplier;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.base.concurrent.SimpleReadWriteLock;

/**
 * Static facade for the telemetry metrics abstraction. Resolves the registered
 * {@link ITelemetryMeterSPI} on first use (lazily via {@link ServiceLoader}); falls back to an
 * internal no-op meter if no implementation is on the classpath.
 * <p>
 * Mirrors the pattern of {@link Telemetry} for tracing. Both facades are independent — a process
 * can register a tracer SPI but no meter SPI (or vice versa) and the absent one degrades to a no-op
 * silently.
 *
 * @author Philip Helger
 * @since 12.2.7
 */
@ThreadSafe
public final class TelemetryMetrics
{
  private static final Logger LOGGER = LoggerFactory.getLogger (TelemetryMetrics.class);
  private static final SimpleReadWriteLock RW_LOCK = new SimpleReadWriteLock ();

  private static volatile ITelemetryMeterSPI s_aMeter;

  private TelemetryMetrics ()
  {}

  /**
   * Install a custom meter. Intended primarily for tests. Pass <code>null</code> to revert to the
   * SPI-discovered meter.
   *
   * @param aMeter
   *        The meter to install, or <code>null</code> to revert.
   */
  public static void install (@Nullable final ITelemetryMeterSPI aMeter)
  {
    RW_LOCK.writeLocked ( () -> s_aMeter = aMeter);
    if (aMeter != null)
      LOGGER.info ("Installed custom telemetry meter '" + aMeter.getClass ().getName () + "'");
  }

  @NonNull
  private static ITelemetryMeterSPI _resolveMeter ()
  {
    final ITelemetryMeterSPI aFast = s_aMeter;
    if (aFast != null)
      return aFast;

    return RW_LOCK.writeLockedGet ( () -> {
      ITelemetryMeterSPI ret = s_aMeter;
      if (ret == null)
      {
        final Iterator <ITelemetryMeterSPI> aIt = ServiceLoader.load (ITelemetryMeterSPI.class).iterator ();
        if (aIt.hasNext ())
        {
          ret = aIt.next ();
          LOGGER.info ("Resolved telemetry meter SPI '" + ret.getClass ().getName () + "'");
        }
        else
        {
          ret = NoOpTelemetryMeter.INSTANCE;
          if (LOGGER.isDebugEnabled ())
            LOGGER.debug ("No ITelemetryMeterSPI registered - using no-op meter");
        }
        s_aMeter = ret;
      }
      return ret;
    });
  }

  /**
   * Create or look up a monotonic counter instrument.
   *
   * @param sName
   *        The instrument name. Never <code>null</code>.
   * @param sDescription
   *        Human-readable description. May be <code>null</code>.
   * @param sUnit
   *        Unit of measure (e.g. <code>"{request}"</code>, <code>"ms"</code>, <code>"By"</code>).
   *        May be <code>null</code>.
   * @return The counter instrument. Never <code>null</code>.
   */
  @NonNull
  public static ITelemetryCounter counter (@NonNull final String sName,
                                           @Nullable final String sDescription,
                                           @Nullable final String sUnit)
  {
    return _resolveMeter ().createCounter (sName, sDescription, sUnit);
  }

  /**
   * Create or look up an up-down counter instrument.
   *
   * @param sName
   *        The instrument name. Never <code>null</code>.
   * @param sDescription
   *        Human-readable description. May be <code>null</code>.
   * @param sUnit
   *        Unit of measure. May be <code>null</code>.
   * @return The up-down counter instrument. Never <code>null</code>.
   */
  @NonNull
  public static ITelemetryUpDownCounter upDownCounter (@NonNull final String sName,
                                                       @Nullable final String sDescription,
                                                       @Nullable final String sUnit)
  {
    return _resolveMeter ().createUpDownCounter (sName, sDescription, sUnit);
  }

  /**
   * Create or look up a histogram instrument.
   *
   * @param sName
   *        The instrument name. Never <code>null</code>.
   * @param sDescription
   *        Human-readable description. May be <code>null</code>.
   * @param sUnit
   *        Unit of measure. May be <code>null</code>.
   * @return The histogram instrument. Never <code>null</code>.
   */
  @NonNull
  public static ITelemetryHistogram histogram (@NonNull final String sName,
                                               @Nullable final String sDescription,
                                               @Nullable final String sUnit)
  {
    return _resolveMeter ().createHistogram (sName, sDescription, sUnit);
  }

  /**
   * Create an observable gauge that polls the supplier at the backend's native cadence.
   *
   * @param sName
   *        The instrument name. Never <code>null</code>.
   * @param sDescription
   *        Human-readable description. May be <code>null</code>.
   * @param sUnit
   *        Unit of measure. May be <code>null</code>.
   * @param aSupplier
   *        The value supplier. Invoked from a backend thread; must be cheap and thread-safe. Never
   *        <code>null</code>.
   * @return The gauge handle — close it to stop observation. Never <code>null</code>.
   */
  @NonNull
  public static ITelemetryGauge gauge (@NonNull final String sName,
                                       @Nullable final String sDescription,
                                       @Nullable final String sUnit,
                                       @NonNull final LongSupplier aSupplier)
  {
    return _resolveMeter ().createGauge (sName, sDescription, sUnit, aSupplier);
  }

  // === No-op fallback ===

  public static final class NoOpTelemetryMeter implements ITelemetryMeterSPI
  {
    public static final NoOpTelemetryMeter INSTANCE = new NoOpTelemetryMeter ();

    private NoOpTelemetryMeter ()
    {}

    @NonNull
    public ITelemetryCounter createCounter (@NonNull final String sName,
                                            @Nullable final String sDescription,
                                            @Nullable final String sUnit)
    {
      return NoOpTelemetryCounter.INSTANCE;
    }

    @NonNull
    public ITelemetryUpDownCounter createUpDownCounter (@NonNull final String sName,
                                                        @Nullable final String sDescription,
                                                        @Nullable final String sUnit)
    {
      return NoOpTelemetryUpDownCounter.INSTANCE;
    }

    @NonNull
    public ITelemetryHistogram createHistogram (@NonNull final String sName,
                                                @Nullable final String sDescription,
                                                @Nullable final String sUnit)
    {
      return NoOpTelemetryHistogram.INSTANCE;
    }

    @NonNull
    public ITelemetryGauge createGauge (@NonNull final String sName,
                                        @Nullable final String sDescription,
                                        @Nullable final String sUnit,
                                        @NonNull final LongSupplier aSupplier)
    {
      return NoOpTelemetryGauge.INSTANCE;
    }
  }

  public static final class NoOpTelemetryCounter implements ITelemetryCounter
  {
    public static final NoOpTelemetryCounter INSTANCE = new NoOpTelemetryCounter ();

    private NoOpTelemetryCounter ()
    {}

    public void add (final long nValue, @NonNull final TelemetryAttributes aAttributes)
    {}
  }

  public static final class NoOpTelemetryUpDownCounter implements ITelemetryUpDownCounter
  {
    public static final NoOpTelemetryUpDownCounter INSTANCE = new NoOpTelemetryUpDownCounter ();

    private NoOpTelemetryUpDownCounter ()
    {}

    public void add (final long nValue, @NonNull final TelemetryAttributes aAttributes)
    {}
  }

  public static final class NoOpTelemetryHistogram implements ITelemetryHistogram
  {
    public static final NoOpTelemetryHistogram INSTANCE = new NoOpTelemetryHistogram ();

    private NoOpTelemetryHistogram ()
    {}

    public void record (final double dValue, @NonNull final TelemetryAttributes aAttributes)
    {}
  }

  public static final class NoOpTelemetryGauge implements ITelemetryGauge
  {
    public static final NoOpTelemetryGauge INSTANCE = new NoOpTelemetryGauge ();

    private NoOpTelemetryGauge ()
    {}

    public void close ()
    {}
  }
}

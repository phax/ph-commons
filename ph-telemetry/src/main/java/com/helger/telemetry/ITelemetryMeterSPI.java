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

import java.util.function.LongSupplier;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.IsSPIInterface;

/**
 * SPI for creating metric instruments. Loaded via {@link java.util.ServiceLoader}. The first
 * registered implementation wins; if none is registered, {@link TelemetryMetrics} falls back to a
 * no-op meter that makes all instrument operations cheap and side-effect-free.
 * <p>
 * Implementations must:
 * <ul>
 * <li>Be thread-safe: factory methods may be called concurrently from any thread.</li>
 * <li>Cache identical instruments (same name + description + unit) so that repeated invocations
 * return the same underlying recorder. Callers are encouraged to cache the returned instrument,
 * but a correct implementation must tolerate repeated creation.</li>
 * </ul>
 *
 * @author Philip Helger
 * @since 12.2.7
 */
@IsSPIInterface
public interface ITelemetryMeterSPI
{
  /**
   * Create or look up a monotonic counter instrument.
   *
   * @param sName
   *        The instrument name. Never <code>null</code>.
   * @param sDescription
   *        Human-readable description. May be <code>null</code>.
   * @param sUnit
   *        Unit of measure (e.g. <code>"{request}"</code>, <code>"ms"</code>,
   *        <code>"By"</code>). May be <code>null</code>.
   * @return The counter instrument. Never <code>null</code>.
   */
  @NonNull
  ITelemetryCounter createCounter (@NonNull String sName, @Nullable String sDescription, @Nullable String sUnit);

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
  ITelemetryUpDownCounter createUpDownCounter (@NonNull String sName,
                                               @Nullable String sDescription,
                                               @Nullable String sUnit);

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
  ITelemetryHistogram createHistogram (@NonNull String sName, @Nullable String sDescription, @Nullable String sUnit);

  /**
   * Create an observable gauge that polls the given supplier at the backend's native cadence.
   *
   * @param sName
   *        The instrument name. Never <code>null</code>.
   * @param sDescription
   *        Human-readable description. May be <code>null</code>.
   * @param sUnit
   *        Unit of measure. May be <code>null</code>.
   * @param aSupplier
   *        The value supplier. Invoked from a backend thread; must be cheap and thread-safe.
   *        Never <code>null</code>.
   * @return The gauge handle — close it to stop observation. Never <code>null</code>.
   */
  @NonNull
  ITelemetryGauge createGauge (@NonNull String sName,
                               @Nullable String sDescription,
                               @Nullable String sUnit,
                               @NonNull LongSupplier aSupplier);
}

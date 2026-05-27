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

/**
 * Observable gauge — backed by a {@link java.util.function.LongSupplier} that is polled by the
 * observability backend at its native cadence. Use cases: registry sizes, thread pool sizes,
 * cache entry counts.
 * <p>
 * The returned instance must be closed when the gauge should stop being observed (e.g. on
 * application shutdown). After {@link #close()} the underlying supplier is no longer invoked.
 * <p>
 * Obtain instances via
 * {@link TelemetryMetrics#gauge(String, String, String, java.util.function.LongSupplier)}.
 *
 * @author Philip Helger
 * @since 12.2.7
 */
public interface ITelemetryGauge extends AutoCloseable
{
  /**
   * Stop observing the gauge. After this call the underlying supplier is no longer invoked.
   * Idempotent.
   */
  @Override
  void close ();
}

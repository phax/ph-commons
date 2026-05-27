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

import org.jspecify.annotations.NonNull;

/**
 * Records a distribution of values — observability backends typically compute summary statistics
 * (count, sum, p50/p95/p99) over the recorded values. Use cases: request durations, payload sizes,
 * batch sizes.
 * <p>
 * Obtain instances via
 * {@link TelemetryMetrics#histogram(String, String, String)}.
 *
 * @author Philip Helger
 * @since 12.2.7
 */
public interface ITelemetryHistogram
{
  /**
   * Record a value with no attached attributes.
   *
   * @param dValue
   *        The value to record.
   */
  default void record (final double dValue)
  {
    record (dValue, TelemetryAttributes.EMPTY);
  }

  /**
   * Record a value with the given attributes.
   *
   * @param dValue
   *        The value to record.
   * @param aAttributes
   *        The attributes to associate with this recording. Never <code>null</code>; pass
   *        {@link TelemetryAttributes#EMPTY} explicitly when no dimensions are needed.
   */
  void record (double dValue, @NonNull TelemetryAttributes aAttributes);
}

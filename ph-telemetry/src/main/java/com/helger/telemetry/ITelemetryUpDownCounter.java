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
 * Counter that may be incremented or decremented. Use cases: in-flight requests, active
 * connections, queue depth.
 * <p>
 * For purely monotonic values, prefer {@link ITelemetryCounter}.
 * <p>
 * Obtain instances via
 * {@link TelemetryMetrics#upDownCounter(String, String, String)}.
 *
 * @author Philip Helger
 * @since 12.2.7
 */
public interface ITelemetryUpDownCounter
{
  /**
   * Add a value (may be negative) to the counter with no attached attributes.
   *
   * @param nValue
   *        The value to add. May be negative.
   */
  default void add (final long nValue)
  {
    add (nValue, TelemetryAttributes.EMPTY);
  }

  /**
   * Add a value (may be negative) to the counter with the given attributes.
   *
   * @param nValue
   *        The value to add. May be negative.
   * @param aAttributes
   *        The attributes to associate with this recording. Never <code>null</code>; pass
   *        {@link TelemetryAttributes#EMPTY} explicitly when no dimensions are needed.
   */
  void add (long nValue, @NonNull TelemetryAttributes aAttributes);
}

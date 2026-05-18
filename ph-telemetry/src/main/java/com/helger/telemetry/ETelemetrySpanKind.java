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
 * The kind of a trace span. Mirrors the OpenTelemetry SpanKind so that an OTel-backed
 * {@link ITelemetryTracerSPI} implementation can translate one-to-one.
 *
 * @author Philip Helger
 * @since 12.2.6
 */
public enum ETelemetrySpanKind
{
  /** Default — internal work inside the application. */
  INTERNAL,
  /** Outgoing call to an external system. */
  CLIENT,
  /** Incoming call handled by the application. */
  SERVER,
  /** Asynchronous send to an external system. */
  PRODUCER,
  /** Asynchronous receive from an external system. */
  CONSUMER
}

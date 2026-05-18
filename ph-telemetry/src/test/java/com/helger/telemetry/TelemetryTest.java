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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;

import org.junit.After;
import org.junit.Test;

/**
 * Test class for class {@link Telemetry}.
 *
 * @author Philip Helger
 */
public final class TelemetryTest
{
  @After
  public void tearDown ()
  {
    Telemetry.install (null);
  }

  @Test
  public void testNoOpFallback ()
  {
    // No SPI registered on the test classpath - must fall back to no-op.
    try (final ITelemetrySpan aSpan = Telemetry.startSpan ("test.span", ETelemetrySpanKind.INTERNAL))
    {
      assertNotNull (aSpan);
      // All mutators must be safe and return this.
      assertSame (aSpan, aSpan.setAttribute ("s", "v"));
      assertSame (aSpan, aSpan.setAttribute ("s", (String) null));
      assertSame (aSpan, aSpan.setAttribute ("b", true));
      assertSame (aSpan, aSpan.setAttribute ("n", 42L));
      assertSame (aSpan, aSpan.setAttribute ("d", 3.14));
      assertSame (aSpan, aSpan.recordException (new RuntimeException ("boom")));
      assertSame (aSpan, aSpan.setStatusOk ());
      assertSame (aSpan, aSpan.setStatusError ("nope"));
    }
  }

  @Test
  public void testDoubleClose ()
  {
    // close() must be idempotent.
    try (final ITelemetrySpan aSpan = Telemetry.startSpan ("test.span", ETelemetrySpanKind.INTERNAL))
    {
      aSpan.close ();
      aSpan.close ();
      // Mutators after close must also be no-ops.
      assertSame (aSpan, aSpan.setStatusOk ());
    }
  }

  @Test
  public void testWithSpanReturnsValue ()
  {
    final String sResult = Telemetry.withSpan ("test.span", ETelemetrySpanKind.INTERNAL, aSpan -> {
      assertNotNull (aSpan);
      return "ok";
    });
    assertEquals ("ok", sResult);
  }

  @Test
  public void testWithSpanRecordsAndRethrows ()
  {
    final RuntimeException aThrown = assertThrows (RuntimeException.class,
                                                   () -> Telemetry.withSpan ("test.span",
                                                                             ETelemetrySpanKind.INTERNAL,
                                                                             aSpan -> {
                                                                               throw new RuntimeException ("boom");
                                                                             }));
    assertEquals ("boom", aThrown.getMessage ());
  }

  @Test
  public void testWithSpanVoidRecordsAndRethrows ()
  {
    final RuntimeException aThrown = assertThrows (RuntimeException.class,
                                                   () -> Telemetry.withSpanVoid ("test.span",
                                                                                 ETelemetrySpanKind.INTERNAL,
                                                                                 aSpan -> {
                                                                                   throw new RuntimeException ("boom");
                                                                                 }));
    assertEquals ("boom", aThrown.getMessage ());
  }

  @Test
  public void testInstallCustomTracer ()
  {
    final ITelemetryTracerSPI aRecorder = (@org.jspecify.annotations.NonNull final String sName,
                                           @org.jspecify.annotations.NonNull final ETelemetrySpanKind eKind) -> new MarkerSpan ();
    Telemetry.install (aRecorder);
    try (final ITelemetrySpan aSpan = Telemetry.startSpan ("custom", ETelemetrySpanKind.CLIENT))
    {
      assertEquals (MarkerSpan.class, aSpan.getClass ());
    }
  }

  private static final class MarkerSpan implements ITelemetrySpan
  {
    @org.jspecify.annotations.NonNull
    public ITelemetrySpan setAttribute (@org.jspecify.annotations.NonNull final String sKey,
                                        @org.jspecify.annotations.Nullable final String sValue)
    {
      return this;
    }

    @org.jspecify.annotations.NonNull
    public ITelemetrySpan setAttribute (@org.jspecify.annotations.NonNull final String sKey, final boolean bValue)
    {
      return this;
    }

    @org.jspecify.annotations.NonNull
    public ITelemetrySpan setAttribute (@org.jspecify.annotations.NonNull final String sKey, final long nValue)
    {
      return this;
    }

    @org.jspecify.annotations.NonNull
    public ITelemetrySpan setAttribute (@org.jspecify.annotations.NonNull final String sKey, final double dValue)
    {
      return this;
    }

    @org.jspecify.annotations.NonNull
    public ITelemetrySpan recordException (@org.jspecify.annotations.NonNull final Throwable aException)
    {
      return this;
    }

    @org.jspecify.annotations.NonNull
    public ITelemetrySpan setStatusOk ()
    {
      return this;
    }

    @org.jspecify.annotations.NonNull
    public ITelemetrySpan setStatusError (@org.jspecify.annotations.Nullable final String sMessage)
    {
      return this;
    }

    public void close ()
    {}
  }
}

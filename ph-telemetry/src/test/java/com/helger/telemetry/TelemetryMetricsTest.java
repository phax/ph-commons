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

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.After;
import org.junit.Test;

/**
 * Test class for class {@link TelemetryMetrics}.
 *
 * @author Philip Helger
 */
public final class TelemetryMetricsTest
{
  @After
  public void tearDown ()
  {
    TelemetryMetrics.install (null);
  }

  @Test
  public void testNoOpFallback ()
  {
    // No SPI registered on the test classpath - must fall back to no-op.
    final ITelemetryCounter aCounter = TelemetryMetrics.counter ("test.counter", "desc", "{op}");
    assertNotNull (aCounter);
    aCounter.add (1);
    aCounter.add (5, TelemetryAttributes.builder ().put ("k", "v").build ());

    final ITelemetryUpDownCounter aUDC = TelemetryMetrics.upDownCounter ("test.udc", "desc", "{op}");
    assertNotNull (aUDC);
    aUDC.add (-3);
    aUDC.add (7, TelemetryAttributes.EMPTY);

    final ITelemetryHistogram aHist = TelemetryMetrics.histogram ("test.hist", "desc", "ms");
    assertNotNull (aHist);
    aHist.record (12.5);
    aHist.record (99.0, TelemetryAttributes.builder ().put ("status", "ok").build ());

    try (final ITelemetryGauge aGauge = TelemetryMetrics.gauge ("test.gauge", "desc", "1", () -> 42L))
    {
      assertNotNull (aGauge);
    }
  }

  @Test
  public void testInstallCustomMeter ()
  {
    final RecordingMeter aRecorder = new RecordingMeter ();
    TelemetryMetrics.install (aRecorder);

    final ITelemetryCounter aCounter = TelemetryMetrics.counter ("c", null, null);
    aCounter.add (3, TelemetryAttributes.builder ().put ("vesid", "x").build ());
    aCounter.add (1);

    assertEquals (4L, aRecorder.m_aCounterTotal.get ());
    assertEquals (1, aRecorder.m_nCounterAttrCalls);

    final ITelemetryHistogram aHist = TelemetryMetrics.histogram ("h", null, null);
    aHist.record (12.0);
    assertEquals (12.0, aRecorder.m_dHistSum, 0.001);
  }

  @Test
  public void testGaugeSupplierWired ()
  {
    final RecordingMeter aRecorder = new RecordingMeter ();
    TelemetryMetrics.install (aRecorder);

    final ITelemetryGauge aGauge = TelemetryMetrics.gauge ("g", null, null, () -> 99L);
    assertNotNull (aGauge);
    assertNotNull (aRecorder.m_aGaugeSupplier);
    assertEquals (99L, aRecorder.m_aGaugeSupplier.getAsLong ());
  }

  private static final class RecordingMeter implements ITelemetryMeterSPI
  {
    final AtomicLong m_aCounterTotal = new AtomicLong ();
    int m_nCounterAttrCalls;
    double m_dHistSum;
    LongSupplier m_aGaugeSupplier;

    @NonNull
    public ITelemetryCounter createCounter (@NonNull final String sName,
                                            @Nullable final String sDescription,
                                            @Nullable final String sUnit)
    {
      return (nValue, aAttrs) -> {
        m_aCounterTotal.addAndGet (nValue);
        if (!aAttrs.isEmpty ())
          m_nCounterAttrCalls++;
      };
    }

    @NonNull
    public ITelemetryUpDownCounter createUpDownCounter (@NonNull final String sName,
                                                        @Nullable final String sDescription,
                                                        @Nullable final String sUnit)
    {
      return (nValue, aAttrs) -> {};
    }

    @NonNull
    public ITelemetryHistogram createHistogram (@NonNull final String sName,
                                                @Nullable final String sDescription,
                                                @Nullable final String sUnit)
    {
      return (dValue, aAttrs) -> m_dHistSum += dValue;
    }

    @NonNull
    public ITelemetryGauge createGauge (@NonNull final String sName,
                                        @Nullable final String sDescription,
                                        @Nullable final String sUnit,
                                        @NonNull final LongSupplier aSupplier)
    {
      m_aGaugeSupplier = aSupplier;
      return () -> {};
    }
  }

  @Test
  public void testNoOpInstancesShared ()
  {
    // Sanity: repeated calls to the no-op meter return the same shared instances.
    final ITelemetryCounter a = TelemetryMetrics.counter ("a", null, null);
    final ITelemetryCounter b = TelemetryMetrics.counter ("b", null, null);
    assertSame (a, b);
  }
}

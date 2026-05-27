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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.junit.Test;

/**
 * Test class for class {@link TelemetryAttributes}.
 *
 * @author Philip Helger
 */
public final class TelemetryAttributesTest
{
  @Test
  public void testEmpty ()
  {
    assertTrue (TelemetryAttributes.EMPTY.isEmpty ());
    assertEquals (0, TelemetryAttributes.EMPTY.size ());

    // Empty build returns the shared EMPTY instance.
    assertSame (TelemetryAttributes.EMPTY, TelemetryAttributes.builder ().build ());
  }

  @Test
  public void testBuilderAndVisitor ()
  {
    final TelemetryAttributes aAttrs = TelemetryAttributes.builder ()
                                                          .put ("s", "value")
                                                          .put ("n", 42L)
                                                          .put ("d", 3.14)
                                                          .put ("b", true)
                                                          .build ();
    assertEquals (4, aAttrs.size ());
    assertFalse (aAttrs.isEmpty ());

    final List <String> aSeen = new ArrayList <> ();
    aAttrs.forEach (new TelemetryAttributes.IVisitor ()
    {
      public void onString (final @NonNull String sKey, final @NonNull String sValue)
      {
        aSeen.add ("S:" + sKey + "=" + sValue);
      }

      public void onLong (final @NonNull String sKey, final long nValue)
      {
        aSeen.add ("L:" + sKey + "=" + nValue);
      }

      public void onDouble (final @NonNull String sKey, final double dValue)
      {
        aSeen.add ("D:" + sKey + "=" + dValue);
      }

      public void onBoolean (final @NonNull String sKey, final boolean bValue)
      {
        aSeen.add ("B:" + sKey + "=" + bValue);
      }
    });
    assertEquals (List.of ("S:s=value", "L:n=42", "D:d=3.14", "B:b=true"), aSeen);
  }

  @Test
  public void testNullStringDropped ()
  {
    final TelemetryAttributes aAttrs = TelemetryAttributes.builder ()
                                                          .put ("present", "x")
                                                          .put ("absent", (String) null)
                                                          .build ();
    assertEquals (1, aAttrs.size ());
  }

  @Test
  public void testImmutableAfterBuild ()
  {
    final TelemetryAttributes.Builder aBuilder = TelemetryAttributes.builder ().put ("a", 1L);
    final TelemetryAttributes aBuilt = aBuilder.build ();
    // Continuing to put after build must not affect the already-built snapshot.
    aBuilder.put ("b", 2L);
    assertEquals (1, aBuilt.size ());
  }
}

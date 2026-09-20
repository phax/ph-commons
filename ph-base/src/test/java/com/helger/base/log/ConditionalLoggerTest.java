/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.base.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test class for class {@link ConditionalLogger}.
 *
 * @author Philip Helger
 */
public final class ConditionalLoggerTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ConditionalLoggerTest.class);
  private static final Exception EX = new IllegalStateException ("mock");

  @Test
  public void testEnabledState ()
  {
    final ConditionalLogger a = new ConditionalLogger (LOGGER);
    assertEquals (Boolean.valueOf (ConditionalLogger.DEFAULT_ENABLED), Boolean.valueOf (a.isEnabled ()));
    assertNotNull (a.toString ());

    final ConditionalLogger b = new ConditionalLogger (LOGGER, false);
    assertFalse (b.isEnabled ());
    // setEnabled returns the previous value
    assertFalse (b.setEnabled (true));
    assertTrue (b.isEnabled ());
    assertTrue (b.setEnabled (false));
    assertFalse (b.isEnabled ());
  }

  @Test
  public void testAllLevelsEnabled ()
  {
    final ConditionalLogger a = new ConditionalLogger (LOGGER, true);

    a.trace (() -> "trace");
    a.trace (() -> "trace", EX);
    a.debug (() -> "debug");
    a.debug (() -> "debug", EX);
    a.info ("info");
    a.info (() -> "info");
    a.info ("info", EX);
    a.info (() -> "info", EX);
    a.warn ("warn");
    a.warn (() -> "warn");
    a.warn ("warn", EX);
    a.warn (() -> "warn", EX);
    a.error ("error");
    a.error (() -> "error");
    a.error ("error", EX);
    a.error (() -> "error", EX);
    assertTrue (a.isEnabled ());
  }

  @Test
  public void testAllLevelsDisabled ()
  {
    // Nothing must be logged and no supplier must be evaluated
    final ConditionalLogger a = new ConditionalLogger (LOGGER, false);

    a.trace (() -> { throw new IllegalStateException ("must not be evaluated"); });
    a.debug (() -> { throw new IllegalStateException ("must not be evaluated"); });
    a.info ("info");
    a.info (() -> { throw new IllegalStateException ("must not be evaluated"); });
    a.info ("info", EX);
    a.warn ("warn");
    a.warn (() -> { throw new IllegalStateException ("must not be evaluated"); });
    a.warn ("warn", EX);
    a.error ("error");
    a.error (() -> { throw new IllegalStateException ("must not be evaluated"); });
    a.error ("error", EX);
    assertFalse (a.isEnabled ());
  }
}

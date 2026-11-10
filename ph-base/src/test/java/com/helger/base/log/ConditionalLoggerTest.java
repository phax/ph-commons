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

import java.lang.reflect.Proxy;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.numeric.mutable.MutableInt;

/**
 * Test class for class {@link ConditionalLogger}.
 *
 * @author Philip Helger
 */
public final class ConditionalLoggerTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ConditionalLoggerTest.class);
  private static final Throwable T = new IllegalStateException ("mock");

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
    a.trace (() -> "trace", T);
    a.debug (() -> "debug");
    a.debug (() -> "debug", T);
    a.info ("info");
    a.info (() -> "info");
    a.info ("info", T);
    a.info (() -> "info", T);
    a.warn ("warn");
    a.warn (() -> "warn");
    a.warn ("warn", T);
    a.warn (() -> "warn", T);
    a.error ("error");
    a.error (() -> "error");
    a.error ("error", T);
    a.error (() -> "error", T);
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
    a.info ("info", T);
    a.warn ("warn");
    a.warn (() -> { throw new IllegalStateException ("must not be evaluated"); });
    a.warn ("warn", T);
    a.error ("error");
    a.error (() -> { throw new IllegalStateException ("must not be evaluated"); });
    a.error ("error", T);
    assertFalse (a.isEnabled ());
  }

  @Test
  public void testLevelOffDoesNotEvaluateSupplier ()
  {
    // A logger with all levels disabled that counts the log calls
    final MutableInt aLogCalls = new MutableInt (0);
    final Logger aOffLogger = (Logger) Proxy.newProxyInstance (Logger.class.getClassLoader (),
                                                               new Class <?> [] { Logger.class },
                                                               (aProxy, aMethod, aArgs) -> {
                                                                 final String sName = aMethod.getName ();
                                                                 if (sName.startsWith ("is") &&
                                                                     sName.endsWith ("Enabled"))
                                                                   return Boolean.FALSE;
                                                                 if (sName.equals ("getName"))
                                                                   return "off";
                                                                 aLogCalls.inc ();
                                                                 return null;
                                                               });
    final ConditionalLogger a = new ConditionalLogger (aOffLogger, true);
    final MutableInt aSupplierCalls = new MutableInt (0);

    a.trace ( () -> "trace" + aSupplierCalls.inc ());
    a.trace ( () -> "trace" + aSupplierCalls.inc (), T);
    a.debug ( () -> "debug" + aSupplierCalls.inc ());
    a.debug ( () -> "debug" + aSupplierCalls.inc (), T);
    a.info ( () -> "info" + aSupplierCalls.inc ());
    a.info ( () -> "info" + aSupplierCalls.inc (), T);
    a.warn ( () -> "warn" + aSupplierCalls.inc ());
    a.warn ( () -> "warn" + aSupplierCalls.inc (), T);
    a.error ( () -> "error" + aSupplierCalls.inc ());
    a.error ( () -> "error" + aSupplierCalls.inc (), T);
    a.info ("info");
    a.info ("info", T);
    a.warn ("warn");
    a.warn ("warn", T);
    a.error ("error");
    a.error ("error", T);

    assertEquals (0, aSupplierCalls.intValue ());
    assertEquals (0, aLogCalls.intValue ());
  }
}

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test class for {@link ILogHandler} and its implementations.
 *
 * @author Philip Helger
 */
public final class LogHandlerTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (LogHandlerTest.class);
  private static final Throwable T = new IllegalStateException ("mock");

  @Test
  public void testConsumer ()
  {
    final List <String> aMsgs = new ArrayList <> ();
    final ILogHandler aHdl = ILogHandler.createForConsumer (aMsgs::add);
    aHdl.info ("i");
    aHdl.warn ("w");
    aHdl.error ("e");
    aHdl.info ("i2", T);
    aHdl.warn ("w2", T);
    aHdl.error ("e2", T);
    assertEquals (List.of ("i", "w", "e", "i2", "w2", "e2"), aMsgs);
  }

  @Test
  public void testBiConsumer ()
  {
    final List <String> aMsgs = new ArrayList <> ();
    final List <Throwable> aThrowables = new ArrayList <> ();
    final ILogHandler aHdl = ILogHandler.createForBiConsumer ((sMsg, t) -> {
      aMsgs.add (sMsg);
      aThrowables.add (t);
    });
    aHdl.error ("e");
    aHdl.warn ("w", T);
    assertEquals (List.of ("e", "w"), aMsgs);
    assertEquals (2, aThrowables.size ());
    assertNull (aThrowables.get (0));
    assertSame (T, aThrowables.get (1));
  }

  @Test
  public void testLoggerAndNoOp ()
  {
    final ILogHandler aLogger = ILogHandler.createForLogger (LOGGER);
    assertNotNull (aLogger.toString ());
    aLogger.info ("info message");
    aLogger.warn ("warn message", T);
    aLogger.error ("error message", T);

    final ILogHandler aNoOp = ILogHandler.createNoOp ();
    assertSame (aNoOp, ILogHandler.createNoOp ());
    aNoOp.info ("ignored");
    aNoOp.warn ("ignored", T);
    aNoOp.error ("ignored", T);
    assertTrue (aNoOp.toString ().length () > 0);
  }

  @Test (expected = NullPointerException.class)
  public void testNullLogger ()
  {
    ILogHandler.createForLogger (null);
  }

  @Test
  public void testLevelInterfaceDefaults ()
  {
    final List <String> aMsgs = new ArrayList <> ();
    final List <Throwable> aThrowables = new ArrayList <> ();
    final IWarnLogger aWarn = (sMsg, aThrowable) -> {
      aMsgs.add (sMsg);
      aThrowables.add (aThrowable);
    };
    aWarn.warn ("a");
    aWarn.warn ("b", T);
    aWarn.warn (() -> "c");
    aWarn.warn (() -> "d", T);
    assertEquals (List.of ("a", "b", "c", "d"), aMsgs);
    assertNull (aThrowables.get (0));
    assertSame (T, aThrowables.get (1));
    assertNull (aThrowables.get (2));
    assertSame (T, aThrowables.get (3));
  }

  @Test
  public void testConditionalLoggerIsLogHandler ()
  {
    final ConditionalLogger aCondLog = new ConditionalLogger (LOGGER, false);
    final ILogHandler aHdl = aCondLog;
    // Disabled - nothing may happen, especially the supplier must not be evaluated
    aHdl.info ("ignored");
    aHdl.warn (() -> { throw new IllegalStateException ("Supplier must not be called"); });
    aHdl.error ("ignored", T);

    // Binary compatible overloads with Exception
    aCondLog.info ("ignored", T);
    aCondLog.error (() -> "ignored", T);

    aCondLog.setEnabled (true);
    aHdl.info ("enabled info");
    aCondLog.error ("enabled error", T);
  }
}

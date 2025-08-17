/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.commons.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.diagnostics.error.level.EErrorLevel;

/**
 * Test class for class {@link InMemoryLogger}
 *
 * @author Philip Helger
 */
public final class InMemoryLoggerTest
{
  @Test
  public void testAll ()
  {
    final InMemoryLogger log = new InMemoryLogger ();
    assertTrue (log.isEmpty ());
    assertEquals (0, log.size ());
    log.error ("error");
    log.error ("error2", new Exception ());
    assertEquals (2, log.size ());
    assertFalse (log.isEmpty ());
    log.warn ("warn");
    log.warn ("warn2", new Exception ());
    log.info ("Info");
    assertEquals (5, log.size ());
    assertEquals (5, log.getAllMessages ().size ());
    assertNotNull (log.toString ());
    log.log (EErrorLevel.WARN, "msg");
    assertFalse (new CommonsArrayList <> (log).isEmpty ());
  }
}

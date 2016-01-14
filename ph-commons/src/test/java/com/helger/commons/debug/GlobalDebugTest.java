/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.debug;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for class {@link GlobalDebug}
 *
 * @author Philip Helger
 */
public final class GlobalDebugTest
{
  private static void _setToDefault ()
  {
    GlobalDebug.setProductionModeDirect (GlobalDebug.DEFAULT_PRODUCTION_MODE);
    GlobalDebug.setDebugModeDirect (GlobalDebug.DEFAULT_DEBUG_MODE);
  }

  @Test
  public void testInstance ()
  {
    final GlobalDebug g = new GlobalDebug ();
    assertTrue (GlobalDebug.isDebugMode ());
    assertFalse (GlobalDebug.isProductionMode ());

    // implicitly disables debug and trace mode
    g.setProductionMode (true);
    assertFalse (GlobalDebug.isDebugMode ());
    assertTrue (GlobalDebug.isProductionMode ());

    g.setProductionMode (false);
    assertFalse (GlobalDebug.isDebugMode ());
    assertFalse (GlobalDebug.isProductionMode ());

    g.setDebugMode (true);
    assertTrue (GlobalDebug.isDebugMode ());
    assertFalse (GlobalDebug.isProductionMode ());

    g.setDebugMode (false);
    assertFalse (GlobalDebug.isDebugMode ());
    assertFalse (GlobalDebug.isProductionMode ());

    _setToDefault ();

    assertTrue (GlobalDebug.DEFAULT_DEBUG_MODE == GlobalDebug.isDebugMode ());
    assertTrue (GlobalDebug.DEFAULT_PRODUCTION_MODE == GlobalDebug.isProductionMode ());
  }

  @Test
  public void testStatic ()
  {
    GlobalDebug.setDebugModeDirect (false);
    assertFalse (GlobalDebug.isDebugMode ());
    assertFalse (GlobalDebug.isProductionMode ());

    GlobalDebug.setDebugModeDirect (true);
    assertTrue (GlobalDebug.isDebugMode ());
    assertFalse (GlobalDebug.isProductionMode ());

    GlobalDebug.setProductionModeDirect (true);
    assertFalse (GlobalDebug.isDebugMode ());
    assertTrue (GlobalDebug.isProductionMode ());

    GlobalDebug.setProductionModeDirect (false);
    assertFalse (GlobalDebug.isDebugMode ());
    assertFalse (GlobalDebug.isProductionMode ());

    _setToDefault ();

    assertTrue (GlobalDebug.DEFAULT_DEBUG_MODE == GlobalDebug.isDebugMode ());
    assertTrue (GlobalDebug.DEFAULT_PRODUCTION_MODE == GlobalDebug.isProductionMode ());
  }
}

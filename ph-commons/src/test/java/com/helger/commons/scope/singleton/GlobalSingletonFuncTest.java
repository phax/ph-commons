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
package com.helger.commons.scope.singleton;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.scope.mock.ScopeTestRule;

/**
 * Test class for class {@link AbstractGlobalSingleton}.
 *
 * @author Philip Helger
 */
public final class GlobalSingletonFuncTest
{
  @Rule
  public final TestRule m_aScopeRule = new ScopeTestRule ();

  @BeforeClass
  public static void beforeClass ()
  {
    assertEquals (0, MockGlobalSingleton.s_nCtorCount);
    assertEquals (0, MockGlobalSingleton.s_nDtorCount);
  }

  @AfterClass
  public static void afterClass ()
  {
    assertEquals (1, MockGlobalSingleton.s_nCtorCount);
    assertEquals (1, MockGlobalSingleton.s_nDtorCount);
  }

  @Test
  public void testBasic ()
  {
    assertTrue (AbstractGlobalSingleton.getAllGlobalSingletons ().isEmpty ());
    assertFalse (AbstractGlobalSingleton.isGlobalSingletonInstantiated (MockGlobalSingleton.class));
    assertNull (AbstractGlobalSingleton.getGlobalSingletonIfInstantiated (MockGlobalSingleton.class));

    final MockGlobalSingleton a = MockGlobalSingleton.getInstance ();
    assertNotNull (a);
    assertTrue (AbstractGlobalSingleton.isGlobalSingletonInstantiated (MockGlobalSingleton.class));
    assertSame (a, AbstractGlobalSingleton.getGlobalSingletonIfInstantiated (MockGlobalSingleton.class));

    assertNotNull (MockGlobalSingleton.getInstance ());
    assertSame (a, MockGlobalSingleton.getInstance ());
  }

  @SuppressWarnings ("deprecation")
  @Test
  public void testInstantiateManually ()
  {
    // The check is only performed when debug mode is enabled
    assertTrue (GlobalDebug.isDebugMode ());
    try
    {
      // Is not meant to be invoked directly!
      new MockGlobalSingleton ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // Expected
    }
  }
}

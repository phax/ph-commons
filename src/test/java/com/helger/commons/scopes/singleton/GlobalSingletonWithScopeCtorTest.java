/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.scopes.singleton;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.scopes.mock.ScopeTestRule;
import com.helger.commons.scopes.singleton.GlobalSingleton;

/**
 * Test class for class {@link GlobalSingleton}.
 *
 * @author Philip Helger
 */
public final class GlobalSingletonWithScopeCtorTest
{
  @Rule
  public final TestRule m_aScopeRule = new ScopeTestRule ();

  @BeforeClass
  public static void beforeClass ()
  {
    assertEquals (0, MockGlobalSingletonWithScopeCtor.s_nCtorCount);
    assertEquals (0, MockGlobalSingletonWithScopeCtor.s_nDtorCount);
  }

  @AfterClass
  public static void afterClass ()
  {
    assertEquals (1, MockGlobalSingletonWithScopeCtor.s_nCtorCount);
    assertEquals (1, MockGlobalSingletonWithScopeCtor.s_nDtorCount);
  }

  @Test
  public void testBasic ()
  {
    assertTrue (GlobalSingleton.getAllGlobalSingletons ().isEmpty ());
    assertFalse (GlobalSingleton.isGlobalSingletonInstantiated (MockGlobalSingletonWithScopeCtor.class));
    assertNull (GlobalSingleton.getGlobalSingletonIfInstantiated (MockGlobalSingletonWithScopeCtor.class));

    final MockGlobalSingletonWithScopeCtor a = MockGlobalSingletonWithScopeCtor.getInstance ();
    assertNotNull (a);
    assertNotNull (a.getScope ());
    assertTrue (GlobalSingleton.isGlobalSingletonInstantiated (MockGlobalSingletonWithScopeCtor.class));
    assertSame (a, GlobalSingleton.getGlobalSingletonIfInstantiated (MockGlobalSingletonWithScopeCtor.class));

    assertNotNull (MockGlobalSingletonWithScopeCtor.getInstance ());
    assertSame (a, MockGlobalSingletonWithScopeCtor.getInstance ());
  }
}

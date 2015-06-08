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
package com.helger.commons.scope.singleton;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.GlobalDebug;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.scope.mock.ScopeTestRule;
import com.helger.commons.scope.singleton.SessionApplicationSingleton;

/**
 * Test class for class {@link SessionApplicationSingleton}.<br>
 * Note: must reside here for Mock* stuff!
 *
 * @author Philip Helger
 */
public final class SessionApplicationSingletonTest
{
  @Rule
  public final TestRule m_aScopeRule = new ScopeTestRule ();

  @Test
  public void testBasic () throws Exception
  {
    assertTrue (SessionApplicationSingleton.getAllSessionApplicationSingletons ().isEmpty ());
    assertFalse (SessionApplicationSingleton.isSessionApplicationSingletonInstantiated (MockSessionApplicationSingleton.class));
    assertNull (SessionApplicationSingleton.getSessionApplicationSingletonIfInstantiated (MockSessionApplicationSingleton.class));

    final MockSessionApplicationSingleton a = MockSessionApplicationSingleton.getInstance ();
    assertTrue (SessionApplicationSingleton.isSessionApplicationSingletonInstantiated (MockSessionApplicationSingleton.class));
    assertSame (a,
                SessionApplicationSingleton.getSessionApplicationSingletonIfInstantiated (MockSessionApplicationSingleton.class));
    assertEquals (0, a.get ());
    a.inc ();
    assertEquals (1, a.get ());
    assertSame (a, MockSessionApplicationSingleton.getInstance ());

    CommonsTestHelper.testDefaultSerialization (a);
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
      new MockSessionApplicationSingleton ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // Expected
    }
  }

  @Test
  public void testBasicWithScopeCtor () throws Exception
  {
    assertTrue (SessionApplicationSingleton.getAllSessionApplicationSingletons ().isEmpty ());
    assertFalse (SessionApplicationSingleton.isSessionApplicationSingletonInstantiated (MockSessionApplicationSingletonWithScopeCtor.class));
    assertNull (SessionApplicationSingleton.getSessionApplicationSingletonIfInstantiated (MockSessionApplicationSingletonWithScopeCtor.class));

    final MockSessionApplicationSingletonWithScopeCtor a = MockSessionApplicationSingletonWithScopeCtor.getInstance ();
    assertNotNull (a);
    assertTrue (SessionApplicationSingleton.isSessionApplicationSingletonInstantiated (MockSessionApplicationSingletonWithScopeCtor.class));
    assertSame (a,
                SessionApplicationSingleton.getSessionApplicationSingletonIfInstantiated (MockSessionApplicationSingletonWithScopeCtor.class));
    assertNotNull (a.getScope ());
    assertEquals (0, a.get ());
    a.inc ();
    assertEquals (1, a.get ());
    assertSame (a, MockSessionApplicationSingletonWithScopeCtor.getInstance ());

    CommonsTestHelper.testDefaultSerialization (a);
  }
}

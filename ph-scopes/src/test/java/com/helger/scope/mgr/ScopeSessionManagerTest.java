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
package com.helger.scope.mgr;

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

import com.helger.base.state.EChange;
import com.helger.scope.ISessionScope;
import com.helger.scope.SessionScope;
import com.helger.scope.mock.ScopeTestRule;

/**
 * Test class for class {@link ScopeSessionManager}.
 *
 * @author Philip Helger
 */
public final class ScopeSessionManagerTest
{
  @Rule
  public final TestRule m_aScopeRule = new ScopeTestRule ();

  @Test
  public void testGetInstance ()
  {
    final ScopeSessionManager aSSM = ScopeSessionManager.getInstance ();
    assertNotNull (aSSM);
    // Always the same instance
    assertSame (aSSM, ScopeSessionManager.getInstance ());
  }

  @Test
  public void testScopeBeginAndEnd ()
  {
    final ScopeSessionManager aSSM = ScopeSessionManager.getInstance ();

    // The ScopeTestRule creates a session scope
    final int nInitialCount = aSSM.getSessionCount ();

    final ISessionScope aScope = new SessionScope ("ssm-test-1");
    aSSM.onScopeBegin (aScope);
    assertTrue (aSSM.containsAnySession ());
    assertEquals (nInitialCount + 1, aSSM.getSessionCount ());
    assertSame (aScope, aSSM.getSessionScopeOfID ("ssm-test-1"));
    assertTrue (aSSM.getAllSessionScopes ().contains (aScope));

    aSSM.onScopeEnd (aScope);
    assertEquals (nInitialCount, aSSM.getSessionCount ());
    assertNull (aSSM.getSessionScopeOfID ("ssm-test-1"));

    // Ending an already destroyed scope is a no-op
    aSSM.onScopeEnd (aScope);
    assertEquals (nInitialCount, aSSM.getSessionCount ());
  }

  @Test
  public void testGetSessionScopeOfID ()
  {
    final ScopeSessionManager aSSM = ScopeSessionManager.getInstance ();
    assertNull (aSSM.getSessionScopeOfID (null));
    assertNull (aSSM.getSessionScopeOfID (""));
    assertNull (aSSM.getSessionScopeOfID ("does-not-exist"));
  }

  @Test
  public void testDestroyAllSessions ()
  {
    final ScopeSessionManager aSSM = ScopeSessionManager.getInstance ();
    aSSM.onScopeBegin (new SessionScope ("ssm-test-2"));
    aSSM.onScopeBegin (new SessionScope ("ssm-test-3"));
    assertTrue (aSSM.containsAnySession ());

    aSSM.destroyAllSessions ();
    assertFalse (aSSM.containsAnySession ());
    assertEquals (0, aSSM.getSessionCount ());
  }

  @Test
  public void testDestroyAllSessionsOnScopeEnd ()
  {
    final ScopeSessionManager aSSM = ScopeSessionManager.getInstance ();
    assertEquals (Boolean.valueOf (ScopeSessionManager.DEFAULT_DESTROY_ALL_SESSIONS_ON_SCOPE_END),
                  Boolean.valueOf (aSSM.isDestroyAllSessionsOnScopeEnd ()));

    try
    {
      assertSame (EChange.CHANGED, aSSM.setDestroyAllSessionsOnScopeEnd (false));
      assertFalse (aSSM.isDestroyAllSessionsOnScopeEnd ());
      assertSame (EChange.UNCHANGED, aSSM.setDestroyAllSessionsOnScopeEnd (false));
    }
    finally
    {
      aSSM.setDestroyAllSessionsOnScopeEnd (ScopeSessionManager.DEFAULT_DESTROY_ALL_SESSIONS_ON_SCOPE_END);
    }
  }

  @Test
  public void testEndAllSessionsOnScopeEnd ()
  {
    final ScopeSessionManager aSSM = ScopeSessionManager.getInstance ();
    assertEquals (Boolean.valueOf (ScopeSessionManager.DEFAULT_END_ALL_SESSIONS_ON_SCOPE_END),
                  Boolean.valueOf (aSSM.isEndAllSessionsOnScopeEnd ()));

    try
    {
      assertSame (EChange.CHANGED, aSSM.setEndAllSessionsOnScopeEnd (false));
      assertFalse (aSSM.isEndAllSessionsOnScopeEnd ());
      assertSame (EChange.UNCHANGED, aSSM.setEndAllSessionsOnScopeEnd (false));
    }
    finally
    {
      aSSM.setEndAllSessionsOnScopeEnd (ScopeSessionManager.DEFAULT_END_ALL_SESSIONS_ON_SCOPE_END);
    }
  }

  @Test
  public void testInvalidParams ()
  {
    final ScopeSessionManager aSSM = ScopeSessionManager.getInstance ();
    try
    {
      aSSM.onScopeBegin (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      aSSM.onScopeEnd (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
